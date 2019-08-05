////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Every output string value from chatscript returns a response text with syntax like the following:                  //
//                                                                                                                    //
//      Do you want to know how to send a download link to share a Wrapsody document?\n                               //
//      type:1 btnnum:0 attachflag:0 fileName:14.png\n,                                                               //
//                                                                                                                    //
// This string value is obtained when cs.processinput() is called with a string value as an input. This will          //
// return a string value like above which results from pattern matching in chatscript server using the input string.  //
//                                                                                                                    //
// Checktype will use that output string value as a parameter and store its nesssary features as                      //
// instance variables so that the programmer can easily obtain those variables .                                      //
//                                                                                                                    //
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
package com.fasoo.CSbridge.helper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;


/**
 * This class classify the response of the ChatScript.
 */
public class checkType {

    /**Has the type of the Answer (0~5) */
    private int typeNum;
    /**Has the response from CS without tags and commands*/
    private String nobuttonTxt = "";
    /**Has the list of the name of the url button*/
    private ArrayList<String> urlTxt= new ArrayList<String>();
    /**Has the set of name and url*/
    private HashMap<String,String> url = new HashMap();
    /**Has the list of name of the Button name*/
    private ArrayList<String> buttonTxt;
    /**Has the Report Type as a Number*/
    private int reportNum;
    /**Has the list of the file names*/
    private ArrayList<String> fileName;
    /**Has the whole line of the Tag part from the Response*/
    private String tags;

    /**
     * Classify the response from the ChatScript
     * @param csAns String value obtained directly from chatscript server after pattern matching.
     **/
    public checkType(String csAns){
        String temp="";
        buttonTxt = new ArrayList<String>();
        String arr[] = csAns.split("\n");
        int length = arr.length;
        tags = arr[length - 1];
        //substring the original answer.
        for(int i = 0; i < arr.length - 1; i++){
            temp+=arr[i]+"\n";
        }

        String temparr[] = temp.split("\n"); //화면에 나올 text들 버튼 text/버튼표시 포함됨
        ArrayList<Integer> buttonlocation = new ArrayList<Integer>();
        ArrayList<Integer> nobuttonlocation =  new ArrayList<Integer>();
        int check = 0;
        for(int i = 0; i < temparr.length; i++) {
            for(int j = 0; j < temparr[i].length()-1; j++) { // -1 because j+1 is called on the 2nd last (if j+1 is called and j is the last then error)
                if(temparr[i].charAt(j) == '(' && temparr[i].charAt(j+1) == '(' ) {
                    buttonlocation.add(i);
                    check=0;
                    break;
                }
                check +=1;
                if (check == temparr[i].length()-1) {
                    nobuttonlocation.add(i);
                    check=0;
                }
            }
        }

        for(int i = 0; i < nobuttonlocation.size(); i++){
            nobuttonTxt+=temparr[nobuttonlocation.get(i)]+"\n";
        }

        for(int i = 0; i < buttonlocation.size(); i++){
            System.out.println(filterbuttontype(temparr[(buttonlocation.get(i))]).toLowerCase());
            if(!(filterbuttontype(temparr[(buttonlocation.get(i))]).toLowerCase().equals("command"))) {
                String key = filterbutton(temparr[(buttonlocation.get(i))]);
                String link = filterbuttontype(temparr[(buttonlocation.get(i))]);
                urlTxt.add(key);
                url.put(key,link);
            } else {
                buttonTxt.add(filterbutton(temparr[(buttonlocation.get(i))]));

            }
        }
    }
    /**
     * Return the response of the CS except tags,buttons.
     * @return a response of the CS.
     **/
    public String getAnswer() {
        return nobuttonTxt;
    }
    /**
     * return the list of the buttons' text
     * @return String value which will be the text that will appear as a button.
     **/
    public ArrayList getBTxt() {
        return buttonTxt;
    }
    /**
     * Return the list of the url links and name of the url.
     * @return Hashmap containing url links
     **/
    public HashMap getUrlMap() {
        return url;
    }
    /**
     * Return the list of URL's name
     * @return ArrayList which contains txt that will direct user to certain url.

     **/
    public ArrayList getUrlTxt() {
        return urlTxt;
    }

