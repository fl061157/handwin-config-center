package com.handwin.config.database;

/**
 * 
 * @author fangliang
 *
 */
public class DataBaseContextHolder {
	
	private final static ThreadLocal<String> contextHolder = new ThreadLocal<String>();

    public static void setDataSource(String dataSource) {
            contextHolder.set( dataSource );
    }

    public static String getDataSource() {
            return contextHolder.get() ;
    }

    public static void clearDataSource() {
            contextHolder.remove() ;
    }

}
