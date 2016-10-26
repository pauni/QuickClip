package pauni.quickclip;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.nio.ByteOrder;
import java.util.Enumeration;

import static android.content.Context.WIFI_SERVICE;

/**a
 * Created by Roni on 11.10.2016.
 */

class DoBeforeStart {
    private Context mContext;
    DoBeforeStart(Context mContext) {
        this.mContext = mContext;
    }


    int getNetworkState() {
        WifiManager wifiMgr = (WifiManager) mContext.getSystemService(WIFI_SERVICE);

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
        WifiManager wifiManager = (WifiManager) mContext.getSystemService(WIFI_SERVICE);
        int ipAddress = wifiManager.getConnectionInfo().getIpAddress();

        // Convert little-endian to big-endianif needed
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
}
