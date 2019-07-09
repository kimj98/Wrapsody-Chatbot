/*
    Copyright by Jaemoon Seok.
 */
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class fileReader {

    /*
        Reads the text file that contains the server info.
        [Sequence]
        1. Host
        2. Port Number
        3. Virtual Host
        4. UserName
        5. Password

        Return the Array which contain the 5 Strings for connect to the server.
     */
    public static String[] readfiles(){
        String saveLine[] = new String[5];
        int lineNum = 0;
        try{
            BufferedReader reader = new BufferedReader(new FileReader(
                    "/Users/user/Desktop/CS_Project/CS_Bridge/CSBridge/connect.txt"));
            String line = reader.readLine();
            while(line != null){
                line = line.substring(line.indexOf(':')+2);
                saveLine[lineNum] =line;
                lineNum += 1;
                line = reader.readLine();
            }
            reader.close();
        } catch(IOException e){
            e.printStackTrace();
        }
        return saveLine;
    }
}
