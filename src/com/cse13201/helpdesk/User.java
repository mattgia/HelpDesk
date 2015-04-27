package com.cse13201.helpdesk;

public class User {

	private String sID;
	private String name;
	private String pw;
	private String type;
	private long lastActive;
	
	public User(String sessionID, String name, String password, String type)
	{
		this.sID = sessionID;
		this.name = name;
		this.pw = password;
		this.type = type;	
		this.lastActive = System.currentTimeMillis();
	}
	public long getLastActive() {
		return lastActive;
	}
	public void setLastActive(long lastActive) {
		this.lastActive = lastActive;
	}
	public String getsID() {
		return sID;
	}
	public void setsID(String sID) {
		this.sID = sID;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPw() {
		return pw;
	}
	public void setPw(String pw) {
		this.pw = pw;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
}
