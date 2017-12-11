package namenode;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import datanode.Datanode;

public class NamenodeServer implements Namenode {
	private static int port = 7002;
	HashMap<String, Integer> map;
	String mapBackupFile = "map.ser";
	int numberOfDatadones = 0;

	public NamenodeServer() {
		try {
			this.map = (HashMap<String, Integer>) loadFromDisk(mapBackupFile);
		} catch (IOException e) {
			this.map = new HashMap<>();
		}
	}

	public static void main(String[] args) {

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
	public int getDatanode(String file) {
		try {
			System.out.println("Entrou no get");
			int id = map.get(file);
			System.out.println("Id retornado: " + String.valueOf(id));
			return id;
		} catch (NullPointerException e) {
			throw new NullPointerException();
		}
		
	}

	@Override
	public void addDatanode(int id) {
		numberOfDatadones += 1;
		map.put(null, id);
		saveToDisk(map, mapBackupFile);
	}

	@Override
	public void addFile(String file) {
		System.out.println("Número de valores: " + numberOfDatadones);
		map.put(file, new Integer(Math.abs(file.hashCode() % numberOfDatadones) + 1));
		System.out.println("Salvo no datanode " + String.valueOf(Math.abs(file.hashCode() % numberOfDatadones) + 1));
		saveToDisk(map, mapBackupFile);
	}

	public void saveToDisk(Object obj, String fileName) {
		try {
			FileOutputStream file = new FileOutputStream(fileName);
			ObjectOutputStream out = new ObjectOutputStream(file);
			out.writeObject(obj);
			out.close();
			file.close();
		} catch (IOException i) {
			i.printStackTrace();
		}
	}

	public Object loadFromDisk(String fileName) throws IOException {
		Object obj = null;
		try {
			FileInputStream fileIn = new FileInputStream(fileName);
			ObjectInputStream in = new ObjectInputStream(fileIn);
			obj = in.readObject();
			in.close();
			fileIn.close();
			return obj;
		} catch (ClassNotFoundException c) {
			System.out.println("Class not found");
			c.printStackTrace();
		}
		return obj;
	}


	public static int getPort() {
		return port;
	}

	public HashMap<String, Integer> getMap() {
		return map;
	}
	
}