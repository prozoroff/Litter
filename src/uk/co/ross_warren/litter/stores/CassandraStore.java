package uk.co.ross_warren.litter.stores;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class CassandraStore {
	protected CassandraStore()
	{
		try {
			LoadSettingsFromFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	
	public String getAdmin()
	{
		return Admin;
	}
	
	
	private String Host = "134.36.36.83"; // Cassandra Server I.P.
	//private String Host = "127.0.0.1"; // Cassandra Server I.P.
	private String Port = "9160";
	private String ClusterName = "CassabdraStarbase";
	//private String ClusterName = "Test Cluster";
	private String Admin = "ross";
	
	public void LoadSettingsFromFile() throws IOException
	{
	//	BufferedReader input = new BufferedReader(new FileReader("settings.txt"));
	//	String line = "";
	//	int i = 0;		
	//	while ((line = input.readLine()) != null) {
	//		if (i == 0) Host = line;
	//		else if (i == 1) Port = line;
	//		else if (i == 2) ClusterName = line;
	//		else if (i == 3) Admin = line;
	//		i++;
	//	}
	//	input.close();
		
	}
}
