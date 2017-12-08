package client;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import proxy.ProxyServer;;




public class ClientServer implements Client {
	
	public static int port = 7000;
	
	public static void main(String[] args) {
		
		
		// Thread para a interface do cliente que expõe os servicos oferecidos pelo
		// sistema de arquivos distribuído
		ClientView clientView = new ClientView();
		Thread t = new Thread(clientView);
		
		try {

			ClientServer obj = new ClientServer();
			Client stub = (Client) UnicastRemoteObject.exportObject(obj, port);

			// Fazendo o bind do stub no registrador
			Registry registry = LocateRegistry.createRegistry(port);
			registry.bind("Client", stub);

			System.out.println("Cliente pronto!");

		} catch (Exception e) {
			System.err.println("Client exception: " + e.toString());
			e.printStackTrace();
		}

		t.start();		
	}

	public void sendRequest(String file, int operation) {
		
		String text = "";
		try {
			
			Registry proxyRegistry = LocateRegistry.getRegistry("localhost", ProxyServer.getPort());
			ProxyServer proxyStub = (ProxyServer) proxyRegistry.lookup("Proxy");
			
			switch (operation) {
			case 1:
				text = getText();
				proxyStub.create(file, text);
			case 2:
				proxyStub.read(file);
			case 3:						
				text = getText();
				proxyStub.create(file, text);
			case 4:
				proxyStub.delete(file);
			case 5:
			
			}
			

		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		}
	}
	
	public String getText() {
		Scanner sc = new Scanner(System.in);	
		System.out.print("Escreva o seu texto a baixo. Após escrever tudo, digite '\\e' para enviar o texto.");
		String line = "";
		String text = "";
		while(!line.equals("\\e")){
			  line = sc.nextLine();
			  text += line + "\n";
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
	
}
