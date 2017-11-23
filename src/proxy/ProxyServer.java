package proxy;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Hashtable;

import datanode.Datanode;
import datanode.DatanodeServer;
import namenode.NamenodeServer;

public class ProxyServer implements Proxy {
	private String file;
	private String text;

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
		Registry namenodeRegistry = null;
		Datanode datanodeStub = null;
		try {
			namenodeRegistry = LocateRegistry.getRegistry(NamenodeServer.getIP(), NamenodeServer.getPort());
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
		Registry namenodeRegistry = LocateRegistry.getRegistry(NamenodeServer.getIP(), NamenodeServer.getPort());
		NamenodeServer namenodeStub = (NamenodeServer) namenodeRegistry.lookup("Namenode");

		// Perguntar ao namenode onde está o datanode desse arquivo
		Hashtable<String, Datanode> hashtable = namenodeStub.getDatanodes();
		Datanode datanodeStub = hashtable.putAll(file.hashCode() % hashtable.size()); // Acho que isso não vai
																				   // dar certo porque o tamanho
																				   // da hashtable muda a medida
																				   // que novos elementos vão sendo
																				   // inseridos
		datanodeStub.create(file, text);
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
