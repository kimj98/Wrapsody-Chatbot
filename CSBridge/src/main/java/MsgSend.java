
/*
This class is used to send(Push) the message to the RabbitmQ's specific Queue.

FuturePlan, Send Back to the "request" exchange after POS Tagged.


 */

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class MsgSend {

    private static final String Q_NAME = "bot-Chatscript";
    private static final String R_Key = "#.user.@BOT@CHATSCRIPT";

    public static void pubMsg(String text) throws IOException, TimeoutException {
        //connect with the rabbitMQ

        //setting
        ConnectionFactory factory = new ConnectionFactory();

        //attempt recovery every 5 sec
        factory.setNetworkRecoveryInterval(5000);

        factory.setHost("192.168.100.30");
        factory.setPort(9501);
        factory.setVirtualHost("/wrapsody-oracle");
        factory.setUsername("wrapsody");
        factory.setPassword("Wrapsody.1");

        factory.setAutomaticRecoveryEnabled(true);

        Connection connection = factory.newConnection();
        final Channel channel = connection.createChannel();


        // Queue-> bot-ChatScript 이름의 큐 생성.
        channel.queueDeclare(Q_NAME,true,false,false,null);

        channel.queueBind(Q_NAME,"request",R_Key); /*   Exchange -> request 이미 있으니 생성 할 필요x
                                                            그러니 queue binding 만 하면 된다.
                                                            Routing Key 는 위에 정의됨
                                                       */

        //SEND MESSAGE(PUBLISH)
        byte[] messageBodyBytes = text.getBytes();
        channel.basicPublish("request",R_Key,null,messageBodyBytes);
        System.out.println("POST : "+ text);
        //channel.close();
        //connection.close();
    }
}
