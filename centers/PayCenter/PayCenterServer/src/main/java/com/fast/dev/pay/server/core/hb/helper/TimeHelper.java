package com.fast.dev.pay.server.core.hb.helper;

import com.fast.dev.data.mongo.helper.DBHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Component
public class TimeHelper {

    private final static String today = "yyyy-MM-dd";

    @Autowired
    private DBHelper dbHelper;


    /**
     * 获取今天的时间
     *
     * @return
     */
    public String getToday() {
        return format(today);
    }


    /**
     * 获取现在的工作时间
     *
     * @return
     */
    public int getNowWorkTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(this.dbHelper.getTime());
        return calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
    }


    /**
     * 格式化时间
     *
     * @return
     */
    private String format(String text) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(text);
        return simpleDateFormat.format(new Date(this.dbHelper.getTime()));
    }

    /**
     * 获取当前的分钟
     *
     * @return
     */
    public long[] getNowMinute() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(this.dbHelper.getTime());
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long time = calendar.getTimeInMillis();
        return new long[]{
                time,
                time + 1000l * 60 - 1
        };
    }

    /**
     * 获取时间段
     *
     * @param type
     * @return
     */
    public long[] getTime(TimeHelper.TimeType type) {
        if (type == TimeHelper.TimeType.Day) {
            return getTodayTime();
        } else if (type == TimeHelper.TimeType.Minute) {
            return getNowMinute();
        }
        return new long[2];
    }

    /**
     * 获取当天的开始于结束日期
     *
     * @return
     */
    public long[] getTodayTime() {
        long time = this.dbHelper.getTime();
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTimeInMillis(time);
        startCalendar.set(Calendar.HOUR_OF_DAY, 0);
        startCalendar.set(Calendar.MINUTE, 0);
        startCalendar.set(Calendar.SECOND, 0);
        startCalendar.set(Calendar.MILLISECOND, 0);

        //今天的结束时间
        long endTime = startCalendar.getTimeInMillis() + 1000 * 60 * 60 * 24 - 1;

        return new long[]{startCalendar.getTimeInMillis(), endTime};
    }

    public static enum TimeType {
        Day,
        Minute
    }


}
