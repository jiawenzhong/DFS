package LAB3;

import java.io.IOException;
import java.util.List;

public interface MapReduceInterface {
    void map(Long key, String value, ChordMessageInterface context) throws IOException;
    void reduce(Long key, List<String> value, ChordMessageInterface context) throws IOException;
}
