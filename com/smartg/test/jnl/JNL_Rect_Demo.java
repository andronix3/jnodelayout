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

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.smartg.swing.layout.JNodeLayout;
import com.smartg.swing.layout.LayoutNode;
import com.smartg.swing.layout.NodeAlignment;
import com.smartg.swing.layout.NodeConstraints;

public class JNL_Rect_Demo {

    public static void main(String[] args) {

	String rootName = "root";

	JPanel target = new JPanel();
	JNodeLayout layout = new JNodeLayout(target, new LayoutNode.RectNode(rootName));

	target.setLayout(layout);

	LayoutNode bottom = new LayoutNode.HorizontalNode("bottom");
	bottom.setVerticalAlignment(NodeAlignment.TOP);
	layout.addLayoutNode(bottom, rootName, new Rectangle2D.Double(0.2, 0.8, 0.6, 0.2));

	LayoutNode top = new LayoutNode.HorizontalNode("top");
	top.setVerticalAlignment(NodeAlignment.BOTTOM);
	layout.addLayoutNode(top, rootName, new Rectangle2D.Double(0.2, 0.0, 0.6, 0.2));

	LayoutNode right = new LayoutNode.VerticalNode("right");
	right.setHorizontalAlignment(NodeAlignment.LEFT);
	layout.addLayoutNode(right, rootName, new Rectangle2D.Double(0.8, 0.2, 0.2, 0.6));

	LayoutNode left = new LayoutNode.VerticalNode("left");
	left.setHorizontalAlignment(NodeAlignment.RIGHT);
	layout.addLayoutNode(left, rootName, new Rectangle2D.Double(0.0, 0.2, 0.2, 0.6));

	LayoutNode center = new LayoutNode.RectNode("center");
	layout.addLayoutNode(center, rootName, new Rectangle2D.Double(0.2, 0.2, 0.6, 0.6));

	createAndPutComponents(target, "RIGHT", 5);
	createAndPutComponents(target, "TOP", 5);
	createAndPutComponents(target, "LEFT", 5);
	createAndPutComponents(target, "BOTTOM", 5);

	target.add(new JButton("CENTER"), new NodeConstraints("center"));

	JFrame frame = new JFrame("Besser als BorderLayout...");
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.getContentPane().add(target);
	frame.pack();
	frame.setVisible(true);
    }

    public static void createAndPutComponents(JPanel target, String name, int count) {
	ArrayList<JComponent> rightButtons = createList(name, count);
	NodeConstraints constraints = new NodeConstraints(name.toLowerCase());
	for (JComponent c : rightButtons) {
	    target.add(c, constraints);
	}
    }

    public static ArrayList<JComponent> createList(String name, int count) {
	ArrayList<JComponent> topList = new ArrayList<JComponent>();
	for (int i = 0; i < count; i++) {
	    topList.add(new JButton(name + " " + i));
	}
	return topList;
    }

}
