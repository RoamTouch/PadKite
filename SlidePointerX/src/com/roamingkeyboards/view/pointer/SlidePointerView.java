package com.roamingkeyboards.view.pointer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.view.View;

import com.roamingkeyboards.domain.slide.pointer.SlidePointer;
import com.roamingkeyboards.domain.slide.position.Coordinates;

public class SlidePointerView extends View {

	private SlidePointer slidePointer;
	private Coordinates fingerCoordinates;
	private Paint mBorderPaint;
	private Paint mInnerPaint;

	private void makePaints() {

		this.mBorderPaint = new Paint();
		this.mBorderPaint.setARGB(255, 255, 255, 255);
		this.mBorderPaint.setAntiAlias(true);
		this.mBorderPaint.setStyle(Style.STROKE);
		this.mBorderPaint.setStrokeWidth(2);

		this.mInnerPaint = new Paint();
		this.mInnerPaint.setARGB(225, 75, 75, 75);
		this.mInnerPaint.setAntiAlias(true);
	}

	public SlidePointerView(final Context context,final SlidePointer slidePointer,final Coordinates fingerCoordinates) {
		this(context,slidePointer);
		this.fingerCoordinates = fingerCoordinates;
	}

	public SlidePointerView(final Context context, final SlidePointer slidePointer) {
		super(context);
		this.slidePointer = slidePointer;
		makePaints();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		slidePointer.updateCoordinates(fingerCoordinates);

		float left = slidePointer.getXCoordinate();
		float top = slidePointer.getYCoordinate();
		float right = slidePointer.getXCoordinate()+10;
		float bottom = slidePointer.getYCoordinate()+10;
		canvas.drawRect(left, top, right, bottom, mInnerPaint);
	}
}
