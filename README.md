# jnodelayout

Node-based LayoutManager.

Currently implemented Nodes are: HorizontalNode, VerticalNode, GridNode, RectNode and LeafNode.
LeafNode is used only internally.

HorizontalNode and VerticalNode are used to layout Children horizontally or vertically.
With RectNode you may specify Children position and size relative to parent component. 
For example Rectangle (0.0, 0.0, 0.5,0.5) specifies that Child will take top left quarter of his Parent.
GridNode is very flexible LayoutNode. You don't have to specify grid dimensions or size of grid cell.
This will be made by GridNode itself according to grid cell count used by each child and their preferred sizes. You should just add children and specify which grid cells each child should occupy.
This however can be challenging job, so I wrote utility class called GridHelper which will make it easy. 
Please, see examples (under com.smartg.test.jnl) for usage.

JNodeLayout specifies six alignment types: TOP, LEFT, BOTTOM, RIGHT, CENTER and STRETCHED. 
RectNode always uses STRETCHED mode.
GridNode supports all alignment types.
HorizontalNode supports LEFT, RIGHT, CENTER and STRETCHED, and VerticaNode - TOP, BOTTOM, CENTER and STRETCHED.
