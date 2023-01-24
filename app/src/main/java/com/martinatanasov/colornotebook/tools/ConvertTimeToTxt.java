package com.martinatanasov.colornotebook.tools;

public class ConvertTimeToTxt {
    public String intToTxtTime(int h, int m){
        String s ="";
        if(h<10){
            s+="0" + h;
        }else{
            s+="" + h;
        }
        if(m<10){
            s+=":0" + m;
        }else{
            s+=":" + m;
        }
        return s;
    }
}
