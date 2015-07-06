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

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

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
    protected ArrayList<LayoutNode> invalidNodes = new ArrayList<LayoutNode>();
    private Integer hgap, vgap;

    private Container target;

    public LayoutNode(String name) {
	this.name = name;
    }

    public abstract Dimension preferredSize();

    public void add(LayoutNode layout) {
	add(layout, null);
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

    public LeafNode add(Component comp) {
	return add(comp, null);
    }

    public LeafNode add(Component comp, Object constraints) {
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
	    ((LayoutNode)this).parent = p;
	    if(getTarget() != null) {
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

		return preferredSize;
	    }
	    return new Dimension();
	}

	public Iterator<LayoutNode> iterator() {
	    return new EmptyIterator<LayoutNode>();
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

	    int hgap = getHgap();
	    int vgap = getVgap();

	    if (horizontalAlignment == NodeAlignment.STRETCHED && verticalAlignment == NodeAlignment.STRETCHED) {
		component.setBounds(new Rectangle(dest.x + hgap, dest.y + vgap, dest.width - hgap, dest.height - vgap));
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

		component.setBounds(new Rectangle(x, y, (int) (width - hgap), (int) (height - vgap)));
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
    }

    public static class VerticalNode extends LayoutNode {

	private ArrayList<LayoutNode> list = new ArrayList<LayoutNode>();

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
	    return new Dimension(width, height);
	}

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

	    Dimension ps = preferredSize();
	    int width;
	    if (horizontalAlignment == NodeAlignment.STRETCHED) {
		width = dest.width;
	    } else {
		width = Math.min(dest.width, ps.width);
	    }

	    double m = 1;

	    if (dest.height < ps.height || verticalAlignment == NodeAlignment.STRETCHED) {
		m = dest.getHeight() / ps.getHeight();
	    }

	    int dx = adjustX(dest.width, ps.width);
	    int dy = adjustY(dest.height, ps.height);

	    int x = dest.x + dx;
	    int y = dest.y + dy;

	    for (LayoutNode c : this) {
		Dimension d = c.preferredSize();
		double height = d.height * m;
		c.layout(new Rectangle(x, y, width, (int) height));
		y += height;
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

	private ArrayList<LayoutNode> list = new ArrayList<LayoutNode>();

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
	    return new Dimension(width, height);
	}

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

	    int dx = adjustX(dest.height, ps.height);
	    int dy = adjustY(dest.width, ps.width);

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
	public void remove(LayoutNode layout) {
	    list.remove(layout);
	}

	@Override
	public int getCount() {
	    return list.size();
	}
    }

    public static class RectNode extends LayoutNode {

	private HashMap<LayoutNode, Rectangle2D> map = new HashMap<LayoutNode, Rectangle2D>();

	public RectNode(String name) {
	    super(name);
	}

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

	    for (LayoutNode n : this) {
		Rectangle2D r = map.get(n);
		int width = dest.width;
		int height = dest.height;

		int x = (int) (width * r.getX());
		int y = (int) (height * r.getY());
		int w = (int) (width * r.getWidth());
		int h = (int) (height * r.getHeight());

		n.layout(new Rectangle(dest.x + x, dest.y + y, w, h));
	    }
	}

	@Override
	public int getCount() {
	    return map.size();
	}
    }

    public static class GridNode extends LayoutNode {

	private GridModel gridModel = new GridModel();

	private HashMap<LayoutNode, Rectangle> map = new HashMap<LayoutNode, Rectangle>();

	public GridNode(String name) {
	    super(name);
	}

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
	public void add(LayoutNode layout, Object constraints) {
	    if (constraints == null) {
		throw new NullPointerException();
	    }
	    Rectangle r = (Rectangle) constraints;
	    map.put(layout, r);
	    layout.parent = this;
	}

	@Override
	public void layout(Rectangle dest) {
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

	    for (LayoutNode gl : this) {
		Rectangle r = map.get(gl);
		int x = getXOffset(0, r.x) + dx;
		double width = getXOffset(r.x, r.x + r.width);
		int y = getYOffset(0, r.y) + dy;
		double height = getYOffset(r.y, r.y + r.height);

		Rectangle bounds = new Rectangle();
		bounds.setRect(dest.x + x * mx, dest.y + y * my, width * mx, height * my);
		gl.layout(bounds);
	    }
	}

	public int getXOffset(int from, int to) {
	    int mx = 0;
	    for (int x = from; x < to; x++) {
		mx += gridModel.getCellSize(x, 0).width;
	    }
	    return mx;
	}

	public int getYOffset(int from, int to) {
	    int my = 0;
	    for (int y = from; y < to; y++) {
		my += gridModel.getCellSize(0, y).height;
	    }
	    return my;
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

    private static class GridModel {
	private int[] cellWidth;
	private int[] cellHeight;

	void setGridWidth(int gridWidth) {
	    this.cellHeight = new int[gridWidth];
	}

	void setGridHeight(int gridHeight) {
	    this.cellWidth = new int[gridHeight];
	}

	Dimension getCellSize(int x, int y) {
	    return new Dimension(cellWidth[x], cellHeight[y]);
	}

	void setCellSize(int x, int y, int width, int height) {
	    if (x >= cellWidth.length) {
		cellWidth = Arrays.copyOf(cellWidth, x + 5);
	    }
	    if (y >= cellHeight.length) {
		cellHeight = Arrays.copyOf(cellHeight, y + 5);
	    }
	    cellWidth[x] = Math.max(cellWidth[x], width);
	    cellHeight[y] = Math.max(cellHeight[y], height);
	}
    }
}
