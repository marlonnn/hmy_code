package com.BC.entertainmentgravitation.dialog;


import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.BC.entertainmentgravitation.R;

public class PictureDialog extends Dialog {

	public PictureDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		// TODO Auto-generated constructor stub
	}

	public PictureDialog(Context context, int theme) {
		super(context, theme);
		// TODO Auto-generated constructor stub
		setOwnerActivity((Activity) context);
	}

	public PictureDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public static class Builder {

		private Context context;
		private View layout;
		private DialogInterface.OnClickListener negativeButtonClickListener;

		public Builder(Context context) {
			this.context = context;
		}

		public Builder setNegativeButton(
				DialogInterface.OnClickListener listener) {
			this.negativeButtonClickListener = listener;
			return this;
		}

		public View findViewById(int id) {
			if (layout == null) {
				return null;
			}
			return layout.findViewById(id);
		}

		public PictureDialog create() {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			// instantiate the dialog with the custom Theme
			final PictureDialog dialog = new PictureDialog(context,
					R.style.Dialog2);
			layout = inflater.inflate(R.layout.dialog_picture, null);
			android.view.ViewGroup.LayoutParams layoutParams = layout
					.getLayoutParams();
			layoutParams = new LayoutParams(layoutParams.MATCH_PARENT,
					layoutParams.MATCH_PARENT);
			layout.setLayoutParams(layoutParams);
			dialog.addContentView(layout, new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			if (negativeButtonClickListener != null) {
				((Button) layout.findViewById(R.id.negativeButton))
						.setOnClickListener(new View.OnClickListener() {
							public void onClick(View v) {
								negativeButtonClickListener.onClick(dialog,
										DialogInterface.BUTTON_NEGATIVE);
							}
						});
			}
			return dialog;
		}

	}
}

