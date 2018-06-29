/*******************************************************************************
 * This software is released under the license CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 *
 * @author Yann Méneroux
 ******************************************************************************/

package fr.ign.cogit.mapmatcher.core;

import fr.ign.cogit.mapmatcher.graphics.Interface;

import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;

import fr.ign.cogit.mapmatcher.network.Graph;
import fr.ign.cogit.mapmatcher.network.Network;
import fr.ign.cogit.mapmatcher.util.Loaders;
import fr.ign.cogit.mapmatcher.util.Parameters;
import fr.ign.cogit.mapmatcher.util.Project;
import fr.ign.cogit.mapmatcher.util.Tools;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.prep.PreparedGeometry;
import com.vividsolutions.jts.geom.prep.PreparedGeometryFactory;
import com.vividsolutions.jts.index.strtree.STRtree;


public class MapMatching {

	// -------------------------------------------------------------------
	// Parameters
	// -------------------------------------------------------------------
	public static double beta = 1.0;
	public static double sigma_gps = 10.0;
	public static double search_radius = 50.0;
	public static double transition = 0.0;
	public static double speed_limit = Double.MAX_VALUE;
	// -------------------------------------------------------------------
	private static String output_delimiter = ",";
	private static String output_folder = "";

	private static boolean output_debug = false;
	private static boolean output_report = true;

	private static int progressBarSwitch = 10;

	public static boolean open_report = true;
	public static boolean gui_mode = false;

	private static int UNSOLVED_POINT = -999999999;
	// -------------------------------------------------------------------
	
	public static int INDEX_BATCH_SIZE = 30000;
	
	// -------------------------------------------------------------------

	private static ArrayList<ArrayList<Double>> CANDIDATES_D;
	private static ArrayList<ArrayList<Integer>> CANDIDATES_I;
	private static ArrayList<ArrayList<Coordinate>> CANDIDATES_C;
	private static ArrayList<ArrayList<Integer>> CANDIDATES_L;
	private static ArrayList<ArrayList<Double>> CANDIDATES_2_S;
	private static ArrayList<ArrayList<Double>> CANDIDATES_2_T;
	private static ArrayList<Long> CANDIDATES_DT;
	private static ArrayList<Double> DISPLACEMENTS_X;
	private static ArrayList<Double> DISPLACEMENTS_Y;
	private static ArrayList<Double> CAN;

	private static HashMap<String, String> CANDIDATES_CORNER;

	private static ArrayList<String> input_tracks;

	private static ArrayList<String> NOT_FEASIBLE;
	private static ArrayList<String> ERROR_OCCURRED;

	private static ArrayList<String> WKT_TRACK_MM;

	private static Network network;

	public static ArrayList<Double> getDisplacementX(){return DISPLACEMENTS_X;}
	public static ArrayList<Double> getDisplacementY(){return DISPLACEMENTS_Y;}


	private static int distancePrecompTime = 0;
	private static int mapMatchTime = 0;
	private static int noCandidate = 0;

	private static boolean no_feasible = false;

	private static int nCompleteFail = 0;

	private static int current_track_code = -1;


	// ----------------------------------------------------------
	// Method to launch all the sequence of operations
	// ----------------------------------------------------------
	public static int executeAllProcessFromGUI(){

		// Transmit parameters
		Loaders.parameterize();
		MapMatching.parameterize();

		if (Parameters.graphical_output){

			Main.gui.graphics.setNetwork(Loaders.loadNetwork(Parameters.input_network_path));

		}


		if (MapMatching.gui_mode){
			Main.gui.label_17.setText("Computing buffer...");
		}

		Tools.progressPercentage(0, input_tracks.size(), MapMatching.gui_mode);


		// Buffering on 1st track
		if (Parameters.distance_buffer.equals("1st_track") && (Parameters.precompute_distances)){

			Track track = Loaders.loadTrack(MapMatching.input_tracks.get(0));

			if (track.getX().size() > 1){

				Geometry geom = track.makeBuffer(4.0/3.0*Parameters.buffer_radius);
				Loaders.setBuffer(geom);

			}

		}

		// Buffering on all tracks
		if (Parameters.distance_buffer.equals("buffered_tracks") && (Parameters.precompute_distances)){


			Geometry geom = Loaders.loadTrack(MapMatching.input_tracks.get(0)).makeBuffer(4.0/3.0*Parameters.buffer_radius);


			for (int f=0; f<input_tracks.size(); f++){

				Track track = Loaders.loadTrack(MapMatching.input_tracks.get(f));

				if (track.getX().size() < 2){continue;}

				geom = geom.union(track.makeBuffer(4.0/3.0*Parameters.buffer_radius));

				Tools.progressPercentage(f, input_tracks.size(), MapMatching.gui_mode);

			}

			Loaders.setBuffer(geom);

			Tools.progressPercentage(input_tracks.size(), input_tracks.size(), MapMatching.gui_mode);

		}


		// Loading and preparing network data
		if (MapMatching.gui_mode){
			Main.gui.label_17.setText("Loading network file...");
		}

		Network network = Loaders.loadNetwork(Parameters.input_network_path);

		if (network == null){
			if (MapMatching.gui_mode){
				Main.gui.label_17.setText("");
			}
			return 1;
		}

		// Potential projection of data
		if (Parameters.project_coordinates){

			if (MapMatching.gui_mode){
				Main.gui.label_17.setText("Local Mercator projection...");
			}


			int test = network.toLocalMercator();

			if (test == -1){

				Tools.printError("Error: network coordinates should be in decimal degrees in WGS 84 system");
				Interface.reactivateComputeButton();
				return -1;

			}

		}


		// Set network to executer
		MapMatching.setNetwork(network);

		// Map maptching
		return MapMatching.operate();

	}


	public static void setNetwork(Network network){

		MapMatching.network = network;

		// ---------------------------------------------------
		// Preparing network topology
		// ---------------------------------------------------

		if (Parameters.make_topology){

			if (MapMatching.gui_mode){
				Main.gui.label_17.setText("Building network topology...");
			}

			MapMatching.network.makeTopology();

		}

		// ---------------------------------------------------
		// Remove simple intersections
		// ---------------------------------------------------
		if (Parameters.remove_deg_2_nodes){

			if (MapMatching.gui_mode){
				Main.gui.label_17.setText("Removing degree 2 nodes...");
			}

			network.removeDegree2Nodes();

		}

		// ---------------------------------------------------
		// Prepare network coordinate system
		// ---------------------------------------------------
		if (Parameters.add_spatial_index){

			network.makeSystem();

		}

		// ---------------------------------------------------
		// Precomputing distances on network
		// ---------------------------------------------------
		long startTime = System.currentTimeMillis();

		if (Parameters.precompute_distances){

			if (MapMatching.gui_mode){
				Main.gui.label_17.setText("Precomputing distances...");
			}


			MapMatching.network.prepare();

		}

		distancePrecompTime = (int) (System.currentTimeMillis()-startTime);

	}

	// -------------------------------------------------------------------
	// Module for for setting map-matching parameters
	// -------------------------------------------------------------------
	public static void parameterize(){

		MapMatching.sigma_gps = Math.pow(Parameters.computation_sigma, 2);

		if (Parameters.network_inaccuracies){

			MapMatching.sigma_gps += Math.pow(Parameters.network_rmse, 2);

		}

		MapMatching.sigma_gps = Math.sqrt(MapMatching.sigma_gps);
		MapMatching.beta = Parameters.computation_beta;
		MapMatching.search_radius = Parameters.computation_radius;
		MapMatching.transition = Parameters.computation_transition;
		MapMatching.speed_limit = Parameters.computation_speed_limit;
		MapMatching.output_delimiter = Parameters.output_delimiter;
		MapMatching.output_debug = Parameters.output_debug;
		MapMatching.output_report = Parameters.output_report;
		MapMatching.output_folder = Parameters.output_path;
		MapMatching.input_tracks = Parameters.input_track_path_list;

		DISPLACEMENTS_X = new ArrayList<Double>();
		DISPLACEMENTS_Y = new ArrayList<Double>();

		NOT_FEASIBLE = new ArrayList<String>();
		ERROR_OCCURRED = new ArrayList<String>();

		CAN = new ArrayList<Double>();

		WKT_TRACK_MM = new ArrayList<String>();

	}

