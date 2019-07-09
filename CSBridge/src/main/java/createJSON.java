import java.util.*;
import org.json.simple.*;

public class createJSON {

    public static JSONObject createJOBJECT(JSONObject jj, JSONObject jjPayload){
        /*

        {
            "createdAt":1562574809315,
            "updatedAt":1562574809315,
            "messageId":3885,
            "sendUserId":"kth",
            "recvConvoId":"ef43070691e044bdb6faf4bfdb840ad5",
            "body":"하하",
            "messageType":0,
            "isNotice":false
         }

         */
        //{"type":"BOT_REOPEN","payload":{"convoId":"ef43070691e044bdb6faf4bfdb840ad5","locale":"en-US","userId":"kth"},"createdAt":1562575428883}

        JSONObject jobj = new JSONObject();
        System.out.println(jj.get("payload"));

        jobj.put("createdAt",jj.get("createdAt"));
        jobj.put("updatedAt",jj.get("createdAt"));
        jobj.put("messageID", 3885);
        jobj.put("sendUserId","@BOT@chatscript");
        jobj.put("recvConvoId",jjPayload.get("convoId"));
        jobj.put("body","");
        jobj.put("messageType",0);
        jobj.put("isNotice",false);


        return jobj;
    }
}
