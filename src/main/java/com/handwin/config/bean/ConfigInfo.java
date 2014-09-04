package com.handwin.config.bean;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @author fangliang
 *
 */
public class ConfigInfo implements Serializable {
	
	private static final long serialVersionUID = 3347045621712701338L;
	
	private long id ;
	private String region ;
	private String business ;
	private String content ;
	private Date gmtCeate ;
	private Date gmtModified ;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	public String getBusiness() {
		return business;
	}
	public void setBusiness(String business) {
		this.business = business;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Date getGmtCeate() {
		return gmtCeate;
	}
	public void setGmtCeate(Date gmtCeate) {
		this.gmtCeate = gmtCeate;
	}
	public Date getGmtModified() {
		return gmtModified;
	}
	public void setGmtModified(Date gmtModified) {
		this.gmtModified = gmtModified;
	}

}
