package pc.dd.liveapp.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by leaditteam on 19.09.17.
 */

public class DateUtil {
    
    final public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("M/d/y");
    
    public static Date parseData(String dateInput) {
        try {
            Date date = simpleDateFormat.parse(dateInput);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static int getDate(Date date, int dayMonthYearWeeks){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        switch (dayMonthYearWeeks){
            case 0:
                return calendar.get(Calendar.DAY_OF_MONTH);
            case 1:
                return calendar.get(Calendar.MONTH);
            case 2:
                return calendar.get(Calendar.YEAR);
            case 3:
                return getNumberOfDay(calendar.get(Calendar.DAY_OF_WEEK));
            default:
                return -1;
        }
        
    }
    
    public static int getNumberOfDay(int calendarNumber){
        switch (calendarNumber){
            case Calendar.MONDAY:
                return 0;
            case Calendar.TUESDAY:
                return 1;
            case Calendar.WEDNESDAY:
                return 2;
            case Calendar.THURSDAY:
                return 3;
            case Calendar.FRIDAY:
                return 4;
            case Calendar.SATURDAY:
                return 5;
            case Calendar.SUNDAY:
                return 6;
                default: return -1;
        }
    }
    
    public static Date getCurrentDate(){
        return Calendar.getInstance().getTime();
    }
}
