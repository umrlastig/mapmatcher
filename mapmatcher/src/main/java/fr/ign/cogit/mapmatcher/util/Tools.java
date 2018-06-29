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

import java.awt.Color;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.TreeMap;

import javax.swing.JOptionPane;
import java.util.Map.Entry;

import fr.ign.cogit.mapmatcher.network.Network;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import fr.ign.cogit.mapmatcher.core.Main;
import fr.ign.cogit.mapmatcher.core.MapMatching;

public class Tools {

	public static int maxBareSize = 65; 

	public static PrintStream output_stream = System.out;

	// Geodetic parameters
	private static double a = 6378137.0;
	private static double e = 0.08181919106;
	private static double pi = Math.PI;

	private static double lon0 = 0.0;
	private static double lat0 = 0.0;

	private static double X0 = 0.0;
	private static double Y0 = 0.0;

	private static int binary_precision = 64;

	private static ArrayList<Double> DISTORSION = new ArrayList<Double>();

	public static void setLongitudeOrigine(double lon0) {Tools.lon0 = lon0*pi/180;}
	public static void setLatitudeOrigine(double lat0) {Tools.lat0 = lat0*pi/180;}

	public static void setProjectionOrigine(Coordinate longlat) {

		setLongitudeOrigine(longlat.x);
		setLatitudeOrigine(longlat.y);

	}

	public static void setXOrigine(double X0) {Tools.X0 = X0;}
	public static void setYOrigine(double Y0) {Tools.Y0 = Y0;}

	public static void setCartoOrigine(Coordinate XY) {

		setXOrigine(XY.x);
		setYOrigine(XY.y);

	}

	public static int getNumberOfProjectedPoints(){return DISTORSION.size();}

	// ----------------------------------------------------------------------
	// Module to compute isometric latitude
	// ----------------------------------------------------------------------
	private static double isolat(double latitude){

		double L = Math.tan(pi/4+latitude/2);
		L *= Math.pow((1-e*Math.sin(latitude))/(1+e*Math.sin(latitude)), e/2);

		return Math.log(L);

	}


	// ----------------------------------------------------------------------
	// Module to compute inverse projection of X
	// For simplicty, computation is performed with binary search
	// Note that this solution is not optimal in terms of computation cost
	// ----------------------------------------------------------------------
	private static double x2longitude(double x) {

		double longitude_min = -pi;
		double longitude_max = +pi;

		double longitude = 0;

		for (int i=0; i<binary_precision; i++){

			longitude = 0.5*(longitude_min + longitude_max);

			double xtest = longitude2x(longitude);

			if (xtest > x){

				longitude_max = longitude;

			}
			else{

				longitude_min = longitude;

			}

		}

		return longitude;

	}

	// ----------------------------------------------------------------------
	// Module to compute inverse projection of Y
	// For simplicty, computation is performed with binary search
	// Note that this solution is not optimal in terms of computation cost
	// ----------------------------------------------------------------------
	private static double y2latitude(double y) {

		double latitude_min = -pi/2;
		double latitude_max = +pi/2;

		double latitude = 0;

		for (int i=0; i<binary_precision; i++){

			latitude = 0.5*(latitude_min + latitude_max);

			double ytest = latitude2y(latitude);

			if (ytest > y){

				latitude_max = latitude;

			}
			else{

				latitude_min = latitude;

			}

		}

		return latitude;

	}

	// ----------------------------------------------------------------------
	// Module to project data in geographic coordinates WGS84
	// ----------------------------------------------------------------------
	public static Coordinate mercator2geo(Coordinate coords_mercator) {

		Coordinate coords = new Coordinate();

		double x = coords_mercator.x;
		double y = coords_mercator.y;

		coords.x = x2longitude(x)*180/pi;
		coords.y = y2latitude(y)*180/pi;

		return coords;

	}


	// ----------------------------------------------------------------------
	// Module to project data in geographic coordinates WGS84
	// ----------------------------------------------------------------------
	public static void toGeo(Coordinate xy) {

		double x = xy.x;
		double y = xy.y;

		xy.x = x2longitude(x)*180/pi;
		xy.y = y2latitude(y)*180/pi;

	}



