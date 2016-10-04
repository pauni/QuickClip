package pauni.quickclip;

import android.content.Intent;

import java.net.ServerSocket;
import java.io.IOException;


/**
 * Created by Roni on 04.10.2016.
 */

public class TCPServer implements Runnable{

    @Override public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(6834);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
