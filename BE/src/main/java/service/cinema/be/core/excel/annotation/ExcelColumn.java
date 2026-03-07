package service.cinema.be.core.excel.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to map Excel columns to entity fields
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelColumn {
    
    /**
     * The header name in Excel file
     */
    String headerName();
    
    /**
     * The order of the column in the template
     */
    int order() default 0;
    
    /**
     * Whether this field is required for import
     */
    boolean required() default true;
    
    /**
     * Custom date format for date fields
     */
    String dateFormat() default "dd/MM/yyyy";
    
    /**
     * Maximum length for string fields
     */
    int maxLength() default Integer.MAX_VALUE;
    
    /**
     * Custom converter class for complex data types
     */
    Class<? extends ExcelConverter> converter() default ExcelConverter.class;
}
