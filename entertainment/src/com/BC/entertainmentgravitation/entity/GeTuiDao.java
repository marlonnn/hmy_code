package com.BC.entertainmentgravitation.entity;

import java.sql.SQLException;
import java.util.List;

import com.BC.entertainmentgravitation.util.DatabaseHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import android.content.Context;

public class GeTuiDao {

	@SuppressWarnings("unused")
	private Context context;  
	private Dao<GeTui, Integer> geTuiDaoOpe; 
	
	private DatabaseHelper helper; 
	
	@SuppressWarnings("unchecked")
	public GeTuiDao(Context context)
	{
		this.context = context;
		try
		{
			helper = DatabaseHelper.getHelper(context);
			geTuiDaoOpe = helper.getDao(GeTui.class);
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	public void add(GeTui getui)
	{
		try
		{
			geTuiDaoOpe.create(getui);
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	public GeTui get(int id)
	{
		try
		{
			return geTuiDaoOpe.queryForId(id);
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public void update(String columnName, boolean value)
	{
		try {
			geTuiDaoOpe.updateBuilder().where().eq(columnName, value);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	

	public void update(GeTui geTui)
	{
		try {
			geTuiDaoOpe.update(geTui);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void update()
	{
		try {
			List<GeTui> data = GetAll();
			for (GeTui g : data)
			{
				if (!g.isHasRead())
				{
					g.setHasRead(true);
					geTuiDaoOpe.update(g);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void update(String columnName, String value)
	{
		try {
			List<GeTui> data = Query(columnName, value);
			for (GeTui g : data)
			{
				if (!g.isHasRead())
				{
					g.setHasRead(true);
					geTuiDaoOpe.update(g);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public List<GeTui> GetAll()
	{
		try {
			return geTuiDaoOpe.queryForAll();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public List<GeTui> Query(String columnName, String value)
	{
		try {
			QueryBuilder<GeTui, Integer> builder = geTuiDaoOpe.queryBuilder();
			builder.where().eq(columnName, value);
			builder.query();
			return builder.query();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
