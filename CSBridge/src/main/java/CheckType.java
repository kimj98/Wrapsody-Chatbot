
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
    private String answer="";
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
            answer+=arr[i]+"\n";
        }
    }

    public String getAnswer(){

        return answer;
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
        if(flag < 4) {
            int pivot = 0;
            for(int i = 0; i < input.length(); i++) {
                if (input.charAt(i) >= 48 && input.charAt(i) <= 57) {
                    pivot +=1;
                    extract = String.valueOf(input.charAt(i));
                    if(pivot == flag) {
                        break;
                    }
                }
            }
        } else {
            int check = 0;
            int startpoint = 0;
            for(int i = 0; i < input.length(); i++) {
                if(input.charAt(i) == ':') {
                    check += 1;
                    startpoint = i;
                    if(check == 4) {
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
}
