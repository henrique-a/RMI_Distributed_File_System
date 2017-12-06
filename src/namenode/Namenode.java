package namenode;

import java.rmi.Remote;
import java.util.List;

import datanode.Datanode;

public interface Namenode extends Remote{
	public List<Datanode> getDatanodes(String file);
	public void addDatanode(Datanode datanode);
	public void addFile(String file);
}
