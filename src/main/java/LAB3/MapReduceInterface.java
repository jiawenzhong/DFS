package LAB3;

import java.io.IOException;

public interface MapReduceInterface {
    void map(Long key, String value) throws IOException;
    void reduce(Long key, String value[]) throws IOException;
}
