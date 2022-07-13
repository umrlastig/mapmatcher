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
package fr.umr.lastig.mapmatcher.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.StringTokenizer;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.prep.PreparedGeometry;
import org.locationtech.jts.geom.prep.PreparedGeometryFactory;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.simplify.DouglasPeuckerSimplifier;

import fr.umr.lastig.mapmatcher.network.Network;
import fr.umr.lastig.mapmatcher.core.MapMatching;
import fr.umr.lastig.mapmatcher.core.Track;




public class Loaders {

	// Network containers
	private static ArrayList<String> SOURCES;
	private static ArrayList<String> TARGETS;
	private static ArrayList<Double> WEIGHTS;
	private static ArrayList<Geometry> GEOMS;
	private static Hashtable<String, ArrayList<String>> NODES;
	private static Hashtable<String,Integer> EDGES;
	private static ArrayList<String> EDGE_NAMES;
	private static ArrayList<Integer> ONE_WAY;


	// Delimiters
	public static String columns_delimiter_network = "";
	public static String columns_delimiter_track = "";

	// Column indices for network
	public static int wkt_id = -1;
	public static int source_id = -1;
	public static int target_id = -1;
	public static int edge_id = -1;
	public static int oneway_id = -1;

	// Column names for network
	public static String geom_column_name = "";
	public static String source_column_name = "";
	public static String target_column_name = "";
	public static String edge_column_name = "";
	public static String oneway_column_name = "";

	// Columns indices for track
	public static int columns_x_id = -1;
	public static int columns_y_id = -1;
	public static int columns_t_id = -1;

	// Columns names for track
	public static String columns_x_name = "";
	public static String columns_y_name = "";
	public static String columns_t_name = "";

	// Headers
	public static boolean track_header = true;
	public static boolean network_header = true;

	// Date format in GPS trace
	public static String dateFmt = "";

	// GPS error code
	public static String gps_error_code = "";

	// Number of wrong points
	public static int excluded_points_number;

	// Excluded data
	public static ArrayList<String> EXCLUDED_X;
	public static ArrayList<String> EXCLUDED_Y;
	public static ArrayList<String> EXCLUDED_T;
	public static ArrayList<Integer> EXCLUDED_I;

	// Network bounding box
	public static double xmin = 0.0;
	public static double xmax = 0.0;
	public static double ymin = 0.0;
	public static double ymax = 0.0;

	// Network buffering
	private static PreparedGeometry buffer = null;

	// -------------------------------------------------------------------
	// Module for for setting map-matching parameters
	// -------------------------------------------------------------------
	public static void parameterize(){

		Loaders.columns_delimiter_network = Parameters.network_delimiter;
		Loaders.columns_delimiter_track = Parameters.track_delimiter;

		Loaders.wkt_id = Parameters.network_geom_id;
		Loaders.source_id = Parameters.network_source_id;
		Loaders.target_id = Parameters.network_target_id;
		Loaders.edge_id = Parameters.network_edge_id;
		Loaders.oneway_id = Parameters.network_oneway_id;

		Loaders.geom_column_name = Parameters.network_geom_name;
		Loaders.source_column_name = Parameters.network_source_name;
		Loaders.target_column_name = Parameters.network_target_name;
		Loaders.edge_column_name = Parameters.network_edge_name;
		Loaders.oneway_column_name = Parameters.network_oneway_name;

		Loaders.track_header = Parameters.track_header;
		Loaders.network_header = Parameters.network_header;

		Loaders.columns_x_id = Parameters.track_columns_x_id;
		Loaders.columns_y_id = Parameters.track_columns_y_id;
		Loaders.columns_t_id = Parameters.track_columns_t_id;

		Loaders.columns_x_name = Parameters.track_columns_x_name;
		Loaders.columns_y_name = Parameters.track_columns_y_name;
		Loaders.columns_t_name = Parameters.track_columns_t_name;

		Loaders.dateFmt = Parameters.track_date_fmt;

		Loaders.gps_error_code = Parameters.track_error_code;

		// Initialization

		excluded_points_number = 0;

		EXCLUDED_I = new ArrayList<Integer>();
		EXCLUDED_T = new ArrayList<String>();
		EXCLUDED_X = new ArrayList<String>();
		EXCLUDED_Y = new ArrayList<String>();

	}
	
