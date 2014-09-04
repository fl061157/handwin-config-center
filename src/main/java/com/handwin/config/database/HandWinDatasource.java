package com.handwin.config.database;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class HandWinDatasource extends AbstractRoutingDataSource {

	@Override
	protected Object determineCurrentLookupKey() {
		return DataBaseContextHolder.getDataSource() ; 
	}

}
