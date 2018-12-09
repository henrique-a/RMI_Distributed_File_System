package client;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import proxy.ProxyServer;
import proxy.Proxy;

public class ClientServer implements Client {
	
	private static int port = 7000;
	public static Registry registry;
	
	public static void main(String[] args) {
		// Thread para a interface do cliente que expõe os servicos oferecidos pelo
		// sistema de arquivos distribuído
		ClientView clientView = new ClientView();
		Thread t = new Thread(clientView);
		
		try {
			System.setProperty("java.rmi.server.hostname", "localhost");

			ClientServer obj = new ClientServer();
			Client stub = (Client) UnicastRemoteObject.exportObject(obj, port);

			// Fazendo o bind do stub no registrador
			registry = LocateRegistry.createRegistry(port);
			registry.bind("Client", stub);

			System.out.println("Cliente pronto!");

		} catch (Exception e) {
			System.err.println("Client exception: " + e.toString());
			e.printStackTrace();
		}

		t.start();		
	}

	public void sendRequest(String file, int operation) throws InputMismatchException {
		
		String text = "";
		try {
			Registry proxyRegistry = LocateRegistry.getRegistry("localhost", ProxyServer.getPort());
			Proxy proxyStub = (Proxy) proxyRegistry.lookup("Proxy");
			
			switch (operation) {
			case 1: // Criar
				text = getText();
				proxyStub.create(file, text);
				break;
			case 2: // Ler
				proxyStub.read(file);
				break;
			case 3:	// Escrever			
				text = getText(); 
				proxyStub.write(file, text);
				break;
			case 4: // Deletar
				proxyStub.delete(file);
				break;
			case 5: // Listar
				proxyStub.list();
				break;
			default:
				throw new InputMismatchException();
				
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		}
	}
	
	public String getText() {
		Scanner sc = new Scanner(System.in);	
		System.out.println("Escreva o seu texto a baixo. Apos escrever tudo, digite '\\e' para enviar o texto.");
		String line = "";
		String text = "";
		while(!line.equals("\\e")){
			  line = sc.nextLine();
			  if (!line.equals("\\e")) {
				  text += line + "\n";
			  }
		}
		sc.close();
		return text;
	}

	@Override
	public void getResponse(String response) {
		System.out.println(response);
	}

	public static int getPort() {
		return port;
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