package pauni.quickclip;

import android.app.IntentService;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Roni on 27.10.2016.
 * This class consists of a ClipboardContentListener and a tcp-client. Basically, when the clipboard
 * changes, the Listener will use the tcp-client to forward the phones clip-content to the computer's
 * tcp-server
 */

public class ClipboardSender extends IntentService {
    android.content.ClipboardManager clipboardManager;
    static String SERVER_ADDRESS = null;
    static int SERVER_PORT = 6834;

    TCPClient tcpClient;
    public ClipboardSender() {
        super("tcp-client_intent_thread");
        tcpClient = new TCPClient(SERVER_ADDRESS, SERVER_PORT);
        clipboardManager = (android.content.ClipboardManager) getSystemService(
                        Context.CLIPBOARD_SERVICE);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    protected void onHandleIntent(Intent intent){
        clipboardManager.addPrimaryClipChangedListener(
                new ClipboardManager.OnPrimaryClipChangedListener() {

                    @Override
                    public void onPrimaryClipChanged() {
                        //do be continued...
            }
        });
    }

    void sendClip(String clip) {
        QuickClipProtocol quickClipProtocol = new QuickClipProtocol();
        String outputLine = quickClipProtocol.sendClip(clip);
        tcpClient.send(outputLine);
    }

    static void setServerAddress(String serverAddress) {
        SERVER_ADDRESS = serverAddress;
    }
}
