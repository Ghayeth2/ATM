package com.atm.core.utils.converter;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Log4j2 @Component
public class DateFormat {
    public LocalDate formatDate(String date) {
       return LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public Date formatDate(Date date, String pattern) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        String formattedDate = formatter.format(date);

        Date d = formatter.parse(formattedDate);
        log.info(" formatted date : "+d);
        return d;
    }
}
