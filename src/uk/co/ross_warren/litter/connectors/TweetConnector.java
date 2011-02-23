package uk.co.ross_warren.litter.connectors;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.ConsistencyLevelPolicy;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.ColumnSlice;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.beans.OrderedRows;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;
import me.prettyprint.hector.api.query.QueryResult;
import me.prettyprint.hector.api.query.RangeSlicesQuery;
import me.prettyprint.hector.api.query.SliceQuery;
import uk.co.ross_warren.litter.Utils.CassandraHosts;
import uk.co.ross_warren.litter.Utils.MyConsistancyLevel;
import uk.co.ross_warren.litter.stores.FollowereeStore;
import uk.co.ross_warren.litter.stores.TweetStore;
import uk.co.ross_warren.litter.stores.UserStore;

public class TweetConnector {
	public TweetConnector()
	{
		
	}
	
	/*
	 * Updates a tweet's like count
	 */
	public void updateTweet(TweetStore store)
	{
		Cluster c; //V2
		try{
			c=CassandraHosts.getCluster();
		}catch (Exception et){
			System.out.println("Can't Connect to Cassandra. Check she is OK?");
			return;
		}
		
		try
		{
			ConsistencyLevelPolicy mcl = new MyConsistancyLevel();
			Keyspace ks = HFactory.createKeyspace("litter", c);  //V2
			ks.setConsistencyLevelPolicy(mcl);
			StringSerializer se = StringSerializer.get();
			Mutator<String> mutator = HFactory.createMutator(ks,se);
			Integer likes = store.getLikes();
			mutator.addInsertion(store.getTweetID(), "AllTweets", HFactory.createStringColumn("likes", likes.toString()));
			mutator.execute();
		}
		catch (Exception e)
		{
			System.out.println("Adding the tweet totally failed :(" + e);
		}
	}
	
	/*
	 * Deletes a tweet and all references to it
	 */
	public void deleteTweet(String tweetID)
	{
		Cluster c; //V2
		try{
			c=CassandraHosts.getCluster();
		}catch (Exception et){
			System.out.println("Can't Connect to Cassandra. Check she is OK?");
			return;
		}
		
		try{
			ConsistencyLevelPolicy mcl = new MyConsistancyLevel();
			Keyspace ks = HFactory.createKeyspace("litter", c);  //V2
			ks.setConsistencyLevelPolicy(mcl);
			StringSerializer se = StringSerializer.get();
			Mutator<String> mutator = HFactory.createMutator(ks,se);
			
			TweetStore store = getTweet(tweetID);
			if (store == null) return;
				
			String username = store.getUser();
			String reply = store.getReplyToUser();

			mutator.delete(username, "UserTweets", tweetID, se);
			mutator.execute();
			mutator = HFactory.createMutator(ks,se);
			mutator.delete(tweetID, "AllTweets", null, se);
			mutator.execute();
			mutator = HFactory.createMutator(ks,se);
			if (reply != null && !reply.equals(""))
			{
				mutator.delete(reply, "AtReplies", tweetID, se);
				mutator.execute();
			}
		}
		catch (Exception e)
		{
			System.out.println("Adding the tweet totally failed :(" + e);
		}
	}
	
