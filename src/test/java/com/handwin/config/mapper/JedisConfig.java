package com.handwin.config.mapper;

import java.io.Serializable;

import org.apache.commons.pool.impl.GenericObjectPool;

import com.alibaba.fastjson.JSON;

public class JedisConfig implements Serializable {
	
	private static final long serialVersionUID = 3837856068587763413L;

	private String host ;
	private int port ;
	private int maxActive = 100 ;
	private int maxIdle = 20 ;
	private int whenExhaustedAction =  GenericObjectPool.WHEN_EXHAUSTED_BLOCK;
	private int maxWait = 3000 ;
	private boolean testOnReturn = false ;
	private boolean testWhileIdle = false ;
	private boolean testOnBorrow = false ;
	
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public int getMaxActive() {
		return maxActive;
	}
	public void setMaxActive(int maxActive) {
		this.maxActive = maxActive;
	}
	public int getMaxIdle() {
		return maxIdle;
	}
	public void setMaxIdle(int maxIdle) {
		this.maxIdle = maxIdle;
	}
	public int getWhenExhaustedAction() {
		return whenExhaustedAction;
	}
	public void setWhenExhaustedAction(int whenExhaustedAction) {
		this.whenExhaustedAction = whenExhaustedAction;
	}
	public int getMaxWait() {
		return maxWait;
	}
	public void setMaxWait(int maxWait) {
		this.maxWait = maxWait;
	}
	public boolean isTestOnReturn() {
		return testOnReturn;
	}
	public void setTestOnReturn(boolean testOnReturn) {
		this.testOnReturn = testOnReturn;
	}
	public boolean isTestWhileIdle() {
		return testWhileIdle;
	}
	public void setTestWhileIdle(boolean testWhileIdle) {
		this.testWhileIdle = testWhileIdle;
	}
	public boolean isTestOnBorrow() {
		return testOnBorrow;
	}
	public void setTestOnBorrow(boolean testOnBorrow) {
		this.testOnBorrow = testOnBorrow;
	}
	
	public static void main(String[] args ) {
		
		JedisConfig jedisConfig = new JedisConfig() ;
		jedisConfig.setHost("127.0.0.1");
		jedisConfig.setPort(5672); 
		
		System.out.println( JSON.toJSONString( jedisConfig ) ); 
		
		
	}

}
