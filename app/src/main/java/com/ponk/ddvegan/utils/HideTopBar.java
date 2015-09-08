package com.ponk.ddvegan.utils;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class HideTopBar extends RecyclerView.ItemDecoration {
	private int qrBarHeight;

	public HideTopBar(int qrBarHeight) {
		this.qrBarHeight = qrBarHeight;
	}

	@Override
	public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
			RecyclerView.State state) {
		if (parent.getChildPosition(view) == 0) {
			outRect.set(0, qrBarHeight, 0, 0);
		} else {
			outRect.set(0, 0, 0, 0);
		}
	}
}
