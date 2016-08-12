/*
 *  Copyright (C) 2016 Ingenic Semiconductor
 *  
 *  ShiGuangHua(Kenny)<guanghua.shi@ingenic.com>
 *   
 *  Elf/AmazingClock Project
 *
 *  This program is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU General Public License as published by the
 *  Free Software Foundation; either version 2 of the License, or (at your
 *  option) any later version.
 *
 *  You should have received a copy of the GNU General Public License along
 *  with this program; if not, write to the Free Software Foundation, Inc.,
 *  675 Mass Ave, Cambridge, MA 02139, USA.
 *
 */
package com.example.testgif;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 
 * @author ShiGuangHua(Kenny)
 *
 */
public class GifVideoImageView extends ImageView {

	// 播放gif动画的关键类
	private Movie mMovie = null;
	// 记录动画开始的时间
	private long mMovieStart;
	// 画笔对象
	private Paint mPaint;
	// 文件路径
	private String mPath;
	private Context mContext;
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				invalidate();
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}

	};

	public GifVideoImageView(Context context) {
		super(context);
		mContext = context;
	}

	public GifVideoImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}

	public GifVideoImageView(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mContext = context;
	}

	// 初始化一些设置
	// 通过路径名读取文件 -> 输入流 -> 字节数组
	// Movie对象解码字节数组
	// 发送message 通知mHandler invalidate（）
	@SuppressLint("NewApi") private void initAndReadGif() {
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		setLayerType(LAYER_TYPE_SOFTWARE, mPaint);// 禁止硬件加速（GPU） 设为软件加速
	}

	/**
	 * 开始播放gif视频
	 */
	public void startPlayGifVideoView() {
		if (mMovie == null) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						// InputStream is = new FileInputStream(mPath);
						AssetManager mngr = mContext.getAssets();
						InputStream is = mngr.open(mPath);

						byte[] array = streamToBytes(is);
						// 使用Movie类对字节数组解码
						mMovie = Movie.decodeByteArray(array, 0, array.length);
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					mHandler.sendEmptyMessage(0);
				}
			}).start();
		} else {
			mHandler.sendEmptyMessage(0);
		}
	}

	// 转换成字节数组
	private byte[] streamToBytes(InputStream is) {
		ByteArrayOutputStream os = new ByteArrayOutputStream(1024);
		byte[] buffer = new byte[1024];
		int len;
		try {
			while ((len = is.read(buffer)) >= 0) {
				os.write(buffer, 0, len);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return os.toByteArray();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (mMovie == null) {
			// mMovie为null 则是一张普通图片，直接调用父类的onDraw
			super.onDraw(canvas);
		} else {
			// 是 gif 图片
			long now = SystemClock.uptimeMillis();
			if (mMovieStart == 0) {
				mMovieStart = now; // 动画开始时间（第1帧）
			}
			int duration = mMovie.duration();// 动画持续时间（gif总帧数）
			if (duration == 0) {
				duration = 1000;
			}
			int realTime = (int) ((now - mMovieStart) % duration);// 当前帧数处于整个动画的时间（当前处于第几帧）
			// 绘制当前时间对应的帧 （绘制当前帧）
			mMovie.setTime(realTime);
			mMovie.draw(canvas, 0, 0); // 在（0,0）位置画
			/*
			 * if((now-mMovieStart)>=duration){ mMovieStart=0; }
			 */
			invalidate(); // 不断onDraw（）
		}
	}

	public String getGifVideoViewPath() {
		return mPath;
	}

	public void setGifVideoViewPath(String mPath) {
		this.mPath = mPath;
		initAndReadGif();
	}
}