	// ----------------------------------------------------------------------
	// Module to project longitude in plane coordinates
	// ----------------------------------------------------------------------
	private static double longitude2x(double longitude) {

		if (Math.abs(longitude) > 2*pi){

			Tools.println("Error: input coordinates must be in radians");

			return Double.MIN_VALUE;

		}

		double k0=1.0;

		double X = (k0*Math.cos(lat0)*a/Math.sqrt(1-(e*e)*Math.pow(Math.sin(lat0),2)))*(longitude-lon0);
		X -= X0;

		return X;

	}

	// ----------------------------------------------------------------------
	// Module to project latitude in plane coordinates
	// ----------------------------------------------------------------------
	private static double latitude2y(double latitude) {

		if (Math.abs(latitude) > pi/2){

			Tools.println("Error: input coordinates must be in radians");

			return Double.MIN_VALUE;

		}

		double k0 = 1.0;

		double Y = (k0*Math.cos(lat0)*a/Math.sqrt(1-(e*e)*Math.pow(Math.sin(lat0),2)))*(isolat(latitude)-isolat(lat0));
		Y -= Y0;

		return Y;

	}


	// ----------------------------------------------------------------------
	// Module to project data in plane coordinates
	// ----------------------------------------------------------------------
	public static Coordinate geo2mercator(Coordinate coords_geo) {

		Coordinate coords = new Coordinate();

		double longitude = coords_geo.x*pi/180;
		double latitude = coords_geo.y*pi/180;

		coords.x = longitude2x(longitude);
		coords.y = latitude2y(latitude);

		return coords;

	}

	// ----------------------------------------------------------------------
	// Module to project data in plane coordinates
	// ----------------------------------------------------------------------
	public static boolean toMercator(Coordinate coords_geo) {

		double longitude = coords_geo.x*pi/180;
		double latitude = coords_geo.y*pi/180;

		coords_geo.x = longitude2x(longitude);
		coords_geo.y = latitude2y(latitude);

		if (coords_geo.x == Double.MIN_VALUE){return false;}
		if (coords_geo.y == Double.MIN_VALUE){return false;}

		return true;

	}

	// ----------------------------------------------------------------------
	// Function to compute linear module on a given point
	// ----------------------------------------------------------------------
	public static double distorsion(Coordinate coords) {

		double x = coords.x;
		double y = coords.y;

		double yproj = latitude2y(y*pi/180)+Y0;

		double n = yproj/(y*pi/180.0-lon0);
		double w = Math.sqrt(1-Math.pow(e*Math.sin(x*pi/180), 2));
		double N = a/w;
		double m = n/(N*Math.cos(x*pi/180));

		return m;

	}

	// ----------------------------------------------------------------------
	// Function to save distorsion 
	// ----------------------------------------------------------------------
	public static void addDistorsion(Coordinate coords) {

		DISTORSION.add(distorsion(coords));

	}

	// ----------------------------------------------------------------------
	// Module to get average distorsion on field
	// ----------------------------------------------------------------------
	public static double average_distorsion() {

		return mean(DISTORSION);

	}

	// ----------------------------------------------------------------------
	// Module to get average distorsion on field
	// ----------------------------------------------------------------------
	public static double absolute_average_distorsion() {

		return abs_mean(DISTORSION);

	}

	// ----------------------------------------------------------------------
	// Module to get maximal distorsion on field
	// ----------------------------------------------------------------------
	public static double maximal_distorsion() {

		return max(DISTORSION);

	}

	// ----------------------------------------------------------------------
	// Module to get minimal distorsion on field
	// ----------------------------------------------------------------------
	public static double minimal_distorsion() {

		return min(DISTORSION);

	}

	// ----------------------------------------------------------------------
	// Module to get maximal absolute distorsion on field
	// ----------------------------------------------------------------------
	public static double max_abs_distorsion() {

		return Math.max(minimal_distorsion(), maximal_distorsion());

	}


	// ----------------------------------------------------------------------
	// Module to get barycenter coordinates
	// ----------------------------------------------------------------------
	public static double[] barycenter(ArrayList<double[]> COORDS) {

		double[] barycenter = new double[2];

		ArrayList<Double> X = new ArrayList<Double>();
		ArrayList<Double> Y = new ArrayList<Double>();

		for (int i=0; i<COORDS.size(); i++) {

			X.add(COORDS.get(i)[0]);
			Y.add(COORDS.get(i)[1]);

		}

		barycenter[0] = mean(X);
		barycenter[1] = mean(Y);

		return barycenter;

	}

