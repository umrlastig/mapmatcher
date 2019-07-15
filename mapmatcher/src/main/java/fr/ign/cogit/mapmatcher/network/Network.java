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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import fr.ign.cogit.mapmatcher.util.Parameters;
import fr.ign.cogit.mapmatcher.util.Tools;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.prep.PreparedGeometry;
import com.vividsolutions.jts.geom.prep.PreparedGeometryFactory;
import com.vividsolutions.jts.index.strtree.STRtree;

import fr.ign.cogit.mapmatcher.core.Main;
import fr.ign.cogit.mapmatcher.core.MapMatching;

public class Network {

	private ArrayList<String> SOURCES;
	private ArrayList<String> TARGETS;
	private ArrayList<Double> WEIGHTS;
	private ArrayList<Geometry> GEOMS;
	private Hashtable<String, ArrayList<String>> NODES;
	private Hashtable<String, Integer> EDGES;
	private Hashtable<String, Integer> EDGE_INDICES;
	private ArrayList<String> EDGE_NAMES;
	private ArrayList<Integer> ONE_WAY;

	private Graph topology = null;

	private String path = "";

	private int nodeNumber = 0;
	private int verticeNumber = 0;

	public int reduc_node = 0;
	public int reduc_vertex = 0;
	public int reduc_edge = 0;

	private Hashtable<String, Double> DISTANCES = null;
	private Hashtable<String, String> LAST_EDGE = null;
	private Hashtable<String, Integer> NODES_LIST = null;

	// ------------------------------------------------------
	// Array for network coordinate system
	// ------------------------------------------------------
	public ArrayList<ArrayList<Integer>> SYSTEM_TRACK;
	public ArrayList<ArrayList<Integer>> SYSTEM_POINT;
	public ArrayList<ArrayList<Double>> SYSTEM_XRAW;
	public ArrayList<ArrayList<Double>> SYSTEM_YRAW;
	public ArrayList<ArrayList<Double>> SYSTEM_ABS;
	public ArrayList<ArrayList<String>> SYSTEM_T;


	public int getNodeNumber(){return nodeNumber;}
	public int getVerticeNumber(){return verticeNumber;}

	public String getPath(){return path;}
	public ArrayList<String> getSources(){return SOURCES;}
	public ArrayList<String> getTargets(){return TARGETS;}
	public ArrayList<Double> getWeights(){return WEIGHTS;}
	public ArrayList<Geometry> getGeometries(){return GEOMS;}
	public Hashtable<String, ArrayList<String>> getNodes(){return NODES;}
	public Hashtable<String, Integer> getEdges(){return EDGES;}
	public Hashtable<String, Integer> getEdgeIndices(){return EDGE_INDICES;}
	public ArrayList<String> getEdgeNames(){return EDGE_NAMES;}
	public ArrayList<Integer> getOneWay(){return ONE_WAY;}

	public void setNodeNumber(int n){this.nodeNumber = n;}
	public void setVerticeNumber(int n){this.verticeNumber = n;}

	public void setPath(String path){this.path = path;}
	public void setSources(ArrayList<String> S){SOURCES = S;}
	public void setTargets(ArrayList<String> T){TARGETS = T;}
	public void setWeights(ArrayList<Double> W){WEIGHTS = W;}
	public void setGeometries(ArrayList<Geometry> G){GEOMS = G;}
	public void setNodes(Hashtable<String, ArrayList<String>> N){NODES = N;}
	public void setEdges(Hashtable<String, Integer> E){EDGES = E;}
	public void setEdgeNames(ArrayList<String> EN){EDGE_NAMES = EN;}
	public void setEdgeIndices(Hashtable<String, Integer> E){EDGE_INDICES = E;}
	public void setOneWay(ArrayList<Integer> OW){ONE_WAY = OW;}

	public void makeSystem(){

		SYSTEM_TRACK = new ArrayList<ArrayList<Integer>>();
		SYSTEM_POINT = new ArrayList<ArrayList<Integer>>();
		SYSTEM_XRAW = new ArrayList<ArrayList<Double>>();
		SYSTEM_YRAW = new ArrayList<ArrayList<Double>>();
		SYSTEM_ABS = new ArrayList<ArrayList<Double>>();
		SYSTEM_T = new ArrayList<ArrayList<String>>();

		for (int i=0; i<SOURCES.size(); i++){

			SYSTEM_TRACK.add(new ArrayList<Integer>());
			SYSTEM_POINT.add(new ArrayList<Integer>());
			SYSTEM_XRAW.add(new ArrayList<Double>());
			SYSTEM_YRAW.add(new ArrayList<Double>());
			SYSTEM_ABS.add(new ArrayList<Double>());
			SYSTEM_T.add(new ArrayList<String>());

		}

	}

