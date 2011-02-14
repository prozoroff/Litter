package uk.co.ross_warren.litter.Utils;

import java.util.Iterator;
import java.util.Set;

import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.factory.HFactory;
//import me.prettyprint.cassandra.service.Cluster;
import me.prettyprint.cassandra.service.*;

public final class CassandraHosts {
	static Cluster c=null;
	static String Host ="127.0.0.1"; // Cassandra Server I.P.
	static String Port = "9160";
	static String ClusterName = "Test Cluster";
	
	public CassandraHosts() {
	}
	
	public static String getHost() {
		return (Host);
	}
	
	public static String[] getHosts() {
		if (c==null){
			// System.out.println("Creating cluster connection");
			c = HFactory.getOrCreateCluster(ClusterName, Host + ":" + Port);
		}
		Set <CassandraHost>hosts= c.getKnownPoolHosts(false);
			
		String sHosts[] = new String[hosts.size()];
		Iterator<CassandraHost> it =hosts.iterator();
		int i=0;
		while (it.hasNext()) {
			CassandraHost ch=it.next();
			   
			sHosts[i]=(String)ch.getHost();
			//System.out.println("Hosts"+sHosts[i]);
			i++;
		}
		return sHosts;
	}
	
	public static Cluster getCluster(){
			c = HFactory.getOrCreateCluster(ClusterName, Host + ":" + Port);
			getHosts();	
			Keyspaces.SetUpKeySpaces(c);
			return c;
	}	
}