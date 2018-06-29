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

package fr.ign.cogit.mapmatcher.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.mapmatcher.util.Parameters;
import fr.ign.cogit.mapmatcher.util.Tools;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;

public class Track {


	private ArrayList<Double> X;
	private ArrayList<Double> Y;
	private List<Double> lon;
	private List<Double> lat;
	private ArrayList<String> T;
	private ArrayList<Long> Tms;


	private String path = "";

	public ArrayList<Double> getX(){return X;}
	public ArrayList<Double> getY(){return Y;}
	public List<Double> getLon(){return lon;}
	public List<Double> getLat(){return lat;}
	public ArrayList<String> getDate(){return T;}
	public ArrayList<Long> getTime(){return Tms;}

	public String getPath(){return path;}

	public Track(ArrayList<Double> X, ArrayList<Double> Y){

		this.X = X;
		this.Y = Y;

	}
	
	public Track(ArrayList<Double> X, ArrayList<Double> Y, ArrayList<String> T, ArrayList<Long> Tms, List<Double> lons, List<Double> lats){

      this.X = X;
      this.Y = Y;
      this.T = T;
      this.Tms = Tms;
      this.lon = lons;
      this.lat = lats;

  }

	public Track(ArrayList<Double> X, ArrayList<Double> Y, ArrayList<String> T, ArrayList<Long> Tms, String path){

		this.X = X;
		this.Y = Y;
		this.T = T;
		this.Tms = Tms;
		this.path = path;

	}

	// ----------------------------------------------
	// Module to get unbiased coordinates
	// ----------------------------------------------
	public void removeBias(){

		for (int i=0; i<X.size(); i++){

			X.set(i, X.get(i)-Parameters.bias_x);
			Y.set(i, Y.get(i)-Parameters.bias_y);

		}

	}

	// ----------------------------------------------
	// Module to convert a track in Mercator
	// ----------------------------------------------
	public int toLocalMercator(){

		// For each polyline
		for (int i=0; i<X.size(); i++){

			// Current coordinateF
			Coordinate cc = new Coordinate(X.get(i), Y.get(i));

			// Local mercator projection
			Coordinate plan = Tools.geo2mercator(cc);

			if (plan.x == Double.MIN_VALUE){return -1;}
			if (plan.y == Double.MIN_VALUE){return -1;}

			// Local mercator distorsion
			Tools.addDistorsion(cc);

			X.set(i, plan.x);
			Y.set(i, plan.y);

		}

		return 0;

	}

	// ----------------------------------------------
	// Module to compute inverse projection
	// ----------------------------------------------
	public void toWGS84(){

		// For each polyline
		for (int i=0; i<X.size(); i++){

			// Current coordinate
			Coordinate cc = new Coordinate(X.get(i), Y.get(i));

			// Local mercator projection
			Coordinate plan = Tools.mercator2geo(cc);

			// Local mercator distorsion
			Tools.addDistorsion(cc);

			X.set(i, plan.x);
			Y.set(i, plan.y);

		}

	}


	// ----------------------------------------------
	// Module to print track in a file
	// ----------------------------------------------
	public void print(String path){

		PrintWriter writer = null;

		(new File(path)).delete();

		try {
			writer = new PrintWriter(new File(path));
		} catch (FileNotFoundException e) {

			Tools.println("Error: cannot print track");
			System.exit(13);

		}

		String chaine = "id,time_stamp,x,y\r\n";

		writer.print(chaine);

		for (int i=0; i<X.size(); i++){

			chaine = i+",";
			chaine += T.get(i)+",";
			chaine += X.get(i)+",";
			chaine += Y.get(i)+"\r\n";

			writer.print(chaine);

		}

		writer.close();

	}


