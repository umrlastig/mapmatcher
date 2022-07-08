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

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButton;

import fr.umr.lastig.mapmatcher.core.Main;

@SuppressWarnings("serial")
public class GraphicsFrame extends JFrame {


	private boolean display_network = true;
	private boolean display_raw_sequence = true;
	private boolean display_mm_sequence = true;
	private boolean display_vectors = false;


	Graphics g;


	public GraphicsFrame(final Graphics g){


		this.g = g;

		JMenuBar menuBar = new JMenuBar();

		JMenu fichier = new JMenu("Graphics");
		JMenuItem item0 = new JMenuItem("Reset");
		JMenuItem item00 = new JMenuItem("Square");
		JMenuItem item1 = new JMenuItem("Close");
		JMenuItem item2 = new JMenuItem("Quit");

		JMenu couches = new JMenu("Layers");
		final JCheckBox item3 = new JCheckBox("Network");
		final JCheckBox item4 = new JCheckBox("Raw sequence");
		final JCheckBox item5 = new JCheckBox("Map-matched sequence");
		final JCheckBox item6 = new JCheckBox("Displacement vectors");

		item3.setSelected(true);
		item4.setSelected(true);
		item5.setSelected(true);
		item6.setSelected(false);

		JMenu options = new JMenu("Options");
		final JCheckBox item7 = new JCheckBox("Grid");
		final JCheckBox item8 = new JCheckBox("Coordinates");

		item7.setSelected(true);
		item8.setSelected(true);

		JMenu gres =  new JMenu("Grid resolution");
		final JRadioButton g5 = new JRadioButton("5");
		final JRadioButton g10 = new JRadioButton("10");
		final JRadioButton g15 = new JRadioButton("15");
		final JRadioButton g20 = new JRadioButton("20");
		final JRadioButton g30 = new JRadioButton("30");
		final JRadioButton g50 = new JRadioButton("50");
		final JRadioButton g100 = new JRadioButton("100");

		gres.add(g5);
		gres.add(g10);
		gres.add(g15);
		gres.add(g20);
		gres.add(g30);
		gres.add(g50);
		gres.add(g100);

		g30.setSelected(true);


		// Grid resolution
		g5.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				g.grid_rx = 5;
				g.grid_ry = 5;

				if (!g5.isSelected()){g5.setSelected(true);}

				if (g5.isSelected()){

					g5.setSelected(true);
					g10.setSelected(false);
					g15.setSelected(false);
					g20.setSelected(false);
					g30.setSelected(false);
					g50.setSelected(false);
					g100.setSelected(false);

				}

				repaint();

			}
		});

		g10.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				g.grid_rx = 10;
				g.grid_ry = 10;

				if (!g10.isSelected()){g10.setSelected(true);}

				if (g10.isSelected()){

					g5.setSelected(false);
					g10.setSelected(true);
					g15.setSelected(false);
					g20.setSelected(false);
					g30.setSelected(false);
					g50.setSelected(false);
					g100.setSelected(false);

				}

				repaint();

			}
		});

		g15.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				g.grid_rx = 15;
				g.grid_ry = 15;

				if (!g15.isSelected()){g15.setSelected(true);}

				if (g15.isSelected()){

					g5.setSelected(false);
					g10.setSelected(false);
					g15.setSelected(true);
					g20.setSelected(false);
					g30.setSelected(false);
					g50.setSelected(false);
					g100.setSelected(false);

				}

				repaint();

			}
		});

		g20.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				g.grid_rx = 20;
				g.grid_ry = 20;

				if (!g20.isSelected()){g20.setSelected(true);}

				if (g20.isSelected()){

					g5.setSelected(false);
					g10.setSelected(false);
					g15.setSelected(false);
					g20.setSelected(true);
					g30.setSelected(false);
					g50.setSelected(false);
					g100.setSelected(false);

				}

				repaint();

			}
		});

		g30.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				g.grid_rx = 30;
				g.grid_ry = 30;

				if (!g30.isSelected()){g30.setSelected(true);}

				if (g30.isSelected()){

					g5.setSelected(false);
					g10.setSelected(false);
					g15.setSelected(false);
					g20.setSelected(false);
					g30.setSelected(true);
					g50.setSelected(false);
					g100.setSelected(false);

				}

				repaint();

			}
		});

		g50.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				g.grid_rx = 50;
				g.grid_ry = 50;

				if (!g50.isSelected()){g50.setSelected(true);}

				if (g50.isSelected()){

					g5.setSelected(false);
					g10.setSelected(false);
					g15.setSelected(false);
					g20.setSelected(false);
					g30.setSelected(false);
					g50.setSelected(true);
					g100.setSelected(false);

				}

				repaint();

			}
		});

		g100.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				g.grid_rx = 100;
				g.grid_ry = 100;

				if (!g100.isSelected()){g100.setSelected(true);}

				if (g100.isSelected()){

					g5.setSelected(false);
					g10.setSelected(false);
					g15.setSelected(false);
					g20.setSelected(false);
					g30.setSelected(false);
					g50.setSelected(false);
					g100.setSelected(true);

				}

				repaint();

			}
		});

		// Hide/Display network
		item3.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				display_network = item3.isSelected();

				for (int i=0; i<g.getNetwork().getGeometries().size(); i++){

					Color c = g.getPolylineColors().get(i);

					g.getPolylineColors().set(i, new Color((float)(c.getRed())/255.f, (float)(c.getGreen())/255.f, (float)(c.getBlue())/255.f, display_network ? 1.f : 0.f));

				}

				repaint();

			}
		});

		// Hide/Display raw sequence
		item4.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				display_raw_sequence = item4.isSelected();

				for (int i=0; i<2*g.getTrack().get(0).getX().size(); i++){

					Color c = g.getPointColors().get(i);

					g.getPointColors().set(i, new Color((float)(c.getRed())/255.f, (float)(c.getGreen())/255.f, (float)(c.getBlue())/255.f, display_raw_sequence ? 1.f : 0.f));

				}

				repaint();

			}
		});

		// Hide/Display map-matched sequence
		item5.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				display_mm_sequence = item5.isSelected();

				for (int i=2*g.getTrackMm().get(0).getX().size(); i<g.getPointColors().size(); i++){

					Color c = g.getPointColors().get(i);

					g.getPointColors().set(i, new Color((float)(c.getRed())/255.f, (float)(c.getGreen())/255.f, (float)(c.getBlue())/255.f, display_mm_sequence ? 1.f : 0.f));

				}

				repaint();

			}
		});


		// Hide/Display vectors
		item6.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				display_vectors = item6.isSelected();

				for (int i=g.getNetwork().getGeometries().size(); i<g.getPolylineColors().size(); i++){

					Color c = g.getPolylineColors().get(i);

					g.getPolylineColors().set(i, new Color((float)(c.getRed())/255.f, (float)(c.getGreen())/255.f, (float)(c.getBlue())/255.f, display_vectors ? 1.f : 0.f));

				}

				repaint();

			}
		});

		// Grid visible
		item7.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				g.gridVisible = item7.isSelected();
				repaint();

			}
		});

		// Coordinates visible
		item8.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				g.coordinatesVisible = item8.isSelected();
				repaint();

			}
		});

		// Reset
		item0.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				g.plot();

			}
		});


		// Square
		item00.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				square();

			}
		});

		// Hide
		item1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				setVisible(false);

				Main.gui.tabbedPane.addTab("Graphical plot", null, Main.gui.graphics, null);
				Main.gui.graphics.extracted = false;

			}
		});

		// Quit
		item2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				System.exit(0);

			}
		});


		fichier.add(item0);
		fichier.add(item00);
		fichier.add(item1);
		fichier.add(item2);

		couches.add(item3);
		couches.add(item4);
		couches.add(item5);
		couches.add(item6);


		options.add(item7);
		options.add(item8);
		options.add(gres);

		menuBar.add(fichier);
		menuBar.add(couches);
		menuBar.add(options);

		setCursor(new Cursor(Cursor.DEFAULT_CURSOR));


		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e) {

				setVisible(false);
				
				Main.gui.tabbedPane.addTab("Graphical plot", null, Main.gui.graphics, null);
				Main.gui.graphics.extracted = false;

			}

		});
		
		
		addComponentListener(new ComponentListener() {
		 
			@Override
			public void componentResized(ComponentEvent e) {

				square();
				
			}

			@Override
			public void componentMoved(ComponentEvent e) {
				
			}

			@Override
			public void componentShown(ComponentEvent e) {
			
			}

			@Override
			public void componentHidden(ComponentEvent e) {
			
			}
			
		});

		setBounds(100, 100, 600, 600);
		setContentPane(g);
		setLocationRelativeTo(null);
		setTitle("Graphical output");
		setIconImage(Main.img.getImage());
		setJMenuBar(menuBar);


	}


	
	// Function to adapt scales
	private void square(){
		
		double dx = g.xmax-g.xmin;
		double dy = g.ymax-g.ymin;

		int w = getWidth();
		int h = getHeight();

		int compteur = 0;

		while (Math.abs(dx*h - dy*w) > Math.pow(10, 5) && (compteur < 10)){

			compteur ++;

			dx = g.xmax-g.xmin;
			dy = g.ymax-g.ymin;


			if (dx > dy){

				double dy2 = dx*h/w;

				double epsilon = Math.abs(dy-dy2)/2.0;

				g.ymin = g.ymin - epsilon;
				g.ymax = g.ymax + epsilon;

			}
			else{

				double dx2 = dy*w/h;

				double epsilon = Math.abs(dx-dx2)/2.0;

				g.xmin = g.xmin - epsilon;
				g.xmax = g.xmax + epsilon;

			}

		}

		repaint();
		
	}



}
