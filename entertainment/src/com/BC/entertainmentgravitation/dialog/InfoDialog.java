package com.BC.entertainmentgravitation.dialog;

import com.BC.entertainmentgravitation.R;
import com.BC.entertainmentgravitation.entity.Member;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.summer.view.CircularImage;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 
 * @author wen zhong
 *
 */
public class InfoDialog extends Dialog{

	public InfoDialog(Context context) {
		super(context);
	}

	public InfoDialog(Context context, int dialog) {
		super(context, dialog);
	}

	public static class Builder{
		
		private Context context;
		private View view;
		
		private DialogInterface.OnClickListener positiveClickListener;
		private DialogInterface.OnClickListener negativeClickListener;
		private View.OnClickListener managerListerner;
		private View.OnClickListener closeListerner;
		
		private Member member;
		
		public Member getMember() {
			return member;
		}

		public void setMember(Member member) {
			this.member = member;
		}

		public DialogInterface.OnClickListener getPositiveClickListener() {
			return positiveClickListener;
		}

		public void setPositiveClickListener(
				DialogInterface.OnClickListener positiveClickListener) {
			this.positiveClickListener = positiveClickListener;
		}

		public DialogInterface.OnClickListener getNegativeClickListener() {
			return negativeClickListener;
		}

		public void setNegativeClickListener(
				DialogInterface.OnClickListener negativeClickListener) {
			this.negativeClickListener = negativeClickListener;
		}

		public View.OnClickListener getManagerListerner() {
			return managerListerner;
		}

		public void setManagerListerner(View.OnClickListener managerListerner) {
			this.managerListerner = managerListerner;
		}

		public View.OnClickListener getCloseListerner() {
			return closeListerner;
		}

		public void setCloseListerner(View.OnClickListener closeListerner) {
			this.closeListerner = closeListerner;
		}

		public Builder(Context context) {
			this.context = context;
		}
		
		private boolean isNullOrEmpty(String o)
		{
			if (o != null)
			{
				if (o.length() == 0)
				{
					return true;
				}
				else
				{
					return false;
				}
			}
			else
			{
				return true;
			}
		}
		
		@SuppressLint("InflateParams")
		public InfoDialog create(){
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			final InfoDialog dialog = new InfoDialog(context,
					R.style.Dialog);
			view = inflater.inflate(R.layout.dialog_info, null);
			dialog.addContentView(view, new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			if (member != null)
			{
				((TextView) view.findViewById(R.id.txtViewName)).setText(isNullOrEmpty(member.getNick()) ? "未知" : member.getNick());
				((TextView) view.findViewById(R.id.txtViewId)).setText(isNullOrEmpty(member.getId()) ? "未知" : member.getId());
				((TextView) view.findViewById(R.id.txtViewLocation)).setText(isNullOrEmpty(member.getRegion()) ? "未知" : member.getRegion());
				ImageView imgViewFemale = (ImageView) view.findViewById(R.id.imgViewFeMale);
				ImageView imgViewMale = (ImageView) view.findViewById(R.id.imgViewMale);
				if (member.getGender() != null && member.getGender().contains("男"))
				{

					imgViewFemale.setVisibility(View.GONE);
				}
				else if (member.getGender() != null && member.getGender().contains("女"))
				{

					imgViewMale.setVisibility(View.GONE);
				}
				else 
				{
					imgViewFemale.setVisibility(View.GONE);
					imgViewMale.setVisibility(View.GONE);
				}
				((TextView) view.findViewById(R.id.txtViewFans)).setText(isNullOrEmpty(member.getFans()) ? "未知" : member.getFans());
				((TextView) view.findViewById(R.id.txtViewFocus)).setText(isNullOrEmpty(member.getFollow()) ? "未知" : member.getFollow());
				((TextView) view.findViewById(R.id.txtViewMood)).setText(isNullOrEmpty(member.getMood()) ? "您还没有发表任何心情哦~" : member.getMood());
				
				ImageView imgViewAuthenticated = (ImageView) view.findViewById(R.id.imgViewAuthenticated);
				if (member.getIs_validated() != null && member.getIs_validated().contains("1"))
				{
					imgViewAuthenticated.setVisibility(View.VISIBLE);
				}
				else
				{
					imgViewAuthenticated.setVisibility(View.GONE);
				}
			}
			TextView manager = (TextView) view.findViewById(R.id.txtViewManager);
			ImageView imgViewClose = (ImageView) view.findViewById(R.id.imgViewClose);
			TextView home = (TextView) view.findViewById(R.id.txtViewHome);
			TextView focus = (TextView) view.findViewById(R.id.txtViewToFocus);
			CircularImage cImagePortrait = (CircularImage) view.findViewById(R.id.cImageportrait);
			
			Glide.with(context)
			.load(member.getPortrait())
			.centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL)
			.placeholder(R.drawable.avatar_def).into(cImagePortrait);
	        
			if (managerListerner != null)
			{
				manager.setOnClickListener(new View.OnClickListener(){
					@Override
					public void onClick(View v) {
						managerListerner.onClick(v);
					}
					
				});
			}
			
			if (closeListerner != null)
			{
				imgViewClose.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						closeListerner.onClick(v);
					}
				});
			}
			
			if (positiveClickListener != null)
			{
				home.setOnClickListener(new View.OnClickListener(){

					@Override
					public void onClick(View v) {
						positiveClickListener.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
					}
					
				});
			}
			if (negativeClickListener != null)
			{
				focus.setOnClickListener(new View.OnClickListener(){

					@Override
					public void onClick(View v) {
						negativeClickListener.onClick(dialog, DialogInterface.BUTTON_NEGATIVE);
					}
					
				});
			}
			dialog.setContentView(view);
			return dialog;
		}
	}
}
