package com.BC.entertainment.chatroom.helper;

import android.text.TextUtils;

import com.netease.nimlib.sdk.chatroom.model.ChatRoomMember;
//import com.netease.nim.demo.DemoCache;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomNotificationAttachment;

import java.util.List;

/**
 * Created by huangjun on 2016/1/13.
 */
public class ChatRoomNotificationHelper {
	
    public static String getNotificationText(ChatRoomNotificationAttachment attachment) {
        if (attachment == null) {
            return "";
        }

        String targets = getTargetNicks(attachment);
        String text;
        switch (attachment.getType()) {
            case ChatRoomMemberIn:
                text = buildText("��ӭ", targets, "����ֱ����");
                break;
            case ChatRoomMemberExit:
                text = buildText(targets, "�뿪��ֱ����");
                break;
            case ChatRoomMemberBlackAdd:
                text = buildText(targets, "������Ա���������");
                break;
            case ChatRoomMemberBlackRemove:
                text = buildText(targets, "������Ա�������");
                break;
            case ChatRoomMemberMuteAdd:
                text = buildText(targets, "������Ա����");
                break;
            case ChatRoomMemberMuteRemove:
                text = buildText(targets, "������Ա�������");
                break;
            case ChatRoomManagerAdd:
                text = buildText(targets, "����������Ա���");
                break;
            case ChatRoomManagerRemove:
                text = buildText(targets, "���������Ա���");
                break;
            case ChatRoomCommonAdd:
                text = buildText(targets, "����Ϊ��ͨ��Ա");
                break;
            case ChatRoomCommonRemove:
                text = buildText(targets, "��ȡ����ͨ��Ա");
                break;
            case ChatRoomClose:
                text = buildText("ֱ���䱻�ر�");
                break;
            case ChatRoomInfoUpdated:
                text = buildText("ֱ������Ϣ�Ѹ���");
                break;
            case ChatRoomMemberKicked:
                text = buildText(targets, "���߳�ֱ����");
                break;
            default:
                text = attachment.toString();
                break;
        }

        return text;
    }

    private static String getTargetNicks(final ChatRoomNotificationAttachment attachment) {
        StringBuilder sb = new StringBuilder();
        List<String> accounts = attachment.getTargets();
        List<String> targets = attachment.getTargetNicks();
        if (attachment.getTargetNicks() != null) {
            for (int i = 0; i < targets.size(); i++) {
//                sb.append(DemoCache.getAccount().equals(accounts.get(i)) ? "��" : targets.get(i));
                sb.append(",");
            }
            sb.deleteCharAt(sb.length() - 1);
        }

        return sb.toString();
    }

    public static String buildText(String pre, String targets, String operate) {
        StringBuilder sb = new StringBuilder();
        if (!TextUtils.isEmpty(pre)) {
            sb.append(pre);
        }

        if (!TextUtils.isEmpty(targets)) {
            sb.append(targets);
        }

        if (!TextUtils.isEmpty(operate)) {
            sb.append(operate);
        }

        return sb.toString();
    }

    private static String buildText(String targets, String operate) {
        return buildText(null, targets, operate);
    }

    private static String buildText(String operate) {
        return buildText(null, operate);
    }
}
