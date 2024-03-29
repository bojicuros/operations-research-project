package pohlepni;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WeightedGraph {

	private Set<Vertex> vertices; // collection of all verices
	private int numOfEdges;

	public WeightedGraph() {
		vertices = new HashSet<>();
		numOfEdges = 0;
	}

	public WeightedGraph(String fileName) {
		vertices = new HashSet<>();
		Path filePath = Paths.get(fileName);
		try {
			List<String> lines = Files.readAllLines(filePath);
			String[] help = lines.get(0).split(" ");

			int numOfNodes = Integer.parseInt(help[0]);
			for (int i = 0; i < numOfNodes; i++) {
				String[] temp = lines.get(i + 1).split(" ");
				this.addVertex(new Vertex(temp[0], Integer.parseInt(temp[1])));
			}

			numOfEdges = Integer.parseInt(help[1]);
			for (int i = numOfNodes + 1; i < numOfEdges + numOfNodes + 1; i++) {
				String[] temp = lines.get(i).split(" ");
				Vertex from = this.getVertex(temp[1]);
				Vertex to = this.getVertex(temp[2]);
				int weight = Integer.parseInt(temp[3]);
				from.addEdge(new Edge(to, weight, temp[0]));
				to.addEdge(new Edge(from, weight, temp[0]));
			}

		} catch (IOException e) {
			System.err.println("Error occured while reading file");
		}
	}

	List<Vertex> getVertices() {
		return new ArrayList<>(vertices);
	}

	int getNumOfEdges() {
		return numOfEdges;
	}

	void setNumOfEdges(int num) {
		numOfEdges = num;
	}

	boolean addVertex(Vertex vertex) {
		return vertices.add(vertex);
	}

	public Vertex getVertex(String label) {
		for (Vertex v : vertices) {
			if (v.label.equals(label))
				return v;
		}
		return null;
	}

	public Edge getEdge(String label) {
		for (Vertex v : vertices)
			for (Edge e : v.getEdges())
				if (e.getLabel().equals(label))
					return e;
		return null;
	}

	static class Vertex {

		private String label;
		private int weight;
		private Set<Edge> edges; // collection of edges to neighbors

		public Vertex(String name, int weight) {
			this.label = name;
			this.weight = weight;
			edges = new HashSet<>();
		}

		String getLabel() {
			return label;
		}

		boolean addEdge(Edge edge) {
			edge.setFrom(this);
			return edges.add(edge);
		}

		Set<Edge> getEdges() {
			return new HashSet<Edge>(edges);
		}

		public int getWeight() {
			return weight;
		}

		public void setWeight(int weight) {
			this.weight = weight;
		}

		public Set<Vertex> getNeighbors() {
			Set<Vertex> rtrn = new HashSet<>();
			for (Edge e : this.edges) {
				rtrn.add(e.to);
			}
			return rtrn;

		}

		@Override
		public boolean equals(Object b) {
			if(b==null)
				return false;
			Vertex v = (Vertex) b;
			return this.label.equals(v.getLabel());
		}

		public String toString() {
			String rez = "Node " + label + " with weight " + weight + " has neighbours: ";
			for (Edge e : edges) {
				rez += "\n  " + e.getTo().label + " connected with edge (label " + e.getLabel() + ")" + " of weight "
						+ e.getWeight();
			}
			rez += "\n";
			return rez;
		}
	}

	static class Edge {

		private Vertex from;
		private String label;
		private Vertex to;
		private int weight;
		

		public Edge(Vertex to, int weight, String label) {
			super();
			this.to = to;
			this.weight = weight;
			this.label = label;

		}

		Vertex getFrom() {
			return from;
		}

		void setFrom(Vertex v) {
			from = v;
		}

		Vertex getTo() {
			return to;
		}

		int getWeight() {
			return weight;
		}

		String getLabel() {
			return label;
		}
		
		@Override
		public boolean equals(Object b) {
			if(b==null)
				return false;
			Edge e = (Edge) b;
			return this.label.equals(e.getLabel());
		}
	}

	public Set<Vertex> allVertices() {
		Set<Vertex> rtrn = new HashSet<>();
		for (Vertex v : vertices)
			rtrn.add(v);
		return rtrn;
	}
	

	public String toString() {
		String rez = "";
		for (Vertex v : vertices) {
			rez += v.toString();
		}
		return rez;
	}
	

	public static void main(String[] args) {

		WeightedGraph graph = new WeightedGraph();

		// construct vertices
		Vertex v1 = new Vertex("0", 2);
		Vertex v2 = new Vertex("1", 3);
		Vertex v3 = new Vertex("2", 7);
		Vertex v4 = new Vertex("3", 6);
		Vertex v5 = new Vertex("4", 1);

		v1.addEdge(new Edge(v2, 1, "0")); // connect v1 v2
		v2.addEdge(new Edge(v1, 1, "0"));

		v2.addEdge(new Edge(v3, 2, "1")); // connect v2 v3
		v3.addEdge(new Edge(v2, 2, "1"));

		v2.addEdge(new Edge(v4, 3, "2")); // connect v2 v4
		v4.addEdge(new Edge(v2, 3, "2"));

		v4.addEdge(new Edge(v5, 1, "3")); // connect v4 v5
		v5.addEdge(new Edge(v4, 1, "3"));

		graph.addVertex(v1);
		graph.addVertex(v2);
		graph.addVertex(v3);
		graph.addVertex(v4);
		graph.addVertex(v5);
		
		System.out.println(graph);
	}

}