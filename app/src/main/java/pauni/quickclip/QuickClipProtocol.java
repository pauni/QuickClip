package pauni.quickclip;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.Objects;


/**
 * Created by Roni on 14.10.2016.
 * Very small protocol to handle the processing of the input
 * externally and return proper output
 */

class QuickClipProtocol {
    static String computerClip;
    private static String clip = "";
    static final String CLIPBOARD_FLAG = "CB";
    private final String AUTHENTICATION_REQUEST_FLAG = "AR";
    Context context;
    QuickClipProtocol(Context context) {
        this.context = context;
    }

    String processInput(String inputLine) {
        String tag = inputLine.substring(0, 2);//flag(0-1)
        String output = null;
        String pinCodePC;

        switch (tag) {
            case AUTHENTICATION_REQUEST_FLAG: //AuthenticationRequest
                pinCodePC = inputLine.substring(2); //input only contains flag(0-1) and password(2-5)
                output = (pinCodeCorrect(pinCodePC)) ? "ARok" : "ARnotok"; //ternary operator
                break;
            case CLIPBOARD_FLAG: //incoming clipboard
                clip = inputLine.substring(8); //clipboard (8+)
                pinCodePC = (inputLine.substring(2, 8)); //password(2-7)
                Log.d("CB", pinCodePC);
                Log.d("CB", inputLine);
                if (pinCodeCorrect(pinCodePC)) {
                    Log.d("CB", pinCodePC);
                    computerClip = clip;
                    output = "CBok";
                }
                else {
                    Log.d("TEST", "PASSED");
                    output = "CBnotok";
                }
                break;
        }
        return output;
    }

    static  String prepareClip(String clip) {
        //returning String in format required by QuickClipProtocol
        return CLIPBOARD_FLAG + MainActivity.pinCodePhone + clip;

    }
    static String getComputerClip() { return computerClip; }
    private boolean pinCodeCorrect(String pinCodePC) {
       return (Objects.equals(MainActivity.pinCodePhone, pinCodePC));
    }
}