package proxy;

import java.rmi.Remote;

public interface Proxy extends Remote{
	void create(String file, String text); 
	void read(String file); 
	void write(String file, String text);
	void delete(String file);
	void sendToClient(String response);
}
