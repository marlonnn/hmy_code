package com.BC.entertainmentgravitation;

import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;

import android.media.AudioTrack;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.BC.entertainment.view.EmotionsView;
import com.BC.entertainmentgravitation.R;
import com.BC.entertainmentgravitation.entity.SignTime;
import com.BC.entertainmentgravitation.fragment.CalendarFragemt;
import com.BC.entertainmentgravitation.util.TimestampTool;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.summer.activity.BaseActivity;
import com.summer.config.Config;
import com.summer.factory.ThreadPoolFactory;
import com.summer.handler.InfoHandler;
import com.summer.json.Entity;
import com.summer.task.HttpBaseTask;
import com.summer.treadpool.ThreadPoolConst;
import com.summer.utils.Audio;
import com.summer.utils.JsonUtil;
import com.summer.utils.ToastUtil;
import com.summer.utils.UrlUtil;

/**
 * 签到
 * @author zhongwen
 *
 */
public class SignInActivity extends BaseActivity {
	private Button signInButton;

	private CalendarFragemt calendarFragemt;
	
	private RefreshHandler mRedrawHandler = new RefreshHandler();
	private EmotionsView ev;
	private AudioTrack audioTrack;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_in);
		signInButton = (Button) findViewById(R.id.signInButton);
		calendarFragemt = (CalendarFragemt) getSupportFragmentManager()
				.findFragmentById(R.id.fragment1);
		signInButton.setOnClickListener(new OnClickListener() {
	
			@Override
			public void onClick(View v) {
				sendReqStarInformation();
			}
		});
		String[] strings = TimestampTool.getCurrentDate().split("-");
		setText(R.id.month, strings[1] + "月签到");
		sendReq();
		
	}
	
	private void showEmotionsView() {
		// 获得表情雨视图,加载icon到内存(在布局文件中置入自定义EmotionsView)
		ev = (EmotionsView) findViewById(R.id.emotion_view); //
		// 此处可实现表情图片的更替，具体判断来自发送的文本内容
		int intDrawable = R.drawable.money;

		audioTrack = Audio.palyAudio(this, R.raw.g5293);

		ev.LoadEmotionImage(intDrawable);
		ev.setVisibility(View.VISIBLE); // 获取当前屏幕的高和宽
		DisplayMetrics dm = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(dm);
		ev.setView(dm.heightPixels, dm.widthPixels);
		updateEmotions();
		sendReq();
	}

	class RefreshHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			if (ev == null || ev.isEnd()) {
				if (audioTrack != null) {
					audioTrack.stop();
					audioTrack.release();
				}
				return;
			}
			ev.addRandomEmotion();
			ev.invalidate();
			sleep(50);
		}

		public void sleep(long delayMillis) {
			this.removeMessages(0);
			sendMessageDelayed(obtainMessage(0), delayMillis);
		}
	};

	public void updateEmotions() {
		ev.setEnd(false);
		ev.clearAllEmotions();
		ev.addRandomEmotion();
		mRedrawHandler.removeMessages(0);
		mRedrawHandler.sleep(100);
	}

	/**
	 * 签到
	 */
	private void sendReqStarInformation() {
		if (Config.User == null) {
			ToastUtil.show(this, "无法获取信息");
			return;
		}
		HashMap<String, String> entity = new HashMap<String, String>();

		entity.put("clientID", Config.User.getClientID());
		entity.put("The_date_of", TimestampTool.getCurrentDate());
		ShowProgressDialog("签到中...");
		
		List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
		addToThreadPool(Config.sign_in, "send search request", params);
	}

	/**
	 * 获取签到天数
	 */
	private void sendReq() {
		if (Config.User == null) {
			ToastUtil.show(this, "无法获取信息");
			return;
		}
		HashMap<String, String> entity = new HashMap<String, String>();

		entity.put("clientID", Config.User.getClientID());
		
		ShowProgressDialog("获取信息...");
		List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
		addToThreadPool(Config.continuous, "send search request", params);
	}
	
    private void addToThreadPool(int taskType, String Tag, List<NameValuePair> params)
    {
    	HttpBaseTask httpTask = new HttpBaseTask(ThreadPoolConst.THREAD_TYPE_FILE_HTTP, Tag, params, UrlUtil.GetUrl(taskType));
    	httpTask.setTaskType(taskType);
    	InfoHandler handler = new InfoHandler(this);
    	httpTask.setInfoHandler(handler);
    	ThreadPoolFactory.getThreadPoolManager().addTask(httpTask);
    }
    
	class Continuous {
		String continuous;

		List<SignTime> signs;

		public List<SignTime> getSigns() {
			return signs;
		}

		public void setSigns(List<SignTime> signs) {
			this.signs = signs;
		}

		public String getContinuous() {
			return continuous;
		}

		public void setContinuous(String continuous) {
			this.continuous = continuous;
		}

	}

	

	class Reward {
		public String Reward_type;
	}

	@Override
	public void RequestSuccessful(String jsonString, int taskType) {
		Gson gson = new Gson();
		switch (taskType) {
		case Config.sign_in:
			Entity<Reward> baseEntity3 = gson.fromJson(jsonString,
					new TypeToken<Entity<Reward>>() {
					}.getType());
			Reward Reward_type = baseEntity3.getData();
			Toast.makeText(this, "签到成功,获得：" + Reward_type.Reward_type + "个娱币",
					Toast.LENGTH_LONG).show();
			showEmotionsView();
			break;
		case Config.continuous:
			Entity<Continuous> baseEntity4 = gson.fromJson(jsonString,
					new TypeToken<Entity<Continuous>>() {
					}.getType());
			if (baseEntity4.getData().getContinuous() != null
					&& !baseEntity4.getData().getContinuous().equals("")) {
				setText(R.id.continuous, baseEntity4.getData().getContinuous());
				calendarFragemt.setSign(baseEntity4.getData().getSigns());
				int continuous = Integer.parseInt(baseEntity4.getData()
						.getContinuous().trim());
				switch (continuous) {
				case 0:
					setText(R.id.total, "累计签到天数\n还有额外奖励");
					break;
				case 1:
					setText(R.id.total, "明日签到获得15娱币");
					break;
				case 2:
					setText(R.id.total, "明日签到获得20娱币");
					break;
				case 3:
					setText(R.id.total, "明日签到获得25娱币");
					break;
				case 4:
					setText(R.id.total, "明日签到获得30娱币");
					break;
				case 5:
					setText(R.id.total, "明日签到获得35娱币");
					break;
				case 6:
					setText(R.id.total, "明日签到获得40娱币");
					break;
				case 7:
					setText(R.id.total, "明日签到获得45娱币");
					break;
				case 8:
					setText(R.id.total, "明日签到获得50娱币");
					break;
				case 9:
					setText(R.id.total, "明日签到获得55娱币");
					break;
				case 10:
					setText(R.id.total, "明日签到获得60娱币");
					break;
				default:
					setText(R.id.total, "明日签到获得60娱币");
					break;
				}
			} else {
				ToastUtil.show(this, "获取数据失败");
			}
			break;

		default:
			break;
		}
	}
}
