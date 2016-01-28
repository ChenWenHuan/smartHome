package com.smarthome.client2.unit.listener;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

public interface BluetoothManagerListener {

	/**
	 * 蓝牙被打开时调用
	 */
	void bluetoothOn();

	/**
	 * 蓝牙被关闭时调用
	 */
	void bluetoothOff();

	/**
	 * 搜索设备时，找到一个新设备时调用
	 */
	void searchNewDevice(BluetoothDevice device);

	/**
	 * 扫描模式发生了改变
	 * 
	 * @param newMode
	 *            新的扫描模式
	 * @param oldMode
	 *            旧的扫描模式
	 */
	void scanModeChanged(int newMode, int oldMode);
	
	/**
	 * 开始扫描设备
	 */
	void startScanDevice();
	
	/**
	 * 结束扫描设备
	 */
	void endScanDevice();
	
	/**
	 * 本地蓝牙设备的名称发生了改变
	 * @param name
	 */
	void bluetoothNameChanged(String name);
	
	void deviceBondStateChanged(BluetoothDevice device, int newState, int oldState);
	
	void acceptNewSocket(BluetoothSocket socket);

}

