package com.example.picovripaddress;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import com.unity3d.player.UnityPlayerNativeActivityPico;

import android.app.Activity;
import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

public class picovrWifiManager extends UnityPlayerNativeActivityPico {

	private static final String TAG = "picovrWifiManager";
	public static Activity unityActivity = null;

	private WifiManager wifiManager;
	private WifiInfo connectionInfo;
	private WifiConfiguration wifiConfig = null;
	private WifiConfiguration tempConfig = null;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		unityActivity = this;

		wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

		if (!wifiManager.isWifiEnabled()) {
			wifiManager.setWifiEnabled(true);
		}
	}

	public void adnroidConnectWifi(String SSID, String PASSWORD) {
		
		Log.e(TAG, "adnroidConnectWifi");
		Log.e(TAG, "SSID = " + SSID);
		Log.e(TAG, "PASSWORD = " + PASSWORD);
		wifiConfig = CreateWifiInfo(SSID, PASSWORD, 3);
		int wcgID = wifiManager.addNetwork(wifiConfig);
		boolean b = wifiManager.enableNetwork(wcgID, true);
		Log.d(TAG, "wcgID = " + wcgID + ", b = " + b);
	}

	public String androidGetIpAddress() {

		Log.d(TAG, "androidGetIpAddress");

		DhcpInfo wifiInfo = wifiManager.getDhcpInfo();
		Log.d(TAG, "ipAddress = " + intToIp(wifiInfo.ipAddress));
		return intToIp(wifiInfo.ipAddress);

	}

	public String androidGetGateWay() {

		Log.d(TAG, "androidGetGateWay");

		DhcpInfo wifiInfo = wifiManager.getDhcpInfo();
		Log.d(TAG, "gateway = " + intToIp(wifiInfo.gateway));
		return intToIp(wifiInfo.gateway);

	}

	public String androidGetDNS() {

		Log.d(TAG, "androidGetDNS");

		DhcpInfo wifiInfo = wifiManager.getDhcpInfo();
		Log.d(TAG, "dns = " + intToIp(wifiInfo.dns1));
		return intToIp(wifiInfo.dns1);

	}

	public void androidSetIpAddress(final String SSID, String PASSWORD, final String ip, final String gateway,
			final String dns) {

		Log.d(TAG, "androidSetIpAddress");
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
					setStaticIPConfig(ip, gateway, dns);
				}
			}, 5000);

		} else {
			setStaticIPConfig(ip, gateway, dns);
		}

	}

	private void setStaticIPConfig(String ip, String gateway, String dns) {
		Log.e(TAG, "setStaticIPConfig");
		wifiConfig = tempConfig;
		boolean b = wifiManager.enableNetwork(tempConfig.networkId, true);
		Log.d(TAG, "tempConfig.networkId = " + tempConfig.networkId + ", b = " + b);
		try {
			setStaticIpConfiguration(wifiManager, wifiConfig, InetAddress.getByName(ip), 24,
					InetAddress.getByName(gateway), InetAddress.getAllByName(dns));
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (wifiManager.disconnect()) {
			boolean c = wifiManager.reconnect();
			Log.d(TAG, " c = " + c);
		}

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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static Object getEnumValue(String enumClassName, String enumValue) throws ClassNotFoundException {
		Log.d(TAG, "getEnumValue");
		Log.d(TAG, "enumClassName = " + enumClassName + " ,enumValue = " + enumValue);
		Class enumClz = (Class) Class.forName(enumClassName);
		return Enum.valueOf(enumClz, enumValue);
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

	private String intToIp(int i) {

		return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + (i >> 24 & 0xFF);
	}
}
