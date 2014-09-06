package com.handwin.config.mapper;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.alibaba.fastjson.JSON;
import com.handwin.config.bean.ConfigInfo;


public class ConfigInfoMapperTest {
	
	private ApplicationContext context ;
	private ConfigInfoMapper configInfoMapper ;
	
	@Before
	public void setUp(){
		context = new FileSystemXmlApplicationContext(
                new String[] { "src/main/resources/spring/root-context.xml" } ) ;
		configInfoMapper = context.getBean( ConfigInfoMapper.class ) ; 
	}
	
	@Test
	public void w() {
		ConfigInfo configInfo = new ConfigInfo() ;
		configInfo.setBusiness("REDIS"); 
		configInfo.setRegion("0086"); 
		
		JedisConfig jedisConfig = new JedisConfig() ;
		jedisConfig.setHost("127.0.0.1");
		jedisConfig.setPort(6379); 
		
		
		configInfo.setContent(JSON.toJSONString( jedisConfig ) ); 
		try {
			configInfoMapper.create(configInfo) ; 
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertNotNull( null ); 
		}
	}
	
	@Test
	public void testGet() {
		String region = "0086";
		String business = "REDIS";
		ConfigInfo configInfo = configInfoMapper.get(region, business) ;
		Assert.assertNotNull( configInfo ) ;
		System.out.println(configInfo.getContent());
 	}
	
	

}
