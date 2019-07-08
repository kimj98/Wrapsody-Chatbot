import java.nio.charset.Charset;

public class utf8 {
    private  final Charset UTF8_CHARSET =Charset.forName("UTF-8");

    public String decodeUTF8(byte[] bytes){
        return (new String(bytes,UTF8_CHARSET));
    }

    public byte[] encodeUTF8(String strings){
        return strings.getBytes(UTF8_CHARSET);
    }
}
