package pauni.quickclip;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    TextView tv_debug = null;
    EditText eT_password;
    DoBeforeStart dbs;

    Context context;

    //onCreate is called at the start
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("Service", ">>>started");
        super.onCreate(savedInstanceState);
        //set the layout-xml-file which should be displayed
        setContentView(R.layout.activity_main);

        //init stuff here for class-wide access
        tv_debug = (TextView) findViewById(R.id.tV_IPaddress);
        eT_password = (EditText) findViewById(R.id.eT_code);
        dbs = new DoBeforeStart(this);
        print(dbs.getLocalIpAddress());
        context = this;
        String pincode;
        if (!Objects.equals( (pincode = eT_password.getText().toString()), "" )) {
            QuickClipProtocol.setPinCode(Integer.parseInt(pincode));
        }
    }

    public void print(String string) {
        tv_debug.setText(string);
    }

    public void startServer(View v) {
        //starting the background process (intentservice) the usual way #google
        Intent intent = new Intent(this, BackgroundService.class);
        this.startService(intent);
    }

    public void stopServer (View v1) {
        BackgroundService.setRun(false);
    }
}