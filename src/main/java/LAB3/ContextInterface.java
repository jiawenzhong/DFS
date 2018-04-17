package LAB3;

import java.rmi.RemoteException;

public interface ContextInterface {
    void setWorkingPeer(Long page);
    void completePeer(Long page, Long n) throws RemoteException;
    Boolean isPhaseCompleted();
    void reduceContext(Long source, ReduceInterface reducer, Context context) throws RemoteException;
    void mapContext(Long page, MapReduceInterface mapper, Context context) throws RemoteException;
}
