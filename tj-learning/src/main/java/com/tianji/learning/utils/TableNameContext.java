package com.tianji.learning.utils;

/**
 * @author liuyp
 * @since 2023/07/19
 */
public class TableNameContext {
    private static final ThreadLocal<String> THREAD_LOCAL = new ThreadLocal<>();

    public static void setTableName(String tableName){
        THREAD_LOCAL.set(tableName);
    }

    public static String getTableName(){
        return THREAD_LOCAL.get();
    }

    public static void removeTableName(){
        THREAD_LOCAL.remove();
    }
}