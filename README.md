# IPAddressManager Instructions

JAR file and demo APK are in /resource.    
Note: Regarding JAR file creation and usage, please refer to [the Guideline](https://github.com/picoxr/support/blob/master/How%20to%20Use%20JAR%20file%20in%20Unity%20project%20on%20Pico%20Device.docx)

## Introduction
Connect specific wifi, set and get IP, gateway and DNS.

## Class Name
```
com.picovr.setstaticip.SetStaticIPClass
```

## Permission
Add shardUserId property: 
```
android:sharedUserId="android.uid.system"
```
```
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
<uses-permission android:name="android.permission.CHANGE_WIFI_STATE"/>
<uses-permission android:name="android.permission.WRITE_SETTINGS"/>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.WAKE_LOCK" />
```

## Interfaces
```
init(Context context) //Initialization method. You need to invoke it in Start() like sample code does.
void ConnectWifi(String ssid, String pwd)
String getIpAddress()
String getGateWay()
String getDNS()
void setIpAddress(String ssid, String pwd, String ip, String gateway, String dns)
```

## Sample code

```
AndroidJavaObject ajo;
AndroidJavaObject context;

void Start()
{
ajo = new AndroidJavaObject("com.picovr.setstaticip.SetStaticIPClass");
context = new AndroidJavaClass("com.unity3d.player.UnityPlayer").GetStatic<AndroidJavaObject>("currentActivity");
ajo.Call("init", context);
}

public void connectWifi()
{
    string name = ifConnectName.text;
    string pwd = ifConnectPwd.text;

    ajo.Call("ConnectWifi", name, pwd);
}

public void getIP()
{
    string ip = ajo.Call<string>("getIpAddress");
    txIP.text = ip;
}

public void getGateway()
{
    string gateway = ajo.Call<string>("getGateWay");
    txGateway.text = gateway;
}

public void getDNS()
{
    string dns = ajo.Call<string>("getDNS");
    txDNS.text = dns;
}

public void setIP()
{
    string name = ifSetName.text;
    string pwd = ifSetPwd.text;
    string ip = ifSetIP.text;
    string gateway = ifSetGateway.text;
    string dns = ifSetDNS.text;
    ajo.Call("setIpAddress", name, pwd, ip, gateway, dns);
}
```



