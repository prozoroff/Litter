package uk.co.ross_warren.litter.Utils;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import uk.co.ross_warren.litter.stores.CassandraStore;

import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.factory.HFactory;
//import me.prettyprint.cassandra.service.Cluster;
import me.prettyprint.cassandra.service.*;

public final class CassandraHosts {
	static Cluster c=null;
	
	public CassandraHosts() {
	}
	
	public static String getHost() {
		return CassandraStore.instance().getHost();
	}
	
	public List<CassandraHost> getPoolHosts()
	{
		if (c==null){
			// System.out.println("Creating cluster connection");
			c = HFactory.getOrCreateCluster(CassandraStore.instance().getClusterName(), CassandraStore.instance().getHost() + ":" + CassandraStore.instance().getPort());
		}
		Set <CassandraHost>hosts= c.getKnownPoolHosts(false);

		Iterator<CassandraHost> it =hosts.iterator();
		int i=0;
		List<CassandraHost> returnHosts = new LinkedList<CassandraHost>();
		while (it.hasNext()) {
			
			CassandraHost ch=it.next();
			returnHosts.add(ch);
			i++;
		}
		return returnHosts;
	}
	
	public List<CassandraHost> getDownedPoolHosts()
	{
		if (c==null){
			// System.out.println("Creating cluster connection");
			c = HFactory.getOrCreateCluster(CassandraStore.instance().getClusterName(), CassandraStore.instance().getHost() + ":" + CassandraStore.instance().getPort());
		}
		Set <CassandraHost>hosts= c.getConnectionManager().getDownedHosts();
		Iterator<CassandraHost> it =hosts.iterator();
		int i=0;
		List<CassandraHost> returnHosts = new LinkedList<CassandraHost>();
		while (it.hasNext()) {
			CassandraHost ch=it.next();
			returnHosts.add(ch);
			i++;
		}
		return returnHosts;
	}
	
	
	
	public static String[] getHosts() {
		if (c==null){
			// System.out.println("Creating cluster connection");
			c = HFactory.getOrCreateCluster(CassandraStore.instance().getClusterName(), CassandraStore.instance().getHost() + ":" + CassandraStore.instance().getPort());
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
			c = HFactory.getOrCreateCluster(CassandraStore.instance().getClusterName(), CassandraStore.instance().getHost() + ":" + CassandraStore.instance().getPort());
			getHosts();	
			Keyspaces.SetUpKeySpaces(c);
			return c;
	}	
}