	public void addPointToEdge(int track_id, int point_id, int edge){

		SYSTEM_TRACK.get(edge).add(track_id);
		SYSTEM_POINT.get(edge).add(point_id);

	}
	
	public void addPointToEdgeWithCoordinates(int track_id, int point_id, int edge, double xraw, double yraw, double abs, String time){

		addPointToEdge(track_id, point_id, edge);
		
		SYSTEM_XRAW.get(edge).add(xraw);
		SYSTEM_YRAW.get(edge).add(yraw);
		
		SYSTEM_ABS.get(edge).add(abs);
		
		SYSTEM_T.get(edge).add(time);

	}

	public Network(){

		SOURCES = new ArrayList<String>();
		TARGETS  = new ArrayList<String>();
		WEIGHTS = new ArrayList<Double>();
		GEOMS = new ArrayList<Geometry>();
		NODES = new Hashtable<String, ArrayList<String>>();
		EDGES = new Hashtable<String, Integer>();
		EDGE_NAMES = new ArrayList<String>();
		EDGE_INDICES = new Hashtable<String, Integer>();
		ONE_WAY = new ArrayList<Integer>();

	}

	// -----------------------------------------------------
	// Module to build topology of a network
	// -----------------------------------------------------
	// Requires a list of geometries and a float value of 
	// tolerance between nodes. All couple of nodes whose
	// distance is less than tolerance will be merged in a 
	// single node. Node names are consecutive integer 
	// values starting from 1 to the total number of nodes
	// -----------------------------------------------------
	public void makeTopology(){

		Tools.println("Building network topology...");

		// ----------------------------------------------------
		// Spatial index computation
		// ----------------------------------------------------
		STRtree ix = new STRtree();

		ArrayList<PreparedGeometry> PREP_GEOMS = new ArrayList<PreparedGeometry>();

		GeometryFactory gf = new GeometryFactory();

		// Run through edge geometries
		for (int i=0; i<GEOMS.size(); i++){

			// Get geometry
			Geometry g = GEOMS.get(i);

			Point p1 = gf.createPoint(g.getCoordinates()[0]);
			Point p2 = gf.createPoint(g.getCoordinates()[g.getCoordinates().length-1]);

			// Prepare geometry
			PREP_GEOMS.add(PreparedGeometryFactory.prepare(p1));
			PREP_GEOMS.add(PreparedGeometryFactory.prepare(p2));

			// add to index
			ix.insert(p1.getEnvelopeInternal(), 2*i);
			ix.insert(p2.getEnvelopeInternal(), 2*i+1);

		}

		ix.build();

		// Storage for node merges
		Hashtable<Integer, Integer> MERGE = new Hashtable<Integer, Integer>();


		// Run through edge geometries
		for (int i=0; i<GEOMS.size(); i++){

			Tools.progressPercentage(i, GEOMS.size(), MapMatching.gui_mode);

			// Get geometry
			Geometry g = GEOMS.get(i);

			Point p1 = gf.createPoint(g.getCoordinates()[0]);
			Point p2 = gf.createPoint(g.getCoordinates()[g.getCoordinates().length-1]);

			Geometry buffer1 = p1.buffer(Parameters.topo_tolerance);
			Geometry buffer2 = p2.buffer(Parameters.topo_tolerance);

			// Get surrounding points
			@SuppressWarnings("unchecked")
			ArrayList<Integer> C1 = (ArrayList<Integer>) ix.query(buffer1.getEnvelopeInternal());

			// Get surrounding points
			@SuppressWarnings("unchecked")
			ArrayList<Integer> C2 = (ArrayList<Integer>) ix.query(buffer2.getEnvelopeInternal());


			// Research for source
			for (int j=0; j<C1.size(); j++) {

				if (PREP_GEOMS.get(C1.get(j)).intersects(buffer1)) {

					int merge_node = Math.min(C1.get(j), 2*i);

					if (MERGE.containsKey(C1.get(j))){

						merge_node = Math.min(merge_node, MERGE.get(C1.get(j)));

					}

					if (MERGE.containsKey(2*i)){

						merge_node = Math.min(merge_node, MERGE.get(2*i));

					}

					MERGE.put(C1.get(j), merge_node);
					MERGE.put(2*i,       merge_node);

				}

			}

			// Research for target
			for (int j=0; j<C2.size(); j++) {

				if (PREP_GEOMS.get(C2.get(j)).intersects(buffer2)) {

					int merge_node = Math.min(C2.get(j), 2*i+1);

					if (MERGE.containsKey(C2.get(j))){

						merge_node = Math.min(merge_node, MERGE.get(C2.get(j)));

					}

					if (MERGE.containsKey(2*i+1)){

						merge_node = Math.min(merge_node, MERGE.get(2*i+1));

					}

					MERGE.put(C2.get(j), merge_node);
					MERGE.put(2*i+1,     merge_node);

				}

			}

		}


		// Merge of nodes
		for (int i=0; i<SOURCES.size(); i++) {

			// ------------------------------------------
			// For sources
			// ------------------------------------------

			int key = Integer.parseInt(SOURCES.get(i));

			while ((MERGE.containsKey(key)) && (MERGE.get(key) != key)) {

				key = MERGE.get(key);

			}


			SOURCES.set(i, key+"");


			// ------------------------------------------
			// For targets
			// ------------------------------------------

			key = Integer.parseInt(TARGETS.get(i));

			while ((MERGE.containsKey(key)) && (MERGE.get(key) != key)) {

				key = MERGE.get(key);

			}

			TARGETS.set(i, key+"");

		}


		// Simplify node names 
		Hashtable<String, Integer> RENAME = new Hashtable<String, Integer>();

		int counter = 1;

		for (int i=0; i<SOURCES.size(); i++){

			if (!RENAME.containsKey(SOURCES.get(i))){

				RENAME.put(SOURCES.get(i), counter); counter ++;

			}

			if (!RENAME.containsKey(TARGETS.get(i))){

				RENAME.put(TARGETS.get(i), counter); counter ++;

			}

		}


		nodeNumber = 0;
		verticeNumber = 0;

		NODES = new Hashtable<String, ArrayList<String>>();
		EDGES = new Hashtable<String, Integer>();

		for (int i=0; i<SOURCES.size(); i++){

			String source = SOURCES.get(i);
			String target = TARGETS.get(i);

			SOURCES.set(i, RENAME.get(source)+"");
			TARGETS.set(i, RENAME.get(target)+"");

			source = SOURCES.get(i);
			target = TARGETS.get(i);

			// -------------------------------------------
			// Register source of edge
			// -------------------------------------------
			if (!NODES.containsKey(source)){

				nodeNumber ++;
				NODES.put(source, new ArrayList<String>());

			}

			NODES.get(source).add(target);

			// -------------------------------------------
			// Register target of edge
			// -------------------------------------------
			if (!NODES.containsKey(target)){

				nodeNumber ++;
				NODES.put(target, new ArrayList<String>());

			}

			NODES.get(target).add(source);

			// Count number of vertices
			verticeNumber += GEOMS.get(i).getCoordinates().length;

			// -------------------------------------------
			// Register edge
			// -------------------------------------------
			EDGES.put(source+"->"+target, i);
			EDGES.put(target+"->"+source, i);

		}


		makeGraph();


		// Progress bar
		Tools.progressPercentage(GEOMS.size(), GEOMS.size(), MapMatching.gui_mode);

		// End message
		Tools.println("Topology has been build with success : "+nodeNumber+" nodes, "+SOURCES.size()+" edges");

	}

