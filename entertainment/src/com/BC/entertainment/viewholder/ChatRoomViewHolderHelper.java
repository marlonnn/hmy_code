package com.BC.entertainment.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.BC.entertainmentgravitation.R;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nimlib.sdk.chatroom.constant.MemberType;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessage;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;

import java.util.Map;

/**
 * Created by hzxuwen on 2016/1/20.
 */
public class ChatRoomViewHolderHelper {

    public static void setNameTextView(ChatRoomMessage message, TextView text, ImageView imageView, Context context) {
        if (message.getMsgType() != MsgTypeEnum.notification) {
            if (message.getChatRoomMessageExtension() != null) {
                text.setText(message.getChatRoomMessageExtension().getSenderNick());
            } else {
                text.setText(NimUserInfoCache.getInstance().getUserName(message.getFromAccount()));
            }

            text.setTextColor(context.getResources().getColor(R.color.color_black_ff999999));
            text.setVisibility(View.VISIBLE);
            setNameIconView(message, imageView);
        }
    }

    private static void setNameIconView(ChatRoomMessage message, ImageView nameIconView) {
        final String KEY = "type";
        Map<String, Object> ext = message.getRemoteExtension();
        if (ext == null || !ext.containsKey(KEY)) {
            nameIconView.setVisibility(View.GONE);
            return;
        }

        MemberType type = MemberType.typeOfValue((Integer) ext.get(KEY));
        if (type == MemberType.ADMIN) {
            nameIconView.setImageResource(R.drawable.admin_icon);
            nameIconView.setVisibility(View.VISIBLE);
        } else if (type == MemberType.CREATOR) {
            nameIconView.setImageResource(R.drawable.master_icon);
            nameIconView.setVisibility(View.VISIBLE);
        } else {
            nameIconView.setVisibility(View.GONE);
        }
    }
}
