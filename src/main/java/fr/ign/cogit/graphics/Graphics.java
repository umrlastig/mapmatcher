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

package fr.ign.cogit.graphics;

import java.awt.Color;
import java.util.ArrayList;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;

import fr.ign.cogit.network.Network;



import fr.ign.cogit.core.Track;

@SuppressWarnings("serial")
public class Graphics extends Plot {


	private Network network;
	private ArrayList<Track> track;
	private ArrayList<Track> track_mm;

	private int max_track_number = 10;

	public void setNetwork(Network network){this.network = network;}

	public Network getNetwork(){return network;}
	public ArrayList<Track> getTrack(){return track;}
	public ArrayList<Track> getTrackMm(){return track_mm;}

	public void setMaxTrackNumber(int N){max_track_number = N;}

	public void addTrack(Track track){if(this.track.size() < max_track_number){this.track.add(track);}}
	public void addTrackMm(Track track_mm){if(this.track_mm.size() < max_track_number){this.track_mm.add(track_mm);}}


	public Graphics(){

		new Plot();

		network = null;
		track = new ArrayList<Track>();
		track_mm = new ArrayList<Track>();

	}


	public void plot(){

		for (int i=0; i<network.getGeometries().size(); i++){

			Geometry g2 = network.getGeometries().get(i);

			Coordinate[] C = g2.getCoordinates();

			double[] X = new double[C.length];
			double[] Y = new double[C.length];

			for (int j=0; j<C.length; j++){

				X[j] = C[j].x;
				Y[j] = C[j].y;

			}

			addPolyLine(X, Y);

		}


		for(int t=0; t<track.size(); t++){
			
			Track t1 = track.get(t);
			Track t2 = track_mm.get(t);
			
			for (int i=0; i<t1.getX().size(); i++){

				addPoint(t1.getX().get(i), t1.getY().get(i), Color.RED, "fo", 5);
				addPoint(t1.getX().get(i), t1.getY().get(i), Color.RED.darker(), "o", 5);

			}
			

			for (int i=0; i<t2.getX().size(); i++){

				addPoint(t2.getX().get(i), t2.getY().get(i), Color.GREEN, "fo", 5);
				addPoint(t2.getX().get(i), t2.getY().get(i), Color.GREEN.darker(), "o", 5);

			}

			for (int i=0; i<t2.getX().size(); i++){

				double xa = t1.getX().get(i);
				double ya = t1.getY().get(i);

				double xb = t2.getX().get(i);
				double yb = t2.getY().get(i);

				double[] X = {xa, xb};
				double[] Y = {ya, yb};

				Color c = Color.BLUE;

				Color transparent_blue = new Color((float)(c.getRed())/255.f, (float)(c.getGreen())/255.f, (float)(c.getBlue())/255.f, 0.0f);

				addPolyLine(X, Y, transparent_blue);

				double l = Math.sqrt(Math.pow(xa-xb,2)+Math.pow(ya-yb,2));

				double theta = Math.atan2(yb-ya, xb-xa)-Math.PI/2;

				double[] Xf = {0-l/10.0, 0, 0+l/10.0};
				double[] Yf = { 0-l/5.0, 0, 0-l/5.0};

				double[] Xr = {0, 0, 0};
				double[] Yr = {0, 0, 0};


				Xr[0] = Math.cos(theta)*Xf[0] - Math.sin(theta)*Yf[0] + xb;
				Yr[0] = Math.sin(theta)*Xf[0] + Math.cos(theta)*Yf[0] + yb;

				Xr[1] = Math.cos(theta)*Xf[1] - Math.sin(theta)*Yf[1] + xb;
				Yr[1] = Math.sin(theta)*Xf[1] + Math.cos(theta)*Yf[1] + yb;

				Xr[2] = Math.cos(theta)*Xf[2] - Math.sin(theta)*Yf[2] + xb;
				Yr[2] = Math.sin(theta)*Xf[2] + Math.cos(theta)*Yf[2] + yb;



				addPolyLine(Xr, Yr, transparent_blue);


			}

		}

		this.setVisible(true);


	}

}
