package pauni.quickclip;

import android.widget.EditText;


/**
 * Created by Roni on 14.10.2016.
 * Very small protocol to handle the processing of the input
 * externally and return proper output
 */

class QuickClipProtocol {
    private  EditText et;
    private static int pinCodePhone;
    private static String clip = "http://www.wikipedia.org";

    QuickClipProtocol() {
    }

    String processInput(String inputLine) {
        String tag = inputLine.substring(0, 2);
        String output = null;
        int pinCodePC;

        switch (tag) {
            case "AR": //AuthenticationRequest
                pinCodePC = Integer.parseInt(inputLine.substring(2)); //input only contains flag(0-1) and password(2-5)
                output = (passwordCorrect(pinCodePC)) ? "ARok" : "ARnotok"; //ternary operator
                break;
            case "CB": //Clipboard
                clip = inputLine.substring(7);
                pinCodePC = Integer.parseInt(inputLine.substring(2, 6)); //input contains flag(0-1), password(2-5) and clipboard.
                if (passwordCorrect(pinCodePC)) {
                    output = "CBok";
                }
                else {
                    output = "CBnotok";
                }
                break;
        }
        return output;
    }

    static String getClip() {
        return clip;
    }
    private boolean passwordCorrect(int pinCodePC) {
       return (pinCodePhone == pinCodePC);
    }

    static void setPinCode(int code) {
        pinCodePhone = code;
    }
}
