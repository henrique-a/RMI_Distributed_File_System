package client;

import java.util.Scanner;

public class ClientView implements Runnable {

	@Override
	public void run() {
		
		Scanner sc = new Scanner(System.in);
		boolean aux = true;
		Client request = new Client();
		
		System.out.print("Bem vindo!");
		System.out.print("Qual arquivo você deseja operar?");
		String file = sc.nextLine();
		System.out.print("Qual operação você deseja realizar?");
		System.out.print("1.Criar \n2.Ler \n3.Escrever \n4.Deletar \n5.Listar");
		String comando = sc.nextLine();
		request.sendRequest(file, comando);
		
		while(aux) {
			//System.out.print("1.Criar \n2.Ler \n3.Escrever \n4.Deletar \n5.Listar");
			//String comando = sc.nextLine();
			if( (comando == "Criar") || (comando == "criar") ) {
				request.sendRequest(file, comando);
				aux = false;
			}else if( (comando == "Ler") || (comando == "ler") ) {
				
				aux = false;
			}else if( (comando == "Escrever") || (comando == "escrever") ) {
				String line = "";
				while(!line.equals("exit")){
					  line = sc.nextLine();
					  line += line + "\n";
					}
				aux = false;
			}else if( (comando == "Deletar") || (comando == "deletar") ){
				
				aux = false;
			}
			else if( (comando == "Listar") || (comando == "listar") ) {
				
				aux = false;
			}else {
				System.out.print("Opção não encontrada. Digite novamente, por favor.");
			}
		}
		
	
		
		
	}

}
