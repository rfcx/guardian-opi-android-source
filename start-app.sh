#!/system/bin/sh

export PATH=/sbin:/vendor/bin:/system/sbin:/system/bin:/system/xbin

if (! ps | grep -q "org.rfcx.guardian.guardian")
then
	sleep 180
	am start -n org.rfcx.guardian.guardian/.activity.MainActivity 2> /data/local/log.txt
	input keyevent 3
	echo "app started" > /data/local/result.txt
fi
