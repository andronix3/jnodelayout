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
import java.awt.Rectangle;

public class NodeUtils {

    public static class GridHelper {
	private Container parent;
	private String gridName;

	private int width;

	private int x, y;

	public GridHelper(Container parent, String gridName, int width) {
	    this.parent = parent;
	    this.gridName = gridName;
	    this.width = width;
	}

	public void add(Component comp) {
	    if (x >= width) {
		x = 0;
		y++;
	    }
	    Rectangle r = new Rectangle(x++, y, 1, 1);
	    parent.add(comp, new NodeConstraints(gridName, r));
	}

	public void add(Component comp, int w) {
	    if (x >= width || (x + w) >= width) {
		x = 0;
		y++;
	    }
	    Rectangle r = new Rectangle(x, y, w, 1);
	    parent.add(comp, new NodeConstraints(gridName, r));
	    x += w;
	}

    }
}
