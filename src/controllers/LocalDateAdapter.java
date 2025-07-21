package controllers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Simple utility class for LocalDate formatting
 * No external dependencies required
 */
public class LocalDateAdapter {
    
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
    
    /**
     * Format LocalDate to string
     */
    public static String serialize(LocalDate localDate) {
        return localDate != null ? formatter.format(localDate) : null;
    }
    
    /**
     * Parse string to LocalDate
     */
    public static LocalDate deserialize(String dateString) {
        return dateString != null && !dateString.isEmpty() ? 
               LocalDate.parse(dateString, formatter) : null;
    }
    
    /**
     * Get current date as formatted string
     */
    public static String getCurrentDateString() {
        return formatter.format(LocalDate.now());
    }
}
