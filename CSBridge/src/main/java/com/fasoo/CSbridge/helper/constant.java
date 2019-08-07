package com.fasoo.CSbridge.helper;

public class constant {

    //[ RabbitmQ Connection ]
    public static final String Host = "192.168.100.30";
    public static final int Port = 9501;
    public static final String VirtualHost = "/wrapsody-oracle";
    public static final String Username = "wrapsody";
    public static final String Password = "Wrapsody.1";

    //[ Comment Reader.java part ]
    public static final String KrFile = "../CSBridge/WrapsodyUserGuide.top";
    public static final String EnFile = "../CSBridge/WrapsodyUserGuide_en.top";

    //[MsgCatch.java  & MsgSend.java part ]
    public static final String QueueName = "bot-chatscript";
    public static final String MSRoutingkey = "#.user.@Bot@chatscript";
    public static final String MSProperty = "com.wrapsody.messaging.model.Message";

    //[Left Menu Send Part ]
    public static final String LMenuPropoerty = "com.wrapsody.messaging.model.ApiResponse";
    public static final String MailRoutingKey = "api.admin.mail.send";
    public static final String MailProperty = "com.wrapsody.messaging.model.ApiRequest";
}
