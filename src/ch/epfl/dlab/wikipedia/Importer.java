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

public class Importer {

	public static Graph loadGraph(String file) {
		Graph graph = new Graph();

		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			for (String line; (line = br.readLine()) != null;) {
				if (line.startsWith("#"))
					continue;
				String[] edge = line.split("\t");
				String parent = edge[1].replace(' ', '_');
				String child = edge[0].replace(' ', '_');

				Node parentCategory = graph.getOrCreateNode(parent);
				Node childCategory = graph.getOrCreateNode(child);

				parentCategory.children.add(childCategory);
				childCategory.parents.add(parentCategory);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return graph;
	}

	public static void main(String[] args) {

		if (args.length < 2) {
			System.out.println("Missing parameters: <input_file> <output_file_name>");
			System.exit(0);
		}

		System.out.println("Running...");
		Graph graph = Importer.loadGraph(args[0]);
		System.out.println("Graph Loaded...");
		
		List<Node> sorted = FAS.get(graph);
		System.out.println(sorted.size());
		System.out.println("done");
		Map<Node, Integer> position = new HashMap<>();
		for (int i = 0; i < sorted.size(); i++) {
			position.put(sorted.get(i), i);
		}

		try {
			FileWriter fw = new FileWriter(new File(args[1]));
			BufferedWriter bw = new BufferedWriter(fw);
			for (Node c : graph.nodes.values()) {
				for (Node parent : c.parents) {
					try {
						int nodePosition = position.get(c);
						int parentPosition = position.get(parent);
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
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
