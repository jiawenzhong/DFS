package LAB3;

import java.io.IOException;

public interface MapReduceInterface {
    void map(Long key, String value, ChordMessageInterface context) throws IOException;
    void reduce(Long key, String value[], ChordMessageInterface context) throws IOException;
}
