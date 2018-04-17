package LAB3;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.nio.file.*;
import java.math.BigInteger;
import java.rmi.RemoteException;
import java.rmi.ServerException;
import java.security.*;
// import a json package


/* JSON Format

 {
    "metadata" :
    {
        file :
        {
            name  : "File1"
            numberOfPages : "3"
            size : "2291"
            page :
            {
                guid   : "22412"
                size   : "1024"
            }
            page :
            {
                guid   : "46312"
                size   : "1024"
            }
            page :
            {
                guid   : "93719"
                size   : "243"
            }
        }
    }
}


 */


public class DFS
{
    int port;
    Chord  chord;

    private long md5(String objectName)
    {
        try
        {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.reset();
            m.update(objectName.getBytes());
            BigInteger bigInt = new BigInteger(1,m.digest());
            return Math.abs(bigInt.longValue());
        }
        catch(NoSuchAlgorithmException e)
        {
                e.printStackTrace();

        }
        return 0;
    }



    public DFS(int port) throws Exception
    {
        this.port = port;
        long guid = md5("" + port);
        chord = new Chord(port, guid);
        Files.createDirectories(Paths.get(guid+"/repository"));
    }

    /**
     * Join ring
     * @param Ip (localhost)
     * @param port to connect to
     * @throws Exception
     */
    public  void join(String Ip, int port) throws Exception
    {
        chord.joinRing(Ip, port);
        chord.Print();
    }

    /**
     * Print used for debugging in the client
     */
    public void print(){
        chord.Print();
    }

    /**
     * read metadata
     * @return metadata to be read
     * @throws Exception
     */
    public Metadata readMetaData() throws Exception
    {
        Metadata metadata = new Metadata();
        Gson gson = new Gson();
        long guid = md5("Metadata.json");
        LAB3.ChordMessageInterface peer = chord.locateSuccessor(guid);

        try {
            InputStream metadataraw = peer.get(guid);//catch exception, check if file exists (if not create it)
            JsonReader jsonReader = new JsonReader(new InputStreamReader(metadataraw));
            metadata = gson.fromJson(jsonReader, Metadata.class);
            //System.out.println("JSON READ: " + jsonReader.toString());//for debugging
            //TODO: create it if it doesn't exist (in the catch)
            //System.out.println("File was not found when attempting to read, so it has been created.");
            //writeMetaData(metadata);
        } catch (RemoteException e){
            System.out.println("File does not exists.");
            System.out.println("Initializing metatdata.");
            createMetaData();
        }

        return metadata;
    }

    /**
     * create/initialize the metadata when there are no files exists
     * @throws Exception
     */
    public void createMetaData() throws Exception {
        Metadata metadata = new Metadata();
//        metadata.addFile("testFile", 0L);
//        metadata.addFile("testFile2", 0L);
//
//        metadata.addPageToFile("testFile", 20L, 1L);
//        metadata.addPageToFile("testFile", 15L, 2L);
//        metadata.addPageToFile("testFile2", 10L, 3L);
//        metadata.addPageToFile("testFile2", 25L, 4L);
//        metadata.addPageToFile("testFile2", 25L, 4L);

        writeMetaData(metadata);
    }

    /**
     * Write the metadata
     * @param metadata to be written and saved
     * @throws Exception TODO: catch it (try/catch)
     * DONE ***
     */
    public void writeMetaData(Metadata metadata) throws Exception
    {
        Gson gson = new Gson();
        try {
            long guid = md5("Metadata.json");
            LAB3.ChordMessageInterface peer = chord.locateSuccessor(guid);
            String jsonString = gson.toJson(metadata);

            writeJsonToFile(jsonString);
            FileStream fileStream = new FileStream("jsonfile.json");
            //System.out.println("JSON WRITE: " + jsonString);//for debugging
            peer.put(guid, fileStream);
        }catch (RemoteException e){
            System.out.println("File does not exists.");
        }
    }

    /**
     * Write the json object to the text file
     * @param json the json object in string form that convert from metadata
     * @throws Exception
     */
    private void writeJsonToFile(String json) throws Exception{
        try (FileWriter file = new FileWriter("jsonfile.json")) {
            file.write(json);
            System.out.println("Successfully Copied JSON Object to File...");
        }catch (RemoteException e){
            System.out.println("File does not exists.");
        }
    }

