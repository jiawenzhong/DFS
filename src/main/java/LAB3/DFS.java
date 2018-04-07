package LAB3;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.nio.file.*;
import java.math.BigInteger;
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

    public  void join(String Ip, int port) throws Exception
    {
        chord.joinRing(Ip, port);
        chord.Print();
    }

    public void print(){
        chord.Print();
    }

    public Metadata readMetaData() throws Exception
    {
        Metadata metadata = new Metadata();
        Gson gson = new Gson();
        long guid = md5("Metadata.json");
        LAB3.ChordMessageInterface peer = chord.locateSuccessor(guid);


        InputStream metadataraw = peer.get(guid);//catch exception, check if file exists (if not create it)
        JsonReader jsonReader = new JsonReader(new InputStreamReader(metadataraw));
        metadata = gson.fromJson(jsonReader, Metadata.class);
            //System.out.println("JSON READ: " + jsonReader.toString());
            //TODO: create it if it doesn't exist
            //System.out.println("File was not found when attempting to read, so it has been created.");
            //writeMetaData(metadata);

        return metadata;
    }

    public void writeMetaData(Metadata metadata) throws Exception
    {
        //TODO: JsonObject j = metadata.createJson() returns Json and
        //TODO: store in InputStream or file
        Gson gson = new Gson();
        long guid = md5("Metadata.json");
        LAB3.ChordMessageInterface peer = chord.locateSuccessor(guid);
        String jsonString = gson.toJson(metadata);
        //InputStream stream = new ByteArrayInputStream(jsonString.getBytes(Charset.forName("UTF-8")));
        writeJsonToFile(jsonString);
        FileStream fileStream = new FileStream("jsonfile.json");
        System.out.println("JSON WRITE: " + jsonString);
        peer.put(guid, fileStream);
    }

    /**
     * write the json object to the text file
     * @param j the json object that convert from metadata
     * @throws Exception
     */
    private void writeJsonToFile(String j) throws Exception{
        try (FileWriter file = new FileWriter("jsonfile.json")) {
            file.write(j);
            System.out.println("Successfully Copied JSON Object to File...");
        }
    }

    public void mv(String oldName, String newName) throws Exception
    {
        // TODO:  Change the name in json_testing.Metadata
        // Write json_testing.Metadata //should happen in write

        //check that the file with specified name exists and update it
        Metadata metadata = readMetaData();//TODO: add to metadata
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


    public String ls() throws Exception
    {
        String listOfFiles = "";
       // TODO: returns all the files in the json_testing.Metadata
       Metadata jp = readMetaData();
       listOfFiles = jp.getListOfNames();

        return listOfFiles;
    }


    public void touch(String fileName) throws Exception
    {
         // TODO: Create the file fileName by adding a new entry to the json_testing.Metadata
        Metadata m = readMetaData();
        m.addFile(fileName, 0L);
        writeMetaData(m);
        //System.out.println(ls());
        // Write json_testing.Metadata



    }
    public void delete(String fileName) throws Exception
    {
        // TODO: remove all the pages in the entry fileName in the json_testing.Metadata and then the entry
        Metadata m = readMetaData();
        MetaFile file = m.getFileByName(fileName);
        for(Page p : file.getListOfPages()){
            LAB3.ChordMessageInterface peer = chord.locateSuccessor(p.getGuid());
            peer.delete(p.getGuid());
        }
        m.metafiles.remove(file);
        writeMetaData(m);

        // for each page in json_testing.Metadata.filename
        //     peer = chord.locateSuccessor(page.guid);
        //     peer.delete(page.guid)
        // delete json_testing.Metadata.filename
        // Write json_testing.Metadata
    }

    public InputStream read(String fileName, int pageNumber) throws Exception
    {
        // TODO: read pageNumber from fileName
        // Does this mean read from the page specified?
        Metadata metadata = readMetaData();
        Long page = metadata.getPage(fileName, pageNumber);
        LAB3.ChordMessageInterface peer = chord.locateSuccessor(page);
        return peer.get(page);

    }

    public InputStream tail(String fileName) throws Exception
    {
        // TODO: return the last page of the fileName
        Metadata m = readMetaData();
        Long tail = m.getTail(fileName);
        LAB3.ChordMessageInterface peer = chord.locateSuccessor(tail);

        return peer.get(tail);
    }

    public InputStream head(String fileName) throws Exception
    {
        // TODO: return the first page of the fileName
        Metadata m = readMetaData();
        Long head = m.getHead(fileName);
        LAB3.ChordMessageInterface peer = chord.locateSuccessor(head);

        return peer.get(head);
    }

    public void append(String filename, String page) throws Exception
    {
        // TODO: append data to fileName. If it is needed, add a new page.
        Metadata m = readMetaData();
        Long guid = md5(page);
        FileStream data = new FileStream(page);

        m.addPageToFile(filename, data.getSize(), guid);
        // Let guid be the last page in json_testing.Metadata.filename
        LAB3.ChordMessageInterface peer = chord.locateSuccessor(guid);
        peer.put(guid, data);
        writeMetaData(m);

        //TODO: check if file exists
        // Write json_testing.Metadata


    }

}
