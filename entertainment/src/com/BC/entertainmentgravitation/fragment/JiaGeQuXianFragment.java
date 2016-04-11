package com.BC.entertainmentgravitation.fragment;

import com.BC.entertainment.view.CoordinateSystemView;
import com.BC.entertainmentgravitation.R;
import com.BC.entertainmentgravitation.TopUpActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.BC.entertainmentgravitation.UpdataMainActivity;
import com.BC.entertainmentgravitation.dialog.ApplauseGiveConcern;
import com.BC.entertainmentgravitation.dialog.PromptDialog;
import com.BC.entertainmentgravitation.entity.KLink;
import com.BC.entertainmentgravitation.entity.Point;
import com.BC.entertainmentgravitation.entity.StarInformation;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.summer.config.Config;
import com.summer.factory.ThreadPoolFactory;
import com.summer.fragment.BaseFragment;
import com.summer.handler.InfoHandler;
import com.summer.json.Entity;
import com.summer.task.HttpBaseTask;
import com.summer.treadpool.ThreadPoolConst;
import com.summer.utils.JsonUtil;
import com.summer.utils.ToastUtil;
import com.summer.utils.UrlUtil;
import com.summer.view.LineChart;

public class JiaGeQuXianFragment extends BaseFragment implements
		OnClickListener {
	View contentView, Applause, BigBird, FocusOn;
	ImageView star_Head_portrait;
	UpdataMainActivity updataMainActivity;
	int type = 1;
	ApplauseGiveConcern applauseGiveConcern;
	RadioGroup radioGroup2;
	KLink kLink;
	CoordinateSystemView coordinateSystemView;
	LineChart lineChart;
	int coordinateSystemWidth = 0;
	public StarInformation starInformation;
	public String starID;

	public UpdataMainActivity getUpdataMainActivity() {
		return updataMainActivity;
	}

	public void setUpdataMainActivity(UpdataMainActivity updataMainActivity) {
		this.updataMainActivity = updataMainActivity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		contentView = inflater.inflate(R.layout.fragment_jia_ge_qu_xian, null);
		lineChart = new LineChart();
		return contentView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		init();
		super.onViewCreated(view, savedInstanceState);
	}

	public void showStarInformation(String starID) {
		this.starID = starID;
		if (starInformation == null) {
			sendReqStarInformation();
		} else {
			initStarInformation();
		}
	}

	private void init() {
		radioGroup2 = (RadioGroup) contentView.findViewById(R.id.radioGroup2);
		coordinateSystemView = (CoordinateSystemView) contentView
				.findViewById(R.id.coordinateSystemView);
		star_Head_portrait = (ImageView) contentView
				.findViewById(R.id.star_Head_portrait);
		Applause = contentView.findViewById(R.id.applause);
		BigBird = contentView.findViewById(R.id.bigbird);
		FocusOn = contentView.findViewById(R.id.FocusOn);
		// applauseAn = (ImageView) contentView.findViewById(R.id.applauseAn);
		// bigbirdAn = (ImageView) contentView.findViewById(R.id.bigbirdAn);
		// FocusOnAn = (ImageView) contentView.findViewById(R.id.FocusOnAn);

		Applause.setOnClickListener(this);
		BigBird.setOnClickListener(this);
		FocusOn.setOnClickListener(this);
		radioGroup2.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				switch (checkedId) {
				case R.id.radio0:
					type = 1;
					sendReqKLineGraph();
					break;
				case R.id.radio1:
					type = 2;
					sendReqKLineGraph();
					break;

				}
			}
		});
	}

	public void initPrice_movements() {
		// TODO Auto-generated method stub
		if (kLink == null || kLink.getPoint() == null
				|| kLink.getPoint().size() == 0) {
			ToastUtil.show(getActivity(), "暂无数据");
			return;
		}
		List<Point> price_movements = kLink.getPoint();
		int l = kLink.getMax().length();
		String o = "21";
		for (int i = 1; i < l - 1; i++) {
			o += "0";
		}
		long over = Long.valueOf(o);
		long max = Long.valueOf(kLink.getMax());
		max = over + max;
		max = max / over;
		max = max * over;
		coordinateSystemView.setyShowMax(max);
		long yLin1 = (max / 7);
		long yLin2 = yLin1 * 2;
		long yLin3 = yLin1 * 3;
		long yLin4 = yLin1 * 4;
		long yLin5 = yLin1 * 5;
		long yLin6 = yLin1 * 6;
		long yLin7 = yLin1 * 7;
		coordinateSystemView.setyPartingName(new String[] { yLin1 + "",
				yLin2 + "", yLin3 + "", yLin4 + "", yLin5 + "", yLin6 + "",
				yLin7 + "" });
		lineChart.getCoordinates().clear();
		switch (type) {
		case 1:
			if (coordinateSystemWidth > 0) {
				LayoutParams params = coordinateSystemView.getLayoutParams();
				params.width = coordinateSystemWidth * 2;
				coordinateSystemView.setLayoutParams(params);
			}
			coordinateSystemView.setxParting(48);
			String[] s = new String[48];
			for (int i = 0; i < s.length; i++) {
				if (i % 2 == 0) {
					s[i] = i / 2 + ":00";
				} else {
					s[i] = i / 2 + ":30";
				}
			}
			coordinateSystemView.setxPartingName(s);
			lineChart.setNum(48);
			break;
		case 2:
			LayoutParams params = coordinateSystemView.getLayoutParams();
			params.width = params.width / 2;
			coordinateSystemWidth = params.width;
			coordinateSystemView.setLayoutParams(params);

			coordinateSystemView.setxParting(7);
			String[] s2 = new String[7];
			Calendar c1 = Calendar.getInstance();
			for (int i = s2.length - 1; i >= 0; i--) {
				SimpleDateFormat format = new SimpleDateFormat("MM-dd");
				s2[i] = format.format(c1.getTime());
				c1.set(Calendar.DAY_OF_MONTH, c1.get(Calendar.DAY_OF_MONTH) - 1);
			}
			coordinateSystemView.setxPartingName(s2);
			lineChart.setNum(7);
			break;
		}
		for (int i = 0; i < price_movements.size(); i++) {
			lineChart.addPoint(i,
					Float.valueOf(price_movements.get(i).getPrice()), null);
		}
		coordinateSystemView.setLineChart(lineChart);

	}

	public void initStarInformation() {
		if (starInformation != null) {
			setText(R.id.Stage_name, starInformation.getStage_name());
			setText(R.id.star_information,
					starInformation.getThe_constellation() + " | "
							+ starInformation.getHeight() + "cm | "
							+ starInformation.getWeight() + "kg");
			setText(R.id.professional, starInformation.getProfessional());
			setText(R.id.prices,
					""
							+ starInformation
									.getThe_current_hooted_thumb_up_prices());
			Glide.with(this).load(starInformation.getHead_portrait())
					.centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL)
					.placeholder(R.drawable.home_image)
					.into(star_Head_portrait);
			applauseGiveConcern = new ApplauseGiveConcern(getActivity(),
					starInformation.getStar_ID(), this,
					starInformation.getThe_current_hooted_thumb_up_prices(),
					starInformation.getStage_name());

			type = 1;
			sendReqKLineGraph();
		} else {
			ToastUtil.show(getActivity(), "获取失败");
		}
	}

	private void showWarningDialog(String title, String message) {
		final PromptDialog.Builder builder = new PromptDialog.Builder(
				getActivity());
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				Intent intent = new Intent(getActivity(), TopUpActivity.class);
				startActivity(intent);
			}
		});
		PromptDialog dialog = builder.create();

		dialog.show();

	}

	public void requestFailed(int errcode, String message) {
		if (message.equals("娱币不足！")) {
			showWarningDialog("是否购买娱币", "您的娱币不足是否去购买");
		}
	}

	public void requestSuccessful(String jsonString, int taskType) {
		Gson gson = new Gson();
		switch (taskType) {
		case Config.give_applause_booed:
			ToastUtil.show(getActivity(), "提交成功");
			if (updataMainActivity != null) {
				updataMainActivity.updataMainActivity();
			}
			sendReqStarInformation();
			switch (applauseGiveConcern.getType()) {
			case 1:
				applauseGiveConcern.showAnimationDialog(R.drawable.circle4,
						R.raw.applaud);
				break;
			case 2:
				applauseGiveConcern.showAnimationDialog(R.drawable.circle5,
						R.raw.give_back);
				break;
			default:
				applauseGiveConcern.showAnimationDialog(R.drawable.circle4,
						R.raw.applaud);
				break;
			}

			break;
		case Config.and_attention:
			ToastUtil.show(getActivity(), "提交成功");
			applauseGiveConcern.showAnimationDialog(R.drawable.circle6,
					R.raw.concern);
			if (updataMainActivity != null) {
				updataMainActivity.updataMainActivity();
			}
			sendReqStarInformation();
			break;
		case Config.star_information:
			Entity<StarInformation> baseEntity2 = gson.fromJson(jsonString,
					new TypeToken<Entity<StarInformation>>() {
					}.getType());
			starInformation = baseEntity2.getData();
			initStarInformation();
			break;
		case Config.k_line_graph:
			Entity<KLink> baseEntity3 = gson.fromJson(jsonString,
					new TypeToken<Entity<KLink>>() {
					}.getType());
			kLink = baseEntity3.getData();
			initPrice_movements();
			break;
		}
	}

	/**
	 * 获取明星信息
	 */
	private void sendReqStarInformation() {
		if (starID == null) {
			ToastUtil.show(getActivity(), "无法获取数据");
			return;
		}
		HashMap<String, String> entity = new HashMap<String, String>();
		entity.put("clientID", Config.User.getClientID());
		entity.put("Star_ID", starID);
		
    	List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
    	ShowProgressDialog(this.getString(R.string.mainactivity_get_start_info));
    	addToThreadPool(Config.star_information, "send k line request", params);
	}

	/**
	 * 获取价格曲线
	 */
	private void sendReqKLineGraph() {
		if (starInformation == null) {
			ToastUtil.show(getActivity(), "无法获取数据");
			return;
		}
		HashMap<String, String> entity = new HashMap<String, String>();
		entity.put("star_id", starInformation.getStar_ID());
		entity.put("type", type + "");

    	List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
    	ShowProgressDialog(this.getString(R.string.mainactivity_kline_graphc));
    	addToThreadPool(Config.k_line_graph, "send k line request", params);
	}

    private void addToThreadPool(int taskType, String Tag, List<NameValuePair> params)
    {
    	HttpBaseTask httpTask = new HttpBaseTask(ThreadPoolConst.THREAD_TYPE_FILE_HTTP, Tag, params, UrlUtil.GetUrl(taskType));
    	httpTask.setTaskType(taskType);
    	InfoHandler handler = new InfoHandler(this);
    	httpTask.setInfoHandler(handler);
    	ThreadPoolFactory.getThreadPoolManager().addTask(httpTask);
    }
    
	@Override
	public void onClick(View v) {
		if (applauseGiveConcern == null) {
			return;
		}
		switch (v.getId()) {
		/**
		 * 鼓掌
		 */
		case R.id.applause:
			applauseGiveConcern.showApplaudDialog(1);
			break;
		/**
		 * 倒彩
		 */
		case R.id.bigbird:
			applauseGiveConcern.showApplaudDialog(2);
			break;
		/**
		 * 关注
		 */
		case R.id.FocusOn:
			applauseGiveConcern.sendFocusRequest();
			break;
		}
	}
}

