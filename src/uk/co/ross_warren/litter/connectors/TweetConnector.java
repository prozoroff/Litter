package uk.co.ross_warren.litter.connectors;

import java.util.LinkedList;
import java.util.List;

import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.ConsistencyLevelPolicy;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.ColumnSlice;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.query.QueryResult;
import me.prettyprint.hector.api.query.SliceQuery;
import uk.co.ross_warren.litter.Utils.CassandraHosts;
import uk.co.ross_warren.litter.Utils.MyConsistancyLevel;
import uk.co.ross_warren.litter.stores.TweetStore;

public class TweetConnector {
	public TweetConnector()
	{
		
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
	
	public List<Integer> getTweets(String username)
	{
		List<Integer> list = new LinkedList<Integer>();
		return list;
	}
}
