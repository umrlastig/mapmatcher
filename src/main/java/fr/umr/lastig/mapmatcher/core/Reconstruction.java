package fr.umr.lastig.mapmatcher.core;

import java.io.FileWriter;
import java.io.IOException;

import fr.umr.lastig.mapmatcher.network.Network;
import fr.umr.lastig.mapmatcher.util.Loaders;

/**
 *
 * 
 *
 */
public class Reconstruction {

	
	public static void execute(String[] args) {
		
		double ks = 1.25;
		double epsilon = 0.001;
		
		String input_network = "/home/marie-dominique/CHOUCAS/MAPMATCHER/res4/network_topo.wkt";
		
		// Export des tronçons parcourus dans l’ordre (avec doublon)
	    // String input_tracks  = "./data/c2c/reconstruction/troncon_itineraire_trie.csv";
        
        String output_reconstruction_csv = "/home/marie-dominique/CHOUCAS/MAPMATCHER/reconstruction/reconstruction.dat";
        String output_reconstruction_wkt = "/home/marie-dominique/CHOUCAS/MAPMATCHER/reconstruction/reconstruction.wkt";

		Loaders.parameterize2();
		
		// ===============================================================================
		System.out.println("-------------------------------");
		System.out.print("Chargement du reseau");
		
		// Building network graph
		Network network = Loaders.loadNetwork(input_network);

		System.out.println(" ok ");
		System.out.println("-------------------------------");
				
			
		FileWriter fileWriter1 = null;
		FileWriter fileWriter2 = null;

		try {
			fileWriter1 = new FileWriter(output_reconstruction_csv);
			fileWriter2 = new FileWriter(output_reconstruction_wkt);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
}
