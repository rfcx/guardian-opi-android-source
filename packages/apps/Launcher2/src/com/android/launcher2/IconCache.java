/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.launcher2;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.mediatek.common.featureoption.FeatureOption;
import com.mediatek.launcher2.ext.DataUtil;
import com.mediatek.launcher2.ext.LauncherLog;
import com.android.launcher.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Cache of application icons.  Icons can be made from any thread.
 */
public class IconCache {
    @SuppressWarnings("unused")
    private static final String TAG = "IconCache";

    private static final int INITIAL_ICON_CACHE_CAPACITY = 50;

    private static class CacheEntry {
        public Bitmap icon;
        public String title;
    }

    private final Bitmap mDefaultIcon;
    private final Context mContext;
    private final PackageManager mPackageManager;
    private final HashMap<ComponentName, CacheEntry> mCache =
            new HashMap<ComponentName, CacheEntry>(INITIAL_ICON_CACHE_CAPACITY);
    private int mIconDpi;

    
    private HashMap<String, String> mCustomApkName = new HashMap<String, String>(INITIAL_ICON_CACHE_CAPACITY);
	public HashMap<String, Drawable> mCustomIconMap = new HashMap<String, Drawable>(INITIAL_ICON_CACHE_CAPACITY);
	private ArrayList<Integer> mWrapImgs = new ArrayList<Integer>(6);
	public HashMap<String, Drawable> mThirdIconMap = new HashMap<String, Drawable>(INITIAL_ICON_CACHE_CAPACITY);
	
	private Resources mResources;
	private String[] mKeyWords = new String[2];
	
    private float mScaleX;
    private float mScaleY; 
    private float mRadius;
    private float mScaleTarget;
    
