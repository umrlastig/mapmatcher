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

package fr.ign.cogit.mapmatcher.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;

import fr.ign.cogit.mapmatcher.core.MapMatching;

// ------------------------------------------------
// Class to manage map-matching parameterization
//------------------------------------------------

public class Parameters {

	// Distributions
	public static int DISTRIBUTION_UNIFORM = 2;
	public static int DISTRIBUTION_EXPONENTIAL = 3;
	public static int DISTRIBUTION_NORMAL = 1;
	public static int DISTRIBUTION_RAYLEIGH = 4;

	// Input files
	public static String input_network_path = "";
	public static String input_track_path = "";

	// Ouptut folder
	public static String output_path = "";

	public static boolean output_debug = false;
	public static boolean output_report = true;
	public static boolean output_parameters = false;


	// Input format
	public static String network_delimiter = ",";
	public static String track_delimiter = ",";
	public static String output_delimiter = ",";
	public static String output_suffix = "_mm.dat";

	public static int network_geom_id = -1;
	public static int network_source_id = -1;
	public static int network_target_id = -1;
	public static int network_edge_id = -1;
	public static int network_oneway_id = -1;

	public static String network_geom_name = "";
	public static String network_source_name = "";
	public static String network_target_name = "";
	public static String network_edge_name = "";
	public static String network_oneway_name = "";

	public static boolean network_header = true;
	public static boolean track_header = true;

	public static int track_columns_x_id = -1;
	public static int track_columns_y_id = -1;
	public static int track_columns_t_id = -1;

	public static String track_columns_x_name = "";
	public static String track_columns_y_name = "";
	public static String track_columns_t_name = "";

	public static String track_date_fmt = "yyyy-MM-dd HH:mm:ss";

	public static String track_error_code = "-1";

	// Computation parameters
	public static int computation_distribution = 1;
	public static double computation_sigma = 10.0;
	public static double computation_beta = 1.0;
	public static double computation_radius = 50.0;
	public static double computation_transition = 0.0;
	public static double computation_autocorrelation = 0.0;
	public static double computation_scope = 100;
	public static double computation_angle = 0.0;

	public static double bias_x = 0.0;
	public static double bias_y = 0.0;
	public static double network_rmse = 0.0;
	public static double confidence_ratio = 1.0;

	public static double computation_speed_limit = Double.MAX_VALUE;

	// Computation options
	public static boolean sort_nodes = true;
	public static boolean failure_skip = false;
	public static boolean make_topology = false;

	public static int max_number_candidates = -1;
	public static double topo_tolerance = 0.01;
	public static double buffer_radius = 300.0;

	public static boolean project_coordinates = false;
	public static boolean precompute_distances = true;
	public static boolean remove_deg_2_nodes = false;
	public static boolean add_spatial_index = false;
	public static boolean rmse_type_before = false;
	public static boolean ref_to_network = false;
	public static boolean index_format_csv = true;
	public static boolean network_inaccuracies = false;
	public static boolean confidence_min_ratio = false;

	public static boolean output_mute = false;
	public static boolean output_errors = true;
	public static boolean output_clear = false;
	public static boolean output_rmse = false;
	public static boolean output_confidence = false;
	public static boolean output_index_all_edge = false;
	public static boolean output_index_coords = false;
	public static boolean output_path_interpolation = false;

	public static boolean graphical_output = false;

	public static String gui_report_path = "";
	public static String gui_parameters_path = "";

	public static String abs_curv_type = "from_source_m";
	public static String distance_buffer = "full_network";


	// List of input files
	public static ArrayList<String> input_track_path_list;


