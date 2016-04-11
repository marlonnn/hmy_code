package com.BC.entertainmentgravitation.dialog;


import android.app.ActionBar.LayoutParams;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.BC.entertainmentgravitation.R;

public class AnimationDialog extends Dialog {

	public AnimationDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		// TODO Auto-generated constructor stub
	}

	public AnimationDialog(Context context, int theme) {
		super(context, theme);
		// TODO Auto-generated constructor stub
	}

	public AnimationDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public static class Builder {
		public interface AnimationOver {
			public void AnimationOver();
		}

		private Context context;
		private String title;
		private String message;
		private String positiveButtonText;
		private String negativeButtonText;
		private View contentView;
		private View layout;
		private ImageView imageView1;
		private DialogInterface.OnClickListener positiveButtonClickListener;
		private DialogInterface.OnClickListener negativeButtonClickListener;
		private AnimationDrawable animationDrawable;
		private AnimationOver animationOver;
		private AudioTrack audioTrack;
		private MediaPlayer player;
		private int audioFile;

		public Builder(Context context) {
			this.context = context;
		}

		public Builder setMessage(String message) {
			this.message = message;
			return this;
		}

		/**
		 * Set the Dialog message from resource
		 * 
		 * @param title
		 * @return
		 */
		public Builder setMessage(int message) {
			this.message = (String) context.getText(message);
			return this;
		}

		/**
		 * Set the Dialog title from resource
		 * 
		 * @param title
		 * @return
		 */
		public Builder setTitle(int title) {
			this.title = (String) context.getText(title);
			return this;
		}

		/**
		 * Set the Dialog title from String
		 * 
		 * @param title
		 * @return
		 */

		public Builder setTitle(String title) {
			this.title = title;
			return this;
		}

		public Builder setContentView(View v) {
			this.contentView = v;
			return this;
		}

		public AnimationDrawable getAnimationDrawable() {
			return animationDrawable;
		}

		public void setAnimationDrawable(Context context, int id) {
			animationDrawable = (AnimationDrawable) context.getResources()
					.getDrawable(id);
		}

		public AnimationOver getAnimationOver() {
			return animationOver;
		}

		public void setAnimationOver(AnimationOver animationOver) {
			this.animationOver = animationOver;
		}

		public void setAudioFile(int audioFile) {
			this.audioFile = audioFile;
		}

		/**
		 * Set the positive button resource and it's listener
		 * 
		 * @param positiveButtonText
		 * @return
		 */
		public Builder setPositiveButton(int positiveButtonText,
				DialogInterface.OnClickListener listener) {
			this.positiveButtonText = (String) context
					.getText(positiveButtonText);
			this.positiveButtonClickListener = listener;
			return this;
		}

		public Builder setPositiveButton(String positiveButtonText,
				DialogInterface.OnClickListener listener) {
			this.positiveButtonText = positiveButtonText;
			this.positiveButtonClickListener = listener;
			return this;
		}

		public Builder setNegativeButton(int negativeButtonText,
				DialogInterface.OnClickListener listener) {
			this.negativeButtonText = (String) context
					.getText(negativeButtonText);
			this.negativeButtonClickListener = listener;
			return this;
		}

		public Builder setNegativeButton(String negativeButtonText,
				DialogInterface.OnClickListener listener) {
			this.negativeButtonText = negativeButtonText;
			this.negativeButtonClickListener = listener;
			return this;
		}

		public View findViewById(int id) {
			if (layout == null) {
				return null;
			}
			return layout.findViewById(id);
		}

		public AnimationDialog create() {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			// instantiate the dialog with the custom Theme
			final AnimationDialog dialog = new AnimationDialog(context,
					R.style.Dialog);
			layout = inflater.inflate(R.layout.dialog_animation, null);

			imageView1 = (ImageView) findViewById(R.id.imageView1);
			if (animationDrawable != null) {
				imageView1.setImageDrawable(animationDrawable);
			}
			dialog.addContentView(layout, new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			// dialog.setContentView(layout);
			return dialog;
		}

		public void startAnimation() {
			// TODO Auto-generated method stub
			animationDrawable.start();
			if (audioFile != 0) {
				// audioTrack = Audio.palyAudio(context, R.raw.concern);
				player = new MediaPlayer().create(context, audioFile);

				player.start();
			}
			int duration = 0;
			for (int i = 0; i < animationDrawable.getNumberOfFrames(); i++) {
				duration += animationDrawable.getDuration(i);
			}
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				public void run() {
					// 此处调用第二个动画播放方法
					animationDrawable.stop();
					if (animationOver != null) {
						animationOver.AnimationOver();
					}
					if (audioTrack != null) {
						audioTrack.stop();
					}
					if (player != null) {
						player.stop();
					}
				}
			}, duration);
		}
	}
}

