package com.BC.entertainment.view;


import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.BC.entertainmentgravitation.R;
import com.BC.entertainmentgravitation.dialog.iWheel;

public class BaseSelectItem extends LinearLayout implements OnClickListener {

	public interface SelectResult {
		public void result(View contentView, TextView contentTextView);
	}

	private LayoutInflater inflater;
	private LinearLayout contentnLayout;
	private TextView title;
	// private View itemImage;
	private TextView content;
	private int dialogContentId = 0;
	private float textSize = 0;
	private LinearLayout dialogContent;
	private SelectResult result;
	private List<String> selectContent = new ArrayList<String>();
	private List<String> select2Content = new ArrayList<String>();
	private List<String> select3Content = new ArrayList<String>();
	private List<String> select4Content = new ArrayList<String>();
	private TextView title2;
	private TextView wheelTitle1;
	private TextView wheelTitle2;
	private TextView wheelTitle3;
	private TextView wheelTitle4;
	private String wheelTitle1String;
	private String wheelTitle2String;
	private String wheelTitle3String;
	private String wheelTitle4String;
	private EditText content2;
	private String equalsString = "ÆäËû£¨¿É±à¼­£©";
	private View showView;

	public boolean showDay = false;

	public boolean canClick = true;

	public boolean isCanClick() {
		return canClick;
	}

	public void setCanClick(boolean canClick) {
		this.canClick = canClick;

	}

	public SelectResult getResult() {
		return result;
	}

	public void setResult(SelectResult result) {
		this.result = result;
	}

	public List<String> getSelectContent() {
		return selectContent;
	}

	public List<String> getSelect2Content() {
		return select2Content;
	}

	public void setSelect2Content(List<String> select2Content) {
		this.select2Content = select2Content;
	}

	public List<String> getSelect3Content() {
		return select3Content;
	}

	public void setSelect3Content(List<String> select3Content) {
		this.select3Content = select3Content;
	}

	public List<String> getSelect4Content() {
		return select4Content;
	}

	public void setSelect4Content(List<String> select4Content) {
		this.select4Content = select4Content;
	}

	public void setSelectContent(List<String> selectContent) {
		this.selectContent = selectContent;
	}

	public void setContent(String str) {
		content.setText(str);
	}

	public void setContent2(String str) {
		content2.setText(str);
	}

	public void setEqualsString(String equalsString) {
		if (equalsString == null) {
			return;
		}
		this.equalsString = equalsString;
	}

	public View getShowView() {
		return showView;
	}

	public void setShowView(View showView) {
		this.showView = showView;
	}

	public String getWheelTitle1String() {
		return wheelTitle1String;
	}

	public void setWheelTitle1String(String wheelTitle1String) {
		this.wheelTitle1String = wheelTitle1String;
	}

	public String getWheelTitle2String() {
		return wheelTitle2String;
	}

	public void setWheelTitle2String(String wheelTitle2String) {
		this.wheelTitle2String = wheelTitle2String;
	}

	public String getWheelTitle3String() {
		return wheelTitle3String;
	}

	public void setWheelTitle3String(String wheelTitle3String) {
		this.wheelTitle3String = wheelTitle3String;
	}

	public String getWheelTitle4String() {
		return wheelTitle4String;
	}

	public void setWheelTitle4String(String wheelTitle4String) {
		this.wheelTitle4String = wheelTitle4String;
	}

	public String getContent() {
		String str = content.getText().toString();
		if (content2.getVisibility() == View.VISIBLE) {
			str = content2.getText().toString();
			content2.setVisibility(View.GONE);
		}
		return str;
	}