	// -------------------------------------------------------------------
	// Module to launch map-matching
	// -------------------------------------------------------------------
	public static int operate(){

		noCandidate = 0;
		nCompleteFail = 0;

		no_feasible = false;

		// ----------------------------------------------------------
		// Clear folder
		// ----------------------------------------------------------
		if (Parameters.output_clear){

			if (MapMatching.gui_mode){
				Main.gui.label_17.setText("Cleaning output folder...");
			}

			Tools.println("Cleaning output folder");

			File[] listOfFiles = (new File(Parameters.output_path)).listFiles();

			for (int i=0; i<listOfFiles.length; i++){

				listOfFiles[i].delete();

			}



		}

		// ----------------------------------------------------------
		// Print parameters
		// ----------------------------------------------------------
		if (Parameters.output_parameters){

			if (MapMatching.gui_mode){

				Main.gui.label_17.setText("Printing parameter file...");

			}

			Parameters.print();

		}


		// ----------------------------------------------------------
		// Map-matching
		// ----------------------------------------------------------

		long startTime = System.currentTimeMillis();

		if (input_tracks.size() >= progressBarSwitch){

			Tools.println("Processing tracks...");

		}

		int index_files_counter = 0;

		// Processing tracks
		for (int i=0; i<input_tracks.size(); i++){

			// Debug mode
			MapMatching.output_debug = (Parameters.output_debug) && (i == 0);

			if (input_tracks.size() >= progressBarSwitch){

				Tools.progressPercentage(i, input_tracks.size(), gui_mode);

			}

			if (input_tracks.size() < progressBarSwitch){

				Tools.println("Processing track "+input_tracks.get(i));

			}

			if (MapMatching.gui_mode){
				Main.gui.label_17.setText("Computing map-matching solution for: "+(new File(input_tracks.get(i)).getName()));
			}

			String input = input_tracks.get(i);
			String output = makeOutputName(input);

			Track track = Loaders.loadTrack(input);

			if (track == null){			

				if (MapMatching.gui_mode){
					Main.gui.label_17.setText("");
				}

				Tools.progressPercentage(0, input_tracks.size(), gui_mode);
				Interface.reactivateComputeButton();
				return 1;

			}			

			// Coordinates projection
			if (Parameters.project_coordinates){

				int test = track.toLocalMercator();

				if (test == -1){

					Tools.printError("Error: track coordinates should be in decimal degrees in WGS 84 system");
					Interface.reactivateComputeButton();
					return -1;

				}

			}

			track.removeBias();

			current_track_code = i;

			// ----------------------------------------------------------
			// Processing map-matching on the track
			// ----------------------------------------------------------

			execute(track, output);


			// ----------------------------------------------------------
			// Output index (step by step if necessary)
			// ----------------------------------------------------------
			if (Parameters.add_spatial_index){
				
				if ((i % INDEX_BATCH_SIZE == INDEX_BATCH_SIZE-1) || (i == input_tracks.size()-1)){

					String num = "";

					if (index_files_counter > 0){
						num = index_files_counter+"";
					}


					if (Parameters.index_format_csv){
						
						network.printIndex(output_folder+"\\index"+num+".csv");
						
					}else{
						
						network.printIndexInXml(output_folder+"\\index"+num+".xml");
						
						if (index_files_counter > 0){
							
							String file1 = output_folder+"\\index.xml";
							String file2 = output_folder+"\\index"+num+".xml";
							
							Tools.mergeXml(file1, file2);
							
						}
						
					}

					network.makeSystem();
					
					index_files_counter ++;

				}
				
			}

		}

		// ---------------------------------------------------------------------
		// Graphical output
		// ---------------------------------------------------------------------

		if (MapMatching.gui_mode){


			Main.gui.graphics.plot();	

			Main.gui.graphics.repaint();

		}


		if (input_tracks.size() >= progressBarSwitch){

			Tools.progressPercentage(input_tracks.size(), input_tracks.size(), gui_mode);
			Tools.println(input_tracks.size()+" tracks processed with success");
			Tools.println("Output files written in "+output_folder);

		}

		if (MapMatching.gui_mode){
			Main.gui.label_17.setText("");
		}

		mapMatchTime = (int) (System.currentTimeMillis()-startTime);


		// ----------------------------------------------------------
		// Print all wkt map-matched trace file
		// ----------------------------------------------------------
		if (Parameters.output_debug){

			printWktAfterMapMatching();

		}

		// ----------------------------------------------------------
		// Print network file if modified
		// ----------------------------------------------------------
		if ((Parameters.make_topology) || (Parameters.remove_deg_2_nodes)){

			String p = Parameters.output_path+"\\"+"network_topo.wkt";

			if (Parameters.project_coordinates){

				network.toWGS84();

			}

			network.printInFile(p);

		}

		// ----------------------------------------------------------
		// Print QGIS project header
		// ----------------------------------------------------------
		if (Parameters.output_debug){

			Project.setAttributes();
			Project.print();

		}

		// ----------------------------------------------------------
		// Print report
		// ----------------------------------------------------------
		if (output_report){

			File report = printReport();

			// ----------------------------------------------------------
			// Display report
			// ----------------------------------------------------------
			if (open_report){

				try {

					Desktop.getDesktop().edit(report);

				} catch (IOException e) {

					if (MapMatching.gui_mode){

						JOptionPane.showMessageDialog(null, "Warning: cannot display report file", "Warning", JOptionPane.WARNING_MESSAGE);

					}else{

						Tools.println("Warning: cannot display report file");
						System.exit(14);

					}

				}

			}

		}		

		if (MapMatching.gui_mode){
			Interface.reactivateComputeButton();
		}

		return 0;

	}

	// -------------------------------------------------------------------
	// Module to prepare output name from input name
	// -------------------------------------------------------------------
	public static String makeOutputName(String inputName){

		inputName = inputName.replace("\\", "/");

		StringTokenizer st = new StringTokenizer(inputName, "/");

		String text = "";

		while(st.hasMoreTokens()){text = st.nextToken();}

		st = new StringTokenizer(text, ".");
		text = st.nextToken(".");
		text = output_folder+"/"+text+Parameters.output_suffix;

		text = text.replace("//", "/");

		return text;

	}

