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

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

import com.smartg.swing.layout.JNodeLayout;
import com.smartg.swing.layout.LayoutNode;
import com.smartg.swing.layout.NodeAlignment;
import com.smartg.swing.layout.NodeUtils.GridHelper;

public class JNL_Grid_Demo2 {

    public static void main(String[] args) {

	String rootName = "root";
	LayoutNode.GridNode root = new LayoutNode.GridNode(rootName);
	root.setHgap(10);
	root.setVgap(10);

	JNodeLayout layout = new JNodeLayout(root);

	JPanel panel = new JPanel();

	panel.setLayout(layout);

	GridHelper gridHelper = new GridHelper(panel, rootName, 11);

	ArrayList<JComponent> horizontalButtons = createButtons(panel, layout, rootName, true);
	ArrayList<JComponent> verticalButtons = createButtons(panel, layout, rootName, false);

	int rows = 10;
	int cols = 10;
	for (int r = 0; r < rows; r++) {
	    for (int c = 0; c < cols; c++) {
		int anInt = (int) Math.pow(r, c);
		JTextField textField = new JTextField(Integer.toString(anInt));
		gridHelper.add(textField);
	    }
	    gridHelper.add(verticalButtons.get(r));
	}

	for (JComponent b : horizontalButtons) {
	    gridHelper.add(b);
	}

	JButton closeButton = new JButton("Close");
	closeButton.addActionListener(new ActionListener() {

	    public void actionPerformed(ActionEvent e) {
		System.exit(0);
	    }
	});

	layout.setVerticalAlignment("root", NodeAlignment.TOP);
	layout.setHorizontalAlignment("root", NodeAlignment.LEFT);

	gridHelper.add(closeButton);

	JFrame frame = new JFrame("Grid demo");
	frame.add(panel);
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.pack();
	frame.setVisible(true);
    }

    static ArrayList<JComponent> createButtons(Container target, JNodeLayout layout, String nodename, boolean horizontal) {
	ArrayList<JComponent> buttons = new ArrayList<JComponent>();
	ButtonGroup bg = new ButtonGroup();
	boolean first = true;
	for (NodeAlignment a : NodeAlignment.values()) {
	    AlignmentAction action = new AlignmentAction(target, a, layout, nodename, horizontal);

	    JToggleButton b = new JToggleButton(action);

	    bg.add(b);
	    buttons.add(b);

	    if (first && action.isEnabled()) {
		b.setSelected(true);
		first = false;
	    }
	}
	for (int i = 0; i < 4; i++) {
	    buttons.add(new JLabel());
	}
	return buttons;
    }

    static class AlignmentAction extends AbstractAction {
	private static final long serialVersionUID = -2728326385982438176L;

	private NodeAlignment alignment;
	private JNodeLayout layout;
	private String nodename;
	private boolean horizontal;
	private Container target;

	public AlignmentAction(Container target, NodeAlignment alignment, JNodeLayout layout, String nodename, boolean horizontal) {
	    super(alignment.toString());
	    this.alignment = alignment;
	    this.layout = layout;
	    this.nodename = nodename;
	    this.horizontal = horizontal;
	    this.target = target;

	    if (horizontal) {
		if (!alignment.isHorizontal()) {
		    setEnabled(false);
		}
	    } else {
		if (!alignment.isVertical()) {
		    setEnabled(false);
		}
	    }
	}

	public void actionPerformed(ActionEvent e) {
	    if (horizontal) {
		if (alignment.isHorizontal()) {
		    layout.setHorizontalAlignment(nodename, alignment);
		    layout.layoutContainer(target);
		}
	    } else {
		if (alignment.isVertical()) {
		    layout.setVerticalAlignment(nodename, alignment);
		    layout.layoutContainer(target);
		}
	    }
	}
    }

}
