package uk.co.ross_warren.litter.stores;

public class TweetStore {
	private int tweetID = 0;
	private String user = "";
	private String timeStamp = "";
	private String replyToUser = "";
	
	public void setTweetID(int tweetID) {
		this.tweetID = tweetID;
	}
	public int getTweetID() {
		return tweetID;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getUser() {
		return user;
	}
	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}
	public String getTimeStamp() {
		return timeStamp;
	}
	public void setReplyToUser(String replyToUser) {
		this.replyToUser = replyToUser;
	}
	public String getReplyToUser() {
		return replyToUser;
	}
}