	// -----------------------------------------------------
	// Build Graph object from network data
	// -----------------------------------------------------
	public void makeGraph(){

		Graph.Edge[] graph = new Graph.Edge[2*SOURCES.size()];


		for (int i=0; i<SOURCES.size(); i++){

			double direct_weight = Double.MAX_VALUE;
			double reverse_weight = Double.MAX_VALUE;
			
			if (ONE_WAY.get(i) == 0){
			
				direct_weight = WEIGHTS.get(i);
				reverse_weight = WEIGHTS.get(i);
			
			}

			if (ONE_WAY.get(i) == -1){

				reverse_weight = WEIGHTS.get(i);

			}
			
			if (ONE_WAY.get(i) == 1){

				direct_weight = WEIGHTS.get(i);

			}

			graph[2*i] = new Graph.Edge(SOURCES.get(i), TARGETS.get(i), direct_weight);
			graph[2*i+1] = new Graph.Edge(TARGETS.get(i), SOURCES.get(i), reverse_weight);


		}


		topology = new Graph(graph);

	}

	// -----------------------------------------------------
	// Get distance between 2 nodes (possibly precomputed)
	// -----------------------------------------------------
	public double getGenericDistance(String startNode, String endNode){

		if (DISTANCES == null){

			topology.dijkstra(startNode, endNode);

			return getPathLength(endNode);

		}

		return getPrecomputedDistance(startNode, endNode);

	}

