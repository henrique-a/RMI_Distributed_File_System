package namenode;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Hashtable;

import datanode.Datanode;
import proxy.Proxy;
import proxy.ProxyServer;

//O namenode pode ser um singleton

public class NamenodeServer implements Namenode {
	private  static String IP;
	private static int port; 
	private Hashtable<String, Datanode> datanodes = 
			new Hashtable<>();
	
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
		return null;
	}

	@Override
	public String addFile(String file, String datanode) {
		return null;
	}


	public static String getIP() {
		return IP;
	}


	public static int getPort() {
		return port;
	}


	public Hashtable<String, Datanode> getDatanodes() {
		return datanodes;
	}

}
