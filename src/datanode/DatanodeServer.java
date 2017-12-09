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

public class DatanodeServer implements Datanode, Serializable {
	
	private static final long serialVersionUID = 797830935937455551L;
	private int port;
	private int id;

	public DatanodeServer(int id, int port) {
		this.port = port;
		this.id = id;
		addToNamenode();
	}

	public static void main(String[] args) {

		Scanner sc = new Scanner(System.in);
		System.out.print("Digite o id do datanode: ");
		int id = sc.nextInt();
		System.out.print("Digite o número da porta: ");
		int port = sc.nextInt();
		
		createDirectory(id); // Método para criar um diretório do datanode
	
		try {

			DatanodeServer obj = new DatanodeServer(id, port);
			Datanode stub = (Datanode) UnicastRemoteObject.exportObject(obj, obj.getPort());

			// Fazendo o bind do stub no registrador
			Registry registry = LocateRegistry.createRegistry(obj.getPort());
			registry.bind("Datanode" + String.valueOf(id), stub);

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
			namenodeStub.addDatanode(this);
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
			// Tem que mandar mensegem pro proxy e do proxy para o cliente dizendo que o arquivo foi criado
		} catch (IOException x) {
			System.err.format("IOException: %s%n", x);
		}
	}

	@Override
	public void read(String fileName) {
		Path file = Paths.get("datanode" + String.valueOf(this.id) + "/" + fileName); // Converte uma String em um Path
		Charset charset = Charset.forName("UTF-8");
		try (BufferedReader reader = Files.newBufferedReader(file, charset)) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				System.out.println(line); // Tem que mandar isso pro proxy e do proxy para o cliente
			}
		} catch (IOException x) {
			System.err.format("IOException: %s%n", x);
		}
	}

	@Override
	public void write(String fileName, String text) {
		Path file = Paths.get("datanode" + String.valueOf(this.id) + "/" + fileName); // Converte uma String em um Path
		Charset charset = Charset.forName("UTF-8");
		try (BufferedWriter writer = Files.newBufferedWriter(file, charset, StandardOpenOption.APPEND)) {
			writer.write(text, 0, text.length());
			// Tem que mandar mensegem pro proxy e do proxy para o cliente dizendo que o arquivo foi atualizado
		} catch (IOException x) {
			System.err.format("IOException: %s%n", x);
		}
	}

	@Override
	public void delete(String fileName) {
		Path file = Paths.get("datanode" + String.valueOf(this.id) + "/" + fileName); // Converte uma String em um Path

		try {
			Files.delete(file);
			// Tem que mandar mensegem pro proxy e do proxy para o cliente dizendo que o arquivo foi deletado
		} catch (NoSuchFileException x) {
			System.err.format("%s: no such" + " file or directory%n", file);
		} catch (DirectoryNotEmptyException x) {
			System.err.format("%s not empty%n", file);
		} catch (IOException x) {
			// File permission problems are caught here.
			System.err.println(x);
		}
	}


	public int getPort() {
		return port;
	}

	public int getId() {
		return id;
	}
}
