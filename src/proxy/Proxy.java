package proxy;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Proxy extends Remote{
	void create(String file, String text) throws RemoteException; 
	void read(String file) throws RemoteException; 
	void write(String file, String text) throws RemoteException;
	void delete(String file) throws RemoteException;
	void sendToClient(String resjavaponse) throws RemoteException;
	void list() throws RemoteException;
}
