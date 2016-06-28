package com.smartg.swing.layout;

import java.awt.Component;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class DefaultGridLine implements GridLine {
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