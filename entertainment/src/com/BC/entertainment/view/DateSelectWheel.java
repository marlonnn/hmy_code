package com.BC.entertainment.view;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.AbstractWheelTextAdapter;
import kankan.wheel.widget.adapters.NumericWheelAdapter;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.BC.entertainmentgravitation.R;

public class DateSelectWheel extends BaseSelcetWheel {
	private WheelView hours;
	private WheelView mins;
	private WheelView day;
	private NumericWheelAdapter dayArrayAdapter;
	private NumericWheelAdapter hourAdapter;
	private NumericWheelAdapter minAdapter;
	Calendar calendar = Calendar.getInstance(Locale.CHINA);
	public boolean showDay = false;

	public boolean isShowDay() {
		return showDay;
	}

	public void setShowDay(boolean showDay) {
		this.showDay = showDay;
		if (showDay) {
			hours.setVisibility(View.GONE);
			mins.setVisibility(View.GONE);
		}
	}

	public DateSelectWheel(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public DateSelectWheel(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public DateSelectWheel(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void addView(View child, int index,
			android.view.ViewGroup.LayoutParams params) {
		// TODO Auto-generated method stub
		if (child instanceof WheelView) {

			switch (child.getId()) {
			case R.id.hour:
				hours = (WheelView) child;
				hours.setDrawShadows(false);
				hourAdapter = new NumericWheelAdapter(child.getContext(), 1,
						12, "%02d");
				hourAdapter.setItemResource(R.layout.wheel_text_item);
				hourAdapter.setItemTextResource(R.id.text);
				hours.setViewAdapter(hourAdapter);
				hours.setCurrentItem(calendar.get(Calendar.MONTH));
				hours.addChangingListener(new OnWheelChangedListener() {

					@Override
					public void onChanged(WheelView wheel, int oldValue,
							int newValue) {
						// TODO Auto-generated method stub
						Calendar calendar = Calendar.getInstance(Locale.CHINA);
						calendar.add(
								Calendar.YEAR,
								Integer.valueOf(dayArrayAdapter.getItemText(
										day.getCurrentItem()).toString()));
						calendar.add(Calendar.MONTH, newValue + 2);
						minAdapter = new NumericWheelAdapter(
								wheel.getContext(),
								1,
								calendar.getActualMaximum(calendar.DAY_OF_MONTH),
								"%02d");
						minAdapter.setItemResource(R.layout.wheel_text_item);
						minAdapter.setItemTextResource(R.id.text);
						mins.setViewAdapter(minAdapter);
						mins.setCurrentItem(0);
						updateDate();
					}
				});

				break;
			case R.id.mins:
				mins = (WheelView) child;
				mins.setDrawShadows(false);
				minAdapter = new NumericWheelAdapter(child.getContext(), 1,
						calendar.getActualMaximum(calendar.DAY_OF_MONTH),
						"%02d");
				minAdapter.setItemResource(R.layout.wheel_text_item);
				minAdapter.setItemTextResource(R.id.text);
				mins.setViewAdapter(minAdapter);
				mins.setCurrentItem(calendar.get(Calendar.DAY_OF_MONTH) - 1);
				mins.addChangingListener(new OnWheelChangedListener() {

					@Override
					public void onChanged(WheelView wheel, int oldValue,
							int newValue) {
						// TODO Auto-generated method stub
						updateDate();
					}
				});
				break;
			case R.id.day:
				day = (WheelView) child;
				day.setDrawShadows(false);
				dayArrayAdapter = new NumericWheelAdapter(child.getContext(),
						1900, calendar.get(Calendar.YEAR), "%02d");
				dayArrayAdapter.setItemResource(R.layout.wheel_text_item);
				dayArrayAdapter.setItemTextResource(R.id.text);
				day.setViewAdapter(dayArrayAdapter);
				day.setCurrentItem(calendar.get(Calendar.YEAR) - 1900);
				day.addChangingListener(new OnWheelChangedListener() {

					@Override
					public void onChanged(WheelView wheel, int oldValue,
							int newValue) {
						// TODO Auto-generated method stub
						updateDate();
					}
				});
				day.getCurrentItem();

				break;
			}
		}
		super.addView(child, index, params);
	}

	private void updateDate() {
		if (wheelInterfasc != null) {
			String date = dayArrayAdapter.getItemText(day.getCurrentItem())
					.toString();
			if (!showDay) {
				date += "-"
						+ hourAdapter.getItemText(hours.getCurrentItem())
								.toString()
						+ "-"
						+ minAdapter.getItemText(mins.getCurrentItem())
								.toString();
			}
			wheelInterfasc.selectValue(0, date, true);
		}
	}

	// protected void updateCity(int currentItem) {
	// // TODO Auto-generated method stub
	// if (countryList == null) {
	// return;
	// }
	// RegionItem item = countryList.get(currentItem);
	// cityList = citydbUtil.selectCity(item.getPcode());
	// city.setViewAdapter(new CountryAdapter(getContext(), cityList));
	// city.setCurrentItem(1);
	// city.setCurrentItem(0);
	// }
	//
	// protected void updateCounty(int currentItem) {
	// // TODO Auto-generated method stub
	// if (cityList != null) {
	// RegionItem item = cityList.get(currentItem);
	// countyList = citydbUtil.selectCounty(item.getPcode());
	// } else {
	// countyList = new ArrayList<RegionItem>();
	// }
	// if (county != null) {
	// county.setViewAdapter(new CountryAdapter(getContext(), countyList));
	// }
	// }
	private class DayArrayAdapter extends AbstractWheelTextAdapter {
		// Count of days to be shown
		private final int daysCount = 90;

		// Calendar
		Calendar calendar;

		/**
		 * Constructor
		 */
		protected DayArrayAdapter(Context context, Calendar calendar) {
			super(context, R.layout.wheel_text_item, NO_RESOURCE);
			this.calendar = calendar;

			setItemTextResource(R.id.text);
		}

		@Override
		public View getItem(int index, View cachedView, ViewGroup parent) {
			int day = index;
			Calendar newCalendar = (Calendar) calendar.clone();
			newCalendar.roll(Calendar.YEAR, day);
			HashMap<String, String> date = getDate(day);
			View view = super.getItem(index, cachedView, parent);

			TextView monthday = (TextView) view.findViewById(R.id.text);
			monthday.setText(date.get("monthday"));
			return view;
		}

		public HashMap<String, String> getDate(int day) {
			HashMap<String, String> date = new HashMap<String, String>();
			Calendar newCalendar = (Calendar) calendar.clone();
			newCalendar.roll(Calendar.YEAR, day);

			DateFormat format2 = new SimpleDateFormat("yyyy");
			date.put("year", (format2.format(newCalendar.getTime())));
			return date;
		}

		@Override
		public int getItemsCount() {
			return daysCount + 1;
		}

		@Override
		protected CharSequence getItemText(int index) {
			return "";
		}
	}
}
