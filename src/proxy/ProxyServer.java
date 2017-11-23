package proxy;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Hashtable;

import datanode.Datanode;
import datanode.DatanodeServer;
import namenode.NamenodeServer;

public class ProxyServer implements Proxy {
	
	public static void main(String[] args) {
		int port = 7001;
		String IP;

		try {

			ProxyServer obj = new ProxyServer();
			Proxy stub = (Proxy) UnicastRemoteObject.exportObject(obj, port);

			// Fazendo o bind do stub no registrador
			Registry registry = LocateRegistry.createRegistry(port);
			registry.bind("Proxy", stub);

			System.out.println("Servidor pronto!");

		} catch (Exception e) {
			System.err.println("Server exception: " + e.toString());
			e.printStackTrace();
		}

	}

	public Datanode getDatanode(String file) {
		// Localiza o registry do namenode e cria um stub do namenode para encaminhar
		// a mensagem aos outros usuários
		Datanode datanodeStub = null;
		try {
			Registry namenodeRegistry = LocateRegistry.getRegistry(NamenodeServer.getIP(), NamenodeServer.getPort());
			NamenodeServer namenodeStub = (NamenodeServer) namenodeRegistry.lookup("Namenode");

			// Perguntar ao namenode onde está o datanode desse arquivo
			datanodeStub = namenodeStub.getDatanode(file);

		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		}
		return datanodeStub;

	}

	@Override
	public void create(String file, String text) {
		// Localiza o registry do namenode e cria um stub do namenode para encaminhar
		// a mensagem aos outros usuários
		try {
			Registry namenodeRegistry = LocateRegistry.getRegistry(NamenodeServer.getIP(), NamenodeServer.getPort());
			NamenodeServer namenodeStub = (NamenodeServer) namenodeRegistry.lookup("Namenode");
			// Perguntar ao namenode em qual datanode colocar esse arquivo
			HashMap<String, Integer> map = namenodeStub.getMap();
			map.put(file, new Integer(Math.abs(file.hashCode() % map.values().size())));
			Datanode datanodeStub = getDatanode(file);
			datanodeStub.create(file, text);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}

	// Método para pedir ao datanode para ler o arquivo
	@Override
	public void read(String file) {
		// Perguntar ao namenode onde está o datanode desse arquivo
		Datanode datanodeStub = getDatanode(file);
		datanodeStub.read(file);
	}

	// Método para pedir ao datanode para escrever no arquivo
	@Override
	public void write(String file, String text) {
		// Perguntar ao namenode onde está o datanode desse arquivo
		Datanode datanodeStub = getDatanode(file);
		datanodeStub.write(file, text);
	}

	@Override
	public void delete(String file) {
		// Perguntar ao namenode onde está o datanode desse arquivo
		Datanode datanodeStub = getDatanode(file);
		datanodeStub.delete(file);
	}

}
