package jpass.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
    
    public static String fromUnixDateToString(String timestamp, String format){
        String default_format = "dd-MM-yyyy HH:mm:ss";
        SimpleDateFormat date_format = new SimpleDateFormat(default_format);
        try{
            date_format = new SimpleDateFormat(format);
        }
        catch(IllegalArgumentException e){}
        finally{
            Date date = new Date(Long.parseLong(timestamp));
            return date_format.format(date).replaceAll("\"", "");
        }
    }
}