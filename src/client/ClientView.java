package client;

import java.util.InputMismatchException;
import java.util.Scanner;

public class ClientView implements Runnable {

	@Override
	public void run() {
		ClientServer clientServer = new ClientServer();
		int comando = 0;
		System.out.println("Bem vindo!");

			while(true) {
				System.out.println("Escolha a operação abaixo:");
				System.out.println("1.Criar \n2.Ler \n3.Escrever \n4.Deletar \n5.Listar \n6.Sair");
				Scanner sc = new Scanner(System.in);
				try {
					comando = sc.nextInt();
					if (comando != 5) {
						System.out.println("Escreva o nome do arquivo desejado:");
						String file = sc.next();
						clientServer.sendRequest(file, comando);
					} else if (comando == 6) {
						sc.close();
						break;
					} else {
						clientServer.sendRequest("", comando);
					}
				} catch (InputMismatchException e) {
					System.err.println("Opção inválida!");
				}		
			}
		System.out.println("Sessão Encerrada");
	}
}