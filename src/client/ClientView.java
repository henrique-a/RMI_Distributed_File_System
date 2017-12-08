package client;

import java.util.Scanner;

public class ClientView implements Runnable {

	@Override
	public void run() {
		
		Scanner sc = new Scanner(System.in);
		boolean aux = true;
		ClientServer clientServer = new ClientServer();
		
		System.out.print("Bem vindo!");
		System.out.print("Qual arquivo você deseja operar?");
		String file = sc.nextLine();
		System.out.print("Esolha o numero da operação desejada?");
		System.out.print("1.Criar \n2.Ler \n3.Escrever \n4.Deletar \n5.Listar");
		int comando = sc.nextInt();
		clientServer.sendRequest(file, comando);		
	}

}
