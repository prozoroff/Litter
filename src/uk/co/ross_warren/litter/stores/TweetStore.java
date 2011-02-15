package uk.co.ross_warren.litter.stores;

public class TweetStore {
	private String tweetID = "";
	private String user = "";
	private long timeStamp = 0;
	private String replyToUser = "";
	private String content = "";
	
	public void setTweetID(String tweetID) {
		this.tweetID = tweetID;
	}
	public String getTweetID() {
		return tweetID;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getUser() {
		return user;
	}
	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}
	public long getTimeStamp() {
		return timeStamp;
	}
	public void setReplyToUser(String replyToUser) {
		this.replyToUser = replyToUser;
	}
	public String getReplyToUser() {
		return replyToUser;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getContent() {
		return content;
	}
}