	public static void parameterize2(){

		Loaders.columns_delimiter_network = ",";


		Loaders.geom_column_name = "wkt";
		Loaders.source_column_name = "source";
		Loaders.target_column_name =  "target";
		Loaders.edge_column_name = "link_id";
		
		Loaders.network_header = true;
		
		// Initialization

		excluded_points_number = 0;

		EXCLUDED_I = new ArrayList<Integer>();
		EXCLUDED_T = new ArrayList<String>();
		EXCLUDED_X = new ArrayList<String>();
		EXCLUDED_Y = new ArrayList<String>();

	}

	// ----------------------------------------------
	// Add buffer to loaders
	// ----------------------------------------------
	public static void setBuffer(Geometry buffer){

		Loaders.buffer = PreparedGeometryFactory.prepare(DouglasPeuckerSimplifier.simplify(buffer, 0.33*Parameters.buffer_radius));

	}


	// ----------------------------------------------
	// Reinitialize buffer
	// ----------------------------------------------
	public static void setBufferNull(){

		Loaders.buffer = null;

	}

	// ----------------------------------------------
	// Module to load GPS track without header
	// ----------------------------------------------
	@SuppressWarnings("resource")
	public static Track loadTrack(String path){

		Scanner scan = null;

		// Load data
		try {scan = new Scanner(new File(path));} 
		catch (FileNotFoundException e) {

			Tools.println("Error: no track file ["+path+"]");
			if (MapMatching.gui_mode){
				return null;
			}else{
				System.exit(6);
			}

		}

		// Initialize
		ArrayList<Double> X = new ArrayList<Double>();
		ArrayList<Double> Y = new ArrayList<Double>();
		ArrayList<String> T = new ArrayList<String>();
		ArrayList<Long> Tms = new ArrayList<Long>();

		// Date converter
		SimpleDateFormat df = new SimpleDateFormat(dateFmt);

		// Header
		if (track_header){

			String header = scan.nextLine();

			StringTokenizer st = new StringTokenizer(header, columns_delimiter_track);

			int counter = 1;

			while(st.hasMoreTokens()){

				String col_name = st.nextToken();

				if (col_name.equals(columns_x_name))    {columns_x_id = counter;}
				if (col_name.equals(columns_y_name))    {columns_y_id = counter;}
				if (col_name.equals(columns_t_name))    {columns_t_id = counter;}

				counter ++;

			}


			if (columns_x_id == -1){

				Tools.printError("Error : x coordinate column \""+columns_x_name+"\" has not been found in network input file");
				if (MapMatching.gui_mode){
					return null;
				}else{
					System.exit(1);
				}

			}

			if (columns_y_id == -1){

				Tools.printError("Error : y coordinate column \""+columns_y_name+"\" has not been found in network input file");
				if (MapMatching.gui_mode){
					return null;
				}else{
					System.exit(1);
				}

			}


			if ((columns_t_id == -1) && (!columns_t_name.equals(""))){

				if (!MapMatching.gui_mode){

					Tools.printError("Error : timestamp column \""+columns_t_name+"\" has not been found in network input file");
					System.exit(1);

				}

			}

		}


		// Reading file

		int counter = 0;

		while (scan.hasNextLine()){

			String line = scan.nextLine();


			if (line.equals("")){continue;}

			StringTokenizer st1 = new StringTokenizer(line, columns_delimiter_track);
			StringTokenizer st2 = new StringTokenizer(line, columns_delimiter_track);
			StringTokenizer st3 = new StringTokenizer(line, columns_delimiter_track);


			String x = "";
			String y = "";
			String t = "no_time_stamp";

			try{

				for (int i=0; i<columns_x_id-1; i++){

					st1.nextToken(columns_delimiter_track);

				}

				for (int i=0; i<columns_y_id-1; i++){

					st2.nextToken(columns_delimiter_track);

				}

				x = st1.nextToken(columns_delimiter_track);
				y = st2.nextToken(columns_delimiter_track);


				if (columns_t_id != -1){


					for (int i=0; i<columns_t_id-1; i++){st3.nextToken(columns_delimiter_track);}

					t = st3.nextToken(columns_delimiter_track);

				}

			}
			catch(NoSuchElementException e){

				if (track_header){counter ++;}
				Tools.printError("Error: line "+(counter+1)+" is inconsistent with data specifications in input track file:\r\n"+path);
				return null;

			}

			if ((x.equals(gps_error_code)) || (y.equals(gps_error_code)) || (t.equals(gps_error_code))){

				excluded_points_number ++;

				EXCLUDED_I.add(X.size()+1);
				EXCLUDED_T.add(t);
				EXCLUDED_X.add(x);
				EXCLUDED_Y.add(y);

				continue;

			}

			try{

				X.add(Double.parseDouble(x));

			}catch(NumberFormatException e){

				Tools.printError("\""+x+"\" is not a valid entry for X coordinate");

				if (MapMatching.gui_mode){
					return null;
				}
				else{
					System.exit(15);
				}


			}

			try{

				Y.add(Double.parseDouble(y));

			}catch(NumberFormatException e){

				Tools.printError("\""+y+"\" is not a valid entry for Y coordinate");


				if (MapMatching.gui_mode){
					return null;
				}
				else{
					System.exit(15);
				}

			}




			T.add(t);

			if (columns_t_id != -1){

				Date date = null;
				try {

					date = df.parse(T.get(T.size()-1));

				} 
				catch (java.text.ParseException e) {

					Tools.printError("Error: inconsistent date format ["+t+"]");

					if (MapMatching.gui_mode){
						return null;
					}
					else{
						System.exit(7);
					}

				}

				Tms.add(date.getTime());
			}
			else{

				Tms.add((long)counter*1000);

			}

			counter ++;

		}

		// Create output
		Track gps = new Track(X, Y, T, Tms, path);

		return gps;

	}

