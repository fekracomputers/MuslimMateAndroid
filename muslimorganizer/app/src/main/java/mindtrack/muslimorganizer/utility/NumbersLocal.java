package mindtrack.muslimorganizer.utility;

import android.content.Context;
import android.content.res.Resources;

/**
 * Class have some function to help in app
 */
public class NumbersLocal {

    /**
     * Function to convert english numbers to arabic
     * if mobile language arabic
     *
     * @param number Number to convert
     * @return Converted number
     */
    public static String convertNumberType(Context context, String number) {

        try {
            if (context.getResources().getConfiguration().locale.getDisplayLanguage().equals("العربية"))
                return number.replaceAll("0", "٠").replaceAll("1", "١")
                        .replaceAll("2", "٢").replaceAll("3", "٣")
                        .replaceAll("4", "٤").replaceAll("5", "٥")
                        .replaceAll("6", "٦").replaceAll("7", "٧")
                        .replaceAll("8", "٨").replaceAll("9", "٩");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return number;
    }

    public static String convertToNumberTypeSystem(Context context , String number){
        try {
            if (Resources.getSystem().getConfiguration().locale.getDisplayLanguage().equals("العربية"))
                return number.replaceAll("0", "٠").replaceAll("1", "١")
                        .replaceAll("2", "٢").replaceAll("3", "٣")
                        .replaceAll("4", "٤").replaceAll("5", "٥")
                        .replaceAll("6", "٦").replaceAll("7", "٧")
                        .replaceAll("8", "٨").replaceAll("9", "٩");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return number;
    }


    /**
     * Function to convert english numbers to arabic
     * if mobile language arabic
     *
     * @param number Number to convert
     * @return Converted number
     */
    public static String convertNumberTypeToEnglish(Context context, String number) {

        try {
            if (context.getResources().getConfiguration().locale.getDisplayLanguage().equals("العربية"))
                return number.replaceAll("٠" , "0").replaceAll( "١" , "1")
                        .replaceAll("٢" , "2").replaceAll("٣" , "3")
                        .replaceAll("٤" , "4").replaceAll("٥" , "5")
                        .replaceAll("٦" , "6").replaceAll("٧" , "7")
                        .replaceAll("٨" , "8").replaceAll("٩" , "9");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return number;
    }


}
