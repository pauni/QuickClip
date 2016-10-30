package pauni.quickclip;

import android.widget.EditText;

import java.util.Objects;


/**
 * Created by Roni on 14.10.2016.
 * Very small protocol to handle the processing of the input
 * externally and return proper output
 */

class QuickClipProtocol {
    private  EditText et;
    private static int pinCodePhone;
    private static String clip = "";
    private final String CLIPBOARD_FLAG = "CB";
    private final String AUTHENTICATION_REQUEST_FLAG = "AR";

    QuickClipProtocol() {


        String pincode;
        if ( !Objects.equals((pincode = MainActivity.pinCodePhone), "") ) {
            pinCodePhone = Integer.parseInt(pincode);
        }
    }

    String processInput(String inputLine) {
        String tag = inputLine.substring(0, 2);
        String output = null;
        int pinCodePC;

        switch (tag) {
            case AUTHENTICATION_REQUEST_FLAG: //AuthenticationRequest
                pinCodePC = Integer.parseInt(inputLine.substring(2)); //input only contains flag(0-1) and password(2-5)
                output = (pinCodeCorrect(pinCodePC)) ? "ARok" : "ARnotok"; //ternary operator
                break;
            case CLIPBOARD_FLAG: //Clipboard
                clip = inputLine.substring(7);
                pinCodePC = Integer.parseInt(inputLine.substring(2, 6)); //input contains flag(0-1), password(2-5) and clipboard.
                if (pinCodeCorrect(pinCodePC)) {
                    output = "CBok";
                }
                else {
                    output = "CBnotok";
                }
                break;
        }
        return output;
    }

    String sendClip(String clip) {
        //returning String in format required by QuickClipProtocol
        return CLIPBOARD_FLAG + pinCodePhone + clip;

    }

    static String getClip() {
        return clip;
    }
    private boolean pinCodeCorrect(int pinCodePC) {
       return (pinCodePhone == pinCodePC);
    }
}
