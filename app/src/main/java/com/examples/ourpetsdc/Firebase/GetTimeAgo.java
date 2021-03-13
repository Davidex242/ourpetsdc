package com.examples.ourpetsdc.Firebase;

import android.app.Application;
import android.content.Context;

public class GetTimeAgo extends Application {

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;
    public static String getTimeAgo(long time, Context ctx) {

        if (time < 1000000000000L) {

            // if timestamp given in seconds, convert to millis

            time *= 1000;
        }

        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return null;
        }

        //See the time that user are out
        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "a minute ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " minutes ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "an hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "yesterday";
        } else {
            return diff / DAY_MILLIS + " days ago";
        }
    }

    public static String getTimeAgoPt(long time, Context ctx) {

        if (time < 1000000000000L) {

            // if timestamp given in seconds, convert to millis

            time *= 1000;
        }

        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return null;
        }

        //See the time that user are out
        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "Agora mesmo";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "um minuto atrás";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " minutos atrás";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "uma hora atrás";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " horas atrás";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "ontem";
        } else {
            return diff / DAY_MILLIS + " dias atrás";
        }
    }

    public static String getTimeAgoCn(long time, Context ctx) {

        if (time < 1000000000000L) {

            // if timestamp given in seconds, convert to millis

            time *= 1000;
        }

        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return null;
        }

        //See the time that user are out
        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "马上";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "一分钟前";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " 几分钟前";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "一个小时之前";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " 小时前";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "昨天";
        } else {
            return diff / DAY_MILLIS + " 几天前";
        }
    }

    public static String getTimeAgoFr(long time, Context ctx) {

        if (time < 1000000000000L) {

            // if timestamp given in seconds, convert to millis

            time *= 1000;
        }

        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return null;
        }

        //See the time that user are out
        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "Maintenant";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "Il y'a une minute";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + "il y a quelques minutes";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "il y a une heure";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " il y a des heures";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "hier";
        } else {
            return diff / DAY_MILLIS + " il y a quelques jours";
        }
    }
}
