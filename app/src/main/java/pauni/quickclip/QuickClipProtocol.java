package pauni.quickclip;

import android.content.Context;
import android.widget.EditText;
import android.widget.Toast;
/**
 * Created by Roni on 14.10.2016.
 */

public class QuickClipProtocol {
    private  EditText et;
    Context mContext;
    String clip = null;

    QuickClipProtocol(Context mContext) {
        this.mContext = mContext;
        MainActivity mA = new MainActivity();
        et = (EditText) mA.findViewById(R.id.eT_code);
    }

    String processInput(String inputLine) {
        String flag = inputLine.substring(0, 2);
        String output = null;
        String password;

        switch (flag) {
            case "AT": //AuthenticationRequest
                password = inputLine.substring(2); //input only contains flag(0-1) and password(2-5)
                output = (passwordCorrect(password)) ? "ATok" : "ATnotok"; //ternary operator
                TCPServer.setUpdateClipboardNow(false);
                break;
            case "CB": //Clipboard
                clip = inputLine.substring(7);
                password = inputLine.substring(2, 6); //input contains flag(0-1), password(2-5) and clipboard.
                if (passwordCorrect(password)) {
                    output = "CBok";
                    TCPServer.setUpdateClipboardNow(true);
                }
                else {
                    output = "CBnotok";
                    TCPServer.setUpdateClipboardNow(false);
                    Toast.makeText(mContext, "clipboard rejected, please verify your passowrd", Toast.LENGTH_SHORT).show();
                    //need to add multilang. But it's ridiculous to do getString in every class,
                    //so I think I'm just going with that class thing...
                }
                break;
        }
        return output;
    }

    private boolean passwordCorrect(String string) {
       return (string == et.getText().toString() );
    }
}
