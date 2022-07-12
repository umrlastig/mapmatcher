package fr.umr.lastig.mapmatcher;

import java.util.ArrayList;

import fr.umr.lastig.mapmatcher.core.MapMatching;
import fr.umr.lastig.mapmatcher.network.Network;
import fr.umr.lastig.mapmatcher.util.Loaders;
import fr.umr.lastig.mapmatcher.util.Parameters;


public class AltituderandoMapMatcher {
	
	private static final String URL_NETWORK     = "./data/topo_l93_2d.csv"; // 
    private static final String URL_SORTIE      = "./data/res/";
    private static final String URL_TRACE_r843  = "./data/r843.csv";
	
    public static void main(String[] args) {
    	
    	try {
            
    		setParameters();
            Loaders.parameterize();
            MapMatching.gui_mode = false;
            
            // =============================================================================
            ArrayList<String> input_track_path_list = getTraces();
            Parameters.input_track_path_list = input_track_path_list;
            System.out.println("Nb traces = " + input_track_path_list.size());
            
            MapMatching.parameterize();
            
            
            // =============================================================================
            //   Loading network 
            Network network = Loaders.loadNetwork(URL_NETWORK);
            // Mercator projection
            // network.toLocalMercator();
            // Precomputing distances on network
            MapMatching.setNetwork(network);
            
            
            // =============================================================================
            //   Appariement
            MapMatching.operate();
            
            
    	} catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	
	/**
	 * Chargement des traces.
	 * @return
	 */
	public static ArrayList<String> getTraces() {
		ArrayList<String> listTrack = new ArrayList<String>();
        listTrack.add(URL_TRACE_r843);
        return listTrack;
    }
	
	
	/**
     * 
     */
    public static void setParameters() {
        
        Parameters.computation_sigma = 4.0; 
        Parameters.computation_radius = 35.0;  // plus c'est grand, plus on peut avoir des erreurs, mais on recale
        Parameters.computation_distribution = Parameters.DISTRIBUTION_NORMAL;
          
        Parameters.computation_beta = 0.2;
        Parameters.computation_transition = 0.0;
        Parameters.computation_speed_limit = 1.7976931348623157E308;
        Parameters.computation_autocorrelation = 60.0;
        Parameters.computation_scope = 100.0;
        Parameters.computation_angle = 0.0;
    
        // # Additional options 
        Parameters.max_number_candidates = -1;
        Parameters.failure_skip = true;
        Parameters.confidence_min_ratio = false;
        Parameters.project_coordinates = false;  /// ------
        
        Parameters.distance_buffer = "buffered_tracks";
        Parameters.buffer_radius = 300.0;
        Parameters.precompute_distances = true;
        
        Parameters.make_topology = false;    // --  true
        Parameters.sort_nodes = true;
        Parameters.remove_deg_2_nodes = false;
        Parameters.ref_to_network = false;
        Parameters.bias_x = 0.0;
        Parameters.bias_y = 0.0;
        Parameters.topo_tolerance = 0.01;
        Parameters.network_rmse = 0.0;
        Parameters.confidence_ratio = 0.0;
        Parameters.network_inaccuracies = false;
        
        // ------
        Parameters.track_header = true;
        Parameters.track_delimiter = ",";
        Parameters.track_columns_x_id = 2;
        Parameters.track_columns_y_id = 3;
        Parameters.track_columns_t_id = -1;
        //Parameters.track_columns_t_id = 4;
        Parameters.track_error_code = "-1";
        //Parameters.track_date_fmt = "yyyy-mm-dd hh:mm:ss";
        Parameters.track_date_fmt = "dd/mm/yyyy hh:mm:ss";
          
        // ------
        Parameters.network_header = true;
        Parameters.network_delimiter = ",";
        Parameters.network_geom_name = "wkt";
        Parameters.network_source_name = "source";
        Parameters.network_target_name = "target";
        Parameters.network_edge_name = "link_id";
        Parameters.network_oneway_name = "";
          
        Parameters.output_clear = false;
        Parameters.output_debug = false;
        Parameters.output_parameters = true;
        Parameters.output_report = true;
        // Parameters.output_graphics = false;
          
        Parameters.output_mute = false;
        Parameters.output_errors = false;
        // Parameters.output_index = false;
        Parameters.output_rmse = true;
        // Parameters.output_abs_type = from_source_m;
        Parameters.output_confidence = false;  /// TEMPS !!!
        Parameters.output_index_all_edge = false;
        Parameters.output_index_coords = false;
        // Parameters.output_index_format = "csv";
        // Parameters.output_rmse_type = "after";
        
        Parameters.input_track_path_list = new ArrayList<String>();
        
        Parameters.output_path = URL_SORTIE;
        Parameters.add_spatial_index = true;
        Parameters.output_index_format_csv = false;
    }
}
