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

package fr.umr.lastig.mapmatcher.graphics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

import javax.swing.JComponent;

import fr.umr.lastig.mapmatcher.core.Main;


@SuppressWarnings("serial")
public class Plot extends JComponent implements MouseListener, MouseMotionListener, MouseWheelListener {

	private int margin = 30;
	public int grid_rx = 30;
	public int grid_ry = 30;

	public double xmin = Double.MAX_VALUE;
	public double ymin = Double.MAX_VALUE;
	public double xmax = Double.MIN_VALUE;
	public double ymax = Double.MIN_VALUE;

	private ArrayList<Double[]> POLYLINES_X;
	private ArrayList<Double[]> POLYLINES_Y;
	private ArrayList<Color> POLYLINES_C;

	private ArrayList<Double> POINTS_X;
	private ArrayList<Double> POINTS_Y;
	private ArrayList<Color> POINTS_C;
	private ArrayList<String> POINTS_S;
	private ArrayList<Integer> POINTS_T;

	public String xlabel = "X";
	public String ylabel = "Y";
	private String xlabel2 = "X";
	private String ylabel2 = "Y";

	private String title = "Plot";
	private Font f = new Font("Arial Bold", Font.ITALIC, 16);
	private Font flab = new Font("Arial", Font.PLAIN, 12);

	public boolean coordinatesVisible = true;
	public boolean gridVisible = true;

	private Color backgroundColor = Color.WHITE;
	private Color linesColor = Color.BLACK;
	private Color frameColor = Color.BLACK;
	private Color titleColor = Color.BLACK;
	private Color labelColor = Color.BLACK;
	private Color gridColor = new Color(0.f,0.f,0.f,0.2f);

	public ArrayList<Color> getPointColors(){return POINTS_C;}
	public ArrayList<Color> getPolylineColors(){return POLYLINES_C;}

	public void setTitle(String title){this.title = title;}

	private boolean zoomRectangle = false;
	private int zoomRectangleX = -1;
	private int zoomRectangleY = -1;
	private int zoomRectangleH = -1;
	private int zoomRectangleW = -1;

	public boolean extracted = false;


	public void addPoint(double x, double y){

		addPoint(x, y, Color.BLACK);

	}

	public void addPoint(double x, double y, Color c){

		addPoint(x, y, c, "fo");

	}

	public void addPoint(double x, double y, Color c, String s){

		addPoint(x, y, c, s, 5);

	}

	public void addPoint(double x, double y, Color c, String s, int t){

		POINTS_X.add(x);
		POINTS_Y.add(y);
		POINTS_C.add(c);
		POINTS_S.add(s);
		POINTS_T.add(t);

		xmin = Math.min(xmin, x);
		ymin = Math.min(ymin, y);
		xmax = Math.max(xmax, x);
		ymax = Math.max(ymax, y);

	}

	public void addPoints(double[] X, double[] Y){

		addPoints(X, Y, Color.BLACK);

	}

	public void addPoints(double[] X, double[] Y, Color c){

		addPoints(X, Y, c, "fo");

	}

	public void addPoints(double[] X, double[] Y, Color c, String s){

		addPoints(X, Y, c, s, 5);

	}

	public void addPoints(double[] X, double[] Y, Color c, String s, int t){

		for (int i=0; i<X.length; i++){

			addPoint(X[i], Y[i], c, s, t);

		}

	}

	public void addPolyLine(double[] X, double[] Y){

		addPolyLine(X, Y, linesColor);

	}

	public void addPolyLine(double[] X, double[] Y, Color c){

		if (X.length != Y.length){

			System.err.println("Error : X and Y must have same dimension");
			System.exit(1);

		}

		if (X.length == 0){

			System.err.println("Error : X and Y must contain at least one point");
			System.exit(0);

		}

		Double[] tempx = new Double[X.length];
		Double[] tempy = new Double[Y.length];

		for (int i=0; i<X.length; i++){
			tempx[i] = new Double(X[i]);
		}
		for (int i=0; i<Y.length; i++){
			tempy[i] = new Double(Y[i]);
		}

		POLYLINES_X.add(tempx);
		POLYLINES_Y.add(tempy);
		POLYLINES_C.add(c);

		for (int i=0; i<X.length; i++){

			if (X[i] > xmax){xmax = X[i];}
			if (X[i] < xmin){xmin = X[i];}
			if (Y[i] > ymax){ymax = Y[i];}
			if (Y[i] < ymin){ymin = Y[i];}

		}

	}

