# IPAddressManager Instructions

Note: Regarding JAR file creation and usage, please refer to [the Guideline](https://github.com/picoxr/support/blob/master/How%20to%20use%20JAR%20file%20in%20Unity%20project%20on%20Pico%20Device.docx)

## Introduction
set a static IP address on Pico device

## Class Name
```
android:name=" com.example.picovripaddress.picovrWifiManager"
```

## Permission
```
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
<uses-permission android:name="android.permission.CHANGE_WIFI_STATE"/>
<uses-permission android:name="android.permission.WRITE_SETTINGS"/>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.WAKE_LOCK" />
```

## Interfaces List

   | Interface           | Explain                 | Remark                                                       |
   | ------------------- | ----------------------- | :----------------------------------------------------------- |
   | androidConnectWifi  | Wifi Connection         | PicoUnityActivity.CallObjectMethod("adnroidConnectWifi",<br/>new object[] { "username", "password" }); |
   | androidGetIpAddress | Get IP address          | string ip="" ;            PicoUnityActivity.CallObjectMethod<string>(ref<br/>ip, "androidGetIpAddress"); |
   | androidGetGateWay   | Get the gateway address | string gateway="";  PicoUnityActivity.CallObjectMethod<string>(ref<br/>gateway, "androidGetGateWay"); |
   | androidGetDNS       | Get DNS                 | string mdns="";        PicoUnityActivity.CallObjectMethod<string>(ref<br/>mdns, "androidGetDNS"); |
   | androidSetIpAddress | Set Ip address          | PicoUnityActivity.CallObjectMethod("androidSetIpAddress", new object[] { "username", "password","ip address","gateway","DNS" }); |

