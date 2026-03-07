package service.cinema.be.core.excel.batch.processor;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.validator.ValidationException;
import service.cinema.be.core.excel.ExcelFieldInfo;
import service.cinema.be.core.excel.annotation.ExcelColumn;
import service.cinema.be.core.excel.annotation.ExcelConverter;
import service.cinema.be.core.excel.annotation.ExcelSheet;
import service.cinema.be.core.excel.converter.DefaultConverters;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
public class GenericExcelProcessor<T> implements ItemProcessor<Row, T> {

    private final Class<T> entityClass;
    private final Map<String, Integer> headerToColumnMap;
    private final List<ExcelFieldInfo> orderedFields;
    private final Map<String, Object> jobParameters;

    public GenericExcelProcessor(Class<T> entityClass, Map<String, Integer> headerToColumnMap) {
        this(entityClass, headerToColumnMap, new HashMap<>());
    }

    public GenericExcelProcessor(Class<T> entityClass, Map<String, Integer> headerToColumnMap, Map<String, Object> jobParameters) {
        this.entityClass = entityClass;
        this.headerToColumnMap = headerToColumnMap;
        this.jobParameters = jobParameters;
        this.orderedFields = initializeFieldMappings(entityClass);
    }

    @Override
    public T process(Row row) throws Exception {
        if (isEmptyRow(row)) {
            log.trace("Skipping empty row {}", row.getRowNum());
            return null; // Skip empty rows
        }

        try {
            T entity = entityClass.getDeclaredConstructor().newInstance();
            
            for (ExcelFieldInfo fieldInfo : orderedFields) {
                Integer columnIndex = headerToColumnMap.get(fieldInfo.getHeaderName().toLowerCase());
                if (columnIndex == null) {
                    if (fieldInfo.getAnnotation().required()) {
                        throw new ValidationException("Required column '" + fieldInfo.getHeaderName() + "' not found in row " + (row.getRowNum() + 1));
                    }
                    continue;
                }
                
                Cell cell = row.getCell(columnIndex);
                String cellValue = getCellValueAsString(cell);
                
                try {
                    Object convertedValue = convertValue(cellValue, fieldInfo, row.getRowNum() + 1);
                    fieldInfo.setAccessible(true);
                    fieldInfo.getField().set(entity, convertedValue);
                } catch (IllegalArgumentException e) {
                    throw new ValidationException("Error in column '" + fieldInfo.getHeaderName() + "' at row " + (row.getRowNum() + 1) + ": " + e.getMessage());
                }
            }
            
            log.trace("Processed row {} into {}", row.getRowNum() + 1, entityClass.getSimpleName());
            return entity;
            
        } catch (ValidationException e) {
            throw e; // Re-throw validation exceptions
        } catch (Exception e) {
            throw new ValidationException("Error processing row " + (row.getRowNum() + 1) + ": " + e.getMessage(), e);
        }
    }

    private List<ExcelFieldInfo> initializeFieldMappings(Class<T> entityClass) {
        Map<String, ExcelFieldInfo> fieldMappings = new HashMap<>();
        List<ExcelFieldInfo> orderedFields = new ArrayList<>();
        
        for (Field field : entityClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(ExcelColumn.class)) {
                ExcelColumn annotation = field.getAnnotation(ExcelColumn.class);
                ExcelFieldInfo fieldInfo = new ExcelFieldInfo(field, annotation);
                fieldMappings.put(annotation.headerName().toLowerCase(), fieldInfo);
                orderedFields.add(fieldInfo);
            }
        }
        
        // Sort fields by order
        orderedFields.sort(Comparator.comparingInt(f -> f.getAnnotation().order()));
        
