package pauni.quickclip;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * Created by Roni on 11.10.2016.
 */

public class doBeforeStarting {
    Context mContext;
    doBeforeStarting(Context mContext) {
        this.mContext = mContext;
    }


    int getNetworkState() {
        WifiManager wifiMgr = (WifiManager) mContext.getSystemService(mContext.WIFI_SERVICE);

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

    String getLocalIpAddress(){
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
                 en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (Exception ex) {
            Log.e("IP Address", ex.toString());
        }
        return null;
    }

    boolean permissionsGranted() {
        //ensure that required permissions are given
        return true;
    }
}
