package LAB3;

import java.math.BigInteger;
import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.util.*;
import java.io.*;


public class Chord extends UnicastRemoteObject implements ChordMessageInterface, Serializable {
    public static final int M = 2;
    Long n = 0L;
    Set<Long> set = new HashSet<Long>();

    Registry registry;    // rmi registry for lookup the remote objects.
    ChordMessageInterface successor;
    ChordMessageInterface predecessor;
    ChordMessageInterface[] finger;
    int nextFinger;
    long guid;        // GUID (i)
    TreeMap<Long, List<String>> BMap = new TreeMap<Long, List<String>>();
    TreeMap <Long, String>  BReduce = new TreeMap<Long, String>();


    public Boolean isKeyInSemiCloseInterval(long key, long key1, long key2) {
        if (key1 < key2)
            return (key > key1 && key <= key2);
        else
            return (key > key1 || key <= key2);
    }

    public Boolean isKeyInOpenInterval(long key, long key1, long key2) {
        if (key1 < key2)
            return (key > key1 && key < key2);
        else
            return (key > key1 || key < key2);
    }


    public void put(long guidObject, InputStream stream) throws RemoteException {
        try {
            String fileName = "./" + guid + "/repository/" + guidObject;
            FileOutputStream output = new FileOutputStream(fileName);
            while (stream.available() > 0)
                output.write(stream.read());
            output.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }


    public InputStream get(long guidObject) throws RemoteException {
        FileStream file = null;
        try {
            file = new FileStream("./" + guid + "/repository/" + guidObject);
        } catch (IOException e) {
            throw (new RemoteException("File does not exists"));
        }
        return file;
    }

    public void delete(long guidObject) throws RemoteException {
        File file = new File("./" + guid + "/repository/" + guidObject);
        file.delete();
    }

    public long getId() throws RemoteException {
        return guid;
    }

    public boolean isAlive() throws RemoteException {
        return true;
    }

    public ChordMessageInterface getPredecessor() throws RemoteException {
        return predecessor;
    }

    public ChordMessageInterface locateSuccessor(long key) throws RemoteException {
        if (key == guid)
            throw new IllegalArgumentException("Key must be distinct that  " + guid);
        if (successor.getId() != guid) {
            if (isKeyInSemiCloseInterval(key, guid, successor.getId()))
                return successor;
            ChordMessageInterface j = closestPrecedingNode(key);

            if (j == null)
                return null;
            return j.locateSuccessor(key);
        }
        return successor;
    }

    public ChordMessageInterface closestPrecedingNode(long key) throws RemoteException {
        // todo
        if (key != guid) {
            int i = M - 1;
            while (i >= 0) {
                try {

                    if (isKeyInSemiCloseInterval(finger[i].getId(), guid, key)) {
                        if (finger[i].getId() != key)
                            return finger[i];
                        else {
                            return successor;
                        }
                    }
                } catch (Exception e) {
                    // Skip ;
                }
                i--;
            }
        }
        return successor;
    }

    public void joinRing(String ip, int port) throws RemoteException {
        try {
            System.out.println("Get Registry to joining ring");
            Registry registry = LocateRegistry.getRegistry(ip, port);
            ChordMessageInterface chord = (ChordMessageInterface) (registry.lookup("LAB3.Chord"));
            predecessor = null;
            successor = chord.locateSuccessor(this.getId());
            System.out.println("Joining ring");
        } catch (RemoteException | NotBoundException e) {
            successor = this;
        }
    }

    public void findingNextSuccessor() {
        int i;
        successor = this;
        for (i = 0; i < M; i++) {
            try {
                if (finger[i].isAlive()) {
                    successor = finger[i];
                }
            } catch (RemoteException | NullPointerException e) {
                finger[i] = null;
            }
        }
    }

    public void stabilize() {
        try {
            if (successor != null) {
                ChordMessageInterface x = successor.getPredecessor();

                if (x != null && x.getId() != this.getId() && isKeyInOpenInterval(x.getId(), this.getId(), successor.getId())) {
                    successor = x;
                }
                if (successor.getId() != getId()) {
                    successor.notify(this);
                }
            }
        } catch (RemoteException | NullPointerException e1) {
            findingNextSuccessor();

        }
    }

    public void notify(ChordMessageInterface j) throws RemoteException {
        if (predecessor == null || (predecessor != null
                && isKeyInOpenInterval(j.getId(), predecessor.getId(), guid)))
            predecessor = j;
        try {
            File folder = new File("./" + guid + "/repository/");
            File[] files = folder.listFiles();
            for (File file : files) {
                long guidObject = Long.valueOf(file.getName());
                if (guidObject < predecessor.getId() && predecessor.getId() < guid) {
                    predecessor.put(guidObject, new FileStream(file.getPath()));
                    file.delete();
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            //happens sometimes when a new file is added during foreach loop
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void fixFingers() {

        long id = guid;
        try {
            long nextId = this.getId() + 1 << (nextFinger + 1);
            finger[nextFinger] = locateSuccessor(nextId);

            if (finger[nextFinger].getId() == guid)
                finger[nextFinger] = null;
            else
                nextFinger = (nextFinger + 1) % M;
        } catch (RemoteException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    public void checkPredecessor() {
        try {
            if (predecessor != null && !predecessor.isAlive())
                predecessor = null;
        } catch (RemoteException e) {
            predecessor = null;
//           e.printStackTrace();
        }
    }

    public Chord(int port, long guid) throws RemoteException {
        int j;
        finger = new ChordMessageInterface[M];
        for (j = 0; j < M; j++) {
            finger[j] = null;
        }
        this.guid = guid;

        predecessor = null;
        successor = this;
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                stabilize();
                fixFingers();
                checkPredecessor();
            }
        }, 500, 500);
        try {
            // create the registry and bind the name and object.
            System.out.println(guid + " is starting RMI at port=" + port);
            registry = LocateRegistry.createRegistry(port);
            registry.rebind("LAB3.Chord", this);
        } catch (RemoteException e) {
            throw e;
        }
    }

    void Print() {
        int i;
        try {
            if (successor != null)
                System.out.println("successor " + successor.getId());
            if (predecessor != null)
                System.out.println("predecessor " + predecessor.getId());
            for (i = 0; i < M; i++) {
                try {
                    if (finger != null)
                        System.out.println("Finger " + i + " " + finger[i].getId());
                } catch (NullPointerException e) {
                    finger[i] = null;
                }
            }
        } catch (RemoteException e) {
            System.out.println("Cannot retrive id");
        }
    }

    public void emitReduce(Long key, String value) throws RemoteException {
        if (isKeyInOpenInterval(key, predecessor.getId(), successor.getId())) {
            // insert in the BReduce
            System.out.println("chord emitReduce: " + key + " " + value);
            BReduce.put(key, value);
        } else {
            ChordMessageInterface peer = this.locateSuccessor(key);
            peer.emitReduce(key, value);
        }

    }

    public void emitMap(Long key, String value) throws RemoteException {
        if (isKeyInOpenInterval(key, predecessor.getId(), successor.getId())) {
            // insert in the BMap. Allows repetition
            List<String> list = new ArrayList<String>();
            list.add(value);
            if (BMap.containsKey(key)) {
                BMap.put(key, list);
            }
            System.out.println("chord emitMap: " + key);
            BMap.put(key, list);
            System.out.println("BMap length: " + BMap.size());

        } else {
            ChordMessageInterface peer = this.locateSuccessor(key);
            peer.emitMap(key, value);
        }
    }


    public void setWorkingPeer(Long page) throws IOException {
        System.out.println("Chord setworker: " + page);
        set.add(page);
    }

    public void completePeer(Long page, Long n) throws RemoteException {
        this.n += n;
        set.remove(page);
        System.out.println("completePeer: page completed: " + page + " set size: " + set.size());

    }

    public Boolean isPhaseCompleted() throws IOException {
        System.out.println("isPhaseCompleted");
        if (set.isEmpty()) {
            System.out.println("chord isPhaseCompleted turns TRUE");
            return true;
        }
        return false;
    }

    public void reduceContext(Long source, MapReduceInterface reducer, ChordMessageInterface context) throws IOException {
        // TODO: create a thread run and then return immediately
        if(source != guid) {
            System.out.println("in reduceContext");
            successor.reduceContext(source, reducer, context);
        }
            Thread thread = new Thread(new Runnable(){
                @Override
                public void run() {
                    Long counter = 0L;
                    System.out.println("reduceContext: BMap length: " + BMap.size());
                    for(Map.Entry<Long,List<String>> entry : BMap.entrySet()) {
                        Long key = entry.getKey();
                        List<String> values = entry.getValue();
                        String strings[] = new String[values.size()];
                        values.toArray(strings);
                        try {
                            System.out.println("chord reduceContext: " + key);
                            counter ++;
                            reducer.reduce(key, values, context);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
//                    try {
//                        System.out.println("reduceContext: completePeer: page " + source);
//                        context.completePeer(source, counter);
//                    } catch (RemoteException e) {
//                        e.printStackTrace();
//                    }
                }
            });
            thread.start();

        saveReduceFile(source);
    }

    public void saveReduceFile(Long source) throws IOException {
//        store Breduce in file
        String fileName = "./" + guid + "/repository/" + (guid - 1);

        FileWriter file = new FileWriter(fileName);
        Collection entreSet = BReduce.entrySet();
        Iterator it = entreSet.iterator();
        //put everything in BReduce in one Page
        while(it.hasNext()){
            file.append(it.next().toString());
        }
        if(source != guid){
            successor.saveReduceFile(source);
        }
        file.close();
    }

    public void mapContext(Long page, MapReduceInterface mapper, ChordMessageInterface context) throws IOException, RemoteException {
        //TODO: read the page line by line, but do we need a file name here
        Thread threadOut = new Thread(new Runnable() {
            @Override
            public void run() {
                String content = "";
                String fileName = "./" + guid + "/repository/" + page;

                //get the file name
                FileReader fileReader;
                Long counter = 0L;
                try {
                    fileReader = new FileReader(fileName);
                    // Always wrap FileReader in BufferedReader.
                    BufferedReader bufferedReader = new BufferedReader(fileReader);
                    while ((content = bufferedReader.readLine()) != null) {
                        counter ++;
                        String split[] = content.split(";");
                        String key = split[0];
                        String value = split[1];
//                        Thread thread = new Thread(new Runnable() {
//                            @Override
//                            public void run() {

                                try {
                                    BigInteger bgInt = new BigInteger(key);
//                                    System.out.println("chord mapContext: " + key);
                                    mapper.map(bgInt.longValue(), value, context);

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
//                        });
//                        thread.start();
                    } catch (Exception e1) {
                    e1.printStackTrace();
                    }
                try {
//                       System.out.println("mapContext: completePeer: page " + page);
                    context.completePeer(page, counter);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        threadOut.start();
    }
}
