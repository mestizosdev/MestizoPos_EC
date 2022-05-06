/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.unicenta.pos.util;

/**
 *
 * @author user
 */

import java.util.*;
import java.util.concurrent.TimeUnit;

public class ElapsedTimeBetweenDates {

    public static void main(String []args) {
        Date startDateTime = new Date(System.currentTimeMillis() - 123456789);
        Date endDateTime = new Date();

        Map<TimeUnit,Long> result = computeDiff(startDateTime, endDateTime);
        System.out.println(result);

        System.out.println("Days: " + result.get(TimeUnit.DAYS));
        System.out.println("Hours: " + result.get(TimeUnit.HOURS));
        System.out.println("Minutes: " + result.get(TimeUnit.MINUTES));
        System.out.println("Seconds: " + result.get(TimeUnit.SECONDS));
        System.out.println("MilliSeconds: " + result.get(TimeUnit.MILLISECONDS));
    }

    public static Map<TimeUnit,Long> computeDiff(Date date1, Date date2) {
        long diffInMilliSeconds = date2.getTime() - date1.getTime();
        List<TimeUnit> units = new ArrayList<>(EnumSet.allOf(TimeUnit.class));
        Collections.reverse(units);
        Map<TimeUnit,Long> result = new LinkedHashMap<>();
        long milliSecondsRest = diffInMilliSeconds;
        for (TimeUnit unit : units) {
            long diff = unit.convert(milliSecondsRest,TimeUnit.MILLISECONDS);
            long diffInMilliSecondsForUnit = unit.toMillis(diff);
            milliSecondsRest = milliSecondsRest - diffInMilliSecondsForUnit;
            result.put(unit,diff);
        }
        return result;
    }

}
