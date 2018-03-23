package com.smartg.swing.layout.worker;

import java.awt.Rectangle;

import com.smartg.swing.layout.LayoutNode;

class NodeLayoutMaker {
	private Rectangle bounds;

	public void doWork(LayoutNode node) {
		node.layout(bounds);
	}

	public Rectangle getBounds() {
		return bounds;
	}

	public void setBounds(Rectangle bounds) {
		this.bounds = bounds;
	}
}