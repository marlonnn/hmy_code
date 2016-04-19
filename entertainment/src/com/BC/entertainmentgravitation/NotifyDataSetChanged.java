package com.BC.entertainmentgravitation;

import java.util.LinkedList;

import com.BC.entertainmentgravitation.entity.ChatMessage;

public interface NotifyDataSetChanged {
	void dataSetChanged(LinkedList<ChatMessage> chatMessages);
}
