package com.BC.entertainmentgravitation.fragment;

import android.support.v4.app.Fragment;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.BC.entertainmentgravitation.R;
import com.BC.entertainmentgravitation.entity.Day;
import com.BC.entertainmentgravitation.entity.SignTime;
import com.BC.entertainmentgravitation.util.TimestampTool;

@SuppressLint("InflateParams") public class CalendarFragment extends Fragment{

	View contentView;
	LayoutInflater inflater;

	private Calendar calendar;
	private Day[] days;
	private int month = -2;
	private int year = 2013;

	int[] ids = { R.id.dayLayout1, R.id.dayLayout2, R.id.dayLayout3,
			R.id.dayLayout4, R.id.dayLayout5, R.id.dayLayout6, R.id.dayLayout7,
			R.id.dayLayout8, R.id.dayLayout9, R.id.dayLayout10,
			R.id.dayLayout11, R.id.dayLayout12, R.id.dayLayout13,
			R.id.dayLayout14, R.id.dayLayout15, R.id.dayLayout16,
			R.id.dayLayout17, R.id.dayLayout18, R.id.dayLayout19,
			R.id.dayLayout20, R.id.dayLayout21, R.id.dayLayout22,
			R.id.dayLayout23, R.id.dayLayout24, R.id.dayLayout25,
			R.id.dayLayout26, R.id.dayLayout27, R.id.dayLayout28,
			R.id.dayLayout29, R.id.dayLayout30, R.id.dayLayout31,
			R.id.dayLayout32, R.id.dayLayout33, R.id.dayLayout34,
			R.id.dayLayout35, R.id.dayLayout36, R.id.dayLayout37,
			R.id.dayLayout38, R.id.dayLayout39, R.id.dayLayout40,
			R.id.dayLayout41, R.id.dayLayout42 };

	List<SignTime> sign;

	public List<SignTime> getSign() {
		return sign;
	}

	public void setSign(List<SignTime> sign) {
		this.sign = sign;
		init();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.inflater = inflater;
		contentView = inflater.inflate(R.layout.fragment_calender, null);
		return contentView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		init();
	}

	private void init() {
		calendar = Calendar.getInstance();
		year = calendar.get(Calendar.YEAR);
		days = monthData();
		for (int i = 0; i < ids.length; i++) {
			View v = newItem(i);
			LinearLayout layout = (LinearLayout) contentView
					.findViewById(ids[i]);
			layout.removeAllViews();
			layout.addView(v);
		}
	}

	public View newItem(int position) {
		LinearLayout layout = new LinearLayout(getActivity());
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setGravity(Gravity.CENTER);
		layout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		TextView textView = new TextView(getActivity());
		textView.setGravity(Gravity.CENTER_HORIZONTAL);
		textView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
		textView.setText(getDayOfMonth(days[position].dayOfMonth + ""));
		if (days[position].isBlack) {
			textView.setTextColor(Color.WHITE);
		} else {
			textView.setTextColor(Color.GRAY);
		}
		if (days[position].isSelect) {
			contentView.findViewById(ids[position]).setBackgroundColor(
					Color.BLUE);
		} else {
			contentView.findViewById(ids[position]).setBackgroundColor(
					Color.BLACK);
		}
		Calendar calendar = Calendar.getInstance();
		calendar.set(days[position].year, days[position].month,
				days[position].dayOfMonth);

		Date date = calendar.getTime();
		String d = TimestampTool.parseDate(date);
		if (sign != null && sign.size() > 0) {
			for (int i = 0; i < sign.size(); i++) {
				String p = TimestampTool.parseDate(TimestampTool
						.strToDateLong(sign.get(i).signTime));
				if (p.equals(d)) {
					contentView.findViewById(ids[position]).setBackgroundColor(
							Color.GREEN);
				}
			}
		}
		textView.setId(100 + position);
		layout.addView(textView);
		layout.setTag(100 + position);
		return layout;
	}

	public Day[] monthData() {
		Day[] days = new Day[42];
		Date date = calendar.getTime();
		int theDayOfMonth;
		int theMonth;
		if (month == -2
				|| (month == calendar.get(Calendar.MONTH) && year == calendar
						.get(Calendar.YEAR))) {
			theDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
			theMonth = calendar.get(Calendar.MONTH);
			month = theMonth;
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			calendar.setTime(getFirstDayOfWeek(calendar.getTime()));
		} else {
			calendar.set(year, month, 1);
			theDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
			theMonth = month;
			calendar.setTime(getFirstDayOfWeek(calendar.getTime()));
		}
		Calendar calendarNow = Calendar.getInstance();
		for (int i = 0; i < days.length; i++) {
			Day day = new Day();

			day.dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
			day.dayOfweek = calendar.get(Calendar.DAY_OF_WEEK);
			day.month = calendar.get(Calendar.MONTH);
			day.year = calendar.get(Calendar.YEAR);
			if (day.dayOfMonth >= theDayOfMonth && theMonth == day.month) {
				day.isBlack = true;
				if (day.dayOfMonth == calendarNow.get(Calendar.DAY_OF_MONTH)
						&& day.month == calendarNow.get(Calendar.MONTH)
						&& day.year == calendarNow.get(Calendar.YEAR)) {
					day.isSelect = true;
				}
			}

			days[i] = day;
			calendar.set(Calendar.DAY_OF_MONTH,
					calendar.get(Calendar.DAY_OF_MONTH) + 1);
		}
		calendar.setTime(date);
		return days;
	}

	public static Date getFirstDayOfWeek(Date date) {
		Calendar c = new GregorianCalendar();
		c.setFirstDayOfWeek(Calendar.SUNDAY);
		c.setTime(date);
		c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek()); // Monday
		return c.getTime();
	}

	public SpannableString getDayOfMonth(String d) {
		String lunarAndSolar = d;
		SpannableString sp = new SpannableString(lunarAndSolar);
		sp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0,
				d.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		sp.setSpan(new RelativeSizeSpan(1.8f), 0, d.length(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		return sp;
	}
}
