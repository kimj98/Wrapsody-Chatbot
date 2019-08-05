//////////////////////////////////////////////////////////////////////////////
// This class is attaching the appropriate data into the JSON file.         //
//                                                                          //
//     Methods:                                                             //
//         jsonAttach():                                                    //
//            -This method figured out the type of the response from the CS.//
//             Get the ArrayList which contains the number of the buttons   //
//             attach them into the JSONObject and return it.               //
//         catalogAttach():                                                 //
//            -This method also get the name of the catalogs and attach them//
//             into the JSON and return it.                                 //
//         commandAttach():                                                 //
//            -The commands means the sub-menus of each catalogs.           //
//             attach them into the JSON and return it.                     //
//         mailAttach():                                                    //
//            -This method get the locale , mail type and others for attach //
//             the required info into the JSON to send the email.           //
//                                                                          //
//////////////////////////////////////////////////////////////////////////////

package com.fasoo.CSbridge.helper;

import org.json.simple.*;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * This class is attaching the appropriate data into the JSON file.
 *
 **/
public class attachJSON {
    /**
     * This method add one or more attachments into the received JSON Object.
     * @param jobj modifying JSON Object from the msgCatch.java
     * @param typeNum has the type number of the response of CS
     * @param fileName has the list of the file names
     * @param btnArr has the info about the buttons
     * @param urlMap has the urls
     * @param urlName has the name of the url
     */
    public static void jsonAttach(JSONObject jobj, int typeNum,
                                  ArrayList<String> fileName,
                                  ArrayList<String> btnArr,
                                  HashMap<String,String> urlMap,ArrayList<String> urlName){
        //Button only
        if (typeNum==5 ){

            JSONObject dummy = new JSONObject();
            JSONArray arr = new JSONArray();
            JSONArray payloadArr = new JSONArray();
            int i = 0;

            dummy.put("attachmentType",typeNum);
            while(i<btnArr.size()){
                JSONObject payloadDummy = new JSONObject();
                payloadDummy.put("value",btnArr.get(i));
                payloadDummy.put("text",btnArr.get(i));
                payloadDummy.put("type","button");
                payloadDummy.put("action","command");
                payloadArr.add(payloadDummy);
                i++;
            }

            i=0;
            while(i<urlMap.size()){
                JSONObject payloadDummy = new JSONObject();
                payloadDummy.put("value",urlMap.get(urlName.get(i)));
                payloadDummy.put("text",urlName.get(i));
                payloadDummy.put("type","button");
                payloadDummy.put("action","link");
                payloadArr.add(payloadDummy);
                i++;
            }
            dummy.put("payload",payloadArr);
            arr.add(dummy);
            jobj.put("attachments",arr);

        }else{
            // File only case ( no button)//
            JSONArray arr = new JSONArray();
            int i = 0;
            while(i<fileName.size()){
                JSONObject dummy = new JSONObject();
                dummy.put("attachmentType",typeNum);
                dummy.put("uri","/bot/wrapsody/"+fileName.get(i));
                dummy.put("attachmentOrder",i);
                // put dummy object into json array "arr"
                arr.add(dummy);
                i++;
            }
            //put json array into the original json object
            jobj.put("attachments",arr);
        }
    }

    //Catalog list 요청에 대한 JSON Object 의 payload 부분에 대한 attachment.

    /**
     * This method attach one or more catalog menus into the received JSON Object.
     * @param jobj received JSON Object
     * @param jobjPayLoad received JSON Object's payload part
     * @param catList has the list of the catalogs' name
     */
    public static void catalogAttach(JSONObject jobj, JSONObject jobjPayLoad, ArrayList<String> catList){
        JSONArray jarr = new JSONArray();

        int listCount =0;
        while(listCount < catList.size()){
            JSONObject dummy = new JSONObject();
            dummy.put("botUserId",jobjPayLoad.get("botUserId"));
            dummy.put("name",catList.get(listCount));
            dummy.put("groupId",listCount);
            dummy.put("catalogOrder",listCount);
            jarr.add(dummy);
            listCount++;
        }
        jobj.put("isSuccess",true);
        jobj.put("resultCode",0);
        jobj.put("payload",jobjPayLoad.get("botUserId"));
        JSONObject jobjBotGroup = new JSONObject();
        jobjBotGroup.put("BotIntentGroup",jarr);
        jobj.put("payload",jobjBotGroup);
    }

