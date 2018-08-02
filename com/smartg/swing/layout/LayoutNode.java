/*
 * Copyright (c) Andrey Kuznetsov. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  o Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  o Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  o Neither the name of Andrey Kuznetsov nor the names of
 *    its contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.smartg.swing.layout;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.border.Border;

import com.smartg.java.util.PreorderIterator;
import com.smartg.util.EmptyIterator;

/**
 * Tree-like layout manager. Components are placed only leafs (one leaf contains
 * just one component). Currently implemented nodes are GridNode,
 * HorizontalNode, VerticalNode and LeafNode.
 */
public abstract class LayoutNode implements Iterable<LayoutNode> {

	private String name;
	private LayoutNode parent;
	protected NodeAlignment horizontalAlignment = NodeAlignment.STRETCHED;
	protected NodeAlignment verticalAlignment = NodeAlignment.STRETCHED;
	protected ArrayList<LayoutNode> invalidNodes = new ArrayList<>();
	private Integer hgap, vgap;
	private Boolean debug;

	private Border border;

	private Container target;

	public LayoutNode(String name) {
		this.name = name;
	}

	public abstract Dimension preferredSize();

	public void add(LayoutNode layout) {
		add(layout, null);
	}

	public Border getBorder() {
		return border;
	}

	public Insets getNodeInsets() {
		Border b = getBorder();
		Container t = getTarget();
		if (b != null && t != null) {
			return b.getBorderInsets(t);
		}
		return new Insets(0, 0, 0, 0);
	}

	public void setBorder(Border border) {
		this.border = border;
	}

	public abstract void add(LayoutNode layout, Object constraints);

	public abstract int getCount();

	public abstract void remove(LayoutNode layout);

	protected Container getTarget() {
		if (parent != null) {
			return parent.getTarget();
		}
		return target;
	}

	void setTarget(Container target) {
		this.target = target;
	}

	public void print(int level) {
		System.out.println(create(level) + name);
		for (LayoutNode node : this) {
			node.print(level + 4);
		}
	}