	// -----------------------------------------------------
	// Get distance between 2 nodes
	// -----------------------------------------------------
	public double getPrecomputedDistance(String startNode, String endNode){
		
		// Enumeration<String> E = DISTANCES.keys();
	        
	    //  while(E.hasMoreElements()){int[] tab = E.nextElement(); System.out.println(tab[0]+","+tab[1]+" : "+DISTANCES.get(tab));}
	      
	    

		 if (DISTANCES == null){

	            Tools.println("Error: network must be prepared before calling precomputed distances");
	            System.exit(5);

	        }

	        int start = NODES_LIST.get(startNode);
	        int end = NODES_LIST.get(endNode);

	        String key = start+"-"+end;
	       
	        if (start == end){
	           
	            return 0.0;
	           
	        }
	       
	        Double dist = DISTANCES.get(key);
	       
	        
	        if (dist == null) {
	           
	            return Math.pow(10, 10);
	           
	        }
	        
	        return dist;

	}


	// -----------------------------------------------------
	// Get last node between 2 nodes (possibly precomputed)
	// -----------------------------------------------------
	public String getGenericLastEdge(String startNode, String endNode){

		if (LAST_EDGE == null){

			topology.dijkstra(startNode, endNode);

			ArrayList<String> PATH = topology.getPath(endNode);

			if (PATH.size() > 1){

				return PATH.get(PATH.size()-2);

			}

			return "";

		}

		return getPrecomputedLastEdge(startNode, endNode);
	}


	// -----------------------------------------------------
	// Get precomputed last node between 2 nodes
	// -----------------------------------------------------
	public String getPrecomputedLastEdge(String startNode, String endNode){

		 if (LAST_EDGE == null){

	            Tools.println("Error: network must be prepared before calling precomputed last edge");
	            System.exit(5);

	        }

	        int start = NODES_LIST.get(startNode);
	        int end = NODES_LIST.get(endNode);

	        String key = start+"-"+end;
	        return LAST_EDGE.get(key);

	}


	// -----------------------------------------------------
	// Operate complete Dijkstra from source
	// -----------------------------------------------------
	public void dijkstra(String startNode){

		topology.dijkstra(startNode);

	}

	// -----------------------------------------------------
	// Operate Dijkstra from source to a certain distance
	// -----------------------------------------------------
	public void dijkstra(String startNode, double distance){

		topology.dijkstra(startNode, distance);

	}

	// -----------------------------------------------------
	// Operate Dijkstra algorithm between two nodes
	// -----------------------------------------------------
	public void dijkstra(String startNode, String endNode){

		topology.dijkstra(startNode, endNode);

	}

	// -----------------------------------------------------
	// Get list of edge of a given path
	// -----------------------------------------------------
	public ArrayList<Integer> getPathEdges(String endNode){

		return topology.getPathEdges(endNode, EDGES);

	}

	// -----------------------------------------------------
	// Length of path (in coordinates units)
	// -----------------------------------------------------
	public double getPathLength(String endNode){

		return topology.getPathLength(endNode);

	}


	 // -----------------------------------------------------
    // Precompute distances and paths on network
    // -----------------------------------------------------
    // This method should be called only if the network is
    // of reasonable size (typically less than 50k nodes)
    // -----------------------------------------------------
    public void prepare(){
    	
    	Tools.println("Precomputing distances on network...");

        // ----------------------------------------------
        // Preparing table of nodes
        // ----------------------------------------------
        ArrayList<String> KEYS = new ArrayList<String>();

        for (String key : NODES.keySet()){

            KEYS.add(key);

        }

        NODES_LIST = new Hashtable<String, Integer>();

        for (int i=0; i<KEYS.size(); i++){

            NODES_LIST.put(KEYS.get(i), i);

        }

        int n = KEYS.size();

        DISTANCES = new Hashtable<String, Double>();
        LAST_EDGE = new Hashtable<String, String>();

        // ----------------------------------------------
        // Computing distances
        // ----------------------------------------------


        int change = Math.max(n/Tools.maxBareSize,1);
       
        for (int i=0; i<n; i++){

            dijkstra(KEYS.get(i), Parameters.buffer_radius);

            if ((i % change == 0) || (MapMatching.gui_mode)){
                Tools.progressPercentage(i, n, MapMatching.gui_mode);
            }

            for (int j=0; j<n; j++){
            	
                String key = i+"-"+j;
           
                double dist = Math.floor(getPathLength(KEYS.get(j))*100)/100;
                
                if (dist > Parameters.buffer_radius) {continue;}

                DISTANCES.put(key, dist);
               
                String key2 = j+"-"+i;
               
                if (Parameters.sort_nodes){

                    ArrayList<String> PATH = topology.getPath(KEYS.get(j));


                    if (PATH.size() > 1){

                        LAST_EDGE.put(key, PATH.get(PATH.size()-2));

                    }else{

                        LAST_EDGE.put(key, "");
                        LAST_EDGE.put(key2, "");

                    }

                }


            }

        }
        
       // Enumeration<int[]> E = DISTANCES.keys();
        
      //  while(E.hasMoreElements()){int[] tab = E.nextElement(); System.out.println(tab[0]+","+tab[1]+" : "+DISTANCES.get(tab));}
        

        Tools.progressPercentage(n, n, MapMatching.gui_mode);
        Tools.println(n*n +" distances have been computed with success");
       
    }
    
