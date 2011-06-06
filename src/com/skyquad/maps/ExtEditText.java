package com.skyquad.maps;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.EditText;

public class ExtEditText extends EditText {
	public ExtEditText(Context context, AttributeSet attrs) {
		super(context, attrs);		
	}

	public void setEditable(boolean mode) {
		this.setFocusable(mode);
		this.setFocusableInTouchMode(mode);
		this.setClickable(mode);
		// Indicate the state of the control by using a colors.
		if (mode) {
			this.setTextColor(Color.BLACK);
		} else {
			this.setTextColor(Color.GRAY);
		}
	}
}