    /**
     * Return the Type of the response from the CS.
     * @return Integer which corresponds to the type number (type of responses)pear in the chabot UI.
     **/
    public int getTypeNum(){
        typeNum = Integer.parseInt(tagger(tags,1).get(0));
        return typeNum;
    }
    /**
     * Return the Report Type of the response from the CS.
     * @return Integer which corresponds to a report number.
     **/
    public int getReportType(){
        reportNum = Integer.parseInt(tagger(tags,2).get(1));

        return reportNum;
    }
    /**
     * Return the list of the file names.
     * @return ArrayList with name of a files
     **/
    public ArrayList<String> getFileName(){
        fileName = tagger(tags,4);
        return fileName;
    }
    /**
     * Find the tags of the response from the CS and return them as a ArrayList.
     * @param input has the line of the tag from CS response script.
     * @param flag has 1 to 4 ( 1: type number, 2: report type, 3: attachmentFlag, 4: file name)
     * @return Array that helps find different tags
     *
     **/
    public static ArrayList<String> tagger(String input, int flag) {
        String extract = "";
        ArrayList<String> answer = new ArrayList();
        if (flag < 4) {
            int pivot = 0;
            for (int i = 0; i < input.length(); i++) {
                if (input.charAt(i) >= 48 && input.charAt(i) <= 57) {
                    pivot += 1;
                    extract = String.valueOf(input.charAt(i));
                    answer.add(extract);
                    if (pivot == flag) {
                        break;
                    }
                }
            }
        } else {
            int check = 0;
            int startpoint = 0;
            for (int i = 0; i < input.length(); i++) {
                if (input.charAt(i) == ':') {
                    check += 1;
                    startpoint = i;
                    if (check == 4) {
                        break;
                    }
                }
            }
            String filtered = input.substring(startpoint + 1);
            filtered = filtered.trim();
            String filtering[] = filtered.split(" ");
            for(int i = 0; i < filtering.length; i++) {
                answer.add(filtering[i]);
            }
        }
        return answer;

    }
    /**
     * Find the "button/ type" pattern from the Scrypt. ( Type: command | link )
     * @param input has the response Script from the CS.
     * @return String with filtered buttons
     **/
    public static String filterbutton(String input) {
        int splitlocation = 0;
        for(int i = 0; i < input.length() - 1; i++) {
            if (input.charAt(i) == '(' && input.charAt(i + 1) == '(') {
                splitlocation = i;
                break;
            }
        }
        return input.substring(0,splitlocation).trim(); // sfdfs [[bi
    }
    /**
     * Check the user's input is Korean,English,or Special Characters
     * @param  input has the user's input
     * @return String value of a tag ("sc" "kr" "en")
     **/
    public static String isEnglish(String input) {
        int sc = 0;
        int nonEnglish = 0;
        int english = 0;
        for (int i = 0; i < input.length(); i++) {
            if(Pattern.matches("^[a-zA-Z]+$",""+input.charAt(i))) {
                english += 1;
            } else if(Pattern.matches("^[@#$&()!`.+,/?]",""+input.charAt(i))){
                sc += 1;
            } else if(Pattern.matches("^[0-9]",""+input.charAt(i))) {
                sc += 1;
            } else if(input.charAt(i) == " ".charAt(0)) {
                sc += 1;
            } else {
                nonEnglish += 1;
            }
        }
        if(english > 0 && nonEnglish > 0) {
            return "kr";
        } else if (english == 0 && nonEnglish > 0) {
            return "kr";
        } else if (english > 0 && nonEnglish == 0) {
            return "en";
        }
        return "sc";
    }
    /**
     * Decide which type of the Button from the CS Response.
     * @param input has the response script from the CS.
     * @return String that corresponds to the type of button
     **/
    public static String filterbuttontype(String input) {
        int startlocation = 0;
        int endlocation = 0;
        for(int i = 0; i < input.length(); i++) {
            if (input.charAt(i) == '/') {
                startlocation = i;
                break;
            }
        }
        for(int i = 0; i < input.length() - 1; i++) {
            if (input.charAt(i) == ')' && input.charAt(i + 1) == ')') {
                endlocation = i;
                break;
            }
        }
        return input.substring(startlocation+1,endlocation).trim();

    }
}