        log.debug("Initialized {} Excel field mappings for {}", orderedFields.size(), entityClass.getSimpleName());
        return orderedFields;
    }

    private Object convertValue(String value, ExcelFieldInfo fieldInfo, int rowNum) throws IllegalArgumentException {
        if (value == null || value.trim().isEmpty()) {
            if (fieldInfo.getAnnotation().required()) {
                throw new IllegalArgumentException("Value is required");
            }
            return null;
        }
        
        Class<?> fieldType = fieldInfo.getField().getType();
        ExcelColumn annotation = fieldInfo.getAnnotation();
        
        // Check for custom converter
        if (annotation.converter() != ExcelConverter.class) {
            try {
                ExcelConverter<?> converter = annotation.converter().getDeclaredConstructor().newInstance();
                return converter.convert(value);
            } catch (Exception e) {
                throw new IllegalArgumentException("Error in custom converter: " + e.getMessage());
            }
        }
        
        // Use default converters
        try {
            if (fieldType == String.class) {
                String result = value.trim();
                if (result.length() > annotation.maxLength()) {
                    throw new IllegalArgumentException("Value exceeds maximum length of " + annotation.maxLength());
                }
                return result;
            } else if (fieldType == Integer.class || fieldType == int.class) {
                return new DefaultConverters.IntegerConverter().convert(value);
            } else if (fieldType == Double.class || fieldType == double.class) {
                return new DefaultConverters.DoubleConverter().convert(value);
            } else if (fieldType == Boolean.class || fieldType == boolean.class) {
                return new DefaultConverters.BooleanConverter().convert(value);
            } else if (fieldType == java.time.LocalDate.class) {
                return new DefaultConverters.LocalDateConverter(annotation.dateFormat()).convert(value);
            } else {
                throw new IllegalArgumentException("Unsupported field type: " + fieldType.getSimpleName());
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Error converting value '" + value + "' for type " + fieldType.getSimpleName() + ": " + e.getMessage());
        }
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return null;
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
                    LocalDateTime dateTime = cell.getLocalDateTimeCellValue();
                    return dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                } else {
                    double numericValue = cell.getNumericCellValue();
                    return numericValue == (long) numericValue 
                        ? String.valueOf((long) numericValue) 
                        : String.valueOf(numericValue);
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    var evaluator = cell.getSheet().getWorkbook()
                        .getCreationHelper().createFormulaEvaluator();
                    var evaluatedValue = evaluator.evaluate(cell);
                    if (evaluatedValue != null) {
                        switch (evaluatedValue.getCellType()) {
                            case NUMERIC:
                                double val = evaluatedValue.getNumberValue();
                                return val == (long) val ? String.valueOf((long) val) : String.valueOf(val);
                            case STRING:
                                return evaluatedValue.getStringValue();
                            case BOOLEAN:
                                return String.valueOf(evaluatedValue.getBooleanValue());
                            default:
                                return "";
                        }
                    }
                } catch (Exception e) {
                    log.warn("Failed to evaluate formula cell, using cached result");
                }
                return cell.getStringCellValue();
            case BLANK:
                return null;
            default:
                return null;
        }
    }

    private boolean isEmptyRow(Row row) {
        if (row == null) return true;
        
        for (int i = row.getFirstCellNum(); i <= row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                String value = getCellValueAsString(cell);
                if (value != null && !value.trim().isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Factory method to create processor with header mapping from a header row
     */
    public static <T> GenericExcelProcessor<T> fromHeaderRow(Class<T> entityClass, Row headerRow) {
        Map<String, Integer> headerToColumnMap = new HashMap<>();
        
        if (headerRow != null) {
            for (Cell cell : headerRow) {
                String headerValue = getCellValueAsStringForFactory(cell);
                if (headerValue != null && !headerValue.trim().isEmpty()) {
                    headerToColumnMap.put(headerValue.toLowerCase().trim(), cell.getColumnIndex());
                }
            }
        }
        
        return new GenericExcelProcessor<>(entityClass, headerToColumnMap);
    }

    private static String getCellValueAsStringForFactory(Cell cell) {
        if (cell == null) return null;
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                } else {
                    double numericValue = cell.getNumericCellValue();
                    return numericValue == (long) numericValue 
                        ? String.valueOf((long) numericValue) 
                        : String.valueOf(numericValue);
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getStringCellValue();
            case BLANK:
                return null;
            default:
                return null;
        }
    }
}
