package LAB3;

import java.rmi.*;
import java.io.*;

public interface ChordMessageInterface extends Remote, Serializable
{
    ChordMessageInterface getPredecessor()  throws RemoteException;
    ChordMessageInterface locateSuccessor(long key) throws RemoteException;
    ChordMessageInterface closestPrecedingNode(long key) throws RemoteException;
    void joinRing(String Ip, int port)  throws RemoteException;
    void notify(ChordMessageInterface j) throws RemoteException;
    boolean isAlive() throws RemoteException;
    long getId() throws RemoteException;
    
    
    void put(long guidObject, InputStream inputStream) throws IOException, RemoteException;
    InputStream get(long guidObject) throws IOException, RemoteException;
    void delete(long guidObject) throws IOException, RemoteException;

    void setWorkingPeer(Long page) throws RemoteException, IOException;
    void completePeer(Long page, Long n) throws RemoteException;
    Boolean isPhaseCompleted() throws RemoteException, IOException;
    void reduceContext(Long source, MapReduceInterface reducer, ChordMessageInterface context) throws RemoteException, IOException;
    void mapContext(Long source, Long page, MapReduceInterface mapper, ChordMessageInterface context) throws IOException, RemoteException;

    void emitMap(Long key, String value) throws RemoteException;
    void emitReduce(Long page, String value) throws RemoteException;

    void saveReduceFile(Long source) throws RemoteException, IOException;
}

