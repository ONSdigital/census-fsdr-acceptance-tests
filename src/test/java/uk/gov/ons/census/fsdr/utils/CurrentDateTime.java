package uk.gov.ons.census.fsdr.utils;

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class CurrentDateTime {
    public String dateTime()  {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        return(dtf.format(now).toString());
    }
}

