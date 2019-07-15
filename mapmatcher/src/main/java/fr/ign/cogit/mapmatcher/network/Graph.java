/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 *
 * @author Yann Méneroux
 ******************************************************************************/

package fr.ign.cogit.mapmatcher.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.NavigableSet;
import java.util.TreeSet;

public class Graph {


	private final Map<String, Vertex> graph; 


	// -------------------------------------------------------------------------
	// 1 edge of the graph (only used by Graph constructor)
	// -------------------------------------------------------------------------
	public static class Edge {
		public final String v1, v2;
		public final double dist;
		public Edge(String v1, String v2, double dist) {
			
			this.v1 = v1;
			this.v2 = v2;
			this.dist = dist;
		}
	}
	// -------------------------------------------------------------------------
	//  1 vertex of the graph, complete with mappings to neighbouring vertices
	// -------------------------------------------------------------------------
	public static class Vertex implements Comparable<Vertex>{
		public final String name;
		public double dist = Double.MAX_VALUE; // MAX_VALUE assumed to be infinity
		public Vertex previous = null;
		public final Map<Vertex, Double> neighbours = new HashMap<Vertex, Double>();



		public Vertex(String name)
		{
			this.name = name;
		}



		// ----------------------------------------------------------------
		// Iterative version of path computation
		// ----------------------------------------------------------------
		private ArrayList<String> getPath(){


			ArrayList<String> OUTPUT = new ArrayList<String>();
			ArrayList<String> OUTPUT_TEMP = new ArrayList<String>();

			Vertex v = this;


			while (v != null){

				OUTPUT_TEMP.add(v.name);

				if (v == v.previous){
					OUTPUT_TEMP.add(v.previous.name);
					break;
				}

				v = v.previous;

			}

			for (int i=OUTPUT_TEMP.size()-1; i>=0; i--){

				OUTPUT.add(OUTPUT_TEMP.get(i));

			}

			OUTPUT.remove(OUTPUT.size()-1);

			return OUTPUT;

		}


		@SuppressWarnings("unused")
		// ----------------------------------------------------------------
		// Recursive version of path computation
		// ----------------------------------------------------------------
		// Problem : not stable with long tracks (requires to increase 
		// JVM stack size for tracks containing more than ~ 10 000 points
		// ----------------------------------------------------------------
		private ArrayList<String> getPathRecursive(){

			ArrayList<String> OUTPUT = new ArrayList<String>();

			if (this == this.previous){
				OUTPUT.add(this.name);
			}
			else if (this.previous == null){
				return new ArrayList<String>();
			}

			else{

				ArrayList<String> output = this.previous.getPathRecursive();

				for (int i=0; i<output.size(); i++){

					OUTPUT.add(output.get(i));

				}

				OUTPUT.add(this.previous.name);

			}

			return OUTPUT;

		}

		// ----------------------------------------------------------------
		// Distance from path
		// ----------------------------------------------------------------
		private double getDistance(){

			return this.dist;

		}

		// ----------------------------------------------------------------
		// Method to print path in console
		// ----------------------------------------------------------------
		private void printPath()
		{
			if (this == this.previous)
			{
				System.out.printf("%s", this.name);
			}
			else if (this.previous == null)
			{
				System.out.printf("%s(unreached)", this.name);
			}
			else
			{
				this.previous.printPath();
				System.out.printf(" -> %s(%f)", this.name, this.dist);
			}
		}

		// ----------------------------------------------------------------
		// Comparing vertices in terms of distance to origin
		// ----------------------------------------------------------------
		public int compareTo(Vertex other)
		{
			if (dist == other.dist)
				return name.compareTo(other.name);

			return Double.compare(dist, other.dist);
		}

		@Override public String toString()
		{
			return "(" + name + ", " + dist + ")";
		}
	}