	/*
	 * Adds a tweet to the alltweets column family, and attributes it to a user.
	 */
	public void addTweet(TweetStore store)
	{
		Cluster c; //V2
		try{
			c=CassandraHosts.getCluster();
		}catch (Exception et){
			System.out.println("Can't Connect to Cassandra. Check she is OK?");
			return;
		}
		try
		{
			System.out.println("User to add tweet:" + store.getUser());
			ConsistencyLevelPolicy mcl = new MyConsistancyLevel();
			Keyspace ks = HFactory.createKeyspace("litter", c);  //V2
			ks.setConsistencyLevelPolicy(mcl);
			StringSerializer se = StringSerializer.get();
			Mutator<String> mutator = HFactory.createMutator(ks,se);
			Long now = System.currentTimeMillis();
			store.setTweetID(store.getUser() + now);
			String time = now.toString();
			if (store.getReplyToUser() == null)
			{
				store.setReplyToUser("");
			}
			mutator.addInsertion(store.getUser(), "UserTweets", HFactory.createStringColumn(store.getTweetID(), time));
			mutator.execute();
			mutator = HFactory.createMutator(ks,se);
			mutator.addInsertion(store.getTweetID(), "AllTweets", HFactory.createStringColumn("user", store.getUser()));
			mutator.addInsertion(store.getTweetID(), "AllTweets", HFactory.createStringColumn("replyToUser", store.getReplyToUser()));
			mutator.addInsertion(store.getTweetID(), "AllTweets", HFactory.createStringColumn("content", store.getContent()));
			mutator.addInsertion(store.getTweetID(), "AllTweets", HFactory.createStringColumn("timestamp", time));
			try
			{
				mutator.addInsertion(store.getTweetID(), "AllTweets", HFactory.createStringColumn("longitude", store.getLongitude()));
				mutator.addInsertion(store.getTweetID(), "AllTweets", HFactory.createStringColumn("latitude", store.getLatitude()));
			}
			catch(Exception e)
			{
				// no location
			}
			mutator.addInsertion(store.getTweetID(), "AllTweets", HFactory.createStringColumn("likes", "0"));
			mutator.execute();
			if (!store.getReplyToUser().equals(""))
			{
				mutator = HFactory.createMutator(ks,se);
				mutator.addInsertion(store.getReplyToUser(), "AtReplies", HFactory.createStringColumn(store.getTweetID(), time));
				mutator.execute();
			}
	}
		catch (Exception e)
		{
			System.out.println("Adding the tweet totally failed :(" + e);
		}
	}
	
	/*
	 * Gets a users feed, meaning all their friends recent tweets
	 * and their @replies
	 */
	public List<TweetStore> getFeed(String username)
	{
		List<String> tweetIDs = new LinkedList<String>();
		UserConnector userConnector = new UserConnector();
		List<FollowereeStore> followees= userConnector.getFollowees(username);
		if (followees == null || followees.size() == 0) return null;
		List<TweetStore> tweets = new LinkedList<TweetStore>();
		List<TweetStore> tweets2 = new LinkedList<TweetStore>();
		for (FollowereeStore store: followees)
		{
			try
			{
				tweets.addAll(getTweets(store.getUsername()));
			}
			catch (Exception e)
			{
				System.out.println("oops" + e);
			}
		}
		try
		{
			tweets.addAll(getAtReplies(username));
			tweets.addAll(getTweets(username));
		}
		catch (Exception e)
		{
			System.out.println("oops" + e);
		}
		for (TweetStore tweet: tweets)
		{
			if (!tweetIDs.contains(tweet.getTweetID()))
			{
				tweetIDs.add(tweet.getTweetID());
				tweets2.add(tweet);
			}
		}
		if (tweets2 != null && tweets2.size() > 0) Collections.sort(tweets2);
		for (TweetStore tweet: tweets2)
		{
			try
			{
				UserConnector connect = new UserConnector();
				UserStore store = connect.getUserByUsername(tweet.getUser());
				store = connect.getUserByEmail(store.getEmail());
				tweet.setAvatarUrl(store.getAvatarUrl());
			}
			catch (Exception e)
			{
				//
			}
		}
		return tweets2;
	}
	
	/*
	 * Gets a single tweet based on ID.
	 */
	public TweetStore getTweet(String tweetID)
	{
		TweetStore result = new TweetStore();
		Cluster c; //V2
		try{
			c=CassandraHosts.getCluster();
		}catch (Exception et){
			System.out.println("Can't Connect to Cassandra. Check she is OK?");
			return null;
		}
		try
		{
			ConsistencyLevelPolicy mcl = new MyConsistancyLevel();
			Keyspace ks = HFactory.createKeyspace("litter", c);  //V2
			ks.setConsistencyLevelPolicy(mcl);
			StringSerializer se = StringSerializer.get();
			SliceQuery<String, String, String> q = HFactory.createSliceQuery(ks, se, se, se);
			q.setColumnFamily("AllTweets")
			.setKey(tweetID)
			.setColumnNames("user", "replyToUser", "content", "latitude", "longitude", "timestamp", "likes");
			QueryResult<ColumnSlice<String, String>> r = q.execute();
			ColumnSlice<String, String> slice = r.get();
			result.setReplyToUser(slice.getColumnByName("replyToUser").getValue());
			String time = slice.getColumnByName("timestamp").getValue();
			if (time != null && !time.equals(""))
			{
				result.setTimeStamp(Long.parseLong(time));
			}
			result.setTweetID(tweetID);
			result.setUser(slice.getColumnByName("user").getValue());
			result.setContent(slice.getColumnByName("content").getValue());
			try {
				result.setLatitude(slice.getColumnByName("latitude").getValue());
				result.setLongitude(slice.getColumnByName("longitude").getValue());
			}
			catch (Exception e)
			{
				result.setLatitude("");
				result.setLongitude("");
			}
			try {
				result.setLikes(Integer.parseInt(slice.getColumnByName("likes").getValue()));
			} catch (Exception e)
			{
				result.setLikes(0);
				System.out.println("getting likes count failed" + e);
			}
			
	
		}
		catch (Exception e)
		{
			System.out.println("There was a problem getting the tweet: " + e);
			return null;
		}
		return result;
	}
	
