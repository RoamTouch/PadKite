package com.roamtouch.view;

import java.util.ArrayList;
import com.roamtouch.settings.PracticeGesture;
import com.roamtouch.swiftee.BrowserActivity;
import android.gesture.Gesture;
import android.gesture.GestureLibrary;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TutorArea extends LinearLayout implements OnClickListener {

	private GestureLibrary mLibrary;
	private Context mContext;
	private BrowserActivity parent;
	private PracticeGesture gParent;
	private int gestureCount;
	private String str[];
	
	public TutorArea(Context context, AttributeSet attrs) {
		super(context, attrs);	
		mContext = context;
		BITMAP_RENDERING_WIDTH = getResources().getDimensionPixelSize(com.roamtouch.swiftee.R.dimen.tutor_gesture_stroke);
	}
	public void setGestureLibrary(GestureLibrary l) {
		mLibrary = l;
		initView();
	}
	
    private static float BITMAP_RENDERING_WIDTH = 4;

    private static final boolean BITMAP_RENDERING_ANTIALIAS = true;
    private static final boolean BITMAP_RENDERING_DITHER = true;
	
	private Bitmap toBitmap(Gesture gesture, int width, int height, int inset, int color)
	{
		final Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);

        final Paint paint = new Paint();
        paint.setAntiAlias(BITMAP_RENDERING_ANTIALIAS);
        paint.setDither(BITMAP_RENDERING_DITHER);
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(BITMAP_RENDERING_WIDTH);

        final Path path = gesture.toPath();
        final RectF bounds = new RectF();
        path.computeBounds(bounds, true);

        final float sx = (width - 2 * inset) / bounds.width();
        final float sy = (height - 2 * inset) / bounds.height();
        final float scale = sx > sy ? sy : sx;
        paint.setStrokeWidth(BITMAP_RENDERING_WIDTH / scale);

        path.offset(-bounds.left + (width - bounds.width() * scale) / 2.0f,
                -bounds.top + (height - bounds.height() * scale) / 2.0f);

        canvas.translate(inset, inset);
        canvas.scale(scale, scale);

        canvas.drawPath(path, paint);

        return bitmap;	
	}
	
	
	private void initView(){
		this.removeAllViews();
		Object tmp[] = mLibrary.getGestureEntries().toArray();
		
		str = new String[tmp.length];
		
		for (int i = 0; i < tmp.length; i++)
			str[i] = tmp[i].toString();
		
		java.util.Arrays.sort( str );
		gestureCount = str.length;

		int buttonGrid = getResources().getDimensionPixelSize(com.roamtouch.swiftee.R.dimen.tutor_button_grid_width);
		int buttonWidth = getResources().getDimensionPixelSize(com.roamtouch.swiftee.R.dimen.tutor_button_width);
		int imageWidth = getResources().getDimensionPixelSize(com.roamtouch.swiftee.R.dimen.tutor_button_image_width);
		int padding = getResources().getDimensionPixelSize(com.roamtouch.swiftee.R.dimen.tutor_button_padding);
		LayoutParams params=new LayoutParams(buttonWidth, buttonWidth);

		for(int i=0;i<gestureCount;i++){
			
			LinearLayout linLayout = new LinearLayout(mContext);
			linLayout.setLayoutParams(new LayoutParams(buttonGrid, LayoutParams.FILL_PARENT));
			linLayout.setOrientation(VERTICAL);
			linLayout.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
			
			linLayout.setPadding(0, padding, 0, padding);

			ImageButton b=new ImageButton(mContext);
			b.setId(i);
			//b.setBackgroundResource(R.drawable.tutor_button_1);
			b.setLayoutParams(params);
			//b.setText(BrowserActivity.convertGestureItem(str[i]));
			String caca= str[i].toString();
			ArrayList<Gesture> list = mLibrary.getGestures(caca);
			Bitmap bit = toBitmap(list.get(0), imageWidth, imageWidth, 10, Color.BLACK);
			BitmapDrawable d = new BitmapDrawable(bit);
			//b.setCompoundDrawablesWithIntrinsicBounds(null,d, null, null);
			b.setImageDrawable(d);
			b.setOnClickListener(this);
			
			TextView tv = new TextView(mContext);
			tv.setText(BrowserActivity.convertGestureItem(str[i]));
			tv.setTextColor(Color.BLACK);
			tv.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
			
			linLayout.addView(b);
			linLayout.addView(tv);
			
			this.addView(linLayout);
		}
	}
	public void onClick(View v) {
		if(parent!=null){
			ArrayList<Gesture> list = mLibrary.getGestures(str[v.getId()].toString());			
			parent.drawGestureToEducate(list.get(0), str[v.getId()].toString());
			return;
		}
		if(gParent != null){
			
		}
	}
	public void setParent(Activity parent) {
		if (parent instanceof BrowserActivity)
			this.parent = (BrowserActivity) parent;		
		else if(parent instanceof PracticeGesture)	
			this.gParent = (PracticeGesture)parent;
	}
	
}
