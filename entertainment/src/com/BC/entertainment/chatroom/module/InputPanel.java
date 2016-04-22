package com.BC.entertainment.chatroom.module;

import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.BC.entertainment.chatroom.gift.Gift;
import com.BC.entertainmentgravitation.R;
import com.netease.nim.uikit.common.util.string.StringUtil;
import com.netease.nim.uikit.session.module.Container;
import com.netease.nimlib.sdk.chatroom.ChatRoomMessageBuilder;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

public class InputPanel {
	
	private final int SHOW_LAYOUT_DELAY = 200;

    private Container container;
    private View view;
    private Handler uiHandler;
    
    protected EditText messageEditText;// 文本消息编辑框
    
    protected View sendMessageButtonInInputBar;// 发送消息按钮
    
    protected View messageInputBar;//输入框控件整体
    
    protected View giftBottomLayout; // 礼物布局
    
    private boolean giftBottomLayoutHasSetup = false;// 礼物布局状态
    
    private List<Gift> gifts;//赠送的礼物

	private LinearLayout messageActivityBottomLayout;//输入框和礼物整体根布局
	
	private boolean isKeyboardShowed = true; // 是否显示键盘

	private ViewPager viewPager;//礼物的滑动页

	private ViewGroup indicator;//滚动点
	
    public InputPanel(Container container, View view, List<Gift> gifts) {
        this.container = container;
        this.view = view;
        this.gifts = gifts;
        this.uiHandler = new Handler();
        initViews();
    	initInputBarListener();
    	initTextEdit();//初始化输入框控件
    	
    }
    
    /**
     * 发送消息或者发送礼物初始化控件
     * @param isSendGift
     */
    public void init(boolean isSendGift)
    {

    	if (isSendGift)
    	{
    		hideInputBar();
    		showGiftLayout();
    	}
    	else
    	{
    		showInputBar();
    		hideGiftLayout();
        	restoreText(false);
    	}
    }
    
    private void initViews()
    {
        // input bar
        messageActivityBottomLayout = (LinearLayout) view.findViewById(R.id.messageActivityBottomLayout);
        messageInputBar = view.findViewById(R.id.textMessageLayout);
        sendMessageButtonInInputBar = view.findViewById(R.id.buttonSendMessage);
        messageEditText = (EditText) view.findViewById(R.id.editTextMessage);
        
        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        indicator = (ViewGroup) view.findViewById(R.id.actions_page_indicator);
    }
    
    private void initInputBarListener()
    {
    	sendMessageButtonInInputBar.setOnClickListener(clickListener);
    }
    
    private void initTextEdit(){
    	messageEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        messageEditText.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    switchToTextLayout(true);
                }
                return false;
            }
        });
        
        messageEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                messageEditText.setHint("");
                checkSendButtonEnable(messageEditText);
            }
        });
        
        messageEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				
			}

			@Override
			public void afterTextChanged(Editable s) {
				checkSendButtonEnable(messageEditText);
			}
        	
        });
    }
    
    private void restoreText(boolean clearText) {
        if (clearText) {
            messageEditText.setText("");
        }

        checkSendButtonEnable(messageEditText);
    }
    
    // 点击edittext，切换键盘和礼物布局
    private void switchToTextLayout(boolean needShowInput) {
        hideGiftLayout();

        messageEditText.setVisibility(View.VISIBLE);

        messageInputBar.setVisibility(View.VISIBLE);

        if (needShowInput) {
            uiHandler.postDelayed(showTextRunnable, SHOW_LAYOUT_DELAY);
        } else {
            hideInputMethod();
        }
    }
    
    // 初始化礼物布局
    private void addGiftLayout() {
        if (giftBottomLayout == null) {
            giftBottomLayout = view.findViewById(R.id.giftLayout);
            giftBottomLayoutHasSetup = false;
        }
        initGiftLayout();
    }
    
    // 初始化具体礼物layout中的项目
    private void initGiftLayout() {
        if (giftBottomLayoutHasSetup) {
            return;
        }

//        ActionsPanel.init(view, gifts);
        giftBottomLayoutHasSetup = true;
    }
    
    //显示礼物按钮，切换礼物布局和键盘
    public void ToggleGiftLayout() {
        if (giftBottomLayout == null || giftBottomLayout.getVisibility() == View.GONE) {
            showGiftLayout();
        } else {
            hideGiftLayout();
        }
    }
    
    private Runnable showGiftFuncRunnable = new Runnable() {
        @Override
        public void run() {
            giftBottomLayout.setVisibility(View.VISIBLE);
        }
    };
    
    private Runnable showTextRunnable = new Runnable() {
        @Override
        public void run() {
            showInputMethod(messageEditText);
        }
    };
    
    // 显示键盘布局
    private void showInputMethod(EditText editTextMessage) {
        editTextMessage.requestFocus();
        //如果已经显示,则继续操作时不需要把光标定位到最后
        if (!isKeyboardShowed) {
            editTextMessage.setSelection(editTextMessage.getText().length());
            isKeyboardShowed = true;
        }

        InputMethodManager imm = (InputMethodManager) container.activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editTextMessage, 0);

    }
    
    // 隐藏键盘布局
    private void hideInputMethod() {
        isKeyboardShowed = false;
        uiHandler.removeCallbacks(showTextRunnable);
        InputMethodManager imm = (InputMethodManager) container.activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(messageEditText.getWindowToken(), 0);
        messageEditText.clearFocus();
    }
    
    // 显示礼物布局
    private void showGiftLayout() {
        addGiftLayout();
        hideInputMethod();

        uiHandler.postDelayed(showGiftFuncRunnable, SHOW_LAYOUT_DELAY);
    }
    
    // 隐藏礼物布局
    private void hideGiftLayout() {
        uiHandler.removeCallbacks(showGiftFuncRunnable);
        if (giftBottomLayout != null) {
            giftBottomLayout.setVisibility(View.GONE);
        }
    }
    
    //显示输入条
    private void showInputBar()
    {
    	if (messageInputBar != null)
    	{
        	messageInputBar.setVisibility(View.VISIBLE);
    	}
    }
    
    //隐藏输入条
    private void hideInputBar()
    {
    	if (messageInputBar != null)
    	{
        	messageInputBar.setVisibility(View.GONE);
    	}
    }
    
    private void checkSendButtonEnable(EditText editText) {
        String textMessage = editText.getText().toString();
        if (!TextUtils.isEmpty(StringUtil.removeBlanks(textMessage)) && editText.hasFocus()) {
        	sendMessageButtonInInputBar.setEnabled(true);
        } else {
        	sendMessageButtonInInputBar.setEnabled(false);
        }
    }
    
    /**
     * ************************* 键盘布局切换 *******************************
     */
    private View.OnClickListener clickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			if (v == sendMessageButtonInInputBar) {
                onTextMessageSendButtonPressed();
            }			
		}
    };
    
    /**
     * 发送文本消息
     */
    private void onTextMessageSendButtonPressed(){
        IMMessage textMessage;
        String text = messageEditText.getText().toString();
        if (container.sessionType == SessionTypeEnum.ChatRoom) {
            textMessage = ChatRoomMessageBuilder.createChatRoomTextMessage(container.account, text);
        } else {
            textMessage = MessageBuilder.createTextMessage(container.account, container.sessionType, text);
        }
        //发送消息
        if (container.proxy.sendMessage(textMessage)) {
            restoreText(true);
        }
    }
}
