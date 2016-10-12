import java.util.HashSet;
import java.util.HashMap;
import java.util.Set;

/**
 * This class represents the entirety of a single sequence graph.
 * A sequence graph is defined as a graph that has exactly one root and one
 * terminal node, and each edge contains information about how the sequence
 * represented by the destination of the edge is to be overlaid onto the
 * sequence represented by the source of the edge. In this case,
 * the index of the parent node's sequence at which the child sequence
 * can be overlaid.
 *
 * The methods of this class facilitate the construction of the graph,
 * while also providing accessor methods for nodes of interest.
 *
 * The units of a SequenceGraph are Nodes. Nodes encode both the node and
 * edge information for a given sequence.
 */
public class SequenceGraph {
	public static class Node {
		/** The sequence this Node represents. */
		private String sequence;
		/** The Nodes whose sequences can be overlaid by this Node's sequence. */
		private HashSet<Node> parents;
		/** The Nodes whose sequences can be overlaid on to this Node's sequence. */
		private HashMap<Node, Integer> children;

		/**
		 * A new Node representing the provided sequence.
		 * Initialized with no parents or children.
		 *
		 * @param sequence The sequence to represent.
		 */
		public Node(String sequence) {
			this.sequence = sequence;
			parents = new HashSet<>();
			children = new HashMap<>();
		}

		/**
		 * Add a parent to this Node's set of parents.
		 * This Node will automatically be added as a child to the parent.
		 *
		 * @param parent The parent node to add.
		 * @param overlapIndex The index at which this Node's sequence overlaps
		 *                     the parent's sequence.
		 */
		public void addParent(Node parent, int overlapIndex) {
			if (parents.contains(parent)) {
				return;
			}

			parents.add(parent);
			parent.addChild(this, overlapIndex);
		}

		/**
		 * Add a child to this Node's set of children.
		 * This Node will automatically be added as a parent to the child.
		 *
		 * @param child The child node to add.
		 * @param overlapIndex The index at which the child's sequence overlaps
		 *                     this Node's sequence.
		 */
		public void addChild(Node child, int overlapIndex) {
			if (children.containsKey(child)) {
				return;
			}

			children.put(child, overlapIndex);
			child.addParent(this, overlapIndex);
		}

		/**
		 * @return The sequence represented by this Node.
		 */
		public String getSequence() { return this.sequence; }

		/**
		 * @return The parents of this Node.
		 */
		public Set<Node> getParents() { return this.parents; }

		/**
		 * @return The children of this Node.
		 */
		public Set<Node> getChildren() { return this.children.keySet(); }

		/**
		 * @param child The child whose sequence overlaps this Node's sequence.
		 * @return The index at which the provided child's sequences overlaps
		 *         this Node's sequence. Returns -1 if the provided Node
		 *         is not a child of this Node.
		 */
		public int getOverlapIndexForChild(Node child) {
			if (!children.containsKey(child)) {
				return -1;
			}

			return children.get(child);
		}
	}

	/** A mapping of sequences to their Node representations in this graph. */
	private HashMap<String, Node> sequenceNodes;

	/**
	 * A new, empty sequence graph.
	 */
	public SequenceGraph() {
		sequenceNodes = new HashMap<>();
	}

	/**
	 * @return The number of nodes in this graph.
	 */
	public int size() {
		return sequenceNodes.size();
	}

	/**
	 * Adds a Node to the graph that represents the provided sequence.
	 * Has no effect if the sequence has already been added.
	 *
	 * @param sequence The sequence to represent in this graph.
	 */
	public void addSequence(String sequence) {
		if (sequenceNodes.containsKey(sequence)) {
			return;
		}

		sequenceNodes.put(sequence, new Node(sequence));
	}

	/**
	 * Gets the Node of this graph representing the provided sequence.
	 *
	 * @param sequence The sequence whose Node is to be returned.
	 * @return The Node representing the sequence, or null if no such Node exists.
	 */
	public Node getSequenceNode(String sequence) {
		return sequenceNodes.get(sequence);
	}

	/**
	 * Adds an edge between the Nodes represented by the provided sequences.
	 * If the Nodes do not already exist, they will be added automatically.
	 *
	 * @param parent The sequence to be the parent of the other sequence.
	 * @param child The sequence to be the child of the other sequence.
	 * @param overlapIndex The overlap index between the parent and child.
	 */
	public void addOverlap(String parent, String child, int overlapIndex) {
		addSequence(parent);
		addSequence(child);

		getSequenceNode(parent).addChild(getSequenceNode(child), overlapIndex);
	}

	/**
	 * A utility method that will return either the root Node or the terminal
	 * Node, as specified.
	 *
	 * @param isParent If true, the terminal Node will be returned. Otherwise
	 *                 the root node will be returned.
	 * @return The root or terminal Node. If no such Node or multiple such
	 *         Nodes exist, return null.
	 */
	private Node getOneWayNode(boolean isParent) {
		Node node = null;

		// Find the node with 0 children or parents, depending on desired Node
		for (String sequence : sequenceNodes.keySet()) {
			Node sequenceNode = getSequenceNode(sequence);
			int numReleventEdges = isParent ? sequenceNode.getParents().size() :
			                                  sequenceNode.getChildren().size();
			if (numReleventEdges == 0) {
				if (node != null) {
					return null;
				}

				node = sequenceNode;
			}
		}

		return node;
	}

	/**
	 * @return The root node of this graph. If no root or multiple roots exist,
	 *         returns null.
	 */
	public Node getRoot() {
		return getOneWayNode(true);
	}

	/**
	 * @return The terminal node node of this graph. If no terminal node or
	 *         multiple terminal nodes exist, returns null.
	 */
	public Node getTerminalNode() {
		return getOneWayNode(false);
	}
}
