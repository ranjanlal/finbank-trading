package com.finbank.trading.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AppUtils {

    public static LocalDate parseDate(String dateAsString, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return LocalDate.parse(dateAsString, formatter);
    }
}
