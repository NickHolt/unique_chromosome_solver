import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashSet;

/**
 * This class parses a FASTA-formatted data file, reading the sequences into
 * memory.
 *
 * Because this class doesn't have state, it is interacted with statically.
 */
public class FastaFileReader {
	/** Enforces non-instantiation of static class. */
	private FastaFileReader() { }

	/**
	 * Parse the provided file, attempting to read the segments encoded
	 * in the file according to FASTA standards.
	 *
	 * @param file The name of the file to read.
	 * @return The parsed sequence data, or null if the file could not be parsed.
	 */
	public static HashSet<String> parseFastaFile(String file) {
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			HashSet<String> sequences = new HashSet<>();
			StringBuilder sequence = new StringBuilder();

			String line = null;
			while ((line = reader.readLine()) != null) {
				if (line.length() == 0) { // Empty line
					continue;
				} else if (line.charAt(0) == ';') { // Comment
					continue;
				} else if (line.charAt(0) == '>') { // Sequence delimiter
					// Flush sequence buffer to start over
					if (sequence.length() > 0) {
						sequences.add(sequence.toString());
						sequence.setLength(0);
					}
				} else { // Sequence partial
					sequence.append(line);
				}
			}

			// Flush remaining sequence buffer
			if (sequence.length() > 0) {
				sequences.add(sequence.toString());
			}

			return sequences;
		} catch (IOException x) {
			return null;
		}
	}
}
