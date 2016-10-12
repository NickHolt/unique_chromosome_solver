import java.io.File;
import java.util.HashSet;

/**
 * This class is responsible for the testing of UniqueChromosomeReconstructor.
 * When invoked from the command-line, this class' main method will search
 * for an adjacent test_data directory, the contents of which will be used
 * as test input. It will then print the results of those tests to standard out.
 *
 * The methods contained within are to help facilitate that testing. In general,
 * this file is not meant to be used from other contexts.
 *
 * Because this class doesn't have state, it is interacted with statically.
 */
public class UniqueChromosomeReconstructorTester {
	/** Enforces non-instantiation of static class. */
	private UniqueChromosomeReconstructorTester() { }

	/**
	 * Checks if all segment Strings are a substring of the given chromosome String.
	 *
	 * @param segments A HashSet of segment Strings.
	 * @param chromosome A String to which all segment strings have membership checked.
	 * @return true if all segment strings are contained in the chromosome string. Returns false otherwise.
	 */
	private static boolean validateChromosome(HashSet<String> segments, String chromosome) {
		if (segments == null || chromosome == null) {
			return false;
		}

		for (String segment : segments) {
			if (!chromosome.contains(segment)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Tests UniqueChromosomeReconstructor's ability to process a given data file.
	 * Prints the result of the test to System.out.
	 *
	 * @param file A FASTA sequence data file to use as input to UniqueChromosomeReconstructor.
	 */
	private static void testFile(File file) {
		// Read file data
		String filePath = file.getAbsolutePath();
		HashSet<String> sequences = FastaFileReader.parseFastaFile(filePath);

		if (sequences == null) {
			System.err.println("ERROR: Could not parse test file at: " + filePath);
			return;
		}

		// Parse file data
		String chromosome = UniqueChromosomeReconstructor.reconstructChromosome(sequences);

		// Validate and report result
		System.out.println(file.getName() + ":");
		System.out.print("    ");
		if (validateChromosome(sequences, chromosome)) {
			System.out.println("PASS");
		} else {
			System.out.println("FAIL");
		}
	}

	/**
	 * Runs validation of UniqueChromosomeReconstructor over all test data files.
	 */
	public static void main(String[] args) {
		// Ensure test directory exists
		File testDir = new File("./test_data");

		if (!testDir.exists() || !testDir.isDirectory()) {
			System.err.println("ERROR: Cannot locate test data directory. Aborting.");
			return;
		}

		// Test each file in the test directory
		File[] files = testDir.listFiles();
		if (files == null) {
			return;
		}

		for (File file : files) {
			testFile(file);
		}
	}
}
