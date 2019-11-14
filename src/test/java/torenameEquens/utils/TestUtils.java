package torenameEquens.utils;

import java.util.Calendar;
import java.util.Date;

public class TestUtils {

    public static Date addTime(Date to, int field, int days ){
        Calendar cal = Calendar.getInstance();
        cal.setTime( to );
        cal.add(field, days);
        return cal.getTime();
    }

}
