package com.BC.entertainmentgravitation;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;

import com.BC.entertainment.adapter.P2PAdapter;
import com.BC.entertainment.chatroom.gift.Gift;
import com.BC.entertainment.chatroom.module.ModuleProxy;
import com.BC.entertainmentgravitation.entity.CardOrder;
import com.BC.entertainmentgravitation.util.ListViewUtil;
import com.BC.entertainmentgravitation.util.StringUtil;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.summer.activity.BaseActivity;
import com.umeng.analytics.MobclickAgent;

public class ChatActivity extends BaseActivity implements OnClickListener, ModuleProxy{

	private static final int MESSAGE_CAPACITY = 500;
	
    // 聊天对象
    protected String sessionId; // p2p对方Account或者群id

    protected SessionTypeEnum sessionType;

	private final int SHOW_LAYOUT_DELAY = 200;
	private ListView messageListView;
	private P2PAdapter adapter;
    protected EditText messageEditText;// 文本消息编辑框
    protected View sendMessageButtonInInputBar;// 发送消息按钮
    protected View messageInputBar;//输入框控件整体
	private boolean isKeyboardShowed = true; // 是否显示键盘
	private Handler uiHandler;
	private Context context;
	private LinkedList<IMMessage> items;//聊天室消息列表
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		registerObservers(true);
		setContentView(R.layout.activity_chat);
		findViewById(R.id.imageViewBack).setOnClickListener(this);
		parseIntent();
	}
	
    private void parseIntent() {
    	context = this;
    	uiHandler = new Handler();
    	sessionId = (String) getIntent().getStringExtra("username");
        sessionType = SessionTypeEnum.P2P;
        initView();
    }
    
    private void initView()
    {
    	items = new LinkedList<>();
    	messageListView = (ListView) findViewById(R.id.messageListView);
    	adapter = new P2PAdapter(context, items);
		messageListView.setAdapter(adapter);
		messageListView.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				/**
				 * 按下
				 * */
				case MotionEvent.ACTION_DOWN:
					break;
				/**
				 * 移动
				 * */
				case MotionEvent.ACTION_MOVE:
				
					break;
				// 拿起
				case MotionEvent.ACTION_UP:
					if (isKeyboardShowed)
					{
						hideInputMethod();
					}
					break;
				default:
					break;
				}
				return true;
			}
		});
		
        messageInputBar = findViewById(R.id.textMessageLayout);
        sendMessageButtonInInputBar = findViewById(R.id.buttonSendMessage);
        sendMessageButtonInInputBar.setOnClickListener(this);
        messageEditText = (EditText) findViewById(R.id.editTextMessage);
        initTextEdit();
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
    
    private void checkSendButtonEnable(EditText editText) {
        String textMessage = editText.getText().toString();
        if (!TextUtils.isEmpty(StringUtil.removeBlanks(textMessage)) && editText.hasFocus()) {
        	sendMessageButtonInInputBar.setEnabled(true);
        } else {
        	sendMessageButtonInInputBar.setEnabled(false);
        }
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        NIMClient.getService(MsgService.class).setChattingAccount(MsgService.MSG_CHATTING_ACCOUNT_NONE,
                SessionTypeEnum.None);
    }
    
	@Override
	protected void onResume() {
		super.onResume();
        MobclickAgent.onResume(this);
        NIMClient.getService(MsgService.class).setChattingAccount(sessionId, sessionType);
        setVolumeControlStream(AudioManager.STREAM_VOICE_CALL); // 默认使用听筒播放
	}
	
    @Override
    public void onDestroy() {
        super.onDestroy();
        registerObservers(false);
    }
    
    @Override
    public void onBackPressed() {
    	this.finish();
    }
    
    
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.buttonSendMessage:
			onTextMessageSendButtonPressed();
			break;
		/**
		 * 返回键
		 */
		case R.id.imageViewBack:
			finish();
			break;
		}
	}

    /**
     * 发送文本消息
     */
    private void onTextMessageSendButtonPressed(){
        IMMessage textMessage;
        String text = messageEditText.getText().toString();
            textMessage = MessageBuilder.createTextMessage(sessionId, sessionType, text);
        //发送消息
        if (sendMessage(textMessage)) {
            restoreText(true);
        }
    }
    
    // 点击edittext，切换键盘和礼物布局
    private void switchToTextLayout(boolean needShowInput) {

        messageEditText.setVisibility(View.VISIBLE);

        messageInputBar.setVisibility(View.VISIBLE);
        

        if (needShowInput) {
            uiHandler.postDelayed(showTextRunnable, SHOW_LAYOUT_DELAY);
        } else {
            hideInputMethod();
        }
    }
    
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

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editTextMessage, 0);

    }
    
    // 隐藏键盘布局
    public void hideInputMethod() {
        isKeyboardShowed = false;
        uiHandler.removeCallbacks(showTextRunnable);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(messageEditText.getWindowToken(), 0);
        messageEditText.clearFocus();
    }

	@Override
	public void RequestSuccessful(String jsonString, int taskType) {
		
	}


    /**
     * ****************** 观察者 **********************
     */

    private void registerObservers(boolean register) {
        MsgServiceObserve service = NIMClient.getService(MsgServiceObserve.class);
        service.observeReceiveMessage(incomingMessageObserver, register);
    }

    /**
     * 消息接收观察者
     */
    @SuppressWarnings("serial")
	Observer<List<IMMessage>> incomingMessageObserver = new Observer<List<IMMessage>>() {
        @Override
        public void onEvent(List<IMMessage> messages) {
        	boolean needScrollToBottom = ListViewUtil.isLastMessageVisible(messageListView);
            if (messages == null || messages.isEmpty()) {
                return;
            }
            for (IMMessage message : messages) {
                if (message == null) {
                    return;
                }
                if (items.size() >= MESSAGE_CAPACITY) {
                    items.poll();
                }
                items.add(message);
                
            }
            adapter.notifyDataSetChanged();

            // incoming messages tip
            IMMessage lastMsg = messages.get(messages.size() - 1);
            if (isMyMessage(lastMsg) && needScrollToBottom) {
                ListViewUtil.scrollToBottom(messageListView);
            }
        }
    };
    
    public boolean isMyMessage(IMMessage message) {
        return message.getSessionType() == sessionType
                && message.getSessionId() != null
                && message.getSessionId().equals(sessionId);
    }


    /**
     * ********************** implements ModuleProxy *********************
     */
    @Override
    public boolean sendMessage(IMMessage message) {

        // send message to server and save to db
        NIMClient.getService(MsgService.class).sendMessage(message, false);

        if (message == null) {
        }
        if (items.size() >= MESSAGE_CAPACITY) {
            items.poll();
        }
        items.add(message);
        adapter.notifyDataSetChanged();
        ListViewUtil.scrollToBottom(messageListView);
        return true;
    }

    @Override
    public void onInputPanelExpand() {
    }

    @Override
    public void shouldCollapseInputPanel() {
    }

    @Override
    public boolean isLongClickEnabled() {
    	return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

	@Override
	public void sendMessage(IMMessage msg, boolean isFirst) {
		NIMClient.getService(MsgService.class).sendMessage(msg, false);
	}

	@Override
	public void showAnimation(Gift gift) {
		
	}
}