	// ----------------------------------------------------------------------
	// Module to get lower left coordinates
	// ----------------------------------------------------------------------
	public static double[] lowerLeft(ArrayList<double[]> COORDS) {

		double[] lower_left = new double[2];

		ArrayList<Double> X = new ArrayList<Double>();
		ArrayList<Double> Y = new ArrayList<Double>();

		for (int i=0; i<COORDS.size(); i++) {

			X.add(COORDS.get(i)[0]);
			Y.add(COORDS.get(i)[1]);

		}

		lower_left[0] = min(X);
		lower_left[1] = min(Y);

		return lower_left;

	}

	// ----------------------------------------------------------------------
	// Module to compute mean of a network
	// ----------------------------------------------------------------------
	public static Coordinate barycenter(Network network){

		double meanx = 0;
		double meany = 0;

		int counter = 0;

		for (int i=0; i<network.getGeometries().size(); i++){

			for (int j=0; j<network.getGeometries().get(i).getCoordinates().length; j++){

				counter ++;

				meanx += network.getGeometries().get(i).getCoordinates()[j].x;
				meany += network.getGeometries().get(i).getCoordinates()[j].y;

			}

		}

		meanx /= (double) counter;
		meany /= (double) counter;

		return new Coordinate(meanx, meany);

	}

	// ----------------------------------------------------------------------
	// Module to compute the lower left point of a network
	// ----------------------------------------------------------------------
	public static Coordinate lowerLeft(Network network){

		double minx = Double.MAX_VALUE;
		double miny = Double.MAX_VALUE;


		for (int i=0; i<network.getGeometries().size(); i++){

			for (int j=0; j<network.getGeometries().get(i).getCoordinates().length; j++){

				minx = Math.min(minx, network.getGeometries().get(i).getCoordinates()[j].x);
				miny = Math.min(miny, network.getGeometries().get(i).getCoordinates()[j].y);

			}

		}

		return new Coordinate(minx, miny);

	}




	// -------------------------------------------------------------------
	// Module to compute distance between point and segment
	// -------------------------------------------------------------------
	public static double[] distance_to_segment(double x0, double y0, double x1, double y1, double x2, double y2){

		double[] output = new double[3];

		// Segment length
		double l = Math.sqrt((x2-x1)*(x2-x1)+(y2-y1)*(y2-y1));

		// Normalized scalar product
		double psn = ((x0-x1)*(x2-x1) + (y0-y1)*(y2-y1))/l;

		double X = Math.max(x1, x2);
		double Y = Math.max(y1, y2);

		double x = Math.min(x1, x2);
		double y = Math.min(y1, y2);

		double xproj = x1 + psn/l*(x2-x1);
		double yproj =	y1 + psn/l*(y2-y1);

		xproj = Math.min(Math.max(xproj, x), X);
		yproj = Math.min(Math.max(yproj, y), Y);

		// Compute distance
		double dist = Math.sqrt((x0-xproj)*(x0-xproj)+(y0-yproj)*(y0-yproj));

		// Prepare output
		output[0] = xproj;
		output[1] = yproj;
		output[2] = dist;

		return output;

	}

	// -------------------------------------------------------------------
	// Module to compute distance between point and polyline
	// -------------------------------------------------------------------
	public static double[] distance_to_polyline(double x, double y, ArrayList<Coordinate> C){

		double[] output = new double[4];

		ArrayList<Double> distances = new ArrayList<Double>();
		ArrayList<Double> xproj = new ArrayList<Double>();
		ArrayList<Double> yproj = new ArrayList<Double>();

		// ---------------------------------------------------------------
		// Compute distances to segments
		// ---------------------------------------------------------------

		for (int i=1; i<C.size(); i++){

			double[] d = distance_to_segment(x, y, C.get(i-1).x, C.get(i-1).y, C.get(i).x, C.get(i).y);

			xproj.add(d[0]); 
			yproj.add(d[1]); 
			distances.add(d[2]); 

		}

		// ---------------------------------------------------------------
		// Search minimal distance
		// ---------------------------------------------------------------

		int min_index = 0;
		double min_dist = distances.get(0);

		for (int i=1; i<distances.size(); i++){

			if (distances.get(i) < min_dist){

				min_dist = distances.get(i);
				min_index = i;

			}

		}

		// Prepare output
		output[0] = xproj.get(min_index);
		output[1] = yproj.get(min_index);
		output[2] = min_dist;
		output[3] = min_index;

		return output;

	}

