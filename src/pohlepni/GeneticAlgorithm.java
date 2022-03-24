package pohlepni;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import pohlepni.WeightedGraph.Edge;
import pohlepni.WeightedGraph.Vertex;

public class GeneticAlgorithm {
	public Random rand = new Random();
	private Population population = new Population();
	public static int count = 9;
	public static int vertex_num = 5;
	public int n;
	private static WeightedGraph G;

	public class Population {
		int size = 20; // novi broj
		ArrayList<Individual> parents = new ArrayList<Individual>();
		ArrayList<Individual> children = new ArrayList<Individual>();
		ArrayList<Individual> children2 = new ArrayList<Individual>();

		public Population() {
			int br = 0;
			while (br < 20) {
				parents.add(new Individual());
				br++;
			}
		}

		// Funckija za ispis gena (pocetni nizovi 0-1 )
		public void writeIndividualsGenes() {
			System.out.println("Roditelji: ");
			for (Individual in : parents) {
				System.out.println(in);
				System.out.println();
			}
			if (children.size() != 0) {
				System.out.println("DJECAAAA: ");
				for (Individual in : children) {
					System.out.println(in);
					System.out.println();
				}
			}
		}
	}

	// klasa za jedinke
	public class Individual {
		public double fitness;
		public int[] genes;

		public Individual() {
			this.fitness = Double.MAX_VALUE;
			genes = new int[vertex_num];
			this.initialize();
		}

		public void initialize() {
			for (int i = 0; i < genes.length; i++)
				genes[i] = Math.abs(rand.nextInt() % 2);
		}

		public void calculateF() {
			WeightedGraph D = graphFromArray(G, genes);
			if (isWTP(G, D))
				this.fitness = evaluate(G, D);
		}

		private WeightedGraph graphFromArray(WeightedGraph G, int[] gen) {
			WeightedGraph D = new WeightedGraph();
			for (int i = 0; i < gen.length; i++)
				if (gen[i] == 1)
					D.addVertex(new Vertex(i + "", G.getVertex(i + "").getWeight()));
			for (Vertex v : D.getVertices()) {
				for (Edge e : G.getVertex(v.getLabel()).getEdges()) {
					Vertex v2 = D.getVertex(e.getTo().getLabel());
					if (v2 != null)
						v.addEdge(new Edge(v2, e.getWeight(), e.getLabel()));
				}
			}
			return D;
		}

		private Boolean isWTP(WeightedGraph G, WeightedGraph D) { // does node without
			return G.allVertices().size() == nodesAndNeighbors(G, D).size(); // neighbors in D exists
		}

		private Set<Vertex> nodesAndNeighbors(WeightedGraph G, WeightedGraph D) { // nodes from D and neighbor
			Set<Vertex> rtrn = D.allVertices(); // of nodes in D
			for (Vertex vrt : D.getVertices()) {
				Vertex temp = G.getVertex(vrt.getLabel());
				for (Vertex v : temp.getNeighbors())
					if (D.getVertex(v.getLabel()) == null)
						rtrn.add(v);
			}
			return rtrn;
		}

		public int evaluate(WeightedGraph G, WeightedGraph D) {
			int sum = 0;
			int sum2 = 0;
			for (Vertex v : D.getVertices()) {
				sum += v.getWeight();
				for (Edge e : v.getEdges())
					sum2 += e.getWeight();
			}
			sum += sum2 / 2;
			for (Vertex v : nodesThatArentInD(G, D)) {
				double min = Double.MAX_VALUE;
				for (Edge e : v.getEdges()) {
					if (D.getVertices().contains(e.getTo())) {
						if (e.getWeight() < min)
							min = e.getWeight();
					}
				}
				sum += min;
			}
			return sum;
		}

		private Set<Vertex> nodesThatArentInD(WeightedGraph G, WeightedGraph D) { // nodes from G that arent
			Set<Vertex> rtrn = new HashSet<>(); // of nodes in D
			for (Vertex v : G.getVertices())
				if (D.getVertex(v.getLabel()) == null)
					rtrn.add(v);
			return rtrn;
		}

		public int[] getGenes() {
			return genes;
		}

		public String toString() {
			String rez = " ";
			for (int i = 0; i < genes.length; i++)
				rez += genes[i] + " ";
			rez += " fitnes: " + fitness;
			return rez;
		}

	}

	public void evaluate() {
		for (Individual in : population.children) {
			in.calculateF();
		}
		for (Individual in : population.parents) {
			in.calculateF();
		}
	}

