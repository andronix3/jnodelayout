package com.smartg.swing.layout;

import java.awt.Component;

public interface GridLine extends Iterable<Object> {
	int getLineWidth();

	int getWidthFor(Component c);

	int getWidthFor(LayoutNode node);
	
	public void setVisible(boolean b);
	
	public void setEnabled(boolean b);
}