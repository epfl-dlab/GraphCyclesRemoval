package ch.epfl.dlab.wikipedia;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/***********************
 * 
 * @author tiziano
 *
 */
public class Graph {

	public HashMap<String, Node> nodes = new HashMap<String, Node>();

	public synchronized Node getOrCreateNode(String name) {
		Node result = nodes.get(name);
		if (result == null) {
			result = new Node(name);
			nodes.put(name, result);
		}
		return result;
	}

	public static class Node {
		public String name;

		private Node(String name) {
			this.name = name;
		}

		public Set<Node> parents = new HashSet<>();
		public Set<Node> children = new HashSet<>();

		@Override
		public int hashCode() {
			return name.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Node)
				return ((Node) obj).name.equals(this.name);
			return false;
		}

		@Override
		public String toString() {
			return name;
		}

	}

}
