package service.cinema.be.core.excel.batch.reader;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.support.AbstractItemCountingItemStreamItemReader;
import org.springframework.core.io.Resource;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

@Slf4j
public class ExcelItemReader extends AbstractItemCountingItemStreamItemReader<Row> {

    private Resource resource;
    private String sheetName;
    private int dataStartRow;
    private int headerRow;
    
    private Workbook workbook;
    private Iterator<Row> rowIterator;
    private int currentRowIndex;
    private int maxRows;

    public ExcelItemReader() {
        this.setName("excelItemReader");
        this.sheetName = "Data";
        this.dataStartRow = 1;
        this.headerRow = 0;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public void setDataStartRow(int dataStartRow) {
        this.dataStartRow = dataStartRow;
    }

    public void setHeaderRow(int headerRow) {
        this.headerRow = headerRow;
    }

    @Override
    protected void doOpen() throws ItemStreamException {
        try {
            log.debug("Opening Excel file: {}", resource.getFilename());
            
            try (InputStream inputStream = resource.getInputStream()) {
                this.workbook = new XSSFWorkbook(inputStream);
            }
            
            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                sheet = workbook.getSheetAt(0);
                log.warn("Sheet '{}' not found, using first sheet: {}", sheetName, sheet.getSheetName());
            }
            
            this.rowIterator = sheet.iterator();
            this.currentRowIndex = 0;
            this.maxRows = sheet.getLastRowNum() - dataStartRow + 1;
            
            // Skip header rows
            while (rowIterator.hasNext() && currentRowIndex < dataStartRow) {
                rowIterator.next();
                currentRowIndex++;
            }
            
            log.debug("Excel reader opened. Total data rows: {}", maxRows);
            
        } catch (IOException e) {
            throw new ItemStreamException("Failed to open Excel file: " + resource.getFilename(), e);
        }
    }

    @Override
    protected Row doRead() throws Exception {
        if (rowIterator != null && rowIterator.hasNext()) {
            Row row = rowIterator.next();
            currentRowIndex++;
            
            // Skip empty rows
            if (isEmptyRow(row)) {
                return doRead(); // Recursive call to skip empty row
            }
            
            log.trace("Reading row {}", currentRowIndex);
            return row;
        }
        return null; // End of file
    }

    @Override
    protected void doClose() throws ItemStreamException {
        try {
            if (workbook != null) {
                workbook.close();
                log.debug("Excel workbook closed");
            }
        } catch (IOException e) {
            throw new ItemStreamException("Failed to close Excel file", e);
        } finally {
            workbook = null;
            rowIterator = null;
            currentRowIndex = 0;
        }
    }

    private boolean isEmptyRow(Row row) {
        if (row == null) return true;
        
        for (int i = row.getFirstCellNum(); i <= row.getLastCellNum(); i++) {
            var cell = row.getCell(i);
            if (cell != null && cell.getCellType() != org.apache.poi.ss.usermodel.CellType.BLANK) {
                String value = getCellValueAsString(cell);
                if (value != null && !value.trim().isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    private String getCellValueAsString(org.apache.poi.ss.usermodel.Cell cell) {
        if (cell == null) return null;
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue()
                        .format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME);
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

    public int getCurrentItemCount() {
        return currentRowIndex - dataStartRow;
    }

    public int getMaxItemCount() {
        return Math.max(0, maxRows);
    }
}
