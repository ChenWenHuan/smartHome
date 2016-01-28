package com.smarthome.client2.util;

/**
 * 
 * @author xingang.sun.com
 *
 */
public class Utils {

    public static <T> T requireNonNull(T obj) {
        if (obj == null) throw new NullPointerException();
        return obj;
    }
}
