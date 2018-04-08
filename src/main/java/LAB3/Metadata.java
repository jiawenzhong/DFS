package LAB3;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a page in a file
 */
class Page implements Serializable
{
    /**
     * id of a page
     */
    @SerializedName("guid")
    Long guid;

    /**
     * length of a page
     */
    @SerializedName("length")
    Long length;

    /**
     * Constructor
     * @param length of a page
     * @param guid id of a page
     */
    public Page(Long guid, Long length){
        this.guid = guid;
        this.length = length;
    }

    /**
     * retrieves the id of a page
     * @return page id
     */
    public Long getGuid(){
        return guid;
    }

    /**
     * sets a page id
     * @param guid id to be set
     */
    public void setGuid(Long guid) {
        this.guid = guid;
    }

    /**
     * retrieves the length of page
     * @return length
     */
    public Long getLength() {
        return length;
    }

    /**
     * sets the page length
     */
    public void setLength() {
        this.length = length;
    }
}

/**
 * This class represents a file in the Metadata
 */
class MetaFile implements Serializable
{
    /**
     * name of the file
     */
    @SerializedName("name")
    String name;

    /**
     * length of the file
     */
    @SerializedName("length")
    Long  length;

    /**
     * list of pages in a file
     */
    @SerializedName("pages")
    List<Page> pages;

    /**
     * Constructor
     * @param name of the file
     * @param length of the file
     */
    public MetaFile (String name, Long length){
        this.name = name;
        this.length = length;
        pages = new ArrayList<>();
    }

    /**
     * retrieves the name of a file
     * @return the name of the file
     */
    public String getName(){
      return name;
    }

    /**
     * sets the mname of a file
     * @param name of file
     */
    public void setName(String name){
      this.name = name;
    }

    /**
     * retrieves the length
     * @return length of file
     */
    public Long getLength(){
      return length;
    }

    /**
     * sets the length of the file
     * @param length of file to be set
     */
    public void setLength(Long length){
      this.length = length;
    }

    /**
     * returns list of pages
     * @return list of pages
     */
    public List<Page> getListOfPages(){
      return pages;
    }

    /**
     * adds a page to a file
     * @param p page to be added
     */
    public void addPage(Page p){
      this.pages.add(p);
    }

    /**
     * retrieves a page specified
     * @param page to be retrieved
     * @return page id TODO: validate
     */
    public Long getPage(int page){
        return pages.get(page - 1).getGuid();
    }

    /**
     * retrieves the last page of the
     * @return
     */
    public Long getLastPage(){
        return pages.get(pages.size() - 1).getGuid();
    }
    /**
     * Returns the amount of pages in a file
     * @return size of the page list
     */
    public int getNumOfPages(){
        return pages.size();
    }
}

/**
 * This class represents the Metadata
 */
public class Metadata implements Serializable
{
    /**
     * list of files
     */
    @SerializedName("metafiles")
    List<MetaFile> metafiles;

    /**
     * default Constructor
     */
    public Metadata(){
        metafiles = new ArrayList<>();
    }

    /**
     * adds a file to the metadata
     * @param name of the file to be added
     * @param length of the file being added
     */
    public void addFile(String name, Long length){
        MetaFile metafile = new MetaFile(name, length);
        this.metafiles.add(metafile);
    }

    public MetaFile getFileByName(String fileName){
        for(MetaFile f : metafiles){
            if(f.getName().equals(fileName)){
                return f;
            }
        }
        return null; //TODO: handle exception in dfs
    }

    /**
     * Returns the list of names of all the files in 1 string
     * @return string of files
     */
    public String getListOfNames(){
        String names = "";
        for(MetaFile f : metafiles)
            names += f.getName() + "\t" + f.getLength() + "\t" + f.getNumOfPages() +"\n";

        return names;
    }

    /**
     * gets a page of a file
     * @param fileName name of file the page belongs to
     * @param page to be retrieved
     * @return page
     */
    public Long getPage(String fileName, int page){
        for(MetaFile f : metafiles){
            if(f.getName().equals(fileName)){
                return f.getPage(page);
            }
        }
        return 0L;
    }

    /**
     * Retreives the first page of the file specified
     * @param fileName name of file to search by
     * @return id of the page
     */
      public Long getHead(String fileName){
          return getPage(fileName, 1);
      }

    /**
     * returns the last page in the file specified
     * @param fileName name of file to search bt
     * @return id of page
     */
      public Long getTail(String fileName) {
          for(MetaFile f : metafiles){
              if(f.getName().equals(fileName)){
                  return f.getLastPage();
              }
          }
          return 0L;
      }

    /**
     * add page to a file
     * @param fileName name of file to add to
     * @param length of page to be added
     * @param guid id of page to be added
     */
      public void addPageToFile(String fileName, Long length, Long guid){
          for(MetaFile f : metafiles){
          if(f.getName().equals(fileName)){
              Page p = new Page(guid, length);
              f.addPage(p);
              f.setLength(f.getLength() + length);
          }
        }
      }


}
