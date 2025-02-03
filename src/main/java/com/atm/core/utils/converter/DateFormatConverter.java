package com.atm.core.utils.converter;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

@Log4j2
@Component
public class DateFormatConverter {
    // Response data date formatter
    public String formatDate(LocalDateTime date) {
        String formattedDate = null;
        try {
            formattedDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss.SSSSSSSSS"));
        } catch (DateTimeParseException e) {
            log.error("Date formatter exception: "+e.getMessage());
        }
        return date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss"));
    }

    // Request data date formatter
    public LocalDateTime formatRequestDate(LocalDateTime date) {
        LocalDateTime res = null;
        try {
//            log.info("Date before formatting: "+date);
            res =  LocalDateTime.parse(date.toString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
//            log.info("Date after formatting: "+res);
        } catch (Exception e) {
            log.error("Date exception: "+e.getMessage());
        }
        return res;
    }

    public LocalDateTime formatRequestDate(String date) {
        LocalDateTime res = null;
        try {
            // Check if the input date string doesn't include time, and append default time
            if (date != null && date.length() == 10) { // Format 'yyyy-MM-dd'
                date = date + " 00:00:00"; // Append default time
            }

            // Parse the date and time string
            res = LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (Exception e) {
            log.error("Date exception: " + e.getMessage());
        }
        return res;
    }
}