	public BaseSelectItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		inflater = LayoutInflater.from(context);
		init();
		attrCode(context, attrs);
	}

	public BaseSelectItem(Context context) {
		super(context);
		inflater = LayoutInflater.from(context);
		init();
	}

	public void showItemImage(boolean show) {
	}

	private void init() {
		contentnLayout = (LinearLayout) inflater.inflate(
				R.layout.base_select_item, null);
		title = (TextView) contentnLayout.findViewById(R.id.title);
		content = (TextView) contentnLayout.findViewById(R.id.content);
		content2 = (EditText) contentnLayout.findViewById(R.id.content2);

		LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);

		contentnLayout.setLayoutParams(layoutParams);

		contentnLayout.setOnClickListener(this);

		addView(contentnLayout);
	}

	private void attrCode(Context context, AttributeSet attrs) {
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.BaseSelectItem);

		int n = a.getIndexCount();
		for (int i = 0; i < n; i++) {
			int attr = a.getIndex(i);
			switch (attr) {
			case R.styleable.BaseSelectItem_basetitle:
				title.setText(a.getString(R.styleable.BaseSelectItem_basetitle));
				break;
			case R.styleable.BaseSelectItem_content:
				content.setText(a.getString(R.styleable.BaseSelectItem_content));
				break;
			case R.styleable.BaseSelectItem_dialogContent:
				dialogContentId = a.getResourceId(
						R.styleable.BaseSelectItem_dialogContent, 0);
				break;
			case R.styleable.BaseSelectItem_textSize:
				textSize = 0;
				textSize = a.getDimension(R.styleable.BaseSelectItem_textSize,
						0);
				if (textSize != 0) {
					title.setTextSize(textSize);
					content.setTextSize(textSize);
				}
				break;
			default:
				break;
			}
		}
		a.recycle();
	}

	public void addDialogContentView(View v) {
		// TODO Auto-generated method stub
		if (dialogContent != null) {
			dialogContent.removeAllViews();
			dialogContent.addView(v);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (canClick) {
			showAlertDialog();
		}
	}

	private void showAlertDialog() {
		// TODO Auto-generated method stub
		AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
		final AlertDialog ad = builder.create();
		ad.show();
		Window window = ad.getWindow();

		int layoutId = R.layout.dialog_alert_select;
		View view = LayoutInflater.from(getContext()).inflate(layoutId, null);
		window.setContentView(view);
		window.findViewById(R.id.yes).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (result != null) {
					result.result(dialogContent, content);
				}
				ad.dismiss();
			}
		});
		window.findViewById(R.id.no).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ad.dismiss();
			}
		});
		dialogContent = (LinearLayout) window.findViewById(R.id.selectContent);
		if (dialogContentId != 0) {
			showView = inflater.inflate(dialogContentId, null);
			LayoutParams layoutParams = new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			showView.setLayoutParams(layoutParams);

			if (wheelTitle1String == null && wheelTitle2String == null
					&& wheelTitle3String == null && wheelTitle4String == null
					&& showView.findViewById(R.id.titleLayout) != null) {
				showView.findViewById(R.id.titleLayout)
						.setVisibility(View.GONE);
			} else {
				wheelTitle1 = (TextView) showView.findViewById(R.id.title1);
				wheelTitle2 = (TextView) showView.findViewById(R.id.title2);
				wheelTitle3 = (TextView) showView.findViewById(R.id.title3);
				wheelTitle4 = (TextView) showView.findViewById(R.id.title4);

				if (wheelTitle1 != null && wheelTitle1String != null) {
					wheelTitle1.setText(wheelTitle1String);
				}
				if (wheelTitle2 != null && wheelTitle2String != null) {
					wheelTitle2.setText(wheelTitle2String);
				}
				if (wheelTitle3 != null && wheelTitle3String != null) {
					wheelTitle3.setText(wheelTitle3String);
				}
				if (wheelTitle4 != null && wheelTitle4String != null) {
					wheelTitle4.setText(wheelTitle4String);
				}

			}

			dialogContent.addView(showView);
			View selectWheel = dialogContent.findViewById(R.id.selectWheel);
			if (selectWheel != null && selectWheel instanceof BaseSelcetWheel) {
				((BaseSelcetWheel) selectWheel).wheelInterfasc = interfasc2;
				if (selectContent != null
						&& !(selectWheel instanceof SelectWheel3)
						&& !(selectWheel instanceof SelectWheel2)
						&& !(selectWheel instanceof CitySelectWheel)) {
					((BaseSelcetWheel) selectWheel)
							.setContentList(selectContent);
				}

				if (selectContent != null && select2Content != null
						&& select3Content != null
						&& selectWheel instanceof SelectWheel3) {
					((SelectWheel3) selectWheel).setContentList1(selectContent);
					((SelectWheel3) selectWheel)
							.setContentList2(select2Content);
					((SelectWheel3) selectWheel)
							.setContentList3(select3Content);
				}
				if (selectContent != null && select2Content != null
						&& select3Content != null && select4Content != null
						&& selectWheel instanceof SelectWheel4) {
					((SelectWheel4) selectWheel).setContentList1(selectContent);
					((SelectWheel4) selectWheel)
							.setContentList2(select2Content);
					((SelectWheel4) selectWheel)
							.setContentList3(select3Content);
					((SelectWheel4) selectWheel)
							.setContentList4(select4Content);
				}
				if (selectContent != null && select2Content != null
						&& selectWheel instanceof SelectWheel2) {
					((SelectWheel2) selectWheel).setContentList1(selectContent);
					((SelectWheel2) selectWheel)
							.setContentList2(select2Content);
				}

				((BaseSelcetWheel) selectWheel).getAll();
				if (selectWheel instanceof DateSelectWheel) {
					((DateSelectWheel) selectWheel).setShowDay(showDay);
					if (showDay) {
						dialogContent.findViewById(R.id.minsView)
								.setVisibility(View.GONE);
						dialogContent.findViewById(R.id.dayView).setVisibility(
								View.GONE);
					}
				}
			}

		}
	}

	iWheel interfasc = new iWheel() {

		@Override
		public void selectValue(int selectID, String selectItem,
				boolean oneString) {
			synchronized (this) {
				// TODO Auto-generated method stub
				if (selectItem != null) {
					selectItem = selectItem.replace(" ", "");
				}
				if (oneString) {
					content.setText(selectItem);
				} else {
					String str = content.getText().toString();
					List<String> s = Arrays.asList(str.split("[-]"));
					ArrayList<String> sb = new ArrayList<String>(s);
					switch (selectID) {
					case 1:
						if (sb.size() > 0) {
							sb.set(0, selectItem);
						} else {
							sb.add(selectItem);
						}
						break;
					case 2:
						if (sb.size() > 1) {
							sb.set(1, selectItem);
						} else {
							sb.add(selectItem);
						}
						break;
					case 3:
						if (sb.size() > 2) {
							sb.set(2, selectItem);
						} else {
							sb.add(selectItem);
						}
						break;
					case 4:
						if (sb.size() > 3) {
							sb.set(3, selectItem);
						} else {
							sb.add(selectItem);
						}
						break;
					}
					str = "";
					for (int i = 0; i < sb.size(); i++) {
						if (i == 0) {
							str = sb.get(i);
						} else {
							if (!sb.get(i).equals("")) {
								str = str + "-" + sb.get(i);
							}
						}
					}
					content.setText(str);
				}
			}
		}
	};
	iWheel interfasc2 = new iWheel() {

		@Override
		public void selectValue(int selectID, String selectItem,
				boolean oneString) {
			synchronized (this) {
				// TODO Auto-generated method stub
				if (selectItem != null) {
					selectItem = selectItem.replace(" ", "");
					try {
						selectItem = new String(selectItem.getBytes(),"UTF-8");
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (oneString) {
					content.setText(selectItem);
					if (selectItem.equals(equalsString)) {
						content2.setVisibility(View.VISIBLE);
					} else {
						content2.setText("");
						content2.setVisibility(View.GONE);
					}
					// title2.setText(content.getText());
				} else {
					String str = content.getText().toString();
					List<String> s = Arrays.asList(str.split("[-]"));
					ArrayList<String> sb = new ArrayList<String>(s);
					switch (selectID) {
					case 1:
						if (sb.size() > 0) {
							sb.set(0, selectItem);
						} else {
							sb.add(selectItem);
						}
						break;

					case 2:
						if (sb.size() > 1) {
							sb.set(1, selectItem);
						} else {
							sb.add(selectItem);
						}
						break;
					case 3:
						if (sb.size() > 2) {
							sb.set(2, selectItem);
						} else {
							sb.add(selectItem);
						}
						break;
					case 4:
						if (sb.size() > 3) {
							sb.set(3, selectItem);
						} else {
							sb.add(selectItem);
						}
						break;
					}
					str = "";
					for (int i = 0; i < sb.size(); i++) {
						if (i == 0) {
							str = sb.get(i);
						} else {
							if (!sb.get(i).equals("")) {
								str = str + "-" + sb.get(i);
							}
						}
					}
					content.setText(str);
					if (str.equals(equalsString)) {
						content2.setVisibility(View.VISIBLE);
					} else {
						content2.setText("");
						content2.setVisibility(View.GONE);
					}
				}
			}
		}
	};
}

