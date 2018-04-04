package LAB3;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.nio.charset.Charset;
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

    public Metadata readMetaData() throws Exception
    {
        Metadata metadata = new Metadata();
        Gson gson = new Gson();
        long guid = md5("Metadata.json");
        LAB3.ChordMessageInterface peer = chord.locateSuccessor(guid);

        //writeMetaData(metadata);

        InputStream metadataraw = peer.get(guid);
        JsonReader jsonReader = new JsonReader(new InputStreamReader(metadataraw));
        metadata = gson.fromJson(jsonReader, Metadata.class);

        return metadata;
    }

    public void writeMetaData(Metadata metadata) throws Exception
    {
        //TODO: JsonObject j = metadata.createJson() returns Json and
        //TODO: store in InputStream or file
        //JSONObject j = metadata.createJson();//get the json object from the metadata
        Gson gson = new Gson();
        //writeJsonToFile(gson);//write to a text file
        long guid = md5("Metadata.json");
        LAB3.ChordMessageInterface peer = chord.locateSuccessor(guid);
        //store into an inputstream
        //File file = new File("jsonfile.json");
        String jsonString = gson.toJson(metadata);
        InputStream stream = new ByteArrayInputStream(jsonString.getBytes(Charset.forName("UTF-8")));

        peer.put(guid, stream);
    }

    /**
     * write the json object to the text file
     * @param j the json object that convert from metadata
     * @throws Exception
     */
    private void writeJsonToFile(JSONObject j) throws Exception{
        try (FileWriter file = new FileWriter("jsonfile.json")) {
            file.write(j.toString());
            System.out.println("Successfully Copied JSON Object to File...");
        }
    }
//
//    //TODO: Eunice
//    public void mv(String oldName, String newName) throws Exception
//    {
//        // TODO:  Change the name in json_testing.Metadata
//        // Write json_testing.Metadata //should happen in write
//
//        //check that the file with specified name exists and update it
//        Metadata metadata = readMetaData();
//        for(MetaFile file : metadata.metafiles){
//            if(file.getName().equals(oldName)){
//                file.setName(newName);
//            }
//        }
//
//        //write it back to the file
//        try{
//            writeMetaData(metadata);
//        }catch(FileNotFoundException fnfe){
//
//            System.out.println("File not found! Write was unsuccessful");
//        }
//    }


    public String ls() throws Exception
    {
        String listOfFiles = "";
       // TODO: returns all the files in the json_testing.Metadata
       // Metadata jp = readMetaData();
        //jp.getListOfNames();
        return listOfFiles;
    }


    public void touch(String fileName) throws Exception
    {
         // TODO: Create the file fileName by adding a new entry to the json_testing.Metadata
        // Write json_testing.Metadata



    }
    public void delete(String fileName) throws Exception
    {
        // TODO: remove all the pages in the entry fileName in the json_testing.Metadata and then the entry
        // for each page in json_testing.Metadata.filename
        //     peer = chord.locateSuccessor(page.guid);
        //     peer.delete(page.guid)
        // delete json_testing.Metadata.filename
        // Write json_testing.Metadata
    }

//    //TODO: Eunice
//    public Byte[] read(String fileName, int pageNumber) throws Exception
//    {
//        // TODO: read pageNumber from fileName
//        // Does this mean read from the page specified?
//        Metadata metadata = readMetaData();
//        Long page = metadata.getPage(fileName, pageNumber); // get actual page object?
//        return null; // return data with chord?
//
//
//    }


    public Byte[] tail(String fileName) throws Exception
    {
        // TODO: return the last page of the fileName
        return null;
    }
    public Byte[] head(String fileName) throws Exception
    {
        // TODO: return the first page of the fileName
        return null;
    }
    public void append(String filename, Byte[] data) throws Exception
    {
        // TODO: append data to fileName. If it is needed, add a new page.
        // Let guid be the last page in json_testing.Metadata.filename
        //LAB3.ChordMessageInterface peer = chord.locateSuccessor(guid);
        //peer.put(guid, data);
        // Write json_testing.Metadata


    }

}
