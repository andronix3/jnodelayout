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

import java.awt.Insets;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerListModel;

import com.smartg.swing.layout.JNodeLayout;
import com.smartg.swing.layout.LayoutNode;
import com.smartg.swing.layout.NodeAlignment;
import com.smartg.swing.layout.NodeConstraints;

public class DiskVievDemo {

    public static void main(String[] args) {
	String rootName = "root";
	LayoutNode.RectNode root = new LayoutNode.RectNode(rootName);
	JNodeLayout layout = new JNodeLayout(root);
	root.setHgap(10);
	root.setVgap(10);

	JPanel panel = new JPanel();

	panel.setLayout(layout);

	layout.addLayoutNode(new LayoutNode.HorizontalNode("topLine"), rootName, new Rectangle2D.Double(0, 0, 1, 0.05));
	layout.addLayoutNode(new LayoutNode.RectNode("panelNode1"), rootName, new Rectangle2D.Double(0, 0.05, 1, 0.65));
	layout.addLayoutNode(new LayoutNode.RectNode("panelNode2"), rootName, new Rectangle2D.Double(0, 0.7, 1, 0.25));
	layout.addLayoutNode(new LayoutNode.HorizontalNode("bottomLine"), rootName, new Rectangle2D.Double(0, 0.95, 0.5, 0.05));
	layout.addLayoutNode(new LayoutNode.HorizontalNode("bottomLineRight"), rootName, new Rectangle2D.Double(0.7, 0.95, 0.3, 0.05));

	layout.setVerticalAlignment("topLine", NodeAlignment.CENTER);

	layout.setVerticalAlignment("bottomLine", NodeAlignment.CENTER);
	layout.setHorizontalAlignment("bottomLine", NodeAlignment.LEFT);

	layout.setVerticalAlignment("bottomLineRight", NodeAlignment.CENTER);
	layout.setHorizontalAlignment("bottomLineRight", NodeAlignment.RIGHT);

	NodeConstraints topLine = new NodeConstraints("topLine");
	panel.add(new JLabel("Hightlight: "), topLine);
	panel.add(new JTextField(35), topLine);
	panel.add(new SlimButton("..."), topLine);
	panel.add(new JButton("ShowNext"), topLine);

	panel.add(new JScrollPane(new JPanel()), new NodeConstraints("panelNode1", null));
	panel.add(new JScrollPane(new JTextArea()), new NodeConstraints("panelNode2", null));

	NodeConstraints bottomLine = new NodeConstraints("bottomLine");

	panel.add(new JLabel("Volume: "), bottomLine);
	Vector<String> items = new Vector<String>(Arrays.asList(new String[] { "A:\\  " }));
	panel.add(new JComboBox<String>(items), bottomLine);
	panel.add(new JButton("Refresh"), bottomLine);

	panel.add(createSpinner(), bottomLine);

	NodeConstraints bottomLineRight = new NodeConstraints("bottomLineRight");
	panel.add(new JButton("Export"), bottomLineRight);
	panel.add(new JButton("Quit"), bottomLineRight);

	JMenuBar menubar = new JMenuBar();
	menubar.add(new JMenu("File"));
	menubar.add(new JMenu("Options"));
	menubar.add(new JMenu("Help"));

	JFrame frame = new JFrame("DisKView - Sysinternals: www.sysinternal.com");
	frame.setJMenuBar(menubar);
	frame.add(panel);
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.pack();
	frame.setVisible(true);

    }

    public static class SlimButton extends JButton {

	private static final long serialVersionUID = 6877552516359145151L;

	public SlimButton() {
	}

	public SlimButton(Icon icon) {
	    super(icon);
	}

	public SlimButton(String text) {
	    super(text);
	}

	public SlimButton(Action a) {
	    super(a);
	}

	public SlimButton(String text, Icon icon) {
	    super(text, icon);
	}

	public void setMargin(Insets m) {
	    super.setMargin(new Insets(m.top, 0, m.bottom, 0));
	}
    }

    static JSpinner createSpinner() {
	String[] strings = { "Zoom" };
	SpinnerListModel model = new SpinnerListModel(strings);
	JSpinner spinner = new JSpinner(model);
	return spinner;
    }

}
