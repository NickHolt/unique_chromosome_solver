This repository contains my solution to the Driver Group coding challenge.

Usage
======

Given a FASTA-formatted data file, the reconstructed chromosome can be
calculated and printed with the following commands:

```
$ javac UniqueChromosomeReconstructor.java
$ java UniqueChromosomeReconstructor data_file
```

Algorithm Overview
======
This solution takes a graph-based approach for finding the viable way in which
the sequences can be overlaid into a single chromosome. To facilitate this,
a custom data type referred to here as a "sequence graph" was used.

A sequence graph is a graph in which every node represents an individual
sequence segment. The node relationship

```
[A] -> [B]
```

signifies that B is a sequence that can be overlaid onto A. In addition,
we go ahead and store the index at which that can occur:

```
    i
[A] -> [B]
```

This says that a valid combination of sequences A and B is:

```
A.substring(0, i) + B
```

As a concrete example:

```
             3
[ATTAGACCTG] -> [AGACCTGCCG]
```

yields:

```
ATTAGACCTG.substring(0, 3) + AGACCTCGCCG = ATT + AGACCTCGCCG = ATTAGACCTGCCG
```

In this particular problem, we also happen to know that the graph contains
only a single root and a single terminal node - as implied by the guarantee of
uniqueness of the reconstructed chromosome.

In order to reconstruct the entire chromosome, we build this graph by checking
every sequence against every other sequence. If an overlap exists (and the
overlap is more than half the sequence lengths), then we add that edge
relationship to the graph.

(50 sequences of 1000 characters ~ 50kb in UTF8, so pulling them all into
 memory for the graph construction shouldn't be an issue)

Once the graph has been constructed, it can be used to reconstruct the
chromosome by finding a Hamiltonian path between the root and terminal node,
then walking that path and building the chromosome string iteratively.

Code Overview
======

(For more detailed descriptions of the classes, please refer to the inline
documentation)

### FastaFileReader.java
This class responsible for the parsing of the FASTA data files into memory.

### SequenceGraph.java
The data structure responsible for representing the sequence graph in code.

### UniqueChromosomeReconstructor.java
The engine that reconstructs the chromosome based on the sequence segments.

Runtime Analysis
======
In the most general sense, this problem is one of Hamiltonian path finding, a
known NP-Complete problem. We can experience these exponential runtimes
when the input data results in a highly connected graph.

However, in the case of uniquely-reconstructible genome sequencing, it's highly
unlikely for such a graph to occur. Even in the generous case of 50 sequences
of length 100, the odds of at least one sequence accidentally overlapping with
at least one other is approximately 1 - ((1 - 0.25<sup>50</sup>)<sup>48</sup>)<sup>50</sup>.
Since this value is infinitesimally close to 0, we can safely assume that these
extra connections will not occur in the graph.<sup>1</sup>

Indeed, the input data set has a unique path from root to the single terminal
node, with no extra branches.

When this occurs, the runtime is O(N<sup>2</sup>), which is incurred when
constructing the graph (traversal is O(N)). The memory used is O(N), as we
have O(N) nodes in the graph as well as O(N) edges.

<sup>1</sup>This does assume genomes are effectively random strings, which is
not true. However, this should still hopefully give us a sense of likelihood.
