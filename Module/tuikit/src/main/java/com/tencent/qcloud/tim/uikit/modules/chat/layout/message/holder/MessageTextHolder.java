package com.tencent.qcloud.tim.uikit.modules.chat.layout.message.holder;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.tencent.qcloud.tim.uikit.R;
import com.tencent.qcloud.tim.uikit.component.face.FaceManager;
import com.tencent.qcloud.tim.uikit.modules.message.MessageInfo;

public class MessageTextHolder extends MessageContentHolder {

    private static final String TAG =MessageTextHolder.class.getSimpleName() ;
    private TextView msgBodyText;

    public MessageTextHolder(View itemView) {
        super(itemView);
    }

    @Override
    public int getVariableLayout() {
        return R.layout.message_adapter_content_text;
    }

    @Override
    public void initVariableViews() {
        msgBodyText = rootView.findViewById(R.id.msg_body_tv);
    }

    @Override
    public void layoutVariableViews(MessageInfo msg, int position) {
        msgBodyText.setVisibility(View.VISIBLE);
        Log.d(TAG, "layoutVariableViews: " + msg.toString());
        if (msg.getExtra() != null) {
            FaceManager.handlerEmojiText(msgBodyText, msg.getExtra().toString(), false);
        }
        if (properties.getChatContextFontSize() != 0) {
            msgBodyText.setTextSize(properties.getChatContextFontSize());
        }
        if (msg.isSelf()) {
            if (properties.getRightChatContentFontColor() != 0) {
                msgBodyText.setTextColor(properties.getRightChatContentFontColor());
            }
        } else {
            if (properties.getLeftChatContentFontColor() != 0) {
                msgBodyText.setTextColor(properties.getLeftChatContentFontColor());
            }
        }
    }

}