	// Module to read parameters
	public static void load(String path){

		// Prepare output
		input_track_path_list = new ArrayList<String>();

		Scanner scan = null;

		// Read file
		try {scan = new Scanner(new File(path));}
		catch (FileNotFoundException e) {
			Tools.println("Error: parameter file ["+path+"] has not been found");
			System.exit(4);
		}

		// Reading lines
		while(scan.hasNextLine()){

			String line_raw = scan.nextLine();

			String line = line_raw.replace(" ","");

			String regex = "\\s+(?=((\\\\[\\\\\"]|[^\\\\\"])*\"(\\\\[\\\\\"]|[^\\\\\"])*\")*(\\\\[\\\\\"]|[^\\\\\"])*$)";

			line = line_raw.replaceAll(regex, "");


			if (line.length() == 0){

				continue;

			}

			if (line.substring(0,1).equals("#")){

				continue;

			}

			line = (new StringTokenizer(line, "#")).nextToken();


			StringTokenizer st = new StringTokenizer(line, "=");

			line_raw = (new StringTokenizer(line_raw, "#")).nextToken().trim();

			if (!st.hasMoreTokens()){

				Tools.println("Error : incorrect statement ["+line_raw+"]");
				System.exit(3);

			}

			String name = st.nextToken("=");

			if (!st.hasMoreTokens()){

				Tools.println("Error : incorrect statement ["+line_raw+"]");
				System.exit(3);

			}



			String value = st.nextToken("=");



			value = value.replace("\"", "");

			name = name.trim();


			// Affectation
			if (name.equals("input.network.path"))     {input_network_path = value;}
			if (name.equals("input.track.path"))       {input_track_path = value;}
			if (name.equals("output.path"))            {output_path = value;}

			if (name.equals("network.delimiter"))      {network_delimiter = value;}
			if (name.equals("track.delimiter"))        {track_delimiter = value;}
			if (name.equals("output.delimiter"))       {output_delimiter = value;}
			if (name.equals("output.suffix"))          {output_suffix = value;}

			if (name.equals("output.debug"))           {output_debug = Boolean.parseBoolean(value);}
			if (name.equals("output.clear"))           {output_clear = Boolean.parseBoolean(value);}
			if (name.equals("output.report"))          {output_report = Boolean.parseBoolean(value);}
			if (name.equals("output.mute"))            {output_mute = Boolean.parseBoolean(value);}
			if (name.equals("output.errors"))          {output_errors = Boolean.parseBoolean(value);}
			if (name.equals("output.rmse"))     	   {output_rmse = Boolean.parseBoolean(value);}
			if (name.equals("output.confidence"))	   {output_confidence = Boolean.parseBoolean(value);}
			if (name.equals("output.index"))		   {add_spatial_index = Boolean.parseBoolean(value);}

			if (name.equals("network.geom.id"))        {network_geom_id = Integer.parseInt(value);}
			if (name.equals("network.source.id"))      {network_source_id = Integer.parseInt(value);}
			if (name.equals("network.target.id"))      {network_target_id = Integer.parseInt(value);}
			if (name.equals("network.edge.id"))        {network_edge_id = Integer.parseInt(value);}
			if (name.equals("network.oneway.id"))      {network_oneway_id = Integer.parseInt(value);}

			if (name.equals("network.geom.name"))      {network_geom_name = value;}
			if (name.equals("network.source.name"))    {network_source_name = value;}
			if (name.equals("network.target.name"))    {network_target_name = value;}
			if (name.equals("network.edge.name"))      {network_edge_name = value;}
			if (name.equals("network.oneway.name"))    {network_oneway_name = value;}

			if (name.equals("track.columns.x.id"))     {track_columns_x_id = Integer.parseInt(value);}
			if (name.equals("track.columns.y.id"))     {track_columns_y_id = Integer.parseInt(value);}
			if (name.equals("track.columns.t.id"))     {track_columns_t_id = Integer.parseInt(value);}

			if (name.equals("track.columns.x.name"))   {track_columns_x_name = value;}
			if (name.equals("track.columns.y.name"))   {track_columns_y_name = value;}
			if (name.equals("track.columns.t.name"))   {track_columns_t_name = value;}

			if (name.equals("track.date.fmt"))         {track_date_fmt = value;}

			if (name.equals("track.error.code"))       {track_error_code = value;}

			if (name.equals("track.header"))           {track_header = Boolean.parseBoolean(value);}
			if (name.equals("network.header"))         {network_header = Boolean.parseBoolean(value);}

			if (name.equals("computation.sigma"))     		      {computation_sigma = Double.parseDouble(value);}
			if (name.equals("computation.beta"))       		      {computation_beta = Double.parseDouble(value);}
			if (name.equals("computation.radius"))     		      {computation_radius = Double.parseDouble(value);}
			if (name.equals("computation.transition")) 		      {computation_transition = Double.parseDouble(value);}
			if (name.equals("computation.limit.speed"))		      {computation_speed_limit = Double.parseDouble(value);}
			if (name.equals("computation.autocorrelation"))       {computation_autocorrelation = Double.parseDouble(value);}
			if (name.equals("computation.autocorrelation.scope")) {computation_scope = Double.parseDouble(value);}
			if (name.equals("computation.angle"))                 {computation_angle = Double.parseDouble(value);}

			if (name.equals("computation.distribution"))	  {

				if (value.equals("uniform"))     {computation_distribution = Parameters.DISTRIBUTION_UNIFORM;}
				if (value.equals("exponential")) {computation_distribution = Parameters.DISTRIBUTION_EXPONENTIAL;}
				if (value.equals("normal"))      {computation_distribution = Parameters.DISTRIBUTION_NORMAL;}
				if (value.equals("rayleigh"))    {computation_distribution = Parameters.DISTRIBUTION_RAYLEIGH;}

			}

			if (name.equals("option.max.candidates"))         {max_number_candidates = Integer.parseInt(value);}
			if (name.equals("option.failure.skip"))           {failure_skip = Boolean.parseBoolean(value);}
			if (name.equals("option.projection"))             {project_coordinates = Boolean.parseBoolean(value);}
			if (name.equals("option.confidence_ratio"))       {confidence_min_ratio = Boolean.parseBoolean(value);}
			if (name.equals("option.precompute.distances"))   {precompute_distances = Boolean.parseBoolean(value);}
			if (name.equals("option.make.topology"))          {make_topology = Boolean.parseBoolean(value);}
			if (name.equals("option.sort.nodes"))             {sort_nodes = Boolean.parseBoolean(value);}
			if (name.equals("option.simplify"))               {remove_deg_2_nodes = Boolean.parseBoolean(value);}
			if (name.equals("option.ref.network"))			  {ref_to_network = Boolean.parseBoolean(value);}
			if (name.equals("output.index.all.edge"))	      {output_index_all_edge =  Boolean.parseBoolean(value);}
			if (name.equals("output.index.coords"))	          {output_index_coords =  Boolean.parseBoolean(value);}
			if (name.equals("output.path.interpolation"))	  {output_path_interpolation =  Boolean.parseBoolean(value);}

			if (name.equals("option.rmse.type"))			  {rmse_type_before = value.equals("before");}
			if (name.equals("output.index.format"))			  {index_format_csv = value.equals("csv");}

			if (name.equals("option.buffer.type"))		      {distance_buffer = value;}

			if (name.equals("topology.tolerance"))            {topo_tolerance = Double.parseDouble(value);}
			if (name.equals("confidence.ratio"))              {confidence_ratio = Double.parseDouble(value);}

			if (name.equals("network.inaccuracies"))          {network_inaccuracies = Boolean.parseBoolean(value);}

			if (name.equals("bias.x"))						  {bias_x = Double.parseDouble(value);}
			if (name.equals("bias.y"))						  {bias_y = Double.parseDouble(value);}
			if (name.equals("network.rmse"))				  {network_rmse = Double.parseDouble(value);}
			if (name.equals("option.buffer.radius"))		  {buffer_radius = Double.parseDouble(value);}

			if (name.equals("output.abs.type"))				  {abs_curv_type = value;}

			if (name.equals("output.graphics"))				  {graphical_output = Boolean.parseBoolean(value);}


		}


		// Corrections
		input_network_path = input_network_path.replaceAll("(^ )|( $)", "");
		input_track_path = input_track_path.replaceAll("(^ )|( $)", "");
		output_path = output_path.replaceAll("(^ )|( $)", "");


		// ------------------------------------
		// Control
		// ------------------------------------

		boolean error = false;

		if (track_header){

			if (track_columns_x_id != -1){

				Tools.println("Error: track.columns.x.id cannot be defined when track.header = true");
				error = true;

			}

			if (track_columns_y_id != -1){

				Tools.println("Error: track.columns.y.id cannot be defined when track.header = true");
				error = true;

			}

			if (track_columns_t_id != -1){

				Tools.println("Error: track.columns.t.id cannot be defined when track.header = true");
				error = true;

			}

		}

		if (!track_header){

			if (!track_columns_x_name.equals("")){

				Tools.println("Error: track.columns.x.name cannot be defined when track.header = false");
				error = true;

			}

			if (!track_columns_y_name.equals("")){

				Tools.println("Error: track.columns.y.name cannot be defined when track.header = false");
				error = true;

			}

			if (!track_columns_t_name.equals("")){

				Tools.println("Error: track.columns.t.name cannot be defined when track.header = false");
				error = true;

			}

		}


		if (network_header){

			if (network_geom_id != -1){

				Tools.println("Error: network.geom.id cannot be defined when network.header = true");
				error = true;

			}

			if (network_source_id != -1){

				Tools.println("Error: network.source.id cannot be defined when network.header = true");
				error = true;

			}

			if (network_target_id != -1){

				Tools.println("Error: network.target.id cannot be defined when network.header = true");
				error = true;

			}

			if (network_edge_id != -1){

				Tools.println("Error: network.edge.id cannot be defined when network.header = true");
				error = true;

			}

			if (network_oneway_id != -1){

				Tools.println("Error: network.oneway.id cannot be defined when network.header = true");
				error = true;

			}

		}

		if (!network_header){

			if (!network_geom_name.equals("")){

				Tools.println("Error: network.geom.name cannot be defined when network.header = false");
				error = true;

			}

			if (!network_source_name.equals("")){

				Tools.println("Error: network.source.name cannot be defined when network.header = false");
				error = true;

			}

			if (!network_target_name.equals("")){

				Tools.println("Error: network.target.name cannot be defined when network.header = false");
				error = true;

			}

			if (!network_edge_name.equals("")){

				Tools.println("Error: network.edge.name cannot be defined when network.header = false");
				error = true;

			}

			if (!network_oneway_name.equals("")){

				Tools.println("Error: network.oneway.name cannot be defined when network.header = false");
				error = true;

			}

		}

		if (track_columns_t_id == -1){

			if (computation_speed_limit != Double.MAX_VALUE){

				Tools.println("Error: computation.limit.speed cannot be defined when no time stamp is provided");
				error = true;

			}

		}

		if (error){

			System.exit(8);

		}
		//C:\Users\ymeneroux\Desktop\Recherches\18-Mitaka\11-relational_learning\2-map_matching\parameters.txt



	}


