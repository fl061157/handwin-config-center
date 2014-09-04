package com.handwin.config.database;

import org.aopalliance.intercept.MethodInvocation; 


public class DataSourceInterceptor {
	
	 public Object invoke(MethodInvocation invocation) throws Throwable {
         DataSource dataSource = invocation.getMethod().getDeclaringClass().getAnnotation( DataSource.class ) ;
         if( dataSource != null ) {
                 DataBaseContextHolder.setDataSource(dataSource.value());
         }
         return invocation.proceed() ;
 }


}
