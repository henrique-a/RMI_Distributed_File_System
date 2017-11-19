package datanode;

import java.rmi.Remote;

public interface Datanode extends Remote{
	void create(String fileName, String text);
	void read(String fileName);
	void write(String fileName, String text);
	void delete(String fileName);
}
