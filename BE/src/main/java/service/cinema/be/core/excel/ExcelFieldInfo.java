package service.cinema.be.core.excel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import service.cinema.be.core.excel.annotation.ExcelColumn;

import java.lang.reflect.Field;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExcelFieldInfo {
    
    private Field field;
    private ExcelColumn annotation;
    private String headerName;
    private int columnIndex;
    
    public ExcelFieldInfo(Field field, ExcelColumn annotation) {
        this.field = field;
        this.annotation = annotation;
        this.headerName = annotation.headerName();
        this.columnIndex = annotation.order();
    }
    
    public void setAccessible(boolean accessible) {
        field.setAccessible(accessible);
    }
}