	// ----------------------------------------------
	// Module to print track in an osm file
	// ----------------------------------------------
	public void printOSM(String path){

		PrintWriter pw = null;

		try {

			pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(path))));

		} catch (IOException e) {

			System.out.println("Error: impossible to print track in OSM file");
			System.exit(19);

		}

		StringBuilder output = new StringBuilder();

		output.append("<?xml version='1.0' encoding='UTF-8'?>\r\n");
		output.append("<osm version='0.6' generator='JOSM'>\r\n");
		output.append("  <bounds minlat='"+Tools.min(Y)+"' minlon='"+Tools.min(X)+"' maxlat='"+Tools.max(Y)+"' maxlon='"+Tools.max(X)+"' origin='OpenStreetMap server' />\r\n");

		for (int i=0; i<X.size(); i++){

			output.append("  <node id='"+i+"' timestamp='2009-08-02T03:36:00Z' user='none' visible='true' version='1' lat='"+Y.get(i)+"' lon='"+X.get(i)+"' />\r\n");

		}



		output.append("<way id='0' timestamp='2009-08-02T03:37:41Z' user='none' visible='true' version='1'>\r\n");

		for (int i=0; i<X.size(); i++){

			output.append("  <nd ref='"+i+"' />\r\n");

		}

		output.append("    <tag k='name' v='track' />\r\n");

		output.append("</way>\r\n");
		output.append("</osm>\r\n");

		pw.write(output.toString());

		pw.close();

	}

	// ----------------------------------------------
	// Module to decompose track in bounding boxes
	// ----------------------------------------------
	public ArrayList<String> generateBBOXES(double r) {

		ArrayList<String> BBOXES = new ArrayList<String>();

		double xmin = Tools.min(X) - 2*r;
		double ymin = Tools.min(Y) - 2*r;
		double xmax = Tools.max(X) + 2*r;
		double ymax = Tools.max(Y) + 2*r;

		int nx = (int) (Math.floor((xmax-xmin)/r)+1);
		int ny = (int) (Math.floor((ymax-ymin)/r)+1);

		xmax = nx*r+xmin;
		ymax = ny*r+ymin;

		int[][] grid = new int[nx][ny];

		for (int xi=0; xi<nx; xi++) {

			for (int yi=0; yi<ny; yi++) {

				grid[xi][yi] = 0;

			}

		}

		for (int i=0; i<X.size(); i++) {

			int xi = (int) ((X.get(i)-xmin)/r);
			int yi = (int) ((Y.get(i)-ymin)/r);

			grid[xi][yi] = 1;

		}

		for (int xi=1; xi<nx-1; xi++) {

			for (int yi=1; yi<ny-1; yi++) {

				if (grid[xi][yi] == 1) {

					grid[xi-1][yi-1] = 2;
					grid[xi-1][yi-0] = 2;
					grid[xi-1][yi+1] = 2;
					grid[xi-0][yi-1] = 2;
					grid[xi-0][yi+1] = 2;
					grid[xi+1][yi-1] = 2;
					grid[xi+1][yi-0] = 2;
					grid[xi+1][yi-1] = 2;

				}

			}

		}

		for (int xi=1; xi<nx-1; xi++) {

			for (int yi=1; yi<ny-1; yi++) {

				if (grid[xi][yi] > 0) {

					double x1 = xmin + xi*r;
					double y1 = ymin + yi*r;

					double x2 = x1 + r;
					double y2 = y1 + r;


					BBOXES.add(y1+","+x1+","+y2+","+x2);

				}

			}

		}

		return BBOXES;

	}



	// ----------------------------------------------
	// Module to bufferize a track
	// ----------------------------------------------
	public Geometry makeBuffer(double radius) {

		Coordinate[] tabCoord = new Coordinate[X.size()];

		for (int i = 0; i < X.size(); i++) {

			Coordinate c = new Coordinate(X.get(i),Y.get(i));
			tabCoord[i] = c;

		}

		LineString l = new GeometryFactory().createLineString(tabCoord);

		return l.buffer(radius);		
		
	}



}
