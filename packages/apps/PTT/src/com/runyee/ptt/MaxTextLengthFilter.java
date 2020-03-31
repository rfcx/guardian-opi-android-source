package com.runyee.ptt;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.Gravity;
import android.widget.Toast;

public class MaxTextLengthFilter implements InputFilter {

	private int mMaxLength;
	private Toast toast;
	private Context mContext;

	public MaxTextLengthFilter(Context contxet, int max) {
		mMaxLength = max;
		mContext = contxet;
	}

	public CharSequence filter(CharSequence source, int start, int end,
			Spanned dest, int dstart, int dend) {
		toast = Toast.makeText(mContext,
				"字符不能超过" + String.valueOf(dest.length()) + "个", 800);
		toast.setGravity(Gravity.TOP, 0, 0);
		android.util.Log.d("lwj", "start=" + start + " end:" + " dstart:"
				+ dstart + " dend:" + dend + " dest.length():" + dest.length()
				+ " source:" + source);

		int keep = mMaxLength - (dest.length() - (dend - dstart));
		if (keep < (end - start)) {
			toast.show();
		} else {
			/*
			 * 限制输入字符为012345678 输入其他字符时弹出提示
			 */
			if (mMaxLength == 1) {
				Pattern p_sq = Pattern.compile("[0-8]*");
				Matcher m_sq = p_sq.matcher(source);
				if (!m_sq.matches()) {
					toast = Toast.makeText(mContext, R.string.sq_valid_text,
							Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.TOP, 0, 0);
					toast.show();
				}
			}
			/*
			 * 限制输入字符为0123456789. 输入其他字符时弹出提示
			 */
			if (mMaxLength == 7 && (source.length() == 1)) {
				Pattern p_txrx = Pattern.compile("[0-9]*");
				Matcher m_txrx = p_txrx.matcher(source);
				Pattern p_sq = Pattern.compile("[.]");
				Matcher m_sq = p_sq.matcher(source);
				if (!m_sq.matches() && !m_txrx.matches()) {
					toast = Toast.makeText(mContext,
							R.string.edittext_valid_text, Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.TOP, 0, 0);
					toast.show();
				}
			}
		}
		if (keep <= 0) {
			return "";
		} else if (keep >= end - start) {
			return null;
		} else {
			return source.subSequence(start, start + keep);
		}
	}
}
