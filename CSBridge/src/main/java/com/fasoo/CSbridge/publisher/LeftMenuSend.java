//////////////////////////////////////////////////////////////////////////
//    This class work similar as MsgSend.java                           //
//    make connection with Rabbitmq message server for publishing       //
//    message.                                                          //
//    Method:                                                           //
//        public static void pubMsg(String, AMQP.BasicProperties)       //
//            - this method call the method from the fileReader.java to //
//              get the data for connection with rabbitmq.              //
//              (the file name called "connnect.txt")                   //
//////////////////////////////////////////////////////////////////////////
package com.fasoo.CSbridge.publisher;
import com.fasoo.CSbridge.helper.*;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * Publish the message about the Left Menu and Sending emails
 */
public class LeftMenuSend {

    /**
     * Publish the message which contains the components of the Left side menus(Catalogs and Commands)
     * @param text String converted JSON Object
     * @param properties The properties of the message
     * @throws IOException handle exception
     * @throws TimeoutException handle exception
     */
    public static void pubMsg(String text, AMQP.BasicProperties properties) throws IOException, TimeoutException {

        //setting
        ConnectionFactory factory = new ConnectionFactory();

        factory.setNetworkRecoveryInterval(1000);
        factory.setHost(constant.Host);
        factory.setPort(constant.Port);
        factory.setVirtualHost(constant.VirtualHost);
        factory.setUsername(constant.Username);
        factory.setPassword(constant.Password);
        factory.setAutomaticRecoveryEnabled(true);

        Connection connection = factory.newConnection();
        final Channel channel = connection.createChannel();
        byte[] messageBodyBytes = text.getBytes();

        //Build msg Properties.
        Map messageProps = new HashMap();
        messageProps.put("__TypeId__",constant.LMenuPropoerty);

        AMQP.BasicProperties.Builder basicProperties = new AMQP.BasicProperties.Builder();
        basicProperties.priority(0).deliveryMode(2).replyTo(properties.getReplyTo())
                .headers(messageProps)
                .contentEncoding("UTF-8")
                .correlationId(properties.getCorrelationId())
                .contentType("application/json").build();

        channel.basicPublish("",properties.getReplyTo(),basicProperties.build(),messageBodyBytes);
    }

    /**
     * Publish the message which contains the format of Sending emails to the administrator
     * @param text String converted JSON Object
     * @throws IOException handle exception
     * @throws TimeoutException handle exception
     */
    public static void pubMail(String text)throws IOException, TimeoutException {
        //setting
        ConnectionFactory factory = new ConnectionFactory();
        factory.setNetworkRecoveryInterval(1000);
        factory.setHost(constant.Host);
        factory.setPort(constant.Port);
        factory.setVirtualHost(constant.VirtualHost);
        factory.setUsername(constant.Username);
        factory.setPassword(constant.Password);
        factory.setAutomaticRecoveryEnabled(true);
        Connection connection = factory.newConnection();

        final Channel channel = connection.createChannel();
        byte[] messageBodyBytes = text.getBytes();

        //Build msg Properties.
        Map messageProps = new HashMap();

        messageProps.put("__TypeId__",constant.MailProperty);


        AMQP.BasicProperties.Builder basicProperties = new AMQP.BasicProperties.Builder();
        basicProperties.priority(0).deliveryMode(2)
                .headers(messageProps)
                .contentEncoding("UTF-8")
                .contentType("application/json").build();

        channel.basicPublish("request",constant.MailRoutingKey,basicProperties.build(),messageBodyBytes);

    }

}
