import com.rabbitmq.client.*;


import java.io.IOException;
import java.util.concurrent.TimeoutException;


public class MsgCatch {

    private static final String Q_NAME = "bot-Chatscript";
    private static final String R_Key = "#.user.@BOT@CHATSCRIPT";
    public static String rePostMsg="";

    public static void main(String[] args) throws IOException, TimeoutException{

        //FOR TESTING PURPOSE : POST the msg into the Queue
        String myText ="문서";
        byte[] tempByte = myText.getBytes();
        String newText = new String(tempByte,"UTF-8");
        MsgSend.pubMsg(newText);


        //setting
        ConnectionFactory factory = new ConnectionFactory();

        //attempt recovery every 5 sec
        factory.setNetworkRecoveryInterval(1000);

        factory.setHost("192.168.100.30");
        factory.setPort(9501);
        factory.setVirtualHost("/wrapsody-oracle");
        factory.setUsername("wrapsody");
        factory.setPassword("Wrapsody.1");

        factory.setAutomaticRecoveryEnabled(true);

        //For repost

        //RECEIVING
        Connection connection2 = factory.newConnection();
        final Channel channel2 = connection2.createChannel();

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
                        String contentType= properties.getContentType();
                        long deliveryTag  = envelope.getDeliveryTag();
                        //process the message components here ...)
                        channel2.basicAck(deliveryTag, false);
                        String msg = new String(body,"UTF-8");

                        System.out.println("you got "+ msg);
                        String PosMsg = PosFilter.filtering(msg);
                        System.out.println("After tagging "+ PosMsg);

                        chatscript cs = new chatscript();
                        System.out.print("Answer:"+cs.processInput(PosMsg));

                        //이 안에서 POST 작업까지.
                       /*
                       rePostMsg=PosMsg;
                       System.out.println("Debug1 " + rePostMsg);
                       */

                    }
                });
        //////////////////////////////////////////////////////////////////
        //MsgSend.pubMsg(rePostMsg);
        //System.out.println("Debug2 " + rePostMsg);
        //Repost after POS TAGGING
        //MsgSend.pubMsg(rePostMsg);

        // channel2.close();
        //connection2.close();

    }
}
