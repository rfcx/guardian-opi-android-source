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

package com.android.settings;
import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
public class LogoPreference extends Preference {
	
 public LogoPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }


  public LogoPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

  public LogoPreference(Context context) {
        super(context);
        init();
    }
    private void init() {
        setLayoutResource(R.layout.logo_preference_layout);
    }


	public View getView(View convertView, ViewGroup parent) {
        View view = super.getView(convertView, parent);
        return view;
    }


}
