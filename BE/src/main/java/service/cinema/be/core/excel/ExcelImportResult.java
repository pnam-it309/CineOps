package service.cinema.be.core.excel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExcelImportResult<T> {
    
    private int totalRows;
    private int successCount;
    private int failureCount;
    private List<T> successEntities;
    private List<ImportError> errors;
    
    public ExcelImportResult(int totalRows) {
        this.totalRows = totalRows;
        this.successCount = 0;
        this.failureCount = 0;
        this.successEntities = new ArrayList<>();
        this.errors = new ArrayList<>();
    }
    
    public void addSuccess(T entity) {
        successEntities.add(entity);
        successCount++;
    }
    
    public void addError(ImportError error) {
        errors.add(error);
        failureCount++;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImportError {
        private int rowNumber;
        private String columnName;
        private String errorMessage;
        private String cellValue;
    }
}