	// Module to analyze regular expression
	public static void readMultipleFiles(){

		input_track_path_list = new ArrayList<String>();

		ArrayList<String> input_track_path_list_prelim = new ArrayList<String>();

		if (input_track_path.contains(";")) {

			StringTokenizer st = new StringTokenizer(input_track_path, ";");

			while (st.hasMoreTokens()) {

				input_track_path_list_prelim.add(st.nextToken());

			}

		}
		else {

			input_track_path_list_prelim.add(input_track_path);

		}

		for (int j=0; j<input_track_path_list_prelim.size(); j++) {

			input_track_path = input_track_path_list_prelim.get(j).replace("\\", "/");

			StringTokenizer st = new StringTokenizer(input_track_path, "/");

			ArrayList<String> items = new ArrayList<String>();

			while(st.hasMoreTokens()){

				items.add(st.nextToken());

			}

			String folder = "";

			for (int i=0; i<items.size()-1; i++){

				folder += items.get(i)+"/";

			}

			File[] listOfFiles = (new File(folder)).listFiles();


			for (int i=0; i<listOfFiles.length; i++){

				String file_path = listOfFiles[i].getPath().replace("\\", "/");

				if(file_path.matches(input_track_path.replace("*", ".*"))){

					input_track_path_list.add(file_path);

				}

			}

		}

	}

