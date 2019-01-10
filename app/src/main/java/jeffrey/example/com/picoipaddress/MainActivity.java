package jeffrey.example.com.picoipaddress;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private EditText et_ssid, et_password, et_getip, et_getgateway, et_getdns;
    private WifiManager wifiManager;
    private WifiInfo connectionInfo;
    private WifiConfiguration wifiConfig = null;
    private WifiConfiguration tempConfig = null;

    private static String SSID;
    private static String PASSWORD;

    @SuppressLint("WifiManagerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et_ssid =  findViewById(R.id.et_ssid);
        et_password =  findViewById(R.id.et_password);
        et_getip =  findViewById(R.id.et_getip);
        et_getgateway =  findViewById(R.id.et_getgateway);
        et_getdns =  findViewById(R.id.et_getdns);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }

        SSID = et_ssid.getText().toString().trim();
        PASSWORD = et_password.getText().toString().trim();

        wifiConfig = CreateWifiInfo(SSID, PASSWORD, 3);
        int wcgID = wifiManager.addNetwork(wifiConfig);
        boolean b = wifiManager.enableNetwork(wcgID, true);
        Log.d(TAG, "wcgID = " + wcgID + ", b = " + b);

    }

    public void getIpClick(View view) {

        Log.d(TAG, "getIpClick");

        connectionInfo = wifiManager.getConnectionInfo();
        String ssid1 = connectionInfo.getSSID();
        String SSID1 = ssid1.substring(1, ssid1.length() - 1);
        Log.d(TAG, "SSID1 = " + SSID1);
        DhcpInfo wifiInfo = wifiManager.getDhcpInfo();
        et_getip.setText(intToIp(wifiInfo.ipAddress));
        Log.d(TAG, "ipAddress = " + intToIp(wifiInfo.ipAddress));
        et_getgateway.setText(intToIp(wifiInfo.gateway));
        Log.d(TAG, "gateway = " + intToIp(wifiInfo.gateway));
        et_getdns.setText(intToIp(wifiInfo.dns1));
        Log.d(TAG, "dns = " + intToIp(wifiInfo.dns1));

    }

    public void clearClick(View view) {

        Log.d(TAG, "clearClick");
        et_getip.setText("");
        et_getgateway.setText("");
        et_getdns.setText("");
    }

    public void setIpClick(View view) {

        Log.d(TAG, "setIpClick");
        Log.d(TAG, "SSID = " + SSID);
        if (!et_getip.getText().toString().trim().isEmpty() && !et_getgateway.getText().toString().trim().isEmpty()
                && !et_getdns.getText().toString().trim().isEmpty()) {
            tempConfig = IsExsits(SSID);

            if (tempConfig == null) {

                wifiConfig = CreateWifiInfo(SSID, PASSWORD, 3);
                int wcgID = wifiManager.addNetwork(wifiConfig);
                boolean b = wifiManager.enableNetwork(wcgID, true);
                Log.d(TAG, "wcgID = " + wcgID + ", b = " + b);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        tempConfig = IsExsits(SSID);
                        setStaticIPConfig();
                    }
                }, 5000);

            } else {
                setStaticIPConfig();
            }
        } else {
            Toast.makeText(this, "Content cannot be empty！", Toast.LENGTH_SHORT).show();
        }

    }

    private void setStaticIPConfig() {
        wifiConfig = tempConfig;
        boolean b = wifiManager.enableNetwork(tempConfig.networkId, true);
        Log.d(TAG, "tempConfig.networkId = " + tempConfig.networkId + ", b = " + b);
        try {
            Log.e(TAG, "Handler.postDelayed");
            setStaticIpConfiguration(wifiManager, wifiConfig,
                    InetAddress.getByName(et_getip.getText().toString().trim()), 24,
                    InetAddress.getByName(et_getgateway.getText().toString().trim()),
                    InetAddress.getAllByName(et_getdns.getText().toString().trim()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (wifiManager.disconnect()){
            boolean c =wifiManager.reconnect();
            Log.d(TAG,  " c = " + c);
        }

    }

    public WifiConfiguration IsExsits(String SSID) {
        Log.d(TAG, "IsExsits SSID = " + SSID);
        List<WifiConfiguration> existingConfigs = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration existingConfig : existingConfigs) {
            if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
                Log.d(TAG, "existingConfig = " + existingConfig.SSID);
                return existingConfig;
            }
        }
        return null;
    }

    public WifiConfiguration CreateWifiInfo(String SSID, String Password, int Type) {

        Log.d(TAG, "CreateWifiInfo , SSID = " + SSID + ", Password = " + Password + ", Type = " + Type);
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";

        WifiConfiguration tempConfig = this.IsExsits(SSID);
        if (tempConfig != null) {
            wifiManager.removeNetwork(tempConfig.networkId);
        }

        if (Type == 1) // WIFICIPHER_NOPASS
        {
            config.wepKeys[0] = "";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        if (Type == 2) // WIFICIPHER_WEP
        {
            config.hiddenSSID = true;
            config.wepKeys[0] = "\"" + Password + "\"";
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        if (Type == 3) // WIFICIPHER_WPA
        {
            config.preSharedKey = "\"" + Password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        return config;
    }

    @SuppressWarnings("unchecked")
    public static void setStaticIpConfiguration(WifiManager manager, WifiConfiguration config, InetAddress ipAddress,
                                                int prefixLength, InetAddress gateway, InetAddress[] dns)
            throws ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, NoSuchFieldException, InstantiationException {

        Log.d(TAG, "setStaticIpConfiguration, manager = " + manager.toString() + ", config = " + config
                + ", ipAddress = " + ipAddress + ", prefixLength" + prefixLength + ", gateway = " + gateway);
        // First set up IpAssignment to STATIC.
        Object ipAssignment = getEnumValue("android.net.IpConfiguration$IpAssignment", "STATIC");
        callMethod(config, "setIpAssignment", new String[] { "android.net.IpConfiguration$IpAssignment" },
                new Object[] { ipAssignment });

        // Then set properties in StaticIpConfiguration.
        Object staticIpConfig = newInstance("android.net.StaticIpConfiguration");

        Object linkAddress = newInstance("android.net.LinkAddress", new Class[] { InetAddress.class, int.class },
                new Object[] { ipAddress, prefixLength });
        setField(staticIpConfig, "ipAddress", linkAddress);
        setField(staticIpConfig, "gateway", gateway);
        ArrayList<Object> aa = (ArrayList<Object>) getField(staticIpConfig, "dnsServers");
        aa.clear();
        for (int i = 0; i < dns.length; i++)
            aa.add(dns[i]);
        callMethod(config, "setStaticIpConfiguration", new String[] { "android.net.StaticIpConfiguration" },
                new Object[] { staticIpConfig });
        Log.d(TAG, "conconconm" + config);
        int updateNetwork = manager.updateNetwork(config);
        boolean saveConfiguration = manager.saveConfiguration();
        Log.d(TAG, "updateNetwork" + updateNetwork + saveConfiguration);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static Object getEnumValue(String enumClassName, String enumValue) throws ClassNotFoundException {
        Log.d(TAG, "getEnumValue");
        Log.d(TAG, "enumClassName = " + enumClassName + " ,enumValue = " + enumValue);
        Class enumClz = (Class) Class.forName(enumClassName);
        return Enum.valueOf(enumClz, enumValue);
    }

    @SuppressWarnings("rawtypes")
    private static void callMethod(Object object, String methodName, String[] parameterTypes, Object[] parameterValues)
            throws ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException {
        Log.d(TAG, "callMethod, object = " + object.toString() + ", methodName = " + methodName);
        Class[] parameterClasses = new Class[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++)
            parameterClasses[i] = Class.forName(parameterTypes[i]);

        Method method = object.getClass().getDeclaredMethod(methodName, parameterClasses);
        method.invoke(object, parameterValues);
    }

    private static Object newInstance(String className) throws ClassNotFoundException, InstantiationException,
            IllegalAccessException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException {

        Log.d(TAG, "newInstance, className = " + className);
        return newInstance(className, new Class[0], new Object[0]);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static Object newInstance(String className, Class[] parameterClasses, Object[] parameterValues)
            throws NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, ClassNotFoundException {
        Log.d(TAG, "newInstance");
        Class clz = Class.forName(className);
        Constructor constructor = clz.getConstructor(parameterClasses);
        return constructor.newInstance(parameterValues);
    }

    private static void setField(Object object, String fieldName, Object value)
            throws IllegalAccessException, IllegalArgumentException, NoSuchFieldException {
        Log.d(TAG, "setField");
        Field field = object.getClass().getDeclaredField(fieldName);
        field.set(object, value);
    }

    private static Object getField(Object object, String fieldName)
            throws IllegalAccessException, IllegalArgumentException, NoSuchFieldException {
        Log.d(TAG, "getField");
        Field field = object.getClass().getDeclaredField(fieldName);
        Object out = field.get(object);
        return out;
    }

    private String intToIp(int i) {

        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + (i >> 24 & 0xFF);
    }
}
