package com.runyee.ptt;

import java.io.IOException;

import android.util.Log;

public class GPIO {

	// echo "-wmode 2 1" > /sys/devices/virtual/misc/mtgpio/pin
	static String cmd_GPIOHigh = "echo \"-wmode 2 1\"  > /sys/devices/virtual/misc/mtgpio/pin";

	// 拉高GPIO
	public static void openGPIO(String gpio) {
		// echo "-wmode 2 1" > /sys/devices/virtual/misc/mtgpio/pin
		String[] cmd1 = {
				"/system/bin/sh",
				"-c",
				"echo \"-wmode " + gpio
						+ " 0\" > /sys/devices/virtual/misc/mtgpio/pin" };

		// echo "-wdir 2 1" > /sys/devices/virtual/misc/mtgpio/pin
		String[] cmd2 = {
				"/system/bin/sh",
				"-c",
				"echo \"-wdir " + gpio
						+ " 1\" > /sys/devices/virtual/misc/mtgpio/pin" };

		// echo "-wdout 2 1" > /sys/devices/virtual/misc/mtgpio/pin
		String[] cmd3 = {
				"/system/bin/sh",
				"-c",
				"echo \"-wdout " + gpio
						+ " 1\" > /sys/devices/virtual/misc/mtgpio/pin" };
		try {
			ShellExe.execCommand(cmd1);
			ShellExe.execCommand(cmd2);
			ShellExe.execCommand(cmd3);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 拉低GPIO
	public static void closeGPIO(String gpio) {
		// echo "-wdout 2 0" > /sys/devices/virtual/misc/mtgpio/pin
		String[] cmd4 = {
				"/system/bin/sh",
				"-c",
				"echo \"-wdout " + gpio
						+ " 0\" > /sys/devices/virtual/misc/mtgpio/pin" };
		try {
			ShellExe.execCommand(cmd4);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 查询GPIO状态
	public static String searchGPIO(String path, String gpio) {
		String[] cmd5 = { "/system/bin/sh", "-c",
				"cat " + path + " | grep -rni " + gpio };
		try {
			ShellExe.execCommand(cmd5);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String result = ShellExe.getOutput();

		// 解决对讲在后台运行时，不停的按热键会报错的问题
		if ("26".equals(gpio) && result.length() == 0) {
			Log.d("lwj123123", "result.length():");
			com.runyee.ptt.txrx.TxRxFragment.flag_fatal = true;
			return "0010000-1";
		}
		return result.substring(result.indexOf(gpio) + gpio.length() + 1);// result.substring(1,5);//
	}

	// 修改GPIO所有位的值(val的格式为 101:1 1 1 0 0 0 1)
	public static void modifiedGPIO(String val) {
		// echo "-w=101:1 1 0 0 1 1 0" > /sys/devices/virtual/misc/mtgpio/pin
		String[] cmd = {
				"/system/bin/sh",
				"-c",
				"echo \"-w=" + val
						+ " \" > /sys/devices/virtual/misc/mtgpio/pin" };
		try {
			ShellExe.execCommand(cmd);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
