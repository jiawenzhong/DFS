package LAB3;

import java.rmi.RemoteException;
import java.util.Set;

public class Context implements ContextInterface {

    Long n = 0L
            ;
    Set<Long> set;

    public void add(Page page){
        set.add(page.getGuid());
    }

    public void setWorkingPeer(Long page)
    {
        set.add(page);
    }
    public void completePeer(Long page, Long n) throws RemoteException
    {
        this.n += n;
        set.remove(page);
    }
    public Boolean isPhaseCompleted()
    {
        if (set.isEmpty())
            return true;
        return false;
    }

    @Override
    public void reduceContext(Long source, ReduceInterface reducer, Context context) throws RemoteException
    {
        // TODO
    }

    @Override
    public void mapContext(Long page, MapReduceInterface mapper, Context context) throws RemoteException
    {
        // TODO:
    }

}
