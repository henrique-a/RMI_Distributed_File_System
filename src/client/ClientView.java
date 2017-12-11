package client;

import java.util.InputMismatchException;
import java.util.Scanner;

public class ClientView implements Runnable {

	@Override
	public void run() {
		ClientServer clientServer = new ClientServer();
		int comando = 0;
		System.out.println("Bem vindo!");

			while(comando != 6) {
				System.out.println("Escolha a operaÁ„o abaixo:");
				System.out.println("1.Criar \n2.Ler \n3.Escrever \n4.Deletar \n5.Listar \n6.Sair");
				Scanner sc = new Scanner(System.in);
				try {
					comando = sc.nextInt();
					System.out.println("Escreva o nome do arquivo desejado:");
					String file = sc.next();
					clientServer.sendRequest(file, comando);
//					sc.close();
				} catch (InputMismatchException e) {
					System.err.println("Op√ß√£o inv√°lida!");
				}		
			}
		System.out.println("Sess„o Encerrada");
	}
}
