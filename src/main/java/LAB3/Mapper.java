package LAB3;

import java.io.IOException;

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
    public void reduce(Long key, String values[], ChordMessageInterface context) throws IOException
    {
        String word = values[0].split(":")[0];
        context.emitMap(key, word +":"+ values.length);
    }
}