	public void reset(){

		POLYLINES_X = new ArrayList<Double[]>();
		POLYLINES_Y = new ArrayList<Double[]>();
		POLYLINES_C = new ArrayList<Color>();

		POINTS_X = new ArrayList<Double>();
		POINTS_Y = new ArrayList<Double>();
		POINTS_C = new ArrayList<Color>();
		POINTS_S = new ArrayList<String>();
		POINTS_T = new ArrayList<Integer>();

		repaint();

	}

	public Plot(){

		POLYLINES_X = new ArrayList<Double[]>();
		POLYLINES_Y = new ArrayList<Double[]>();
		POLYLINES_C = new ArrayList<Color>();

		POINTS_X = new ArrayList<Double>();
		POINTS_Y = new ArrayList<Double>();
		POINTS_C = new ArrayList<Color>();
		POINTS_S = new ArrayList<String>();
		POINTS_T = new ArrayList<Integer>();


	}


	// Graphics
	public void paint(Graphics g) {

		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);

		// Background
		g.setColor(backgroundColor);
		g.fillRect(0, 0, getWidth(), getHeight()); 


		// Grid
		double rx = (double)(getWidth()-2*margin)/grid_rx;
		double ry = (double)(getHeight()-2*margin)/grid_ry;

		// Style
		Graphics2D g2d = (Graphics2D) g.create();
		Stroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{2,1}, 0);
		g2d.setStroke(dashed);
		g2d.setColor(gridColor);

		if (gridVisible){

			// X_axis grid
			for (int xg=1; xg<=grid_rx; xg++){

				g2d.drawLine((int)(xg*rx+margin), margin, (int)(xg*rx+margin), getHeight()-margin);

			}

			// Y-axis grid
			for (int yg=1; yg<=grid_ry; yg++){

				g2d.drawLine(margin, (int)(yg*ry+margin), getWidth()-margin, (int)(yg*ry+margin));

			}

		}


		// Polylines plot
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		for (int i=0; i<POLYLINES_X.size(); i++){



			int[] Xl = new int[POLYLINES_X.get(i).length];
			int[] Yl = new int[POLYLINES_X.get(i).length];

			for (int j=0; j<POLYLINES_X.get(i).length; j++){

				int[] temp = transformCoordinates(POLYLINES_X.get(i)[j], POLYLINES_Y.get(i)[j]);

				Xl[j] = temp[0];
				Yl[j] = temp[1];

			}


			g.setColor(POLYLINES_C.get(i));

			g.drawPolyline(Xl, Yl, Xl.length);

		}


		// Points plot
		for (int i=0; i<POINTS_X.size(); i++){

			int[] coords = transformCoordinates(POINTS_X.get(i), POINTS_Y.get(i));

			g.setColor(POINTS_C.get(i));

			if (POINTS_S.get(i).equals("o")){
				g.drawOval(coords[0]-POINTS_T.get(i)/2, coords[1]-POINTS_T.get(i)/2, POINTS_T.get(i), POINTS_T.get(i));
			}

			if (POINTS_S.get(i).equals("fo")){

				g.fillOval(coords[0]-POINTS_T.get(i)/2, coords[1]-POINTS_T.get(i)/2, POINTS_T.get(i), POINTS_T.get(i));

			}

			if (POINTS_S.get(i).equals("s")){

				g.drawRect(coords[0]-POINTS_T.get(i)/2, coords[1]-POINTS_T.get(i)/2, POINTS_T.get(i), POINTS_T.get(i));

			}

			if (POINTS_S.get(i).equals("fs")){

				g.fillRect(coords[0]-POINTS_T.get(i)/2, coords[1]-POINTS_T.get(i)/2, POINTS_T.get(i), POINTS_T.get(i));

			}

			if (POINTS_S.get(i).equals("x")){

				g.drawLine(coords[0]-POINTS_T.get(i)/2, coords[1]-POINTS_T.get(i)/2, coords[0]+POINTS_T.get(i)/2, coords[1]+POINTS_T.get(i)/2);
				g.drawLine(coords[0]-POINTS_T.get(i)/2, coords[1]+POINTS_T.get(i)/2, coords[0]+POINTS_T.get(i)/2, coords[1]-POINTS_T.get(i)/2);

			}

			if (POINTS_S.get(i).equals("^")){

				int[] TX = {(int) (coords[0]-0.7*POINTS_T.get(i)), coords[0], (int) (coords[0]+0.7*POINTS_T.get(i))};
				int[] TY = {(int)(coords[1]+0.3*POINTS_T.get(i)), (int)(coords[1]-POINTS_T.get(i)+0.3*POINTS_T.get(i)), (int)(coords[1]+0.3*POINTS_T.get(i))};

				g.drawPolygon(TX, TY, 3);

			}

			if (POINTS_S.get(i).equals("f^")){

				int[] TX = {(int) (coords[0]-0.7*POINTS_T.get(i)), coords[0], (int) (coords[0]+0.7*POINTS_T.get(i))};
				int[] TY = {(int)(coords[1]+0.3*POINTS_T.get(i)), (int)(coords[1]-POINTS_T.get(i)+0.3*POINTS_T.get(i)), (int)(coords[1]+0.3*POINTS_T.get(i))};

				g.fillPolygon(TX, TY, 3);

			}

			if (POINTS_S.get(i).equals("v")){

				int[] TX = {(int) (coords[0]-0.7*POINTS_T.get(i)), coords[0], (int) (coords[0]+0.7*POINTS_T.get(i))};
				int[] TY = {coords[1], coords[1]+POINTS_T.get(i), coords[1]};

				g.drawPolygon(TX, TY, 3);

			}

			if (POINTS_S.get(i).equals("fv")){

				int[] TX = {(int) (coords[0]-0.7*POINTS_T.get(i)), coords[0], (int) (coords[0]+0.7*POINTS_T.get(i))};
				int[] TY = {coords[1], coords[1]+POINTS_T.get(i), coords[1]};

				g.fillPolygon(TX, TY, 3);

			}

		}

		// Zoom rectangle
		if (zoomRectangle){
			Stroke dashed2 = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{4,4}, 0);
			g2d.setStroke(dashed2);
			g2d.setColor(Color.RED.darker());

			int x0 = zoomRectangleX;
			int y0 = zoomRectangleY;

			if (zoomRectangleW < 0){x0 = x0 + zoomRectangleW; zoomRectangleW = -zoomRectangleW;}
			if (zoomRectangleH < 0){y0 = y0 + zoomRectangleH; zoomRectangleH = -zoomRectangleH;}

			g2d.drawRect(x0, y0, zoomRectangleW, zoomRectangleH);
		}

		// Frame plot
		g.setColor(backgroundColor);
		g.fillRect(0, 0, getWidth()-margin, margin);
		g.fillRect(0, 0, margin, getHeight());
		g.fillRect(0, getHeight()-margin, getWidth(), margin);
		g.fillRect(getWidth()-margin, 0, margin, getHeight());

		// Frame
		g.setColor(frameColor);
		g.drawRect(margin, margin, getWidth()-2*margin,  getHeight()-2*margin);

		// Title
		g.setColor(titleColor);
		g.setFont(f);
		FontMetrics metrics = g.getFontMetrics(g.getFont());
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.drawString(title, getWidth()/2 - metrics.stringWidth(title)/2, margin/2+metrics.getHeight()/2);

		// X label
		if (!coordinatesVisible){xlabel2 = xlabel;}
		g.setColor(labelColor);
		g.setFont(flab);
		metrics = g.getFontMetrics(g.getFont());
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.drawString(xlabel2, getWidth()/2 - metrics.stringWidth(xlabel2)/2, getHeight()-margin/2+10);

		// Y label
		if (!coordinatesVisible){ylabel2 = ylabel;}
		AffineTransform affineTransform = new AffineTransform();
		affineTransform.rotate(Math.toRadians(-90), 0, 0);
		Font rotatedFont = flab.deriveFont(affineTransform);
		g2d.setFont(rotatedFont); g2d.setColor(labelColor);
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.drawString(ylabel2, margin/2, getHeight()/2+metrics.stringWidth(ylabel2)/2);


	}

	// Conversion from space coordinates to frame coordinates
	private int[] transformCoordinates(double x, double y){

		double mx = (xmax-xmin)/16.0;
		double my = (ymax-ymin)/16.0;

		double xmin2 = xmin - mx;
		double ymin2 = ymin - my;
		double xmax2 = xmax + mx;
		double ymax2 = ymax + my;


		double xnorm = (x-xmin2)/(xmax2-xmin2);
		double ynorm = (y-ymin2)/(ymax2-ymin2);

		int[] transformation = {0, 0};

		transformation[0] = (int)(xnorm*(getWidth()-2*margin))+margin;
		transformation[1] = (int)((1-ynorm)*(getHeight()-2*margin))+margin;

		return transformation;

	}

	// Conversion from frame coordinates to space coordinates
	private double[] transformCoordinatesReverse(int posx, int posy){

		double mx = (xmax-xmin)/16.0;
		double my = (ymax-ymin)/16.0;

		double xmin2 = xmin - mx;
		double ymin2 = ymin - my;
		double xmax2 = xmax + mx;
		double ymax2 = ymax + my;

		double[] transformation = {0, 0};

		transformation[0] = (double)(posx-margin)/(double)(getWidth()-2*margin)*(xmax2-xmin2)+xmin2;
		transformation[1] = (1.0-(double)(posy-margin)/(double)(getHeight()-2*margin))*(ymax2-ymin2)+ymin2;

		return transformation;

	}


	int startX = -1; 
	int startY = -1;

	int curX = -1; 
	int curY = -1;

	int inDrag = 0;

	int oldX;
	int oldY;

	int oldStartX = -1;
	int oldStartY = -1;

	int oldWidth = -1;
	int oldHeight = -1;

	@Override
	public void mouseClicked(MouseEvent e) {


		if (e.getClickCount() == 2){

			if (!extracted){

				GraphicsFrame frame = new GraphicsFrame(Main.gui.graphics);
				frame.setVisible(true);

			}

			Main.gui.graphics.extracted = true;

		}

	}

	@Override
	public void mousePressed(MouseEvent e) {

		Point p = e.getPoint();

		startX = p.x;
		startY = p.y;
		inDrag = e.getButton();

		if (inDrag == 3){

			zoomRectangleX = startX;
			zoomRectangleY = startY;

		}

	}

	@Override
	public void mouseReleased(MouseEvent e) {

		inDrag = 0;

		oldX = -1;
		oldStartX = startX;
		oldStartY = startY;
		oldWidth = curX - startX;
		oldHeight = curY - startY;

		// Rescaling
		if ((e.getButton() == 3) && (zoomRectangle)){

			double[] p1 = transformCoordinatesReverse(zoomRectangleX, zoomRectangleY);
			double[] p2 = transformCoordinatesReverse(zoomRectangleX + zoomRectangleW, zoomRectangleY + zoomRectangleH);

			zoomRectangle = false;

			xmin = p1[0];
			ymin = p2[1];
			xmax = p2[0];
			ymax = p1[1];

			repaint();

		}

	}

	@Override
	public void mouseEntered(MouseEvent e) {


	}

	@Override
	public void mouseExited(MouseEvent e) {


	}

	@Override
	public void mouseDragged(MouseEvent e) {

		Point p = e.getPoint();

		curX = p.x;
		curY = p.y;

		// Translation
		if (inDrag == 1) {

			double[] p1 = transformCoordinatesReverse(startX, startY);
			double[] p2 = transformCoordinatesReverse(p.x, p.y);

			int dx = (int)(p1[0]-p2[0]);
			int dy = (int)(p1[1]-p2[1]);

			xmin = xmin + dx;
			xmax = xmax + dx;

			ymin = ymin + dy;
			ymax = ymax + dy;

			repaint();

		}

		// Rescaling
		if (inDrag == 3) {

			zoomRectangle = true;

			zoomRectangleW = p.x - zoomRectangleX;
			zoomRectangleH = p.y - zoomRectangleY;

			repaint();

		}

		startX = curX;
		startY = curY;

	}

	@Override
	public void mouseMoved(MouseEvent e) {

		double[] transfo = transformCoordinatesReverse(e.getX(), e.getY());

		xlabel2 = xlabel+" = "+Math.floor(transfo[0]*1000)/1000+" m";
		ylabel2 = ylabel+" = "+Math.floor(transfo[1]*1000)/1000+" m";

		repaint();

	}


	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		
		int notches = e.getWheelRotation();

		int i = e.getX();
		int j = e.getY();

		double[] conv = transformCoordinatesReverse(i, j);

		double x = conv[0];
		double y = conv[1];
		

		while(notches != 0){

			if (notches < 0){
			
			xmin = xmin + (x-xmin)*0.002;
			ymin = ymin + (y-ymin)*0.002;
			xmax = xmax - (xmax-x)*0.002;
			ymax = ymax - (ymax-y)*0.002;
			
				notches ++;

			}
			else{

			xmin = xmin - (x-xmin)*0.002;
			ymin = ymin - (y-ymin)*0.002;
			xmax = xmax + (xmax-x)*0.002;
			ymax = ymax + (ymax-y)*0.002;

				notches --;

			}

		}

		repaint();

	}


}

