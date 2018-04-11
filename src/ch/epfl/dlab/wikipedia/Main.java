package ch.epfl.dlab.wikipedia;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.epfl.dlab.wikipedia.Graph.Node;

/**
 * 
 * @author Tiziano Piccardi <tiziano.piccardi@epfl.ch>
 *
 */
public class Main {

	/************
	 * Load the graph in memory. Lines that start with # are ignored
	 * 
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public static Graph loadGraph(String fileName) throws IOException {
		Graph graph = new Graph();
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		for (String line; (line = br.readLine()) != null;) {
			if (line.startsWith("#"))
				continue;
			String[] edge = line.split("\t");
			String parent = edge[1];
			String child = edge[0];

			Node parentCategory = graph.getOrCreateNode(parent);
			Node childCategory = graph.getOrCreateNode(child);

			parentCategory.children.add(childCategory);
			childCategory.parents.add(parentCategory);
		}
		br.close();
		return graph;
	}

	/*********
	 * Write only the edges that respect the topological order
	 * @param sorted
	 * @param graph
	 * @param fileName
	 * @throws IOException
	 */
	public static void writeGraph(List<Node> sorted, Graph graph, String fileName) throws IOException {
		
		// Generate an inverse index: category -> position
		Map<Node, Integer> position = new HashMap<>();
		for (int i = 0; i < sorted.size(); i++) {
			position.put(sorted.get(i), i);
		}

		FileWriter fw = new FileWriter(new File(fileName));
		BufferedWriter bw = new BufferedWriter(fw);
		
		// For every node, check all the parents (== enumerate all the edges) 
		for (Node c : graph.nodes.values()) {
			for (Node parent : c.parents) {
				try {
					int nodePosition = position.get(c);
					int parentPosition = position.get(parent);
					
					// if the child appears earlier in the array, I should ignore it
					if (nodePosition > parentPosition) {
						bw.write(c.name + "\t" + parent.name);
						bw.newLine();
					}

				} catch (Exception e) {
					e.printStackTrace();
					System.err.println(c);
					System.out.println(parent);
				}

			}
		}
		bw.flush();
		bw.close();

	}

	/**
	 * Entry point
	 * @param args
	 */
	public static void main(String[] args) {

		if (args.length < 2) {
			System.out.println("Missing parameters: <input_file> <output_file_name>");
			System.exit(0);
		}

		System.out.println("Running...");
		
		// Load the graph
		Graph graph = null;
		try {
			graph = Main.loadGraph(args[0]);
		} catch (IOException e) {
			System.err.println(e.getMessage());
			System.exit(-1);
		}
		System.out.println("Graph Loaded...");

		// Apply topological sorting
		System.out.println("Sorting...");
		List<Node> sorted = FAS.get(graph);
		System.out.println("Done.");

		System.out.println("Writing the result in " + args[1]);

		// Write the edges in the file: only the forward links
		try {
			writeGraph(sorted, graph, args[1]);
		} catch (IOException e) {
			System.err.println(e.getMessage());
			System.exit(-1);
		}

		System.out.println("DONE! Bye");

	}

}
