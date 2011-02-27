package uk.co.ross_warren.litter.connectors;

import java.util.ArrayList;
import java.util.List;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.ConsistencyLevelPolicy;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.ColumnSlice;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;
import me.prettyprint.hector.api.query.ColumnQuery;
import me.prettyprint.hector.api.query.QueryResult;
import me.prettyprint.hector.api.query.SliceQuery;
import me.prettyprint.cassandra.serializers.StringSerializer;
import uk.co.ross_warren.litter.Utils.CassandraHosts;
import uk.co.ross_warren.litter.Utils.MyConsistancyLevel;
import uk.co.ross_warren.litter.stores.*;

public class UserConnector {
	public UserConnector(){
	}
	
	public String connectionTest()
	{
		Cluster c; //V2
		try {
			c=CassandraHosts.getCluster();
			ConsistencyLevelPolicy mcl = new MyConsistancyLevel();
			Keyspace ks = HFactory.createKeyspace("litter", c);  //V2
			ks.setConsistencyLevelPolicy(mcl);
			StringSerializer se = StringSerializer.get();
			SliceQuery<String, String, String> q = HFactory.createSliceQuery(ks, se, se, se);
			q.setColumnFamily("UserTweets")
			.setKey("test")
			.setRange("", "", false, 100);
			QueryResult<ColumnSlice<String, String>> r = q.execute();
			return "Connection Success";
		} catch (Exception et){
			System.out.println("Can't Connect to Cassandra. Check she is OK?");
			return "Connection Failed - " + et;
		}
	}
	