	// ----------------------------------------------
	// Name of edges (if defined)
	// ----------------------------------------------
	public String getEdgeName(int index){

		if (EDGE_NAMES.size() == 0){
			return ""+(index+1);
		}

		return EDGE_NAMES.get(index);

	}

	// ----------------------------------------------
	// Module to project coordinates
	// ----------------------------------------------
	public int toLocalMercator(){

		// Correct operation test
		boolean correct_transfo = true;

		// Origine for projection algorithm
		Tools.setProjectionOrigine(Tools.barycenter(this));

		// Origine of cartographic coordinates
		Tools.setCartoOrigine(Tools.geo2mercator(Tools.lowerLeft(this)));

		// For each polyline
		for (int i=0; i<GEOMS.size(); i++){

			// For each coordinate in the polyline
			for (int j=0; j<GEOMS.get(i).getCoordinates().length; j++){

				// Local mercator distorsion
				Tools.addDistorsion(GEOMS.get(i).getCoordinates()[j]);

				// Local mercator projection
				correct_transfo = Tools.toMercator(GEOMS.get(i).getCoordinates()[j]);

				if (!correct_transfo){

					return -1;

				}

			}

			// Updating weights
			WEIGHTS.set(i, GEOMS.get(i).getLength());

		}

		// Rebuilding topology
		makeGraph();

		return 0;

	}

	// ----------------------------------------------
	// Module to compute inverse projection
	// ----------------------------------------------
	public void toWGS84(){

		// For each polyline
		for (int i=0; i<getGeometries().size(); i++){

			// For each coordinate in the polyline
			for (int j=0; j<getGeometries().get(i).getCoordinates().length; j++){

				// Local mercator projection
				Tools.toGeo(getGeometries().get(i).getCoordinates()[j]);

			}

			// Updating weights
			getWeights().set(i, getGeometries().get(i).getLength());

		}

		// Rebuilding topology
		makeGraph();

	}


	// ----------------------------------------------
	// Module to get distance factor
	// ----------------------------------------------
	public double distanceFactor(double d){

		Coordinate c = Tools.mercator2geo(Tools.barycenter(this));

		Coordinate cn = new Coordinate(c.x, c.y+d);
		Coordinate ce = new Coordinate(c.x+d, c.y);

		Coordinate p = Tools.geo2mercator(c);
		Coordinate pn = Tools.geo2mercator(cn);
		Coordinate pe = Tools.geo2mercator(ce);

		double fn = Math.sqrt((pn.x-p.x)*(pn.x-p.x)+(pn.y-p.y)*(pn.y-p.y));
		double fe = Math.sqrt((pe.x-p.x)*(pe.x-p.x)+(pe.y-p.y)*(pe.y-p.y));

		double dp = 0.5*(fn+fe);

		return dp;

	}


	// ----------------------------------------------
	// Module to print network in a file
	// ----------------------------------------------
	public void print(String path){

		PrintWriter writer = null;

		(new File(path)).delete();

		try {
			writer = new PrintWriter(new File(path));
		} catch (FileNotFoundException e) {

			Tools.println("Error: cannot print network");
			System.exit(13);

		}

		String chaine = "wkt,";

		if (EDGE_NAMES.size() != 0){chaine += "gid,";}

		chaine += "source,target\r\n";

		writer.print(chaine);

		for (int i=0; i<getGeometries().size(); i++){

			chaine = "";
			chaine += "\""+getGeometries().get(i)+"\""+",";

			if (EDGE_NAMES.size() != 0){
				chaine += EDGE_NAMES.get(i)+",";
			}

			chaine += getSources().get(i)+",";
			chaine += getTargets().get(i)+"\r\n";

			writer.print(chaine);

		}

		writer.close();

	}

