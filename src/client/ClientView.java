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
				System.out.println("Escolha a operação abaixo:");
				System.out.println("1.Criar \n2.Ler \n3.Escrever \n4.Deletar \n5.Listar \n6.Sair");
				Scanner sc = new Scanner(System.in);
				try {
					comando = sc.nextInt();
					System.out.println("Escreva o nome do arquivo desejado:");
					String file = sc.next();
					clientServer.sendRequest(file, comando);
				} catch (InputMismatchException e) {
					System.err.println("Opção inválida!");
				}		
			}
		System.out.println("Sessão Encerrada");
	}
}