    public IconCache(Context context) {
        ActivityManager activityManager =
                (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        mContext = context;
        mResources = context.getResources();
        mPackageManager = context.getPackageManager();
        mIconDpi = activityManager.getLauncherLargeIconDensity();
        if (LauncherLog.DEBUG) {
            LauncherLog.d(TAG, "IconCache, mIconDpi = " + mIconDpi);
        }
        // need to set mIconDpi before getting default icon
        mDefaultIcon = makeDefaultIcon();
        
        mScaleX = mResources.getInteger(R.integer.config_icon_scale_x) / (float) 100;
		mScaleY = mResources.getInteger(R.integer.config_icon_scale_y) / (float) 100;
		mRadius = mResources.getInteger(R.integer.config_icon_clip_radius) / (float) 100;
		
		mScaleTarget = mResources.getInteger(R.integer.config_third_icon_scale_target) / (float) 100;
		
		initHCTCustomApkIcon();
		initHCTCustomApkName();
		initHctAppBackgroundWrapImages();
		
       
    }

    private void initHCTCustomApkIcon() {
		mCustomIconMap.clear();
		mThirdIconMap.clear();

		String packageName = mResources.getResourcePackageName(R.array.hct_custom_apkIcon);

		String[] icons = mResources.getStringArray(R.array.hct_custom_apkIcon);
		int length = icons.length;

		for (int i = 0; i < length; i++) {
			mKeyWords = icons[i].split("\\|");

			Drawable d = null;
			int resId = mResources.getIdentifier(mKeyWords[1].trim(), "drawable", packageName);

			try {
				d = mResources.getDrawable(resId);
			} catch (Exception e) {
				Log.d(TAG, "no resource found, className->" + mKeyWords[0]);
			}

			mCustomIconMap.put(mKeyWords[0].trim(), d);
		}
	}

	private void initHCTCustomApkName() {
		String[] names = mResources.getStringArray(R.array.hct_custom_apkName);
		int length = names.length;
		mCustomApkName.clear();

		for (int i = 0; i < length; i++) {
			mKeyWords = names[i].split("\\|");
			mCustomApkName.put(mKeyWords[0].trim(), mKeyWords[1].trim());
		}
	}

	private void initHctAppBackgroundWrapImages() {
		mWrapImgs.clear();

		String packageName = mResources.getResourcePackageName(R.array.hct_bg_app_wrap);
		final String[] extras = mResources.getStringArray(R.array.hct_bg_app_wrap);

		for (String extra : extras) {
			int resId = mResources.getIdentifier(extra, "drawable", packageName);

			if (resId != 0) {
				mWrapImgs.add(resId);
			}
		}
	}

	public boolean isHCTCustomIcon(String clsName) {
		return (null == mCustomIconMap.get(clsName)) ? false : true;
	}
    
    public Drawable getFullResDefaultActivityIcon() {
        return getFullResIcon(Resources.getSystem(),
                android.R.mipmap.sym_def_app_icon);
    }

    public Drawable getFullResIcon(Resources resources, int iconId) {
        Drawable d;
        try {
            d = resources.getDrawableForDensity(iconId, mIconDpi);
        } catch (Resources.NotFoundException e) {
            d = null;
        }

        return (d != null) ? d : getFullResDefaultActivityIcon();
    }

    public Drawable getFullResIcon(String packageName, int iconId) {
        Resources resources;
        try {
            resources = mPackageManager.getResourcesForApplication(packageName);
        } catch (PackageManager.NameNotFoundException e) {
            resources = null;
        }
        if (resources != null) {
            if (iconId != 0) {
                return getFullResIcon(resources, iconId);
            }
        }
        return getFullResDefaultActivityIcon();
    }

    public Drawable getFullResIcon(ResolveInfo info) {
        return getFullResIcon(info.activityInfo);
    }

    public Drawable getFullResIcon(ActivityInfo info) {
		Resources resources;

		try {
			resources = mPackageManager.getResourcesForApplication(info.applicationInfo);
		} catch (PackageManager.NameNotFoundException e) {
			resources = null;
		}

		if (resources != null) {
			int iconId = info.getIconResource();

			if (iconId != 0) {
				if (LauncherModel.mIsCustomApkIcon) {
					return getIconCache(info);
				} else {
					return getFullResIcon(resources, iconId);
				}
			}
		}

		return getFullResDefaultActivityIcon();
	}

    private Bitmap makeDefaultIcon() {
        Drawable d = getFullResDefaultActivityIcon();
        Bitmap b = Bitmap.createBitmap(Math.max(d.getIntrinsicWidth(), 1),
                Math.max(d.getIntrinsicHeight(), 1),
                Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        d.setBounds(0, 0, b.getWidth(), b.getHeight());
        d.draw(c);
        c.setBitmap(null);
        return b;
    }

    /**
     * Remove any records for the supplied ComponentName.
     */
    public void remove(ComponentName componentName) {
        synchronized (mCache) {
            mCache.remove(componentName);
        }
    }

    /**
     * Empty out the cache.
     */
    public void flush() {
        synchronized (mCache) {
            /// M: Cause GC free memory
            for (ComponentName cn : mCache.keySet()) {
                CacheEntry e = mCache.get(cn);
                e.icon = null;
                e.title = null;
                e = null;
            }

            mCache.clear();

            /// M: Add for smart book feature. Need to update mIconDpi when plug in/out smart book.
            if (FeatureOption.MTK_SMARTBOOK_SUPPORT) {
                ActivityManager activityManager =
                        (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
                mIconDpi = activityManager.getLauncherLargeIconDensity();
                if (LauncherLog.DEBUG) {
                    LauncherLog.d(TAG, "flush, mIconDpi = " + mIconDpi);
                }
            }
        }

        if (LauncherLog.DEBUG) {
            LauncherLog.d(TAG, "Flush icon cache here.");
        }
    }

    /**
     * Fill in "application" with the icon and label for "info."
     */
    public void getTitleAndIcon(ApplicationInfo application, ResolveInfo info,
            HashMap<Object, CharSequence> labelCache) {
        CacheEntry entry = cacheLocked(application.componentName, info, labelCache);

        application.title = entry.title;
        application.iconBitmap = entry.icon;
    }

    public Bitmap getIcon(Intent intent) {
        final ResolveInfo resolveInfo = mPackageManager.resolveActivity(intent, 0);
        ComponentName component = intent.getComponent();

        if (resolveInfo == null || component == null) {
            return mDefaultIcon;
        }

        CacheEntry entry = cacheLocked(component, resolveInfo, null);
        return entry.icon;
    }

    public Bitmap getIcon(ComponentName component, ResolveInfo resolveInfo,
            HashMap<Object, CharSequence> labelCache) {
        if (resolveInfo == null || component == null) {
            return null;
        }

        CacheEntry entry = cacheLocked(component, resolveInfo, labelCache);
        return entry.icon;
    }

    public boolean isDefaultIcon(Bitmap icon) {
        return mDefaultIcon == icon;
    }

    private CacheEntry cacheLocked(ComponentName componentName, ResolveInfo info,
            HashMap<Object, CharSequence> labelCache) {
        if (LauncherLog.DEBUG_LAYOUT) {
            LauncherLog.d(TAG, "cacheLocked: componentName = " + componentName
                    + ", info = " + info + ", HashMap<Object, CharSequence>:size = "
                    +  ((labelCache == null) ? "null" : labelCache.size()));
        }
        // M: for multi-thread, it may remove and clear entry when add it
        CacheEntry retVal = new CacheEntry();
        retVal.icon = null;
        retVal.title = null;

        CacheEntry entry = null;
        boolean isCached = false;
        synchronized (mCache) {
            entry = mCache.get(componentName);

            if (entry != null ) {
                retVal.icon = entry.icon;
                retVal.title = entry.title;
                if (entry.title == null || entry.icon == null) {
                    mCache.remove(componentName);
                } else {
                    isCached = true;
                }
            }
        }

        if (!isCached) {
            entry = new CacheEntry();

            ComponentName key = DataUtil.getInstance().getComponentNameFromResolveInfo(info);
            if (labelCache != null && labelCache.containsKey(key)) {
                retVal.title = labelCache.get(key).toString();
                if (LauncherLog.DEBUG_LOADERS) {
                    LauncherLog.d(TAG, "CacheLocked get title from cache: title = " + retVal.title);
                }
            } else {
                retVal.title = info.loadLabel(mPackageManager).toString();
                if (LauncherLog.DEBUG_LOADERS) {
                    LauncherLog.d(TAG, "CacheLocked get title from pms: title = " + retVal.title);
                }
                if (labelCache != null) {
                    labelCache.put(key, retVal.title);
                }
            }
            if (retVal.title == null) {
                retVal.title = info.activityInfo.name;
                if (LauncherLog.DEBUG_LOADERS) {
                    LauncherLog.d(TAG, "CacheLocked get title from activity information: entry.title = " + retVal.title);
                }
            }

            entry.title = retVal.title;
            retVal.icon = DataUtil.getInstance().createIconBitmap(
                    getFullResIcon(info), mContext);
            entry.icon = retVal.icon;
        }
        
        synchronized (mCache) {
            if (mCache.get(componentName) == null) {
                mCache.put(componentName, entry);
            }
        }
        return retVal;
    }

    public HashMap<ComponentName,Bitmap> getAllIcons() {
        synchronized (mCache) {
            HashMap<ComponentName,Bitmap> set = new HashMap<ComponentName,Bitmap>();
            for (ComponentName cn : mCache.keySet()) {
                final CacheEntry e = mCache.get(cn);
                set.put(cn, e.icon);
            }
            return set;
        }
    }
    
    public Drawable getIconCache(ActivityInfo info) {
		Resources resources = null;
		int iconId = 0;

		try {
			resources = mPackageManager.getResourcesForApplication(info.applicationInfo);
		} catch (PackageManager.NameNotFoundException e) {
			resources = null;
		}

		if (resources != null) {
			iconId = info.getIconResource();
		}

		return getIconCache(info.packageName, info.name, resources, iconId);
	}
    
    private Drawable getIconCache(String pkgName, String clsName, Resources resources, int iconId) {
		Drawable drawable = null;
		final String strKey = pkgName + ":" + clsName;

		if (null != strKey && null != resources) {
			if (isHCTCustomIcon(clsName)) {
				drawable = mCustomIconMap.get(clsName);
			} else {
				if (mThirdIconMap.containsKey(strKey)) {
					drawable = mThirdIconMap.get(strKey);
				} else {
					drawable = getFullResIcon(resources, iconId);
					drawable = hctCovertThirdIcon(strKey, drawable);

					mThirdIconMap.put(strKey, drawable);
				}
			}
		}

		return ((null == drawable) ? getFullResDefaultActivityIcon() : drawable);
	}
    
    private Drawable hctCovertThirdIcon(String clsName, Drawable drawable) {
		Drawable bg = mResources.getDrawable(generateRandomImg(clsName));
		Bitmap bitmap = Utilities.createCompoundBitmap(Utilities.drawableToBitmap(bg), Utilities.drawableToBitmap(drawable), mScaleX, mScaleY, mRadius,mScaleTarget);

		return (new BitmapDrawable(mResources, bitmap));
	}
    public int generateRandomImg(String clsName) {
		int length = 0;

		if (clsName != null) {
			length = clsName.length();
		}

		int n = mWrapImgs.size();

		int i = length % n;

		return mWrapImgs.get(i);
	}
}