	// Module to print parameters
	public static void print(){

		PrintWriter writer_param = null;

		String path = output_path+"/parameters.txt";

		if (MapMatching.gui_mode){

			path = Parameters.gui_parameters_path;

		}

		File file_param = new File(path);

		int counter = 2;

		while (file_param.exists()){

			file_param = new File(output_path+"/parameters("+counter+").txt");
			counter ++;

		}

		Tools.print("Report file "+path+" : ");

		try {

			writer_param = new PrintWriter(file_param);

			Date date = new Date();
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

			writer_param.write("# ----------------------------------------------\r\n");
			writer_param.write("# Parameter file for map-matching               \r\n");
			writer_param.write("# ----------------------------------------------\r\n");
			writer_param.write("# User : " + System.getProperty("user.name")+"\r\n");
			writer_param.write("# Date : " + dateFormat.format(date)+"\r\n");
			writer_param.write("# ----------------------------------------------\r\n");

			writer_param.write("\r\n");
			writer_param.write("# Input files \r\n");
			writer_param.write("input.network.path = \""+input_network_path+"\"\r\n");
			writer_param.write("input.track.path = \""+input_track_path+"\"\r\n");

			writer_param.write("\r\n");
			writer_param.write("# Network format \r\n");
			writer_param.write("network.header = "+network_header+"\r\n");
			writer_param.write("network.delimiter = \""+network_delimiter+"\"\r\n");

			if (network_header){
				writer_param.write("network.geom.name = \""+network_geom_name+"\"\r\n");
				writer_param.write("network.source.name = \""+network_source_name+"\"\r\n");
				writer_param.write("network.target.name = \""+network_target_name+"\"\r\n");
				writer_param.write("network.edge.name = \""+network_edge_name+"\"\r\n");
				writer_param.write("network.oneway.name = \""+network_oneway_name+"\"\r\n");
			}else{
				writer_param.write("network.geom.id = "+network_geom_id+"\r\n");
				writer_param.write("network.source.id = "+network_source_id+"\r\n");
				writer_param.write("network.target.id = "+network_target_id+"\r\n");
				writer_param.write("network.edge.id = "+network_edge_id+"\r\n");
				writer_param.write("network.oneway.id = "+network_oneway_id+"\r\n");
			}

			writer_param.write("\r\n");
			writer_param.write("# Track format \r\n");
			writer_param.write("track.header = "+track_header+"\r\n");
			writer_param.write("track.delimiter = \""+track_delimiter+"\"\r\n");

			if (track_header){
				writer_param.write("track.columns.x.name = \""+track_columns_x_name+"\"\r\n");
				writer_param.write("track.columns.y.name = \""+track_columns_y_name+"\"\r\n");
				writer_param.write("track.columns.t.name = \""+track_columns_t_name+"\"\r\n");
			}else{
				writer_param.write("track.columns.x.id = "+track_columns_x_id+"\r\n");
				writer_param.write("track.columns.y.id = "+track_columns_y_id+"\r\n");
				writer_param.write("track.columns.t.id = "+track_columns_t_id+"\r\n");
			}

			writer_param.write("track.error.code = \""+track_error_code+"\"\r\n");
			writer_param.write("track.date.fmt = \""+track_date_fmt+"\"\r\n");

			writer_param.write("\r\n");
			writer_param.write("# Computation parameters \r\n");
			writer_param.write("computation.sigma = "+computation_sigma+"\r\n");
			writer_param.write("computation.beta = "+computation_beta+"\r\n");
			writer_param.write("computation.radius = "+computation_radius+"\r\n");
			writer_param.write("computation.transition = "+computation_transition+"\r\n");
			writer_param.write("computation.limit.speed = "+computation_speed_limit+"\r\n");
			writer_param.write("computation.autocorrelation = "+computation_autocorrelation+"\r\n");
			writer_param.write("computation.autocorrelation.scope = "+computation_scope+"\r\n");
			writer_param.write("computation.angle = "+computation_angle+"\r\n");


			if (computation_distribution == DISTRIBUTION_UNIFORM)      {writer_param.write("computation.distribution = uniform\r\n");}
			if (computation_distribution == DISTRIBUTION_EXPONENTIAL)  {writer_param.write("computation.distribution = exponential\r\n");}
			if (computation_distribution == DISTRIBUTION_NORMAL)       {writer_param.write("computation.distribution = normal\r\n");}
			if (computation_distribution == DISTRIBUTION_RAYLEIGH)     {writer_param.write("computation.distribution = rayleigh\r\n");}

			writer_param.write("\r\n");
			writer_param.write("# Additional options \r\n");
			writer_param.write("option.max.candidates = "+max_number_candidates+"\r\n");
			writer_param.write("option.failure.skip = "+failure_skip+"\r\n");
			writer_param.write("option.confidence.ratio = "+confidence_min_ratio+"\r\n");
			writer_param.write("option.projection = "+project_coordinates+"\r\n");
			writer_param.write("option.precompute.distances = "+precompute_distances+"\r\n");
			writer_param.write("option.buffer.type = "+distance_buffer+"\r\n");
			writer_param.write("option.buffer.radius = "+buffer_radius+"\r\n");
			writer_param.write("option.make.topology = "+make_topology+"\r\n");
			writer_param.write("option.sort.nodes = "+sort_nodes+"\r\n");
			writer_param.write("option.simplify = "+remove_deg_2_nodes+"\r\n");
			writer_param.write("option.ref.network = "+ref_to_network+"\r\n");

			writer_param.write("bias.x = "+bias_x+"\r\n");
			writer_param.write("bias.y = "+bias_y+"\r\n");
			writer_param.write("topology.tolerance = "+topo_tolerance+"\r\n");
			writer_param.write("network.rmse = "+network_rmse+"\r\n");
			writer_param.write("confidence.ratio = "+confidence_ratio+"\r\n");

			writer_param.write("network.inaccuracies = "+network_inaccuracies+"\r\n");

			writer_param.write("\r\n");
			writer_param.write("# Output \r\n");
			writer_param.write("output.path = \""+output_path+"\"\r\n");
			writer_param.write("output.clear = "+output_clear+"\r\n");
			writer_param.write("output.report = "+output_report+"\r\n");
			writer_param.write("output.graphics = "+graphical_output+"\r\n");
			writer_param.write("output.debug = "+output_debug+"\r\n");
			writer_param.write("output.suffix = \""+output_suffix+"\"\r\n");
			writer_param.write("output.delimiter = \""+output_delimiter+"\"\r\n");
			writer_param.write("output.mute = "+output_mute+"\r\n");
			writer_param.write("output.errors = "+output_errors+"\r\n");
			writer_param.write("output.index = "+add_spatial_index+"\r\n");
			writer_param.write("output.rmse = "+output_rmse+"\r\n");
			writer_param.write("output.abs.type = "+abs_curv_type+"\r\n");
			writer_param.write("output.confidence = "+output_confidence+"\r\n");
			writer_param.write("output.index.all.edge = "+output_index_all_edge+"\r\n");
			writer_param.write("output.index.coords = "+output_index_coords+"\r\n");
			writer_param.write("output.path.interpolation = "+output_path_interpolation+"\r\n");
			
			

			if (index_format_csv){

				writer_param.write("output.index.format = csv\r\n");

			}
			else {

				writer_param.write("output.index.format = xml\r\n");

			}


			if (rmse_type_before){

				writer_param.write("output.rmse.type = before\r\n");

			}
			else {

				writer_param.write("output.rmse.type = after\r\n");

			}

			writer_param.close();

			Tools.println("ok");


		} catch (FileNotFoundException e) {

			if (MapMatching.gui_mode){

				JOptionPane.showMessageDialog(null, "Warning: cannot write parameters file", "Warning", JOptionPane.WARNING_MESSAGE);

			}else{

				Tools.println("Error: cannot write parameters file");
				System.exit(14);

			}

		}

	}

