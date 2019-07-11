import com.rabbitmq.client.*;

import javafx.geometry.Pos;
import org.json.simple.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


import java.io.IOException;

import java.util.concurrent.TimeoutException;

import static com.sun.javafx.util.Utils.split;


public class MsgCatch {

    private static final String Q_NAME = "bot-chatscript";
    private static final String R_Key = "#.user.@BOT@chatscript";
    public static String rePostMsg="";
    public static String convoID="";
    public static String eventConID="";

    public static void main(String[] args) throws IOException, TimeoutException{

        //FOR TESTING PURPOSE : POST the msg into the Queue
        /*
        String myText ="문서";
        byte[] tempByte = myText.getBytes();
        String newText = new String(tempByte,"UTF-8");
        MsgSend.pubMsg(newText);
        */

        //setting
        ConnectionFactory factory = new ConnectionFactory();


        //For repost
        fileReader textread = new fileReader();
        String[] saveLine = textread.readfiles();
        factory.setNetworkRecoveryInterval(1000);
        factory.setHost(saveLine[0]);
        factory.setPort(Integer.parseInt(saveLine[1]));
        factory.setVirtualHost(saveLine[2]);
        factory.setUsername(saveLine[3]);
        factory.setPassword(saveLine[4]);
        factory.setAutomaticRecoveryEnabled(true);

        //RECEIVING
        final Connection connection2 = factory.newConnection();
        final Channel channel2 = connection2.createChannel();
        channel2.queueDeclare(Q_NAME,true,false,false,null);
        channel2.queueBind(Q_NAME,"user",R_Key);
        boolean autoAck =false;
        //while loop -> 다받고 process진행
        channel2.basicConsume(Q_NAME, autoAck, "myConsummerTag",
                new DefaultConsumer(channel2) {
                    @Override
                    public void handleDelivery(String consumerTag,
                                               Envelope envelope,
                                               AMQP.BasicProperties properties,
                                               byte[] body) throws IOException
                    {
                        String routingKey = envelope.getRoutingKey();
                        System.out.println("routing Key is : " +routingKey);
                        String[] exCheck = split(routingKey,".");
                        chatscript cs = new chatscript();
                        String contentType= properties.getContentType();
                        long deliveryTag  = envelope.getDeliveryTag();
                        //process the message components here ...)
                        channel2.basicAck(deliveryTag, false);

                        System.out.println("before if 0 : "+ exCheck[0]);

                        // event 케이스가 아닐때.
                        if(!(exCheck[0].equals("event"))){

                            String msg = new String(body,"UTF-8");

                            /*
                               Use the JSON Parser class to parsing the json type input.
                               String question : store the "Body" key value of the JSon type.
                            */
                            JSONParser parser = new JSONParser();
                            String question = cs.myJsonParser(msg);

                            //Send the POS filter to pos tagging.
                            String PosMsg = PosFilter.filtering(question);
                            System.out.println("Debug1");

                            try{
                                JSONObject jObj = (JSONObject) parser.parse(msg); // {"senduser":"kth" ......}

                                // this if statement prevent to read the bot's respond msg.
                                if(!( "@BOT@chatscript".equals((String)jObj.get("sendUserId")))){
                                    jObj.put("sendUserId","@BOT@chatscript");

                                    /*
                                       make the CheckType object to get the question without the numbers
                                       get numbers for get the type number of the attachment
                                                       get button number of the attachment
                                                       and get the name of the image if it has.
                                   */
                                    System.out.println("Debug2: bot respond");
                                    CheckType checkType = new CheckType(cs.processInput(PosMsg));

                                    int typeNum = checkType.getTypeNum();
                                    System.out.println("Debug3: after checktype use");
                                    int btnNum = checkType.getBtnNum();
                                    String imgName = checkType.getImgName();


                                    jObj.put("body",checkType.getAnswer());
                                    System.out.println("After get answer from the Checktype");
                                    convoID = (String)jObj.get("recvConvoId");
                                    if(typeNum<6) {
                                        attachJSON.jsonAttach(jObj, typeNum, btnNum, imgName);
                                    }
                                    //System.out.println("TypeNumber is : " + convoID);
                                    String newJson = jObj.toString();

                                    MsgSend.pubMsg(newJson,0);          // 문서를열람하고싶으시다면.... 답변.
                                }else{
                                    System.out.println("you are here ass");
                                }
                            }catch(TimeoutException e){
                                //.setStackTrace();
                            }catch(ParseException e){

                            }
                        }else{
                            //EVENT CASE

                            System.out.println("inside else");
                            String msg = new String(body,"UTF-8");

                            //Debug : check the Received Message
                            //System.out.println("you got "+ msg);

                            /*
                               Use the JSON Parser class to parsing the json type input.
                               String question : store the "Body" key value of the JSon type.
                            */
                            JSONParser parser = new JSONParser();
                            String question = cs.myJsonConvParser(msg);


                            try{
                                JSONObject jObj = (JSONObject) parser.parse(msg);// {"senduser":"kth" ......}
                                JSONObject jObj2 = (JSONObject) parser.parse(jObj.get("payload").toString());


                                if(( "BOT_REOPEN".equals((String)jObj.get("type")))){


                                    String newBody = cs.processInput("null");
                                    createJSON newJSON = new createJSON();
                                    JSONObject finalJSON = newJSON.createJOBJECT(jObj,jObj2);
                                    eventConID = finalJSON.get("recvConvoId").toString();



                                    System.out.println("new convoID : " + finalJSON.get("recvConvoId"));

                                    finalJSON.put("body",newBody);

                                    String newJson = finalJSON.toString();




                                    MsgSend.pubMsg(newJson,1);          // 문서를열람하고싶으시다면.... 답변.
                                }else{
                                    String newBody = cs.processInput("null2");
                                    createJSON newJSON = new createJSON();
                                    JSONObject finalJSON = newJSON.createJOBJECT(jObj,jObj2);
                                    eventConID = finalJSON.get("recvConvoId").toString();

                                    System.out.println("new convoID : " + finalJSON.get("recvConvoId"));

                                    finalJSON.put("body",newBody);

                                    String newJson = finalJSON.toString();

                                    MsgSend.pubMsg(newJson,1);          // 문서를열람하고싶으시다면.... 답변.
                                }
                            }catch(TimeoutException e){
                                //.setStackTrace();
                            }catch(ParseException e){

                            }

                        }

                        //이 안에서 POST 작업까지.
                       /*
                       rePostMsg=PosMsg;
                       System.out.println("Debug1 " + rePostMsg);
                       */

                    }
                });

    }
    public static String sendConvo2(){
        String convo = "chat.bot.convo."+eventConID;
        return convo;
    }
    public static String sendConvo(){
        String ss ="chat.bot.convo."+convoID;
        return ss;
    }
}
