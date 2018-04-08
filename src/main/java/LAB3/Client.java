package LAB3;

import java.io.*;
import java.util.Scanner;

public class Client
{
    DFS dfs;
    public Client(int p) throws Exception {
        dfs = new DFS(p);
    }

    public String readPageContents(String file, InputStream stream){
        String contents = "";
        try {
            FileOutputStream output = new FileOutputStream(file);
            while (stream.available() > 0)
                output.write(stream.read());
            output.close();

            FileReader fileReader =
                    new FileReader(file);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader =
                    new BufferedReader(fileReader);

            while((contents = bufferedReader.readLine()) != null) {
                System.out.println("Page: "+ contents);
            }
        }
        catch (IOException e) {
            System.out.println(e);
        }
        return contents;
    }

    static public void main(String args[]) throws Exception
    {
        if (args.length < 1 ) {
            throw new IllegalArgumentException("Parameter: <port>");
        }
//        TODO: this used to create the file the first time around
//        Metadata metadata = new Metadata();
//        metadata.addFile("testFile", 0L);
//        metadata.addFile("testFile2", 0L);
//
//        metadata.addPageToFile("testFile", 20L, 1L);
//        metadata.addPageToFile("testFile", 15L, 2L);
//        metadata.addPageToFile("testFile2", 10L, 3L);
//        metadata.addPageToFile("testFile2", 25L, 4L);
//        metadata.addPageToFile("testFile2", 25L, 4L);

        Client client = new Client( Integer.parseInt(args[0]));
//        client.dfs.writeMetaData(metadata);

        Scanner in = new Scanner(System.in);

        System.out.println("Type in your option from the list below:");
        System.out.println("join (ip) (port)");
        System.out.println("ls");
        System.out.println("touch (desired file name)");
        System.out.println("delete (file name)");
        System.out.println("read (file name) (page)");
        System.out.println("tail (file name)");
        System.out.println("head (file name)");
        System.out.println("append (file name) (page)");
        System.out.println("move (file name)");


        while(true) {
            String input = in.nextLine();
            System.out.println(input);
            String[] array = input.split(" ");
            String dfsCommand = array[0];
            String fileName = "";
            InputStream stream = null;

            switch (dfsCommand) {
                case "print":
                    client.dfs.print();
                    break;
                case "join":
                    String ip = array[1];
                    int port = Integer.parseInt(array[2]);
                    client.dfs.join(ip, port);
                    break;
                case "ls":
                    System.out.println(client.dfs.ls());
                    break;
                case "touch":
                    fileName = array[1];
                    client.dfs.touch(fileName);
                    break;
                case "delete":
                    fileName = array[1];
                    client.dfs.delete(fileName);
                    break;
                case "read":
                    fileName = array[1];
                    int pageNum = Integer.parseInt(array[2]);
                    stream = client.dfs.read(fileName, pageNum);
                    String file = "./"+ fileName + pageNum;
                    client.readPageContents(file, stream);
                    break;
                case "tail":
                    fileName = array[1];
                    stream = client.dfs.tail(fileName);
                    client.readPageContents(fileName, stream);
                    break;
                case "head":
                    fileName = array[1];
                    stream = client.dfs.head(fileName);
                    String f = "./"+ fileName;
                    client.readPageContents(f, stream);
                    break;
                case "append":
                    fileName = array[1];
                    String page = array[2];
                    client.dfs.append(fileName, page);
                    break;
                case "move":
                    fileName = array[1];
                    String newName = array[2];
                    client.dfs.mv(fileName, newName);
                default:
                    System.out.println("Command " + dfsCommand + " does not exist.");
                    break;

            }
        }
    }
}