	public boolean isDebug() {
		if (debug != null) {
			return debug;
		}
		LayoutNode parent = getParent();
		if (parent != null) {
			return parent.isDebug();
		}
		return false;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public LayoutNode findNode(String name) {
		if (name.equals(getName())) {
			return this;
		}
		for (LayoutNode node : this) {
			LayoutNode res = node.findNode(name);
			if (res != null) {
				return res;
			}
		}
		return null;
	}

	public void paintBorder(Graphics g, Rectangle r) {
		if (border != null && g != null) {
			border.paintBorder(getTarget(), g, r.x, r.y, r.width, r.height);
		}
	}

	public void paintNode(Graphics g, Rectangle r) {
		if (g == null) {
			return;
		}
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setColor(Color.RED);
		g2.setStroke(new BasicStroke(2));
		g2.drawRect(r.x, r.y, r.width, r.height);
	}

	@Override
	public String toString() {
		String className = getClass().getName();
		if (className.indexOf('$') >= 0) {
			className = className.substring(className.indexOf('$'));
		} else {
			className = className.substring(className.lastIndexOf('.'));
		}
		return className + " [name=" + name + "]";
	}

	/**
	 * We have to remove leaf nodes (which was added not through LayoutManager),
	 * after their child-component was removed from target Container;
	 */
	protected void removeInvalidNodes() {
		while (invalidNodes.size() > 0) {
			LayoutNode node = invalidNodes.remove(0);
			remove(node);
		}
	}

	public void add(Component comp) {
		add(comp, null);
	}

	public void add(Component comp, Object constraints) {
		LeafNode leaf = new LeafNode(this, comp);
		add(leaf, constraints);
	}

	LeafNode addLeafNode(Component comp, Object constraints) {
		LeafNode leaf = new LeafNode(this, comp);
		add(leaf, constraints);
		return leaf;
	}

	public String getName() {
		return name;
	}

	public boolean isLeaf() {
		return false;
	}

	public abstract void layout(Rectangle dest);

	public int getHgap() {
		if (hgap != null) {
			return hgap;
		}
		if (parent != null) {
			return parent.getHgap();
		}
		return 0;
	}

	public void setHgap(int hgap) {
		this.hgap = hgap;
	}

	public int getVgap() {
		if (vgap != null) {
			return vgap;
		}
		if (parent != null) {
			return parent.getVgap();
		}
		return 0;
	}

	public void setVgap(int vgap) {
		this.vgap = vgap;
	}

	public LayoutNode getParent() {
		return parent;
	}

	public NodeAlignment getHorizontalAlignment() {
		return horizontalAlignment;
	}

	public void setHorizontalAlignment(NodeAlignment alignment) {
		switch (alignment) {
		case TOP:
		case BOTTOM:
			throw new IllegalArgumentException("Using vertical constant for horizontal alignment.");
		default:
			this.horizontalAlignment = alignment;
			break;
		}
	}

	public Iterator<Component> components() {
		return new ComponentIterator();
	}

	private class ComponentIterator implements Iterator<Component> {

		private final Iterator<LayoutNode> iterator = new PreorderIterator<>(LayoutNode.this);

		private Component comp;

		public ComponentIterator() {
			comp = goNext();
		}

		private Component goNext() {
			while (iterator.hasNext()) {
				LayoutNode node = iterator.next();
				if (node.isLeaf()) {
					LeafNode leafNode = (LeafNode) node;
					return leafNode.getComponent();
				}
			}
			return null;
		}

		@Override
		public boolean hasNext() {
			return comp != null;
		}

		@Override
		public Component next() {
			Component tmp = comp;
			comp = goNext();
			return tmp;
		}

		@Override
		public void remove() {
		}
	}

	public NodeAlignment getVerticalAlignment() {
		return verticalAlignment;
	}

	protected void setInvalidNode(LayoutNode node) {
		if (node != null && node.getParent() == this && !invalidNodes.contains(node)) {
			invalidNodes.add(node);
		}
	}

	public void setVerticalAlignment(NodeAlignment alignment) {
		switch (alignment) {
		case LEFT:
		case RIGHT:
			throw new IllegalArgumentException("Using horizontal constant for vertical alignment.");
		default:
			this.verticalAlignment = alignment;
			break;
		}
	}

	protected int adjustX(int destWidth, int preferredWidth) {
		int dx = 0;
		if (horizontalAlignment == NodeAlignment.RIGHT) {
			dx = Math.max(0, destWidth - preferredWidth);
		} else if (horizontalAlignment == NodeAlignment.CENTER) {
			dx = Math.max(0, (destWidth - preferredWidth) / 2);
		}
		return dx;
	}

	protected int adjustY(int destHeight, int preferredHeight) {
		int dy = 0;
		if (verticalAlignment == NodeAlignment.BOTTOM) {
			dy = Math.max(0, destHeight - preferredHeight);
		} else if (verticalAlignment == NodeAlignment.CENTER) {
			dy = Math.max(0, (destHeight - preferredHeight) / 2);
		}
		return dy;
	}

	String create(int length) {
		StringBuffer sb = new StringBuffer(length);
		for (int i = 0; i < length; i++) {
			sb.append(' ');
		}
		return sb.toString();
	}

	static class LeafNode extends LayoutNode {

		private Component component;
		private Container container;

		LeafNode(LayoutNode p, Component component) {
			super("");
			((LayoutNode) this).parent = p;
			if (getTarget() != null) {
				getTarget().add(component);
			}
			this.component = component;
			this.container = component.getParent();
		}

		@Override
		public Dimension preferredSize() {
			if (component.getParent() != container) {
				if (container == null) {
					container = component.getParent();
				} else {
					getParent().setInvalidNode(this);
					return new Dimension();
				}
			}
			int hgap = getHgap();
			int vgap = getVgap();

			if (component.isVisible()) {

				Dimension preferredSize = component.getPreferredSize();

				preferredSize.width += hgap;
				preferredSize.height += vgap;

				Insets insets = getNodeInsets();
				preferredSize.width += insets.left + insets.right;
				preferredSize.height += insets.top + insets.bottom;

				return preferredSize;
			}
			return new Dimension();
		}

		@Override
		public Iterator<LayoutNode> iterator() {
			return new EmptyIterator<>();
		}

		@Override
		public boolean isLeaf() {
			return true;
		}

		@Override
		public void add(LayoutNode layout, Object constraints) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void layout(Rectangle dest) {
			if (component.getParent() != container) {
				getParent().setInvalidNode(this);
				return;
			}

			Insets insets = getNodeInsets();

			dest.x += insets.left;
			dest.y += insets.top;
			dest.width -= insets.left + insets.right;
			dest.height -= insets.top + insets.bottom;

			int hgap = getHgap();
			int vgap = getVgap();

			if (horizontalAlignment == NodeAlignment.STRETCHED && verticalAlignment == NodeAlignment.STRETCHED) {
				Rectangle r = new Rectangle(dest.x + hgap, dest.y + vgap, dest.width - hgap, dest.height - vgap);
				component.setBounds(r);
				if (isDebug()) {
					String s = component.getName();
					if (s != null) {
						System.out.println(s + " " + r);
					}
				}
			} else {
				Dimension ps = preferredSize();

				double mx = 1;
				if (horizontalAlignment == NodeAlignment.STRETCHED) {
					mx = dest.getWidth() / ps.getWidth();
				}

				double my = 1;
				if (verticalAlignment == NodeAlignment.STRETCHED) {
					my = dest.getHeight() / ps.getHeight();
				}

				int dx = adjustX(dest.height, ps.height);
				int dy = adjustY(dest.width, ps.width);

				int x = dest.x + dx + hgap;
				int y = dest.y + dy + vgap;

				double height = ps.height * my;
				double width = ps.width * mx;

				Rectangle r = new Rectangle(x, y, (int) (width - hgap), (int) (height - vgap));
				component.setBounds(r);
				if (isDebug()) {
					String s = component.getName();
					if (s != null) {
						System.out.println(s + " " + r);
					}
				}
			}
		}

		Component getComponent() {
			return component;
		}

		@Override
		public void remove(LayoutNode layout) {

		}

		@Override
		public void print(int level) {
			System.out.println(create(level) + component);
		}

		@Override
		public int getCount() {
			return 1;
		}

		@Override
		public String toString() {
			return super.toString() + " [" + component.getName() + "]";
		}
	}

	public static class VerticalNode extends LayoutNode {

		private ArrayList<LayoutNode> list = new ArrayList<>();

		public VerticalNode(String name) {
			super(name);
		}

		@Override
		public Dimension preferredSize() {
			int height = 0;
			int width = 0;
			for (LayoutNode c : this) {
				Dimension ps = c.preferredSize();
				height += ps.height;
				width = Math.max(width, ps.width);
			}
			Insets insets = getNodeInsets();
			width += insets.left + insets.right;
			height += insets.top + insets.bottom;

			return new Dimension(width, height);
		}

		@Override
		public Iterator<LayoutNode> iterator() {
			return list.iterator();
		}

		@Override
		public void add(LayoutNode layout, Object constraints) {
			if (!list.contains(layout)) {
				list.add(layout);
				layout.parent = this;
			}
		}

		@Override
		public void layout(Rectangle dest) {
			removeInvalidNodes();

			Insets insets = getNodeInsets();

			dest.x += insets.left;
			dest.y += insets.top;
			dest.width -= insets.left + insets.right;
			dest.height -= insets.top + insets.bottom;

			Xym xym = computeXym(dest);

			for (LayoutNode c : this) {
				Dimension d = c.preferredSize();
				double height = d.height * xym.m;
				Rectangle r = xym.create(height);
				c.layout(r);
				xym.y += height;
			}
		}

		@Override
		public void paintBorder(Graphics g, Rectangle dest) {
			super.paintBorder(g, dest);

			Xym xym = computeXym(dest);

			for (LayoutNode c : this) {
				Dimension d = c.preferredSize();
				double height = d.height * xym.m;
				Rectangle r = xym.create(height);
				c.paintBorder(g, r);
				xym.y += height;
			}
		}

		@Override
		public void paintNode(Graphics g, Rectangle dest) {
			super.paintNode(g, dest);

			Xym xym = computeXym(dest);

			for (LayoutNode c : this) {
				Dimension d = c.preferredSize();
				double height = d.height * xym.m;
				Rectangle r = xym.create(height);
				c.paintNode(g, r);
				xym.y += height;
			}
		}

		private Xym computeXym(Rectangle dest) {
			Dimension ps = preferredSize();
			int width;
			if (horizontalAlignment == NodeAlignment.STRETCHED) {
				width = dest.width;
			} else {
				width = Math.min(dest.width, ps.width);
			}

			float m = 1f;

			if (dest.height < ps.height || verticalAlignment == NodeAlignment.STRETCHED) {
				m = (float) (dest.getHeight() / ps.getHeight());
			}

			int dx = adjustX(dest.width, ps.width);
			int dy = adjustY(dest.height, ps.height);

			int x = dest.x + dx;
			int y = dest.y + dy;

			Xym xym = new Xym();
			xym.x = x;
			xym.y = y;
			xym.m = m;
			xym.width = width;

			return xym;
		}

		static class Xym {

			float m;
			int x, y, width;

			Rectangle create(double height) {
				return new Rectangle(x, y, width, (int) (height));
			}
		}

		@Override
		public void remove(LayoutNode layout) {
			list.remove(layout);
		}

		@Override
		public int getCount() {
			return list.size();
		}
	}

	public static class HorizontalNode extends LayoutNode {

		private ArrayList<LayoutNode> list = new ArrayList<>();

		public HorizontalNode(String name) {
			super(name);
		}

		@Override
		public Dimension preferredSize() {
			int height = 0;
			int width = 0;
			for (LayoutNode c : this) {
				Dimension ps = c.preferredSize();
				width += ps.width;
				height = Math.max(height, ps.height);
			}
			Insets insets = getNodeInsets();
			width += insets.left + insets.right;
			height += insets.top + insets.bottom;

			return new Dimension(width, height);
		}

		@Override
		public Iterator<LayoutNode> iterator() {
			return list.iterator();
		}

		@Override
		public void add(LayoutNode layout, Object constraints) {
			if (!list.contains(layout)) {
				list.add(layout);
				layout.parent = this;
			}
		}

		@Override
		public void layout(Rectangle dest) {
			removeInvalidNodes();

			Insets insets = getNodeInsets();

			dest.x += insets.left;
			dest.y += insets.top;
			dest.width -= insets.left + insets.right;
			dest.height -= insets.top + insets.bottom;

			Dimension ps = preferredSize();
			int height;
			if (verticalAlignment == NodeAlignment.STRETCHED) {
				height = dest.height;
			} else {
				height = Math.min(dest.height, ps.height);
			}

			double m = 1;

			if (dest.width < ps.width || horizontalAlignment == NodeAlignment.STRETCHED) {
				m = dest.getWidth() / ps.getWidth();
			}

			int dx = adjustX(dest.width, ps.width);
			int dy = adjustY(dest.height, ps.height);

			int x = dest.x + dx;
			int y = dest.y + dy;

			for (LayoutNode c : this) {
				Dimension d = c.preferredSize();
				double width = d.width * m;
				Rectangle bounds = new Rectangle();
				bounds.setRect(x, y, width, height);
				c.layout(bounds);
				x += width;
			}
		}

		@Override
		public void paintBorder(Graphics g, Rectangle dest) {
			super.paintBorder(g, dest);

			Dimension ps = preferredSize();
			int height;
			if (verticalAlignment == NodeAlignment.STRETCHED) {
				height = dest.height;
			} else {
				height = Math.min(dest.height, ps.height);
			}

			double m = 1;

			if (dest.width < ps.width || horizontalAlignment == NodeAlignment.STRETCHED) {
				m = dest.getWidth() / ps.getWidth();
			}

			int dx = adjustX(dest.width, ps.width);
			int dy = adjustY(dest.height, ps.height);

			int x = dest.x + dx;
			int y = dest.y + dy;

			for (LayoutNode c : this) {
				Dimension d = c.preferredSize();
				double width = d.width * m;
				Rectangle bounds = new Rectangle();
				bounds.setRect(x, y, width, height);
				c.paintBorder(g, bounds);
				x += width;
			}
		}

		@Override
		public void paintNode(Graphics g, Rectangle dest) {
			super.paintNode(g, dest);

			Dimension ps = preferredSize();
			int height;
			if (verticalAlignment == NodeAlignment.STRETCHED) {
				height = dest.height;
			} else {
				height = Math.min(dest.height, ps.height);
			}

			double m = 1;

			if (dest.width < ps.width || horizontalAlignment == NodeAlignment.STRETCHED) {
				m = dest.getWidth() / ps.getWidth();
			}

			int dx = adjustX(dest.width, ps.width);
			int dy = adjustY(dest.height, ps.height);

			int x = dest.x + dx;
			int y = dest.y + dy;

			for (LayoutNode c : this) {
				Dimension d = c.preferredSize();
				double width = d.width * m;
				Rectangle bounds = new Rectangle();
				bounds.setRect(x, y, width, height);
				c.paintNode(g, bounds);
				x += width;
			}
		}

		@Override
		public void remove(LayoutNode layout) {
			list.remove(layout);
		}

		@Override
		public int getCount() {
			return list.size();
		}
	}

	public static class RectNode extends LayoutNode {

		private HashMap<LayoutNode, Rectangle2D> map = new HashMap<>();

		public RectNode(String name) {
			super(name);
		}

		@Override
		public Iterator<LayoutNode> iterator() {
			return map.keySet().iterator();
		}

		@Override
		public Dimension preferredSize() {
			double width = 0;
			double height = 0;
			for (LayoutNode n : this) {
				Dimension ps = n.preferredSize();
				Rectangle2D r = map.get(n);
				double w = ps.width / r.getWidth();
				double h = ps.height / r.getHeight();
				width = Math.max(w, width);
				height = Math.max(h, height);
			}
			Insets insets = getNodeInsets();
			width += insets.left + insets.right;
			height += insets.top + insets.bottom;

			Dimension res = new Dimension();
			res.setSize(width, height);
			return res;
		}

		private void checkRange(double... nn) {
			for (double d : nn) {
				if (d < -0.001 || d > 1.001) {
					throw new IllegalArgumentException();
				}
			}
		}

		@Override
		public void add(LayoutNode layout, Object constraints) {
			Rectangle2D r = (Rectangle2D) constraints;
			if (r == null) {
				r = new Rectangle2D.Double(0, 0, 1, 1);
			}
			checkRange(r.getX(), r.getY(), r.getX() + r.getWidth(), r.getY() + r.getHeight());
			map.put(layout, r);
			layout.parent = this;
		}

		@Override
		public void remove(LayoutNode layout) {
			map.remove(layout);
		}

		@Override
		public void layout(Rectangle dest) {
			removeInvalidNodes();

			Insets insets = getNodeInsets();

			dest.x += insets.left;
			dest.y += insets.top;
			dest.width -= insets.left + insets.right;
			dest.height -= insets.top + insets.bottom;

			for (LayoutNode n : this) {
				Rectangle2D r = map.get(n);
				int width = dest.width;
				int height = dest.height;

				int x = (int) (width * r.getX());
				int y = (int) (height * r.getY());
				int w = (int) (width * r.getWidth());
				int h = (int) (height * r.getHeight());

				Rectangle bounds = new Rectangle(dest.x + x, dest.y + y, w, h);
				n.layout(bounds);
			}
		}

		@Override
		public void paintBorder(Graphics g, Rectangle dest) {
			super.paintBorder(g, dest);

			for (LayoutNode n : this) {
				Rectangle2D r = map.get(n);
				int width = dest.width;
				int height = dest.height;

				int x = (int) (width * r.getX());
				int y = (int) (height * r.getY());
				int w = (int) (width * r.getWidth());
				int h = (int) (height * r.getHeight());

				Rectangle bounds = new Rectangle(dest.x + x, dest.y + y, w, h);

				n.paintBorder(g, bounds);
			}
		}

		@Override
		public void paintNode(Graphics g, Rectangle dest) {
			super.paintNode(g, dest);
			for (LayoutNode n : this) {
				Rectangle2D r = map.get(n);
				int width = dest.width;
				int height = dest.height;

				int x = (int) (width * r.getX());
				int y = (int) (height * r.getY());
				int w = (int) (width * r.getWidth());
				int h = (int) (height * r.getHeight());

				Rectangle bounds = new Rectangle(dest.x + x, dest.y + y, w, h);

				n.paintNode(g, bounds);
			}
		}

		@Override
		public int getCount() {
			return map.size();
		}
	}

	public static class GridNode extends LayoutNode {

		private GridModel gridModel = new GridModel();

		private HashMap<LayoutNode, Rectangle> map = new HashMap<>();

		public GridNode(String name) {
			super(name);
		}

		@Override
		public Iterator<LayoutNode> iterator() {
			return map.keySet().iterator();
		}

		@Override
		public Dimension preferredSize() {
			Rectangle r = getGridBounds();
			gridModel.setGridHeight(r.height + 1);
			gridModel.setGridWidth(r.width + 1);

			updateCellSizes();

			int width = 0;
			int height = 0;

			for (int x = r.x; x < r.x + r.width; x++) {
				Dimension cs = gridModel.getCellSize(x, 0);
				width += cs.width;
			}

			for (int y = r.y; y < r.y + r.height; y++) {
				Dimension cs = gridModel.getCellSize(0, y);
				height += cs.height;
			}

			Insets insets = getNodeInsets();
			width += insets.left + insets.right;
			height += insets.top + insets.bottom;

			Dimension d = new Dimension(width, height);
			return d;
		}

		private void updateCellSizes() {
			double width = 0;
			double height = 0;
			for (LayoutNode gl : this) {
				Rectangle r = map.get(gl);
				Rectangle2D cs = cellSize(gl, r);

				for (int x = r.x; x < r.x + r.width; x++) {
					for (int y = r.y; y < r.y + r.height; y++) {
						gridModel.setCellSize(x, y, (int) cs.getWidth(), (int) cs.getHeight());
					}
				}

				width = Math.max(width, cs.getWidth());
				height = Math.max(height, cs.getHeight());
			}
		}

		public Rectangle getGridBounds() {
			Rectangle res = new Rectangle();
			for (LayoutNode gl : this) {
				Rectangle r = map.get(gl);
				res = res.union(r);
			}
			return res;
		}

		private Rectangle2D cellSize(LayoutNode gl, Rectangle r) {
			Dimension ps = gl.preferredSize();
			double w = ps.getWidth() / r.getWidth();
			double h = ps.getHeight() / r.getHeight();

			return new Rectangle2D.Double(0, 0, w, h);
		}

		@Override
		public void add(LayoutNode node, Object constraints) {
			if (constraints == null) {
				throw new NullPointerException();
			}
			if (isDebug()) {
				System.out.println("add: " + node + " " + constraints);
			}
			Rectangle r = (Rectangle) constraints;
			map.put(node, r);
			node.parent = this;
		}

		@Override
		public void layout(Rectangle dest) {
			if (isDebug()) {
				System.out.print("");
			}
			removeInvalidNodes();

			Dimension preferredSize = preferredSize();

			double mx = 1;
			if (horizontalAlignment == NodeAlignment.STRETCHED) {
				mx = dest.getWidth() / preferredSize.getWidth();
			}

			double my = 1;
			if (verticalAlignment == NodeAlignment.STRETCHED) {
				my = dest.getHeight() / preferredSize.getHeight();
			}

			int dx = adjustX(dest.width - dest.x, preferredSize.width);
			int dy = adjustY(dest.height - dest.y, preferredSize.height);

			Insets insets = getNodeInsets();

			dest.x += insets.left;
			dest.y += insets.top;
			dest.width -= insets.left + insets.right;
			dest.height -= insets.top + insets.bottom;

			for (LayoutNode gl : this) {
				Rectangle r = map.get(gl);
				int x = getXOffset(0, r.x, mx) + dx;
				double width = getXOffset(r.x, r.x + r.width, mx);
				int y = getYOffset(0, r.y, my) + dy;
				double height = getYOffset(r.y, r.y + r.height, my);

				Rectangle bounds = new Rectangle();
				bounds.setRect(dest.x + x, dest.y + y, width, height);
				gl.layout(bounds);
			}
		}

		@Override
		public void paintBorder(Graphics g, Rectangle dest) {
			super.paintBorder(g, dest);

			Dimension preferredSize = preferredSize();

			double mx = 1;
			if (horizontalAlignment == NodeAlignment.STRETCHED) {
				mx = dest.getWidth() / preferredSize.getWidth();
			}
			double my = 1;
			if (verticalAlignment == NodeAlignment.STRETCHED) {
				my = dest.getHeight() / preferredSize.getHeight();
			}

			int dx = adjustX(dest.width - dest.x, preferredSize.width);
			int dy = adjustY(dest.height - dest.y, preferredSize.height);

			Insets insets = getNodeInsets();

			dest.x += insets.left;
			dest.y += insets.top;
			dest.width -= insets.left + insets.right;
			dest.height -= insets.top + insets.bottom;

			for (LayoutNode gl : this) {
				Rectangle r = map.get(gl);
				int x = getXOffset(0, r.x, mx) + dx;
				double width = getXOffset(r.x, r.x + r.width, mx);
				int y = getYOffset(0, r.y, my) + dy;
				double height = getYOffset(r.y, r.y + r.height, my);

				Rectangle bounds = new Rectangle();
				bounds.setRect(dest.x + x, dest.y + y, width, height);
				gl.paintBorder(g, bounds);
			}
		}

		public int getMaxCellWidth() {
			return gridModel.maxCellWidth;
		}

		public void setMaxCellWidth(int maxCellWidth) {
			gridModel.maxCellWidth = maxCellWidth;
			if (gridModel.minCellWidth > maxCellWidth) {
				gridModel.minCellWidth = maxCellWidth;
			}
		}

		public int getMinCellWidth() {
			return gridModel.minCellWidth;
		}

		public void setMinCellWidth(int minCellWidth) {
			gridModel.minCellWidth = minCellWidth;
			if (gridModel.maxCellWidth < minCellWidth) {
				gridModel.maxCellWidth = minCellWidth;
			}
		}

		public int getMaxCellHeight() {
			return gridModel.maxCellHeight;
		}

		public void setMaxCellWidth(int x, int max) {
			gridModel.setMaxCellWidth(x, max);
		}

		public void setMinCellWidth(int x, int min) {
			gridModel.setMinCellWidth(x, min);
		}

		public void setMaxCellHeight(int y, int max) {
			gridModel.setMaxCellHeight(y, max);
		}

		public void setMinCellHeight(int y, int min) {
			gridModel.setMinCellHeight(y, min);
		}

		public void setMaxCellHeight(int maxCellHeight) {
			gridModel.maxCellHeight = maxCellHeight;
			if (gridModel.minCellHeight > maxCellHeight) {
				gridModel.minCellHeight = maxCellHeight;
			}
		}

		public int getMinCellHeight() {
			return gridModel.minCellHeight;
		}

		public void setMinCellHeight(int minCellHeight) {
			gridModel.minCellHeight = minCellHeight;
			if (gridModel.maxCellHeight < minCellHeight) {
				gridModel.maxCellHeight = minCellHeight;
			}
		}

		@Override
		public void paintNode(Graphics g, Rectangle dest) {
			super.paintNode(g, dest);

			Dimension preferredSize = preferredSize();

			double mx = 1;
			if (horizontalAlignment == NodeAlignment.STRETCHED) {
				mx = dest.getWidth() / preferredSize.getWidth();
			}
			double my = 1;
			if (verticalAlignment == NodeAlignment.STRETCHED) {
				my = dest.getHeight() / preferredSize.getHeight();
			}

			int dx = adjustX(dest.width - dest.x, preferredSize.width);
			int dy = adjustY(dest.height - dest.y, preferredSize.height);

			Insets insets = getNodeInsets();

			dest.x += insets.left;
			dest.y += insets.top;
			dest.width -= insets.left + insets.right;
			dest.height -= insets.top + insets.bottom;

			for (LayoutNode gl : this) {
				Rectangle r = map.get(gl);
				int x = getXOffset(0, r.x, mx) + dx;
				double width = getXOffset(r.x, r.x + r.width, mx);
				int y = getYOffset(0, r.y, my) + dy;
				double height = getYOffset(r.y, r.y + r.height, my);

				Rectangle bounds = new Rectangle();
				bounds.setRect(dest.x + x, dest.y + y, width, height);
				gl.paintNode(g, bounds);
			}
		}

		public void printNodes() {
			for (LayoutNode node : this) {
				System.out.println(node + " " + map.get(node));
			}
		}

		public int getXOffset(int from, int to, double d) {
			int width = 0;

			for (int x = from; x < to; x++) {
				double w = gridModel.getCellSize(x, 0).width * d;
				int max = gridModel.getMaxCellWidth(x);
				int min = gridModel.getMinCellWidth(x);
				width += Math.min(Math.max(w, min), max);
			}
			return width;
		}

		public int getYOffset(int from, int to, double d) {
			int height = 0;
			for (int y = from; y < to; y++) {
				int h = gridModel.getCellSize(0, y).height;
				int max = gridModel.getMaxCellHeight(y);
				int min = gridModel.getMinCellHeight(y);
				height += Math.min(Math.max(h, min), max);
			}
			return height;
		}

		@Override
		public void remove(LayoutNode layout) {
			map.remove(layout);
		}

		@Override
		public int getCount() {
			return map.size();
		}
	}

	private class GridModel {

		private int[] cellWidth;
		private int[] cellHeight;

		private Integer[] minCellWidths;
		private Integer[] minCellHeights;

		private Integer[] maxCellWidths;
		private Integer[] maxCellHeights;

		private int minCellWidth = 0;
		private int minCellHeight = 0;

		private int maxCellWidth = 10000;
		private int maxCellHeight = 10000;

		int getFixedWidthSum() {
			int sum = 0;
			for (int x = 0; x < maxCellWidths.length; x++) {
				if (maxCellWidths[x] != null) {
					sum += maxCellWidths[x];
				}
			}
			return sum;
		}

		int getFixedHeightsSum() {
			int sum = 0;
			for (int y = 0; y > maxCellHeights.length; y++) {
				if (maxCellHeights[y] != null) {
					sum += maxCellHeights[y];
				}
			}
			return sum;
		}

		void setMinCellWidth(int x, int min) {
			if (this.minCellWidths == null) {
				this.minCellWidths = new Integer[x + 1];
			}
			if (this.minCellWidths.length <= x) {
				this.minCellWidths = Arrays.copyOf(this.minCellWidths, x + 5);
			}
			this.minCellWidths[x] = min;
		}

		void setMaxCellWidth(int x, int min) {
			if (this.maxCellWidths == null) {
				this.maxCellWidths = new Integer[x + 1];
			}
			if (this.maxCellWidths.length <= x) {
				this.maxCellWidths = Arrays.copyOf(this.maxCellWidths, x + 5);
			}
			this.maxCellWidths[x] = min;
		}

		void setMinCellHeight(int y, int min) {
			if (this.minCellHeights == null) {
				this.minCellHeights = new Integer[y + 1];
			}
			if (this.minCellHeights.length <= y) {
				this.minCellHeights = Arrays.copyOf(this.minCellHeights, y + 5);
			}
			this.minCellHeights[y] = min;
		}

		void setMaxCellHeight(int y, int min) {
			if (maxCellHeights == null) {
				maxCellHeights = new Integer[y + 1];
			}
			if (maxCellHeights.length <= y) {
				maxCellHeights = Arrays.copyOf(this.maxCellHeights, y + 5);
			}
			maxCellHeights[y] = min;
		}

		void setGridHeight(int size) {
			this.cellHeight = new int[size];
			if (this.minCellHeights == null) {
				this.minCellHeights = new Integer[size];
			} else {
				this.minCellHeights = Arrays.copyOf(this.minCellHeights, size);
			}
			if (this.maxCellHeights == null) {
				this.maxCellHeights = new Integer[size];
			} else {
				this.maxCellHeights = Arrays.copyOf(this.minCellHeights, size);
			}
		}

		void setGridWidth(int size) {
			this.cellWidth = new int[size];
			if (this.minCellWidths == null) {
				this.minCellWidths = new Integer[size];
			} else {
				this.minCellWidths = Arrays.copyOf(this.minCellWidths, size);
			}
			if (this.maxCellWidths == null) {
				this.maxCellWidths = new Integer[size];
			} else {
				this.maxCellWidths = Arrays.copyOf(this.maxCellWidths, size);
			}
		}

		private int getMaxCellWidth(int index) {
			if (this.maxCellWidths == null || this.maxCellWidths[index] == null) {
				return this.maxCellWidth;
			}
			return this.maxCellWidths[index];
		}

		private int getMaxCellHeight(int index) {
			if (this.maxCellHeights == null || this.maxCellHeights[index] == null) {
				return this.maxCellHeight;
			}
			return this.maxCellHeights[index];
		}

		private int getMinCellWidth(int index) {
			if (this.minCellWidths == null || this.minCellWidths[index] == null) {
				return this.minCellWidth;
			}
			return this.minCellWidths[index];
		}

		private int getMinCellHeight(int index) {
			if (this.minCellHeights == null || this.minCellHeights[index] == null) {
				return minCellHeight;
			}
			return this.minCellHeights[index];
		}

		Dimension getCellSize(int x, int y) {
			int width = Math.min(getMaxCellWidth(x), Math.max(getMinCellWidth(x), this.cellWidth[x]));
			int height = Math.min(getMaxCellHeight(y), Math.max(getMinCellHeight(y), this.cellHeight[y]));
			return new Dimension(width, height);
		}

		void setCellSize(int x, int y, int width, int height) {
			if (x >= this.cellWidth.length) {
				this.cellWidth = Arrays.copyOf(this.cellWidth, x + 5);
			}
			if (y >= this.cellHeight.length) {
				this.cellHeight = Arrays.copyOf(this.cellHeight, y + 5);
			}
			this.cellWidth[x] = Math.max(this.cellWidth[x], width);
			this.cellHeight[y] = Math.max(this.cellHeight[y], height);
		}
	}
}
