package client;

public class Client {

	public static void main(String[] args) {
		
		
		// Thread para a interface do cliente que expõe os servicos oferecidos pelo
		// sistema de arquivos distribuído
		ClientView clientView = new ClientView();
		Thread t = new Thread(clientView);
		t.start();

	}

	public void sendRequest(String file, int operation, String proxyIP) {
		
	}
}