	/*
	 * Gets a users recent at replies
	 */
	public List<TweetStore> getAtReplies(String username)
	{
		List<TweetStore> list = new LinkedList<TweetStore>();
		Cluster c; //V2
		try{
			c=CassandraHosts.getCluster();
		}catch (Exception et){
			System.out.println("Can't Connect to Cassandra. Check she is OK?");
			return null;
		}
		
		try 
		{
			ConsistencyLevelPolicy mcl = new MyConsistancyLevel();
			Keyspace ks = HFactory.createKeyspace("litter", c);  //V2
			ks.setConsistencyLevelPolicy(mcl);
			StringSerializer se = StringSerializer.get();
			SliceQuery<String, String, String> q = HFactory.createSliceQuery(ks, se, se, se);
			q.setColumnFamily("AtReplies")
			.setKey(username)
			.setRange("", "", false, 100);
			QueryResult<ColumnSlice<String, String>> r = q.execute();
			ColumnSlice<String, String> slice = r.get();
			List<HColumn<String, String>> slices = slice.getColumns();
			for (HColumn<String, String> column: slices)
			{
				TweetStore store = new TweetStore();
				store.setTweetID(column.getName());
				if (column.getValue() != null && !column.getValue().equals(""))
				{
					store.setTimeStamp(Long.parseLong(column.getValue()));
				}
				try {
					TweetStore store2 = getTweet(column.getName());
					store.setUser(store2.getUser());
					store.setReplyToUser(store2.getReplyToUser());
					store.setContent(store2.getContent());
					store.setLikes(store2.getLikes());
					store.setLongitude(store2.getLongitude());
					store.setLatitude(store2.getLatitude());
				}
				catch (Exception e)
				{
					System.out.println("Tweet Error!" + e);
				}
				list.add(store);
			}
			return list;
		}
		catch (Exception e)
		{
			System.out.println("Getting at reply tweets failed miserably. Oh dear" + e);
			return null;
		}
	}
	
	/*
	 * Checks if a user has liked a tweet
	 */
	public Boolean checkLike(String username, String tweetID)
	{
		System.out.println("Checking if " + username + " likes " + tweetID);
		Cluster c; //V2
		try{
			c=CassandraHosts.getCluster();
		}catch (Exception et){
			System.out.println("Can't Connect to Cassandra. Check she is OK?");
			return null;
		}
		
		try
		{
			ConsistencyLevelPolicy mcl = new MyConsistancyLevel();
			Keyspace ks = HFactory.createKeyspace("litter", c);  //V2
			ks.setConsistencyLevelPolicy(mcl);
			StringSerializer se = StringSerializer.get();
			RangeSlicesQuery<String, String, String> rangeSlicesQuery =
				HFactory.createRangeSlicesQuery(ks, se, se, se);
				rangeSlicesQuery.setColumnFamily("Likes");
				rangeSlicesQuery.setKeys(username, username);
				rangeSlicesQuery.setRange(tweetID, tweetID, false, 999);
				QueryResult<OrderedRows<String, String, String>> result = rangeSlicesQuery.execute();
			OrderedRows<String, String, String> rows = result.get();
			
			if (rows != null && rows.getByKey(username) != null && rows.getByKey(username).getColumnSlice().getColumns() != null)
			{
				if (rows.getByKey(username).getColumnSlice().getColumns().isEmpty() == false) return true;
			}
			return false;
		} catch (Exception e)
		{
			System.out.println("Couldn't check if the tweet is liked :(" + e);
			return false;
		}
	}
	
