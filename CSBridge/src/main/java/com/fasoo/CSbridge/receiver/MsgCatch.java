///////////////////////////////////////////////////////////////////////////////////////////////
//    This Class is the Main Class.                                                          //
//    The main works of this class is listening(receiving) and sending the message with the  //
//    Rabbitmq message server.                                                               //
//    Inside the main method,                                                                //
//          1. making connection with rabbitmq server                                        //
//          2. receive the message from the message server.                                  //
//          3. indicate the type of the message                                              //
//              - chat, event, api                                                           //
//          4. Send the message to the rabbitmq message server.                              //
//    Methods:                                                                               //
//        public static void main(String[] args)                                             //
//          1. [ Chat case ]                                                                 //
//             Determine the received message's language type and send the appropriate       //
//             ChatScript server ( Korean, English ) and get the response from it.           //
//             Based on the type of the response, figure it out the response message         //
//             need to attach something or not. If yes, then do the requirement works.       //
//             The requirement works would be attach photo, files, buttons and etc .         //
//          2. [ Event case ]                                                                //
//             The event class have two cases in side ot itself.                             //
//                 - Reopen case & Initial open case.                                        //
//             Parse the received JSON and store the user's ID and locale.                   //
//             And Send back the "Welcome message" to the User.                              //
//          3. [ Api  case ]                                                                 //
//             The Api case is used for update the Left side menu(Categories)                //
//             And its sub-menus( commands ).                                                //
//             Read the list of the menus and commands from the topic file                   //
//             based on user's "Locale"                                                      //
///////////////////////////////////////////////////////////////////////////////////////////////


package com.fasoo.CSbridge.receiver;

import com.fasoo.CSbridge.helper.*;
import com.fasoo.CSbridge.publisher.LeftMenuSend;
import com.fasoo.CSbridge.publisher.MsgSend;
import com.rabbitmq.client.*;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;
import static com.sun.javafx.util.Utils.split;
/**
 * MsgCatch is the main class.
 * Make connection with Rabbitmq server and listening message
 * and send back after modification.
 */
public class MsgCatch {

    /**Store the user's ID and Locale info*/
    public static HashMap<String,String> userLocale = new HashMap<String, String>();
    /**Store the user's ID and question*/
    public static HashMap<String,String> userReport = new HashMap<String, String>();

