package datanode;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Datanode extends Remote{
	void create(String fileName, String text) throws RemoteException;
	void read(String fileName) throws RemoteException, IOException;
	void write(String fileName, String text) throws RemoteException;
	void delete(String fileName) throws RemoteException;
}