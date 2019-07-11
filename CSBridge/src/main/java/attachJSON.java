import org.json.simple.*;

public class attachJSON {

    /*    [append the "attachments" part into the original json file before publish.]

           This method will return the JSONObject after append the attachment part.
           - Parameters
           -> JSONObject jobj : this comes from the MsgCatch.java before program call the publish method.
           -> int typeNum     : this int variable shows the type of the attachment.
                                (0 = file, 1=img, 2=video, 3=sound, 4=scrap, 5=action)
           -> int btnNum      : this variable will used only the case "typeNum" is 5(button).
                                therefore, it contains how many buttons required.
           -> String imgName  : this variable has the name of the img file. if the name is 0 then there is no
                                img required.
    */
    public static void jsonAttach(JSONObject jobj, int typeNum, int btnNum, String fileName){

        JSONObject dummy = new JSONObject();

        JSONArray arr = new JSONArray();
        JSONArray payloadArr = new JSONArray();
        /*
           jobj
               - arr
                   -dummy ( attachments)
                     -payloadDummy ( attachment payload )
         */

        //Button & img case
        if(typeNum==5 && typeNum == 1){

        }else if (typeNum==5){//Button only

            int i = 0;
            dummy.put("attachmentType",typeNum);
            String valueArr[] = jobj.get("body").toString().split("\n");

            while(i<btnNum){
                JSONObject payloadDummy = new JSONObject();
                payloadDummy.put("value",valueArr[i]);
                payloadDummy.put("text",valueArr[i]);
                payloadDummy.put("type","button");
                payloadDummy.put("action","command");
                payloadArr.add(payloadDummy);
                i++;
            }



            dummy.put("payload",payloadArr);
            arr.add(dummy);
            jobj.put("attachments",arr);

        }else{

            // file only case.(no button) //

            // 7/10 -> image handle case test.
            /*[]표시는 optional.
            "attachments"
            [
            {
                "attachmentType"   :<attachment Type>,
                ["title"           :<메세지 제목>,]
                ["uri"             :<attachment type별 uri>,]
                ["payload"         :<attachment object>,]
                ["attachmentOrder" :<attachment 표시 순서>]
            }
            ]
            */
            System.out.println("here type number ");
            dummy.put("attachmentType",typeNum);

            dummy.put("uri","/bot/wrapsody/"+fileName);

            // put dummy object into json array "arr"
            arr.add(dummy);
            //put json array into the original json object
            jobj.put("attachments",arr);

        }


    }
}