    /**
     * Make connection with Rabbitmq server for listening message.
     * Send back after modify the message.
     * @param args include the input from the command lines.
     * @throws IOException handle exception
     * @throws TimeoutException handle exception
     *
     */
    public static void main(String[] args) throws IOException, TimeoutException{
        //settings for connecting with RabbitmQ
        ConnectionFactory factory = new ConnectionFactory();
//        fileReader textread = new fileReader();
//        String[] saveLine = textread.readfiles();
        factory.setNetworkRecoveryInterval(1000);
        factory.setHost(constant.Host);
        factory.setPort(constant.Port);
        factory.setVirtualHost(constant.VirtualHost);
        factory.setUsername(constant.Username);
        factory.setPassword(constant.Password);
        factory.setAutomaticRecoveryEnabled(true);

        //generate channel & declare queue & bind queue with the exchange.
        final Connection connection2 = factory.newConnection();
        final Channel channel2 = connection2.createChannel();
        channel2.queueDeclare(constant.QueueName,true,false,false,null);
        channel2.queueBind(constant.QueueName,"user",constant.MSRoutingkey);

        //RECEIVING Process.
        boolean autoAck =false;
        channel2.basicConsume(constant.QueueName, autoAck, "myConsummerTag",
                new DefaultConsumer(channel2) {
                    @Override
                    public void handleDelivery(String consumerTag,
                                               Envelope envelope,
                                               AMQP.BasicProperties properties,
                                               byte[] body) throws IOException
                    {

                        String[] exCheck = split(envelope.getRoutingKey(),".");
                        chatscript cs = new chatscript();
                        chatscript_en csEN= new chatscript_en();
                        long deliveryTag  = envelope.getDeliveryTag();
                        //process the message components here ...)
                        channel2.basicAck(deliveryTag, false);

                        // event 케이스가 아닐때.
                        if((exCheck[0].equals("chat"))){
                            String msg = new String(body);
                            /*
                               Use the JSON Parser class to parsing the json type input.
                               String question : store the "Body" key value of the JSon type.
                            */
                            JSONParser parser = new JSONParser();
                            String question = cs.myJsonParser(msg);
                            String PosMsg="";
                            MsgSend msgSend = new MsgSend();

                            try{
                                JSONObject jObj = (JSONObject) parser.parse(msg); // {"senduser":"kth" ......}
                                JSONObject jObj2 = (JSONObject) parser.parse(msg);
                                // this if statement prevent to read the bot's respond msg.
                                if(!( "@BOT@chatscript".equals((String)jObj.get("sendUserId")))){
                                    String origin_userId = jObj.get("sendUserId").toString();
                                    jObj.put("sendUserId","@BOT@chatscript"); // main response sending to user.
                                    jObj2.put("sendUserId","@BOT@chatscript");// Additional question

                                    //put the question into the POS tagger.
                                    PosMsg = PosFilter.filtering(question);
                                    String isEnglish = checkType.isEnglish(PosMsg);
                                    isEnglish = isEnglish.trim();
                                    PosMsg = PosMsg.trim();

                                    String output = "";

                                    //String output : store the raw type of the response from CS.
                                    if(isEnglish == "en") {
                                        output = csEN.processInput(PosMsg,origin_userId);
                                    } else if(isEnglish == "sc"){
                                        //if user enter special character, then response EN/KR
                                        if(userLocale.get(origin_userId).equals("ko_KR")|
                                                userLocale.get(origin_userId).equals("ko")|
                                                userLocale.get(origin_userId).equals("ko-KR")){
                                            PosMsg = "ㅁㄴㅇㄻㄴㅇㄻㄴㅇㄹ";
                                            output = cs.processInput(PosMsg,origin_userId);
                                        }else{
                                            PosMsg = "asdfasdfasdfasdfasdfasdfasdfasdf";
                                            output = csEN.processInput(PosMsg,origin_userId);
                                        }
                                    }else {
                                        output = cs.processInput(PosMsg,origin_userId);
                                    }
                                    //checkType : refine the raw type response from CS.
                                    checkType checkTYPE = new checkType(output);
                                    int typeNum = checkTYPE.getTypeNum();
                                    int btnNum = checkTYPE.getReportType();
                                    ArrayList<String> fileName = checkTYPE.getFileName();
                                    ArrayList<String> buttonTxt = checkTYPE.getBTxt();

                                    String finalAnswer = ""; // it will handle the response of the CS.
                                    if(checkTYPE.getAnswer().matches("B"+"[0-9]"+"*"+"\n") ){
                                        //This case is for coincide words like "Branch" "Useage Map"
                                           if(userLocale.get(origin_userId).equals("en-US")){
                                               checkType ct = new checkType(csEN.processInput(checkTYPE.getAnswer(), origin_userId));
                                               finalAnswer = ct.getAnswer();
                                           }else{
                                               checkType ct = new checkType(cs.processInput(checkTYPE.getAnswer(), origin_userId));
                                               finalAnswer = ct.getAnswer();
                                           }

                                    }else if(checkTYPE.getAnswer().equals("C1\n")){
                                        // This case is for the Confirmation part of the sending reports to the server.
                                        userReport.put(origin_userId, question);

                                        if(userLocale.get(origin_userId).equals("en-US") |
                                            userLocale.get(origin_userId).equals("en_US")){
                                            checkType reportConfirm = new checkType(csEN.processInput("C1",origin_userId));
                                            finalAnswer = reportConfirm.getAnswer();
                                            typeNum = reportConfirm.getTypeNum();
                                            buttonTxt.clear();
                                            buttonTxt=reportConfirm.getBTxt();
                                        }else{
                                            checkType reportConfirm = new checkType(cs.processInput("C1",origin_userId));
                                            finalAnswer = reportConfirm.getAnswer();
                                            typeNum = reportConfirm.getTypeNum();
                                            buttonTxt.clear();
                                            buttonTxt=reportConfirm.getBTxt();
                                        }
                                    }else{
                                        //Normal Pattern.
                                         if (isEnglish == "en" || isEnglish=="sc") {
                                            userLocale.put(origin_userId, "en-US");
                                            finalAnswer = checkTYPE.getAnswer();
                                        } else if (isEnglish == "kr"|| isEnglish=="sc") {
                                            userLocale.put(origin_userId, "ko-KR");
                                            finalAnswer = checkTYPE.getAnswer();
                                        }
                                    }

                                    jObj.put("body", finalAnswer);
                                    msgSend.setConversationId(jObj.get("recvConvoId").toString());

                                    if (typeNum < 6) {
                                        System.out.println("attach case ");
                                        attachJSON.jsonAttach(jObj, typeNum, fileName, buttonTxt,
                                                checkTYPE.getUrlMap(), checkTYPE.getUrlTxt());
                                    }


                                    //After attach or not, make json object to string and publish.
                                    String newJson = jObj.toString();
                                    if(checkTYPE.getReportType() != 3){
                                        msgSend.pubMsg(newJson, 0);
                                    }

                                    //This if/else statement will publish additional question sentences.
                                    //the additional sentence will be published except exception case.
                                    if(typeNum!=7 && typeNum != 5){

                                        if ((userLocale.get(origin_userId).equals("en-US"))){
                                            checkType newcheckType2 = new checkType(csEN.processInput("Q2",origin_userId));
                                            finalAnswer = newcheckType2.getAnswer();
                                            jObj2.put("body",finalAnswer);
                                        }else {
                                            checkType newcheckType2 = new checkType(cs.processInput("Q2",origin_userId));
                                            finalAnswer = newcheckType2.getAnswer();
                                            jObj2.put("body", finalAnswer);
                                        }

                                        newJson = jObj2.toString();
                                        msgSend.pubMsg(newJson,0);
                                    }

                                    if( checkTYPE.getReportType() == 1){

                                        JSONObject dummyObj = attachJSON.mailAttach(userReport.get(origin_userId),
                                                                                    origin_userId,userLocale.get(origin_userId),
                                                                           "feature_suggestion");
                                        LeftMenuSend.pubMail(dummyObj.toString());

                                    }else if(checkTYPE.getReportType() == 2){

                                        JSONObject dummyObj = attachJSON.mailAttach(userReport.get(origin_userId),
                                                                                    origin_userId,userLocale.get(origin_userId),
                                                                            "error_submission");
                                        LeftMenuSend.pubMail(dummyObj.toString());


                                    } else if (checkTYPE.getReportType() == 3) {

                                        String answer = checkTYPE.getAnswer();
                                        checkType newchecktype = new checkType(cs.processInput(answer,origin_userId));

                                        jObj.put("body",newchecktype.getAnswer());
                                        msgSend.pubMsg(jObj.toString(),1);
                                    }
                                }
                            }catch(TimeoutException e){
                            }catch(ParseException e){
                            }
                        }else if(exCheck[0].equals("event")) {
                            //EVENT CASE
                            String msg = new String(body, "UTF-8");
                            JSONParser parser = new JSONParser();
                            MsgSend msgSend = new MsgSend();
                            try {
                                JSONObject jObj = (JSONObject) parser.parse(msg);// {"senduser":"kth" ......}
                                JSONObject jObj2 = (JSONObject) parser.parse(jObj.get("payload").toString());

                                userLocale.put(jObj2.get("userId").toString(),
                                        jObj2.get("locale").toString());
                                String newBody;
                                if (("BOT_REOPEN".equals(jObj.get("type")))) {
                                    if (userLocale.get(jObj2.get("userId")).equals("ko-KR") |
                                            userLocale.get(jObj2.get("userId")).equals("ko")) {
                                        newBody = cs.processInput("null",jObj2.get("userId").toString());//Korean greeting
                                    } else {
                                        newBody = csEN.processInput("nul",jObj2.get("userId").toString()); // English greeting.
                                    }
                                } else {
                                    userLocale.put(jObj2.get("userId").toString(), jObj2.get("locale").toString());
                                    if (userLocale.get(jObj2.get("userId")).equals("ko-KR") |
                                            userLocale.get(jObj2.get("userId")).equals("ko")) {
                                        newBody = cs.processInput("null2",jObj2.get("userId").toString());
                                    } else {
                                        newBody = csEN.processInput("nul2",jObj2.get("userId").toString());
                                    }
                                }
                                createJSON newJSON = new createJSON();
                                JSONObject finalJSON = newJSON.createJOBJECT(jObj, jObj2);
                                msgSend.setConversationId(finalJSON.get("recvConvoId").toString());
                                //eventConID = finalJSON.get("recvConvoId").toString();

                                finalJSON.put("body", newBody);
                                String newJson = finalJSON.toString();
                                msgSend.pubMsg(newJson, 1);

                            } catch (TimeoutException e) {
                            } catch (ParseException e) {
                            }
                        }else {
                            // api case
                            String msg = new String(body,"UTF-8");
                            JSONParser parser = new JSONParser();

                            try{
                                JSONObject jObj = (JSONObject) parser.parse(msg);
                                JSONObject jObj2 = (JSONObject) parser.parse(jObj.get("payload").toString());

                                ArrayList<String> catalogs = new ArrayList<String>();
                                catalogs.add("FAQ");
                                catalogs.add("New Features");
                                catalogs.add("Notice");

                                ArrayList<String> catalogs_KR = new ArrayList<String>();
                                catalogs_KR.add("Wrapsody 사용법");
                                catalogs_KR.add("신규 및 주요 기능");
                                catalogs_KR.add("공지 사항");

                                if(exCheck[2].equals("catalog") && exCheck[3].equals("list")){
                                    if(jObj.get("locale").equals("ko_KR")|
                                        jObj.get("locale").equals("ko")){
                                        attachJSON.catalogAttach(jObj,jObj2,catalogs_KR);
                                        LeftMenuSend.pubMsg(jObj.toString(),properties);

                                    }else{
                                        attachJSON.catalogAttach(jObj,jObj2,catalogs);
                                        LeftMenuSend.pubMsg(jObj.toString(),properties);
                                    }

                                }
                                else if(exCheck[2].equals("command") && exCheck[3].equals("list")){
                                    attachJSON.commandAttach(jObj,jObj2);
                                    LeftMenuSend.pubMsg(jObj.toString(),properties);
                                }

                            }catch(ParseException e){
                            }catch(TimeoutException e){
                            }
                        } // api case end.
                    }
                });
    }

}
