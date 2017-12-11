package client;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import proxy.ProxyServer;
import proxy.Proxy;




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

	public void sendRequest(String file, int operation) throws InputMismatchException {
		
		String text = "";
		try {
			Registry proxyRegistry = LocateRegistry.getRegistry("localhost", ProxyServer.getPort());
			Proxy proxyStub = (Proxy) proxyRegistry.lookup("Proxy");
			
			switch (operation) {
			case 1: // Criar
				text = getText();
				proxyStub.create(file, text);
//				System.out.println("Ja deseja escrever algo no arquivo? Escreva 'S' ou 'N'");
//				String confirmacao = sc.next();
//				if(confirmacao.equals("S")) {
//					text = getText();
//					proxyStub.create(file, text);
//				}if(confirmacao.equals("N")) {
//					text = getText();
//					proxyStub.create(file, "");
//				}
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
			case 5:
				
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
			  text += line + "\n";
		}
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
