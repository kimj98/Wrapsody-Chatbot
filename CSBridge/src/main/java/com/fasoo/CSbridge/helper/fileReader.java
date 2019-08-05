////////////////////////////////////////////////////////////////////////////////////////////////////
//    This class Reads the text file that contains the server info.                               //
//    Methods:                                                                                    //
//        public static String[] readfiles()                                                      //
//            -read the txt file ( connect.txt )                                                  //
//             Saved server data into the String array and return it.                             //
//    [Data read sequence]                                                                        //
//        1. Host                                                                                 //
//        2. Port Number                                                                          //
//        3. Virtual Host                                                                         //
//        4. UserName                                                                             //
//        5. Password                                                                             //
//        Copyright by Jaemoon Seok.                                                              //
////////////////////////////////////////////////////////////////////////////////////////////////////

package com.fasoo.CSbridge.helper;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Reads text file that contains Rabbitmq server info.
 */
public class fileReader {

    /**
     * Read the text file to get the connection info.
     * @return Array that contains server info.
     */
    public static String[] readfiles(){
        String saveLine[] = new String[5];
        int lineNum = 0;
        try{
            BufferedReader reader = new BufferedReader(new FileReader(
                    "../CSBridge/connect.txt"));
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