	// ----------------------------------------------
	// Generic Module to load road network
	// ----------------------------------------------
	public static Network loadNetwork(String path){

		// Special case OSM file
		StringTokenizer st = new StringTokenizer(path, ".");
		st.nextToken(".");

		if (st.nextToken(".").equals("osm")){

			return loadNetworkFromOSM(path);

		}

		return loadNetworkFromCSV(path);

	}


	// ----------------------------------------------
	// Module to load road network from CSV file
	// ----------------------------------------------
	@SuppressWarnings("resource")
	public static Network loadNetworkFromCSV(String path){

		// Variables
		String line;
		StringTokenizer st;
		Scanner scan = null;

		xmin = Double.MAX_VALUE;
		ymin = Double.MAX_VALUE;
		xmax = Double.MIN_VALUE;
		ymax = Double.MIN_VALUE;

		WKTReader reader = new WKTReader();


		// Initialization

		Network NETWORK = new Network();

		int nodeNumber = 0;
		int verticeNumber = 0;

		NETWORK.setPath(path);

		SOURCES = new ArrayList<String>();
		TARGETS  = new ArrayList<String>();
		WEIGHTS  = new ArrayList<Double>();
		GEOMS = new ArrayList<Geometry>();
		NODES = new Hashtable<String, ArrayList<String>>();
		EDGES = new Hashtable<String, Integer>();
		EDGE_NAMES = new ArrayList<String>();
		ONE_WAY =  new ArrayList<Integer>();

		// Load file
		try {scan = new Scanner(new File(path));} 
		catch (FileNotFoundException e) {

			Tools.printError("Error: no network file ["+path+"]");

			if (MapMatching.gui_mode){
				return null;
			}
			else{
				System.exit(6);
			}


		}

		// ------------------------------------------
		// Headline analysis
		// ------------------------------------------
		if (network_header) {


			int counter = 1;

			line = scan.nextLine();

			st = new StringTokenizer(line, columns_delimiter_network);

			while(st.hasMoreTokens()){

				String col_name = st.nextToken();
				
				if (col_name.equals(geom_column_name))   {wkt_id = counter;}
				if (col_name.equals(source_column_name)) {source_id = counter;}
				if (col_name.equals(target_column_name)) {target_id = counter;}
				if (col_name.equals(edge_column_name))   {edge_id = counter;}
				if (col_name.equals(oneway_column_name)) {oneway_id = counter;}


				counter ++;

			}


			if (wkt_id == -1){

				Tools.printError("Error : geometry column \""+geom_column_name+"\" has not been found in network input file");

				if (MapMatching.gui_mode){
					return null;
				}
				else{
					System.exit(1);
				}

			}

			if ((source_id == -1) && (!Parameters.make_topology)){

				Tools.printError("Error : source column \""+source_column_name+"\" has not been found in network input file");

				if (MapMatching.gui_mode){
					return null;
				}
				else{
					System.exit(1);
				}

			}

			if ((target_id == -1) && (!Parameters.make_topology)){

				Tools.printError("Error : target column \""+target_column_name+"\" has not been found in network input file");

				if (MapMatching.gui_mode){
					return null;
				}
				else{
					System.exit(1);
				}

			}

			if ((edge_id == -1) && (!edge_column_name.equals(""))){

				Tools.printError("Error : edge column \""+edge_column_name+"\" has not been found in network input file");

				if (MapMatching.gui_mode){
					return null;
				}
				else{
					System.exit(1);
				}

			}

			if ((oneway_id == -1) && (!oneway_column_name.equals(""))){

				Tools.printError("Error : one way column \""+oneway_column_name+"\" has not been found in network input file");

				if (MapMatching.gui_mode){
					return null;
				}
				else{
					System.exit(1);
				}

			}

		}


		// ------------------------------------------
		// File body analysis
		// ------------------------------------------


		wkt_id --;
		source_id --;
		target_id --;
		edge_id --;
		oneway_id --;

		Hashtable<String, Integer> EDGE_ID_NAMES = new Hashtable<String, Integer>();

		int max_id = Math.max(wkt_id, source_id);
		max_id = Math.max(max_id, target_id);
		max_id = Math.max(max_id, edge_id);
		max_id = Math.max(max_id, oneway_id);


		while(scan.hasNextLine()){

			line = scan.nextLine();

			if (line.equals("")){continue;}

			String[] values = line.replaceAll("^\"", "").split("\"?("+Parameters.network_delimiter+"|$)(?=(([^\"]*\"){2})*[^\"]*$) *\"?");

			if (max_id >= values.length){

				int ln = SOURCES.size()+1;

				if(network_header){ln ++;}

				Tools.printError("Error: line "+ln+" inconsistent with data specifications in network input file");
				return null;

			}



			if (!(buffer == null)){

				try {

					Geometry g = reader.read(values[wkt_id]);

					if (!Loaders.buffer.intersects(g)){continue;}

				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}


			String source = "";
			String target = "";

			if (!Parameters.make_topology){

				source = values[source_id];
				target = values[target_id];

			}
			else{

				source = (2*SOURCES.size())+"";
				target = (2*SOURCES.size()+1)+"";	

			}

			SOURCES.add(source);
			TARGETS.add(target);


			if (oneway_id > -1){

				if ((values[oneway_id].equals("inverse"))||(values[oneway_id].equals("-1"))){
					
					ONE_WAY.add(0);
					
				}
				else if ((values[oneway_id].equals("direct"))||(values[oneway_id].equals("1"))){
					
					ONE_WAY.add(1);
					
				}
				else{
					
					ONE_WAY.add(0);
					
				}

			}
			else{

				ONE_WAY.add(0);

			}

			if (edge_id > -1){

				if (EDGE_ID_NAMES.containsKey(values[edge_id])){

					System.out.println(values[edge_id]);

					Tools.printError("Error: network edge id must be unique");

					if (MapMatching.gui_mode){
						return null;
					}
					else{
						System.exit(15);
					}

				}
				else{

					EDGE_NAMES.add(values[edge_id]);
					EDGE_ID_NAMES.put(values[edge_id], 0);

				}

			}

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


			// -------------------------------------------
			// Register geometry
			// -------------------------------------------

			Geometry geom = null;

			try {

				geom = reader.read(values[wkt_id]);

				WEIGHTS.add(geom.getLength());

			} catch (ParseException e) {

				Tools.printError("Error : geometry column is not in WKT format");

				if (MapMatching.gui_mode){
					return null;
				}
				else{
					System.exit(15);
				}

			}

			GEOMS.add(geom);

			// Updating bounding box
			for (int i=0; i<geom.getCoordinates().length; i++){

				xmin = Math.min(xmin, geom.getCoordinates()[i].x);
				xmax = Math.max(xmax, geom.getCoordinates()[i].x);

				ymin = Math.min(ymin, geom.getCoordinates()[i].y);
				ymax = Math.max(ymax, geom.getCoordinates()[i].y);

			}


			// Count number of vertices
			verticeNumber += geom.getCoordinates().length-2;

			// -------------------------------------------
			// Register edge
			// -------------------------------------------
			EDGES.put(source+"->"+target, GEOMS.size()-1);
			EDGES.put(target+"->"+source, GEOMS.size()-1);

		}

		// -------------------------------------------
		// Return output network
		// -------------------------------------------

		NETWORK.setSources(SOURCES);
		NETWORK.setTargets(TARGETS);
		NETWORK.setWeights(WEIGHTS);
		NETWORK.setGeometries(GEOMS);
		NETWORK.setNodes(NODES);
		NETWORK.setEdges(EDGES);
		NETWORK.setEdgeNames(EDGE_NAMES);
		NETWORK.setOneWay(ONE_WAY);

		NETWORK.setNodeNumber(nodeNumber);
		NETWORK.setVerticeNumber(verticeNumber);


		// -------------------------------------------
		// Build topology
		// -------------------------------------------
		NETWORK.makeGraph();


		return NETWORK;

	}


	// ----------------------------------------------
	// Module to load road network from OSM file
	// ----------------------------------------------
	@SuppressWarnings("resource")
	public static Network loadNetworkFromOSM(String path){

		Network NETWORK = new Network();

		GeometryFactory gf = new GeometryFactory();

		int nodeNumber = 0;
		int verticeNumber = 0;

		NETWORK.setPath(path);

		SOURCES = new ArrayList<String>();
		TARGETS  = new ArrayList<String>();
		WEIGHTS  = new ArrayList<Double>();
		GEOMS = new ArrayList<Geometry>();
		NODES = new Hashtable<String, ArrayList<String>>();
		EDGES = new Hashtable<String, Integer>();
		EDGE_NAMES = new ArrayList<String>();
		ONE_WAY =  new ArrayList<Integer>();

		Hashtable<String, Coordinate> LIST_OF_NODES = new Hashtable<String, Coordinate>();


		BufferedReader br = null;

		try {

			br = new BufferedReader(new FileReader(path));

		} catch (FileNotFoundException e2) {

			Tools.printError("Error: no network file ["+path+"]");

			if (MapMatching.gui_mode){

				return null;
			}
			else{

				System.exit(6);

			}
		}

		String line = null;

		try {
			line = br.readLine();
		} catch (IOException e1) {

		}

		ArrayList<String> NODES_IN_WAY = new ArrayList<String>();

		String edge_name = "";

		boolean road = false;

		while(line != null){

			if (line.startsWith("  <node id=")){

				StringTokenizer st = new StringTokenizer(line, "\"");
				st.nextToken("\"");

				String node_id = st.nextToken("\"");

				st.nextToken("\"");
				double x = Double.parseDouble(st.nextToken("\""));

				st.nextToken("\"");
				double y = Double.parseDouble(st.nextToken("\""));

				NODES.put(node_id, new ArrayList<String>());
				LIST_OF_NODES.put(node_id, new Coordinate(x, y));

				nodeNumber ++;

			}


			if (line.startsWith("  <way id=")){

				road = false;

				NODES_IN_WAY = new ArrayList<String>();

				StringTokenizer st = new StringTokenizer(line, "\"");
				st.nextToken("\"");

				edge_name = st.nextToken("\"");

			}

			if (line.startsWith("    <nd ref")){

				StringTokenizer st = new StringTokenizer(line, "\"");
				st.nextToken("\"");

				String node_id = st.nextToken("\"");

				if (LIST_OF_NODES.get(node_id) != null){

					NODES_IN_WAY.add(node_id);

				}

			}    

			if (line.startsWith("    <tag")){

				StringTokenizer st = new StringTokenizer(line, "\"");
				st.nextToken("\"");

				String type = st.nextToken("\"");


				if (type.equals("highway")){

					road = true;

				}


			}

			if (line.startsWith("  </way>")){

				if (!road){

					try {line = br.readLine();}
					catch (IOException e) {}

					continue;

				}

				String source = NODES_IN_WAY.get(0);
				String target = NODES_IN_WAY.get(NODES_IN_WAY.size()-1);


				if (NODES.get(source) == null){

					try {line = br.readLine();}
					catch (IOException e) {}

					continue;

				}

				if (NODES.get(target) == null){

					try {line = br.readLine();}
					catch (IOException e) {}

					continue;

				}

				Coordinate[] coordinates = new Coordinate[NODES_IN_WAY.size()];

				for (int i=0; i<NODES_IN_WAY.size(); i++){

					coordinates[i] = LIST_OF_NODES.get(NODES_IN_WAY.get(i));

				}

				if (coordinates.length < 2){

					try {line = br.readLine();}
					catch (IOException e) {}

					continue;

				}

				SOURCES.add(source);
				TARGETS.add(target);

				ONE_WAY.add(0);

				EDGE_NAMES.add(edge_name);

				NODES.get(source).add(target);
				NODES.get(target).add(source);

				Geometry geom = gf.createLineString(coordinates);

				GEOMS.add(geom);
				WEIGHTS.add(geom.getLength());

				// Updating bounding box
				for (int i=0; i<geom.getCoordinates().length; i++){

					xmin = Math.min(xmin, geom.getCoordinates()[i].x);
					xmax = Math.max(xmax, geom.getCoordinates()[i].x);

					ymin = Math.min(ymin, geom.getCoordinates()[i].y);
					ymax = Math.max(ymax, geom.getCoordinates()[i].y);

				}

				EDGES.put(source+"->"+target, GEOMS.size()-1);
				EDGES.put(target+"->"+source, GEOMS.size()-1);

				verticeNumber += (NODES_IN_WAY.size()-2);

			}

			try {line = br.readLine();}
			catch (IOException e) {}


		}


		// -------------------------------------------
		// Return output network
		// -------------------------------------------

		NETWORK.setSources(SOURCES);
		NETWORK.setTargets(TARGETS);
		NETWORK.setWeights(WEIGHTS);
		NETWORK.setGeometries(GEOMS);
		NETWORK.setNodes(NODES);
		NETWORK.setEdges(EDGES);
		NETWORK.setEdgeNames(EDGE_NAMES);
		NETWORK.setOneWay(ONE_WAY);

		NETWORK.setNodeNumber(nodeNumber);
		NETWORK.setVerticeNumber(verticeNumber);


		// -------------------------------------------
		// Build topology
		// -------------------------------------------
		NETWORK.makeGraph();

		return NETWORK;

	}


}