	// -------------------------------------------------------------------
	// Module to compute distance between point edge source and target
	// -------------------------------------------------------------------
	public static double[] distance_to_vertices(double x, double y, Geometry geom, int l){

		double distance[] = {0.0, 0.0};

		for (int i = 1; i<=l; i++){

			double dx = geom.getCoordinates()[i].x - geom.getCoordinates()[i-1].x;
			double dy = geom.getCoordinates()[i].y - geom.getCoordinates()[i-1].y;

			distance[0] += Math.sqrt(dx*dx + dy*dy);

		}

		double dx = x - geom.getCoordinates()[l].x;
		double dy = y - geom.getCoordinates()[l].y;

		distance[0] += Math.sqrt(dx*dx + dy*dy);

		distance[1] = geom.getLength()-distance[0];

		return distance;

	}

	// -------------------------------------------------------------------
	// Sum of a list
	// -------------------------------------------------------------------
	public static double sum(ArrayList<Double> X){

		double sum = 0;

		for (int i=0; i<X.size(); i++){

			if (Double.isNaN(X.get(i))){

				continue;

			}

			sum += X.get(i);

		}

		return sum;

	} 


	// -------------------------------------------------------------------
	// Mean of a list
	// -------------------------------------------------------------------
	public static double mean(ArrayList<Double> X){

		return sum(X)/X.size();

	} 

	// -------------------------------------------------------------------
	// Absolute mean of a list
	// -------------------------------------------------------------------
	public static double abs_mean(ArrayList<Double> X){

		double sum = 0;

		for (int i=0; i<X.size(); i++){

			if (Double.isNaN(X.get(i))){

				continue;

			}

			sum += Math.abs(X.get(i));

		}

		return sum/X.size();

	} 

	// -------------------------------------------------------------------
	// Min of a list
	// -------------------------------------------------------------------
	public static double min(ArrayList<Double> X){

		double min = Double.MAX_VALUE;

		for (int i=0; i<X.size(); i++){

			if (Double.isNaN(X.get(i))){

				continue;

			}

			min = Math.min(min, X.get(i));

		}

		return min;

	} 

	// -------------------------------------------------------------------
	// Max of a list
	// -------------------------------------------------------------------
	public static double max(ArrayList<Double> X){

		double max = Double.MIN_VALUE;

		for (int i=0; i<X.size(); i++){

			if (Double.isNaN(X.get(i))){

				continue;

			}

			max = Math.max(max, X.get(i));

		}

		return max;

	} 


	// -------------------------------------------------------------------
	// Max displacement
	// -------------------------------------------------------------------
	public static double max(ArrayList<Double> X, ArrayList<Double> Y){

		double max = 0;

		for (int i=0; i<X.size(); i++){

			double d = Math.pow(X.get(i), 2) +  Math.pow(Y.get(i), 2);

			max = Math.max(max, d);

		}

		return Math.sqrt(max);

	} 

	// -------------------------------------------------------------------
	// Standard deviation of a list
	// -------------------------------------------------------------------
	public static double std(ArrayList<Double> X){

		double mean1 = 0;
		double mean2 = 0;

		for (int i=0; i<X.size(); i++){

			mean1 += X.get(i);
			mean2 += Math.pow(X.get(i), 2);

		}

		mean1 /= X.size();
		mean2 /= X.size();

		double std = Math.sqrt(mean2-mean1*mean1);

		return std;

	} 

	// -------------------------------------------------------------------
	// RMSE of a couple of lists X and Y
	// -------------------------------------------------------------------
	public static double rmse(ArrayList<Double> X, ArrayList<Double> Y){

		double mean = 0;

		for (int i=0; i<X.size(); i++){

			mean += Math.pow(X.get(i), 2) +  Math.pow(Y.get(i), 2);

		}

		mean /= X.size();

		return Math.sqrt(mean);

	} 

	// -------------------------------------------------------------------
	// Median absolute deviation of lists X and Y
	// -------------------------------------------------------------------
	public static double mad(ArrayList<Double> X, ArrayList<Double> Y){

		ArrayList<Double> values = new ArrayList<Double>();

		for (int i=0; i<X.size(); i++){

			values.add(Math.sqrt(Math.pow(X.get(i), 2) +  Math.pow(Y.get(i), 2)));

		}

		Collections.sort(values);

		double median = 0;

		if (values.size() % 2 == 0){

			median = values.get(values.size()/2) +  values.get(values.size()/2+1);

		}
		else{

			median = values.get(values.size()/2+1);

		}

		return median;

	} 


