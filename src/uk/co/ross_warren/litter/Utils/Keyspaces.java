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
				//System.out.println("Keyspace probably doesn't exist, tryping to create it"+et);
				List<ColumnFamilyDefinition> cfs = new ArrayList<ColumnFamilyDefinition>(); 
				BasicColumnFamilyDefinition user = new BasicColumnFamilyDefinition(); 
				BasicColumnFamilyDefinition username = new BasicColumnFamilyDefinition(); 
				BasicColumnFamilyDefinition followers = new BasicColumnFamilyDefinition();
				BasicColumnFamilyDefinition followees = new BasicColumnFamilyDefinition();
				user.setName("User");
				username.setName("Username");
				followers.setName("Followers");
				followees.setName("Followees");
				user.setKeyspaceName("litter");
				username.setKeyspaceName("litter");
				followers.setKeyspaceName("litter");
				followees.setKeyspaceName("litter");
				user.setComparatorType(ComparatorType.BYTESTYPE);
				username.setComparatorType(ComparatorType.BYTESTYPE);
				followers.setComparatorType(ComparatorType.BYTESTYPE);
				followees.setComparatorType(ComparatorType.BYTESTYPE);
				ColumnFamilyDefinition userdef = new ThriftCfDef(user); 
				ColumnFamilyDefinition usernamedef = new ThriftCfDef(username);
				ColumnFamilyDefinition followerdef = new ThriftCfDef(followers); 
				ColumnFamilyDefinition followeedef = new ThriftCfDef(followees);
				cfs.add(userdef);
				cfs.add(usernamedef);
				cfs.add(followerdef);
				cfs.add(followeedef);
				KeyspaceDefinition ks=HFactory.createKeyspaceDefinition("litter","org.apache.cassandra.locator.SimpleStrategy", 1, cfs);
				c.addKeyspace(ks);
				//System.out.println("Keyspace created ?");
				try{
					c.describeKeyspace("litter");
				//	System.out.println("Keyspace: "+kd.getName());
				//	System.out.println("Replication: "+kd.getReplicationFactor());
				//	System.out.println("Strategy: "+kd.getStrategyClass());
				}catch(Exception et2){
				//	System.out.println("Opps no it wasn't !" +et2);
				}
			}
			
			
		}catch(Exception et){
			System.out.println("Other keyspace or column definition error" +et);
		}
		
	}
}
