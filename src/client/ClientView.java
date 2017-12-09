package client;

import java.util.Scanner;

public class ClientView implements Runnable {

	@Override
	public void run() {
		ClientServer clientServer = new ClientServer();
		
		System.out.println("Bem vindo!");
		System.out.println("Esolha o numero da operacao desejada?");
		System.out.println("1.Criar \n2.Ler \n3.Escrever \n4.Deletar \n5.Listar");
		
		while(true) {
			Scanner sc = new Scanner(System.in);

			int comando = sc.nextInt();
			System.out.println("Escreva o nome do arquivo desejado:");
			String file = sc.next();
			clientServer.sendRequest(file, comando);		
		}
		
		
	}

}
