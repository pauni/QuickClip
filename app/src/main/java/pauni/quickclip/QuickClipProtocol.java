package pauni.quickclip;

import android.content.Context;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Objects;

/**
 * Created by Roni on 14.10.2016.
 */

public class QuickClipProtocol {
    private  EditText et;
    static int pinCodePhone;
    String clip = null;

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

    String getClip() {
        return clip;
    }
    private boolean passwordCorrect(int pinCodePC) {
       return (pinCodePhone == pinCodePC);
    }

    static void setPinCode(int code) {
        pinCodePhone = code;
    }
}
