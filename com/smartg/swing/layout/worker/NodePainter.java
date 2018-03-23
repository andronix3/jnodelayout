package com.smartg.swing.layout.worker;

import java.awt.Graphics;
import java.awt.Rectangle;

import com.smartg.swing.layout.LayoutNode;

class NodePainter implements NodeWorker {
	private Graphics graphics;
	private Rectangle bounds;

	public void doWork(LayoutNode node) {
		node.paintNode(graphics, bounds);
	}

	public Graphics getGraphics() {
		return graphics;
	}

	public void setGraphics(Graphics graphics) {
		this.graphics = graphics;
	}

	public Rectangle getBounds() {
		return bounds;
	}

	public void setBounds(Rectangle bounds) {
		this.bounds = bounds;
	}
}