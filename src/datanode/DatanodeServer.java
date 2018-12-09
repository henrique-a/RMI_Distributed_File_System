package datanode;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

import namenode.Namenode;
import namenode.NamenodeServer;
import proxy.Proxy;
import proxy.ProxyServer;

public class DatanodeServer implements Datanode, Serializable {
	
	private static final long serialVersionUID = 797830935937455551L;
	private int port;
	private int id;

	public DatanodeServer(int id) {
		this.id = id;
		this.port = 5000 + id;
		addToNamenode();
	}

	public static void main(String[] args) {

		Scanner sc = new Scanner(System.in);
		System.out.print("Digite o id do datanode: ");
		int id = sc.nextInt();
		sc.close();
	
		createDirectory(id); // Método para criar um diretório do datanode
		
		try {
			
			String IP = localIP();
			System.setProperty("java.rmi.server.hostname", IP);

			DatanodeServer obj = new DatanodeServer(id);
			Datanode stub = (Datanode) UnicastRemoteObject.exportObject(obj, obj.getPort());
			
			// Fazendo o bind do stub no registrador
			Registry registry = LocateRegistry.createRegistry(obj.getPort());
			registry.rebind("Datanode" + String.valueOf(id), stub);

			System.out.println("Servidor pronto!");

		} catch (Exception e) {
			System.err.println("Server exception: " + e.toString());
			e.printStackTrace();
		}
	}

	private static void createDirectory(int id) {
		File file = new File("datanode" + String.valueOf(id));
		file.mkdir();
	}

	public void addToNamenode() {
		
		try {
			Registry namenodeRegistry = LocateRegistry.getRegistry("localhost", NamenodeServer.getPort());
			Namenode namenodeStub = (Namenode) namenodeRegistry.lookup("Namenode");
			namenodeStub.addDatanode(this.id);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void create(String fileName, String text) {
		Path file = Paths.get("datanode" + String.valueOf(this.id) + "/" + fileName); // Converte uma String em um Path
		Charset charset = Charset.forName("UTF-8");
		try (BufferedWriter writer = Files.newBufferedWriter(file, charset, StandardOpenOption.CREATE)) {
			writer.write(text, 0, text.length()); 
			Registry proxyRegistry = LocateRegistry.getRegistry("localhost", ProxyServer.getPort());
			Proxy proxyStub = (Proxy) proxyRegistry.lookup("Proxy");
			proxyStub.sendToClient("Arquivo " + fileName + ".txt criado!");
		} catch (IOException e) {
			System.err.format("IOException: %s%n", e);
		} catch (NotBoundException e) {
				e.printStackTrace();
			}
	}

	@Override
	public void read(String fileName) throws IOException {
		Path file = Paths.get("datanode" + String.valueOf(this.id) + "/" + fileName); // Converte uma String em um Path
		Charset charset = Charset.forName("UTF-8");
		try (BufferedReader reader = Files.newBufferedReader(file, charset)) {
			String text = "";
			String line = "";
			while ((line = reader.readLine()) != null) {
				text += line;
			}
			Registry proxyRegistry = LocateRegistry.getRegistry("localhost", ProxyServer.getPort());
			Proxy proxyStub = (Proxy) proxyRegistry.lookup("Proxy");
			proxyStub.sendToClient(text);
		} catch (IOException x) {
			throw new IOException()	;
		} catch (NotBoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void write(String fileName, String text) {
		Path file = Paths.get("datanode" + String.valueOf(this.id) + "/" + fileName); // Converte uma String em um Path
		Charset charset = Charset.forName("UTF-8");
		try (BufferedWriter writer = Files.newBufferedWriter(file, charset, StandardOpenOption.APPEND)) {
			writer.write(text, 0, text.length());
			Registry proxyRegistry = LocateRegistry.getRegistry("localhost", ProxyServer.getPort());
			Proxy proxyStub = (Proxy) proxyRegistry.lookup("Proxy");
			proxyStub.sendToClient("Arquivo " + fileName + ".txt editado!");
		} catch (IOException e) {
			System.err.format("IOException: %s%n", e);
		} catch (NotBoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void delete(String fileName) {
		Path file = Paths.get("datanode" + String.valueOf(this.id) + "/" + fileName); // Converte uma String em um Path

		try {
			Files.delete(file);
			Registry proxyRegistry = LocateRegistry.getRegistry("localhost", ProxyServer.getPort());
			Proxy proxyStub = (Proxy) proxyRegistry.lookup("Proxy");
			proxyStub.sendToClient("Arquivo " + fileName + ".txt deletado!");
		} catch (NoSuchFileException x) {
			System.err.format("%s: no such" + " file or directory%n", file);
		} catch (DirectoryNotEmptyException x) {
			System.err.format("%s not empty%n", file);
		} catch (IOException e) {
			// File permission problems are caught here.
			System.err.println(e);
		} catch (NotBoundException e) {
			e.printStackTrace();
		}
	}

	public int getPort() {
		return port;
	}

	public int getId() {
		return id;
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

}