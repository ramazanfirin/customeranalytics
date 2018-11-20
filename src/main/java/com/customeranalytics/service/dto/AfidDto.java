package com.customeranalytics.service.dto;

import java.util.Date;

public class AfidDto {
	byte[] afid;
	Date date;
	
	public AfidDto(byte[] afid, Date date) {
		super();
		this.afid = afid;
		this.date = date;
	}
	
	public byte[] getAfid() {
		return afid;
	}
	public void setAfid(byte[] afid) {
		this.afid = afid;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	
	
}
