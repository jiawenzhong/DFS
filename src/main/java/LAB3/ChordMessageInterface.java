package LAB3;

import java.rmi.*;
import java.io.*;

public interface ChordMessageInterface extends Remote
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

    void setWorkingPeer(Long page);
    void completePeer(Long page, Long n) throws RemoteException;
    Boolean isPhaseCompleted();
    void reduceContext(Long source, MapReduceInterface reducer, ChordMessageInterface context) throws RemoteException;
    void mapContext(Long page, MapReduceInterface mapper, ChordMessageInterface context) throws RemoteException;

    void emitMap(Long key, String value) throws RemoteException;
    void emitReduce(Long page, String value) throws RemoteException;

    void saveReduceFile(Long source) throws IOException;
}

