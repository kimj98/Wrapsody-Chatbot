////////////////////////////////////////////////////////////////////////////////////////////////////
//    This class read the ChatScript Topic file to find out                                       //
//    the Preset Questions for each categories.                                                   //
//    Method:                                                                                     //
//        public void readfiles(JSONObject)                                                       //
//            -This method figure out the user's locale to decide which topic files is required.  //
//             Read the file line by line and find specific form ( new, FAQ, notice )and add them //
//             into the ArrayList.                                                                //
//            -@Param:                                                                            //
//                JSONObject j : this variable is used only check the user's locale.              //
//        public ArrayList getFAQList() :                                                         //
//            return the ArrayList which has the list of the FAQ catalog.                         //
//        public ArrayList getNewList() :                                                         //
//            return the ArrayList which has the list of the new features catalog.                //
//        public ArrayList getNoticeList() :                                                      //
//            return the ArrayList which ahs the list of the notice catalog.                      //
////////////////////////////////////////////////////////////////////////////////////////////////////


package com.fasoo.CSbridge.helper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import org.json.simple.JSONObject;

/**
 * Read the topic file to extract the Commands for the Left Menu's Categories
 */
public class commentReader {

    public static ArrayList myArrayListNew = new ArrayList<String>();
    public static ArrayList myArrayListFAQ = new ArrayList<String>();
    public static ArrayList myArrayListNotice = new ArrayList<String>();

    /**
     * Read the topic file( Korean / English ) and extract the list of the Commands
     * @param j Checking the user's Locale data
     */
    public void readfiles(JSONObject j) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(constant.KrFile));

            if (j.get("locale").toString().equals("en_US") ||j.get("locale").toString().equals("en-US")) {
                reader = new BufferedReader(new FileReader(
                        constant.EnFile));
            }

            String line = reader.readLine();
            String line2 = "";
            while (line != null) {

                if (line.indexOf('#') != -1) {
                    int i=0;
                    int brNum = 0;    // right side ')'
                    int leftBrNum =0; // left  side '('
                    int inRight=0,inLeft=0;
                    for (i=0 ; i < line.length(); i++) {

                        if (line.charAt(i)== ')') {
                            brNum += 1;
                            if(brNum==1){
                                inRight=i;
                            }
                        }else if(line.charAt(i)=='('){
                            leftBrNum+=1;
                            if(leftBrNum==2){
                                inLeft=i;
                            }
                        }
                        if (brNum == 2) {
                            line2 = line.substring(i + 1);
                            line2 = line2.trim();
                            break;
                        }
                    }
                    if(line.length() > 0 && brNum==2 && leftBrNum==2){

                        if (line.substring(inLeft+1,inRight).contains("new")) {
                            myArrayListNew.add(line2);
                        } else if (line.substring(inLeft+1,inRight).contains("FAQ")) {
                            myArrayListFAQ.add(line2);
                        } else if (line.substring(inLeft+1,inRight).contains("notice")){
                            myArrayListNotice.add(line2);
                        }
                    }

                }
                line = reader.readLine();
                //System.out.println("now the line is :" + line);
            }
            reader.close();
        }catch(IOException e) {
             e.printStackTrace();
        }
    }

    /**
     * Return the List of the FAQ Category's Commands
     * @return ArrayList of the Commands
     */
    public  ArrayList getFAQList() { return myArrayListFAQ; }

    /**
     * Return the List of the New-Featre Category Commands
     * @return ArrayList of the Commands
     */
    public  ArrayList getNewList() { return myArrayListNew; }

    /**
     * Return the List of the Notice Category's Commands
     * @return ArrayList of the Commands
     */
    public  ArrayList getNoticeList()  { return myArrayListNotice; }
}
