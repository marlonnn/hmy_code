package com.BC.entertainmentgravitation.fragment;

import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.BC.entertainment.cache.InfoCache;
import com.BC.entertainment.view.CoordinateSystemView;
import com.BC.entertainmentgravitation.JiaGeQuXianActivity;
import com.BC.entertainmentgravitation.R;
import com.BC.entertainmentgravitation.TopUpActivity;
import com.BC.entertainmentgravitation.UpdataMainActivity;
import com.BC.entertainmentgravitation.dialog.ApplauseGiveConcern;
import com.BC.entertainmentgravitation.dialog.PromptDialog;
import com.BC.entertainmentgravitation.entity.KLink;
import com.BC.entertainmentgravitation.entity.Point;
import com.BC.entertainmentgravitation.entity.StarInformation;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.summer.config.Config;
import com.summer.factory.ThreadPoolFactory;
import com.summer.fragment.BaseFragment;
import com.summer.handler.InfoHandler;
import com.summer.json.Entity;
import com.summer.logger.XLog;
import com.summer.task.HttpBaseTask;
import com.summer.treadpool.ThreadPoolConst;
import com.summer.utils.JsonUtil;
import com.summer.utils.ToastUtil;
import com.summer.utils.UrlUtil;
import com.summer.view.LineChart;

