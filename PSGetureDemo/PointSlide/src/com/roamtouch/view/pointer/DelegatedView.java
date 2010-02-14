package com.roamtouch.view.pointer;

import android.view.MotionEvent;
import android.view.View;

public class DelegatedView {
	
	private View view;
	
	private MotionEvent event;

	private DelegatedView(View view, MotionEvent event) {
		this.view = view;
		this.event = event;
	}

	/**
	 * Factory method to build a delegated view 
	 * 
	 * @param view
	 * @param event
	 * @return
	 */
	public static DelegatedView make(View view,MotionEvent event) {
		
		return new DelegatedView(view,event);
	}
	
	public View getView() {
		return view;
	}

	public MotionEvent getEvent() {
		return event;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((event == null) ? 0 : event.hashCode());
		result = prime * result + ((view == null) ? 0 : view.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DelegatedView other = (DelegatedView) obj;
		if (event == null) {
			if (other.event != null)
				return false;
		} else if (!event.equals(other.event))
			return false;
		if (view == null) {
			if (other.view != null)
				return false;
		} else if (!view.equals(other.view))
			return false;
		return true;
	}
	
	
}
