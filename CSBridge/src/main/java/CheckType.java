

public class CheckType {

    /*
     - private int typeNum : assign the type of the Answer (0~5)
     - private String answer : assign the Answer from CS after delete typeNum.
     - private int btnNum : assign the button number of the attachment.
     - private int attachFlag : assign 1 or 0 to figure out attachment require or not.
     - private String photoNum : assign the name of the .png file
     */
    private int typeNum;
    private String answer="";
    private int btnNum;
    private int attachFlag;
    private String phtNum;

    /*
        TODO:
        1. Find the Number (last character in the "csAns"
        2. substring the Ans without typeNum.
    */
    public CheckType(String csAns){
        String arr[] = csAns.split("\n");
        int length = arr.length;
        String tags = arr[length - 1];
        int tagslength = tags.length();
        System.out.println((tags));
        //get the type number from the answer.
        typeNum = (tags.charAt(5))-'0';

        //get the button nu-'mber if 0 = no button.\n
        btnNum = (tags.charAt(14))-'0';

        //attachment Flag check
        attachFlag = (tags.charAt(27))-'0';

        //get the image file's name.
        phtNum = tags.substring(tags.length()-2,tags.length());


        //substring the original answer.
        for(int i = 0; i < arr.length - 1; i++){
            answer+=arr[i]+"\n";
        }

        //Debug : extract the
        System.out.println("Type number is : "+typeNum);
        System.out.println("Button Number is: " + btnNum);
        System.out.println("Attachment Flag : " + attachFlag);
        System.out.println("Photo number is :" + phtNum);
        System.out.println("New Question:"+ answer);
    }

    public String getAnswer(){
        return answer;
    }
    public int getTypeNum(){
        return typeNum;
    }
    public int getBtnNum(){
        return btnNum;
    }
    public String getImgName(){
        return phtNum;
    }
}
