package namenode;

import java.rmi.Remote;

import datanode.Datanode;

public interface Namenode extends Remote{
	Datanode getDatanode(String file);
	String addFile(String file, String datanode);
}