	// -----------------------------------------------------------------------------
	// Method to round number
	// -----------------------------------------------------------------------------
	public static double round(double x, int decimal){

		return Math.floor(x*Math.pow(10, decimal))/Math.pow(10, decimal);

	}

	// -----------------------------------------------------------------------------
	// Method to print error in console or gui
	// -----------------------------------------------------------------------------
	public static void printError(String line){

		if (MapMatching.gui_mode){

			Main.gui.progressBar.setForeground(Color.RED);

			JOptionPane.showMessageDialog(null, line, "Error", JOptionPane.ERROR_MESSAGE);

			Main.gui.progressBar.setForeground(Color.GREEN.darker());

		}else{

			System.out.print(line);

		}

	}


	// -----------------------------------------------------------------------------
	// Method to check if output must be printed in console
	// -----------------------------------------------------------------------------
	public static void print(String line){

		if ((!MapMatching.gui_mode) && (!Parameters.output_mute)){

			output_stream.print(line);

		}

	}


	// -----------------------------------------------------------------------------
	// Method to check if output must be printed in console
	// -----------------------------------------------------------------------------
	public static void println(String line){

		if ((!MapMatching.gui_mode) && (!Parameters.output_mute)){

			output_stream.println(line);

		}

	}

	// -----------------------------------------------------------------------------
	// Method to update a progress bar
	// -----------------------------------------------------------------------------
	public static void progressPercentage(int remain, int total, boolean gui) {

		if (gui){

			progressPercentageGUI(remain, total);

		}
		else{

			progressPercentageConsole(remain, total);

		}

	}

	// -----------------------------------------------------------------------------
	// Method to create a progress bar
	// -----------------------------------------------------------------------------
	public static void progressPercentageConsole(int remain, int total) {

		if (remain > total) {
			throw new IllegalArgumentException();
		}

		int remainProcent =(int) (((double) remain / (double) total) * maxBareSize);

		char defaultChar = '-';
		String icon = "*";
		String bare = new String(new char[maxBareSize]).replace('\0', defaultChar) + "]";

		StringBuilder bareDone = new StringBuilder();
		bareDone.append("[");

		for (int i = 0; i < remainProcent; i++) {

			bareDone.append(icon);

		}

		String bareRemain = bare.substring(remainProcent, bare.length());
		Tools.print("\r" + bareDone + bareRemain + " " + remainProcent*100/maxBareSize + " %");

		if (remain == total) {
			Tools.print("\n");
		}

	}

	// -----------------------------------------------------------------------------
	// Method to create a progress bar in GUI
	// -----------------------------------------------------------------------------
	public static void progressPercentageGUI(int remain, int total) {

		int value = (int)(100*(double)remain/(double)total);

		Main.gui.progressBar.setValue(value);

	}



	// ----------------------------------------------------------------------
	// Module to ask question in the console
	// ----------------------------------------------------------------------
	public static String input(String question){

		@SuppressWarnings("resource")
		Scanner reader = new Scanner(System.in);  

		Tools.print(question);

		return reader.nextLine();

	}

	// ----------------------------------------------------------------------
	// Module to merge two line string geometries
	// ----------------------------------------------------------------------
	public static Geometry merge(Geometry g1, Geometry g2){

		GeometryFactory gf = new GeometryFactory();

		Coordinate[] C = new Coordinate[g1.getCoordinates().length+g2.getCoordinates().length-1];

		for (int i=0; i<g1.getCoordinates().length; i++){

			C[i] = g1.getCoordinates()[i];

		}

		for (int i=1; i<g2.getCoordinates().length; i++){

			C[g1.getCoordinates().length+i-1] = g2.getCoordinates()[i];

		}

		return gf.createLineString(C);

	}

	// ----------------------------------------------------------------------
	// Module to reverse a line string geometries
	// ----------------------------------------------------------------------
	public static Geometry reverse(Geometry g){

		GeometryFactory gf = new GeometryFactory();

		Coordinate[] C = new Coordinate[g.getCoordinates().length];

		for (int i=0; i<g.getCoordinates().length; i++){

			C[g.getCoordinates().length-i-1] = g.getCoordinates()[i];

		}

		return gf.createLineString(C);

	}

