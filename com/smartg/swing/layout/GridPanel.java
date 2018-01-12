package com.smartg.swing.layout;

import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.JPanel;

import com.smartg.swing.layout.LayoutNode.GridNode;

public class GridPanel extends JPanel {

    private static final long serialVersionUID = 3961732863340737992L;

    private final LayoutNode root = new GridNode("root");
    private final JNodeLayout layout = new JNodeLayout(this, root);
    private final GridHelper helper;

    public GridPanel(int gridWidth) {
        setLayout(layout);
        this.helper = new GridHelper(this, "root", gridWidth);
    }

    @Override
    public final JNodeLayout getLayout() {
        return layout;
    }

    @Override
    public final void setLayout(LayoutManager m) {
        if (m instanceof JNodeLayout) {
            super.setLayout(m);
        }
    }

    public final void skip(int w) {
        helper.skip(w);
    }

    public final void skipLine() {
        helper.skipLine();
    }

    public final void skipToNextLine() {
        helper.skipToNextLine();
    }

    public final void add(JComponent c) {
        helper.add(c);
    }

    public final void add(JComponent c, int width) {
        helper.add(c, width);
    }

    public JComponent getComponent(String name) {
        for (Component c : getComponents()) {
            if (c instanceof JComponent) {
                JComponent jc = (JComponent) c;
                String cp = "" + jc.getClientProperty("GridPanel#Name");
                if (cp.equals(name)) {
                    return jc;
                }
            }
        }
        return null;
    }

    public <T extends JComponent> T getComponent(String name, Class<T> classe) {
        for (Component c : getComponents()) {
            if (c instanceof JComponent) {
                JComponent jc = (JComponent) c;
                String cp = "" + jc.getClientProperty("GridPanel#Name");
                if (cp.equals(name)) {
                    return classe.cast(jc);
                }
            }
        }
        return null;
    }

    public final void add(String name, JComponent c, int width) {
        c.putClientProperty("GridPanel#Name", name);
        helper.add(c, width);
    }

    public final void add(String name, JComponent c, Rectangle r) {
        c.putClientProperty("GridPanel#Name", name);
        helper.add(c, r);
    }

    public final void add(JComponent c, Rectangle r) {
        helper.add(c, r);
    }

    public final void add(GridLine line) {
        helper.add(line);
    }

    public final void add(LayoutNode node) {
        helper.add(node);
    }

    public final void add(LayoutNode node, int width) {
        helper.add(node, width);
    }

    public final void add(LayoutNode node, Rectangle r) {
        helper.add(node, r);
    }

    public final int getGridX() {
        return helper.getX();
    }

    public final int getGridY() {
        return helper.getY();
    }

    public final int getGridWidth() {
        return helper.getWidth();
    }

    public final void setHgap(int hgap) {
        root.setHgap(hgap);
    }

    public final void setVgap(int vgap) {
        root.setVgap(vgap);
    }

    public final void setHorizontalAlignment(NodeAlignment a) {
        root.setHorizontalAlignment(a);
    }

    public final void setVerticalAlignment(NodeAlignment a) {
        root.setVerticalAlignment(a);
    }

}
