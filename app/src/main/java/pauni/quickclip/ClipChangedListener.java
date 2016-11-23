package pauni.quickclip;

import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Objects;

import static android.content.ClipDescription.MIMETYPE_TEXT_HTML;
import static android.content.ClipDescription.MIMETYPE_TEXT_PLAIN;

/**
 * Created by Roni on 23.11.2016.
 */

class ClipChangedListener implements Runnable {
    static ClipboardManager clipboardManager;
    static ClipboardManager.OnPrimaryClipChangedListener mPrimaryClipChangedListener;
    public Context context;
    DatabaseHelper myDb;
    String clip;
    String recentClip;

    boolean freeToDoIt = true;
    private Handler handler;

    ClipChangedListener(Context c) {
        context = c;
        handler = new Handler();
        myDb = new DatabaseHelper(context);
        clipboardManager = (android.content.ClipboardManager) context.getSystemService(
                Context.CLIPBOARD_SERVICE);
    }

    public void run() {
        final TCPClient tcpClient = new TCPClient();
        final int SERVER_PORT = 6834; //port of choice for quickclip

        mPrimaryClipChangedListener =  new ClipboardManager.OnPrimaryClipChangedListener() {
            @Override
            public void onPrimaryClipChanged() {
                freeToDoIt = false; //prevent hortClipMemory from doing its work
                //getting cliptext (as chars) and clipdescr.
                CharSequence clipChars = clipboardManager.getPrimaryClip().getItemAt(0).getText();
                String clipText;
                ClipDescription clipDescription = clipboardManager.getPrimaryClipDescription();

                //only save it to the history, if it's a text (picture-support will come)
                if (clipChars != null &&  (clipDescription.hasMimeType(MIMETYPE_TEXT_PLAIN) ||
                        clipDescription.hasMimeType(MIMETYPE_TEXT_HTML))) {


                    //saving current clip for 3 secs and check if it's the same as before
                    clipText = clipChars.toString();
                    if (Objects.equals(clipText, recentClip)) {
                        Log.d("hack", "caught the triple");
                        return;
                    }
                    recentClip = clipText;


                    boolean isInserted = myDb.insertData(clipText, getTimeStamp());

                    if (isInserted) {
                        Toast.makeText(context, "saved", Toast.LENGTH_SHORT).show();
                    } else Log.d("isInserted", "false");

                    //
                    if (!Objects.equals( clipText, QuickClipProtocol.computerClip) ) {
                        boolean isConnected = tcpClient.connect(TCPServer.getIpAddress(), SERVER_PORT);
                        if (isConnected) {
                            tcpClient.send(QuickClipProtocol.prepareClip(clip));
                            tcpClient.close();
                            Toast.makeText(context, "dispatched", Toast.LENGTH_SHORT).show();
                        } else Toast.makeText(context, "no pc, no send", Toast.LENGTH_SHORT).show();
                    }
                }
                freeToDoIt = true; //allow shortClipMemory to do its work again
            }
        };

        clipboardManager.addPrimaryClipChangedListener(mPrimaryClipChangedListener);

        //a not satisfying hack, to avoid the Chrome or Chromium browser to copy things THREE times!
        Runnable shortClipMemory = new Runnable() {
            @Override
            public void run() {
                if (freeToDoIt) {
                    recentClip = null;
                }
                handler.postDelayed(this, 3000);
            }
        };
        handler.post(shortClipMemory);
    }

    static void unregOnClickListener(Context c) {
        clipboardManager.removePrimaryClipChangedListener(mPrimaryClipChangedListener);
        Toast.makeText(c, "Service destroyed", Toast.LENGTH_SHORT).show();
    }

    private String getTimeStamp() {
        Calendar calender = Calendar.getInstance();
        DateFormat dateFormat = DateFormat.getDateTimeInstance();
        return dateFormat.format(calender.getTime());
    }
}
