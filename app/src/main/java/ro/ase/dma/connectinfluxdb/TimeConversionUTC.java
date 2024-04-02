package ro.ase.dma.connectinfluxdb;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


public class TimeConversionUTC {

    // Class for changing the date from UTC into UTC+3

    public static ArrayList<String> parseToUtcPlus3 (ArrayList<String> timesUTC)
    {
        ArrayList<String> arrayTimeUtcPlus3 = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        for ( String timeUTC : timesUTC)
        {
            try{
                Date date = simpleDateFormat.parse(timeUTC);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                calendar.add(Calendar.HOUR_OF_DAY,3); // adding 3 hours

                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+3")); //format the date in GMT+3/UTC+3
                String timeUtcPlus3 = simpleDateFormat.format(calendar.getTime());
                arrayTimeUtcPlus3.add(timeUtcPlus3);
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

        return arrayTimeUtcPlus3;

    }
}