	// ----------------------------------------------------------------------
	// Method to merge index blocs
	// ----------------------------------------------------------------------
	public static void mergeXml(String mergedFile, String fileToInsert) {

		try {

			// Lecture du fichier "fusion"
			DocumentBuilderFactory documentBuilderFactory0 = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder0 = documentBuilderFactory0.newDocumentBuilder();
			Document document0 = documentBuilder0.parse(mergedFile);
			Element root0 = document0.getDocumentElement();

			// System.out.println("Fichier 0 - total path : " + root0.getElementsByTagName("paths").getLength());
			// System.out.println("Fichier 0 - total edge : " + root0.getElementsByTagName("edge").getLength());

			DocumentBuilderFactory documentBuilderFactory1 = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder1 = documentBuilderFactory1.newDocumentBuilder();
			Document document1 = documentBuilder1.parse(fileToInsert);
			Element root1 = document1.getDocumentElement();

			// System.out.println("Fichier 1 - total path : " + root1.getElementsByTagName("paths").getLength());
			// System.out.println("Fichier 1 - total edge : " + root1.getElementsByTagName("edge").getLength());


			// =============================================================================================

			//   Fusion des PATHS

			// Noeud parent dans lequel on va ajouter les enfants

			Node noeudPath0 = root0.getElementsByTagName("paths").item(0);


			// Liste des identifiants déjà présents dans le fichier

			TreeMap<Integer, Node> listExistingId = new TreeMap<Integer, Node>();

			for (int j = 0; j < noeudPath0.getChildNodes().getLength(); j++) {
				Node node = noeudPath0.getChildNodes().item(j);
				if (node instanceof Element) {
					Element trackNode = (Element)node;
					listExistingId.put(Integer.parseInt(trackNode.getAttribute("id")), trackNode);
				}
			}


			Node nodePath1 = root1.getElementsByTagName("paths").item(0);


			// On ajoute les tracks du path 1 qui n'y sont pas encore
			for (int j = 0; j < nodePath1.getChildNodes().getLength(); j++) {
				Node node = nodePath1.getChildNodes().item(j);
				if (node instanceof Element) {
					Element trackNode = (Element)node;
					int id = Integer.parseInt(trackNode.getAttribute("id"));

					// Est-ce que c'est un nouveau premier élément ?
					if (listExistingId.get(id) == null) {

						// On récupère le noeud juste inférieur car c'est trié
						if (listExistingId.headMap(id).size() > 0) {

							// Dernier noeud dont l'identifiant est inférieur à celui qu'on veut fusionner
							Node n = listExistingId.get(listExistingId.headMap(id).lastKey());

							// On ajoute le noeud
							Node firstDocImportedNode = document0.importNode(trackNode, true);
							noeudPath0.insertBefore(firstDocImportedNode, n.getNextSibling());

							// On met a jour la liste des identifiants
							listExistingId.put(id, firstDocImportedNode);

						} else {

							// C'est un nouveau premier élément
							Node firstDocImportedNode = document0.importNode(trackNode, true);
							noeudPath0.insertBefore(firstDocImportedNode, noeudPath0.getFirstChild());

							// Mise à jour la liste des identifiants ?
							listExistingId.put(id, firstDocImportedNode);

						}

					}

				}

			}


			// =============================================================================================

			//   Fusion des EDGES
			NodeList listExistingEdge = root0.getElementsByTagName("edge");

			// Liste des identifiants EDGE#TRACK déjà présents dans le fichier
			TreeMap<Integer, Object[]> treeExistingEdgeTrack = new TreeMap<Integer, Object[]>();
			for (int j = 0; j < listExistingEdge.getLength(); j++) {
				Node node = listExistingEdge.item(j);
				if (node instanceof Element) {
					Element edgeNode = (Element)node;
					int idEdge = Integer.parseInt(edgeNode.getAttribute("id"));

					// Liste des tracks
					List<Integer> listTrack = new ArrayList<Integer>();
					for (int k = 0; k < edgeNode.getChildNodes().getLength(); k++) {
						Node nodeEnfant = edgeNode.getChildNodes().item(k);
						if (nodeEnfant instanceof Element) {
							Element trackNode = (Element)nodeEnfant;
							int idTrack = Integer.parseInt(trackNode.getAttribute("id"));
							listTrack.add(idTrack);
						}
					}

					Object[] treeEdgeTrack = new Object[2];
					treeEdgeTrack[0] = edgeNode;
					treeEdgeTrack[1] = listTrack;
					treeExistingEdgeTrack.put(idEdge, treeEdgeTrack);

				}

			}

			// On boucle sur les edges-tracks à insérer
			NodeList listEdgeToInsert = root1.getElementsByTagName("edge");

			for (int j = 0; j < listEdgeToInsert.getLength(); j++) {
				
				Node nodeE = listEdgeToInsert.item(j);
				if (nodeE instanceof Element) {
					Element edgeNode = (Element)nodeE;
					int idEdge = Integer.parseInt(edgeNode.getAttribute("id"));

					// Est-ce que le edge existe ?
					if (treeExistingEdgeTrack.get(idEdge) == null) {

						// Est-ce que c'est un nouveau premier élément ?
						if (treeExistingEdgeTrack.headMap(idEdge).size() <= 0) {

							Entry<Integer, Object[]> ent = treeExistingEdgeTrack.firstEntry();
							Element elt = (Element)ent.getValue()[0];
							Node firstDocImportedNode = document0.importNode(edgeNode, true);
							elt.getParentNode().insertBefore(firstDocImportedNode, elt);

						} else {

							// On cherche le plus grand id edge inférieur à idEdge
							int lastKey = treeExistingEdgeTrack.headMap(idEdge).lastKey();
							Element lastElt = (Element)treeExistingEdgeTrack.get(lastKey)[0];

							// On ajoute le edge
							Node firstDocImportedNode = document0.importNode(edgeNode, true);
							lastElt.getParentNode().insertBefore(firstDocImportedNode, lastElt.getNextSibling());


							// On met a jour la liste des identifiants
							// listExistingId.put(id, firstDocImportedNode);

							Object[] treeEdgeTrack = new Object[2];
							treeEdgeTrack[0] = firstDocImportedNode;
							treeEdgeTrack[1] = new ArrayList<Integer>();
							treeExistingEdgeTrack.put(idEdge, treeEdgeTrack);

							// Le count n'a pas besoin d'être mis à jour

						}

					} else {

						// Le EDGE existe deja, on ajoute uniquement les tracks

						// On recupere le COUNT
						Element e = (Element) treeExistingEdgeTrack.get(idEdge)[0];
						int count = Integer.parseInt(e.getAttribute("count"));
						int add = Integer.parseInt(edgeNode.getAttribute("count"));
						((Element) treeExistingEdgeTrack.get(idEdge)[0]).setAttribute("count", Integer.toString(count + add));

						// On récupère d'abord les tracks

						for (int k = 0; k < edgeNode.getChildNodes().getLength(); k++) {
							Node nodeEnfant = edgeNode.getChildNodes().item(k);
							if (nodeEnfant instanceof Element) {
								Element trackAInserer = (Element)nodeEnfant;
								int idTrack = Integer.parseInt(trackAInserer.getAttribute("id"));

								// On recupere le edge qui a le même numero
								Object[] tabEdgeToMerged = treeExistingEdgeTrack.get(idEdge);
								Element edgeToMerged = (Element) tabEdgeToMerged[0];

								// On cherche le track juste apres
								// par defaut c'est le premier
								Element t0 = (Element) edgeToMerged.getElementsByTagName("track").item(0);
								for (int cpt = 0; cpt < edgeToMerged.getElementsByTagName("track").getLength(); cpt++) {
									Element t = (Element)edgeToMerged.getElementsByTagName("track").item(cpt);
									int idAComp = Integer.parseInt(t.getAttribute("id"));

									if (idTrack < idAComp) {
										t0 = t;
										break;
									}
								}

								Node firstDocImportedNode = document0.importNode(trackAInserer, true);
								t0.getParentNode().insertBefore(firstDocImportedNode, t0);

							}

						}

						// break;

					}

				}

			}


			// =============================================================================================

			//   On rééecrit en streaming
			DOMSource source = new DOMSource(document0);

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

			StreamResult result = new StreamResult(mergedFile);
			transformer.transform(source, result);


		} catch (Exception e) {

			e.printStackTrace();

		}

	}


}


