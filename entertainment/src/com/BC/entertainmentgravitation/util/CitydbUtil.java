package com.BC.entertainmentgravitation.util;

import java.util.ArrayList;
import java.util.List;

import com.BC.entertainmentgravitation.entity.MyListItem;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CitydbUtil {
	private DBManager dbm;
	private SQLiteDatabase db;
	private static CitydbUtil citydbUtil;

	public CitydbUtil(Context context) {
		dbm = new DBManager(context);
	}

	public static CitydbUtil structureCitydbUtil(Context context) {
		if (citydbUtil == null) {
			citydbUtil = new CitydbUtil(context);
		}
		return citydbUtil;
	}

	private void openDB() {
		dbm.openDatabase();
		db = dbm.getDatabase();
	}

	private void closDB() {
		dbm.closeDatabase();
		db.close();
	}

	public List<MyListItem> selectCountry() {
		openDB();
		List<MyListItem> list = new ArrayList<MyListItem>();

		try {
			String sql = "select * from fs_province";
			Cursor cursor = db.rawQuery(sql, null);
			cursor.moveToFirst();
			while (!cursor.isLast()) {
				String code = cursor.getString(cursor
						.getColumnIndex("ProvinceID"));
				String name = cursor.getString(1);
				MyListItem myListItem = new MyListItem();
				myListItem.setName(name);
				myListItem.setPcode(code);
				list.add(myListItem);
				cursor.moveToNext();
			}
			String code = cursor.getString(cursor.getColumnIndex("ProvinceID"));
			String name = cursor.getString(1);
			MyListItem myListItem = new MyListItem();
			myListItem.setName(name);
			myListItem.setPcode(code);
			list.add(myListItem);

		} catch (Exception e) {
		}
		closDB();
		return list;
	}

	public List<MyListItem> selectCity(String pcode) {
		openDB();
		List<MyListItem> list = new ArrayList<MyListItem>();
		try {
			String sql = "select * from fs_city where ProvinceID='" + pcode
					+ "'";
			Cursor cursor = db.rawQuery(sql, null);
			cursor.moveToFirst();
			while (!cursor.isLast()) {
				String code = cursor.getString(cursor.getColumnIndex("CityID"));
				String name = cursor.getString(1);
				MyListItem myListItem = new MyListItem();
				myListItem.setName(name);
				myListItem.setPcode(code);
				list.add(myListItem);
				cursor.moveToNext();
			}
			String code = cursor.getString(cursor.getColumnIndex("CityID"));
			String name = cursor.getString(1);
			MyListItem myListItem = new MyListItem();
			myListItem.setName(name);
			myListItem.setPcode(code);
			list.add(myListItem);

		} catch (Exception e) {
		}
		closDB();
		return list;
	}

	public List<MyListItem> selectCounty(String pcode) {
		openDB();
		List<MyListItem> list = new ArrayList<MyListItem>();

		try {
			String sql = "select * from fs_district where CityID='" + pcode
					+ "'";
			Cursor cursor = db.rawQuery(sql, null);
			cursor.moveToFirst();
			while (!cursor.isLast()) {
				String code = cursor.getString(cursor
						.getColumnIndex("DistrictID"));
				String name = cursor.getString(1);
				MyListItem myListItem = new MyListItem();
				myListItem.setName(name);
				myListItem.setPcode(code);
				list.add(myListItem);
				cursor.moveToNext();
			}
			String code = cursor.getString(cursor.getColumnIndex("DistrictID"));
			String name = cursor.getString(1);
			MyListItem myListItem = new MyListItem();
			myListItem.setName(name);
			myListItem.setPcode(code);
			list.add(myListItem);

		} catch (Exception e) {
		}
		closDB();
		return list;
	}
}
