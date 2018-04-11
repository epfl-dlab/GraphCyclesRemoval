package ch.epfl.dlab.wikipedia;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import ch.epfl.dlab.wikipedia.Graph.Node;

/********
 * A fast and effective heuristic for the feedback arc set problem
 * https://www.sciencedirect.com/science/article/pii/002001909390079O
 * 
 * @author tiziano
 */
public class FAS {

	public static List<Node> get(Graph graph) {
		LinkedList<Node> s1 = new LinkedList<>();
		LinkedList<Node> s2 = new LinkedList<>();

		HashMap<Integer, Set<Node>> byIndegree = new HashMap<>();
		HashMap<Integer, Set<Node>> byOutdegree = new HashMap<>();

		// SIMPLE CONNECTED == NO SELF CYCLES
		// Remove self loops
		for (Node c : graph.nodes.values()) {
			for (Node child : new HashSet<>(c.children))
				if (child.equals(c))
					c.children.remove(c);
			for (Node parent : new HashSet<>(c.parents))
				if (parent.equals(c))
					c.parents.remove(c);
		}

		////////////////////////////////
		// Get max in and out degree
		int maxIndegree = 0;
		int maxOutdegree = 0;
		for (Node c : graph.nodes.values()) {
			maxIndegree = Math.max(maxIndegree, c.parents.size());
			maxOutdegree = Math.max(maxOutdegree, c.children.size());
		}
		for (int i = 0; i <= maxIndegree; i++)
			byIndegree.put(i, new HashSet<>());
		for (int i = 0; i <= maxOutdegree; i++)
			byOutdegree.put(i, new HashSet<>());
		////////////////////////////////
		for (Node c : graph.nodes.values()) {
			byIndegree.get(c.parents.size()).add(c);
			byOutdegree.get(c.children.size()).add(c);
		}

		// Algorithm starts here
		while (graph.nodes.size() > 0) {

			/*******
			 * SINKS BLOCK
			 */
			while (byOutdegree.get(0).size() > 0) {

				// create a copy because it can change while iterating
				Set<Node> sinks = new HashSet<>(byOutdegree.get(0));
				for (Node sink : sinks) {

					byOutdegree.get(0).remove(sink);
					byIndegree.get(sink.parents.size()).remove(sink);
					graph.nodes.remove(sink.name);

					// s2 <- u s2
					s2.push(sink);

					for (Node parent : sink.parents) {
						// remove the parent from the set of node with out
						// degree n
						byOutdegree.get(parent.children.size()).remove(parent);
						// remove the sink
						parent.children.remove(sink);
						// add the parent from the set of node with out degree
						// n-1
						byOutdegree.get(parent.children.size()).add(parent);
					}

				}

			}

			/*******
			 * SOURCES BLOCK
			 */
			while (byIndegree.get(0).size() > 0) {
				Set<Node> sources = new HashSet<>(byIndegree.get(0));
				for (Node source : sources) {
					byIndegree.get(0).remove(source);
					byOutdegree.get(source.children.size()).remove(source);
					graph.nodes.remove(source.name);

					s1.add(source);

					for (Node child : source.children) {
						byIndegree.get(child.parents.size()).remove(child);
						child.parents.remove(source);
						byIndegree.get(child.parents.size()).add(child);
					}

				}
			}

			/*******
			 * MAX DELTA BLOCK
			 */
			if (graph.nodes.values().size() > 0) {
				int max = Integer.MIN_VALUE;
				Node selected = null;
				for (Node c : graph.nodes.values()) {
					int d = c.children.size() - c.parents.size();
					if (max < d) {
						max = d;
						selected = c;
					}
				}

				s1.add(selected);

				graph.nodes.remove(selected.name);

				for (Node parent : selected.parents) {
					byOutdegree.get(parent.children.size()).remove(parent);
					parent.children.remove(selected);
					byOutdegree.get(parent.children.size()).add(parent);
				}
				for (Node child : selected.children) {
					byIndegree.get(child.parents.size()).remove(child);
					child.parents.remove(selected);
					byIndegree.get(child.parents.size()).add(child);
				}

			}
		}

		s1.addAll(s2);
		return new ArrayList<>(s1);
	}

}
