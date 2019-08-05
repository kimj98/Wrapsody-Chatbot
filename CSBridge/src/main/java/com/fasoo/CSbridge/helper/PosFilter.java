
package com.fasoo.CSbridge.helper;

import org.openkoreantext.processor.OpenKoreanTextProcessorJava;
import org.openkoreantext.processor.tokenizer.KoreanTokenizer;
import scala.collection.Seq;
/**
 * Modifying the user input from the Korean-POS tagger.
 * @author Taehoon Kim.
 */
public class PosFilter {
    /**
     * Extract the POS Tagged words from the POS Tagger.
     * @param text the words what user enter as Korean.
     * @return POS Tagged user Input.
     */
    public static String filtering(String text) {
        Seq<KoreanTokenizer.KoreanToken> tokenss = OpenKoreanTextProcessorJava.tokenize(text);
        String newTokens = tokenss.mkString(" ");
        //System.out.println(newTokens+ " is original");

        String resultToken = ""; // 여기에 저장해서 return 할거야
        //initPos & checkPos = Substring 하기위한 index num 구하는 variables
        int initPos =0;
        int checkPos=0;
        int gualCheck=0;
        //가장 처음 시작할때는 whiteSpace 없이 바로 글자 시작이니까
        //특이케이스로 flag 세워서 처리.
        int flagCheck=0;
        for(int i=0; i< newTokens.length();i++){
            //System.out.println(newTokens.charAt(i));
            if(flagCheck==0){
                if(newTokens.charAt(i)=='('){
                    checkPos=i;
                    //temp String 에다가 substirng 해온 값을 저장 및 결과에 추가.
                    String tmp = newTokens.substring(initPos,checkPos);
                    resultToken+= tmp+" "; // 뒤에 올 글자를 위해 whiteSpace 추가.
                    initPos = checkPos;//다음 검사를 위해 initPos 위치 수정.
                    flagCheck=1;
                    gualCheck+=1;
                }
            }else{
                if(newTokens.charAt(i)==')'){
                    gualCheck++;
                    // 우 괄호 +2 index는 text의 첫글자의 index num ->  initPos STOP
                    if(gualCheck%2==0) {
                        initPos = i + 2;
                    }
                }else if(newTokens.charAt(i)=='('){
                    gualCheck++;
                    if(gualCheck%2!=0){
                        //좌 괄호 를 만났을때 STOP 이제 substring 의 오른쪽 index num 구했으니 작업 시작.
                        checkPos=i;
                        String tmp = newTokens.substring(initPos,checkPos);
                        resultToken+=tmp+" ";
                        initPos=checkPos;
                    }
                }
            }
        }
        return resultToken;
    }
}
