package com.smartg.test.jnl;

import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.Vector;

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
	panel.add(new JButton("..."), topLine);
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

    static JSpinner createSpinner() {
	String[] strings = { "Zoom" };
	SpinnerListModel model = new SpinnerListModel(strings);
	JSpinner spinner = new JSpinner(model);
	return spinner;
    }

}