	public void selection() {
		Collections.sort(population.children, new Comparator<Individual>() {
			@Override
			public int compare(Individual a, Individual b) {
				return (int) (a.fitness - b.fitness);
			}
		});

		if (population.children.size() == 0) {
			Collections.sort(population.parents, new Comparator<Individual>() {
				@Override
				public int compare(Individual a, Individual b) {
					return (int) (a.fitness - b.fitness);
				}
			});

			int i = 0;
			while (i < 5) {
				population.children.add(population.parents.get(0));
				population.parents.remove(0);
				i++;
			}
		} else {
			population.parents.clear();
			int i = 5;
			while (population.children.size() > 5) {
				if (i < 20) {
					population.parents.add(population.children.get(5));
					i++;
				}
				population.children.remove(5);
			}
		}

	}

	public void crossover() {
		population.children2.clear();
		n = rand.nextInt(vertex_num);
		System.out.println("N: " + n);
		Collections.shuffle(population.parents);
		for (int i = 0; i < population.parents.size() - 1; i++) {
			crossover2(population.parents.get(i), population.parents.get(i + 1));
		}
		System.out.println("Poslije ukrstanja;");
		for (Individual in : population.children2) {
			System.out.println(in);
			System.out.println();
		}

	}

	public void crossover2(Individual p1, Individual p2) {
		int i = 0;
		Individual c1 = new Individual();
		Individual c2 = new Individual();
		while (i < n) {
			c1.genes[i] = p1.genes[i];
			c2.genes[i] = p2.genes[i];
			i++;
		}
		while (n < vertex_num) {
			c1.genes[n] = p2.genes[n];
			c2.genes[n] = p1.genes[n];
			n++;
		}
		population.children2.add(c1);
		population.children2.add(c2);
	}

	public void mutation() {
		double pm = (double) (Math.random() * ((double) 1) / 2) + ((double) 1 / vertex_num); // vjerovatnoca ga ce gen
																								// mutirati
		for (Individual c : population.children2) {
			for (int i = 0; i < c.genes.length; i++) {
				int pg = rand.nextInt(100);
				if (pg >= 0 && pg <= (int) (pm * 100)) { // --> izvrsava se mutiranje gena sa vjerovatnocom pm
					if (c.genes[i] == 0)
						c.genes[i] = 1;
					else
						c.genes[i] = 0;
				}
			}
		}
		population.children.addAll(population.children2);
	}

	public static void main(String[] args) {
		G = new WeightedGraph();

		// construct vertices
		Vertex v0 = new Vertex("0", 2);
		Vertex v1 = new Vertex("1", 3);
		Vertex v2 = new Vertex("2", 7);
		Vertex v3 = new Vertex("3", 6);
		Vertex v4 = new Vertex("4", 1);
		Vertex v5 = new Vertex("5", 4);

		v0.addEdge(new Edge(v1, 1, "0")); // connect v1 v2
		v1.addEdge(new Edge(v0, 1, "0"));

		v1.addEdge(new Edge(v2, 2, "1")); // connect v2 v3
		v2.addEdge(new Edge(v1, 2, "1"));

		v1.addEdge(new Edge(v3, 3, "2")); // connect v2 v4
		v3.addEdge(new Edge(v1, 3, "2"));

		v3.addEdge(new Edge(v4, 1, "3")); // connect v4 v5
		v4.addEdge(new Edge(v3, 1, "3"));

		v5.addEdge(new Edge(v0, 1, "4")); // connect v4 v5
		v0.addEdge(new Edge(v5, 1, "4"));

		v5.addEdge(new Edge(v4, 4, "5")); // connect v4 v5
		v4.addEdge(new Edge(v5, 4, "5"));

		v0.addEdge(new Edge(v4, 8, "6")); // connect v4 v5
		v4.addEdge(new Edge(v0, 8, "6"));

		G.addVertex(v0);
		G.addVertex(v1);
		G.addVertex(v2);
		G.addVertex(v3);
		G.addVertex(v4);
		G.addVertex(v5);
		GeneticAlgorithm ga = new GeneticAlgorithm();
		ga.evaluate();
		//while (count < 10) {
			
			  ga.population.writeIndividualsGenes();
			 System.out.println("----------------------------------");
			 
			ga.selection();
			count++;
			ga.crossover();
			ga.mutation();
			 ga.population.writeIndividualsGenes();
		//}
	}
}