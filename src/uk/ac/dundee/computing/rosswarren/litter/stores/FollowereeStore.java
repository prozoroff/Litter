package uk.ac.dundee.computing.rosswarren.litter.stores;

import java.util.Date;

import com.sun.jmx.snmp.Timestamp;

public class FollowereeStore {
	private String username;
	private Timestamp date;
	private String avatarurl;
	
	public FollowereeStore()
	{
		username = "";
		avatarurl = "";
	}
	
	public String getUsername()
	{
		return username;
	}
	
	public String getAvatarUrl()
	{
		return avatarurl;
	}
	
	public void setAvatarUrl(String avatarurl)
	{
		this.avatarurl = avatarurl;
	}
	
	
	public Timestamp getDate()
	{
		return date;
	}
	
	public void setUsername(String username)
	{
		this.username = username;
	}
	
	public void setDate (Timestamp date)
	{
		this.date = date;
	}
}
