# IPAddressManager Instructions

Note: Regarding java package creation and usege, please refer to [the Guideline](https://github.com/PicoSupport/PicoSupport/blob/master/How_to_use_JAR_file_in_Unity_project_on_Pico_device.docx)

## Introduction
set a static IP address on Pico device

## Class Name
android:name=" com.example.picovripaddress.picovrWifiManager"

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
   ​                                                                  Interface Instructions

   | Interface           | Explain                 | Remark                                                       |
   | ------------------- | ----------------------- | :----------------------------------------------------------- |
   | androidConnectWifi  | Wifi Connection         | PicoUnityActivity.CallObjectMethod("adnroidConnectWifi",<br/>new object[] { "username", "password" }); |
   | androidGetIpAddress | Get IP address          | string ip="" ;            PicoUnityActivity.CallObjectMethod<string>(ref<br/>ip, "androidGetIpAddress"); |
   | androidGetGateWay   | Get the gateway address | string gateway="";  PicoUnityActivity.CallObjectMethod<string>(ref<br/>gateway, "androidGetGateWay"); |
   | androidGetDNS       | Get DNS                 | string mdns="";        PicoUnityActivity.CallObjectMethod<string>(ref<br/>mdns, "androidGetDNS"); |
   | androidSetIpAddress | Set Ip address          | PicoUnityActivity.CallObjectMethod("androidSetIpAddress", new object[] { "username", "password","ip address","gateway","DNS" }); |

