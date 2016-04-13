package com.BC.entertainmentgravitation.dialog;

import java.util.ArrayList;
import java.util.List;

import com.BC.entertainmentgravitation.R;
import com.BC.entertainmentgravitation.entity.PayWays;
import com.summer.adapter.CommonAdapter;
import com.summer.config.Config;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

/***
 * 微信和支付宝支付选择对话框
 * @author zhongwen
 *
 */
public class ChoosePayDialog {
	
	private Handler handler;
	private Dialog dialog;
	private ListView listView;
	private Context context;
	private ChoosePayCallback choosePayCallback;
	
	public ChoosePayDialog(Context context, final Handler handler)
	{
		this.handler = handler;
		this.context = context;
		dialog = new Dialog(context);
		dialog.setContentView(R.layout.dialog_choose_pay);
		listView = (ListView) dialog.findViewById(R.id.dialog_pay_listView);
		
		List<PayWays> list = getPayWays();
//		ChoosePayAdapter adapter = new ChoosePayAdapter(context, list);
		CommonAdapter<PayWays> adapter = new CommonAdapter<PayWays>(context, 
				R.layout.dialog_choose_pay_item, list){

			@Override
			public void convert(
					ViewHolder helper,
					PayWays item) {
				helper.setText(R.id.text_pay, item.getName() + "");
				helper.setImageResource(R.id.image_pay, item.getImageResource());
			}};
		listView.setAdapter(adapter);
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				PayWays payWays = (PayWays)view.getTag();
				if(payWays != null)
				{
					choosePayCallback.pay(handler, payWays);
					Dismiss();
				}
			}
			
		});
	}
	
	private List<PayWays> getPayWays()
	{
		List<PayWays> list = new ArrayList<PayWays>();
		PayWays wxPay = new PayWays();
		wxPay.setName(context.getApplicationContext().getString(R.string.pay_wx));
		wxPay.setImageResource(R.drawable.pay_wechat);
		wxPay.setPayId(Config.PAY_WX);
		list.add(wxPay);
		PayWays aliPay = new PayWays();
		aliPay.setName(context.getApplicationContext().getString(R.string.pay_alipay));
		aliPay.setImageResource(R.drawable.pay_alipay);
		aliPay.setPayId(Config.PAY_ALI);
		list.add(aliPay);
		return list;
	}
	
	//支付方式选择之后的回调函数
	public interface ChoosePayCallback
	{
		public void pay(Handler handler, PayWays payWays);
	}
	
	public void SetChoosePayCallback(ChoosePayCallback choosePayCallback)
	{
		this.choosePayCallback = choosePayCallback;
	}
	
	public void Show()
	{
		Window dialogWindow = dialog.getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
//		dialogWindow.setGravity(Gravity.LEFT | Gravity.TOP);
//        lp.x = 100; // 新位置X坐标
//        lp.y = 100; // 新位置Y坐标
        lp.width = 600; // 宽度
        lp.height = 500; // 高度
//        lp.alpha = 0.7f; // 透明度
        dialogWindow.setAttributes(lp);
		this.dialog.show();
	}
	
	public void Dismiss()
	{
		this.dialog.dismiss();
	}

}
