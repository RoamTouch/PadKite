package com.roamtouch.utils;

import java.lang.reflect.Array;

public class ResizeArray {
	
	 /**
     * Resize a Java array
     *
     * @param oldArray
     * @param minimumSize
     * @return the new Java array
     */
    public static Object ResizeArray(final Object oldArray, final int minimumSize) {
        final Class<?> cls = oldArray.getClass();
        if (!cls.isArray()) {
            return null;
        }
        final int oldLength = Array.getLength(oldArray);
        int newLength = oldLength + (oldLength / 2); // 50% more
        if (newLength < minimumSize) {
            newLength = minimumSize;
        }
        final Class<?> componentType = oldArray.getClass().getComponentType();
        final Object newArray = Array.newInstance(componentType, newLength);
        System.arraycopy(oldArray, 0, newArray, 0, oldLength);
        return newArray;
    }
	

}
