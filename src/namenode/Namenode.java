package namenode;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import datanode.Datanode;

public interface Namenode extends Remote{
	public int getDatanode(String file) throws RemoteException, NullPointerException;
	public void addDatanode(int id) throws RemoteException;
	public void addFile(String file) throws RemoteException;
}