package com.fasoo.CSbridge.helper;

public class constant {

    // 2019.8.12 : fix the info for security
    //[ RabbitmQ Connection ]
    public static final String Host = "xxx.xxx.xx.xx";
    public static final int Port = 0001;
    public static final String VirtualHost = "/Virtual-HostName";
    public static final String Username = "DummyName";
    public static final String Password = "DummyPW";

    //[ Comment Reader.java part ]
    public static final String KrFile = "../CSBridge/KoreanVersion.top";
    public static final String EnFile = "../CSBridge/EngVersion.top";

    //[MsgCatch.java  & MsgSend.java part ]
    public static final String QueueName = "testQueueName";
    public static final String MSRoutingkey = "#.user.@Bot@chatscript";
    public static final String MSProperty = "PropertyNameHere";

    //[Left Menu Send Part ]
    public static final String LMenuPropoerty = "Property_Name_Here";
    public static final String MailRoutingKey = "Api_Routing_Key";
    public static final String MailProperty = "Api_Routing_Key";
}
