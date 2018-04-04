package LAB3;

public class Test {

    DFS dfs;
    public Test(int p) throws Exception {
        dfs = new DFS(p);

        // User interface:
        // join, ls, touch, delete, read, tail, head, append, move
        // menu

    }
    public static void main(String args[]) throws Exception {
        Metadata metadata = new Metadata();
        metadata.addFile("testFile", 0L);
        metadata.addFile("testFile2", 0L);

        metadata.addPageToFile("testFile", 10L, 1L);
        metadata.addPageToFile("testFile", 15L, 2L);
        metadata.addPageToFile("testFile2", 20L, 3L);
        metadata.addPageToFile("testFile2", 25L, 4L);
        metadata.addPageToFile("testFile2", 25L, 4L);

        //TODO: test the json here
        Test test = new Test(6004);
        test.dfs.writeMetaData(metadata);
        System.out.println(metadata.getListOfNames());

//        Metadata m = test.dfs.readMetaData();
//        System.out.println(metadata.getListOfNames());

   }
}
