package uk.co.ross_warren.litter.stores;

public class CassandraStore {
	protected CassandraStore()
	{
		
	}
	static private CassandraStore _instance = null;
	
	static public CassandraStore instance()
	{
		if (null == _instance)
		{
			_instance = new CassandraStore();
		}
		return _instance;
	}
	
	public void setClusterName(String clusterName) {
		ClusterName = clusterName;
	}
	public String getClusterName() {
		return ClusterName;
	}
	public void setPort(String port) {
		Port = port;
	}

	public String getPort() {
		return Port;
	}
	public void setHost(String host) {
		Host = host;
	}

	public String getHost() {
		return Host;
	}
	private String Host = "127.0.0.1"; // Cassandra Server I.P.
	private String Port = "9160";
	private String ClusterName = "Test Cluster";
}
