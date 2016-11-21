package pauni.quickclip;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.annotation.UiThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    static String pinCodePhone;
    TextView textView_ipAddress = null;
    TextView tv_notify_expl = null;
    Switch switch_onOff = null;
    Switch switch_notification = null;
    EditText eT_pincode;
    Context context;
    static boolean notificationsEnabled = true;
    Intent backgroundService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //set the layout-xml-file which should be displayed
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //init stuff here for class-wide access
        initializeAllViews();
        regAllListeners();
        print(textView_ipAddress, getIpAddress(), getResources().getColor(R.color.fontColor));

        backgroundService = new Intent(context, ClipboardManagingService.class);

    }

    private void regAllListeners() {
        switch_notification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    notificationsEnabled = true;
                    print(tv_notify_expl, getString(R.string.notification_enabled), 0);
                } else {
                    //add a dialog here, asking the user to confirm
                    notificationsEnabled = false;
                    print(tv_notify_expl, getString(R.string.notification_disabled), 0);
                }
            }
        });

        switch_onOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            //using context variable to get the right context from MainActivity
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    pinCodePhone = eT_pincode.getText().toString(); //save the entered pin
                    eT_pincode.setText(""); //reset password_editText
                    startService(backgroundService); //start Server and clipListener and
                    print(textView_ipAddress, getIpAddress(), getResources().getColor(R.color.green) );
                } else {
                    stopService(backgroundService);
                    print(textView_ipAddress, null, getResources().getColor(R.color.fontColor) );
                }
            }
        });
    }

    public void test(View v) {

        finish();
        /*
        Intent intent = new Intent(this, ClipboardHistoryActivity.class);
        startActivity(intent); */

}















    private String getIpAddress() {
        WifiManager wifiManager = (WifiManager) this.getSystemService(WIFI_SERVICE);
        int ipAddress = wifiManager.getConnectionInfo().getIpAddress();
        // Convert little-endian to big-endian if needed
        if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
            ipAddress = Integer.reverseBytes(ipAddress);
        }
        byte[] ipByteArray = BigInteger.valueOf(ipAddress).toByteArray();

        String ipAddressString;
        try {
            ipAddressString = InetAddress.getByAddress(ipByteArray).getHostAddress();
        } catch (UnknownHostException ex) {
            Log.e("WIFIIP", "Unable to get host address.");
            ipAddressString = null;
        }
        return ipAddressString;
    }
    private int getNetworkState() {
        WifiManager wifiMgr = (WifiManager) getSystemService(WIFI_SERVICE);

        if (wifiMgr.isWifiEnabled()) { // Wi-Fi adapter is ON

            WifiInfo wifiInfo = wifiMgr.getConnectionInfo();

            if( wifiInfo.getNetworkId() == -1 ){
                return 2; // Not connected to an access point
            }
            return 3; // Connected to an access point
        }
        else {
            return 1; // Wi-Fi adapter is OFF
        }
    }
    public void print(TextView tv, String text, int color) {
        Log.d("print", "1");
            if (!Objects.equals(text, "") && text != null) {
                tv.setText(text);
        }
        Log.d("print", "2");
            if (color != 0) {
                tv.setTextColor(color);
            }

    }

    private void initializeAllViews() {
        context = getApplicationContext();
        switch_onOff = (Switch) findViewById(R.id.switch_onOff);
        tv_notify_expl = (TextView) findViewById(R.id.tv_notify_expl);
        textView_ipAddress = (TextView) findViewById(R.id.tv_status);
        eT_pincode = (EditText) findViewById(R.id.eT_code);
        switch_notification = (Switch) findViewById(R.id.switch_notification);
    }
}