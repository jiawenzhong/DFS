package LAB3;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

public interface MapReduceInterface extends Serializable {
    void map(Long key, String value, ChordMessageInterface context) throws IOException;
    void reduce(Long key, List<String> value, ChordMessageInterface context) throws IOException;
}
