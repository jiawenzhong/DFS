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
//        for(String word : value.split(":"))
        context.emitMap(key, value);
    }

    /**
     * counts the # of time the key appears
     * @param key
     * @param values
     * @param context
     * @throws IOException
     */
    public void reduce(Long key, List<String > values, ChordMessageInterface context) throws IOException
    {
        String word = values.get(0).split(":")[0];
        context.emitMap(key, word +":"+ values.size());
    }
}
