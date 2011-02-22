package uk.co.ross_warren.litter.stores;

public class TweetStore implements Comparable<TweetStore>
{
	
	private String tweetID = "";
	private String user = "";
	private Long timeStamp = (long) 0;
	private String replyToUser = "";
	private String content = "";
	private String avatarurl = "";
	private String latitude = "";
	private String longitude = "";
	
	private int likes = 0;
	
	private Long sort = timeStamp;
	
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
		sort = timeStamp;
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
	@Override
	public int compareTo(TweetStore o) {
		// TODO Auto-generated method stub
		return (int) (o.sort - this.sort);
	}
	public void switchToLikeOrdering()
	{
		sort = (long) likes;
	}
	public void switchToTimeOrdering()
	{
		sort = timeStamp;
	}
	public void setLikes(int likes) {
		this.likes = likes;
	}
	public int getLikes() {
		return likes;
	}
	public void setAvatarUrl(String avatarurl) {
		this.avatarurl = avatarurl;
	}
	public String getAvatarUrl() {
		return avatarurl;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getLongitude() {
		return longitude;
	}
}
