package datanode;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

public class DatanodeServer implements Datanode {
	private String IP;
	private String name;
	private int port;
	private int id;

	public DatanodeServer(String IP, String name, int port, int id) {
		this.IP = IP;
		this.name = name;
		this.port = port;
		this.id = id;
	}

	public static void main(String[] args) {

		Scanner sc = new Scanner(System.in);
		System.out.print("Digite o IP da máquina: ");
		String IP = sc.nextLine();
		System.out.print("Digite o nome do datanode: ");
		String name = sc.nextLine();
		System.out.print("Digite o número da porta: ");
		int port = sc.nextInt();
		System.out.print("Digite o id: ");
		int id = sc.nextInt();

		try {

			DatanodeServer obj = new DatanodeServer(IP, name, port, id);
			Datanode stub = (Datanode) UnicastRemoteObject.exportObject(obj, obj.getPort());

			// Fazendo o bind do stub no registrador
			Registry registry = LocateRegistry.createRegistry(obj.getPort());
			registry.bind("Datanode", stub);

			System.out.println("Servidor pronto!");

		} catch (Exception e) {
			System.err.println("Server exception: " + e.toString());
			e.printStackTrace();
		}
	}

	@Override
	public void create(String fileName, String text) {
		Path file = Paths.get(fileName); // Converte uma String em um Path
		Charset charset = Charset.forName("UTF-8");
		try (BufferedWriter writer = Files.newBufferedWriter(file, charset, StandardOpenOption.CREATE)) {
			writer.write(text, 0, text.length());
		} catch (IOException x) {
			System.err.format("IOException: %s%n", x);
		}
	}

	@Override
	public void read(String fileName) {
		Path file = Paths.get(fileName); // Converte uma String em um Path
		Charset charset = Charset.forName("UTF-8");
		try (BufferedReader reader = Files.newBufferedReader(file, charset)) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
			}
		} catch (IOException x) {
			System.err.format("IOException: %s%n", x);
		}
	}

	@Override
	public void write(String fileName, String text) {
		Path file = Paths.get(fileName); // Converte uma String em um Path
		Charset charset = Charset.forName("UTF-8");
		try (BufferedWriter writer = Files.newBufferedWriter(file, charset, StandardOpenOption.APPEND)) {
			writer.write(text, 0, text.length());
		} catch (IOException x) {
			System.err.format("IOException: %s%n", x);
		}
	}

	@Override
	public void delete(String fileName) {
		Path file = Paths.get(fileName); // Converte uma String em um Path

		try {
			Files.delete(file);
		} catch (NoSuchFileException x) {
			System.err.format("%s: no such" + " file or directory%n", file);
		} catch (DirectoryNotEmptyException x) {
			System.err.format("%s not empty%n", file);
		} catch (IOException x) {
			// File permission problems are caught here.
			System.err.println(x);
		}
	}

	public String getIP() {
		return IP;
	}

	public String getName() {
		return name;
	}

	public int getPort() {
		return port;
	}

	public int getId() {
		return id;
	}
}
