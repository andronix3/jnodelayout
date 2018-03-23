package com.smartg.swing.layout;

import java.awt.Component;
import java.awt.Container;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JLabel;

import com.smartg.swing.layout.LayoutNode.GridNode;

public class GridHelper {

    public static int FILL = -1;

    private final Container parent;
    private final String gridName;
    private JNodeLayout nodeLayout;

    private int width;

    private int x, y;

    boolean initDone;

    public GridHelper(Container parent, String gridName, int width) {
        this.parent = parent;
        this.gridName = gridName;
        this.width = width;
        softInit();
    }

    private void softInit() {
        try {
            init();
        } catch (IllegalArgumentException ex) {
            // do nothing
        }
    }

    private void init() {
        LayoutManager layout = parent.getLayout();
        if (!(layout instanceof JNodeLayout)) {
            throw new IllegalArgumentException();
        }
        nodeLayout = (JNodeLayout) layout;
        nodeLayout.syncNodes();
        LayoutNode node = nodeLayout.getNode(gridName);
        if (!(node instanceof GridNode)) {
            throw new IllegalArgumentException();
        }
        GridNode gridNode = (GridNode) node;
        Rectangle gridBounds = gridNode.getGridBounds();
        y = gridBounds.height + gridBounds.y;
        if (width == 0) {
            width = gridBounds.width;
        }
        initDone = true;
    }

    public void add(Component comp) {
        if (!initDone) {
            init();
        }
        if (x >= width) {
            x = 0;
            y++;
        }
        Rectangle r = new Rectangle(x++, y, 1, 1);
        parent.add(comp, new NodeConstraints(gridName, r));
    }

    public void add(LayoutNode node) {
        if (!initDone) {
            init();
        }
        if (x >= width) {
            x = 0;
            y++;
        }
        Rectangle r = new Rectangle(x++, y, 1, 1);
        nodeLayout.addLayoutNode(node, gridName, r);
    }

    public void add(Component comp, int w) {
        if (!initDone) {
            init();
        }
        if (w < 0) {
            w = width - x;
        }

        if (x >= width || (x + w) > width) {
            x = 0;
            y++;
        }
        Rectangle r = new Rectangle(x, y, w, 1);
        parent.add(comp, new NodeConstraints(gridName, r));
        x += w;
    }

    public void add(GridLine line) {
        if (!initDone) {
            init();
        }
        if (x != 0) {
            skipToNextLine();
        }
        for (Object obj : line) {
            if (obj instanceof Component) {
                Component comp = (Component) obj;
                int w = line.getWidthFor(comp);
                Rectangle r = new Rectangle(x, y, w, 1);
                parent.add(comp, new NodeConstraints(gridName, r));
                x += w;
            } else if (obj instanceof LayoutNode) {
                LayoutNode node = (LayoutNode) obj;
                int w = line.getWidthFor(node);
                Rectangle r = new Rectangle(x, y, w, 1);
                nodeLayout.addLayoutNode(node, gridName, r);
                x += w;
            } else {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE,
                        "Unexpected Object Type: " + obj.getClass().getName());
            }
        }
        x = 0;
        y++;
    }

    public void add(LayoutNode node, int w) {
        if (!initDone) {
            init();
        }
        if (w < 0) {
            w = width - x;
        }

        if (x >= width || (x + w) > width) {
            x = 0;
            y++;
        }
        Rectangle r = new Rectangle(x, y, w, 1);
        nodeLayout.addLayoutNode(node, gridName, r);
        x += w;
    }

    public void add(Component comp, Rectangle r) {
        parent.add(comp, new NodeConstraints(gridName, r));
    }

    public void add(LayoutNode node, Rectangle r) {
        nodeLayout.addLayoutNode(node, gridName, r);
    }

    public void skip(int w) {
        if (!initDone) {
            init();
        }
        if (x >= width || (x + w) > width) {
            x = 0;
            y++;
        }
        x += w;
    }

    public void skipToNextLine() {
        if (!initDone) {
            init();
        }
        x = 0;
        y++;
    }

    public void skipLine() {
        if (x != 0) {
            skipToNextLine();
        }
        add(new JLabel(), width);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

}
