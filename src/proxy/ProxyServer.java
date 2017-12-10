package proxy;


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

	public static void main(String[] args) {

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

	public List<Datanode> getDatanodes(String file) throws NullPointerException {
		// Localiza o registry do namenode e cria um stub do namenode para encaminhar
		// a mensagem aos outros usuários
		List<Datanode> datanodeStubs = null;
		try {
			Registry namenodeRegistry = LocateRegistry.getRegistry("localhost", NamenodeServer.getPort());
			Namenode namenodeStub = (Namenode) namenodeRegistry.lookup("Namenode");

			// Perguntar ao namenode onde está o datanode desse arquivo
			try {
				datanodeStubs = namenodeStub.getDatanodes(file);
			} catch (NullPointerException e) {
				throw new NullPointerException();
			}
			

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
			Registry namenodeRegistry = LocateRegistry.getRegistry("localhost", NamenodeServer.getPort());
			Namenode namenodeStub = (Namenode) namenodeRegistry.lookup("Namenode");

			// Adicionar arquivo na tabela hash do namenode
			System.out.println("Solicitacao de Criacao do Arquivo: "+file+".txt");
			namenodeStub.addFile(file);
			
			try {
				List<Datanode> datanodeStubs = getDatanodes(file);
				for (Datanode datanodeStub : datanodeStubs) {
					datanodeStub.create(file, text);
					}
				System.out.println("Arquivo criado com sucesso!");
			} catch (NullPointerException e) {
				sendToClient("Arquivo inexistente!");
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
		try {
			// Perguntar ao namenode onde está o datanode desse arquivo
			System.out.println("Solicitacao de Leitura do Arquivo: "+file+".txt");
			List<Datanode> datanodeStubs = getDatanodes(file);			
			datanodeStubs.get(0).read(file);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			sendToClient("Arquivo inexistente!");
		}
	}

	// Método para pedir ao datanode para escrever no arquivo
	@Override
	public void write(String file, String text) {
		try {
			// Perguntar ao namenode onde está o datanode desse arquivo
			System.out.println("Solicitacao de Escrita do Arquivo: "+file+".txt");
			List<Datanode> datanodeStubs = getDatanodes(file);
			for (Datanode datanodeStub : datanodeStubs) {
				datanodeStub.write(file, text);
			}
			System.out.println("Escrita realizada com sucesso!");
		} catch (NullPointerException e) {
			sendToClient("Arquivo inexistente!");
			
		} catch (RemoteException e) {
			e.printStackTrace();
		}		
	}

	@Override
	public void delete(String file) {
		try {
			// Perguntar ao namenode onde está o datanode desse arquivo
			System.out.println("Solicitacao de Exclus�o do Arquivo: "+file+".txt");
			List<Datanode> datanodeStubs = getDatanodes(file);
			for (Datanode datanodeStub : datanodeStubs) {
				datanodeStub.delete(file);
			}
			System.out.println("Exclus�o realizada com sucesso!");
		} catch (NullPointerException e) {
			sendToClient("Arquivo inexistente!");
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
	}

	public static int getPort() {
		return port;
	}

	@Override
	public void sendToClient(String response) {
		Registry clientRegistry;
		try {
			clientRegistry = LocateRegistry.getRegistry("localhost", ClientServer.getPort());
			Client clientStub = (Client) clientRegistry.lookup("Client");
			clientStub.getResponse(response);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		}
	}
	
}
