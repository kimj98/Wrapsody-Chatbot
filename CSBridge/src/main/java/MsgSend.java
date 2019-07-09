
/*
This class is used to send(Push) the message to the RabbitmQ's specific Queue.

FuturePlan, Send Back to the "request" exchange after POS Tagged.


 */

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import org.json.simple.*;

public class MsgSend {

    private static final String Q_NAME = "bot-chatscript";
    private static final String R_Key = "#.user.@BOT@chatscript";

    public static void pubMsg(String text,int flag) throws IOException, TimeoutException {
        //connect with the rabbitMQ

        //setting
        ConnectionFactory factory = new ConnectionFactory();


        fileReader textread = new fileReader();

        String[] saveLine = textread.readfiles();

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


        //SEND MESSAGE(PUBLISH)
        byte[] messageBodyBytes = text.getBytes();

        /*
        priority:	0
        delivery_mode:	2
        headers:__TypeId__:	com.wrapsody.messaging.model.Message
        content_encoding:	UTF-8
        content_type:	application/json
         */
        //Build msg Properties.
        Map messageProps = new HashMap();
        messageProps.put("__TypeId__","com.wrapsody.messaging.model.Message");


        //Set message Basic properties. ( 이건 event 상관없이 same setting)
        AMQP.BasicProperties.Builder basicProperties = new AMQP.BasicProperties.Builder();
        basicProperties.priority(0).deliveryMode(2)
                .headers(messageProps)
                .contentEncoding("UTF-8")
                .contentType("application/json");

        //System.out.println("text is :" +text);
        if(flag == 1){
            // Receive Convo ID 대신 convoID 의 value를 가져와야함.
            channel.basicPublish("request",MsgCatch.sendConvo2(),basicProperties.build(),messageBodyBytes);
        }else{

            channel.basicPublish("request",MsgCatch.sendConvo(),basicProperties.build(),messageBodyBytes);

            System.out.println("POST : "+ text);
        }

        //channel.close();
        //connection.close();
    }
}
