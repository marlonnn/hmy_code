package com.BC.entertainmentgravitation.fragment;

import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.BC.entertainment.cache.InfoCache;
import com.BC.entertainment.view.CoordinateSystemView;
import com.BC.entertainmentgravitation.R;
import com.BC.entertainmentgravitation.entity.EditPersonal;
import com.BC.entertainmentgravitation.entity.KLink;
import com.BC.entertainmentgravitation.entity.Point;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
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
import com.summer.utils.StringUtil;
import com.summer.utils.ToastUtil;
import com.summer.utils.UrlUtil;
import com.summer.view.CircularImage;
import com.summer.view.LineChart;

/**
 * 价值曲线
 * @author wen zhong
 *
 */
public class CurveFragment extends BaseFragment implements OnClickListener{
	
	private View rootView;
	private Gson gson;
	
	private CoordinateSystemView coordinateSystemView;
	private CircularImage cImagePortrait;
	private TextView txtViewName;
	private TextView txtViewCareer;
	private TextView txtViewHongBao;
	private TextView txtViewChange;
	private TextView txtViewIndex;
	private TextView txtViewLocation;
	
	private LineChart lineChart;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		gson = new Gson();
		sendPersonalInfoRequest();
		sendKLineGraphRequest();
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onResume() {
		super.onResume();
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onStop() {
		super.onStop();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	@SuppressLint("InflateParams") @Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_curve, null);
		return rootView;
	}
	
	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		findViewById();
	}
	
	private void findViewById()
	{
		coordinateSystemView = (CoordinateSystemView) rootView.findViewById(R.id.coordinateSystemView);
		cImagePortrait = (CircularImage) rootView.findViewById(R.id.cImageportrait);
		txtViewName = (TextView) rootView.findViewById(R.id.txtViewName);
		txtViewCareer = (TextView) rootView.findViewById(R.id.txtViewCareer);
		txtViewHongBao = (TextView) rootView.findViewById(R.id.txtViewHongBao);
		txtViewChange = (TextView) rootView.findViewById(R.id.txtViewChange);
		txtViewIndex = (TextView) rootView.findViewById(R.id.txtViewIndex);
		txtViewLocation = (TextView) rootView.findViewById(R.id.txtViewLocation);
		rootView.findViewById(R.id.focus).setOnClickListener(this);
		rootView.findViewById(R.id.invest).setOnClickListener(this);
		rootView.findViewById(R.id.divest).setOnClickListener(this);
		
		Glide.with(this).load(Config.User.getImage())
		.centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL)
		.placeholder(R.drawable.avatar_def).into(cImagePortrait);
		txtViewName.setText(Config.User.getNickName());
	}
	
	/**
	 * 初始化价值曲线
	 * @param kLink
	 */
	public void setPriceCurve(KLink kLink) {
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
		for (int i = 0; i < price_movements.size(); i++) {
			lineChart.addPoint(i,
					Float.valueOf(price_movements.get(i).getPrice()), null);
		}
		coordinateSystemView.setLineChart(lineChart);

	}

    /**
     * 获取用户信息
     */
    private void sendPersonalInfoRequest()
    {
    	if (Config.User == null)
    	{
			ToastUtil.show(getActivity(), StringUtil.getXmlResource(getActivity(), R.string.mainactivity_login_invalidate));
			return;
    	}
    	HashMap<String, String> entity = new HashMap<String, String>();
    	entity.put("clientID", Config.User.getClientID());
    	List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
    	ShowProgressDialog(this.getString(R.string.mainactivity_get_user_info));
    	addToThreadPool(Config.personal_information, "get user info", params);
    }
	
	/**
	 * 获取价格曲线
	 */
	private void sendKLineGraphRequest() {
		HashMap<String, String> entity = new HashMap<String, String>();
		entity.put("star_id", Config.User.getClientID());
		entity.put("type", "1");
    	List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
//    	ShowProgressDialog("获取折线图...");
    	addToThreadPool(Config.k_line_graph, "send kLine request", params);
	}
	
    private void addToThreadPool(int taskType, String tag, List<NameValuePair> params)
    {
    	HttpBaseTask httpTask = new HttpBaseTask(ThreadPoolConst.THREAD_TYPE_FILE_HTTP, tag, params, UrlUtil.GetUrl(taskType));
    	httpTask.setTaskType(taskType);
    	InfoHandler handler = new InfoHandler(this);
    	httpTask.setInfoHandler(handler);
    	ThreadPoolFactory.getThreadPoolManager().addTask(httpTask);
    }
	@Override
	public void onClick(View v) {
		switch(v.getId())
		{
		case R.id.focus:
			ToastUtil.show(getActivity(), "此功能正在完善中，敬请期待...");
			break;
		case R.id.invest:
			ToastUtil.show(getActivity(), "此功能正在完善中，敬请期待...");
			break;
		case R.id.divest:
			ToastUtil.show(getActivity(), "此功能正在完善中，敬请期待...");
			break;
		}
		
	}
	
	@Override
	public void RequestSuccessful(String jsonString, int taskType) {
		switch(taskType)
		{
		case Config.personal_information:
			Entity<EditPersonal> baseEntity = gson.fromJson(jsonString,
					new TypeToken<Entity<EditPersonal>>() {
					}.getType());
			if (baseEntity.getData() != null)
			{
				InfoCache.getInstance().setPersonalInfo(baseEntity.getData());
				txtViewLocation.setText(baseEntity.getData().getRegion());
				txtViewCareer.setText(baseEntity.getData().getProfessional());
			}

			break;
		case Config.k_line_graph:
			XLog.i("get k line success");
			Entity<KLink> baseEntity3 = gson.fromJson(jsonString,
					new TypeToken<Entity<KLink>>() {
					}.getType());
			KLink kLink = baseEntity3.getData();
			int diff = Integer.parseInt(kLink.getDifference());
			txtViewChange.setText("昨日涨跌"+diff+"点");
			txtViewHongBao.setText(kLink.getBonus() == null ? "" : kLink.getBonus());
			setPriceCurve(kLink);
			XLog.i(kLink.toString());
			break;
		}
	}

}
