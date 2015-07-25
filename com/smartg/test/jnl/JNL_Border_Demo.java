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

import java.awt.Rectangle;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.smartg.swing.layout.JNodeLayout;
import com.smartg.swing.layout.LayoutNode;
import com.smartg.swing.layout.NodeAlignment;
import com.smartg.swing.layout.NodeConstraints;

public class JNL_Border_Demo {

    public static void main(String[] args) {
	LayoutNode.GridNode root = new LayoutNode.GridNode("root");

	root.setHgap(5);
	root.setVgap(5);
	
	JPanel target = new JPanel();
	JNodeLayout layout = new JNodeLayout(target, root);

	target.setLayout(layout);
	String rootName = "root";

	createLabel1(target, rootName);
	createTextField(layout, target, rootName);
	createLabel2(layout, target, rootName);
	createTextArea(target, rootName);
	createCornerButton(layout, target, rootName);
	createRightButton(layout, target, rootName);
	createRightColumn(layout, target, rootName);
	createBottomRow(layout, target, rootName);

	JFrame frame = new JFrame("Besser als BorderLayout...");
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.getContentPane().add(target);
	frame.pack();
	frame.setVisible(true);
    }

    public static void createTextField(JNodeLayout slm, JPanel target, String rootName) {
	JComponent text = createTextField("SampleText...");
	target.add(text, new NodeConstraints(rootName, new Rectangle(1, 0, 5, 1)));
	slm.setHorizontalAlignment(text, NodeAlignment.STRETCHED);
	slm.setVerticalAlignment(text, NodeAlignment.BOTTOM);
    }

    public static void createLabel1(JPanel target, String rootName) {
	JComponent label1 = createLabel("Label 1");
	target.add(label1, new NodeConstraints(rootName, new Rectangle(0, 0, 1, 1)));
    }

    public static void createRightButton(JNodeLayout slm, JPanel target, String rootName) {
	JComponent rightButton = createButton("RightButton");
	target.add(rightButton, new NodeConstraints(rootName, new Rectangle(5, 4, 1, 1)));
	slm.setHorizontalAlignment(rightButton, NodeAlignment.LEFT);
	slm.setVerticalAlignment(rightButton, NodeAlignment.TOP);
    }

    public static void createCornerButton(JNodeLayout slm, JPanel target, String rootName) {
	JComponent cornerButton = createButton("CornerButton");
	target.add(cornerButton, new NodeConstraints(rootName, new Rectangle(0, 4, 1, 1)));
	slm.setHorizontalAlignment(cornerButton, NodeAlignment.RIGHT);
	slm.setVerticalAlignment(cornerButton, NodeAlignment.TOP);
    }

    public static void createTextArea(JPanel root, String rootName) {
	JComponent textArea22 = createTextArea("SampleText...\r\n" + "SampleText...\r\n" + "SampleText...\r\n" + "SampleText...\r\n" + "SampleText...\r\n"
		+ "SampleText...\r\n" + "SampleText...\r\n" + "SampleText...\r\n" + "SampleText...\r\n" + "SampleText...\r\n" + "SampleText...\r\n"
		+ "SampleText...\r\n" + "SampleText...\r\n" + "SampleText...\r\n" + "SampleText...\r\n" + "SampleText...\r\n" + "SampleText...\r\n"
		+ "SampleText...\r\n" + "SampleText...\r\n" + "SampleText...\r\n" + "SampleText...\r\n" + "SampleText...\r\n" + "SampleText...\r\n"
		+ "SampleText...\r\n" + "SampleText...\r\n" + "SampleText...\r\n" + "SampleText...\r\n" + "SampleText...\r\n" + "SampleText...\r\n"
		+ "SampleText...\r\n" + "SampleText...\r\n" + "SampleText...\r\n" + "SampleText...\r\n" + "SampleText.");

	root.add(new JScrollPane(textArea22), new NodeConstraints(rootName, new Rectangle(1, 1, 4, 3)));
    }

    public static void createLabel2(JNodeLayout slm, JPanel root, String rootName) {
	JComponent label2 = createLabel("Label 2");
	root.add(label2, new NodeConstraints(rootName, new Rectangle(0, 1, 1, 3)));
	slm.setVerticalAlignment(label2, NodeAlignment.TOP);
	slm.setHorizontalAlignment(label2, NodeAlignment.STRETCHED);
    }

    public static void createBottomRow(JNodeLayout layout, JPanel target, String rootName) {
	String column4 = "col4";

	LayoutNode.VerticalNode verticalNode = new LayoutNode.VerticalNode(column4);
	layout.addLayoutNode(verticalNode, rootName, new Rectangle(5, 1, 1, 3));

	JComponent button01 = createButton("First");
	JComponent button02 = createButton("Second");
	JComponent button03 = createButton("Third");
	JComponent button04 = createButton("Forth");
	JComponent button05 = createButton("Fifth");

	target.add(button01, new NodeConstraints(column4));
	target.add(button02, new NodeConstraints(column4));
	target.add(button03, new NodeConstraints(column4));
	target.add(button04, new NodeConstraints(column4));
	target.add(button05, new NodeConstraints(column4));
		

	layout.setHorizontalAlignment(column4, NodeAlignment.STRETCHED);
	layout.setVerticalAlignment(column4, NodeAlignment.TOP);
    }

    public static void createRightColumn(JNodeLayout slm, JPanel target, String rootName) {
	String row4 = "row4";

	slm.addLayoutNode(new LayoutNode.HorizontalNode(row4), rootName, new Rectangle(1, 4, 4, 1));

	JComponent button1 = createButton("First");
	JComponent button2 = createButton("Second");
	JComponent button3 = createButton("Third");
	JComponent button4 = createButton("Forth");
	JComponent button5 = createButton("Fifth");

	target.add(button1, new NodeConstraints(row4));
	target.add(button2, new NodeConstraints(row4));
	target.add(button3, new NodeConstraints(row4));
	target.add(button4, new NodeConstraints(row4));
	target.add(button5, new NodeConstraints(row4));

	slm.setHorizontalAlignment(row4, NodeAlignment.CENTER);
	slm.setVerticalAlignment(row4, NodeAlignment.CENTER);
    }

    public static JLabel createLabel(String text) {
	JLabel jLabel = new JLabel(text, SwingConstants.RIGHT);
	return jLabel;
    }

    public static JButton createButton(String text) {
	JButton jLabel = new JButton(text);
	return jLabel;
    }

    public static JComponent createTextField(String text) {
	JTextField jLabel = new JTextField(text);
	return jLabel;
    }

    public static JComponent createTextArea(String text) {
	JTextArea jLabel = new JTextArea(10, 20);
	jLabel.setText(text);
	return jLabel;
    }

}
