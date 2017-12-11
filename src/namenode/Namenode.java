package namenode;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import datanode.Datanode;

public interface Namenode extends Remote{
	public List<Datanode> getDatanodes(String file) throws RemoteException, NullPointerException;
	public void addDatanode(Datanode datanode) throws RemoteException;
	public void addFile(String file) throws RemoteException;
	public List<Datanode> list() throws RemoteException;
}