	/*
	 * Sets the user to have unliked a tweet
	 */
	public void unLike(String username, String tweetID)
	{
		if (checkLike(username, tweetID) == true)
		{
			Cluster c; //V2
			try{
				c=CassandraHosts.getCluster();
			}catch (Exception et){
				System.out.println("Can't Connect to Cassandra. Check she is OK?");
				return;
			}
			
			try
			{
				ConsistencyLevelPolicy mcl = new MyConsistancyLevel();
				Keyspace ks = HFactory.createKeyspace("litter", c);  //V2
				ks.setConsistencyLevelPolicy(mcl);
				StringSerializer se = StringSerializer.get();
				Mutator<String> mutator = HFactory.createMutator(ks,se);
				mutator.delete(username, "Likes", tweetID, se);
				mutator.execute();
				TweetStore tweet = getTweet(tweetID);
				try
				{
					tweet.setLikes(tweet.getLikes() - 1);
				} catch (Exception e)
				{
					tweet.setLikes(1);
				}
				updateTweet(tweet);
			}
			catch (Exception e)
			{
				System.out.println("Errors!" + e);
			}
			
		} else {
			System.out.println("Wasn't liked anyway! What are you doing calling that? you fool!");
		}
	}
		
	/*
	 * Likes a tweet for a username
	 */
	public void like(String username, String tweetID)
	{
		if (checkLike(username, tweetID) == false)
		{
			Cluster c; //V2
			try{
				c=CassandraHosts.getCluster();
			}catch (Exception et){
				System.out.println("Can't Connect to Cassandra. Check she is OK?");
				return;
			}
			
			try
			{
				ConsistencyLevelPolicy mcl = new MyConsistancyLevel();
				Keyspace ks = HFactory.createKeyspace("litter", c);  //V2
				ks.setConsistencyLevelPolicy(mcl);
				StringSerializer se = StringSerializer.get();
				Mutator<String> mutator = HFactory.createMutator(ks,se);
				Long now = System.currentTimeMillis();
				mutator.addInsertion(username, "Likes", HFactory.createStringColumn(tweetID, now.toString()));
				mutator.execute();
				TweetStore tweet = getTweet(tweetID);
				try
				{
					tweet.setLikes(tweet.getLikes() + 1);
				} catch (Exception e)
				{
					tweet.setLikes(1);
				}
				updateTweet(tweet);
			}
			catch (Exception e)
			{
				System.out.println("Errors!" + e);
			}
			
		} else {
			System.out.println("Already liked");
		}
	}
	
	/*
	 * Gets a users recent tweets
	 */
	public List<TweetStore> getTweets(String username)
	{
		List<TweetStore> list = new LinkedList<TweetStore>();
		Cluster c; //V2
		try{
			c=CassandraHosts.getCluster();
		}catch (Exception et){
			System.out.println("Can't Connect to Cassandra. Check she is OK?");
			return null;
		}
		
		try 
		{
			ConsistencyLevelPolicy mcl = new MyConsistancyLevel();
			Keyspace ks = HFactory.createKeyspace("litter", c);  //V2
			ks.setConsistencyLevelPolicy(mcl);
			StringSerializer se = StringSerializer.get();
			SliceQuery<String, String, String> q = HFactory.createSliceQuery(ks, se, se, se);
			q.setColumnFamily("UserTweets")
			.setKey(username)
			.setRange("", "", false, 100);
			QueryResult<ColumnSlice<String, String>> r = q.execute();
			ColumnSlice<String, String> slice = r.get();
			List<HColumn<String, String>> slices = slice.getColumns();
			for (HColumn<String, String> column: slices)
			{
				TweetStore store = new TweetStore();
				store.setTweetID(column.getName());
				if (column.getValue() != null && !column.getValue().equals(""))
				{
					store.setTimeStamp(Long.parseLong(column.getValue()));
				}
				try {
					TweetStore store2 = getTweet(column.getName());
					store.setUser(store2.getUser());
					store.setReplyToUser(store2.getReplyToUser());
					store.setContent(store2.getContent());
					store.setLikes(store2.getLikes());
					store.setLongitude(store2.getLongitude());
					store.setLatitude(store2.getLatitude());
				}
				catch (Exception e)
				{
					System.out.println("Tweet Error!" + e);
				}
				list.add(store);
			}
			return list;
		}
		catch (Exception e)
		{
			System.out.println("Getting tweets failed miserably. Oh dear" + e);
			return null;
		}
	}
}
