package com.handwin.config.mapper;

import org.apache.ibatis.annotations.Param;

import com.handwin.config.bean.ConfigInfo;
import com.handwin.config.database.DataSource;

/**
 * 
 * @author fangliang
 *
 */
@DataSource("mysql001.law.com")
public interface ConfigInfoMapper {
	
	public int create(@Param("configInfo") ConfigInfo configInfo ); 
	
	public ConfigInfo get(@Param("region") String region , @Param("business") String business ); 

}
