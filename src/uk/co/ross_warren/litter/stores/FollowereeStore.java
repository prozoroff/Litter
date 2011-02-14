package uk.co.ross_warren.litter.stores;

public class FollowereeStore {
	private String username = "";
	private long date = 0;
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
	
	
	public long getDate()
	{
		return date;
	}
	
	public void setUsername(String username)
	{
		this.username = username;
	}
	
	public void setDate (long date)
	{
		this.date = date;
	}
}
