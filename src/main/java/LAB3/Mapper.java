package LAB3;

import java.io.IOException;
import java.util.List;

public class Mapper implements MapReduceInterface {

    /**
     * sort the keys
     * @param key
     * @param value
     * @param context
     * @throws IOException
     */
    public void map(Long key, String value, ChordMessageInterface context) throws IOException
    {
        context.emitMap(key, value);
    }

    /**
     * counts the number of time the key appears
     * @param key key in map
     * @param values value for corresponding key
     * @param context chord message interface
     * @throws IOException
     */
    public void reduce(Long key, List<String > values, ChordMessageInterface context) throws IOException
    {
        String word = values.get(0).split(":")[0];
        context.emitReduce(key, word +":"+ values.size());
    }
}
