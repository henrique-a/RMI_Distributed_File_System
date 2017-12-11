package proxy;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

import client.Client;
import client.ClientServer;
import datanode.Datanode;
import namenode.Namenode;
import namenode.NamenodeServer;

public class ProxyServer implements Proxy {

	private static int port = 7001;
	public static Registry registry;

	public static void main(String[] args) {

		try {
			String IP = localIP();
			System.setProperty("java.rmi.server.hostname", IP);

			ProxyServer obj = new ProxyServer();
			Proxy stub = (Proxy) UnicastRemoteObject.exportObject(obj, port);

			// Fazendo o bind do stub no registrador
			registry = LocateRegistry.createRegistry(port);
			registry.bind("Proxy", stub);

			System.out.println("Servidor pronto!");

		} catch (Exception e) {
			System.err.println("Server exception: " + e.toString());
			e.printStackTrace();
		}

	}

	@Override
	public void create(String file, String text) {
		try {
			// Localiza o registry do namenode e cria um stub do namenode para encaminhar
			// a mensagem aos outros usuários
			Registry namenodeRegistry = LocateRegistry.getRegistry("localhost", NamenodeServer.getPort());
			Namenode namenodeStub = (Namenode) namenodeRegistry.lookup("Namenode");
			// Adicionar arquivo na tabela hash do namenode
			namenodeStub.addFile(file);
			int datanodeID = namenodeStub.getDatanode(file); // aqui eu pego o id e chamo um stub pra criar o arquivo
			System.out.println("Solicitacao de Criacao do Arquivo: " + file + ".txt");

			Registry datanodeRegistry = LocateRegistry.getRegistry(getIP(), 5000 + datanodeID);
			Datanode datanodeStub = (Datanode) datanodeRegistry.lookup("Datanode" + String.valueOf(datanodeID));
			datanodeStub.create(file, text);

		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e) {
			sendToClient("Servico Indisponivel");
		}

	}

	// Método para pedir ao datanode para ler o arquivo
	@Override
	public void read(String file) {
		try {
			// Perguntar ao namenode onde está o datanode desse arquivo
			System.out.println("Solicitacao de Leitura do Arquivo: " + file + ".txt");
			int datanodeID = getDatanode(file);
			System.out.println("Proxy encontrou arquivo " + file + "no datanode " + String.valueOf(datanodeID));
			Registry datanodeRegistry = LocateRegistry.getRegistry("localhost", 5000 + datanodeID);
			Datanode datanodeStub = (Datanode) datanodeRegistry.lookup("Datanode" + Integer.toString(datanodeID));
			datanodeStub.read(file);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			sendToClient("Arquivo inexistente!");
		} catch (NotBoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			sendToClient("Arquivo inexistente!");
		}
	}

	// Método para pedir ao datanode para escrever no arquivo
	@Override
	public void write(String file, String text) {
		try {
			// Perguntar ao namenode onde está o datanode desse arquivo
			System.out.println("Solicitacao de Escrita do Arquivo: " + file + ".txt");
			int datanodeID = getDatanode(file);
			Registry datanodeRegistry = LocateRegistry.getRegistry("localhost", 5000 + datanodeID);
			Datanode datanodeStub = (Datanode) datanodeRegistry.lookup("Datanode" + Integer.toString(datanodeID));
			datanodeStub.write(file, text);
			System.out.println("Escrita realizada com sucesso!");
		} catch (NullPointerException e) {
			sendToClient("Arquivo inexistente!");

		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void delete(String file) {
		try {
			// Perguntar ao namenode onde está o datanode desse arquivo
			System.out.println("Solicitacao de Exclusao do Arquivo: " + file + ".txt");
			int datanodeID = getDatanode(file);
			Registry datanodeRegistry = LocateRegistry.getRegistry("localhost", 5000 + datanodeID);
			Datanode datanodeStub = (Datanode) datanodeRegistry.lookup("Datanode" + Integer.toString(datanodeID));
			datanodeStub.delete(file);
			System.out.println("Exclusao realizada com sucesso!");
		} catch (NullPointerException e) {
			sendToClient("Arquivo inexistente!");
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		}

	}

	public static int getPort() {
		return port;
	}

	@Override
	public void sendToClient(String response) {
		try {
			Registry clientRegistry = LocateRegistry.getRegistry("localhost", ClientServer.getPort());
			Client clientStub = (Client) clientRegistry.lookup("Client");
			clientStub.getResponse(response);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		}
	}

	public int getDatanode(String file) throws NullPointerException {
		// Localiza o registry do namenode e cria um stub do namenode para encaminhar
		// a mensagem aos outros usuários
		int datanode = 0;
		try {
			Registry namenodeRegistry = LocateRegistry.getRegistry("localhost", NamenodeServer.getPort());
			Namenode namenodeStub = (Namenode) namenodeRegistry.lookup("Namenode");

			// Perguntar ao namenode onde está o datanode desse arquivo
			datanode = namenodeStub.getDatanode(file); // aqui eu pego só o id

		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			sendToClient("Serviço indisponível");
		}
		return datanode;

	}

	public static String localIP() {
		try (final DatagramSocket socket = new DatagramSocket()) {
			socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
			return socket.getLocalAddress().getHostAddress();
		} catch (SocketException e) {
			e.printStackTrace();
			return "";
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return "";
		}

	}

	public String getIP() {
		try (final DatagramSocket socket = new DatagramSocket()) {
			socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
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