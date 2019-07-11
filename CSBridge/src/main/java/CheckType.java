import java.util.ArrayList;

public class CheckType {

    /*
     - private int typeNum : assign the type of the Answer (0~5)
     - private String answer : assign the Answer from CS after delete typeNum.
     - private int btnNum : assign the button number of the attachment.
     - private int attachFlag : assign 1 or 0 to figure out attachment require or not.
     - private String photoNum : assign the name of the .png file
     - private String tags     : assign the last sentence from the script ( type number etc..)

     */
    private int typeNum;
    private String temp="";
    private String nobuttonTxt = "";
    private ArrayList<String> buttonTxt;
    private int btnNum;
    private int attachFlag;
    private String fileName;
    private String tags;

    /*
        TODO:
        1. Find the Number (last character in the "csAns"
        2. substring the Ans without typeNum.
    */
    public CheckType(String csAns){
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
        for(int i = 0; i < temparr.length; i++) {
            for(int j = 0; j < temparr[j].length()-1; j++) { // -1 because j+1 is called on the 2nd last (if j+1 is called and j is the last then error)
                if(temparr[i].charAt(j) == '[' && temparr[i].charAt(j+1) == '[' ) {
                    buttonlocation.add(i);
                } else {
                    nobuttonlocation.add(i);
                }
            }
        }

        for(int i = 0; i < nobuttonlocation.size(); i++){
            nobuttonTxt+=temparr[nobuttonlocation.get(i)]+"\n";
        }
        for(int i = 0; i < buttonlocation.size(); i++){
            buttonTxt.add(filterbutton(temparr[(buttonlocation.get(i))]));
        }
    }

    public String getAnswer() {
        return nobuttonTxt;
    }

    public ArrayList getBTxt() {
        return buttonTxt;
    }
    public int getTypeNum(){
        typeNum = Integer.parseInt(tagger(tags,1));
        return typeNum;
    }
    public int getBtnNum(){
        btnNum = Integer.parseInt(tagger(tags,2));
        System.out.println("button number : "+ btnNum);
        return btnNum;
    }
    public String getFileName(){
        fileName = tagger(tags,4);
        System.out.println("filename is : "+ fileName);
        return fileName;
    }
    public String tagger(String input, int flag) {
        String extract = "";
        if (flag < 4) {
            int pivot = 0;
            for (int i = 0; i < input.length(); i++) {
                if (input.charAt(i) >= 48 && input.charAt(i) <= 57) {
                    pivot += 1;
                    extract = String.valueOf(input.charAt(i));
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
            extract = filtered;
        }
        return extract;

    }
    public static String filterbutton(String input) {
        int splitlocation = 0;
        for(int i = 0; i < input.length() - 1; i++) {
            if (input.charAt(i) == '[' && input.charAt(i + 1) == '[') {
                splitlocation = i;
                break;
            }
        }
        return input.substring(0,splitlocation).trim(); // sfdfs [[bi
    }

    public static void main(String[] args) {
        String a = "abcd";
        System.out.println(a.substring(0,2));
    }
}
