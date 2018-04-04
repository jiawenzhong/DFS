package LAB3;


public class Client
{
    DFS dfs;
    public Client(int p) throws Exception {
        dfs = new DFS(p);

            // User interface:
            // join, ls, touch, delete, read, tail, head, append, move
            // menu


    }

//    public void writeMetadata(){
//        dfs.writeMetaData();
//    }

    static public void main(String args[]) throws Exception
    {
        if (args.length < 1 ) {
            throw new IllegalArgumentException("Parameter: <port>");
        }
        Client client=new Client( Integer.parseInt(args[0]));


     }
}
