package service.cinema.be.core.excel.converter;

import service.cinema.be.core.excel.annotation.ExcelConverter;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Slf4j
public class DefaultConverters {
    
    public static class StringConverter implements ExcelConverter<String> {
        @Override
        public String convert(String value) {
            return value != null ? value.trim() : null;
        }
        
        @Override
        public String convertToString(String value) {
            return value;
        }
    }
    
    public static class IntegerConverter implements ExcelConverter<Integer> {
        @Override
        public Integer convert(String value) throws IllegalArgumentException {
            if (value == null || value.trim().isEmpty()) return null;
            try {
                return Integer.parseInt(value.trim());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid integer format: " + value);
            }
        }
        
        @Override
        public String convertToString(Integer value) {
            return value != null ? value.toString() : "";
        }
    }
    
    public static class DoubleConverter implements ExcelConverter<Double> {
        @Override
        public Double convert(String value) throws IllegalArgumentException {
            if (value == null || value.trim().isEmpty()) return null;
            try {
                return Double.parseDouble(value.trim());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid double format: " + value);
            }
        }
        
        @Override
        public String convertToString(Double value) {
            return value != null ? value.toString() : "";
        }
    }
    
    public static class BigDecimalConverter implements ExcelConverter<BigDecimal> {
        @Override
        public BigDecimal convert(String value) throws IllegalArgumentException {
            if (value == null || value.trim().isEmpty()) return null;
            try {
                return new BigDecimal(value.trim());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid decimal format: " + value);
            }
        }
        
        @Override
        public String convertToString(BigDecimal value) {
            return value != null ? value.toString() : "";
        }
    }
    
    public static class LocalDateConverter implements ExcelConverter<LocalDate> {
        private final String pattern;
        private final DateTimeFormatter customFormatter;
        
        public LocalDateConverter(String pattern) {
            this.pattern = pattern;
            this.customFormatter = DateTimeFormatter.ofPattern(pattern);
        }
        
        @Override
        public LocalDate convert(String value) throws IllegalArgumentException {
            if (value == null || value.trim().isEmpty()) return null;
            try {
                // Try parsing ISO format first (from BaseImportService)
                if (value.contains("T")) {
                    return LocalDateTime.parse(value.trim(), DateTimeFormatter.ISO_LOCAL_DATE_TIME).toLocalDate();
                }
                // Parse according to annotation configuration (for manual text input)
                return LocalDate.parse(value.trim(), customFormatter);
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid date format. Expected: " + pattern + " or ISO Date");
            }
        }
        
        @Override
        public String convertToString(LocalDate value) {
            return value != null ? value.format(customFormatter) : "";
        }
    }
    
    public static class BooleanConverter implements ExcelConverter<Boolean> {
        @Override
        public Boolean convert(String value) throws IllegalArgumentException {
            if (value == null || value.trim().isEmpty()) return null;
            String trimmed = value.trim().toLowerCase();
            if (trimmed.equals("true") || trimmed.equals("1") || trimmed.equals("yes") || trimmed.equals("có")) {
                return true;
            } else if (trimmed.equals("false") || trimmed.equals("0") || trimmed.equals("no") || trimmed.equals("không")) {
                return false;
            } else {
                throw new IllegalArgumentException("Invalid boolean format: " + value);
            }
        }
        
        @Override
        public String convertToString(Boolean value) {
            return value != null ? value.toString() : "";
        }
    }
}
