package service.cinema.be.core.excel.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import service.cinema.be.core.excel.ExcelFieldInfo;
import service.cinema.be.core.excel.ExcelImportResult;
import service.cinema.be.core.excel.annotation.ExcelColumn;
import service.cinema.be.core.excel.annotation.ExcelConverter;
import service.cinema.be.core.excel.annotation.ExcelSheet;
import service.cinema.be.core.excel.converter.DefaultConverters;

import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public abstract class BaseImportService<T, ID> {
    
    protected final JpaRepository<T, ID> repository;
    private final Class<T> entityClass;
    private Map<String, ExcelFieldInfo> fieldMappings;
    private List<ExcelFieldInfo> orderedFields;
    
    @SuppressWarnings("unchecked")
    public BaseImportService(JpaRepository<T, ID> repository) {
        this.repository = repository;
        this.entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        initializeFieldMappings();
    }
    
    private void initializeFieldMappings() {
        fieldMappings = new HashMap<>();
        orderedFields = new ArrayList<>();
        
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
    }
    
    public byte[] generateTemplate() throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            ExcelSheet sheetAnnotation = entityClass.getAnnotation(ExcelSheet.class);
            String sheetName = sheetAnnotation != null ? sheetAnnotation.sheetName() : "Data";
            
            Sheet sheet = workbook.createSheet(sheetName);
            
            // Create header row
            Row headerRow = sheet.createRow(0);
            CellStyle headerStyle = createHeaderStyle(workbook);
            
            for (int i = 0; i < orderedFields.size(); i++) {
                ExcelFieldInfo fieldInfo = orderedFields.get(i);
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(fieldInfo.getHeaderName());
                cell.setCellStyle(headerStyle);
                
                // Auto-size column
                sheet.autoSizeColumn(i);
            }
            
            // Add example data row
            Row exampleRow = sheet.createRow(1);
            addExampleData(exampleRow);
            
            // Auto-size all columns again after adding data
            for (int i = 0; i < orderedFields.size(); i++) {
                sheet.autoSizeColumn(i);
            }
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }
    
    private void addExampleData(Row row) {
        for (int i = 0; i < orderedFields.size(); i++) {
            ExcelFieldInfo fieldInfo = orderedFields.get(i);
            String exampleValue = getExampleValue(fieldInfo);
            row.createCell(i).setCellValue(exampleValue);
        }
    }
    
    private String getExampleValue(ExcelFieldInfo fieldInfo) {
        Class<?> fieldType = fieldInfo.getField().getType();
        
        if (fieldType == String.class) {
            return "Example " + fieldInfo.getHeaderName();
        } else if (fieldType == Integer.class || fieldType == int.class) {
            return "123";
        } else if (fieldType == Double.class || fieldType == double.class) {
            return "123.45";
        } else if (fieldType == Boolean.class || fieldType == boolean.class) {
            return "true";
        } else if (fieldType == LocalDate.class) {
            return fieldInfo.getAnnotation().dateFormat();
        } else {
            return "";
        }
    }
    
    @Transactional
    public ExcelImportResult<T> importFromExcel(MultipartFile file) throws IOException, IllegalArgumentException {
        ExcelImportResult<T> result = new ExcelImportResult<>();
        
        // Use try-with-resources for both InputStream and Workbook to avoid resource leaks
        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {
            
            ExcelSheet sheetAnnotation = entityClass.getAnnotation(ExcelSheet.class);
            String sheetName = sheetAnnotation != null ? sheetAnnotation.sheetName() : "Data";
            int headerRow = sheetAnnotation != null ? sheetAnnotation.headerRow() : 0;
            int dataStartRow = sheetAnnotation != null ? sheetAnnotation.dataStartRow() : 1;
            
            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                throw new IllegalArgumentException("Sheet '" + sheetName + "' not found");
            }
            
            // Read headers and map to columns
            Map<String, Integer> headerToColumnMap = readHeaders(sheet, headerRow);
            
            // Process data rows
            int totalRows = sheet.getLastRowNum() - dataStartRow + 1;
            result = new ExcelImportResult<>(Math.max(0, totalRows));
            
            // Create list to collect valid entities for batch insert
            List<T> validEntities = new ArrayList<>();
            
            for (int rowNum = dataStartRow; rowNum <= sheet.getLastRowNum(); rowNum++) {
                Row row = sheet.getRow(rowNum);
                if (row == null || isEmptyRow(row)) continue;
                
                try {
                    T entity = processRow(row, headerToColumnMap, rowNum + 1);
                    if (entity != null) {
                        // Validate and add to list instead of saving immediately
                        validateEntity(entity, rowNum + 1);
                        validEntities.add(entity);
                    }
                } catch (IllegalArgumentException e) {
                    log.error("Error processing row {}: {}", rowNum + 1, e.getMessage());
                    result.addError(new ExcelImportResult.ImportError(
                        rowNum + 1, "", e.getMessage(), ""
                    ));
                }
            }
            
            // Batch save all valid entities at once (performance improvement)
            if (!validEntities.isEmpty()) {
                List<T> savedEntities = repository.saveAll(validEntities);
                savedEntities.forEach(result::addSuccess);
            }
        }
        
        return result;
    }
    
    private Map<String, Integer> readHeaders(Sheet sheet, int headerRow) {
        Map<String, Integer> headerToColumnMap = new HashMap<>();
        Row header = sheet.getRow(headerRow);
        
        if (header != null) {
            for (Cell cell : header) {
                String headerValue = getCellValueAsString(cell);
                if (headerValue != null && !headerValue.trim().isEmpty()) {
                    headerToColumnMap.put(headerValue.toLowerCase().trim(), cell.getColumnIndex());
                }
            }
        }
        
        return headerToColumnMap;
    }
    
    private T processRow(Row row, Map<String, Integer> headerToColumnMap, int rowNum) throws IllegalArgumentException {
        try {
            T entity = entityClass.getDeclaredConstructor().newInstance();
            
            for (ExcelFieldInfo fieldInfo : orderedFields) {
                Integer columnIndex = headerToColumnMap.get(fieldInfo.getHeaderName().toLowerCase());
                if (columnIndex == null) {
                    if (fieldInfo.getAnnotation().required()) {
                        throw new IllegalArgumentException("Required column '" + fieldInfo.getHeaderName() + "' not found");
                    }
                    continue;
                }
                
                Cell cell = row.getCell(columnIndex);
                String cellValue = getCellValueAsString(cell);
                
                try {
                    Object convertedValue = convertValue(cellValue, fieldInfo, rowNum);
                    fieldInfo.setAccessible(true);
                    fieldInfo.getField().set(entity, convertedValue);
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Error in column '" + fieldInfo.getHeaderName() + "': " + e.getMessage());
                } catch (Exception e) {
                    throw new IllegalArgumentException("Error in column '" + fieldInfo.getHeaderName() + "': " + e.getMessage());
                }
            }
            
            return entity;
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Entity class must have a no-argument constructor: " + e.getMessage());
        } catch (InstantiationException e) {
            throw new IllegalArgumentException("Cannot instantiate entity class: " + e.getMessage());
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Cannot access entity constructor: " + e.getMessage());
        } catch (java.lang.reflect.InvocationTargetException e) {
            throw new IllegalArgumentException("Error creating entity instance: " + e.getTargetException().getMessage());
        }
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
            } else if (fieldType == LocalDate.class) {
                return new DefaultConverters.LocalDateConverter(annotation.dateFormat()).convert(value);
            } else {
                throw new IllegalArgumentException("Unsupported field type: " + fieldType.getSimpleName());
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Error in column '" + fieldInfo.getHeaderName() + "': " + e.getMessage());
        }
    }
    
    protected void validateEntity(T entity, int rowNum) throws IllegalArgumentException {
        // Override in subclasses for custom validation
    }
    
    private String getCellValueAsString(Cell cell) {
        if (cell == null) return null;
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    // FIX DATE BUG: Convert Excel date to ISO format instead of default toString()
                    LocalDateTime dateTime = cell.getLocalDateTimeCellValue();
                    return dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                } else {
                    // Handle large numbers that might be displayed in scientific notation
                    double numericValue = cell.getNumericCellValue();
                    if (numericValue == (long) numericValue) {
                        return String.valueOf((long) numericValue);
                    } else {
                        return String.valueOf(numericValue);
                    }
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                // For formula cells, try to evaluate the result
                try {
                    FormulaEvaluator evaluator = cell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();
                    CellValue evaluatedValue = evaluator.evaluate(cell);
                    if (evaluatedValue != null) {
                        switch (evaluatedValue.getCellType()) {
                            case NUMERIC:
                                if (DateUtil.isCellDateFormatted(cell)) {
                                    LocalDateTime dateTime = cell.getLocalDateTimeCellValue();
                                    return dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                                } else {
                                    double val = evaluatedValue.getNumberValue();
                                    return val == (long) val ? String.valueOf((long) val) : String.valueOf(val);
                                }
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
        for (Cell cell : row) {
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                String value = getCellValueAsString(cell);
                if (value != null && !value.trim().isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }
    
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        
        return style;
    }
}
