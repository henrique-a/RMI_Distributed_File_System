package proxy;


import java.io.File;
import java.io.FileFilter;
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
		try {
			// Localiza o registry do namenode e cria um stub do namenode para encaminhar
			// a mensagem aos outros usuários			
			Registry namenodeRegistry = LocateRegistry.getRegistry("localhost", NamenodeServer.getPort());
			Namenode namenodeStub = (Namenode) namenodeRegistry.lookup("Namenode");
			// Adicionar arquivo na tabela hash do namenode
			namenodeStub.addFile(file);
			List<Datanode> datanodes = getDatanodes(file);
			System.out.println("Solicitacao de Criacao do Arquivo: "+file+".txt");
			
			namenodeStub.addFile(file);
			for (Datanode datanodeStub : datanodes) {
				datanodeStub.create(file, text);
			}
			
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
			System.out.println("Solicitacao de Exclusao do Arquivo: "+file+".txt");
			List<Datanode> datanodeStubs = getDatanodes(file);
			for (Datanode datanodeStub : datanodeStubs) {
				datanodeStub.delete(file);
			}
			System.out.println("Exclusao realizada com sucesso!");
		} catch (NullPointerException e) {
			sendToClient("Arquivo inexistente!");
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
	}
	
	public void list() {
		
//		File folder = new File("C:\\Users\\Davi\\Documents\\Davi\\Distribuidos\\sd_projetofinal_374930\\datanode1");
//		File[] listOfFiles = folder.listFiles();
//
//		for (File file : listOfFiles) {
//		    if (file.isFile()) {
//		    	sendToClient(file.getName());
//		    }
//		}
		
		File f = new File("C:\\Users\\Davi\\Documents\\Davi\\Distribuidos\\sd_projetofinal_374930"); // current directory

		FileFilter directoryFilter = new FileFilter() {
			public boolean accept(File file) {
				return file.isDirectory();
			}
		};

		File[] files = f.listFiles(directoryFilter);
		for (File file : files) {
			if (file.isDirectory() && file.toString().startsWith("datanode")) {
				
				File f2 = new File("C:\\Users\\Davi\\Documents\\Davi\\Distribuidos\\sd_projetofinal_374930"+file.getName());
				File[] listOfFiles = f2.listFiles();
				
				for (File files2 : listOfFiles) {
					if (files2.isFile()) {
						sendToClient(files2.getName());
					}
				}
			}	
		}
		
		
		
		
		
		
		
//	FileFilter filter = new FileFilter() {
//		public boolean accept(File file) {
//			return file.getName().startsWith("datanode");
//		}
//		};
//		File dir = new File("C:\\Users\\Davi\\Documents\\Davi\\Distribuidos\\sd_projetofinal_374930");
//		File[] files = dir.listFiles(filter);
//		File[][] txts = new File[files.length][];
//		
//		for(int i = 0; i< files.length ; i++) {
//			File txt = new File("C:\\Users\\Davi\\Documents\\Davi\\Distribuidos\\sd_projetofinal_374930\\"+files[i].toString());
//			txts[i] = txt.listFiles();
//			System.out.println(txt.listFiles());
//		}
////		return txts;
//		System.out.println(txts);

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
	
	
}