	// ----------------------------------------------------------------
	// Method to build a graph from a set of edges
	// ----------------------------------------------------------------
	public Graph(Edge[] edges) {
		graph = new HashMap<String, Vertex>(edges.length);

		//one pass to find all vertices
		for (Edge e : edges) {
			
			if (!graph.containsKey(e.v1)) graph.put(e.v1, new Vertex(e.v1));
			if (!graph.containsKey(e.v2)) graph.put(e.v2, new Vertex(e.v2));
		}

		//another pass to set neighbouring vertices
		for (Edge e : edges) {			
			graph.get(e.v1).neighbours.put(graph.get(e.v2), e.dist);
			//graph.get(e.v2).neighbours.put(graph.get(e.v1), e.dist); // also do this for an undirected graph
		}
		
		
	}



	// ----------------------------------------------------------------
	// Runs dijkstra using two specified vertices
	// ----------------------------------------------------------------
	public void dijkstra(String startName, String endName) {

		if (!graph.containsKey(startName)) {
			System.err.printf("Graph doesn't contain start vertex \"%s\"\n", startName);
			return;
		}

		if (!graph.containsKey(endName)) {
			System.err.printf("Graph doesn't contain end vertex \"%s\"\n", endName);
			return;
		}

		final Vertex source = graph.get(startName);
		final Vertex target = graph.get(endName);

		NavigableSet<Vertex> q = new TreeSet<Vertex>();

		// set-up vertices
		for (Vertex v : graph.values()) {
			v.previous = v == source ? source : null;
			v.dist = v == source ? 0 : Double.MAX_VALUE;
			q.add(v);
		}

		dijkstra(q, target);
	}

	// ----------------------------------------------------------------
	// Runs dijkstra until a specific threshold distance
	// ----------------------------------------------------------------
	public void dijkstra(String startName, double distance) {

		if (!graph.containsKey(startName)) {
			System.err.printf("Graph doesn't contain start vertex \"%s\"\n", startName);
			return;
		}

		final Vertex source = graph.get(startName);

		NavigableSet<Vertex> q = new TreeSet<Vertex>();

		// set-up vertices
		for (Vertex v : graph.values()) {
			v.previous = v == source ? source : null;
			v.dist = v == source ? 0 : Double.MAX_VALUE;
			q.add(v);
		}

		dijkstra(q, (int) (distance));
	}

	// ----------------------------------------------------------------
	// Implementation of dijkstra's algorithm using a binary heap
	// ----------------------------------------------------------------
	private void dijkstra(final NavigableSet<Vertex> q, int distance) {      
		Vertex u, v;
		while (!q.isEmpty()) {

			u = q.pollFirst(); // vertex with shortest distance (first iteration will return source)

			if (u.dist > distance){return;}

			if (u.dist == Double.MAX_VALUE) break; // we can ignore u (and any other remaining vertices) since they are unreachable

			//look at distances to each neighbour
			for (Map.Entry<Vertex, Double> a : u.neighbours.entrySet()) {
				v = a.getKey(); //the neighbour in this iteration

				final double alternateDist = u.dist + a.getValue();
				if (alternateDist < v.dist) { // shorter path to neighbour found
					q.remove(v);
					v.dist = alternateDist;
					v.previous = u;
					q.add(v);

				} 
			}
		}
	}


	// ----------------------------------------------------------------
	// Implementation of dijkstra's algorithm using a binary heap.
	// ----------------------------------------------------------------
	private void dijkstra(final NavigableSet<Vertex> q, Vertex target) {      
		Vertex u, v;
		while (!q.isEmpty()) {

			u = q.pollFirst(); // vertex with shortest distance (first iteration will return source)

			// Reaching target
			if (u == target){return;}

			if (u.dist == Double.MAX_VALUE) break; // we can ignore u (and any other remaining vertices) since they are unreachable

			//look at distances to each neighbour
			for (Map.Entry<Vertex, Double> a : u.neighbours.entrySet()) {
				v = a.getKey(); //the neighbour in this iteration

				final double alternateDist = u.dist + a.getValue();
				if (alternateDist < v.dist) { // shorter path to neighbour found
					q.remove(v);
					v.dist = alternateDist;
					v.previous = u;
					q.add(v);

				} 
			}
		}
	}

