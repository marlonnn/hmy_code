package com.BC.entertainment.chatroom.module;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import com.netease.nimlib.sdk.chatroom.constant.MemberType;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMember;

public class ChatRoomComparator {
	
	private  static Map<MemberType, Integer> compMap = new HashMap<>();

    static {
        compMap.put(MemberType.CREATOR, 0);
        compMap.put(MemberType.ADMIN, 1);
        compMap.put(MemberType.NORMAL, 2);
        compMap.put(MemberType.LIMITED, 3);
        compMap.put(MemberType.GUEST, 4);
    }

    public static Comparator<ChatRoomMember> comp = new Comparator<ChatRoomMember>() {
        @Override
        public int compare(ChatRoomMember lhs, ChatRoomMember rhs) {
            if (lhs == null) {
                return 1;
            }

            if (rhs == null) {
                return -1;
            }

            return compMap.get(lhs.getMemberType()) - compMap.get(rhs.getMemberType());
        }
    };

}
