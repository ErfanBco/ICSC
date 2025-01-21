package shady.bco.js;

import android.util.Log;

public class Fa {
    ///Version : 2




    public static String NumsToPersian(String text){
         String[][] Nums={
                {"0", "۰"},
                {"1", "۱"},
                {"2", "۲"},
                {"3", "۳"},
                {"4", "۴"},
                {"5", "۵"},
                {"6", "۶"},
                {"7", "۷"},
                {"8", "۸"},
                {"9", "۹"}};
        boolean IsNegative=false;
        if (text.contains("-")){
            IsNegative=true;
            text=text.replace("-","");
        }
        for (int i=1;i<=Nums.length;i++){
            text=text.replace(Nums[i-1][0],Nums[i-1][1]);
        }
        if (IsNegative) text=text+"-";
        return text;
    }


   public static boolean IsTheWordEnglish(String Name){
       char[] NameArray=Name.toLowerCase().toCharArray();

       String enKeys = "qwertyuiopasdfghjklzxcvbnm";
       String faKeys = "ضصثقفغعهخحجچپشسیبلاتنمکگظطزرذدئوژ";
       char[] EnArray= enKeys.toLowerCase().toCharArray();
       char[] FaArray= faKeys.toLowerCase().toCharArray();

       int HowManyFa=0,HowManyEn=0;
       for (int i=1;i<=NameArray.length;i++){
           for (int x=1;x<=Math.max(EnArray.length,FaArray.length);x++){
               if (x<=EnArray.length){
                   if (NameArray[i-1]==EnArray[x-1]) HowManyEn++;
               }
               if (x<=FaArray.length){
                   if (NameArray[i-1]==FaArray[x-1]) HowManyFa++;
               }
           }
       }
       Log.d("Fa","There is "+String.valueOf(HowManyEn)+" English Letters and there is "+String.valueOf(HowManyFa)+" Farsi Letters");
       return HowManyEn > HowManyFa;
   }
}