	// -------------------------------------------------------------------
	// Module for map-matching a track on a network
	// -------------------------------------------------------------------
	public static void execute(Track track, String outputPath){

		PrintWriter writer = null;

		PrintWriter writer0 = null;
		PrintWriter writer1 = null;
		PrintWriter writer2 = null;
		PrintWriter writer3 = null;

		File file0 = null;
		File file1 = null;
		File file2 = null;
		File file3 = null;

		File file = new File(outputPath);
		file.delete();

		try {

			writer = new PrintWriter(new BufferedWriter(new FileWriter(file)));

		} catch (IOException e) {

			if (MapMatching.gui_mode){

				JOptionPane.showMessageDialog(null, "Error: cannot create output file at "+outputPath, "Error", JOptionPane.ERROR_MESSAGE);
				Interface.reactivateComputeButton();
				return;

			}else{

				Tools.println("Error: cannot create output file at "+outputPath);
				System.exit(12);

			}

		}

		if (output_debug){

			file0 = new File(Parameters.output_path+"/proj.txt");
			file1 = new File(Parameters.output_path+"/chain.wkt");
			file2 = new File(Parameters.output_path+"/path.wkt");
			file3 = new File(Parameters.output_path+"/mapmatch.wkt");

			file0.delete();
			file1.delete();
			file2.delete();
			file3.delete();

			try {

				writer0 = new PrintWriter(file0);
				writer1 = new PrintWriter(file1);
				writer2 = new PrintWriter(file2);
				writer3 = new PrintWriter(file3);

			} catch (FileNotFoundException e) {

				if (MapMatching.gui_mode){

					JOptionPane.showMessageDialog(null, "Cannot write debug files", "Error", JOptionPane.ERROR_MESSAGE);
					Interface.reactivateComputeButton();
					return;

				}else{

					Tools.println("Error: cannot write debug file");
					System.exit(14);

				}

			}

		}


		// Output headline
		StringBuilder output = new StringBuilder("id"+output_delimiter);
		output.append("timestamp");       output.append(output_delimiter); 
		output.append("xraw");            output.append(output_delimiter); 
		output.append("yraw");            output.append(output_delimiter); 
		output.append("xmap_matched");    output.append(output_delimiter); 
		output.append("ymap_matched");    output.append(output_delimiter); 

		if (Parameters.output_rmse){

			output.append("rmse");    output.append(output_delimiter); 

		}

		if (Parameters.output_confidence){

			output.append("confidence");    output.append(output_delimiter); 

		}

		output.append("link_id");         output.append(output_delimiter); 

		if (Parameters.ref_to_network){

			output.append("abs_curv");    output.append(output_delimiter); 

		}

		output.append("node_id");         output.append("\r\n"); 

		writer.write(output.toString());

		// Candidates storage variables initialization
		CANDIDATES_D = new ArrayList<ArrayList<Double>>();
		CANDIDATES_I = new ArrayList<ArrayList<Integer>>();
		CANDIDATES_C = new ArrayList<ArrayList<Coordinate>>();
		CANDIDATES_L = new ArrayList<ArrayList<Integer>>();
		CANDIDATES_2_S = new ArrayList<ArrayList<Double>>();
		CANDIDATES_2_T = new ArrayList<ArrayList<Double>>();
		CANDIDATES_DT = new ArrayList<Long>();
		CANDIDATES_CORNER = new HashMap<String, String>();

		// Map-matching parameterization
		double alpha = 2*Math.pow(sigma_gps, 2)/beta;

		// ----------------------------------------------------
		// Spatial index computation
		// ----------------------------------------------------
		STRtree ix = new STRtree();

		ArrayList<PreparedGeometry> PREP_GEOMS = new ArrayList<PreparedGeometry>();

		// Run through edge geometries
		for (int i=0; i<network.getGeometries().size(); i++){

			// Get geometry
			Geometry g = network.getGeometries().get(i);

			// Prepare geometry
			PREP_GEOMS.add(PreparedGeometryFactory.prepare(g));

			// add to index
			ix.insert(g.getEnvelopeInternal(), i);

		}

		ix.build();

		GeometryFactory gf = new GeometryFactory();

		// --------------------------------------------------------------
		// Running through GPS points
		// --------------------------------------------------------------
		for (int i=0; i<track.getX().size(); i++){

			// Initialization
			CANDIDATES_D.add(new ArrayList<Double>());
			CANDIDATES_I.add(new ArrayList<Integer>());
			CANDIDATES_C.add(new ArrayList<Coordinate>());
			CANDIDATES_L.add(new ArrayList<Integer>());
			CANDIDATES_2_S.add(new ArrayList<Double>());
			CANDIDATES_2_T.add(new ArrayList<Double>());

			if (i == 0){
				CANDIDATES_DT.add((long) 0);
			}else{
				CANDIDATES_DT.add(track.getTime().get(i)-track.getTime().get(i-1));
			}

			// Get point coordinates
			double x = track.getX().get(i);
			double y = track.getY().get(i);

			// Transform to buffer
			Point point = gf.createPoint(new Coordinate(x, y));
			Geometry buffer = point.buffer(search_radius);

			// Get surrounding edges
			@SuppressWarnings("unchecked")
			ArrayList<Integer> CANDIDATES = (ArrayList<Integer>) ix.query(buffer.getEnvelopeInternal());


			// --------------------------------------------------------------
			// Maximal number of candidates
			// --------------------------------------------------------------

			double cut = computeCutDistance(CANDIDATES, PREP_GEOMS, buffer, x, y);
			
			// --------------------------------------------------------------
			// Running through candidates
			// --------------------------------------------------------------
			for (int j=0; j<CANDIDATES.size(); j++){

				PreparedGeometry edge = PREP_GEOMS.get(CANDIDATES.get(j));

				if (edge.intersects(buffer)){

					ArrayList<Coordinate> POLYLINE = new ArrayList<Coordinate> (Arrays.asList(edge.getGeometry().getCoordinates()));

					double[] candidate = Tools.distance_to_polyline(x, y, POLYLINE);

					if (candidate[2] > cut){continue;}

					// --------------------------------------------------------------
					// Save spatial query
					// --------------------------------------------------------------

					Coordinate c = new Coordinate(candidate[0], candidate[1]);

					CANDIDATES_C.get(CANDIDATES_C.size()-1).add(c);
					CANDIDATES_D.get(CANDIDATES_D.size()-1).add(candidate[2]);
					CANDIDATES_L.get(CANDIDATES_L.size()-1).add(CANDIDATES.get(j));
					CANDIDATES_I.get(CANDIDATES_I.size()-1).add((int)candidate[3]);


					// --------------------------------------------------------------
					// Computing distances to edge source and target
					// --------------------------------------------------------------

					Geometry geom = network.getGeometries().get(CANDIDATES.get(j));

					double[] distances = Tools.distance_to_vertices(c.x, c.y, geom, (int)candidate[3]);	

					CANDIDATES_2_S.get(CANDIDATES_2_S.size()-1).add(distances[0]);
					CANDIDATES_2_T.get(CANDIDATES_2_T.size()-1).add(distances[1]);


					if (distances[0] == 0){

						CANDIDATES_CORNER.put(i+"|"+(CANDIDATES_2_S.get(CANDIDATES_2_S.size()-1).size()-1), network.getSources().get(CANDIDATES.get(j)));

					}

					if (distances[1] == 0){

						CANDIDATES_CORNER.put(i+"|"+(CANDIDATES_2_S.get(CANDIDATES_2_S.size()-1).size()-1), network.getTargets().get(CANDIDATES.get(j)));

					}

					if (output_debug){

						Coordinate cbis = new Coordinate(c.x, c.y);

						if (Parameters.project_coordinates){

							Tools.toGeo(cbis);

						}

						writer0.print(cbis.x+","+cbis.y+","+candidate[2]+","+distances[0]+","+distances[1]+"\r\n");

					}

				}

			}

			// Check
			if (CANDIDATES_C.get(CANDIDATES_C.size()-1).size() == 0){

				if (Parameters.failure_skip){

					CANDIDATES_C.get(CANDIDATES_C.size()-1).add(point.getCoordinate());
					CANDIDATES_D.get(CANDIDATES_D.size()-1).add(0.0);
					CANDIDATES_L.get(CANDIDATES_L.size()-1).add(UNSOLVED_POINT);
					CANDIDATES_I.get(CANDIDATES_I.size()-1).add(UNSOLVED_POINT);
					CANDIDATES_2_S.get(CANDIDATES_2_S.size()-1).add(-1.0);
					CANDIDATES_2_T.get(CANDIDATES_2_T.size()-1).add(-1.0);

					noCandidate ++;

				}
				else{

					if (MapMatching.gui_mode){

						Tools.printError("Error: no candidate for track point number "+i+".\nIncrease search radius parameter, check point coordinates consistency or allow \"skip unsolved points\" option.");
						return;

					}
					else{

						Tools.println("");
						Tools.print("Error: no candidate for track point number "+i+". \r\n");
						Tools.println("Increase search radius parameter, check point coordinates consistency or allow \"skip unsolved points\" option.");
						System.exit(11);

					}

				}


			}

		}

		// Counting candidate number per point
		for (int i = 0; i<CANDIDATES_C.size(); i++){

			CAN.add((double) CANDIDATES_C.get(i).size());

		}


		// --------------------------------------------------------------
		// Creating Hidden Markov Model
		// --------------------------------------------------------------

		ArrayList<String> HMM_SOURCES = new ArrayList<String>();
		ArrayList<String> HMM_TARGETS = new ArrayList<String>();
		ArrayList<Double> HMM_WEIGHTS = new ArrayList<Double>();

		Hashtable<String, Integer> HMM_EDGES = new Hashtable<String, Integer>();


		if (CANDIDATES_C.size() == 0){

			while(Loaders.EXCLUDED_I.size() != 0){

				output =  new StringBuilder(); 

				output.append(Loaders.EXCLUDED_I.get(0));  output.append(output_delimiter);
				output.append(Loaders.EXCLUDED_T.get(0));  output.append(output_delimiter);
				output.append(Loaders.EXCLUDED_X.get(0));  output.append(output_delimiter);
				output.append(Loaders.EXCLUDED_Y.get(0));  output.append(output_delimiter);
				output.append(Loaders.gps_error_code);     output.append(output_delimiter);
				output.append(Loaders.gps_error_code);     output.append(output_delimiter);

				if (Parameters.output_rmse){

					output.append(Loaders.gps_error_code);     output.append(output_delimiter);

				}

				if (Parameters.output_confidence){

					output.append(Loaders.gps_error_code);     output.append(output_delimiter);

				}

				output.append(Loaders.gps_error_code);     output.append(output_delimiter);

				if (Parameters.ref_to_network){

					output.append(Loaders.gps_error_code);     output.append(output_delimiter);

				}

				output.append(Loaders.gps_error_code);     output.append("\r\n");

				writer.write(output.toString());

				Loaders.EXCLUDED_I.remove(0);
				Loaders.EXCLUDED_T.remove(0);
				Loaders.EXCLUDED_X.remove(0);
				Loaders.EXCLUDED_Y.remove(0);

			}

			writer.close();

			return;

		}

		// --------------------------------------------------------------
		// Initialization
		// --------------------------------------------------------------
		for (int j=0; j<CANDIDATES_C.get(0).size(); j++){

			double weight = Math.pow(CANDIDATES_D.get(0).get(j), 2);

			HMM_SOURCES.add("origin");
			HMM_TARGETS.add(0+"|"+j);
			HMM_WEIGHTS.add(weight);

			HMM_EDGES.put("origin"+"->"+0+"|"+j, 0);

		}



		// --------------------------------------------------------------
		// Running through track
		// --------------------------------------------------------------

		int change = CANDIDATES_C.size()/100;

		for (int i=1; i<CANDIDATES_C.size(); i++){

			// Running through candidates at step i-1
			for (int j=0; j<CANDIDATES_C.get(i-1).size(); j++){

				Coordinate c1 = CANDIDATES_C.get(i-1).get(j);

				// Running through candidates at step i
				for (int k=0; k<CANDIDATES_C.get(i).size(); k++){

					if ((CANDIDATES_L.get(i).get(k) == UNSOLVED_POINT) || (CANDIDATES_L.get(i-1).get(j) == UNSOLVED_POINT)){

						HMM_SOURCES.add((i-1)+"|"+j);
						HMM_TARGETS.add(i+"|"+k);
						HMM_WEIGHTS.add(0.0);

						HMM_EDGES.put((i-1)+"|"+j+"->"+i+"|"+k, HMM_SOURCES.size()-1);

						continue;

					}


					Coordinate c2 = CANDIDATES_C.get(i).get(k);

					// Start and end sources
					String startSource = network.getSources().get(CANDIDATES_L.get(i-1).get(j));
					String endSource = network.getSources().get(CANDIDATES_L.get(i).get(k));

					// Start and end targets
					String startTarget = network.getTargets().get(CANDIDATES_L.get(i-1).get(j));
					String endTarget = network.getTargets().get(CANDIDATES_L.get(i).get(k));


					// --------------------------------------------------------------
					// Distance between candidates on the network
					// --------------------------------------------------------------

					int link1 = CANDIDATES_L.get(i-1).get(j);
					int link2 = CANDIDATES_L.get(i).get(k);

					// Path to start edge node
					double s1 = CANDIDATES_2_S.get(i-1).get(j);
					double s2 = CANDIDATES_2_T.get(i-1).get(j);

					// Path to end candidate
					double t1 = CANDIDATES_2_S.get(i).get(k);
					double t2 = CANDIDATES_2_T.get(i).get(k);


					double l = 0;
					double weight = 0;

					if (alpha != 0){

						if (link1 == link2){

							l = t1 - s1;

							if (network.getOneWay().get(link1) != 0){

								// System.out.println(network.getEdgeName(link1)+","+network.getOneWay().get(link1)+","+l);

								if ((network.getOneWay().get(link1) > 0) && (l < 0)){

									l = Double.MAX_VALUE/CANDIDATES_C.size();

								}

								if ((network.getOneWay().get(link1) < 0) && (l > 0)){

									l = Double.MAX_VALUE/CANDIDATES_C.size();

								}


							}
							else{

								l = Math.abs(l);

							}



						}
						else{

							// Cost on transition
							weight = transition;

							// Path from edge to edge
							double d11 = network.getGenericDistance(startSource, endSource);
							double d12 = network.getGenericDistance(startSource, endTarget);
							double d21 = network.getGenericDistance(startTarget, endSource);
							double d22 = network.getGenericDistance(startTarget, endTarget);


							// Combining paths
							d11 += s1+t1;
							d12 += s1+t2;
							d21 += s2+t1;
							d22 += s2+t2;

							l = Math.min(Math.min(d11, d12), Math.min(d21, d22));

						}

					}

					// --------------------------------------------------------------
					// Straightline distance between candidates
					// --------------------------------------------------------------

					double l0 = c1.distance(c2);


					// --------------------------------------------------------------
					// Distance between candidates and edges
					// --------------------------------------------------------------

					double d = CANDIDATES_D.get(i).get(k);

					// --------------------------------------------------------------
					// Speed limitation between consecutive points
					// --------------------------------------------------------------

					double speed = l/((double)(CANDIDATES_DT.get(i))/1000.0);

					if (speed > Parameters.computation_speed_limit){

						continue;

					}

					// --------------------------------------------------------------
					// Autocorrelation between errors
					// --------------------------------------------------------------
					if (Parameters.computation_autocorrelation != 0){

						double ex1 = track.getX().get(i-1) - c1.x;
						double ey1 = track.getY().get(i-1) - c1.y;
						double ex2 = track.getX().get(i) - c2.x;
						double ey2 = track.getY().get(i) - c2.y;

						double correlation_term = (Math.pow(ex1-ex2, 2) + Math.pow(ey1-ey2, 2));

						weight += Parameters.computation_autocorrelation*l0/Parameters.computation_scope*correlation_term;

					}


					// --------------------------------------------------------------
					// Angle difference
					// -------------------------------------------------------------
					if (Parameters.computation_angle != 0){

						double Xa = track.getX().get(i)-track.getX().get(i-1);
						double Ya = track.getY().get(i)-track.getY().get(i-1);
						double Xb = c2.x - c1.x;
						double Yb = c2.y - c1.y;

						double Na = Math.sqrt(Xa*Xa+Ya*Ya);
						double Nb = Math.sqrt(Xb*Xb+Yb*Yb);
						double C = (Xa*Xb+Ya*Yb)/(Na*Nb);
						double S = (Xa*Yb-Ya*Xb);
						double dtheta = Math.signum(S)*Math.acos(C);

						double wtheta = Parameters.computation_angle*dtheta*dtheta/2*Math.pow(Math.asin(sigma_gps/l0),2);

						if (Double.isNaN(wtheta)){

							wtheta = 0;

						}

						weight += wtheta;

					}

					// --------------------------------------------------------------
					// Computing the weight between candidates
					// --------------------------------------------------------------

					// Uniform distribution
					if (Parameters.computation_distribution == Parameters.DISTRIBUTION_UNIFORM){

						if (d < Math.sqrt(2)*sigma_gps){

							weight += Math.sqrt(2)/2*sigma_gps;

						}

					}

					// Exponential distribution
					if (Parameters.computation_distribution == Parameters.DISTRIBUTION_EXPONENTIAL){

						weight += d;
					}

					// Normal distribution
					if (Parameters.computation_distribution == Parameters.DISTRIBUTION_NORMAL){

						weight += Math.pow(d, 2);
					}

					// Rayleigh distribution
					if (Parameters.computation_distribution == Parameters.DISTRIBUTION_RAYLEIGH){

						weight += Math.pow(d/sigma_gps, 2)/2 - Math.log(d);
					}

					// Transition probability
					weight += alpha*(l-l0);

					// Max value correction
					double max = Double.MAX_VALUE/CANDIDATES_C.size();
					weight = Math.min(weight, max);

					// --------------------------------------------------------------
					// Cutting on speeds
					// --------------------------------------------------------------

					if (CANDIDATES_DT.get(i) == 0){

						if (MapMatching.gui_mode){

							Tools.printError("Error: two consecutive points with same timestamp for track "+track.getPath());
							return;

						}
						else{

							Tools.println("Error: two consecutive points with same timestamp");
							System.exit(12);

						}

					}


					// --------------------------------------------------------------
					// Adding link to Hidden Markov Model
					// --------------------------------------------------------------

					HMM_SOURCES.add((i-1)+"|"+j);
					HMM_TARGETS.add(i+"|"+k);
					HMM_WEIGHTS.add(weight);

					HMM_EDGES.put((i-1)+"|"+j+"->"+i+"|"+k, HMM_SOURCES.size()-1);


					if (output_debug){

						Coordinate cbis1 = new Coordinate(c1.x, c1.y);
						Coordinate cbis2 = new Coordinate(c2.x, c2.y);

						if (Parameters.project_coordinates){

							Tools.toGeo(cbis1);
							Tools.toGeo(cbis2);

						}

						String chaine = i+",\"";
						chaine += "LINESTRING(";

						chaine += cbis1.x+" ";
						chaine += cbis1.y+",";
						chaine += cbis2.x+" ";
						chaine += cbis2.y+")";

						chaine += "\"";
						chaine += ","+l+","+l0+","+weight+"\r\n";

						writer1.write(chaine);

					}

				}

			}

			// Progress bar
			if (input_tracks.size() < progressBarSwitch){

				if (change != 0){

					if ((gui_mode)||(i % change == 0)){

						Tools.progressPercentage(i, CANDIDATES_C.size(), gui_mode);

					}

				}

			}


		}

		// Progress bar termination
		if (input_tracks.size() < progressBarSwitch){

			Tools.progressPercentage(CANDIDATES_C.size(), CANDIDATES_C.size(), gui_mode);

		}

		// --------------------------------------------------------------
		// Closing chain
		// --------------------------------------------------------------
		for (int j=0; j<CANDIDATES_C.get(CANDIDATES_C.size()-1).size(); j++){

			double weight = 0;

			HMM_SOURCES.add(CANDIDATES_C.size()-1+"|"+j);
			HMM_TARGETS.add("destination");
			HMM_WEIGHTS.add(weight);

			HMM_EDGES.put(CANDIDATES_C.size()-1+"|"+j+"->"+"destination", HMM_SOURCES.size()-1);

		}

		// Counting edge number
		int hmm_edge_number = HMM_SOURCES.size();


		// --------------------------------------------------------------
		// Converting HMM to graph model
		// --------------------------------------------------------------

		Graph.Edge[] graph = new Graph.Edge[hmm_edge_number];

		for (int i=0; i<hmm_edge_number; i++){

			graph[i] = new Graph.Edge(HMM_SOURCES.get(i), HMM_TARGETS.get(i),  HMM_WEIGHTS.get(i));

		}

		Graph g = new Graph(graph);

		// --------------------------------------------------------------
		// Solving HMM problem with Dijkstra algorithm
		// --------------------------------------------------------------

		String START = "origin";
		String END = "destination";

		ArrayList<Integer> path = null;

		g.dijkstra(START, END);

		path = g.getPathEdges(END, HMM_EDGES);


		double performance = 0.0;

		if (Parameters.output_confidence){

			performance = g.getPathLength(END);

		}


		if (path.size() == 0){

			ERROR_OCCURRED.add(track.getPath());

			nCompleteFail += track.getX().size();

			return;

		}

		if (g.getPathLength(END) >= Integer.MAX_VALUE/path.size()){

			no_feasible = true;

			NOT_FEASIBLE.add(track.getPath());

		}


		Track track_mm = new Track(new ArrayList<Double>(), new ArrayList<Double>());


		// --------------------------------------------------------------
		// Extracting path
		// --------------------------------------------------------------
		if (Parameters.output_debug){

			StringBuilder wkt_track_mm = new StringBuilder("\"LINESTRING(");

			for (int i=1; i<path.size()-1; i++){

				String source = HMM_SOURCES.get(path.get(i));
				String target = HMM_TARGETS.get(path.get(i));

				StringTokenizer st1 = new StringTokenizer(source, "|");
				StringTokenizer st2 = new StringTokenizer(target, "|");

				int source1 = Integer.parseInt(st1.nextToken());
				int source2 = Integer.parseInt(st1.nextToken());
				int target1 = Integer.parseInt(st2.nextToken());
				int target2 = Integer.parseInt(st2.nextToken());

				double x1 = CANDIDATES_C.get(source1).get(source2).x;
				double y1 = CANDIDATES_C.get(source1).get(source2).y;
				double x2 = CANDIDATES_C.get(target1).get(target2).x;
				double y2 = CANDIDATES_C.get(target1).get(target2).y;

				if (Parameters.project_coordinates){

					Coordinate c1 = new Coordinate(x1, y1);
					Coordinate c2 = new Coordinate(x2, y2);

					Tools.toGeo(c1);
					Tools.toGeo(c2);

					x1 = c1.x;
					y1 = c1.y;
					x2 = c2.x;
					y2 = c2.y;

				}

				if (MapMatching.output_debug){

					String chaine = "LINESTRING(";
					chaine += x1+" ";
					chaine += y1+",";
					chaine += x2+" ";
					chaine += y2+")";

					writer2.write(i+",\""+chaine+"\""+"\r\n");

				}

				wkt_track_mm.append(x1); wkt_track_mm.append(" ");
				wkt_track_mm.append(y1); wkt_track_mm.append(",");

				if (i == path.size()-1){

					wkt_track_mm.append(x2); wkt_track_mm.append(" ");
					wkt_track_mm.append(y2);

				}

			}

			wkt_track_mm.append(")\"");

			WKT_TRACK_MM.add(wkt_track_mm.toString());

		}

		// ----------------------------------------------------------
		// Print output
		// ----------------------------------------------------------

		Integer l = null;
		int shift = 0;

		for (int i=1; i<path.size(); i++){

			if (Parameters.output_errors){

				if (Loaders.EXCLUDED_I.size() != 0){

					if (Loaders.EXCLUDED_I.get(0).equals(i)){

						output =  new StringBuilder(); 

						output.append(Loaders.EXCLUDED_I.get(0));  output.append(output_delimiter);
						output.append(Loaders.EXCLUDED_T.get(0));  output.append(output_delimiter);
						output.append(Loaders.EXCLUDED_X.get(0));  output.append(output_delimiter);
						output.append(Loaders.EXCLUDED_Y.get(0));  output.append(output_delimiter);
						output.append(Loaders.gps_error_code);     output.append(output_delimiter);
						output.append(Loaders.gps_error_code);     output.append(output_delimiter);

						if (Parameters.output_rmse){

							output.append(Loaders.gps_error_code);     output.append(output_delimiter);

						}

						if (Parameters.output_confidence){

							output.append(Loaders.gps_error_code);     output.append(output_delimiter);

						}

						output.append(Loaders.gps_error_code);     output.append(output_delimiter);

						if (Parameters.ref_to_network){

							output.append(Loaders.gps_error_code);     output.append(output_delimiter);

						}

						output.append(Loaders.gps_error_code);     output.append("\r\n");

						writer.write(output.toString());

						Loaders.EXCLUDED_I.remove(0);
						Loaders.EXCLUDED_T.remove(0);
						Loaders.EXCLUDED_X.remove(0);
						Loaders.EXCLUDED_Y.remove(0);

						i --;
						shift ++;

						continue;

					}

				}

			}


			String source = HMM_SOURCES.get(path.get(i));

			StringTokenizer st1 = new StringTokenizer(source, "|");

			int source1 = Integer.parseInt(st1.nextToken());
			int source2 = Integer.parseInt(st1.nextToken());

			double xmm = CANDIDATES_C.get(source1).get(source2).x;
			double ymm = CANDIDATES_C.get(source1).get(source2).y;

			double xraw = track.getX().get(i-1) + Parameters.bias_x;
			double yraw = track.getY().get(i-1) + Parameters.bias_y;

			// Add displacements
			DISPLACEMENTS_X.add(xraw-xmm);
			DISPLACEMENTS_Y.add(yraw-ymm);

			// Reference in network system

			double abs = 0;

			if (Parameters.abs_curv_type.equals("from_source_m")){abs = CANDIDATES_2_S.get(source1).get(source2);}
			if (Parameters.abs_curv_type.equals("from_target_m")){abs = CANDIDATES_2_T.get(source1).get(source2);}
			if (Parameters.abs_curv_type.equals("from_source_%")){abs = CANDIDATES_2_S.get(source1).get(source2)/(CANDIDATES_2_S.get(source1).get(source2)+CANDIDATES_2_T.get(source1).get(source2));}
			if (Parameters.abs_curv_type.equals("from_target_%")){abs = CANDIDATES_2_T.get(source1).get(source2)/(CANDIDATES_2_S.get(source1).get(source2)+CANDIDATES_2_T.get(source1).get(source2));}


			// --------------------------------------------------------------
			// Confidence index computation
			// --------------------------------------------------------------

			double confidence = 0.0;

			if ((Parameters.output_confidence) && (Parameters.input_track_path_list.size() < progressBarSwitch)){

				if (MapMatching.gui_mode){

					Main.gui.label_17.setText("Confidence indices computation...");

				}

				for (int m=0; m<hmm_edge_number; m++){

					graph[m] = new Graph.Edge(HMM_SOURCES.get(m), HMM_TARGETS.get(m),  HMM_WEIGHTS.get(m));

					if (HMM_SOURCES.get(m).equals(source)){

						graph[m] = new Graph.Edge(HMM_SOURCES.get(m), HMM_TARGETS.get(m),  Double.MAX_VALUE/CANDIDATES_C.size()*HMM_WEIGHTS.get(m));

					}

				}

				g = new Graph(graph);

				g.dijkstra(START, END);


				confidence = 1.0-performance/g.getPathLength(END);


				if (Parameters.input_track_path_list.size() < progressBarSwitch){

					Tools.progressPercentage(i, CANDIDATES_C.size(), gui_mode);

				}

			}


			// Coordinates inverse projection
			if (Parameters.project_coordinates){

				Coordinate cmm = Tools.mercator2geo(new Coordinate(xmm, ymm));
				Coordinate craw = Tools.mercator2geo(new Coordinate(xraw, yraw));

				xmm = cmm.x;
				ymm = cmm.y;

				xraw = craw.x;
				yraw = craw.y;

			}

			if (output_debug){

				String chaine = "LINESTRING(";
				chaine += xraw+" ";
				chaine += yraw+",";
				chaine += xmm+" ";
				chaine += ymm+")";

				writer3.write(i+",\""+chaine+"\""+"\r\n");

			}


			String date = track.getDate().get(i-1);

			String link_name = Parameters.track_error_code;
			String node_name = Parameters.track_error_code;


			node_name = CANDIDATES_CORNER.get(source);


			if (node_name == null){

				node_name = Parameters.track_error_code;
				l = CANDIDATES_L.get(source1).get(source2);

			}

			else{

				if ((l == null) || (!Parameters.sort_nodes)){

					l = CANDIDATES_L.get(source1).get(source2);

				}
				else{

					if (l != UNSOLVED_POINT){

						ArrayList<String> A = network.getNodes().get(node_name);

						String previous = network.getSources().get(l);
						String n = network.getGenericLastEdge(previous, node_name);

						if (A.contains(n)){

							l = network.getEdges().get(n+"->"+node_name);

						}

					}

				}

			}


			if (l != UNSOLVED_POINT){

				link_name = network.getEdgeName(l);

			}


			if (Parameters.output_confidence){

				if (Parameters.confidence_min_ratio){

					if (confidence < Parameters.confidence_ratio){

						link_name = Parameters.track_error_code;

						xmm = yraw;
						ymm = yraw;

						noCandidate ++;

					}

				}

			}

			output = new StringBuilder();

			output.append(i+shift);      output.append(output_delimiter);
			output.append(date);         output.append(output_delimiter);
			output.append(xraw);         output.append(output_delimiter);
			output.append(yraw);         output.append(output_delimiter);
			output.append(xmm);          output.append(output_delimiter);
			output.append(ymm);          output.append(output_delimiter);


			track_mm.getX().add(xmm);
			track_mm.getY().add(ymm);


			if (Parameters.output_rmse){

				double rmse = Math.sqrt(Math.pow(xraw-xmm, 2)+Math.pow(yraw-ymm, 2));

				if (Parameters.network_inaccuracies){

					rmse = Math.sqrt(Math.pow(rmse, 2) + Math.pow(Parameters.network_rmse, 2));

				}

				if (Parameters.rmse_type_before){

					rmse *= Math.sqrt(2);

				}

				output.append(rmse);     output.append(output_delimiter);

			}


			if (Parameters.output_confidence){

				output.append(confidence);     output.append(output_delimiter);

			}

			output.append(link_name);    output.append(output_delimiter);

			if (Parameters.ref_to_network){

				// Sorting nodes test
				if ((node_name != Parameters.track_error_code) && (l != UNSOLVED_POINT)) {

					String source_node = network.getSources().get(l);
					String target_node = network.getTargets().get(l);

					if (node_name.equals(source_node)){abs = 0.0;}
					if (node_name.equals(target_node)){abs = network.getWeights().get(l);}

				}

				output.append(abs);     output.append(output_delimiter);

			}

			output.append(node_name);       output.append("\r\n");

			writer.write(output.toString());

			// ---------------------------------------------------------------------
			// Network reference system
			// ---------------------------------------------------------------------
			if (Parameters.add_spatial_index){

				if (l == UNSOLVED_POINT){continue;}

				if (Parameters.output_index_coords){

					network.addPointToEdgeWithCoordinates(current_track_code, i, l, xraw, yraw, abs, date);

				}
				else{

					network.addPointToEdge(current_track_code, i, l);

				}


			}
			// ---------------------------------------------------------------------

		}

		writer.close();

		if (Parameters.input_track_path_list.size() < progressBarSwitch){

			if (MapMatching.gui_mode){

				Main.gui.label_17.setText("");

			}

			Tools.progressPercentage(CANDIDATES_C.size(), CANDIDATES_C.size(), gui_mode);

		}

		if (input_tracks.size() < progressBarSwitch){
			Tools.println("Output file "+file.getPath().replace("\\", "/")+" : ok");
		}

		if (output_debug){

			writer0.close();
			if (input_tracks.size() < progressBarSwitch){
				Tools.println("Debug file "+file0.getPath().replace("\\", "/")+" : ok");
			}

			writer1.close();
			if (input_tracks.size() < progressBarSwitch){
				Tools.println("Debug file "+file1.getPath().replace("\\", "/")+" : ok");
			}

			writer2.close();
			if (input_tracks.size() < progressBarSwitch){
				Tools.println("Debug file "+file2.getPath().replace("\\", "/")+" : ok");
			}

			writer3.close();
			if (input_tracks.size() < progressBarSwitch){
				Tools.println("Debug file "+file3.getPath().replace("\\", "/")+" : ok");
			}

		}



		// ---------------------------------------------------------------------
		// Graphical output
		// ---------------------------------------------------------------------


		if (MapMatching.gui_mode){


			if (Main.gui.graphics.getNetwork() == null){
				Main.gui.graphics.setNetwork(MapMatching.network);
			}

			Main.gui.graphics.addTrack(track);

			if (Parameters.project_coordinates){
				track_mm.toLocalMercator();	
			}
			
			Main.gui.graphics.addTrackMm(track_mm);
			

		}

	}



