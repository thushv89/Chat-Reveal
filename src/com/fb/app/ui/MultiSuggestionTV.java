package com.fb.app.ui;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

public class MultiSuggestionTV extends AutoCompleteTextView{

	
	private String previous = "";
	private String seperator = ",";

	public  MultiSuggestionTV(final Context context, final AttributeSet attrs, final int defStyle) {
		super(context, attrs, defStyle);
		this.setThreshold(0);
	}

	public MultiSuggestionTV(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		this.setThreshold(0);
	}

	public MultiSuggestionTV(final Context context) {
		super(context);
		this.setThreshold(0);
	}

	@Override
	protected void performFiltering(final CharSequence text, final int keyCode) {

		String filterText = text.toString().trim();
		previous = filterText.substring(0,filterText.lastIndexOf(getSeperator())+1);
		filterText = filterText.substring(filterText.lastIndexOf(getSeperator()) + 1);
		if(!TextUtils.isEmpty(filterText)){
			super.performFiltering(filterText, keyCode);
		}

	}

	@Override
	protected void replaceText(final CharSequence text) {
		super.replaceText(previous+text+getSeperator());
	}

	public String getSeperator() {
		return seperator;
	}
	public void setSeperator(final String seperator) {
		this.seperator = seperator;
	}

}