	// ----------------------------------------------------------------
	// Runs dijkstra using a specified source vertex
	// ----------------------------------------------------------------
	public void dijkstra(String startName) {
		if (!graph.containsKey(startName)) {
			System.err.printf("Graph doesn't contain start vertex \"%s\"\n", startName);
			return;
		}
		final Vertex source = graph.get(startName);
		NavigableSet<Vertex> q = new TreeSet<Vertex>();

		// set-up vertices
		for (Vertex v : graph.values()) {
			v.previous = v == source ? source : null;
			v.dist = v == source ? 0 : Double.MAX_VALUE;
			q.add(v);
		}

		dijkstra(q);
	}

	// ----------------------------------------------------------------
	// Implementation of dijkstra's algorithm using a binary heap.
	// ----------------------------------------------------------------
	private void dijkstra(final NavigableSet<Vertex> q) {      
		Vertex u, v;
		while (!q.isEmpty()) {

			u = q.pollFirst(); // vertex with shortest distance (first iteration will return source)
			if (u.dist == Double.MAX_VALUE) break; // we can ignore u (and any other remaining vertices) since they are unreachable

			//look at distances to each neighbour
			for (Map.Entry<Vertex, Double> a : u.neighbours.entrySet()) {
				v = a.getKey(); //the neighbour in this iteration

				final double alternateDist = u.dist + a.getValue();
				if (alternateDist < v.dist) { // shorter path to neighbour found
					q.remove(v);
					v.dist = alternateDist;
					v.previous = u;
					q.add(v);
				} 
			}
		}
	}

	// ----------------------------------------------------------------
	// Prints a path from the source to the specified vertex
	// ----------------------------------------------------------------
	public void printPath(String endName) {
		if (!graph.containsKey(endName)) {
			System.err.printf("Graph doesn't contain end vertex \"%s\"\n", endName);
			return;
		}

		graph.get(endName).printPath();
		System.out.println();
	}


	// ----------------------------------------------------------------
	// Prints the path from the source to every vertex 
	// (output order is not guaranteed)
	// ----------------------------------------------------------------
	public void printAllPaths() {
		for (Vertex v : graph.values()) {
			v.printPath();
			System.out.println();
		}
	}

	// ----------------------------------------------------------------
	// Get path length
	// ----------------------------------------------------------------
	public double getPathLength(String endName) {

		if (!graph.containsKey(endName)) {
			System.err.printf("Graph doesn't contain end vertex \"%s\"\n", endName);
			System.exit(10);
		}

		if ((graph.get(endName).getDistance()) == Double.MAX_VALUE){

			return Integer.MAX_VALUE;

		}

		return (graph.get(endName).getDistance());

	}


	// ----------------------------------------------------------------
	// Get a path from the source to the specified vertex
	// ----------------------------------------------------------------
	public ArrayList<String> getPath(String endName) {
		if (!graph.containsKey(endName)) {
			System.err.printf("Graph doesn't contain end vertex \"%s\"\n", endName);
			return null;
		}

		ArrayList<String> OUTPUT = graph.get(endName).getPath();

		// ----------------------------------------------------------------
		// Recursive version of path computation
		// ----------------------------------------------------------------
		// Problem : not stable with long tracks (requires to increase 
		// JVM stack size for tracks containing more than ~ 10 000 points
		// ----------------------------------------------------------------
		// ArrayList<String> OUTPUT = graph.get(endName).getPathRecursive();
		// ----------------------------------------------------------------

		if (OUTPUT.size() == 0){

			return OUTPUT;

		}

		// Corrections
		OUTPUT.remove(0);
		OUTPUT.add(endName);

		return OUTPUT;

	}

	// ----------------------------------------------------------------
	// Get a path from the source to the specified vertex
	// ----------------------------------------------------------------
	public ArrayList<Integer> getPathEdges(String endName, Hashtable<String, Integer> TABLE) {

		ArrayList<Integer> OUTPUT = new ArrayList<Integer>();

		ArrayList<String> PATH = getPath(endName);

		for (int i=0; i<PATH.size()-1; i++){

			String key = PATH.get(i)+"->"+PATH.get(i+1);

			OUTPUT.add(TABLE.get(key));

		}


		return OUTPUT;

	}

}
