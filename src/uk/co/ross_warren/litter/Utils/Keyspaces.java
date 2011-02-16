package uk.co.ross_warren.litter.Utils;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.ddl.ColumnFamilyDefinition;

import me.prettyprint.hector.api.ddl.KeyspaceDefinition;

import java.util.ArrayList;
import java.util.List;

import me.prettyprint.cassandra.model.BasicColumnFamilyDefinition;
import me.prettyprint.cassandra.service.*;
import me.prettyprint.hector.api.ddl.ComparatorType;
import me.prettyprint.hector.api.*;

public final class Keyspaces {
	public Keyspaces(){
	}
	public static void SetUpKeySpaces(Cluster c){
		try{
			try{
				c.describeKeyspace("litter");
			}catch(Exception et){
				System.out.println("Keyspace probably doesn't exist, tryping to create it" + et);
				List<ColumnFamilyDefinition> cfs = new ArrayList<ColumnFamilyDefinition>(); 
				BasicColumnFamilyDefinition user = new BasicColumnFamilyDefinition(); 
				BasicColumnFamilyDefinition username = new BasicColumnFamilyDefinition(); 
				BasicColumnFamilyDefinition followers = new BasicColumnFamilyDefinition();
				BasicColumnFamilyDefinition followees = new BasicColumnFamilyDefinition();
				BasicColumnFamilyDefinition alltweets = new BasicColumnFamilyDefinition();
				BasicColumnFamilyDefinition usertweets = new BasicColumnFamilyDefinition();
				BasicColumnFamilyDefinition atreplies = new BasicColumnFamilyDefinition();
				BasicColumnFamilyDefinition likes = new BasicColumnFamilyDefinition();
				
				user.setName("User");
				username.setName("Username");
				followers.setName("Followers");
				followees.setName("Followees");
				alltweets.setName("AllTweets");
				usertweets.setName("UserTweets");
				atreplies.setName("AtReplies");
				likes.setName("Likes");
				user.setKeyspaceName("litter");
				username.setKeyspaceName("litter");
				followers.setKeyspaceName("litter");
				followees.setKeyspaceName("litter");
				alltweets.setKeyspaceName("litter");
				usertweets.setKeyspaceName("litter");
				atreplies.setKeyspaceName("litter");
				likes.setKeyspaceName("litter");
				user.setComparatorType(ComparatorType.BYTESTYPE);
				username.setComparatorType(ComparatorType.BYTESTYPE);
				followers.setComparatorType(ComparatorType.BYTESTYPE);
				followees.setComparatorType(ComparatorType.BYTESTYPE);
				alltweets.setComparatorType(ComparatorType.BYTESTYPE);
				usertweets.setComparatorType(ComparatorType.BYTESTYPE);
				atreplies.setComparatorType(ComparatorType.BYTESTYPE);
				likes.setComparatorType(ComparatorType.BYTESTYPE);
				ColumnFamilyDefinition userdef = new ThriftCfDef(user); 
				ColumnFamilyDefinition usernamedef = new ThriftCfDef(username);
				ColumnFamilyDefinition followerdef = new ThriftCfDef(followers); 
				ColumnFamilyDefinition followeedef = new ThriftCfDef(followees);
				ColumnFamilyDefinition alltweetsdef = new ThriftCfDef(alltweets); 
				ColumnFamilyDefinition usertweetsdef = new ThriftCfDef(usertweets);
				ColumnFamilyDefinition atrepliesdef = new ThriftCfDef(atreplies); 
				ColumnFamilyDefinition likesdef = new ThriftCfDef(likes);
				cfs.add(userdef);
				cfs.add(usernamedef);
				cfs.add(followerdef);
				cfs.add(followeedef);
				cfs.add(alltweetsdef);
				cfs.add(usertweetsdef);
				cfs.add(atrepliesdef);
				cfs.add(likesdef);
				KeyspaceDefinition ks=HFactory.createKeyspaceDefinition("litter","org.apache.cassandra.locator.SimpleStrategy", 1, cfs);
				c.addKeyspace(ks);
				System.out.println("Keyspace created ?");
				try{
					c.describeKeyspace("litter");
				}catch(Exception et2){
					System.out.println("Oops no it wasn't!" +et2);
				}
			}
		}catch(Exception et){
			System.out.println("Other keyspace or column definition error" +et);
		}
	}
}
