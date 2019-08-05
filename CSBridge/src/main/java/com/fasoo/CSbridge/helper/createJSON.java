//////////////////////////////////////////////////////////////////////////////////
//  This class make the new JSONObject and put some components and return it.   //
//  Method:                                                                     //
//      public static JSONObject createJOBJECT(JSONObject,JSONObject)           //
//      -@Param:                                                                //
//          -JSONObject jj       : this variable is the main JSON object.       //
//          -JSONObject jjPayload: this variable has the payload part of the jj.//
//////////////////////////////////////////////////////////////////////////////////
package com.fasoo.CSbridge.helper;
import org.json.simple.*;

/**
 * Create JSON Object to attach the required components.
 */
public class createJSON {

    /**
     * Create new JSON Object with the data from exist JSON Object.
     * @param jj received JSON Object
     * @param jjPayload received JSON Object's payload part
     * @return New JSON Object with new required components
     */
    public static JSONObject createJOBJECT(JSONObject jj, JSONObject jjPayload){

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