	// ----------------------------------------------------------
	// Eliminating candidates from distances
	// ----------------------------------------------------------
	private static double computeCutDistance(ArrayList<Integer> CANDIDATES, ArrayList<PreparedGeometry> PREP_GEOMS, Geometry buffer, double x, double y){

		double cut = Double.MAX_VALUE;

		if (Parameters.max_number_candidates > 0){
			ArrayList<Double> DIST = new ArrayList<Double>();

			for (int j=0; j<CANDIDATES.size(); j++){

				PreparedGeometry edge = PREP_GEOMS.get(CANDIDATES.get(j));

				if (edge.intersects(buffer)){

					ArrayList<Coordinate> POLYLINE = new ArrayList<Coordinate> (Arrays.asList(edge.getGeometry().getCoordinates()));

					DIST.add(Tools.distance_to_polyline(x, y, POLYLINE)[2]);

				}

			}

			// Sorting list of distances
			Collections.sort(DIST);

			if (DIST.size() > Parameters.max_number_candidates){
				cut = DIST.get(Parameters.max_number_candidates-1);
			}

		}

		return cut;

	}

	// ----------------------------------------------------------
	// Print all wkt map-matched track files
	// ----------------------------------------------------------
	private static void printWktAfterMapMatching(){

		PrintWriter writer = null;

		File file = new File(Parameters.output_path+"/all_tracks_mm.wkt");
		file.delete();

		Tools.print("Wkt file "+file.getPath().replace("\\", "/")+" : ");

		try {

			writer = new PrintWriter(file);

		} catch (FileNotFoundException e) {

			if (MapMatching.gui_mode){

				JOptionPane.showMessageDialog(null, "Cannot write debug files", "Warning", JOptionPane.WARNING_MESSAGE);

			}else{

				Tools.println("Error: cannot write debug file");
				System.exit(14);

			}


		}

		for (int i=0; i<WKT_TRACK_MM.size(); i++){

			writer.write(i+","+WKT_TRACK_MM.get(i)+"\r\n");

		}

		writer.close();

		Tools.println("ok");

	}