    /**
     * This method attach one or more list of the commands into the received JSON Object
     * @param jobj received JSON Object
     * @param payLoadObj received JSON Object's payload part
     */
    public static void commandAttach(JSONObject jobj, JSONObject payLoadObj){

        commentReader cr = new commentReader();
        cr.readfiles(jobj);
        ArrayList<String> faqCommand = cr.getFAQList();
        ArrayList<String> newCommand = cr.getNewList();
        ArrayList<String> notice     = cr.getNoticeList();

        JSONArray newArr    = new JSONArray();
        JSONArray faqArr    = new JSONArray();
        JSONArray noticeArr = new JSONArray();

        if(payLoadObj.get("groupId").toString().equals("0")){

            for(int i = 0; i < faqCommand.size(); i++) {
                JSONObject dummy = new JSONObject();
                dummy.put("botUserId",payLoadObj.get("botUserId"));
                dummy.put("command", faqCommand.get(i));
                dummy.put("catalogOrder",i);
                faqArr.add(dummy);
            }
            JSONObject cmdBot = new JSONObject();
            cmdBot.put("botUserId",payLoadObj.get("botUserId"));
            cmdBot.put("groupId",payLoadObj.get("groupId"));
            cmdBot.put("BotCommands",faqArr);
            jobj.put("payload",cmdBot);

        }else if(payLoadObj.get("groupId").toString().equals("1")){
            for(int i = 0; i < newCommand.size(); i++) {
                JSONObject dummy = new JSONObject();
                dummy.put("botUserId",payLoadObj.get("botUserId"));
                dummy.put("command", newCommand.get(i));
                dummy.put("catalogOrder",i);
                newArr.add(dummy);
            }
            JSONObject cmdBot = new JSONObject();
            cmdBot.put("botUserId",payLoadObj.get("botUserId"));
            cmdBot.put("groupId",payLoadObj.get("groupId"));
            cmdBot.put("BotCommands",newArr);
            jobj.put("payload",cmdBot);
        }else if(payLoadObj.get("groupId").toString().equals("2")){
            for(int i = 0; i < notice.size(); i++) {
                JSONObject dummy = new JSONObject();
                dummy.put("botUserId",payLoadObj.get("botUserId"));
                dummy.put("command", notice.get(i));
                dummy.put("catalogOrder",i);
                noticeArr.add(dummy);
            }
            JSONObject cmdBot = new JSONObject();
            cmdBot.put("botUserId",payLoadObj.get("botUserId"));
            cmdBot.put("groupId",payLoadObj.get("groupId"));
            cmdBot.put("BotCommands",noticeArr);
            jobj.put("payload",cmdBot);
        }
        jobj.put("isSuccess",true);
        jobj.put("resultCode",0);
        //prevent cumulative data
        faqCommand.clear();
        newCommand.clear();
        notice.clear();
    }

    /**
     * This method attach some info about the mail (sender, locale, mail type, user's input)
     * @param userInput has user's input
     * @param userID has user's ID
     * @param userLocale has user's Locale
     * @param mailType mail type can be (feature_suggestion | error_submission)
     * @return return the JSON Object which contains info about the mail
     */
    public static JSONObject mailAttach(String userInput,String userID,String userLocale,String mailType){
        JSONObject jobj = new JSONObject();
        JSONObject jjPayload = new JSONObject();
        //json payload.

        jjPayload.put("body", userInput);
        jjPayload.put("userId", userID);
        jjPayload.put("type", mailType);

        //main json
        jobj.put("sender",userID);
        jobj.put("locale",userLocale);
        jobj.put("payload",jjPayload);

        return jobj;
    }
}