public class JiaGeQuXianFragment2 extends BaseFragment implements
		OnClickListener {
	View contentView, Applause, BigBird, FocusOn;
	// 涨跌点数
	private TextView tv_Difference;
	UpdataMainActivity updataMainActivity;
	int type = 1;
	ApplauseGiveConcern applauseGiveConcern;
	KLink kLink;

	CoordinateSystemView coordinateSystemView;
	LineChart lineChart;

	public UpdataMainActivity getUpdataMainActivity() {
		return updataMainActivity;
	}

	public void setUpdataMainActivity(UpdataMainActivity updataMainActivity) {
		this.updataMainActivity = updataMainActivity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		contentView = inflater.inflate(R.layout.fragment_jia_ge_qu_xian2, null);
		lineChart = new LineChart();

		return contentView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		init();
		super.onViewCreated(view, savedInstanceState);
	}

	public void showStarInformation() {
		if (InfoCache.getInstance().getStartInfo() == null) {
			getStarInfoRequest();
		} else {
			initStarInformation();
		}
	}

	private void init() {
		coordinateSystemView = (CoordinateSystemView) contentView
				.findViewById(R.id.coordinateSystemView);
		Applause = contentView.findViewById(R.id.applause);
		BigBird = contentView.findViewById(R.id.bigbird);
		FocusOn = contentView.findViewById(R.id.FocusOn);
		tv_Difference = (TextView) getView().findViewById(R.id.Different_name);

		Applause.setOnClickListener(this);
		BigBird.setOnClickListener(this);
		FocusOn.setOnClickListener(this);
	}

	public void initPrice_movements() {
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
		}
		for (int i = 0; i < price_movements.size(); i++) {
			lineChart.addPoint(i,
					Float.valueOf(price_movements.get(i).getPrice()), null);
		}
		coordinateSystemView.setLineChart(lineChart);

	}

	public void initStarInformation() {
		if (InfoCache.getInstance().getStartInfo() != null) {
			setText(R.id.Stage_name,
					InfoCache.getInstance().getStartInfo().getStage_name());
			setText(R.id.professional,
					InfoCache.getInstance().getStartInfo().getProfessional());
			setText(R.id.prices,
					"当前指数："
							+ InfoCache.getInstance().getStartInfo()
									.getThe_current_hooted_thumb_up_prices()
							+ "\n点击查看大图");
			contentView.findViewById(R.id.prices).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							Intent intent = new Intent(v.getContext(),
									JiaGeQuXianActivity.class);
							JiaGeQuXianActivity.starID = InfoCache.getInstance().getStartInfo()
									.getStar_ID();
							startActivity(intent);
						}
					});
			applauseGiveConcern = new ApplauseGiveConcern(getActivity(),
					InfoCache.getInstance().getStartInfo().getStar_ID(), this,
					InfoCache.getInstance().getStartInfo()
							.getThe_current_hooted_thumb_up_prices(),
					InfoCache.getInstance().getStartInfo().getStage_name());
			type = 1;
			sendKLineGraphRequest();

		} else {
			ToastUtil.show(getActivity(), "获取失败");
		}
	}

	/**
	 * 获取价格曲线
	 */
	public void sendKLineGraphRequest() {
		if (InfoCache.getInstance().getStartInfo() == null) {
			ToastUtil.show(getActivity(), "无法获取数据");
			return;
		}
		HashMap<String, String> entity = new HashMap<String, String>();
		entity.put("star_id", InfoCache.getInstance().getStartInfo().getStar_ID());
		entity.put("type", type + "");

		XLog.i(entity.toString());
		XLog.i(InfoCache.getInstance().getStartInfo().getStar_ID());
    	List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
    	ShowProgressDialog("获取折线图...");
    	addToThreadPool(Config.k_line_graph, "send kLine request", params);
	}
	
    private void addToThreadPool(int taskType, String Tag, List<NameValuePair> params)
    {
    	HttpBaseTask httpTask = new HttpBaseTask(ThreadPoolConst.THREAD_TYPE_WORK, Tag, params, UrlUtil.GetUrl(taskType));
    	httpTask.setTaskType(taskType);
    	InfoHandler handler = new InfoHandler(this);
    	httpTask.setInfoHandler(handler);
    	ThreadPoolFactory.getThreadPoolManager().addTask(httpTask);
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

	/**
	 * 获取明星信息
	 */
	public void getStarInfoRequest() {
		if (InfoCache.getInstance().getStartInfo() == null) {
			ToastUtil.show(getActivity(), "无法获取数据");
			return;
		}
		HashMap<String, String> entity = new HashMap<String, String>();

		entity.put("clientID", Config.User.getClientID());
		entity.put("Star_ID", InfoCache.getInstance().getStartInfo().getStar_ID());
    	List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
    	ShowProgressDialog(this.getString(R.string.mainactivity_get_start_info));
    	addToThreadPool(Config.star_information, "get user info", params);
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

	@Override
	public void RequestSuccessful(String jsonString, int taskType) {
		XLog.i("taskType: " + taskType + " jsonString: " + jsonString);
		Gson gson = new Gson();
		switch (taskType) {
		case Config.give_applause_booed:
			ToastUtil.show(getActivity(), "提交成功");
			if (updataMainActivity != null) {
				updataMainActivity.updataMainActivity();
			}
			getStarInfoRequest();
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
			getStarInfoRequest();
			break;
		case Config.star_information:

			Entity<StarInformation> baseEntity2 = gson.fromJson(jsonString,
					new TypeToken<Entity<StarInformation>>() {
					}.getType());
			if (baseEntity2.getData() != null)
			{
				InfoCache.getInstance().setStartInfo(baseEntity2.getData());
				initStarInformation();
			}
			break;
		case Config.k_line_graph:
			XLog.i("get k line success");
			Entity<KLink> baseEntity3 = gson.fromJson(jsonString,
					new TypeToken<Entity<KLink>>() {
					}.getType());
			kLink = baseEntity3.getData();
			int diff = Integer.parseInt(kLink.getDifference());
			tv_Difference.setText("昨日涨跌"+diff+"点");
			SpannableStringBuilder builder = new 
					SpannableStringBuilder(tv_Difference.getText().toString());
			int difflength = tv_Difference.getText().length()-1;
			ForegroundColorSpan colorSpan = null;
			if (diff == 0) {
				colorSpan = new ForegroundColorSpan(getResources().getColor(R.color.gray));
			}else if (diff > 0) {
				colorSpan = new ForegroundColorSpan(getResources().getColor(R.color.red2));
			}else if (diff < 0) {
				colorSpan = new ForegroundColorSpan(getResources().getColor(R.color.green));
			}
			builder.setSpan(colorSpan, 4, difflength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			tv_Difference.setText(builder);
			initPrice_movements();
			XLog.i(kLink.toString());
			break;
		}
	}
}
