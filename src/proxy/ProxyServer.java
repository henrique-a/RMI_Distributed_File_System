package proxy;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Scanner;

import datanode.Datanode;
import datanode.DatanodeServer;
import namenode.NamenodeServer;

public class ProxyServer implements Proxy {

	private static String IP;
	private static int port = 7001;

	public static void main(String[] args) {
		IP = localIP();

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

	public List<Datanode> getDatanodes(String file) {
		// Localiza o registry do namenode e cria um stub do namenode para encaminhar
		// a mensagem aos outros usuários
		List<Datanode> datanodeStubs = null;
		try {
			Registry namenodeRegistry = LocateRegistry.getRegistry(NamenodeServer.getIP(), NamenodeServer.getPort());
			NamenodeServer namenodeStub = (NamenodeServer) namenodeRegistry.lookup("Namenode");

			// Perguntar ao namenode onde está o datanode desse arquivo
			datanodeStubs = namenodeStub.getDatanodes(file);

		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		}
		return datanodeStubs;

	}

	@Override
	public void create(String file, String text) {

		// Localiza o registry do namenode e cria um stub do namenode para encaminhar
		// a mensagem aos outros usuários
		try {
			Registry namenodeRegistry = LocateRegistry.getRegistry(NamenodeServer.getIP(), NamenodeServer.getPort());
			NamenodeServer namenodeStub = (NamenodeServer) namenodeRegistry.lookup("Namenode");

			// Adicionar arquivo na tabela hash do namenode
			namenodeStub.addFile(file);

			List<Datanode> datanodeStubs = getDatanodes(file);
			for (Datanode datanodeStub : datanodeStubs) {
				datanodeStub.create(file, text);
			}

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
		List<Datanode> datanodeStubs = getDatanodes(file);
		datanodeStubs.get(0).read(file);
	}

	// Método para pedir ao datanode para escrever no arquivo
	@Override
	public void write(String file, String text) {
		// Perguntar ao namenode onde está o datanode desse arquivo
		List<Datanode> datanodeStubs = getDatanodes(file);
		for (Datanode datanodeStub : datanodeStubs) {
			datanodeStub.write(file, text);
		}
	}

	@Override
	public void delete(String file) {
		// Perguntar ao namenode onde está o datanode desse arquivo
		List<Datanode> datanodeStubs = getDatanodes(file);
		for (Datanode datanodeStub : datanodeStubs) {
			datanodeStub.delete(file);
		}
	}

	// Método para pegar o ip da máquina
	public static String localIP() {
		try (final DatagramSocket socket = new DatagramSocket()) {
			socket.connect(InetAddress.getByName("8.8.8.8"), 10000);
			return socket.getLocalAddress().getHostAddress();
		} catch (SocketException e) {
			e.printStackTrace();
			return "";
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return "";
		}
	}

}
