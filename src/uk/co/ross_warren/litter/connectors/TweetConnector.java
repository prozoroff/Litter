package uk.co.ross_warren.litter.connectors;

import java.util.LinkedList;
import java.util.List;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.ConsistencyLevelPolicy;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.ColumnSlice;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;
import me.prettyprint.hector.api.query.QueryResult;
import me.prettyprint.hector.api.query.SliceQuery;
import uk.co.ross_warren.litter.Utils.CassandraHosts;
import uk.co.ross_warren.litter.Utils.MyConsistancyLevel;
import uk.co.ross_warren.litter.stores.TweetStore;

public class TweetConnector {
	public TweetConnector()
	{
		
	}
	
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
			ConsistencyLevelPolicy mcl = new MyConsistancyLevel();
			Keyspace ko = HFactory.createKeyspace("litter", c);  //V2
			ko.setConsistencyLevelPolicy(mcl);
			StringSerializer se = StringSerializer.get();
			Mutator<String> mutator = HFactory.createMutator(ko,se);
			Long now = System.currentTimeMillis();
			store.setTweetID(store.getUser() + now);
			String time = now.toString();
			mutator.addInsertion(store.getUser(), "UserTweets", HFactory.createStringColumn(store.getTweetID(), time));
			mutator.execute();
			mutator = HFactory.createMutator(ko,se);
			mutator.addInsertion(store.getTweetID(), "AllTweets", HFactory.createStringColumn("user", store.getUser()));
			mutator.addInsertion(store.getTweetID(), "AllTweets", HFactory.createStringColumn("replyToUser", store.getReplyToUser()));
			mutator.addInsertion(store.getTweetID(), "AllTweets", HFactory.createStringColumn("content", store.getContent()));
			mutator.addInsertion(store.getTweetID(), "AllTweets", HFactory.createStringColumn("timestamp", time));
			mutator.execute();
		}
		catch (Exception e)
		{
			System.out.println("Adding the tweet totally failed :(" + e);
		}
	}
	
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
			Keyspace ko = HFactory.createKeyspace("litter", c);  //V2
			ko.setConsistencyLevelPolicy(mcl);
			StringSerializer se = StringSerializer.get();
			SliceQuery<String, String, String> q = HFactory.createSliceQuery(ko, se, se, se);
			q.setColumnFamily("AllTweets")
			.setKey(tweetID)
			.setColumnNames("user", "replyToUser", "content", "timestamp");
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
		}
		catch (Exception e)
		{
			System.out.println("There was a problem getting the tweet: " + e);
			return null;
		}
		return result;
	}
	
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
			Keyspace ko = HFactory.createKeyspace("litter", c);  //V2
			ko.setConsistencyLevelPolicy(mcl);
			StringSerializer se = StringSerializer.get();
			SliceQuery<String, String, String> q = HFactory.createSliceQuery(ko, se, se, se);
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
