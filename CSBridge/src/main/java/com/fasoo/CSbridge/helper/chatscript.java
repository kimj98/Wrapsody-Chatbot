///////////////////////////////////////////////////////////////////////////////////////////////////////
//   This class will be the bridge between the CSBridge and CS Server(Korean).                       //
//   Methods:                                                                                        //
//       public String processInput(String,String)                                                   //
//           - this function call the doMessage method and return its return value.                  //
//             this return value will be the response of the ChatScript.                             //
//           - @Param:                                                                               //
//               -String input : this variable has the user's input on the Web.                      //
//               -String userId: this variable has the user's Id( Login ID on web).                  //
//       public String myJsonParser(String)                                                          //
//           - this method will return the String value after parse the String converted JSON Object.//
//           - @Param:                                                                               //
//               -String s : this variable has the String converted JSON Object.                     //
//       public String doMessage(String)                                                             //
//           - this method makes connection with CS Server with specific Port Number for get the     //
//             response from the CS. And return the response.                                        //
///////////////////////////////////////////////////////////////////////////////////////////////////////


package com.fasoo.CSbridge.helper;

import java.net.Socket;
import java.io.IOException;
import java.util.*;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.InputStreamReader;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
/**
 * This class make connection between Korean version of ChatScript server
 */
public class chatscript {

    char nullChar = (char) 0;
    String userName = "Testbot"; // or take as input
    /**
     * Text to send to the chatscript server for pattern matching
     * @param input String value of user's input
     * @param userId String value of user's ID
     * @return Response of the ChatScript from doMessage method.
     * @see #doMessage(String)
     **/
    public String processInput(String input, String userId){
        return doMessage(userId+nullChar+nullChar+input+nullChar);
    }
    /**
     * This method parse the JSON and extract the body part.
     * @param s String value of message received from RabbtMQ server
     * @return String value of body in JSON
     */
    public String myJsonParser(String s){
        JSONParser parser = new JSONParser();
        String body="";
        try{
            JSONObject jObj = (JSONObject) parser.parse(s);
            body = (String)jObj.get("body");

        }catch (ParseException e){
            e.printStackTrace();
        }
        return body;
    }

    /**
     * This method connect with ChatScript server and get the response from it.
     * @param mess String value of user's input
     * @return String output from ChatScript server
     * @see #processInput(String, String)
     **/
    private String doMessage(String mess)
    {
        Socket echoSocket;
        String resp = "";
        String temp = "";
        try {
            echoSocket = new Socket("localhost", 1025);
            PrintWriter out = new PrintWriter(echoSocket.getOutputStream(),
                    true);
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    echoSocket.getInputStream()));
            out.println(mess);
            while((temp = in.readLine()) != null){
                resp += temp+"\n";
            }
            echoSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resp;
    }
    /**
     * @deprecated
     * @param args .
     */
    public void init(HashMap<String, String> args) {
        System.out.println("ChatScript Bot init()...");
        System.out.println("got back:" + doMessage(nullChar+"1"+nullChar+nullChar));
        System.out.println("now starting conversation..." + doMessage(userName+nullChar+nullChar+nullChar));
        System.out.println("conversation started with default bot as " + userName);
    }
    /**
     * @deprecated
     * @return Type of the Bot
     */
    public String getBotType() {
        // TODO Auto-generated method stub
        return "ChatScript";
    }
}