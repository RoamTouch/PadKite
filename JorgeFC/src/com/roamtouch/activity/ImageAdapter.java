package com.roamtouch.activity;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {

	private Context mContext;
	
	private Integer[] mImageIds = {
			R.drawable.image_cursor,
			R.drawable.link_cursor,
			R.drawable.icon,
			R.drawable.no_target_cursor,
			R.drawable.text_cursor,
			R.drawable.address_bar_cursor
	};
	
	public ImageAdapter(Context c) {
		this.mContext = c;	
	}
	
	@Override
	public int getCount() {
		return mImageIds.length;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ImageView i = new ImageView(mContext);
		i.setImageResource(this.mImageIds[position]);
		i.setLayoutParams(new Gallery.LayoutParams(200,50));
		i.setScaleType(ImageView.ScaleType.FIT_XY);
		return i;
	}
}
