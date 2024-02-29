package com.netease.cloud.extension.util;

import com.netease.cloud.extension.logging.Logger;

import java.lang.reflect.Field;

/**
 * @author Liubsyy
 * @date 2023/7/9 11:58 PM
 **/
public class ReflectUtil {
    private static Logger logger = Logger.getLogger(ReflectUtil.class);

    public static<F,T> F getField(T obj, String fieldName)  {
        try{
            Field declaredField = obj.getClass().getDeclaredField(fieldName);
            boolean accessible = declaredField.isAccessible();
            declaredField.setAccessible(true);
            F result = (F)declaredField.get(obj);
            declaredField.setAccessible(accessible);
            return result;
        }catch (Exception e) {
            logger.error("ERROR in getField, obj={},fieldName={}",obj,fieldName);
        }

        return null;
    }


}
