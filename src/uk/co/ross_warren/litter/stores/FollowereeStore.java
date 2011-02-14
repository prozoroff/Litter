package uk.co.ross_warren.litter.stores;

import com.sun.jmx.snmp.Timestamp;

public class FollowereeStore {
	private String username = "";
	private Timestamp date;
	private String avatarurl ="";
	
	public FollowereeStore()
	{
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