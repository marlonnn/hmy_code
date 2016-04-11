package com.BC.entertainment.view;

import com.summer.config.Config;
import com.summer.view.Histogram;
import com.summer.view.LineChart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.util.AttributeSet;
import android.view.View;

public class CoordinateSystemView extends View {

	/**
	 * y,x轴的名称
	 */
	String yName, xName;
	/**
	 * 原点
	 */
	float x0, y0;
	/**
	 * x,y轴最大值
	 */
	float xMax, yMax;
	/**
	 * 轴线颜色
	 */
	int axisColor;
	/**
	 * y、x轴的步长
	 */
	float yStepSize, xStepSize;
	/**
	 * y轴分割线
	 */
	boolean yPartingLine = true;
	/**
	 * x轴分割线
	 */
	boolean xPartingLine = true;
	/**
	 * x分割数量
	 */
	int xParting = 24;
	/**
	 * y分割数量
	 */
	int yParting = 7;
	/**
	 * y轴显示最大值
	 */
	float yShowMax = 7;
	/**
	 * x轴显示最大值
	 */
	float xShowMax = 23;
	/**
	 * 折线图
	 */
	LineChart lineChart;
	/**
	 * 直方图
	 */
	Histogram histogram;
	/**
	 * y轴分割线标注
	 */
	String[] xPartingName;
	/**
	 * x轴标注
	 */
	String[] yPartingName;

	/**
	 * x轴标注颜色
	 * 
	 * @return
	 */
	int xTextColor = Color.WHITE;

	int yTextColor = Color.WHITE;
	/**
	 * 显示坐标线
	 */
	boolean showLine = true;
	/**
	 * 显示X坐标的起点
	 */
	boolean showXBeginNames = true;

	float xOffSet = 0;

	// int xLineColor =

	public String getyName() {
		return yName;
	}

	public float getxOffSet() {
		return xOffSet;
	}

	public void setxOffSet(float xOffSet) {
		this.xOffSet = xOffSet;
	}

	public boolean isShowXBeginNames() {
		return showXBeginNames;
	}

	public void setShowXBeginNames(boolean showXBeginNames) {
		this.showXBeginNames = showXBeginNames;
	}

	public boolean isShowLine() {
		return showLine;
	}

	public void setShowLine(boolean showLine) {
		this.showLine = showLine;
	}

	public void setyName(String yName) {
		this.yName = yName;
	}

	public String getxName() {
		return xName;
	}

	public void setxName(String xName) {
		this.xName = xName;
	}

	public boolean isyPartingLine() {
		return yPartingLine;
	}

	public void setyPartingLine(boolean yPartingLine) {
		this.yPartingLine = yPartingLine;
	}

	public int getxParting() {
		return xParting;
	}

	public void setxParting(int xParting) {
		this.xParting = xParting;
	}

	public int getyParting() {
		return yParting;
	}

	public void setyParting(int yParting) {
		this.yParting = yParting;
	}

	public float getyShowMax() {
		return yShowMax;
	}

	public void setyShowMax(float yShowMax) {
		this.yShowMax = yShowMax;
	}

	public float getxShowMax() {
		return xShowMax;
	}

	public void setxShowMax(float xShowMax) {
		this.xShowMax = xShowMax;
	}

	public LineChart getLineChart() {
		return lineChart;
	}

	public void setLineChart(LineChart lineChart) {
		this.lineChart = lineChart;
		this.invalidate();
	}

	public Histogram getHistogram() {
		return histogram;
	}

	public void setHistogram(Histogram histogram) {
		this.histogram = histogram;
		this.invalidate();
	}

	public String[] getxPartingName() {
		return xPartingName;
	}

	public void setxPartingName(String[] xPartingName) {
		this.xPartingName = xPartingName;
	}

	public String[] getyPartingName() {
		return yPartingName;
	}

	public void setyPartingName(String[] yPartingName) {
		this.yPartingName = yPartingName;
	}

	public CoordinateSystemView(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
	}

