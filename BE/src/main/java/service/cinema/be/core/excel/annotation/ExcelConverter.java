package service.cinema.be.core.excel.annotation;

/**
 * Interface for custom Excel data converters
 */
public interface ExcelConverter<T> {
    
    /**
     * Convert string value from Excel to target type
     * @param value the string value from Excel cell
     * @return converted value
     * @throws Exception if conversion fails
     */
    T convert(String value) throws Exception;
    
    /**
     * Convert target type value to string for Excel export
     * @param value the value to convert
     * @return string representation for Excel
     * @throws Exception if conversion fails
     */
    String convertToString(T value) throws Exception;
}