	/*
	 * Gets a user with attributes username and email based on username
	 */
	public UserStore getUserByUsername(String username)
	{
		UserStore user = new UserStore();
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
			ColumnQuery<String, String, String> columnQuery =
				HFactory.createStringColumnQuery(ks);
			columnQuery.setColumnFamily("Username").setKey(username).setName("email");
			QueryResult<HColumn<String, String>> result = columnQuery.execute();
			user.login(result.get().getValue());
			user.setUserName(username);
			user.setEmail(result.get().getValue());
			return user;
		}catch (Exception et){
			System.out.println("Evil errors occured getting the user by username!- "+et);
			return null;
		}
	}
	
	/*
	 * Gets a user and all their attributes based on email
	 */
	public UserStore getUserByEmail(String email) 
	{
		UserStore user = new UserStore();
		Cluster c; //V2
		try{
			c=CassandraHosts.getCluster();
		}catch (Exception et){
			System.out.println("Can't connect to Cassandra. Maybe she is spending the night in Appolo's temple? -"+et);
			return null;
		}
		try
		{
			ConsistencyLevelPolicy mcl = new MyConsistancyLevel();
			Keyspace ks = HFactory.createKeyspace("litter", c);  //V2
			ks.setConsistencyLevelPolicy(mcl);
			StringSerializer se = StringSerializer.get();
			SliceQuery<String, String, String> q = HFactory.createSliceQuery(ks, se, se, se);
			q.setColumnFamily("User")
			.setKey(email)
			.setColumnNames("name", "bio", "username", "avatarurl");
			QueryResult<ColumnSlice<String, String>> r = q.execute();
			ColumnSlice<String, String> slice = r.get();
			user.login(email);
			user.setName(slice.getColumnByName("name").getValue());
			user.setUserName(slice.getColumnByName("username").getValue().toLowerCase());
			user.setBio(slice.getColumnByName("bio").getValue());
			user.setAvatar(slice.getColumnByName("avatarurl").getValue());
			return user;
		}catch (Exception et){
			System.out.println("Can't get user by email :( Nicht so gut ja? - "+et);
			return null;
		}
	}
	
	/*
	 * Updates a users details
	 */
	public boolean updateUser(UserStore Author){
		Cluster c; //V2
		try{
			c=CassandraHosts.getCluster();
		}catch (Exception et){
			System.out.println("Couldn't connect to Cassandra. Maybe she is ignoring you. Probably best just to apologise. "+et);
			return false;
		}
		try{
			if (this.getUserByUsername(Author.getUserName()) != null)
			{
				System.out.println("User " + Author.getUserName() + " exists, updating now.");
				ConsistencyLevelPolicy mcl = new MyConsistancyLevel();
				Keyspace ks = HFactory.createKeyspace("litter", c);  //V2
				ks.setConsistencyLevelPolicy(mcl);
				StringSerializer se = StringSerializer.get();
				Mutator<String> mutator = HFactory.createMutator(ks,se);
				mutator.addInsertion(Author.getEmail(), "User", HFactory.createStringColumn("name", Author.getName()))
					.addInsertion(Author.getEmail(), "User", HFactory.createStringColumn("bio", Author.getBio()))
					.addInsertion(Author.getEmail(), "User", HFactory.createStringColumn("avatarurl", Author.getAvatarUrl()));
				mutator.execute();			
			} else {
				System.out.println("Username not found");
				return false;
			}
			return true;
		}catch (Exception et){
			System.out.println("Can't update the user :( ." + et);
			return false;
		}
	}

	/*
	 * Deletes a user (UNTESTED)
	 */
	public void deleteUser(String username, String email, List<FollowereeStore> followers, List<FollowereeStore> followees)
	{
		Cluster c; //V2
		try{
			c=CassandraHosts.getCluster();
		}catch (Exception et){
			System.out.println("Couldn't connect to Cassandra. Maybe she is ignoring you. Probably best just to apologise. "+et);
			return;
		}
		try
		{
			ConsistencyLevelPolicy mcl = new MyConsistancyLevel();
			Keyspace ks = HFactory.createKeyspace("litter", c);  //V2
			ks.setConsistencyLevelPolicy(mcl);
			StringSerializer se = StringSerializer.get();
			Mutator<String> mutator = HFactory.createMutator(ks,se);
			mutator.delete(username, "Likes", null, se);
			mutator.execute();
			mutator = HFactory.createMutator(ks,se);
			mutator.delete(username, "UserTweets", null, se);
			mutator.execute();
			mutator = HFactory.createMutator(ks,se);
			mutator.delete(username, "AtReplies", null, se);
			mutator.execute();
			mutator = HFactory.createMutator(ks,se);
			mutator.delete(username, "Followers", null, se);
			mutator.execute();
			mutator = HFactory.createMutator(ks,se);
			mutator.delete(username, "Followees", null, se);
			mutator.execute();
			mutator = HFactory.createMutator(ks,se);
			mutator.delete(username, "Username", null, se);
			mutator.execute();
			mutator = HFactory.createMutator(ks,se);
			mutator.delete(email, "User", null, se);
			mutator.execute();
			
			for (FollowereeStore store: followers)
			{
				removeFollowee(store.getUsername(), username);
			}
			for (FollowereeStore store: followees)
			{
				removeFollower(username, store.getUsername());
			}
			
		} catch(Exception e)
		{
			System.out.println("Error in deleting " + e);
		}
		
	}
	
	/*
	 * Adds a user to the database
	 */
	public boolean addUser(UserStore Author){
		Cluster c; //V2
		try{
			c=CassandraHosts.getCluster();
		}catch (Exception et){
			System.out.println("Couldn't connect to Cassandra. Maybe she is ignoring you. Probably best just to apologise. "+et);
			return false;
		}
		try{
			if (this.getUserByUsername(Author.getUserName()) == null && this.getUserByEmail(Author.getEmail()) == null)
			{
				System.out.println("User " + Author.getUserName() + " probably Doesn't exist, adding now.");
				ConsistencyLevelPolicy mcl = new MyConsistancyLevel();
				Keyspace ks = HFactory.createKeyspace("litter", c);  //V2
				ks.setConsistencyLevelPolicy(mcl);
				StringSerializer se = StringSerializer.get();
				Mutator<String> mutator = HFactory.createMutator(ks,se);
				mutator.addInsertion(Author.getEmail(), "User", HFactory.createStringColumn("name", Author.getName()))
					.addInsertion(Author.getEmail(), "User", HFactory.createStringColumn("username", Author.getUserName()))
					.addInsertion(Author.getEmail(), "User", HFactory.createStringColumn("bio", Author.getBio()))
					.addInsertion(Author.getEmail(), "User", HFactory.createStringColumn("avatarurl", Author.getAvatarUrl()));
				mutator.execute();
				mutator = HFactory.createMutator(ks,se);
				mutator.addInsertion(Author.getUserName(), "Username", HFactory.createStringColumn("email", Author.getEmail()));
				mutator.execute();
				
			} else {
				System.out.println("Username taken, or there was an error reading the database");
				return false;
			}
			return true;
		}catch (Exception et){
			System.out.println("Can't add the user :( Cassandra must be in a bad mood today." + et);
			return false;
		}
	}

	/*
	 * adds a follower to a user
	 */
	public boolean addFollower(String toFollow, String toBeFollowed)
	{
		Cluster c; //V2
		try{
			c=CassandraHosts.getCluster();
		}catch (Exception et){
			System.out.println("Can't connect to Cassandra. Have you tried turning her on? - " + et);
			return false;
		}
		try{
			if (this.getUserByUsername(toFollow) !=null && this.getUserByUsername(toBeFollowed) !=null)
			{
				ConsistencyLevelPolicy mcl = new MyConsistancyLevel();
				Keyspace ks = HFactory.createKeyspace("litter", c);  //V2
				ks.setConsistencyLevelPolicy(mcl);
				StringSerializer se = StringSerializer.get();
				
				//--------------------- Check if it already exists
				//RangeSlicesQuery<String, String, String> rangeSlicesQuery =
				//	HFactory.createRangeSlicesQuery(ks, se, se, se);
				//	rangeSlicesQuery.setColumnFamily("Followers");
				//	rangeSlicesQuery.setKeys(toBeFollowed, toBeFollowed);
				//	rangeSlicesQuery.setRange(toFollow, toFollow, false, 999);
				//	QueryResult<OrderedRows<String, String, String>> result = rangeSlicesQuery.execute();
				//OrderedRows<String, String, String> rows = result.get();
				//if (rows.getByKey(toBeFollowed).getColumnSlice().getColumns().isEmpty() == false) return false;
				//---------------------- 
				Long now = System.currentTimeMillis();
				Mutator<String> mutator = HFactory.createMutator(ks,se);
				mutator.addInsertion(toBeFollowed, "Followers", HFactory.createStringColumn(toFollow, now.toString()));
				mutator.execute();
				return true;
			}
			else {
				System.out.println("You are trying to connect users that don't exist. Shame on you!");
				return false;
			}
		}catch (Exception et){
			System.out.println("Adding the follower went terribly wrong. I suggest hiding underground until it all blows over. -" + et);
			return false;
		}
	}
	
	/*
	 * removes a follower from a user
	 */
	public boolean removeFollower(String toFollow, String toBeFollowed)
	{
		Cluster c; //V2
		try{
			c=CassandraHosts.getCluster();
		}catch (Exception et){
			System.out.println("Connecting to Cassandra failed. Better make sure she is not with that Ajax fellow again. - " + et);
			return false;
		}
		try{
			ConsistencyLevelPolicy mcl = new MyConsistancyLevel();
			Keyspace ks = HFactory.createKeyspace("litter", c);  //V2
			ks.setConsistencyLevelPolicy(mcl);
			StringSerializer se = StringSerializer.get();
			Mutator<String> mutator = HFactory.createMutator(ks,se);
			mutator.delete(toBeFollowed, "Followers", toFollow, se);
			mutator.execute();
			return true;
		}catch (Exception et){
			System.out.println("There was a problem removing the follower. Maybe it is a STALKER :0 - "+et);
			return false;
		}
	}
	
	public boolean addFollowee(String toFollow, String toBeFollowed)
	{
		Cluster c; //V2
		try{
			c=CassandraHosts.getCluster();
		}catch (Exception et){
			System.out.println("Can't connect to Cassandra :( Maybe her memory leaked again. - " + et);
			return false;
		}
		try{
			if (this.getUserByUsername(toFollow) !=null && this.getUserByUsername(toBeFollowed) !=null)
			{
				ConsistencyLevelPolicy mcl = new MyConsistancyLevel();
				Keyspace ks = HFactory.createKeyspace("litter", c);  //V2
				ks.setConsistencyLevelPolicy(mcl);
				StringSerializer se = StringSerializer.get();
				Long now = System.currentTimeMillis();
				Mutator<String> mutator = HFactory.createMutator(ks,se);
				mutator.addInsertion(toFollow, "Followees", HFactory.createStringColumn(toBeFollowed, now.toString()));
				mutator.execute();
				return true;
			}
			else {
				return false;
			}
		}catch (Exception et){
			System.out.println("Can't add followee. Some people just don't like being followed. "+et);
			return false;
		}
	}
	
	/*
	 * user unfollows another user
	 */
	public boolean removeFollowee(String toFollow, String toBeFollowed)
	{
		Cluster c; //V2
		try{
			c=CassandraHosts.getCluster();
		}catch (Exception et){
			System.out.println("Cassandra is no where to be seen! -"+et);
			return false;
		}
		try{
			ConsistencyLevelPolicy mcl = new MyConsistancyLevel();
			Keyspace ks = HFactory.createKeyspace("litter", c);  //V2
			ks.setConsistencyLevelPolicy(mcl);
			StringSerializer se = StringSerializer.get();
			Mutator<String> mutator = HFactory.createMutator(ks,se);
			mutator.delete(toFollow, "Followees", toBeFollowed, se);
			mutator.execute();
			return true;
		}catch (Exception et){
			System.out.println("Can't remove the followee. A helpful error message shall follow... "+et);
			return false;
		}
	}
	
	/*
	 * Get those who the user follows
	 */
	public List<FollowereeStore> getFollowees(String username)
	{
		Cluster c; //V2
		try{
			c=CassandraHosts.getCluster();
		}catch (Exception et){
			System.out.println("Cassandra is not here right now. Please leave your query after the tone. -"+et);
			return null;
		}
		try {
			List<FollowereeStore> results = new ArrayList<FollowereeStore>();
	
			ConsistencyLevelPolicy mcl = new MyConsistancyLevel();
			Keyspace ks = HFactory.createKeyspace("litter", c);  //V2
			ks.setConsistencyLevelPolicy(mcl);
			StringSerializer se = StringSerializer.get();
			SliceQuery<String, String, String> q = HFactory.createSliceQuery(ks, se, se, se);
			q.setColumnFamily("Followees")
			.setKey(username)
			.setRange("", "", true, 999);
			QueryResult<ColumnSlice<String, String>> r = q.execute();
			ColumnSlice<String, String> slice = r.get();
			List<HColumn<String, String>> slices = slice.getColumns();
			for (HColumn<String, String> column: slices)
			{
				FollowereeStore result = new FollowereeStore();
				result.setUsername(column.getName());
				if (column.getValue() != null && !column.getValue().equals(""))
				{
					result.setDate(Long.parseLong(column.getValue()));
				}
				results.add(result);
			}
			return results;
		}
		catch (Exception e) {
			System.out.println("There were some problems getting the users the user follows. - "+ e);
			return null;
		}
	}
	
	/*
	 * Get a users followers
	 */
	public List<FollowereeStore> getFollowers(String username)
	{
		Cluster c; //V2
		try{
			c=CassandraHosts.getCluster();
		}catch (Exception et){
			System.out.println("Cassandra is too busy hearing the future to bother with the present. Sorry. -"+et);
			return null;
		}
		try {
			List<FollowereeStore> results = new ArrayList<FollowereeStore>();
			ConsistencyLevelPolicy mcl = new MyConsistancyLevel();
			Keyspace ks = HFactory.createKeyspace("litter", c);  //V2
			ks.setConsistencyLevelPolicy(mcl);
			StringSerializer se = StringSerializer.get();
			SliceQuery<String, String, String> q = HFactory.createSliceQuery(ks, se, se, se);
			q.setColumnFamily("Followers")
			.setKey(username)
			.setRange("", "", true, 999);
			QueryResult<ColumnSlice<String, String>> r = q.execute();
			ColumnSlice<String, String> slice = r.get();
			List<HColumn<String, String>> slices = slice.getColumns();
			for (HColumn<String, String> column: slices)
			{
				FollowereeStore result = new FollowereeStore();
				result.setUsername(column.getName());
				if (column.getValue() != null && !column.getValue().equals(""))
				{
					result.setDate(Long.parseLong(column.getValue()));
				}
				results.add(result);
			}
			return results;
		}
		catch (Exception e) {
			System.out.println("Getting the followers was not as simple as first appeared. Are they ALL stalkers? " + e);
			return null;
		}
	}
}