    /**
     * Renames the file specified
     * @param oldName of the file
     * @param newName to be given to the file
     * @throws Exception
     */
    public void mv(String oldName, String newName) throws Exception
    {
        //check that the file with specified name exists and update it
        Metadata metadata = readMetaData();
        for(MetaFile file : metadata.metafiles){
            if(file.getName().equals(oldName)){
                file.setName(newName);
                break;
            }
        }

        //write it back to the file
        try{
            writeMetaData(metadata);
        }catch(FileNotFoundException fnfe){

            System.out.println("File not found! Write was unsuccessful");
        }
    }

    /**
     * Returns all the files in the metadata
     * @return string of all the files
     * @throws Exception
     */
    public String ls() throws Exception
    {
        String listOfFiles = "";
        try {
            Metadata metadata = readMetaData();
            listOfFiles = metadata.getListOfNames();
        } catch (FileNotFoundException e){
            System.out.println("File does not exists.");
        }

        return listOfFiles;
    }

    /**
     * Create the file fileName by adding a new entry to the metadata
     * @param fileName of file to be crated
     * @throws Exception
     */
    public void touch(String fileName) throws Exception
    {
        Metadata m = readMetaData();
        m.addFile(fileName, 0L);
        writeMetaData(m);

    }

    /**
     * TODO: test in client!
     * Remove all the pages in the entry fileName in the metadata, and then the entry
     * @param fileName to be delete
     * @throws Exception
     */
    public void delete(String fileName) throws Exception
    {
        Metadata m = readMetaData();
        try {
            MetaFile file = m.getFileByName(fileName);
            for (Page p : file.getListOfPages()) {
                LAB3.ChordMessageInterface peer = chord.locateSuccessor(p.getGuid());
                peer.delete(p.getGuid());
            }
            m.metafiles.remove(file);
            writeMetaData(m);
        } catch (RemoteException e){
            System.out.println("File does not exists.");
        } catch (NullPointerException a){
            System.out.println("File does not exists.");
        }
    }

    /**
     * Read contents of the page pageNumber from fileName
     * @param fileName of file to read from
     * @param pageNumber of page to read from
     * @return input stream representation of a page
     * @throws Exception
     */
    public InputStream read(String fileName, int pageNumber) throws Exception
    {
        // TODO: read pageNumber from fileName
        // Does this mean read from the page specified?
        Metadata metadata;
        Long page = null;
        LAB3.ChordMessageInterface peer = null;
        metadata = readMetaData();
        page = metadata.getPage(fileName, pageNumber);
        peer = chord.locateSuccessor(page);

        return peer.get(page);
    }

    /**
     * Return the last page of the fileName specified
     * @param fileName of file
     * @return input stream representation of a page
     * @throws Exception
     */
    public InputStream tail(String fileName) throws Exception
    {

        Metadata m = readMetaData();
        Long tail = m.getTail(fileName);
        LAB3.ChordMessageInterface peer = chord.locateSuccessor(tail);
        return peer.get(tail);
    }

    /**
     * Returns the first page of the fileName
     * @param fileName of file
     * @return input stream representation of a page
     * @throws Exception
     */
    public InputStream head(String fileName) throws Exception
    {
        Metadata m = readMetaData();
        Long head = m.getHead(fileName);
        LAB3.ChordMessageInterface peer = chord.locateSuccessor(head);

        return peer.get(head);
    }

    /**
     * Append data to fileName TODO: create new page if needed
     * @param filename of file to append to
     * @param page that will be appended to fileName
     * @throws Exception
     */
    public void append(String filename, String page) throws Exception {
        try {
            Metadata m = readMetaData();
            Long guid = md5(page);
            FileStream data = new FileStream(page);

            m.addPageToFile(filename, data.getSize(), guid);
            // Let guid be the last page in json_testing.Metadata.filename
            LAB3.ChordMessageInterface peer = chord.locateSuccessor(guid);
            peer.put(guid, data);
            writeMetaData(m);
        }catch (FileNotFoundException e){
            System.out.println("File not found.");
        }
    }

    public void runMapReduce(MetaFile metafile) throws RemoteException {
        Context context= new Context();
        // TODO: do we need a mapreduce class?
//        mapreduce = new MapReduceInterface();
//        // map Phases
        for( Page page : metafile.getListOfPages()) {
            context.add(page);
            // TODO: what is peer? use this in here to get the peer?
            LAB3.ChordMessageInterface peer = chord.locateSuccessor(page.getGuid());
            // TODO: let peer be the process responsible for storing page
            // TODO: peer doesn't have mapContext, Context object does
            // peer.mapContext(page, mapreduce, context);
        }
//        wait until context.hasCompleted() = true
//        // reduce phase
//        reduceContext(guid, mapreduce, context);
//        wait until context.hasCompleted() = true;
    }

}
