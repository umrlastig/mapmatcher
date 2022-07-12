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

package fr.umr.lastig.mapmatcher.core;


import java.awt.EventQueue;

import javax.swing.ImageIcon;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.locationtech.jts.geom.Geometry;

import fr.umr.lastig.mapmatcher.graphics.Interface;
import fr.umr.lastig.mapmatcher.network.Network;
import fr.umr.lastig.mapmatcher.util.Loaders;
import fr.umr.lastig.mapmatcher.util.Parameters;
import fr.umr.lastig.mapmatcher.util.Tools;


public class Main {

	public static Interface gui;
	public static ImageIcon img = new ImageIcon("resources\\grenouille-couleur.jpg");

	public static void main(String[] args) {
		MapMatching.gui_mode = (args.length == 0);
		if (MapMatching.gui_mode) {
			executeInterface();
		} else {
			executeConsole(args[0]);
		}
	}


	// ----------------------------------------------------------------------
	// Execute method in a console user interface
	// ----------------------------------------------------------------------
	public static void executeConsole (String parameter_file) {
		long start = System.currentTimeMillis();

		Parameters.load(parameter_file);

		Loaders.parameterize();
		MapMatching.parameterize();  
		
		System.out.println(Parameters.distance_buffer);

		// Buffering on 1st track
		if (Parameters.distance_buffer.equals("1st_track") && (Parameters.precompute_distances)){

			Track track = Loaders.loadTrack(Parameters.input_track_path_list.get(0));

			if (track.getX().size() > 1){

				Geometry geom = track.makeBuffer(4.0/3.0*Parameters.buffer_radius);
				Loaders.setBuffer(geom);

			}

		}

		// Buffering on all tracks
		if (Parameters.distance_buffer.equals("buffered_tracks") && (Parameters.precompute_distances)){


			Geometry geom = Loaders.loadTrack(Parameters.input_track_path_list.get(0)).makeBuffer(4.0/3.0*Parameters.buffer_radius);


			for (int f=0; f<Parameters.input_track_path_list.size(); f++){

				Track track = Loaders.loadTrack(Parameters.input_track_path_list.get(f));

				if (track.getX().size() < 2){continue;}

				geom = geom.union(track.makeBuffer(4.0/3.0*Parameters.buffer_radius));

				Tools.progressPercentage(f, Parameters.input_track_path_list.size(), MapMatching.gui_mode);

			}

			Loaders.setBuffer(geom);

			Tools.progressPercentage(Parameters.input_track_path_list.size(), Parameters.input_track_path_list.size(), MapMatching.gui_mode);

		}


		Tools.print("Loading network...");
		Network network = Loaders.loadNetwork(Parameters.input_network_path);
		Tools.println("ok");

		// Mercator projection
		if (Parameters.project_coordinates){

			network.toLocalMercator();

		}


		MapMatching.setNetwork(network);

		MapMatching.operate();

		long time = (System.currentTimeMillis()-start)/1000;

		Tools.println("Map-matching solution computed with success (elapsed time = "+time+" s)");

	}


	// ----------------------------------------------------------------------
	// Execute method in a graphic user interface
	// ----------------------------------------------------------------------
	public static void executeInterface(){

		try {

			String os = System.getProperty("os.name").toLowerCase();

			// For windows os
			if (os.contains("windows")){

				UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");

			}

			// For linux os
			if ((os.contains("linux")) || (os.contains("unix"))){

				//UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");

			}


		}
		catch (UnsupportedLookAndFeelException e) {
			// handle exception
		}
		catch (ClassNotFoundException e) {
			// handle exception
		}
		catch (InstantiationException e) {
			// handle exception
		}
		catch (IllegalAccessException e) {
			// handle exception
		}

		EventQueue.invokeLater(new Runnable() {

			public void run() {

				try {

					gui = new Interface();
					gui.frmMapMatcher.setVisible(true);


					gui.frmMapMatcher.setIconImage(img.getImage());

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}


}