	// ----------------------------------------------------------
	// Print report file
	// ----------------------------------------------------------
	private static File printReport(){

		PrintWriter writer_report = null;

		String path = output_folder+"/map_matching_report.txt";

		if (MapMatching.gui_mode){

			path = Parameters.gui_report_path;

		}


		File file_report = new File(path);

		Tools.print("Report file "+path+" : ");

		file_report.delete();

		try {

			writer_report = new PrintWriter(file_report);

			int n = MapMatching.getDisplacementX().size()-noCandidate;

			int nex = Loaders.excluded_points_number;
			int ntrack = input_tracks.size();
			int npts = n + nex + nCompleteFail+noCandidate;

			int edgeNumber = network.getSources().size();
			int nodeNumber = network.getNodeNumber();
			int verticeNumber = network.getVerticeNumber();


			double meanX = Tools.round(Tools.mean(MapMatching.getDisplacementX()), 2);
			double meanY = Tools.round(Tools.mean(MapMatching.getDisplacementY()), 2);
			double max = Tools.round(Tools.max(MapMatching.getDisplacementX(), MapMatching.getDisplacementY()), 2);
			double rmse = Tools.round(Tools.rmse(MapMatching.getDisplacementX(), MapMatching.getDisplacementY()), 2);
			double mad = Tools.round(Tools.mad(MapMatching.getDisplacementX(), MapMatching.getDisplacementY()), 2);
			double stdMeanX = Tools.round(1.96*Tools.std(MapMatching.getDisplacementX())/Math.sqrt(n), 2);
			double stdMeanY = Tools.round(1.96*Tools.std(MapMatching.getDisplacementY())/Math.sqrt(n), 2);
			double sigmaGPS = Parameters.computation_sigma;
			double radius = Parameters.computation_radius;
			double beta = Parameters.computation_beta;
			double transition = Parameters.computation_transition;
			double exp = Tools.round(rmse, 2);
			double sigma_posterior = Tools.round(1.4826*mad, 2);

			rmse = Tools.round(Math.sqrt(2)*rmse, 2);

			String NetworkPath = network.getPath();

			double distancePrecompTimeDouble = Tools.round((double)distancePrecompTime/1000.0, 3);
			double mapMatchTimeDouble = Tools.round((double)mapMatchTime/1000.0, 3);
			double totalTime = Tools.round(distancePrecompTimeDouble + mapMatchTimeDouble, 3);
			double totalTimeByTrack = Tools.round(totalTime/input_tracks.size(), 3);

			int candidate_number = (int) Tools.sum(CAN);
			double mean_candidate = Tools.round(Tools.mean(CAN), 1);
			int min_candidate = (int) Tools.min(CAN);
			int max_candidate = (int) Tools.max(CAN);

			int failures = (noCandidate+nCompleteFail);

			double ratio = Tools.round((double)n/(double)npts*100.0, 1);
			double ratio1 = Tools.round((double)nex/(double)npts*100.0, 1);
			double ratio2 = Tools.round((double)(failures)/(double)npts*100.0, 1);

			double speed_limit = Tools.round(Parameters.computation_speed_limit, 2);

			int numberOfProjectedPoints = Tools.getNumberOfProjectedPoints();
			int absAvgDistorsion = (int) (Tools.absolute_average_distorsion()*Math.pow(10, 5));
			int maxAbsDistorsion = (int) (Tools.max_abs_distorsion()*Math.pow(10, 5));

			int mb = 1024*1024;

			Runtime runtime = Runtime.getRuntime();

			double maxheapmem =  runtime.maxMemory() / mb;


			Date date = new Date();
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

			writer_report.print("--------------------------------------------------------------------------\r\n");
			writer_report.print("                         MAP MATCHING REPORT FILE                         \r\n");
			writer_report.print("--------------------------------------------------------------------------\r\n");
			writer_report.print("* General information\r\n");
			writer_report.write("User : " + System.getProperty("user.name")+"\r\n");
			writer_report.write("Date : " + dateFormat.format(date)+"\r\n");
			writer_report.write("System : " + System.getProperty("os.name")+" ");
			writer_report.write("("+System.getProperty("os.version")+") ");
			writer_report.write("" + System.getProperty("os.arch")+"\r\n");
			writer_report.write("Java version : " + System.getProperty("java.version")+"  ");
			writer_report.write("Heap memory : " + maxheapmem+" MB\r\n");
			writer_report.print("--------------------------------------------------------------------------\r\n");
			writer_report.print("* Input tracks \r\n");
			writer_report.print("Path = "+Parameters.input_track_path+"\r\n");
			writer_report.print("Number of tracks = "+ntrack+"\r\n");
			writer_report.print("Number of points = "+npts+"\r\n");
			writer_report.print("Number of map-matched points = "+n+" ("+ratio+" %)\r\n");
			writer_report.print("Number of removed points = "+nex+" ("+ratio1+" %)  ");
			writer_report.print("Failures = "+failures+" ("+ratio2+" %)\r\n");
			writer_report.print("--------------------------------------------------------------------------\r\n");
			writer_report.print("* Input network");

			if (Parameters.remove_deg_2_nodes){
				writer_report.print(" (after simplification)");
			}

			writer_report.print("\r\n");

			writer_report.print(""+NetworkPath+"\r\n");

			writer_report.print("Number of edges = "+edgeNumber+" ");

			if (Parameters.remove_deg_2_nodes){
				writer_report.print("("+network.reduc_edge+" %)");
			}

			writer_report.print("\r\n");

			writer_report.print("Number of nodes = "+nodeNumber+" ");

			if (Parameters.remove_deg_2_nodes){
				writer_report.print("("+network.reduc_node+" %)");
			}

			writer_report.print("\r\n");

			writer_report.print("Number of vertices = "+verticeNumber+" ");

			if (Parameters.remove_deg_2_nodes){
				writer_report.print("(+"+network.reduc_vertex+" %)");
			}
			writer_report.print("\r\n");



			writer_report.print("--------------------------------------------------------------------------\r\n");
			writer_report.print("* Computation parameters\r\n");
			writer_report.print("GPS standard error = "+sigmaGPS+" m\r\n");
			writer_report.print("Transition model factor beta = "+beta+"\r\n");
			writer_report.print("Search radius = "+radius+" m\r\n");
			writer_report.print("Transition fixed cost = "+transition+"\r\n");
			writer_report.print("Speed limitation = "+speed_limit+" m/s\r\n");

			writer_report.print("Measurements autocorrelation = "+Parameters.computation_autocorrelation+" %\r\n");
			writer_report.print("Autocorrelation scope = "+Parameters.computation_scope+" m\r\n");

			if (Parameters.computation_distribution == Parameters.DISTRIBUTION_UNIFORM)      {writer_report.write("GPS errors distribution = uniform\r\n");}
			if (Parameters.computation_distribution == Parameters.DISTRIBUTION_EXPONENTIAL)  {writer_report.write("GPS errors distribution = exponential\r\n");}
			if (Parameters.computation_distribution == Parameters.DISTRIBUTION_NORMAL)       {writer_report.write("GPS errors distribution = normal\r\n");}
			if (Parameters.computation_distribution == Parameters.DISTRIBUTION_RAYLEIGH)     {writer_report.write("GPS errors distribution = rayleigh\r\n");}

			writer_report.print("Angle factor = "+Parameters.computation_angle+"\r\n");

			if (Parameters.project_coordinates){

				writer_report.print("--------------------------------------------------------------------------\r\n");
				writer_report.print("* Projection in local Mercator \r\n");
				writer_report.print("Number of projected points = "+numberOfProjectedPoints+"\r\n");
				writer_report.print("Average absolute distorsion = "+absAvgDistorsion+" cm/km\r\n");
				writer_report.print("Maximal absolute distorsion = "+maxAbsDistorsion+" cm/km\r\n");

			}

			writer_report.print("--------------------------------------------------------------------------\r\n");
			writer_report.print("* Candidate points\r\n");
			writer_report.print("Number of candidates = "+candidate_number+" (");
			writer_report.print("avg = "+mean_candidate+"  ");
			writer_report.print("min = "+min_candidate+"  ");
			writer_report.print("max = "+max_candidate+")\r\n");
			writer_report.print("--------------------------------------------------------------------------\r\n");
			writer_report.print("* Output statistics\r\n");
			writer_report.print("Root Mean Square Error = "+rmse+" m"+"\r\n");
			writer_report.print("Bias in East direction = "+meanX+" m (+/- "+stdMeanX+")"+"\r\n");
			writer_report.print("Bias in North direction = "+meanY+" m (+/- "+stdMeanY+")"+"\r\n");
			writer_report.print("Maximal displacement = "+max+" m"+"\r\n");
			writer_report.print("Expected error after map-matching = "+exp+" m"+"\r\n");
			writer_report.print("--------------------------------------------------------------------------\r\n");
			writer_report.print("Median absolute deviation = "+mad+" m"+"\r\n");
			writer_report.print("Sigma posterior estimate = "+sigma_posterior+" m"+"\r\n");
			writer_report.print("--------------------------------------------------------------------------\r\n");
			writer_report.print("* Timing\r\n");
			writer_report.print("Distances precomputation time = "+distancePrecompTimeDouble+" s"+"\r\n");
			writer_report.print("Map-matching operation time = "+mapMatchTimeDouble+" s"+"\r\n");
			writer_report.print("Total running time = "+totalTime+" s"+"\r\n");
			writer_report.print("Map-matching time / track = "+totalTimeByTrack+" s"+"\r\n");
			writer_report.print("--------------------------------------------------------------------------\r\n");
			writer_report.print("* Output folder\r\n");
			writer_report.print(output_folder+"\r\n");
			writer_report.print("--------------------------------------------------------------------------\r\n");


			if (no_feasible){

				writer_report.print("\r\n");

				if (input_tracks.size() == 1){
					writer_report.print("Warning: no feasible path on road network");
				}
				else{

					writer_report.print("Warning: no feasible path on road network for the following track(s):\r\n");

					for (int i=0; i<NOT_FEASIBLE.size(); i++){

						writer_report.print(NOT_FEASIBLE.get(i)+"\r\n");

					}
				}

			}

			if (ERROR_OCCURRED.size() > 0){

				writer_report.print("\r\n");

				if (input_tracks.size() == 1){
					writer_report.print("Error: no solution found");
				}
				else{

					writer_report.print("Error occurred on the following track(s):\r\n");

					for (int i=0; i<ERROR_OCCURRED.size(); i++){

						writer_report.print(ERROR_OCCURRED.get(i)+"\r\n");

					}
				}

			}

			writer_report.close();

			Tools.println("ok");

			return file_report;


		} catch (FileNotFoundException e) {

			if (MapMatching.gui_mode){

				JOptionPane.showMessageDialog(null, "Cannot write report file", "Warning", JOptionPane.WARNING_MESSAGE);

			}else{

				Tools.println("Error: cannot report debug file");
				System.exit(9);

			}

		}


		Interface.reactivateComputeButton();
		return null;

	}


}
