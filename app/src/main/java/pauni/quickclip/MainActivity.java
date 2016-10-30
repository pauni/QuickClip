package pauni.quickclip;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    static String pinCodePhone;
    TextView tv_debug = null;
    EditText eT_pincode;
    WLANInfo wlanInfo;
    Context context;

    //onCreate is called at the start
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("Service", ">>>started");
        super.onCreate(savedInstanceState);
        //set the layout-xml-file which should be displayed
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //init stuff here for class-wide access
        tv_debug = (TextView) findViewById(R.id.tV_IPaddress);
        eT_pincode = (EditText) findViewById(R.id.eT_code);
        wlanInfo = new WLANInfo(this);
        print(wlanInfo.getIpAddress());
        context = this;
    }

    public void print(String string) {
        tv_debug.setText(string);
    }

    public void startServer(View v) {
        //starting the background process (intentservice) the usual way #google
        setPinCodePhone(eT_pincode.getText().toString());
        Intent intent = new Intent(this, WaitForPcClip.class);
        this.startService(intent);
    }

    public void stopServer (View v1) {
        WaitForPcClip.setRun(false);
    }

    private void setPinCodePhone(String string) {
        pinCodePhone = string;
    }
}