	// Module to reset values
	public static void reset(){

		Parameters.DISTRIBUTION_UNIFORM = 2;
		Parameters.DISTRIBUTION_EXPONENTIAL = 3;
		Parameters.DISTRIBUTION_NORMAL = 1;
		Parameters.DISTRIBUTION_RAYLEIGH = 4;

		Parameters.input_network_path = "";
		Parameters.input_track_path = "";

		Parameters.output_path = "";

		Parameters.output_debug = false;
		Parameters.output_report = true;
		Parameters.graphical_output = false;
		Parameters.output_parameters = false;

		Parameters.network_delimiter = ",";
		Parameters.track_delimiter = ",";
		Parameters.output_delimiter = ",";
		Parameters.output_suffix = "_mm.dat";

		Parameters.network_geom_id = -1;
		Parameters.network_source_id = -1;
		Parameters.network_target_id = -1;
		Parameters.network_edge_id = -1;
		Parameters.network_oneway_id = -1;

		Parameters.network_geom_name = "";
		Parameters.network_source_name = "";
		Parameters.network_target_name = "";
		Parameters.network_edge_name = "";
		Parameters.network_oneway_name = "";

		Parameters.network_header = true;
		Parameters.track_header = true;

		Parameters.track_columns_x_id = -1;
		Parameters.track_columns_y_id = -1;
		Parameters.track_columns_t_id = -1;

		Parameters.track_columns_x_name = "";
		Parameters.track_columns_y_name = "";
		Parameters.track_columns_t_name = "";

		Parameters.track_date_fmt = "yyyy-MM-dd HH:mm:ss";

		Parameters.track_error_code = "-1";

		Parameters.computation_distribution = 1;
		Parameters.computation_sigma = 10.0;
		Parameters.computation_beta = 1.0;
		Parameters.computation_radius = 50.0;
		Parameters.computation_transition = 0.0;
		Parameters.computation_autocorrelation = 0.0;
		Parameters.computation_scope = 100;
		Parameters.computation_angle = 0.0;

		Parameters.bias_x = 0.0;
		Parameters.bias_y = 0.0;
		Parameters.network_rmse = 0.0;

		Parameters.computation_speed_limit = Double.MAX_VALUE;

		Parameters.abs_curv_type = "from_source_m";

		Parameters.sort_nodes = false;
		Parameters.failure_skip = false;
		Parameters.make_topology = false;

		Parameters.max_number_candidates = -1;
		Parameters.topo_tolerance = 0.01;
		Parameters.confidence_ratio = 0.0;

		Parameters.project_coordinates = false;
		Parameters.precompute_distances = true;
		Parameters.remove_deg_2_nodes = false;
		Parameters.add_spatial_index = false;
		Parameters.rmse_type_before = false;
		Parameters.ref_to_network = false;
		Parameters.confidence_min_ratio = false;

		Parameters.output_mute = false;
		Parameters.output_errors = true;
		Parameters.output_clear = false;
		Parameters.output_rmse = false;
		Parameters.output_confidence = false;
		Parameters.index_format_csv = true;
		Parameters.output_index_all_edge = false;
		Parameters.output_index_coords = false;
		Parameters.network_inaccuracies = false;
		Parameters.output_path_interpolation = false;

		Parameters.buffer_radius = 300;
		Parameters.distance_buffer = "full_network";

		Parameters.graphical_output = false;

		Parameters.gui_report_path = "";
		Parameters.gui_parameters_path = "";

		Parameters.input_track_path_list = new ArrayList<String>();

	}

}