	public CoordinateSystemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public CoordinateSystemView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		// if (showXBeginNames) {
		// xStepSize = (this.getWidth() - 180 - 2 * xOffSet) / (xParting - 1);
		// } else {
		// xStepSize = (this.getWidth() - 180 - 2 * xOffSet) / (xParting + 1);
		// }
		x0 = 80 + xOffSet;
		y0 = this.getHeight() - 40;
		xMax = this.getWidth() - 80 - xOffSet;
		yMax = 40;
		xStepSize = (xMax - x0) / xParting;
		yStepSize = (y0 - yMax) / (yParting);
		Paint paint;
		paint = new Paint();
		if (showLine) {
			// paint.setColor(Color.WHITE);
			// paint.setStrokeWidth(0);
			// canvas.drawLine(x0, yMax, x0, y0, paint);
			// canvas.drawLine(x0 - 1, y0, xMax, y0, paint);
			// if (yPartingLine && yParting > 0) {
			// paint.setColor(Color.GRAY);
			// paint.setStrokeWidth(1);
			// for (int i = 0; i < yParting; i++) {
			// canvas.drawLine(x0 - 1, y0 - yStepSize * (i + 1), xMax, y0
			// - yStepSize * (i + 1), paint);
			// }
			// }
			// if (xPartingLine && xParting > 0) {
			// paint.setColor(Color.GRAY);
			// paint.setStrokeWidth(1);
			// for (int i = 0; i < xParting; i++) {
			// canvas.drawLine(x0 - 1, y0 - yStepSize * (i + 1), xMax, y0
			// - yStepSize * (i + 1), paint);
			// }
			// }
		}
		float textsize = yStepSize * 3 / 5;
		if (textsize > 27) {
			textsize = 27;
		}
		paint.setColor(yTextColor);
		paint.setStrokeWidth(4);
		paint.setTextSize(textsize);
		paint.setTextAlign(Paint.Align.RIGHT);
		paint.setTypeface(Config.tf);
		if (yName != null) {
			canvas.drawText(yName, x0, yMax, paint);
		}
		FontMetricsInt fmi = paint.getFontMetricsInt();
		if (yPartingName == null || yPartingName.length != yParting) {
			for (int i = 0; i < yParting; i++) {
				float baseline = y0 - yStepSize * (i + 1);
				// canvas.drawText(i + 1 + "00", x0 - 10, baseline - yStepSize,
				// paint);
				canvas.drawText(i + 1 + "00", x0 - 10, baseline, paint);
			}
		} else {
			for (int i = 0; i < yPartingName.length; i++) {
				float baseline = y0 - yStepSize * (i + 1);
				// canvas.drawText(yPartingName[i], x0 - 10, baseline -
				// yStepSize,
				// paint);
				canvas.drawText(yPartingName[i], x0 - 10, baseline, paint);
			}
		}
		textsize = xStepSize * 2 / 5;
		if (textsize > 27) {
			textsize = 27;
		}
		paint.setTextSize(textsize);
		paint.setTextAlign(Paint.Align.CENTER);
		paint.setColor(xTextColor);
		if (xName != null) {
			canvas.drawText(xName, xMax, y0 - paint.getFontMetrics().ascent
					+ 10, paint);
		}
		if (xPartingName == null || xPartingName.length != xParting) {
			for (int i = 0; i < xParting; i++) {
				int xb = i + 1;
				if (showXBeginNames) {
					xb = i;
				}
				// canvas.drawText(xb + ":00", x0 + xStepSize * xb,
				// y0 - paint.getFontMetrics().ascent + 10, paint);
				canvas.drawText(xb + ":00", x0 + xStepSize * xb,
						y0 - paint.getFontMetrics().ascent + 10, paint);
			}
		} else {
			for (int i = 0; i < xPartingName.length; i++) {
				int xb = i + 1;
				if (showXBeginNames) {
					xb = i;
				}
				canvas.drawText(xPartingName[i], x0 + xStepSize * xb, y0
						- paint.getFontMetrics().ascent + 10, paint);
			}
		}
		paint.setColor(Color.GREEN);
		paint.setStrokeWidth(2);
		initHistogramBg();
		if (histogram != null) {
			histogram.drawHistogram(canvas, paint, x0, xMax, y0, yMax,
					xShowMax, yShowMax);
		}
		if (lineChart != null) {
			lineChart.drawLineChart(canvas, paint, x0, xMax, y0, yMax,
					xShowMax, yShowMax);
		}
	}

	private void initHistogramBg() {
		// TODO Auto-generated method stub
		histogram = new Histogram(getContext());
		histogram.setNumber(xParting);
		for (int i = 1; i < xParting; i++) {
			histogram.addPoint(i, yShowMax, null);
		}
	}
}
