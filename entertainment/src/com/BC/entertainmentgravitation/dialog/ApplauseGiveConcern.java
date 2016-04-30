package com.BC.entertainmentgravitation.dialog;

import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;

import com.BC.entertainment.view.SelectWheel4;
import com.BC.entertainmentgravitation.R;
import com.BC.entertainmentgravitation.dialog.AnimationDialog.Builder.AnimationOver;
import com.summer.config.Config;
import com.summer.factory.ThreadPoolFactory;
import com.summer.handler.InfoHandler;
import com.summer.handler.InfoHandler.InfoReceiver;
import com.summer.task.HttpBaseTask;
import com.summer.treadpool.ThreadPoolConst;
import com.summer.utils.JsonUtil;
import com.summer.utils.ToastUtil;
import com.summer.utils.UrlUtil;

import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class ApplauseGiveConcern {
	
	private Context context;
	private String Star_ID;
	private InfoReceiver infoReceiver;
	private int type = 1;
	private int price = 0;
	private String name = "";
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public ApplauseGiveConcern(Context context, String star_ID,
			InfoReceiver infoReceiver, int price, String name) {
		super();
		this.context = context;
		Star_ID = star_ID;
		this.infoReceiver = infoReceiver;
		this.price = price;
		this.name = name;
	}

	public void showApplaudDialog(final int type) {
		final ApplaudDialog.Builder builder = new ApplaudDialog.Builder(context);
		if (type == 1) {
			builder.setTitle("请输入鼓掌次数");
		} else {
			builder.setTitle("请输入踢红包个数");
		}
		builder.setMessage("请输入次数");

		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

				EditText message = (EditText) builder
						.findViewById(R.id.message);
				if (message != null && !message.getText().toString().equals("")
						&& !message.getText().toString().equals("0")) {
					sendApplaseOrBoosRequest(type, message.getText().toString());
					dialog.dismiss();
				} else {
					ToastUtil.show(context, "抱歉至少送一个掌声");
				}
			}
		});

		builder.setNegativeButton("取消",
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		ApplaudDialog animationDialog = builder.create();
		EditText editText = (EditText) builder.findViewById(R.id.message);
		final TextView textView1 = (TextView) builder
				.findViewById(R.id.textView1);
		final TextView textView2 = (TextView) builder
				.findViewById(R.id.textView2);
		final TextView textView3 = (TextView) builder
				.findViewById(R.id.TextView03);
		final TextView textView4 = (TextView) builder
				.findViewById(R.id.TextView04);
		if (type == 1) {
			editText.setText("6");
			textView1.setText("需要花费娱币数量：");
			if (name != null) {
				textView2.setText("" + (price * 6 + 15));
				textView4.setText("能兑换" + name + "的红包数量：");
				textView3.setText(6 + "");
				textView4.setVisibility(View.GONE);
				textView3.setVisibility(View.GONE);
			}
			editText.addTextChangedListener(new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {
				}

				@Override
				public void afterTextChanged(Editable s) {
					String ts = s.toString();
					if (!ts.equals("")) {
						int v = Integer.valueOf(ts);
						textView2.setText("" + (price * v + v * (v - 1) / 2));
						textView4.setText("能兑换" + name + "的红包数量：");
						textView3.setText(v + "");
					}
				}
			});
		} else {
			editText.setText("1");
			textView1.setText("当前选择红包数可兑换回娱币数量：");
			textView2.setText("" + (price - 1));
			textView4.setVisibility(View.GONE);
			textView3.setVisibility(View.GONE);
			SelectWheel4 selectWheel4 = (SelectWheel4) builder
					.findViewById(R.id.selectWheel);
			selectWheel4.setSelectItem4(1);
			// }
			editText.addTextChangedListener(new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {
				}

				@Override
				public void afterTextChanged(Editable s) {
					String ts = s.toString();
					if (!ts.equals("")) {
						int v = Integer.valueOf(ts);
						textView2.setText("" + count(price, v));
					}
				}

				private int count(int x, int y) {
					return x * y - y * (y + 1) / 2;
				}
			});
		}
		animationDialog.show();
	}

	public void showAnimationDialog(int id, int audio) {
		final AnimationDialog.Builder builder = new AnimationDialog.Builder(
				context);

		builder.setAnimationDrawable(context, id);
		builder.setAudioFile(audio);
		final AnimationDialog animationDialog = builder.create();
		builder.setAnimationOver(new AnimationOver() {

			@Override
			public void AnimationOver() {
				animationDialog.dismiss();
			}
		});
		animationDialog.show();
		builder.startAnimation();
	}

	/**
	 * 加关注
	 */
	public void sendFocusRequest() {
		if (Config.User == null || Star_ID == null) {
			ToastUtil.show(context, "抱歉，提交失败");
			return;
		}
		HashMap<String, String> entity = new HashMap<String, String>();

		entity.put("clientID", Config.User.getClientID());
		entity.put("Star_ID", Star_ID);
		List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
		addToThreadPool(Config.and_attention, "send focus request", params);
	}

	/**
	 * 鼓掌、喝倒彩
	 */
	public void sendApplaseOrBoosRequest(int type, String number) {
		if (Config.User == null) {
			ToastUtil.show(context, "抱歉，提交失败");
			return;
		}
		this.type = type;
		HashMap<String, String> entity = new HashMap<String, String>();

		entity.put("clientID", Config.User.getClientID());
		entity.put("Star_ID", Star_ID);
		entity.put("Type", "" + type);
		entity.put("number", number);
		List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
		addToThreadPool(Config.give_applause_booed, "send applase or boos request", params);
	}
	
    private void addToThreadPool(int taskType, String Tag, List<NameValuePair> params)
    {
    	HttpBaseTask httpTask = new HttpBaseTask(ThreadPoolConst.THREAD_TYPE_FILE_HTTP, Tag, params, UrlUtil.GetUrl(taskType));
    	httpTask.setTaskType(taskType);
    	InfoHandler handler = new InfoHandler(infoReceiver);
    	httpTask.setInfoHandler(handler);
    	ThreadPoolFactory.getThreadPoolManager().addTask(httpTask);
    }
}
