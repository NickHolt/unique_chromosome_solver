import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.LinkedList;

/**
 * This class reconstructs chromosomes that have been uniquely split into sequences.
 * The sequences must fulfill the following properties:
 *     - All sequences can be uniquely glued together to make a single chromosome.
 *     - Two sequences can only be glued together if they overlap by more than half their length.
 *
 * The methods in this class are mostly private facilities relating to the
 * reconstruction of the chromosome.
 *
 * Because this class doesn't have state, it is interacted with statically.
 */
public class UniqueChromosomeReconstructor {
	/** Enforces non-instantiation of static class. */
	private UniqueChromosomeReconstructor() { }

	/**
	 * Reconstructs the chromosome represented by the provided sequences.
	 *
	 * @param sequences The sequences that constitute the chromosome to reconstruct.
	 * @return The reconstructed chromosome, or null if no such chromosome could be found.
	 */
	public static String reconstructChromosome(Set<String> sequences) {
		SequenceGraph graph = generateSequenceGraph(sequences);
		SequenceGraph.Node root = graph.getRoot();

		// Ensure graph was well-formed
		if (graph == null || root == null || graph.getTerminalNode() == null) {
			return null;
		}

		return reconstructChromosomeFromGraph(graph);
	}

	/**
	 * Gets the index at which one sequence overlaps another by more than
	 * half the sequence lengths.
	 *
	 * @param base The sequence onto which to overlay the other.
	 * @param toOverlay The sequence to overlay onto the other.
	 * @return The index at which the overlap occurs, or -1 if no such index exists.
	 */
	private static int getSequenceOverlapIndex(String base, String toOverlay) {
		if (toOverlay.length() <= base.length() / 2) {
			return -1;
		}

		// Start with the first half of the overlaying sequence
		StringBuilder suffix = new StringBuilder();
		suffix.append(toOverlay.substring(0, base.length() / 2 + 1));

		// Pull characters from the rest of the overlaying sequence until a match is found
		for (int i = base.length() / 2 + 1; i < toOverlay.length(); i++) {
			suffix.append(toOverlay.charAt(i));

			if (base.endsWith(suffix.toString())) {
				return base.length() - suffix.length();
			}
		}

		// No overlap found
		return -1;
	}

	/**
	 * Generates a SequenceGraph from the given sequences.
	 * This method will construct a graph given by the provided sequences,
	 * including the edges between any two overlapping sequences.
	 *
	 * @param sequences The sequences to use in the graph.
	 * @return The constructed graph, or null if the graph could not be created.
	 */
	private static SequenceGraph generateSequenceGraph(Set<String> sequences) {
		if (sequences == null) {
			return null;
		}

		SequenceGraph graph = new SequenceGraph();

		// Pairwise check all sequences for overlapping relationships
		for (String firstSequence : sequences) {
			graph.addSequence(firstSequence);

			for (String secondSequence : sequences) {
				if (firstSequence == secondSequence) {
					continue;
				}

				int overlap = getSequenceOverlapIndex(firstSequence, secondSequence);
				if (overlap >= 0) {
					graph.addOverlap(firstSequence, secondSequence, overlap);
				}
			}
		}

		return graph;
	}

	/**
	 * Given a SequenceGraph, reconstructs the chromosome encoded by the graph's Nodes.
	 *
	 * @param graph The graph to construct the chromosome from.
	 * @return The constructed chromosome, or null if no such chromosome exists.
	 */
	private static String reconstructChromosomeFromGraph(SequenceGraph graph) {
		// Get Hamiltonian path through graph
		LinkedList<SequenceGraph.Node> path = findHamiltonianPath(graph);
		if (path == null) {
			return null;
		}

		// Walk path and rebuild chromosome string
		StringBuilder chromosome = new StringBuilder();

		SequenceGraph.Node parent = path.pollFirst();
		SequenceGraph.Node child = path.pollFirst();
		while (child != null) {
			int overlapIndex = parent.getOverlapIndexForChild(child);
			chromosome.append(parent.getSequence().substring(0, overlapIndex));

			parent = child;
			child = path.pollFirst();
		}

		// Terminal sequence
		chromosome.append(parent.getSequence());

		return chromosome.toString();
	}

	/**
	 * Determines the Hamiltonian path through a sequence graph.
	 *
	 * @param graph The graph to find the path within.
	 * @return The Hamiltonian path, or null if no such path exists.
	 */
	private static LinkedList<SequenceGraph.Node> findHamiltonianPath(SequenceGraph graph) {
		SequenceGraph.Node root = graph.getRoot();
		HashSet<SequenceGraph.Node> visitedSet = new HashSet<>();
		LinkedList<SequenceGraph.Node> path = new LinkedList<>();

		if (root == null || !findHamiltonianPath(graph, root, visitedSet, path)) {
			return null;
		}

		return path;
	}

	/**
	 * Determines the Hamiltonian path through a sequence graph.
	 * If such a path was found, it would be represented in the provided
	 * path list. This method is used as a helper.
	 *
	 * @param graph The graph to find the path within.
	 * @param current The current node in the path iteration.
	 * @param visited The set of already-visited nodes.
	 * @param path The path of Nodes thus far in the search.
	 * @return true if a Hamiltonian path was found, false otherwise.
	 */
	private static boolean findHamiltonianPath(SequenceGraph graph,
	                                           SequenceGraph.Node current,
	                                           Set<SequenceGraph.Node> visited,
	                                           LinkedList<SequenceGraph.Node> path) {
		if (visited.contains(current)) {
			return false;
		}

		// Mark current Node
		path.add(current);
		visited.add(current);

		// Check path-terminating state
		if (current.getChildren().size() == 0) {
			if (path.size() == graph.size()) {
				return true;
			} else {
				path.removeLast();
				return false;
			}
		}

		// Recurse on children
		for (SequenceGraph.Node child : current.getChildren()) {
			if (findHamiltonianPath(graph, child, visited, path)) {
				// Path found through child
				return true;
			}
		}

		// No path through current node given current path
		path.removeLast();
		visited.remove(current);

		return false;
	}

	/**
	 * Prints the command-line usage instructions for this class to standard out.
	 */
	private static void printUsage() {
		System.out.println("Usage: java UniqueUniqueChromosomeReconstructor [-h] input_file");
		System.out.println("            -h: Print usage information");
		System.out.println("    input_file: File containing input sequence data");
	}

	/**
	 * Takes a data file as a command line argument and attempts to reconstruct the
	 * chromosome encoded by the segments of that file.
	 * The result of the attempt is printed to standard out.
	 */
	public static void main(String[] args) {
		// Parse command-line arguments
		if (args.length < 1 || args.length > 2 || Arrays.asList(args).contains("-h")) {
			printUsage();
			return;
		}

		// Parse file
		String filename = args[args.length - 1];
		HashSet<String> sequences = FastaFileReader.parseFastaFile(filename);
		if (sequences == null) {
			System.err.println("ERROR: Unable to parse FASTA file \"" + filename + "\"");
			return;
		}

		// Attempt to reconstruct the chromosome
		String result = UniqueChromosomeReconstructor.reconstructChromosome(sequences);
		if (result == null) {
			System.err.println("ERROR: Unable to reconstruct a unique chromosome");
			return;
		}

		// Report result
		System.out.println("The reconstructed chromosome is:");
		System.out.println(result);
		System.out.println("Length: " + result.length());
	}
}