	// -----------------------------------------------------
	// Method to print network
	// -----------------------------------------------------
	public void printInFile(String path){

		PrintWriter pw = null;

		try {
			pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(path))));
		} catch (IOException e) {

			System.out.println("Error: impossible to print network");
			System.exit(19);

		}

		StringBuilder output = new StringBuilder();

		output.append("link_id");      output.append(Parameters.output_delimiter);
		output.append("wkt");          output.append(Parameters.output_delimiter);
		output.append("source");       output.append(Parameters.output_delimiter);
		output.append("target");       output.append(Parameters.output_delimiter);
		output.append("one_way");      output.append("\r\n");

		pw.print(output.toString());

		for (int i=0; i<GEOMS.size(); i++){

			output = new StringBuilder();

			output.append(getEdgeName(i));               output.append(Parameters.output_delimiter);
			output.append("\""+GEOMS.get(i)+"\"");       output.append(Parameters.output_delimiter);
			output.append(SOURCES.get(i));               output.append(Parameters.output_delimiter);
			output.append(TARGETS.get(i));               output.append(Parameters.output_delimiter);
			output.append(ONE_WAY.get(i));       		 output.append("\r\n");

			pw.print(output.toString());

		}

		pw.close();

		Tools.println("Network file "+path.replace("\\", "/")+" : ok");

	}

	// -----------------------------------------------------
	// Method to print index
	// -----------------------------------------------------
	public void printIndex(String path){
		
		String delim2 = ";";
		
		if (Parameters.output_delimiter.equals(";")){
			
			delim2 = ",";
			
		}
		
		Tools.println("Printing index...");
		
		if (MapMatching.gui_mode){
			Main.gui.label_17.setText("Printing index...");
		}


		PrintWriter pw = null;

		try {

			pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(path))));

		} catch (IOException e) {

			System.out.println("Error: cannot print index");
			System.exit(19);

		}
		
		StringBuilder output = new StringBuilder();
		
		// Printing track names
		for (int i=0; i<Parameters.input_track_path_list.size(); i++){
			
			output.append(new File(Parameters.input_track_path_list.get(i)).getName());
			
			if (i != Parameters.input_track_path_list.size()-1){
				
				output.append(Parameters.output_delimiter);
				
			}
			
			
		}
		
		pw.println(output.toString());


		for (int i=0; i<SYSTEM_TRACK.size(); i++){

			Tools.progressPercentage(i, SYSTEM_POINT.size(), MapMatching.gui_mode);

			// Print all edges
			if (!Parameters.output_index_all_edge){
				
				if (SYSTEM_TRACK.get(i).size() == 0){
					
					continue;
					
				}
				
			}

			output = new StringBuilder();

			output.append(getEdgeName(i)); output.append(Parameters.output_delimiter);

			for (int j=0; j<SYSTEM_POINT.get(i).size(); j++){

				output.append(SYSTEM_TRACK.get(i).get(j));  output.append(delim2);
				output.append(SYSTEM_POINT.get(i).get(j));   

				if (j != SYSTEM_POINT.get(i).size()-1){

					output.append(Parameters.output_delimiter);

				}

			}

			pw.println(output.toString());

		}

		pw.close();

		Tools.progressPercentage(SYSTEM_POINT.size(), SYSTEM_POINT.size(), MapMatching.gui_mode);

		Tools.println("Index file "+path.replace("\\", "/")+" : ok");
		
		if (MapMatching.gui_mode){
			Main.gui.label_17.setText("");
		}

	}


	// -----------------------------------------------------
	// Method to print index in xml format
	// -----------------------------------------------------
	public void printIndexInXml(String path){

		if (MapMatching.gui_mode){
			Main.gui.label_17.setText("Printing index...");
		}

		PrintWriter pw = null;

		try {

			pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(path))));

		} catch (IOException e) {

			System.out.println("Error: cannot print index");
			System.exit(19);

		}

		
		pw.println("<?xml version='1.0' encoding='UTF-8'?>");
		
		pw.println("<index>");
		
		pw.println("<!-- /////////////////////////////////////////");
		pw.println("       List of alias indices for tracks       ");
		pw.println("////////////////////////////////////////// -->");
		
		StringBuilder output = new StringBuilder();
		
		output.append("    <paths>\r\n");
		
		// Printing track names
		for (int i=0; i<Parameters.input_track_path_list.size(); i++){
			
			output.append("        <track id='"); 
			output.append(i); output.append("' path='");
			output.append(new File(Parameters.input_track_path_list.get(i)).getName());
			output.append("'/>\r\n");
			
		}
		
		output.append("    </paths>");
		
		pw.println(output.toString());
		
		pw.println("<!-- /////////////////////////////////////////");
		pw.println("    Indexing track points on network edges    ");
		pw.println("////////////////////////////////////////// -->");
		
		int track = 0;

		for (int i=0; i<SYSTEM_TRACK.size(); i++){
			
			Tools.progressPercentage(i, SYSTEM_POINT.size(), MapMatching.gui_mode);

			output = new StringBuilder();

			output.append("    <edge id='"+getEdgeName(i)+"' count='"+SYSTEM_TRACK.get(i).size()+"'>");
			
			if (SYSTEM_TRACK.get(i).size() == 0){
				
				output.append("    </edge>\r\n");
				
				// Print all edges
				if (Parameters.output_index_all_edge){
					
					pw.print(output.toString());
					
				}
				
				continue;
				
			}
			else{
				
				output.append("\r\n");
				
			}
			
			track = SYSTEM_TRACK.get(i).get(0);
			output.append("        <track id='"+track+"'>\r\n");
		
			for (int j=0; j<SYSTEM_TRACK.get(i).size(); j++){
				
				if (SYSTEM_TRACK.get(i).get(j) != track){
					
					track = SYSTEM_TRACK.get(i).get(j);
					
					output.append("        </track>\r\n");
					output.append("        <track id='"+track+"'>\r\n");
					
				}
				
				if (Parameters.output_index_coords){
					
					output.append("            <point id='"+SYSTEM_POINT.get(i).get(j)+"'>\r\n"); 
					output.append("                <xraw>"+SYSTEM_XRAW.get(i).get(j)+"</xraw>\r\n"); 
					output.append("                <yraw>"+SYSTEM_YRAW.get(i).get(j)+"</yraw>\r\n"); 
					output.append("                <abs>"+SYSTEM_ABS.get(i).get(j)+"</abs>\r\n"); 
					output.append("                <time>"+SYSTEM_T.get(i).get(j)+"</time>\r\n");
					output.append("            </point>\r\n"); 
					
				}
				else{
					
					output.append("            <point id='"+SYSTEM_POINT.get(i).get(j)+"'/>\r\n"); 
					
				}

			}
			
			output.append("        </track>\r\n"); 
			output.append("    </edge>\r\n"); 

			pw.print(output.toString());

		}
		
		pw.println("</index>");

		pw.close();

		Tools.progressPercentage(SYSTEM_POINT.size(), SYSTEM_POINT.size(), MapMatching.gui_mode);

		Tools.println("Index file "+path.replace("\\", "/")+" : ok");
		
		if (MapMatching.gui_mode){
			Main.gui.label_17.setText("");
		}

	}


	// -----------------------------------------------------
	// Module to remove degree 2 nodes in a network
	// -----------------------------------------------------
	public void removeDegree2Nodes(){

		Tools.println("Removing degree 2 nodes...");

		int initial_node_number = nodeNumber;
		int initial_edge_number = SOURCES.size();
		int initial_vertex_number = verticeNumber;

		// -------------------------------------------------
		// Search of degree 2 nodes
		// -------------------------------------------------

		Hashtable<String, Integer> SOMMETS = new Hashtable<String, Integer>();

		for (int i=0; i<SOURCES.size(); i++){

			if (!SOMMETS.containsKey(SOURCES.get(i))){SOMMETS.put(SOURCES.get(i), 0);}
			if (!SOMMETS.containsKey(TARGETS.get(i))){SOMMETS.put(TARGETS.get(i), 0);}

			SOMMETS.put(SOURCES.get(i), SOMMETS.get(SOURCES.get(i))+1);
			SOMMETS.put(TARGETS.get(i), SOMMETS.get(TARGETS.get(i))+1);

		}

		Enumeration<String> list_of_sommets = SOMMETS.keys();
		ArrayList<String> DEG_2_NODES = new ArrayList<String>();

		while(list_of_sommets.hasMoreElements()){

			String node = list_of_sommets.nextElement();

			if (SOMMETS.get(node) == 2){

				DEG_2_NODES.add(node);
				
			}

		}
		
		Hashtable<Integer, Integer> TO_REMOVE = new Hashtable<Integer, Integer>();


		for (int i=0; i<DEG_2_NODES.size(); i++){

			//	System.out.println(i+" "+DEG_2_NODES.get(i));

			ArrayList<String> VOISINS = NODES.get(DEG_2_NODES.get(i));


			String edge11 = VOISINS.get(0)+"->"+DEG_2_NODES.get(i);
			String edge12 = DEG_2_NODES.get(i)+"->"+VOISINS.get(0);

			int nedge1 = 0;

			if (EDGES.containsKey(edge11)){

				nedge1 = EDGES.get(edge11);
				EDGES.remove(edge11);

			}
			else{

				nedge1 = EDGES.get(edge12);
				EDGES.remove(edge12);

			}

			String edge21 = VOISINS.get(1)+"->"+DEG_2_NODES.get(i);
			String edge22 = DEG_2_NODES.get(i)+"->"+VOISINS.get(1);

			int nedge2 = 0;

			if (EDGES.containsKey(edge21)){

				nedge2 = EDGES.get(edge21);
				EDGES.remove(edge21);

			}
			else{
				if (EDGES.containsKey(edge22)){
					nedge2 = EDGES.get(edge22);
					EDGES.remove(edge22);
				}

			}

			SOURCES.add(VOISINS.get(0));
			TARGETS.add(VOISINS.get(1));
			
			NODES.get(VOISINS.get(0)).add(VOISINS.get(1));
			NODES.get(VOISINS.get(1)).add(VOISINS.get(0));
			NODES.get(VOISINS.get(0)).remove(DEG_2_NODES.get(i));
			NODES.get(VOISINS.get(1)).remove(DEG_2_NODES.get(i));
			
			EDGES.put(VOISINS.get(0)+"->"+VOISINS.get(1), GEOMS.size());


			//	System.out.println(SOURCES.get(nedge1)+" -> "+TARGETS.get(nedge1));
			//	System.out.println(SOURCES.get(nedge2)+" -> "+TARGETS.get(nedge2));

			//	System.out.println(GEOMS.get(nedge1));
			//	System.out.println(GEOMS.get(nedge2));

			if (SOURCES.get(nedge1).equals(DEG_2_NODES.get(i))){

				GEOMS.set(nedge1, Tools.reverse(GEOMS.get(nedge1)));

			}

			if (!SOURCES.get(nedge2).equals(DEG_2_NODES.get(i))){

				GEOMS.set(nedge2, Tools.reverse(GEOMS.get(nedge2)));

			}


			GEOMS.add(Tools.merge(GEOMS.get(nedge1), GEOMS.get(nedge2)));
			WEIGHTS.add(GEOMS.get(GEOMS.size()-1).getLength());
			
			TO_REMOVE.put(nedge1, 0);
			TO_REMOVE.put(nedge2, 0);
			

			//	System.out.println(GEOMS.get(GEOMS.size()-1));


			//	System.out.println("-----------------------------");

		}


		// Remove useless edges

		

		for (int i=SOURCES.size()-1; i>=0; i--){

			if (TO_REMOVE.containsKey(i)){

				SOURCES.remove(i);
				TARGETS.remove(i);
				GEOMS.remove(i);
				WEIGHTS.remove(i);

			}

		}


		// -------------------------------------------------
		// Rebuild topology
		// -------------------------------------------------

		EDGE_NAMES = new ArrayList<String>();

		NODES = new Hashtable<String, ArrayList<String>>();

		nodeNumber = 0;

		for (int i=0; i<SOURCES.size(); i++) {

			String source = SOURCES.get(i);
			String target = TARGETS.get(i);

			if (!NODES.containsKey(source)){

				nodeNumber ++;
				NODES.put(source, new ArrayList<String>());

			}

			if (!NODES.containsKey(target)){

				nodeNumber ++;
				NODES.put(target, new ArrayList<String>());

			}

			NODES.get(target).add(source);

		}


		verticeNumber = 0;

		for (int i=0; i<GEOMS.size(); i++){

			verticeNumber += GEOMS.get(i).getCoordinates().length-2;

		}
		
		EDGES = new Hashtable<String, Integer>();
		
		for (int i=0; i<SOURCES.size(); i++){
			
			EDGES.put(SOURCES.get(i)+"->"+TARGETS.get(i), i);
			
		}

		makeGraph();

		int new_number_of_nodes = nodeNumber;
		int new_number_of_edges = SOURCES.size();
		int new_number_of_vertices = verticeNumber;

		reduc_node = (int)(-100*(double)(initial_node_number-new_number_of_nodes)/initial_node_number);
		reduc_edge = (int)(-100*(double)(initial_edge_number-new_number_of_edges)/initial_edge_number);
		reduc_vertex = (int)(-100*(double)(initial_vertex_number-new_number_of_vertices)/initial_vertex_number);

		Tools.println("Network has been simplified with success:");
		Tools.print("Edges = "+SOURCES.size()+" ["+reduc_edge+" %]  ");
		Tools.print("Nodes = "+nodeNumber+" ["+reduc_node+" %]  ");
		Tools.println("Vertices = "+verticeNumber+" [+"+reduc_vertex+" %]");

	}
	
}
