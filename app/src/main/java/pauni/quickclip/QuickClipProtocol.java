package pauni.quickclip;

import android.content.Context;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Roni on 14.10.2016.
 */

public class QuickClipProtocol {
    private String inputLine;
    private  EditText et;
    Context mContext;

    QuickClipProtocol(String clientInput, Context mContext) {
        inputLine = clientInput;
        this.mContext = mContext;
        MainActivity mA = new MainActivity();
        et = (EditText) mA.findViewById(R.id.eT_code);
    }

    String proccesInput(String string) {
        String flag = inputLine.substring(0, 2);
        String output = null;
        String password;
        String clip;

        switch (flag) {
            case "AT": //AuthenticationRequest
                password = inputLine.substring(2);
                output = (passwordCorrect(password)) ? "ok" : "notok"; //ternary operator
                break;
            case "CB": //Clipboard
                clip = inputLine.substring(7);
                password = inputLine.substring(2, 6);
                if (passwordCorrect(password)) {
                    output = clip;
                }
                else {
                    output = "password changed";
                    Toast.makeText(mContext, "clipboard rejected, please verify your passowrd", Toast.LENGTH_SHORT).show();
                    //need to add multilang. But it's ridiculous to do getString in every class,
                    //so I think I'm just going with that class thing...
                }
                break;
        }
        return output;
    }

    boolean passwordCorrect(String string) {
       return (string == et.getText().toString() );
    }
}
