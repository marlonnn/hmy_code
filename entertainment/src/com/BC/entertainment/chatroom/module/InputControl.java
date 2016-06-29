package com.BC.entertainment.chatroom.module;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.BC.entertainment.adapter.GiftPagerAdapter_new;
import com.BC.entertainment.cache.ChatCache;
import com.BC.entertainment.chatroom.gift.BaseGift_new;
import com.BC.entertainmentgravitation.ChargeActivity;
import com.BC.entertainmentgravitation.R;
import com.BC.entertainmentgravitation.util.StringUtil;
import com.netease.nimlib.sdk.chatroom.ChatRoomMessageBuilder;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

public class InputControl {

	private final int SHOW_LAYOUT_DELAY = 200;
    private Container_new container;
	private View view;
    private Handler uiHandler;
    private Bubbling bubble;//气泡
    private List<BaseGift_new> gifts;//赠送的礼物
    
    protected EditText messageEditText;// 文本消息编辑框
    protected View sendMessageButtonInInputBar;// 发送消息按钮
    protected View messageInputBar;//输入框控件整体
    protected View giftBottomLayout; // 礼物布局
	private boolean isKeyboardShowed = true; // 是否显示键盘
	private ViewPager viewPager;//礼物的滑动页
	private ViewGroup indicator;//滚动点
	private TextView amountMoney;
	private ImageView imgViewRight;
	
    private boolean giftBottomLayoutHasSetup = false;// 礼物布局状态
	
	public InputControl (Container_new container, View rootView)
	{
		this.container = container;
		this.view = rootView;
        this.uiHandler = new Handler();
        
        initViews();
    	initInputBarListener();
    	initTextEdit();//初始化输入框控件
	}
	
    private void initViews()
    {
    	//bubbling
    	bubble = (Bubbling) view.findViewById(R.id.bubbling);
        // input bar
        messageInputBar = view.findViewById(R.id.textMessageLayout);
        sendMessageButtonInInputBar = view.findViewById(R.id.buttonSendMessage);
        messageEditText = (EditText) view.findViewById(R.id.editTextMessage);
        
        viewPager = (ViewPager) view.findViewById(R.id.gift_viewPager);
        indicator = (ViewGroup) view.findViewById(R.id.gift_page_indicator);
        amountMoney = (TextView) view.findViewById(R.id.textView_total_money);
        imgViewRight = (ImageView) view.findViewById(R.id.imgViewRight);
        imgViewRight.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(container.getActivity(), ChargeActivity.class);
				container.getActivity().startActivity(intent);
			}
		});
    }
    
    /**
     * 初始化礼物布局PageListener
     * @param indicator
     * @param count
     * @param viewPager
     */
    @SuppressWarnings("deprecation")
	private void initPageListener(final ViewGroup indicator, final int count, final ViewPager viewPager) {
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                setIndicator(indicator, count, position);
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        setIndicator(indicator, count, 0);
    }
    
    /**
     * 设置页码
     */
    private void setIndicator(ViewGroup indicator, int total, int current) {
        if (total <= 1) {
            indicator.removeAllViews();
        } else {
            indicator.removeAllViews();
            for (int i = 0; i < total; i++) {
                ImageView imgCur = new ImageView(indicator.getContext());
                imgCur.setId(i);
                // 判断当前页码来更新
                if (i == current) {
                    imgCur.setBackgroundResource(R.drawable.nim_moon_page_selected);
                } else {
                    imgCur.setBackgroundResource(R.drawable.nim_moon_page_unselected);
                }

                indicator.addView(imgCur);
            }
        }
    }
    
    public void setAmountMoney(String money)
    {
    	if (money != null)
    	{
    		amountMoney.setText(money);
    	}
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
                messageEditText.setHint("一条消息10个娛币哦");
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
    
    // 点击edittext，切换键盘和礼物布局
    private void switchToTextLayout(boolean needShowInput) {
        hideGiftLayout();

        messageEditText.setVisibility(View.VISIBLE);

        messageInputBar.setVisibility(View.VISIBLE);
        
        bubble.setVisibility(View.GONE);

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
        initgiftViewPage();
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

        InputMethodManager imm = (InputMethodManager) container.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editTextMessage, 0);

    }
    
    // 隐藏键盘布局
    public void hideInputMethod() {
        isKeyboardShowed = false;
        uiHandler.removeCallbacks(showTextRunnable);
        InputMethodManager imm = (InputMethodManager) container.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(messageEditText.getWindowToken(), 0);
        messageEditText.clearFocus();
    }
    
    /**
     * 初始化礼物滑动页面
     */
    private void initgiftViewPage()
    {
    	for(int i =0; i<gifts.size(); i++ )
    	{
    		gifts.get(i).setContainer(container);
    	}
    	GiftPagerAdapter_new adapter = new GiftPagerAdapter_new(viewPager, gifts);
        viewPager.setAdapter(adapter);
        initPageListener(indicator, adapter.getCount(), viewPager);
    }
    
    // 显示礼物布局
    public void showGiftLayout() {
        addGiftLayout();
        hideInputMethod();

        uiHandler.postDelayed(showGiftFuncRunnable, SHOW_LAYOUT_DELAY);
    }
    
    // 隐藏礼物布局
    public void hideGiftLayout() {
        uiHandler.removeCallbacks(showGiftFuncRunnable);
        if (giftBottomLayout != null) {
            giftBottomLayout.setVisibility(View.GONE);
        }
    }
    
    //显示输入条
    public void showInputBar()
    {
    	if (messageInputBar != null)
    	{
        	messageInputBar.setVisibility(View.VISIBLE);
    	}
    	if (bubble != null)
    	{
    		bubble.setVisibility(View.GONE);
    	}
    }
    
    //隐藏输入条
    public void hideInputBar()
    {
    	if (messageInputBar != null)
    	{
        	messageInputBar.setVisibility(View.GONE);
    	}
    	if (bubble != null)
    	{
    		bubble.setVisibility(View.VISIBLE);
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
        if (container.getSessionType() == SessionTypeEnum.ChatRoom) {
            textMessage = ChatRoomMessageBuilder.createChatRoomTextMessage( ChatCache.getInstance().getChatRoom().getChatroomid(), text);
        } else {
            textMessage = MessageBuilder.createTextMessage(ChatCache.getInstance().getChatRoom().getChatroomid(), container.getSessionType(), text);
        }
        //发送消息
        if (container.getActivityCallback().sendMessage(textMessage))
        {
        	restoreText(true);
        }
    }
    
    private void restoreText(boolean clearText) {
        if (clearText) {
            messageEditText.setText("");
        }
        checkSendButtonEnable(messageEditText);
    }
}
