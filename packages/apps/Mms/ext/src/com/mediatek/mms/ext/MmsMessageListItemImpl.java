/* Copyright Statement:
 *
 * This software/firmware and related documentation ("MediaTek Software") are
 * protected under relevant copyright laws. The information contained herein
 * is confidential and proprietary to MediaTek Inc. and/or its licensors.
 * Without the prior written permission of MediaTek inc. and/or its licensors,
 * any reproduction, modification, use or disclosure of MediaTek Software,
 * and information contained herein, in whole or in part, shall be strictly prohibited.
 *
 * MediaTek Inc. (C) 2012. All rights reserved.
 *
 * BY OPENING THIS FILE, RECEIVER HEREBY UNEQUIVOCALLY ACKNOWLEDGES AND AGREES
 * THAT THE SOFTWARE/FIRMWARE AND ITS DOCUMENTATIONS ("MEDIATEK SOFTWARE")
 * RECEIVED FROM MEDIATEK AND/OR ITS REPRESENTATIVES ARE PROVIDED TO RECEIVER ON
 * AN "AS-IS" BASIS ONLY. MEDIATEK EXPRESSLY DISCLAIMS ANY AND ALL WARRANTIES,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NONINFRINGEMENT.
 * NEITHER DOES MEDIATEK PROVIDE ANY WARRANTY WHATSOEVER WITH RESPECT TO THE
 * SOFTWARE OF ANY THIRD PARTY WHICH MAY BE USED BY, INCORPORATED IN, OR
 * SUPPLIED WITH THE MEDIATEK SOFTWARE, AND RECEIVER AGREES TO LOOK ONLY TO SUCH
 * THIRD PARTY FOR ANY WARRANTY CLAIM RELATING THERETO. RECEIVER EXPRESSLY ACKNOWLEDGES
 * THAT IT IS RECEIVER'S SOLE RESPONSIBILITY TO OBTAIN FROM ANY THIRD PARTY ALL PROPER LICENSES
 * CONTAINED IN MEDIATEK SOFTWARE. MEDIATEK SHALL ALSO NOT BE RESPONSIBLE FOR ANY MEDIATEK
 * SOFTWARE RELEASES MADE TO RECEIVER'S SPECIFICATION OR TO CONFORM TO A PARTICULAR
 * STANDARD OR OPEN FORUM. RECEIVER'S SOLE AND EXCLUSIVE REMEDY AND MEDIATEK'S ENTIRE AND
 * CUMULATIVE LIABILITY WITH RESPECT TO THE MEDIATEK SOFTWARE RELEASED HEREUNDER WILL BE,
 * AT MEDIATEK'S OPTION, TO REVISE OR REPLACE THE MEDIATEK SOFTWARE AT ISSUE,
 * OR REFUND ANY SOFTWARE LICENSE FEES OR SERVICE CHARGE PAID BY RECEIVER TO
 * MEDIATEK FOR SUCH MEDIATEK SOFTWARE AT ISSUE.
 *
 * The following software/firmware and/or related documentation ("MediaTek Software")
 * have been modified by MediaTek Inc. All revisions are subject to any receiver's
 * applicable license agreements with MediaTek Inc.
 */

package com.mediatek.mms.ext;

import android.content.Context;
import android.content.ContextWrapper;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mediatek.encapsulation.MmsLog;

public class MmsMessageListItemImpl extends ContextWrapper implements IMmsMessageListItem {
    private static final String TAG = "Mms/MmsMessageListItemImpl";
    private IMmsMessageListItemHost mHost = null;

    public MmsMessageListItemImpl(Context context) {
        super(context);
    }

    public void init(IMmsMessageListItemHost host) {
        mHost = host;
        return;
    }

    protected IMmsMessageListItemHost getHost() {
        return mHost;
    }

    public void showSimType(Context context, int slotId, TextView textView) {
        MmsLog.d(TAG, "showSimType default");
    }

    /**
     * M: show dwonload button
     *
     * @return
     */
    public boolean showDownloadButton() {
        return false;
    }

    /**
     * M:
     *
     * @return
     * @author Youxiu Wang
     */
    public boolean hideDownloadButton() {
        return false;
    }

    /**
     * M:
     *
     * @return
     */
    public boolean hideAllButton() {
        return false;
    }

    public String getSentDateStr(Context context, String srcTxt, long msgId, int msgType,
            long smsSentDate, int boxId) {
        return srcTxt;
    }

    public void drawMassTextMsgStatus(Context context, boolean isSms, long timestamp) {
    }

    public boolean needEditFailedMessge(Context context, long msgId, long timeStamp) {
        return true;
    }

    public boolean setNotifyContent(String address, String subject, String msgSizeText,
            String expireText, TextView expireTextView) {
        return false;
    }

    public boolean isEnableShowDualTime() {
        MmsLog.d(TAG, "isEnableShowDualTime: false");
        return false;
    }

    public void setDualTime(Context context, boolean isRecievedMsg, int soltId, TextView dateView,
            LinearLayout linearLayout, String timeDate) {

    }

    /// M: New plugin API @{
    public boolean showStorageFullToast(Context context) {
        return false;
    }
    /// @}
}