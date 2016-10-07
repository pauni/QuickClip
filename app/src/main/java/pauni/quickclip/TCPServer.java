package pauni.quickclip;

import android.app.IntentService;
import android.content.Intent;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.io.IOException;
import java.net.Socket;


/**
 * Created by Roni on 04.10.2016.
 */
public class TCPServer extends IntentService {

    public TCPServer() {
        super("tcp_intent_thread");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        onHandleIntent(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Toast.makeText(TCPServer.this, "service destroyed", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

    @Override
    public void onHandleIntent(Intent Intent) {
        //using try because to catch exceptions (e.g. port is used)
        //create server- and clientsocket. Init clientsocket with serverSocket.accept()
        //this method waits until a client connects to server Socket and binds
        //address and port of the client to the clientsocket

        //creating a printwriter (writes to the outputstream of the clientsocket
        //creating an inputstreamreader (listens to clientsocket's inputstream

            Toast.makeText(TCPServer.this, "tcp kommt", Toast.LENGTH_SHORT).show();

            while (true) {
                try {
                    ServerSocket serverSocket = new ServerSocket(6834);
                    Socket clientSocket = serverSocket.accept();
                    PrintWriter out =
                            new PrintWriter(clientSocket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(clientSocket.getInputStream()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
    }
}
