package namenode;

import java.rmi.Remote;

import datanode.Datanode;

public interface Namenode extends Remote{
	public Datanode getDatanode(String file);
	public void addDatanode(Datanode datanode);
	public void addFile(String file);
}
