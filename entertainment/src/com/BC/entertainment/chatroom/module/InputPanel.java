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
    
    protected EditText messageEditText;// �ı���Ϣ�༭��
    
    protected View sendMessageButtonInInputBar;// ������Ϣ��ť
    
    protected View messageInputBar;//�����ؼ�����
    
    protected View giftBottomLayout; // ���ﲼ��
    
    private boolean giftBottomLayoutHasSetup = false;// ���ﲼ��״̬
    
    private List<Gift> gifts;//���͵�����

	private LinearLayout messageActivityBottomLayout;//�������������������
	
	private boolean isKeyboardShowed = true; // �Ƿ���ʾ����

	private ViewPager viewPager;//����Ļ���ҳ

	private ViewGroup indicator;//������
	
    public InputPanel(Container container, View view, List<Gift> gifts) {
        this.container = container;
        this.view = view;
        this.gifts = gifts;
        this.uiHandler = new Handler();
        initViews();
    	initInputBarListener();
    	initTextEdit();//��ʼ�������ؼ�
    	
    }
    
    /**
     * ������Ϣ���߷��������ʼ���ؼ�
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
    
    // ���edittext���л����̺����ﲼ��
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
    
    // ��ʼ�����ﲼ��
    private void addGiftLayout() {
        if (giftBottomLayout == null) {
            giftBottomLayout = view.findViewById(R.id.giftLayout);
            giftBottomLayoutHasSetup = false;
        }
        initGiftLayout();
    }
    
    // ��ʼ����������layout�е���Ŀ
    private void initGiftLayout() {
        if (giftBottomLayoutHasSetup) {
            return;
        }

//        ActionsPanel.init(view, gifts);
        giftBottomLayoutHasSetup = true;
    }
    
    //��ʾ���ﰴť���л����ﲼ�ֺͼ���
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
    
    // ��ʾ���̲���
    private void showInputMethod(EditText editTextMessage) {
        editTextMessage.requestFocus();
        //����Ѿ���ʾ,���������ʱ����Ҫ�ѹ�궨λ�����
        if (!isKeyboardShowed) {
            editTextMessage.setSelection(editTextMessage.getText().length());
            isKeyboardShowed = true;
        }

        InputMethodManager imm = (InputMethodManager) container.activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editTextMessage, 0);

    }
    
    // ���ؼ��̲���
    private void hideInputMethod() {
        isKeyboardShowed = false;
        uiHandler.removeCallbacks(showTextRunnable);
        InputMethodManager imm = (InputMethodManager) container.activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(messageEditText.getWindowToken(), 0);
        messageEditText.clearFocus();
    }
    
    // ��ʾ���ﲼ��
    private void showGiftLayout() {
        addGiftLayout();
        hideInputMethod();

        uiHandler.postDelayed(showGiftFuncRunnable, SHOW_LAYOUT_DELAY);
    }
    
    // �������ﲼ��
    private void hideGiftLayout() {
        uiHandler.removeCallbacks(showGiftFuncRunnable);
        if (giftBottomLayout != null) {
            giftBottomLayout.setVisibility(View.GONE);
        }
    }
    
    //��ʾ������
    private void showInputBar()
    {
    	if (messageInputBar != null)
    	{
        	messageInputBar.setVisibility(View.VISIBLE);
    	}
    }
    
    //����������
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
     * ************************* ���̲����л� *******************************
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
     * �����ı���Ϣ
     */
    private void onTextMessageSendButtonPressed(){
        IMMessage textMessage;
        String text = messageEditText.getText().toString();
        if (container.sessionType == SessionTypeEnum.ChatRoom) {
            textMessage = ChatRoomMessageBuilder.createChatRoomTextMessage(container.account, text);
        } else {
            textMessage = MessageBuilder.createTextMessage(container.account, container.sessionType, text);
        }
        //������Ϣ
        if (container.proxy.sendMessage(textMessage)) {
            restoreText(true);
        }
    }
}
