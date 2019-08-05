/*
This class is used to send(Push) the message to the RabbitmQ's specific Queue.


 */
package com.fasoo.CSbridge.publisher;

import com.fasoo.CSbridge.helper.*;
import com.fasoo.CSbridge.receiver.*;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * Publish the message to the User or Bot
 */
public class MsgSend {

    private static final String Q_NAME = "bot-chatscript";
    private static final String R_Key = "#.user.@BOT@chatscript";
    private static  String conversationId = "";

    /**
     * Publish the message which contains the words to pop up into the Chat-room.
     * @param text String converted JSON Object
     * @param flag this int value will be 0 or 1 \n( 0: sender is User, 1: sender is Bot )
     * @throws IOException handle exception
     * @throws TimeoutException handle exception
     */
    public static void pubMsg(String text,int flag) throws IOException, TimeoutException {
        //setting
        ConnectionFactory factory = new ConnectionFactory();

        fileReader textread = new fileReader();
        String[] saveLine = textread.readfiles();
        //connect with the rabbitMQ
        //attempt recovery every 5 sec
        factory.setNetworkRecoveryInterval(1000);
        factory.setHost(saveLine[0]);
        factory.setPort(Integer.parseInt(saveLine[1]));
        factory.setVirtualHost(saveLine[2]);
        factory.setUsername(saveLine[3]);
        factory.setPassword(saveLine[4]);
        factory.setAutomaticRecoveryEnabled(true);

        Connection connection = factory.newConnection();
        final Channel channel = connection.createChannel();

        // Queue-> bot-ChatScript 이름의 큐 생성.
        channel.queueDeclare(Q_NAME,true,false,false,null);

        channel.queueBind(Q_NAME,"user",R_Key); /*   Exchange -> user 는 이미 있으니 생성 할 필요x
                                                            그러니 queue binding 만 하면 된다.
                                                            Routing Key 는 위에 정의됨
                                                       */
        byte[] messageBodyBytes = text.getBytes();

        //Build msg Properties.
        Map messageProps = new HashMap();
        messageProps.put("__TypeId__","com.wrapsody.messaging.model.Message");

        //Set message Basic properties. ( 이건 event 상관없이 same setting)
        AMQP.BasicProperties.Builder basicProperties = new AMQP.BasicProperties.Builder();
        basicProperties.priority(0).deliveryMode(2)
                .headers(messageProps)
                .contentEncoding("UTF-8")
                .contentType("application/json").build();

        if(flag == 1){
            // Receive Convo ID 대신 convoID 의 value를 가져와야함.
            channel.basicPublish("request",conversationId,basicProperties.build(),messageBodyBytes);
        }else{
            channel.basicPublish("request",conversationId,basicProperties.build(),messageBodyBytes);
        }
    }

    /**
     * Setup a new conversation ID ( routing key )
     * @param convoId String value of new routing key
     */
    public void setConversationId(String convoId){
        this.conversationId ="chat.bot.convo."+convoId;
    }
}
