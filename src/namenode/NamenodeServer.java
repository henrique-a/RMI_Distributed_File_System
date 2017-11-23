package namenode;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import datanode.Datanode;
import proxy.Proxy;
import proxy.ProxyServer;

//O namenode pode ser um singleton

public class NamenodeServer implements Namenode {
	private  static String IP;
	private static int port;
	HashMap<String, Integer> map = new HashMap<>();	
	List<Datanode> datanodes = new ArrayList<>();
	
	public static void main(String[] args) {
		int port = 7001;

		try {

			NamenodeServer obj = new NamenodeServer();
			Namenode stub = (Namenode) UnicastRemoteObject.exportObject(obj, port);

			// Fazendo o bind do stub no registrador
			Registry registry = LocateRegistry.createRegistry(port);
			registry.bind("Namenode", stub);

			System.out.println("Namenode pronto!");

		} catch (Exception e) {
			System.err.println("Server exception: " + e.toString());
			e.printStackTrace();
		}

	}

	
	@Override
	public Datanode getDatanode(String file) {
		int id = map.get(file);
		return datanodes.get(id);
	}

	@Override
	public void addDatanode(Datanode datanode) {
		datanodes.add(datanode);
		map.put(null, datanodes.size());
	}


	public static String getIP() {
		return IP;
	}


	public static int getPort() {
		return port;
	}


	public HashMap<String, Integer> getMap() {
		return map;
	}

}
