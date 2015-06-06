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
package com.smartg.test.jnl;

import java.awt.Frame;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.smartg.swing.layout.LayoutNode;
import com.smartg.swing.layout.JNodeLayout;
import com.smartg.swing.layout.NodeAlignment;

public class JNL_NestedDemo {

    public static void main(String[] args) {

	JPanel target = new JPanel();

	Logger.getGlobal().setLevel(Level.WARNING);

	LayoutNode root = new LayoutNode.RectNode("root");
	JNodeLayout layout = new JNodeLayout(root);
	target.setLayout(layout);

	root.setHgap(10);
	root.setVgap(10);

	LayoutNode center = root;
	for (int i = 0; i < 8; i++) {
	    HashMap<String, ArrayList<JComponent>> map1 = createAndAddComponents(target, i);
	    center = create(center, map1, false, i);
	}

	JButton button = new JButton("Center");
	target.add(button);
	center.add(button, null);// RectNode accepts null constraints

	layout.syncNodes();
	layout.verify();

	JFrame frame = new JFrame("Besser als BorderLayout...");
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.getContentPane().add(target);
	frame.pack();
	frame.setVisible(true);
	frame.setExtendedState(Frame.MAXIMIZED_BOTH);
    }

    public static HashMap<String, ArrayList<JComponent>> createAndAddComponents(JPanel target, int stufe) {
	ArrayList<JComponent> topList = createList("TOP ", 5, stufe);
	ArrayList<JComponent> bottomList = createList("BOTTOM ", 5, stufe);
	ArrayList<JComponent> leftList = createList("LEFT ", 5, stufe);
	ArrayList<JComponent> rightList = createList("RIGHT ", 5, stufe);

	ArrayList<JComponent> bottomRightList = createList("BOTTOM_RIGHT ", 1, stufe);
	ArrayList<JComponent> topRightList = createList("TOP_RIGHT ", 1, stufe);
	ArrayList<JComponent> bottomLeftList = createList("BOTTOM_LEFT ", 1, stufe);
	ArrayList<JComponent> topLeftList = createList("TOP_LEFT ", 1, stufe);

	HashMap<String, ArrayList<JComponent>> map = new HashMap<String, ArrayList<JComponent>>();
	map.put("top", topList);
	map.put("bottom", bottomList);
	map.put("left", leftList);
	map.put("right", rightList);
	map.put("bottomRight", bottomRightList);
	map.put("bottomLeft", bottomLeftList);
	map.put("topLeft", topLeftList);
	map.put("topRight", topRightList);

	for (ArrayList<JComponent> list : map.values()) {
	    for (JComponent c : list) {
		target.add(c);
	    }
	}
	return map;
    }

    public static ArrayList<JComponent> createList(String name, int count, int stufe) {
	ArrayList<JComponent> topList = new ArrayList<JComponent>();
	for (int i = 0; i < count; i++) {
	    topList.add(new JButton(name + "#" + stufe + ":" + i));
	}
	return topList;
    }

    public static LayoutNode create(LayoutNode root, HashMap<String, ArrayList<JComponent>> map, boolean changeAlignment, int stufe) {
	LayoutNode bottom = new LayoutNode.HorizontalNode("bottom" + stufe);
	root.add(bottom, new Rectangle2D.Double(0.1, 0.9, 0.8, 0.1));

	LayoutNode top = new LayoutNode.HorizontalNode("top" + stufe);
	root.add(top, new Rectangle2D.Double(0.1, 0.0, 0.8, 0.1));

	LayoutNode right = new LayoutNode.VerticalNode("right" + stufe);
	root.add(right, new Rectangle2D.Double(0.9, 0.1, 0.1, 0.8));

	LayoutNode left = new LayoutNode.VerticalNode("left" + stufe);
	root.add(left, new Rectangle2D.Double(0.0, 0.1, 0.1, 0.8));

	LayoutNode center = new LayoutNode.RectNode("center" + stufe);
	root.add(center, new Rectangle2D.Double(0.1, 0.1, 0.8, 0.8));

	LayoutNode bottomRight = new LayoutNode.VerticalNode("bottomRight" + stufe);
	root.add(bottomRight, new Rectangle2D.Double(0.9, 0.9, 0.1, 0.1));

	LayoutNode bottomLeft = new LayoutNode.VerticalNode("bottomLeft" + stufe);
	root.add(bottomLeft, new Rectangle2D.Double(0.0, 0.9, 0.1, 0.1));

	LayoutNode topRight = new LayoutNode.VerticalNode("topRight" + stufe);
	root.add(topRight, new Rectangle2D.Double(0.9, 0.0, 0.1, 0.1));

	LayoutNode topLeft = new LayoutNode.VerticalNode("topLeft" + stufe);
	root.add(topLeft, new Rectangle2D.Double(0.0, 0.0, 0.1, 0.1));

	if (changeAlignment) {
	    bottom.setVerticalAlignment(NodeAlignment.TOP);
	    right.setHorizontalAlignment(NodeAlignment.LEFT);
	    top.setVerticalAlignment(NodeAlignment.BOTTOM);
	    left.setHorizontalAlignment(NodeAlignment.RIGHT);
	    bottomRight.setHorizontalAlignment(NodeAlignment.LEFT);
	    bottomRight.setVerticalAlignment(NodeAlignment.TOP);
	    bottomLeft.setHorizontalAlignment(NodeAlignment.RIGHT);
	    bottomLeft.setVerticalAlignment(NodeAlignment.TOP);
	    topRight.setHorizontalAlignment(NodeAlignment.LEFT);
	    topRight.setVerticalAlignment(NodeAlignment.BOTTOM);
	    topLeft.setHorizontalAlignment(NodeAlignment.RIGHT);
	    topLeft.setVerticalAlignment(NodeAlignment.BOTTOM);
	}
	add(map, top, "top");
	add(map, right, "right");
	add(map, left, "left");
	add(map, bottom, "bottom");

	add(map, topLeft, "topLeft");
	add(map, topRight, "topRight");
	add(map, bottomLeft, "bottomLeft");
	add(map, bottomRight, "bottomRight");

	return center;
    }

    public static void add(HashMap<String, ArrayList<JComponent>> map, LayoutNode top, String key) {
	ArrayList<JComponent> topList = map.get(key);
	for (JComponent c : topList) {
	    top.add(c, null);
	}
    }
}
