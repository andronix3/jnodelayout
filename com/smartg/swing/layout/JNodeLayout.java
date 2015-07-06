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
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.smartg.swing.layout.LayoutNode.LeafNode;

/**
 * JNodeLayoutis a node-based LayoutManager.
 * 
 */
public class JNodeLayout implements LayoutManager2 {

    private HashMap<String, LayoutNode> map = new HashMap<String, LayoutNode>();
    private HashMap<Component, LayoutNode.LeafNode> byComponent = new HashMap<Component, LayoutNode.LeafNode>();

    private final LayoutNode root;

    public JNodeLayout(LayoutNode root) {
	this.root = root;
	putNode(root);
    }

    /**
     * If you use this constructor you may just add Components to layout nodes,
     * the Components will be added to target Container by LayoutManager, thus
     * making code shorter.
     * 
     * @param target
     * @param root
     */
    public JNodeLayout(Container target, LayoutNode root) {
	this.root = root;
	root.setTarget(target);
    }

    /**
     * Set horizontal alignment for node with given name
     * 
     * @param nodeName
     *            name of node
     * @param alignment
     */
    public void setHorizontalAlignment(String nodeName, NodeAlignment alignment) {
	LayoutNode gl = map.get(nodeName);
	if (gl != null) {
	    gl.setHorizontalAlignment(alignment);
	} else {
	    Logger.getGlobal().warning("LayoutNode " + nodeName + " not found.");
	}
    }

    /**
     * Set vertical alignment for node with given name
     * 
     * @param nodeName
     *            name of node
     * @param alignment
     */
    public void setVerticalAlignment(String nodeName, NodeAlignment alignment) {
	LayoutNode gl = map.get(nodeName);
	if (gl != null) {
	    gl.setVerticalAlignment(alignment);
	} else {
	    Logger.getGlobal().warning("LayoutNode " + nodeName + " not found.");
	}
    }

    /**
     * set horizontal alignment for leaf node which contains specified component
     * 
     * @param comp
     *            component
     * @param alignment
     */
    public void setHorizontalAlignment(Component comp, NodeAlignment alignment) {
	LayoutNode gl = byComponent.get(comp);
	if (gl != null) {
	    gl.setHorizontalAlignment(alignment);
	} else {
	    Logger.getGlobal().warning("LeafNode not found for " + comp);
	}
    }

    /**
     * set vertical alignment for leaf node which contains specified component
     * 
     * @param comp
     *            component
     * @param alignment
     */
    public void setVerticalAlignment(Component comp, NodeAlignment alignment) {
	LayoutNode gl = byComponent.get(comp);
	if (gl != null) {
	    gl.setVerticalAlignment(alignment);
	} else {
	    Logger.getGlobal().warning("LeafNode not found for " + comp);
	}
    }

    public void addLayoutNode(LayoutNode layoutNode, String parentNodeName, Object constraints) {
	putNode(layoutNode);
	if (parentNodeName != null) {
	    LayoutNode gl = map.get(parentNodeName);
	    gl.add(layoutNode, constraints);
	}
    }

    /**
     * This method does nothing
     */
    public void addLayoutComponent(String name, Component comp) {
    }

    public void removeLayoutComponent(Component comp) {
	LeafNode leaf = byComponent.remove(comp);
	if (leaf != null) {
	    leaf.getParent().remove(leaf);
	}
    }

    public LayoutNode getNode(String name) {
	return map.get(name);
    }

    public Dimension preferredLayoutSize(Container parent) {
	Insets insets = parent.getInsets();
	Dimension preferredSize = root.preferredSize();
	preferredSize.width += insets.left + insets.right;
	preferredSize.height += insets.top + insets.bottom;
	preferredSize.width += root.getHgap();
	preferredSize.height += root.getVgap();

	return preferredSize;
    }

    public Dimension minimumLayoutSize(Container parent) {
	return preferredLayoutSize(parent);
    }

    public void setHgap(int hgap) {
	root.setHgap(hgap);
    }

    public void setVgap(int vgap) {
	root.setVgap(vgap);
    }

    public int getVgap() {
	return root.getVgap();
    }

    public int getHgap() {
	return root.getHgap();
    }

    public void layoutContainer(Container parent) {
	Rectangle bounds = parent.getBounds();
	Insets insets = parent.getInsets();

	bounds.width -= insets.left + insets.right;
	bounds.x = insets.left;

	bounds.height -= insets.top + insets.bottom;
	bounds.y = insets.top;

	int hgap = getRoot().getHgap();
	int vgap = getRoot().getVgap();

	bounds.width -= hgap;
	bounds.height -= vgap;

	root.layout(bounds);
    }

    /**
     * @param constraints
     *            must be NodeConstraints. However constraints objects of wrong
     *            type or null-constraints will be gracefully ignored by
     *            JNodeLayout. Such components may be added direct to LayoutNode
     *            later.
     * @see NodeConstraints
     */
    public void addLayoutComponent(Component comp, Object constraints) {
	if (constraints == null || !(constraints instanceof NodeConstraints)) {
	    return;
	}
	NodeConstraints constr = (NodeConstraints) constraints;
	LayoutNode node = map.get(constr.getName());
	if (node == null) {
	    node = root.findNode(constr.getName());
	    if (node != null) {
		map.put(constr.getName(), node);
	    }
	}
	if (node != null) {
	    LeafNode leaf = node.add(comp, constr.getConstraints());
	    byComponent.put(comp, leaf);
	} else {
	    Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Node not found: " + constr.getName(), new NullPointerException());
	}
    }

    public LayoutNode getRoot() {
	return root;
    }

    /**
     * Register nodes which was added direct to LayoutNode.
     */
    public void syncNodes() {
	syncNode(this.root);
    }

    private void syncNode(LayoutNode parent) {
	for (LayoutNode child : parent) {
	    if (!child.isLeaf()) {
		putNode(child);
		syncNode(child);
	    } else {
		LeafNode leafNode = (LeafNode) child;
		byComponent.put(leafNode.getComponent(), leafNode);
	    }
	}
    }

    private void putNode(LayoutNode node) {
	LayoutNode oldNode = map.get(node.getName());
	if (oldNode != null) {
	    Logger.getGlobal().log(Level.WARNING, "Warning: node with key \"" + node.getName() + "\" already exists " + oldNode.hashCode());
	}
	map.put(node.getName(), node);
    }

    /**
     * For debugging purpose: check if each node has unique name.
     */
    public void verify() {
	verifyNode(root);
    }

    private void verifyNode(LayoutNode parent) {
	for (LayoutNode child : parent) {
	    if (!child.isLeaf()) {
		String name = child.getName();
		if (map.get(name) != child) {
		    throw new RuntimeException("Verify failed: node " + child.getName() + " " + child + "in not registered or uses same key with another node");
		}
		verifyNode(child);
	    }
	}
    }

    public Dimension maximumLayoutSize(Container target) {
	return root.preferredSize();
    }

    public float getLayoutAlignmentX(Container target) {
	return 0;
    }

    public float getLayoutAlignmentY(Container target) {
	return 0;
    }

    public void invalidateLayout(Container target) {

    }
}
