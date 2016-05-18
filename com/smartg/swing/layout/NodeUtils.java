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
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JLabel;

import com.smartg.swing.layout.LayoutNode.GridNode;

public class NodeUtils {

	public static interface GridLine extends Iterable<Object> {
		int getLineWidth();

		int getWidthFor(Component c);

		int getWidthFor(LayoutNode node);
		
		public void setVisible(boolean b);
		
		public void setEnabled(boolean b);
	}

	public static class DefaultGridLine implements GridLine {
		private Map<Object, Integer> map = new LinkedHashMap<Object, Integer>();
		private int lineWidth;

		public Iterator<Object> iterator() {
			return map.keySet().iterator();
		}

		public int getWidthFor(Component c) {
			Integer n = map.get(c);
			if (n != null) {
				return n;
			}
			return 0;
		}

		public int getWidthFor(LayoutNode c) {
			Integer n = map.get(c);
			if (n != null) {
				return n;
			}
			return 0;
		}

		public void add(Component c, int width) {
			lineWidth += width;
			map.put(c, width);
		}

		public void add(LayoutNode c, int width) {
			lineWidth += width;
			map.put(c, width);
		}
		
		public void clear() {
			map.clear();
		}

		public int getLineWidth() {
			return lineWidth;
		}
		
		public void setVisible(boolean b) {
			for(Object obj: this) {
				if(obj instanceof Component) {
					((Component)obj).setVisible(b);
				}
				else {
					LayoutNode node = (LayoutNode) obj;
					Iterator<Component> components = node.components();
					while(components.hasNext()) {
						components.next().setVisible(b);
					}
				}
			}
		}

		public void setEnabled(boolean b) {
			for(Object obj: this) {
				if(obj instanceof Component) {
					((Component)obj).setEnabled(b);
				}
				else {
					LayoutNode node = (LayoutNode) obj;
					Iterator<Component> components = node.components();
					while(components.hasNext()) {
						components.next().setEnabled(b);
					}
				}
			}
		}		
	}

	public static class GridHelper {
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
}
