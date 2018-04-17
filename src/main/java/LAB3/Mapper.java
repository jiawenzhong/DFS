package LAB3;

import java.io.IOException;

public class Mapper {

    public void map(Long key, String value) throws IOException
    {
//        For each word in value
//        emit(md5(word), word +":"+1);
    }
    public void reduce(Long key, String values[]) throws IOException
    {
//        word = values[0].split(":")[0]
//        emit(key, word +":"+ len(values));
    }
}
