# AOSP for OrangePi 3G-Iot

This repo contains changes
- Add i2c module
- Remove unused app
- Enable gps location by default
- Android will boot up when powered
- Allow SYSTEM uid to use su(super user)
- SSH supported
- GPIO permission for external application

The repo contains the folders that same path as the full AOSP. Except **boot-changes** and files outside folders.

### How to apply AOSP build changes

1. Download original AOSP for OrangePi 3G-Iot from [here](https://mega.nz/#F!q8xQXZBQ!CXsQgfR2JaFsttTtBT_GMQ!ewpRRKLC)
2. Clone this repo
3. Apply changes by copy files in the repo and replace them to the original AOSP
4. ***EXCEPT the /packages/app/ folder, you need to delete all of original ones and paste these ones from this repo instead**

### How to apply boot-changes

1. Once AOSP compiled, you need to unpack boot.img by using **unpack-MTK.pl** in this repo
2. Replace init.rc and add reboot.sh in the same place.
3. Repack by using **repack-MTK.pl**

More infomation for building and edit init.rc. Please [visit](https://confluence.rfcx.org/display/RD/How+to+compile+Android+for+Orange+Pi+3G-IoT)

