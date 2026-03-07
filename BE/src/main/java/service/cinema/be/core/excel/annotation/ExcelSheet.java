package service.cinema.be.core.excel.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to configure Excel sheet settings for an entity
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelSheet {
    
    /**
     * The sheet name in Excel file
     */
    String sheetName() default "Data";
    
    /**
     * The header row number (0-based)
     */
    int headerRow() default 0;
    
    /**
     * The data start row number (0-based)
     */
    int dataStartRow() default 1;
}
