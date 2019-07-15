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

package fr.ign.cogit.mapmatcher.graphics;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.Label;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.TextArea;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.GeneralPath;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicSliderUI;

import fr.ign.cogit.mapmatcher.core.Main;
import fr.ign.cogit.mapmatcher.core.MapMatching;
import fr.ign.cogit.mapmatcher.core.Track;
import fr.ign.cogit.mapmatcher.util.Loaders;
import fr.ign.cogit.mapmatcher.util.Parameters;
import fr.ign.cogit.mapmatcher.util.Tools;

class CustomSliderUI extends BasicSliderUI {

	private BasicStroke stroke = new BasicStroke(1f, BasicStroke.CAP_ROUND, 
			BasicStroke.JOIN_ROUND, 0f, new float[]{2f, 3f}, 0f);

	public CustomSliderUI(JSlider b) {
		super(b);
	}


	public void paint(Graphics g, JComponent c) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
				RenderingHints.VALUE_ANTIALIAS_ON);
		super.paint(g, c);
	}


	protected Dimension getThumbSize() {
		return new Dimension(12, 16);
	}


	public void paintTrack(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		Stroke old = g2d.getStroke();
		g2d.setStroke(stroke);
		g2d.setPaint(Color.BLACK);
		if (slider.getOrientation() == SwingConstants.HORIZONTAL) {
			g2d.drawLine(trackRect.x, trackRect.y + trackRect.height / 2, 
					trackRect.x + trackRect.width, trackRect.y + trackRect.height / 2);
		} else {
			g2d.drawLine(trackRect.x + trackRect.width / 2, trackRect.y, 
					trackRect.x + trackRect.width / 2, trackRect.y + trackRect.height);
		}
		g2d.setStroke(old);
	}


	public void paintThumb(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		int x1 = thumbRect.x + 2;
		int x2 = thumbRect.x + thumbRect.width - 2;
		int width = thumbRect.width - 4;
		int topY = thumbRect.y + thumbRect.height / 2 - thumbRect.width / 3;
		GeneralPath shape = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
		shape.moveTo(x1, topY);
		shape.lineTo(x2, topY);
		shape.lineTo((x1 + x2) / 2, topY + width);
		shape.closePath();

		//g2d.setPaint(new Color(81, 83, 186));
		g2d.setPaint(Color.GRAY.brighter());

		g2d.fill(shape);
		Stroke old = g2d.getStroke();
		g2d.setStroke(new BasicStroke(2f));

		g2d.setPaint(new Color(131, 127, 211));
		g2d.setPaint(Color.GRAY);

		g2d.draw(shape);
		g2d.setStroke(old);
	}
}


public class Interface {

	public JFrame frmMapMatcher;
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;
	private JTextField textField_3;
	private JTextField textField_4;
	private JTextField textField_6;
	private JTextField textField_7;
	private JTextField textField_8;
	private JTextField textField_10;
	private JTextField textField_11;
	private JTextField textField_12;
	private JTextField txtmmdat;	

	private JComboBox<String> choice;
	private JComboBox<String> choice_1;
	private JComboBox<String> choice_2;
	private JComboBox<String> choice_3;
	private JComboBox<String> choice_4;
	private JComboBox<String> choice_5;
	private JComboBox<String> choice_6; 
	private JComboBox<String> choice_7; 
	private JComboBox<String> choice_8; 
	private JComboBox<String> comboBox_1;
	private JComboBox<String> comboBox; 

	private JCheckBox checkBox; 
	private JCheckBox chckbxNewCheckBox;
	private JCheckBox chckbxPrintReportFile;
	private JCheckBox chckbxPrintDebugFiles; 
	private JCheckBox chckbxSaveParameters; 
	private JCheckBox chckbxBuildTopology;
	private JCheckBox chckbxRemoveDegree;
	private JCheckBox checkBox_2; 
	private JCheckBox chckbxOpenReportFile;
	private JCheckBox chckbxLimitSpeedBetween;
	private JCheckBox chckbxActivateHelp;
	private JCheckBox chckbxSkipUnsolvedPoints;
	private JCheckBox chckbxReorganizeLabelsOn;
	private JCheckBox chckbxLimitNumberOf;
	private JCheckBox chckbxSpatiotemporalAutocorrelationBetween;
	private JCheckBox chckbxKeepSensorError;
	private JCheckBox chckbxCleanDirectory;
	private JCheckBox chckbxInterpolation;


	private JCheckBox chckbxComputeEpochbyepochConfidence;
	private JCheckBox chckbxComputeEpochbyepochRmse;
	private JCheckBox chckbxStoreMapmatchedPoints;
	private JCheckBox chckbxSensorObservationsAre;
	private JCheckBox chckbxRecordMapmatchedPoint;
	private JCheckBox chckbxPrintIndexFor;
	private JCheckBox chckbxPrintPointsCoordinates;
	private JCheckBox chckbxAllowForNetwork;
	private JCheckBox chckbxGraphicalOutput;
	private JCheckBox chckbxPrecomputeDistancesOn;

	private JRadioButton rdbtnCsv;
	private JRadioButton rdbtnXml;
	private JRadioButton rdbtnPredicted;
	private JRadioButton rdbtnNewRadioButton;

	private JLabel label_16;
	public JLabel label_17;
	private JLabel lblOutputDirectory;
	private JLabel lblNumberOfFiles;
	private JLabel lblOutputSuffix;

	private Label label_2; 
	private Label label_18;
	private Label label_23;
	private Label label_24;
	private Label label_25;
	private Label label_26;

	private TextArea textArea_1;

	private JSlider slider;
	private JSpinner spinner;

	public JProgressBar progressBar;
	private JTextField textField_5;
	private JTextField textField_13;

	private JButton btnNewButton;
	private JButton btnS;
	private JButton button;
	private JButton button_1;
	private JButton button_2;
	private JButton button_3;
	private JButton button_4;
	private JButton button_5;
	private JButton button_6;
	private JButton button_7;
	private JButton button_8;
	private JButton button_9;
	private JButton button_10;
	private JButton button_11;
	private JButton btnReset;
	private JButton btnHelp;
	private JButton btnNext;
	private JButton btnQuit;
	private JButton btnPrevious;
	private JButton btnPrevious_1;
	private JButton getFromMap;
	private static JButton btnCompute; 

	private String[] CHOICE_DISTRIBUTIONS = new String[] {"Normal   ", "Uniform", "Exponential", "Rayleigh"};
	private JTextField textField_14;
	private JTextField textField_15;
	private JTextField textField_9;
	private JTextField textField_16;
	private JTextField textField_17;

	public fr.ign.cogit.mapmatcher.graphics.Graphics graphics = new fr.ign.cogit.mapmatcher.graphics.Graphics();

	public final JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
	private JTextField textField_18;

	private boolean helpMultipleTrackPaths = true; 

	private String web_link = "https://github.com/IGNF/mapmatcher";



	// -------------------------------------------------------------------------------
	// Function to test if parameter is valid
	// -------------------------------------------------------------------------------
	private boolean validate(String x, double xmin, double xmax){

		double num = 0;

		try{

			num = Double.parseDouble(x);

		}catch(NumberFormatException e){

			return false;

		}

		return ((num <= xmax)&&((num >= xmin)));

	}

	// -------------------------------------------------------------------------------
	// Function to read header
	// -------------------------------------------------------------------------------
	private ArrayList<String> getHeaderOfFile(String path, String delimiter){

		ArrayList<String> ELEMENTS = new ArrayList<String>();

		// Load file
		try {

			@SuppressWarnings("resource")
			Scanner scan = new Scanner(new File(path));

			String firstLine = scan.nextLine().replaceAll("\"", "");
			StringTokenizer st = new StringTokenizer(firstLine, delimiter);

			while (st.hasMoreTokens()){

				String item = st.nextToken();

				ELEMENTS.add(item);

			}

		} catch (FileNotFoundException e1) {

			System.out.println("Cannot find network input file "+ path);
			System.exit(-1);

		}

		return ELEMENTS;

	}


	// -------------------------------------------------------------------------------
	// Function to fill network input file columns combo boxes
	// -------------------------------------------------------------------------------
	private void fillNetworkComboBoxes(){

		if (!(new File(textField.getText())).exists()){

			return;

		}

		if (!(new File(textField.getText())).isFile()){

			return;

		}

		// Special case OSM file
		StringTokenizer st = new StringTokenizer(textField.getText(), ".");
		st.nextToken(".");

		if (st.nextToken(".").equals("osm")){

			return;

		}


		choice.removeAllItems();
		choice_1.removeAllItems();
		choice_2.removeAllItems();
		choice_3.removeAllItems();
		choice_4.removeAllItems();


		ArrayList<String> CHOICES = getHeaderOfFile(textField.getText(), Parameters.network_delimiter);

		choice.addItem("");
		choice_1.addItem("");
		choice_2.addItem("");
		choice_3.addItem("");
		choice_4.addItem("");

		for (int i=0; i<CHOICES.size(); i++){

			if (chckbxNewCheckBox.isSelected()){

				choice.addItem(CHOICES.get(i));
				choice_1.addItem(CHOICES.get(i));
				choice_2.addItem(CHOICES.get(i));
				choice_3.addItem(CHOICES.get(i));
				choice_4.addItem(CHOICES.get(i));

			}
			else{

				choice.addItem(i+1+"");
				choice_1.addItem(i+1+"");
				choice_2.addItem(i+1+"");
				choice_3.addItem(i+1+"");
				choice_4.addItem(i+1+"");

			}

		}


		if (CHOICES.contains("id")){choice.setSelectedItem("id");}
		if (CHOICES.contains("ID")){choice.setSelectedItem("ID");}
		if (CHOICES.contains("link")){choice.setSelectedItem("link");}
		if (CHOICES.contains("link_id")){choice.setSelectedItem("link_id");}

		if (CHOICES.contains("wkt")){choice_1.setSelectedItem("wkt");}
		if (CHOICES.contains("WKT")){choice_1.setSelectedItem("WKT");}

		if (CHOICES.contains("source")){choice_2.setSelectedItem("source");}
		if (CHOICES.contains("SOURCE")){choice_2.setSelectedItem("SOURCE");}

		if (CHOICES.contains("target")){choice_3.setSelectedItem("target");}
		if (CHOICES.contains("TARGET")){choice_3.setSelectedItem("TARGET");}

	}

	// -------------------------------------------------------------------------------
	// Function to fill track input file columns combo boxes
	// -------------------------------------------------------------------------------
	private void fillTrackComboBoxes(){

		if (textField_1.getText().endsWith(".gpx")){

			choice_7.setEnabled(false);

			checkBox.setEnabled(false);

			label_2.setEnabled(false);
			textField_3.setEnabled(false);


		}
		else{

			choice_5.setEnabled(true);
			choice_6.setEnabled(true);
			choice_7.setEnabled(true);

			checkBox.setEnabled(true);

			label_2.setEnabled(true);
			textField_3.setEnabled(true);

		}

		choice_5.removeAllItems();
		choice_6.removeAllItems();
		choice_7.removeAllItems();


		Parameters.input_track_path = textField_1.getText();

		if (Parameters.input_track_path.trim().equals("")){
			return;
		}

		Parameters.readMultipleFiles();


		if (Parameters.input_track_path_list.size() == 0){

			lblNumberOfFiles.setText("Number of files to print :  "+computeNumberOfFilesToPrint());
			return;

		}

		if (!(new File(Parameters.input_track_path_list.get(0))).exists()){

			lblNumberOfFiles.setText("Number of files to print :  "+computeNumberOfFilesToPrint());
			return;

		}


		ArrayList<String> CHOICES = getHeaderOfFile(Parameters.input_track_path_list.get(0), Parameters.track_delimiter);

		choice_5.addItem("");
		choice_6.addItem("");
		choice_7.addItem("");

		for (int i=0; i<CHOICES.size(); i++){

			if (checkBox.isSelected()){

				choice_5.addItem(CHOICES.get(i));
				choice_6.addItem(CHOICES.get(i));
				choice_7.addItem(CHOICES.get(i));

			}
			else{

				choice_5.addItem(i+1+"");
				choice_6.addItem(i+1+"");
				choice_7.addItem(i+1+"");

			}

		}

		if (CHOICES.contains("x")){choice_5.setSelectedItem("x");}
		if (CHOICES.contains("X")){choice_5.setSelectedItem("X");}
		if (CHOICES.contains("E")){choice_5.setSelectedItem("E");}
		if (CHOICES.contains("lon")){choice_5.setSelectedItem("lon");}
		if (CHOICES.contains("lambda")){choice_5.setSelectedItem("lambda");}
		if (CHOICES.contains("longitude")){choice_5.setSelectedItem("longitude");}

		if (CHOICES.contains("y")){choice_6.setSelectedItem("y");}
		if (CHOICES.contains("Y")){choice_6.setSelectedItem("Y");}
		if (CHOICES.contains("N")){choice_6.setSelectedItem("N");}
		if (CHOICES.contains("lat")){choice_6.setSelectedItem("lat");}
		if (CHOICES.contains("phi")){choice_6.setSelectedItem("phi");}
		if (CHOICES.contains("latitude")){choice_6.setSelectedItem("latitude");}

		if (CHOICES.contains("t")){choice_7.setSelectedItem("t");}
		if (CHOICES.contains("T")){choice_7.setSelectedItem("T");}
		if (CHOICES.contains("time")){choice_7.setSelectedItem("time");}
		if (CHOICES.contains("time_stamp")){choice_7.setSelectedItem("time_stamp");}
		if (CHOICES.contains("time_stamps")){choice_7.setSelectedItem("time_stamps");}
		if (CHOICES.contains("date")){choice_7.setSelectedItem("date");}


		// Change display
		if (Parameters.input_track_path_list.size() > 1){

			label_16.setText("("+Parameters.input_track_path_list.size()+" files to process)");
			lblOutputDirectory.setText("Output directory for map-matched tracks ("+Parameters.input_track_path_list.size()+")");

		}
		else{

			label_16.setText("");
			lblOutputDirectory.setText("Output directory for map-matched tracks");

		}

		lblNumberOfFiles.setText("Number of files to print :  "+computeNumberOfFilesToPrint());


		if (textField_1.getText().endsWith(".gpx")){

			choice_5.addItem(" ");
			choice_6.addItem(" ");

			choice_5.setSelectedItem(" ");
			choice_6.setSelectedItem(" ");

			choice_5.setEnabled(false);
			choice_6.setEnabled(false);

		}


	}


	// -------------------------------------------------------------------------------
	// Function to compute number of files to print
	// -------------------------------------------------------------------------------
	private int computeNumberOfFilesToPrint(){

		// Update text area
		textArea_1.setText("");

		int output = 0;

		boolean bnet = (chckbxBuildTopology.isSelected() || chckbxRemoveDegree.isSelected());


		if (bnet){

			if (textArea_1.isEnabled()){

				textArea_1.append(textField_4.getText()+"\\"+"network_topo.wkt".replace("/", "\\")+"\r\n");

			}

		}

		if (chckbxStoreMapmatchedPoints.isSelected()){

			if (textArea_1.isEnabled()){

				textArea_1.append(textField_4.getText()+"\\"+"index.dat".replace("/", "\\")+"\r\n");

			}

		}

		if (Parameters.input_track_path_list != null){

			output +=  Parameters.input_track_path_list.size();

			Parameters.output_suffix = txtmmdat.getText();

			for (int i=0; i<Parameters.input_track_path_list.size(); i++){

				String name = textField_4.getText()+MapMatching.makeOutputName(Parameters.input_track_path_list.get(i));

				if (textArea_1.isEnabled()){
					textArea_1.append(name.replace("/", "\\")+"\r\n");
				}

				if (i > 100){
					textArea_1.append((Parameters.input_track_path_list.size()-i+1)+" other output track file(s)...\r\n");
					break;
				}

			}
			
			if (chckbxInterpolation.isSelected()){
				
				output +=  Parameters.input_track_path_list.size();

				for (int i=0; i<Parameters.input_track_path_list.size(); i++){

					String name = textField_4.getText()+MapMatching.makeOutputName(Parameters.input_track_path_list.get(i));
					name = name.substring(0,name.length()-4) + "_interp.dat";

					if (textArea_1.isEnabled()){
						textArea_1.append(name.replace("/", "\\")+"\r\n");
					}

					if (i > 100){
						textArea_1.append((Parameters.input_track_path_list.size()-i+1)+" other interpolation file(s)...\r\n");
						break;
					}

				}
				
			}

		} 


		output += Boolean.compare(chckbxPrintReportFile.isSelected(), false);
		output += Boolean.compare(chckbxSaveParameters.isSelected(), false);
		output += Boolean.compare(chckbxPrintDebugFiles.isSelected(), false)*6;
		output += Boolean.compare(chckbxStoreMapmatchedPoints.isSelected(), false);

		output += Boolean.compare(bnet, false);

		if (chckbxPrintReportFile.isSelected()){


			if (textArea_1.isEnabled()){
				textArea_1.append(textField_12.getText().replace("/", "\\")+"\r\n");
			}



		}

		if (chckbxSaveParameters.isSelected()){

			if (textArea_1.isEnabled()){
				textArea_1.append(textField_6.getText().replace("/", "\\")+"\r\n");
			}

		}

		if (chckbxPrintDebugFiles.isSelected()){

			if (textArea_1.isEnabled()){
				textArea_1.append(textField_6.getText().replace("/", "\\")+"\\chain.wkt\r\n");
				textArea_1.append(textField_6.getText().replace("/", "\\")+"\\path.wkt\r\n");
				textArea_1.append(textField_6.getText().replace("/", "\\")+"\\proj.txt\r\n");
				textArea_1.append(textField_6.getText().replace("/", "\\")+"\\mapmatching.wkt\r\n");
				textArea_1.append(textField_6.getText().replace("/", "\\")+"\\project.qgs\r\n");
				textArea_1.append(textField_6.getText().replace("/", "\\")+"\\all_track_mm.wkt\r\n");

			}

		}

		return output;

	}

	// -------------------------------------------------------------------------------
	// Function to define editable property of output list of files
	// -------------------------------------------------------------------------------
	private void setListOfOutputs(){

		boolean b2 = !textField_4.getText().trim().equals("");

		textArea_1.setEnabled(b2);

		if (textArea_1.isEnabled()){

			lblNumberOfFiles.setText("Number of files to print :  "+computeNumberOfFilesToPrint());

		}

	}

	// -------------------------------------------------------------------------------
	// Function to open a specific file in a scroll pane
	// -------------------------------------------------------------------------------
	private static void visualize(String path, String title) {

		JPanel middlePanel = new JPanel();

		// create the middle panel components
		JTextArea display = new JTextArea (20, 60);
		display.setEditable (false); // set textArea non-editable
		final JScrollPane scroll = new JScrollPane(display);
		scroll.setVerticalScrollBarPolicy (ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		//Add Textarea in to middle panel
		middlePanel.add(scroll);

		// My code
		JFrame frame = new JFrame ();
		frame.getContentPane().add(middlePanel);
		frame.pack();
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setIconImage(Main.img.getImage());
		frame.setTitle(title);


		try {



			@SuppressWarnings("resource")
			Scanner scan = new Scanner(new File(path));

			while(scan.hasNextLine()) {

				display.append(scan.nextLine()+"\r\n");

			}

			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					scroll.getVerticalScrollBar().setValue(0);
				}
			});


		} catch (FileNotFoundException e) {

			if (path.trim().equals("")){

				JOptionPane.showMessageDialog(null, "File path must be specified before visualization", "Input warning", JOptionPane.WARNING_MESSAGE);
				return;

			}

			JOptionPane.showMessageDialog(null, "The file "+path+" cannot be found", "Input error", JOptionPane.ERROR_MESSAGE);
			return;

		}

		frame.setVisible (true);

	}

	// Method to call after procedure termination
	public static void reactivateComputeButton(){

		btnCompute.setEnabled(true);

	}

	/**
	 * Create the application.
	 */
	public Interface() {
		initialize();
	}

	/**
	 * Method to handle drag and drop
	 */
	public synchronized void dragAndDrop(DropTargetDropEvent evt, JTextField tf) {

		if (tf.getText().startsWith("Use *")){
			removeHelpText();
		}

		try {
			evt.acceptDrop(DnDConstants.ACTION_COPY);
			@SuppressWarnings("unchecked")
			List<File> droppedFiles = (List<File>) evt
			.getTransferable().getTransferData(
					DataFlavor.javaFileListFlavor);

			String paths = "";

			if (tf == textField_1) {

				for (int i=0; i<droppedFiles.size(); i++) {

					File file = droppedFiles.get(i);
					paths = paths+file.getAbsolutePath();

					if (i != droppedFiles.size()-1){

						paths = paths + ";";

					}

				}

			}else {

				paths = droppedFiles.get(0).getAbsolutePath();

			}

			tf.setText(paths);



		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}





	/**
	 * Initialize the contents of the frame.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes", "serial" })
	private void initialize() {

		// ---------------------------------------------------------------------------------------
		// Interface
		// ---------------------------------------------------------------------------------------

		frmMapMatcher = new JFrame();
		frmMapMatcher.setBounds(100, 100, 417, 487);
		frmMapMatcher.setResizable(false);
		frmMapMatcher.setLocationRelativeTo(null);
		frmMapMatcher.setTitle("Map Matcher");
		frmMapMatcher.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frmMapMatcher.getContentPane().add(tabbedPane, BorderLayout.CENTER);

		JPanel panel = new JPanel();
		panel.setToolTipText("");
		tabbedPane.addTab("Input", null, panel, null);
		panel.setLayout(null);

		JLabel lblNewLabel = new JLabel("Network");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblNewLabel.setBounds(22, 11, 83, 14);
		panel.add(lblNewLabel);

		JLabel lblTracks = new JLabel("Tracks");
		lblTracks.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblTracks.setBounds(22, 195, 83, 14);
		panel.add(lblTracks);

		textField = new JTextField();
		textField.setBounds(27, 38, 240, 25);
		panel.add(textField);
		textField.setColumns(10);

		textField.setDropTarget(new DropTarget() {
			public synchronized void drop(DropTargetDropEvent evt) {

				dragAndDrop(evt, textField);

			}

		});

		textField_1 = new JTextField();
		textField_1.setColumns(10);
		textField_1.setBounds(27, 220, 240, 25);
		panel.add(textField_1);

		textField_1.setDropTarget(new DropTarget() {
			public synchronized void drop(DropTargetDropEvent evt) {

				dragAndDrop(evt, textField_1);
				fillTrackComboBoxes();

			}

		});

		btnNewButton = new JButton("...");
		btnNewButton.setBounds(280, 38, 30, 25);
		panel.add(btnNewButton);

		button = new JButton("...");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		button.setBounds(280, 220, 30, 25);
		panel.add(button);

		btnS = new JButton("\",\"");
		btnS.setBounds(315, 38, 30, 25);
		btnS.setMargin(new Insets(0,0,0,0));
		panel.add(btnS);

		button_2 = new JButton("\",\"");
		button_2.setBounds(315, 220, 30, 25);
		button_2.setMargin(new Insets(0,0,0,0));
		panel.add(button_2);

		chckbxNewCheckBox = new JCheckBox(" First line contains header");
		chckbxNewCheckBox.setSelected(true);
		chckbxNewCheckBox.setBounds(22, 70, 162, 23);
		panel.add(chckbxNewCheckBox);

		choice = new JComboBox<String>();
		choice.setBounds(70, 131, 62, 20);
		panel.add(choice);

		choice_1 = new JComboBox<String>();
		choice_1.setBounds(70, 157, 62, 20);
		panel.add(choice_1);


		chckbxBuildTopology = new JCheckBox(" Build topology");
		chckbxBuildTopology.setBounds(22, 96, 117, 23);
		panel.add(chckbxBuildTopology);

		textField_2 = new JTextField();
		textField_2.setHorizontalAlignment(SwingConstants.CENTER);
		textField_2.setText("0.01");
		textField_2.setEnabled(false);
		textField_2.setColumns(10);
		textField_2.setBounds(305, 97, 45, 20);
		panel.add(textField_2);

		final Label label = new Label("Tolerance between nodes :");
		label.setEnabled(false);
		label.setBounds(145, 97, 154, 22);
		panel.add(label);

		final Label label_1 = new Label(" m");
		label_1.setEnabled(false);
		label_1.setBounds(355, 96, 20, 22);
		panel.add(label_1);

		label_2 = new Label("Sensor error code :");
		label_2.setBounds(205, 252, 105, 22);
		panel.add(label_2);

		textField_3 = new JTextField();
		textField_3.setText("-1");
		textField_3.setHorizontalAlignment(SwingConstants.CENTER);
		textField_3.setColumns(10);
		textField_3.setBounds(316, 252, 45, 20);
		panel.add(textField_3);

		checkBox = new JCheckBox(" First line contains header");
		checkBox.setSelected(true);
		checkBox.setBounds(22, 252, 162, 23);
		panel.add(checkBox);

		btnNext = new JButton("Next");
		btnNext.setBounds(290, 384, 89, 25);
		panel.add(btnNext);

		Label label_3 = new Label("id :");
		label_3.setBounds(22, 127, 38, 22);
		panel.add(label_3);

		Label label_4 = new Label("geom :");
		label_4.setBounds(22, 155, 38, 22);
		panel.add(label_4);

		final Label label_5 = new Label("source :");
		label_5.setBounds(150, 129, 49, 22);
		panel.add(label_5);

		choice_2 = new JComboBox<String>();
		choice_2.setBounds(205, 131, 62, 20);
		panel.add(choice_2);

		final Label label_6 = new Label("target :");
		label_6.setBounds(150, 155, 49, 22);
		panel.add(label_6);

		choice_3 = new JComboBox<String>();
		choice_3.setBounds(205, 157, 62, 20);
		panel.add(choice_3);

		final Label label_7 = new Label("one way :");
		label_7.setBounds(300, 131, 62, 22);
		panel.add(label_7);

		choice_4 = new JComboBox<String>();
		choice_4.setBounds(298, 157, 62, 20);
		panel.add(choice_4);

		Label label_8 = new Label("X");
		label_8.setBounds(25, 294, 14, 22);
		panel.add(label_8);

		choice_5 = new JComboBox<String>();
		choice_5.setBounds(45, 296, 75, 22);
		panel.add(choice_5);

		Label label_9 = new Label("Y");
		label_9.setBounds(148, 294, 14, 22);
		panel.add(label_9);

		choice_6 = new JComboBox<String>();
		choice_6.setBounds(170, 296, 75, 22);
		panel.add(choice_6);

		Label label_10 = new Label("T");
		label_10.setBounds(280, 294, 20, 22);
		panel.add(label_10);

		choice_7 = new JComboBox<String>();
		choice_7.setBounds(300, 294, 75, 22);
		panel.add(choice_7);

		Label label_11 = new Label("Time stamp format :");
		label_11.setBounds(25, 335, 124, 22);
		panel.add(label_11);


		choice_8 = new JComboBox<String>();
		choice_8.setModel(new DefaultComboBoxModel(new String[] {

				"yyyy-mm-dd hh:mm:ss", 
				"yyyy-mm-dd hh:mm:ss.sss", 
				"hh:mm:ss yyyy-mm-dd", 
				"dd-mm-yyyy hh:mm:ss.s", 
				"dd-mm-yyyy hh:mm:ss.sss", 
				"hh:mm:ss yyyy-mm-dd", 
				"dd/mm/yyyy hh:mm:ss.s", 
				"dd/mm/yyyy hh:mm:ss.sss", 
				"yyyy/mm/dd hh:mm:ss.s", 
				"yyyy/mm/dd hh:mm:ss.sss", 
				"hh:mm:ss yyyy/mm/dd", 
				"hh:mm:ss dd/mm/yyyy", 
				"hh:mm:ss", ""}));

		choice_8.setToolTipText("");
		choice_8.setEditable(true);
		choice_8.setBounds(155, 335, 220, 22);

		panel.add(choice_8);

		label_16 = new JLabel("");
		label_16.setHorizontalAlignment(SwingConstants.LEFT);
		label_16.setFont(new Font("Tahoma", Font.PLAIN, 11));
		label_16.setBounds(78, 195, 240, 14);
		panel.add(label_16);

		chckbxRemoveDegree = new JCheckBox(" Remove non-intersection nodes");
		chckbxRemoveDegree.setBounds(194, 70, 181, 23);
		panel.add(chckbxRemoveDegree);

		Font font = new Font("Arial Unicode MS", Font.PLAIN, 23);

		button_5 = new JButton("\u25CB");
		button_5.setFont(font);
		button_5.setMargin(new Insets(0,0,0,0));
		button_5.setBounds(350, 38, 30, 25);
		panel.add(button_5);

		button_1 = new JButton("\u25CB");
		button_1.setFont(font);

		button_1.setMargin(new Insets(0, 0, 0, 0));
		button_1.setBounds(350, 220, 30, 25);
		panel.add(button_1);

		button_11 = new JButton("Load parameters");
		button_11.setBounds(25, 384, 115, 25);
		panel.add(button_11);

		btnReset = new JButton("Reset");
		btnReset.setBounds(170, 384, 89, 25);
		panel.add(btnReset);


		JPanel panel_1 = new JPanel();
		tabbedPane.addTab("Output", null, panel_1, null);
		panel_1.setLayout(null);

		lblOutputDirectory = new JLabel("Output directory for map-matched tracks");
		lblOutputDirectory.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblOutputDirectory.setBounds(22, 11, 240, 14);
		panel_1.add(lblOutputDirectory);

		textField_4 = new JTextField();
		textField_4.setColumns(10);
		textField_4.setBounds(27, 38, 240, 25);
		panel_1.add(textField_4);

		textField_4.setDropTarget(new DropTarget() {
			public synchronized void drop(DropTargetDropEvent evt) {

				dragAndDrop(evt, textField_4);

				setListOfOutputs();

			}

		});

		button_3 = new JButton("...");
		button_3.setBounds(280, 38, 38, 25);
		panel_1.add(button_3);

		button_4 = new JButton("\",\"");
		button_4.setBounds(330, 38, 45, 25);
		panel_1.add(button_4);

		chckbxPrintReportFile = new JCheckBox(" Print report file");
		chckbxPrintReportFile.setSelected(true);
		chckbxPrintReportFile.setBounds(22, 125, 123, 23);
		panel_1.add(chckbxPrintReportFile);

		chckbxOpenReportFile = new JCheckBox("Open report after termination");
		chckbxOpenReportFile.setSelected(true);
		chckbxOpenReportFile.setBounds(149, 125, 190, 23);
		panel_1.add(chckbxOpenReportFile);

		chckbxPrintDebugFiles = new JCheckBox(" Print debug files (6)");
		chckbxPrintDebugFiles.setBounds(22, 250, 160, 23);
		panel_1.add(chckbxPrintDebugFiles);

		chckbxSaveParameters = new JCheckBox("Save parameters");
		chckbxSaveParameters.setBounds(22, 190, 160, 23);
		panel_1.add(chckbxSaveParameters);

		textField_6 = new JTextField();
		textField_6.setEnabled(false);
		textField_6.setColumns(10);
		textField_6.setBounds(27, 215, 240, 25);
		panel_1.add(textField_6);

		button_6 = new JButton("...");
		button_6.setEnabled(false);
		button_6.setBounds(280, 215, 38, 25);
		panel_1.add(button_6);

		chckbxCleanDirectory = new JCheckBox("Clean directory");
		chckbxCleanDirectory.setBounds(22, 70, 123, 23);
		panel_1.add(chckbxCleanDirectory);

		chckbxGraphicalOutput = new JCheckBox("Plot full network");
		chckbxGraphicalOutput.setSelected(false);
		chckbxGraphicalOutput.setBounds(149, 70, 123, 23);
		panel_1.add(chckbxGraphicalOutput);


		button_7 = new JButton("Next");
		button_7.setBounds(290, 384, 89, 25);
		panel_1.add(button_7);

		btnPrevious = new JButton("Previous");
		btnPrevious.setBounds(25, 384, 89, 25);
		panel_1.add(btnPrevious);

		chckbxKeepSensorError = new JCheckBox("Keep sensor errors");
		chckbxKeepSensorError.setSelected(true);
		chckbxKeepSensorError.setBounds(22, 95, 120, 23);
		panel_1.add(chckbxKeepSensorError);

		chckbxInterpolation = new JCheckBox("Print interpolation");
		chckbxInterpolation.setSelected(false);
		chckbxInterpolation.setBounds(149, 95, 130, 23);
		panel_1.add(chckbxInterpolation);

		textField_12 = new JTextField();
		textField_12.setColumns(10);
		textField_12.setBounds(27, 155, 240, 25);
		panel_1.add(textField_12);

		button_8 = new JButton("...");
		button_8.setBounds(280, 155, 38, 25);
		panel_1.add(button_8);

		txtmmdat = new JTextField();
		txtmmdat.setHorizontalAlignment(SwingConstants.CENTER);
		txtmmdat.setText("_mm.dat");
		txtmmdat.setColumns(10);
		txtmmdat.setBounds(287, 95, 78, 20);
		panel_1.add(txtmmdat);

		lblOutputSuffix = new JLabel("Output suffix");
		lblOutputSuffix.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblOutputSuffix.setBounds(292, 70, 74, 20);
		panel_1.add(lblOutputSuffix);

		lblNumberOfFiles = new JLabel("Number of files to print :  1");
		lblNumberOfFiles.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblNumberOfFiles.setBounds(206, 265, 190, 14);
		panel_1.add(lblNumberOfFiles);

		textArea_1 = new TextArea();
		textArea_1.setEnabled(false);
		textArea_1.setBounds(25, 287, 353, 81);
		panel_1.add(textArea_1);

		JPanel panel_2 = new JPanel();
		tabbedPane.addTab("Computation", null, panel_2, null);
		tabbedPane.setEnabledAt(2, true);
		panel_2.setLayout(null);

		JLabel lblAlgorithmParameters = new JLabel("Parameters");
		lblAlgorithmParameters.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblAlgorithmParameters.setBounds(22, 11, 148, 14);
		panel_2.add(lblAlgorithmParameters);

		textField_7 = new JTextField();
		textField_7.setHorizontalAlignment(SwingConstants.CENTER);
		textField_7.setText("8.0");
		textField_7.setColumns(10);
		textField_7.setBounds(22, 40, 45, 20);
		panel_2.add(textField_7);

		Label label_12 = new Label("Positional accuracy");
		label_12.setBounds(75, 40, 128, 22);
		panel_2.add(label_12);

		textField_8 = new JTextField();
		textField_8.setText("20.0");
		textField_8.setHorizontalAlignment(SwingConstants.CENTER);
		textField_8.setColumns(10);
		textField_8.setBounds(218, 40, 45, 20);
		panel_2.add(textField_8);

		Label label_13 = new Label("Search radius (m)");
		label_13.setBounds(269, 40, 117, 22);
		panel_2.add(label_13);

		Label label_14 = new Label("Transition cost");
		label_14.setBounds(75, 66, 80, 22);
		panel_2.add(label_14);

		chckbxLimitNumberOf = new JCheckBox("Limit number of candidates for each point");
		chckbxLimitNumberOf.setBounds(22, 222, 261, 23);
		panel_2.add(chckbxLimitNumberOf);

		spinner = new JSpinner();
		spinner.setEnabled(false);
		spinner.setModel(new SpinnerNumberModel(new Integer(1), new Integer(1), null, new Integer(1)));
		spinner.setBounds(291, 222, 45, 20);
		panel_2.add(spinner);

		chckbxLimitSpeedBetween = new JCheckBox("Limit speed between successive points");
		chckbxLimitSpeedBetween.setBounds(22, 253, 229, 23);
		panel_2.add(chckbxLimitSpeedBetween);

		slider = new JSlider();
		slider.setEnabled(false);
		slider.setForeground(Color.BLACK);
		slider.setMinorTickSpacing(10);
		slider.setMajorTickSpacing(20);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);


		slider.setBounds(22, 136, 301, 38);

		slider.setUI(new CustomSliderUI(slider));

		panel_2.add(slider);

		textField_10 = new JTextField();
		textField_10.setHorizontalAlignment(SwingConstants.CENTER);
		textField_10.setText("50.0");
		textField_10.setEnabled(false);
		textField_10.setColumns(10);
		textField_10.setBounds(291, 253, 45, 20);
		panel_2.add(textField_10);

		JLabel lblMs = new JLabel("m / s");
		lblMs.setEnabled(false);
		lblMs.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblMs.setBounds(350, 252, 29, 23);
		panel_2.add(lblMs);

		chckbxPrecomputeDistancesOn = new JCheckBox("Precompute distances on");
		chckbxPrecomputeDistancesOn.setSelected(true);
		chckbxPrecomputeDistancesOn.setBounds(22, 190, 145, 23);
		panel_2.add(chckbxPrecomputeDistancesOn);

		textField_11 = new JTextField();
		textField_11.setText("1.0");
		textField_11.setHorizontalAlignment(SwingConstants.CENTER);
		textField_11.setColumns(10);
		textField_11.setBounds(22, 68, 45, 20);
		panel_2.add(textField_11);

		chckbxSkipUnsolvedPoints = new JCheckBox("Skip unsolved points");
		chckbxSkipUnsolvedPoints.setSelected(true);
		chckbxSkipUnsolvedPoints.setBounds(246, 285, 128, 23);
		panel_2.add(chckbxSkipUnsolvedPoints);

		btnPrevious_1 = new JButton("Previous");
		btnPrevious_1.setBounds(25, 384, 89, 25);
		panel_2.add(btnPrevious_1);

		btnQuit = new JButton("Quit");
		btnQuit.setBounds(290, 384, 89, 25);
		panel_2.add(btnQuit);

		btnCompute = new JButton("Compute");
		btnCompute.setBounds(152, 384, 100, 25);
		panel_2.add(btnCompute);

		progressBar = new JProgressBar();
		progressBar.setBounds(31, 340, 338, 20);
		progressBar.setStringPainted(true);
		progressBar.setForeground(Color.GREEN.darker());
		panel_2.add(progressBar);

		label_17 = new JLabel("");
		label_17.setBounds(31, 314, 338, 22);
		panel_2.add(label_17);

		chckbxSpatiotemporalAutocorrelationBetween = new JCheckBox("Spatio-temporal autocorrelation between measurements");
		chckbxSpatiotemporalAutocorrelationBetween.setBounds(22, 110, 342, 23);
		panel_2.add(chckbxSpatiotemporalAutocorrelationBetween);

		final Label label_19 = new Label("scope");
		label_19.setEnabled(false);
		label_19.setBounds(340, 135, 36, 14);
		panel_2.add(label_19);

		textField_5 = new JTextField();
		textField_5.setText("100.0");
		textField_5.setHorizontalAlignment(SwingConstants.CENTER);
		textField_5.setEnabled(false);
		textField_5.setColumns(10);
		textField_5.setBounds(336, 150, 45, 20);
		panel_2.add(textField_5);

		textField_13 = new JTextField();
		textField_13.setText("0.0");
		textField_13.setHorizontalAlignment(SwingConstants.CENTER);
		textField_13.setColumns(10);
		textField_13.setBounds(218, 68, 45, 20);
		panel_2.add(textField_13);

		Label label_20 = new Label("Angle factor");
		label_20.setBounds(269, 66, 117, 22);
		panel_2.add(label_20);

		chckbxReorganizeLabelsOn = new JCheckBox("Reorganize labels on post-processing");
		chckbxReorganizeLabelsOn.setSelected(true);
		chckbxReorganizeLabelsOn.setBounds(22, 285, 222, 23);
		panel_2.add(chckbxReorganizeLabelsOn);

		final JComboBox comboBox_2 = new JComboBox();
		comboBox_2.setModel(new DefaultComboBoxModel(new String[] {"full network", "buffered tracks", "1st buffered track"}));
		comboBox_2.setBounds(175, 191, 105, 20);
		panel_2.add(comboBox_2);

		final Label label_30 = new Label("Radius:");
		label_30.setBounds(290, 190, 45, 22);
		panel_2.add(label_30);

		textField_18 = new JTextField();
		textField_18.setText("300.0");
		textField_18.setHorizontalAlignment(SwingConstants.CENTER);
		textField_18.setColumns(10);
		textField_18.setBounds(336, 191, 45, 20);
		panel_2.add(textField_18);



		// Activate precompute distances option
		chckbxPrecomputeDistancesOn.addActionListener(new ActionListener() {


			public void actionPerformed(ActionEvent e) {

				comboBox_2.setEnabled(chckbxPrecomputeDistancesOn.isSelected());
				label_30.setEnabled(chckbxPrecomputeDistancesOn.isSelected());
				textField_18.setEnabled(chckbxPrecomputeDistancesOn.isSelected());

			}
		});

		// --------------------------------------------------------------------------------------
		// Test if getnet.jar is available
		// --------------------------------------------------------------------------------------
		File file_getnet_jar = new File("getnet/getnet.jar");

		if (file_getnet_jar.isFile()){

			getFromMap = new JButton("BD TOPO");
			getFromMap.setBounds(280, 10, 100, 20);
			panel.add(getFromMap);

			getFromMap.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {

					String proxy = "";
					String key = "";

					File file_key = new File("getnet/key.dat");

					if (file_key.isFile()){

						try {

							@SuppressWarnings("resource")
							Scanner scan = new Scanner(file_key);
							key = " "+scan.nextLine();

							if (scan.hasNextLine()){
								StringTokenizer st = new StringTokenizer(scan.nextLine(),":");
								proxy = " -Dhttp.proxyHost="+st.nextToken(":")+" -Dhttp.proxyPort="+st.nextToken(":");
							}

						} catch (FileNotFoundException e1) {

							e1.printStackTrace();
						}

					}

					try {

						String cmd = "java"+proxy+" -jar getnet/getnet.jar"+key;
						Runtime.getRuntime().exec(cmd);

					} catch (IOException e1) {
						e1.printStackTrace();
					}


				}


			});

		}
		// --------------------------------------------------------------------------------------


		// Confirm clean output directory
		chckbxCleanDirectory.addActionListener(new ActionListener() {


			public void actionPerformed(ActionEvent e) {

				if (!chckbxCleanDirectory.isSelected()){

					return;

				}

				int dialogResult = JOptionPane.showConfirmDialog(null, "All files in output directory will be deleted. Do you confirm ?", "Warning", JOptionPane.YES_NO_OPTION);

				if (dialogResult == 1){

					chckbxCleanDirectory.setSelected(false);

				}

			}

		});

		// Button to change page section
		btnPrevious_1.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				tabbedPane.setSelectedIndex(1);

			}
		});

		// Button to change page section
		btnQuit.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				int dialogResult = JOptionPane.showConfirmDialog(null, "Are you sure you want to quit Map Matcher ?", "Quit", JOptionPane.YES_NO_OPTION);

				if (dialogResult == 0){

					System.exit(0);

				}

			}
		});

		// Maximum number of candidates check box
		chckbxLimitNumberOf.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				spinner.setEnabled(chckbxLimitNumberOf.isSelected());

			}
		});

		// Autocorrelation check box
		chckbxSpatiotemporalAutocorrelationBetween.addActionListener(new ActionListener() {


			public void actionPerformed(ActionEvent e) {

				textField_5.setEnabled(chckbxSpatiotemporalAutocorrelationBetween.isSelected());
				label_19.setEnabled(chckbxSpatiotemporalAutocorrelationBetween.isSelected());

				slider.setEnabled(chckbxSpatiotemporalAutocorrelationBetween.isSelected());


				String text = "Spatio-temporal autocorrelation between measurements ";

				if (chckbxSpatiotemporalAutocorrelationBetween.isSelected()){
					text += "("+slider.getValue()+" %)";
				}

				chckbxSpatiotemporalAutocorrelationBetween.setText(text);

			}
		});



		// Maximum speed check box
		chckbxLimitSpeedBetween.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				textField_10.setEnabled(chckbxLimitSpeedBetween.isSelected());

			}
		});

		// Maximum speed slider
		slider.addChangeListener(new ChangeListener() {


			public void stateChanged(ChangeEvent e) {

				String text = "Spatio-temporal autocorrelation between measurements ("+slider.getValue()+" %)";

				chckbxSpatiotemporalAutocorrelationBetween.setText(text);

			}
		});


		textField_10.addFocusListener(new FocusListener() {


			public void focusLost(FocusEvent e) {

				String text = textField_10.getText();

				if (!validate(text, Double.MIN_VALUE, Double.MAX_VALUE)){

					JOptionPane.showMessageDialog(null, text+" is not a valid entry for speed limit", "Input error",
							JOptionPane.ERROR_MESSAGE);

					textField_10.setText("");

					return;

				}

				slider.setValue((int)Double.parseDouble(text));

			}


			public void focusGained(FocusEvent e) {

			}
		});


		// Securities on numerical values
		textField_7.addFocusListener(new FocusListener() {


			public void focusLost(FocusEvent e) {

				String text = textField_7.getText();

				if (!validate(text, 0, Double.MAX_VALUE)){

					JOptionPane.showMessageDialog(null, text+" is not a valid entry for track positional accuracy", "Input error",
							JOptionPane.ERROR_MESSAGE);

					textField_7.setText("8.0");

					return;

				}

			}


			public void focusGained(FocusEvent e) {

			}
		});

		textField_8.addFocusListener(new FocusListener() {


			public void focusLost(FocusEvent e) {

				String text = textField_8.getText();

				if (!validate(text, 0, Double.MAX_VALUE)){

					JOptionPane.showMessageDialog(null, text+" is not a valid entry for search radius", "Input error",
							JOptionPane.ERROR_MESSAGE);

					textField_8.setText("20.0");

					return;

				}


			}


			public void focusGained(FocusEvent e) {

			}
		});

		textField_11.addFocusListener(new FocusListener() {


			public void focusLost(FocusEvent e) {

				String text = textField_11.getText();

				if (!validate(text, 0, Double.MAX_VALUE)){

					JOptionPane.showMessageDialog(null, text+" is not a valid entry for beta transition factor", "Input error",
							JOptionPane.ERROR_MESSAGE);

					textField_11.setText("1.0");

					return;

				}

			}


			public void focusGained(FocusEvent e) {

			}
		});

		FocusListener radius_comparison_listener = new FocusListener() {


			public void focusLost(FocusEvent e) {

				double search = Double.parseDouble(textField_8.getText());
				double radius = Double.parseDouble(textField_18.getText());

				if (radius < search){

					JOptionPane.showMessageDialog(null, "Distance precomputation radius must be greater than search radius", "Input error",
							JOptionPane.ERROR_MESSAGE);

					textField_8.setText("20");
					textField_18.setText("300");

					return;

				}

			}


			public void focusGained(FocusEvent e) {

			}
		};

		textField_8.addFocusListener(radius_comparison_listener);
		textField_18.addFocusListener(radius_comparison_listener);




		btnReset.addActionListener(new ActionListener() {


			public void actionPerformed(ActionEvent e) {

				int dialogResult = JOptionPane.showConfirmDialog(null, "Are you sure you want to reset parameters ?", "Reset", JOptionPane.YES_NO_OPTION);

				if (dialogResult == 1){

					return;

				}

				chckbxPrintDebugFiles.setText("Print debug files");
				lblNumberOfFiles.setText("Number of files to print :  0");
				textArea_1.setText("");

				textField.setText("");
				textField_1.setText("");

				chckbxNewCheckBox.setSelected(true);
				checkBox.setSelected(true);

				textField_2.setText("0.01");
				textField_2.setEnabled(false);
				textField_3.setText("-1");
				textField_4.setText("");
				txtmmdat.setText("_mm.dat");
				textField_5.setText("100");
				textField_9.setText("0.0");
				textField_12.setText("");
				textField_6.setText("");

				choice_8.setSelectedItem("yyyy-mm-dd hh:mm:ss");

				chckbxRemoveDegree.setSelected(false);
				chckbxBuildTopology.setSelected(false);
				chckbxPrintReportFile.setSelected(true);
				chckbxGraphicalOutput.setSelected(false);
				chckbxSaveParameters.setSelected(false);
				chckbxPrintDebugFiles.setSelected(false);
				chckbxRecordMapmatchedPoint.setSelected(false);
				checkBox_2.setSelected(false);


				label.setEnabled(false);
				textField_2.setEnabled(false);

				chckbxCleanDirectory.setSelected(false);
				chckbxKeepSensorError.setSelected(true);
				chckbxOpenReportFile.setSelected(true);
				chckbxReorganizeLabelsOn.setSelected(false);
				chckbxSkipUnsolvedPoints.setSelected(true);
				chckbxPrecomputeDistancesOn.setSelected(false);
				chckbxLimitNumberOf.setSelected(false);
				chckbxLimitSpeedBetween.setSelected(false);
				chckbxSpatiotemporalAutocorrelationBetween.setSelected(false);
				chckbxComputeEpochbyepochRmse.setSelected(false);
				chckbxComputeEpochbyepochConfidence.setSelected(false);
				chckbxStoreMapmatchedPoints.setSelected(false);

				textField_7.setText("8.0");
				textField_8.setText("20.0");
				textField_11.setText("1.0");
				textField_13.setText("0.0");

				slider.setValue(50);

				chckbxSensorObservationsAre.setSelected(false);

				rdbtnPredicted.setSelected(true);
				rdbtnPredicted.setEnabled(false);
				rdbtnNewRadioButton.setSelected(false);
				rdbtnNewRadioButton.setEnabled(false);

				rdbtnXml.setEnabled(chckbxStoreMapmatchedPoints.isSelected());
				rdbtnCsv.setEnabled(chckbxStoreMapmatchedPoints.isSelected());
				chckbxPrintIndexFor.setEnabled(chckbxStoreMapmatchedPoints.isSelected());
				chckbxPrintIndexFor.setSelected(chckbxStoreMapmatchedPoints.isSelected());

				chckbxComputeEpochbyepochRmse.setSelected(false);
				chckbxRecordMapmatchedPoint.setSelected(false);

				label_18.setEnabled(false);
				label_23.setEnabled(false);
				label_24.setEnabled(false);
				label_25.setEnabled(false);
				label_26.setEnabled(false);

				textField_14.setEnabled(false);
				textField_15.setEnabled(false);

				textField_14.setText("0.0");
				textField_15.setText("0.0");

				comboBox_1.setSelectedIndex(Parameters.DISTRIBUTION_NORMAL-1);

				textField_10.setEnabled(chckbxLimitSpeedBetween.isSelected());
				spinner.setEnabled(chckbxLimitNumberOf.isSelected());

				spinner.setValue(1);

				textField_10.setText("50.0");

				choice.setSelectedItem("");
				choice_1.setSelectedItem("");
				choice_2.setSelectedItem("");
				choice_3.setSelectedItem("");
				choice_4.setSelectedItem("");

				choice_5.setSelectedItem("");
				choice_6.setSelectedItem("");
				choice_7.setSelectedItem("");

				chckbxSpatiotemporalAutocorrelationBetween.setText("Spatio-temporal autocorrelation between measurements");

				addHelpText();
				helpMultipleTrackPaths = true;

				// Reset Parameters class
				Parameters.reset();

			}

		});

		// -----------------------------------------------------
		// Computation
		// -----------------------------------------------------
		btnCompute.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				Loaders.setBufferNull();
				Tools.progressPercentage(0, 100, true);

				// Filling parameters
				if (textField.getText().equals("")){

					JOptionPane.showMessageDialog(null, "No network input path provided", "Error", JOptionPane.ERROR_MESSAGE);
					return;

				}
				else{

					Parameters.input_network_path = textField.getText();

				}


				if (textField_1.getText().equals("")){

					JOptionPane.showMessageDialog(null, "No track input path provided", "Error", JOptionPane.ERROR_MESSAGE);
					return;

				}
				else{

					Parameters.input_track_path = textField_1.getText();

				}


				if (textField_4.getText().equals("")){

					JOptionPane.showMessageDialog(null, "No output folder provided", "Error", JOptionPane.ERROR_MESSAGE);
					return;

				}
				else{

					Parameters.output_path = textField_4.getText();

				}

				if (txtmmdat.getText().equals("")){

					Parameters.output_suffix = "_mm.dat";

				}else{

					Parameters.output_suffix = txtmmdat.getText();

				}


				Parameters.output_debug = chckbxPrintDebugFiles.isSelected();
				Parameters.output_report = chckbxPrintReportFile.isSelected();
				Parameters.output_parameters = chckbxSaveParameters.isSelected();

				Parameters.output_clear = chckbxCleanDirectory.isSelected();
				Parameters.output_errors = chckbxKeepSensorError.isSelected();
				Parameters.output_path_interpolation = chckbxInterpolation.isSelected();
				MapMatching.open_report = chckbxOpenReportFile.isSelected();

				Parameters.buffer_radius = Double.parseDouble(textField_18.getText());

				if (comboBox_2.getSelectedItem().equals("full network")){Parameters.distance_buffer = "full_network";}
				if (comboBox_2.getSelectedItem().equals("buffered tracks")){Parameters.distance_buffer = "buffered_tracks";}
				if (comboBox_2.getSelectedItem().equals("1st buffered track")){Parameters.distance_buffer = "1st_track";}


				Parameters.network_header = chckbxNewCheckBox.isSelected();
				Parameters.track_header = checkBox.isSelected();

				Parameters.graphical_output = chckbxGraphicalOutput.isSelected();

				if (Parameters.network_header){

					Parameters.network_geom_name = (String) choice_1.getSelectedItem();
					Parameters.network_source_name = (String) choice_2.getSelectedItem();
					Parameters.network_target_name = (String) choice_3.getSelectedItem();
					Parameters.network_edge_name = (String) choice.getSelectedItem();
					Parameters.network_oneway_name = (String) choice_4.getSelectedItem();

				}
				else{

					try{

						Parameters.network_geom_id = Integer.parseInt((String)choice_1.getSelectedItem());
						Parameters.network_source_id = Integer.parseInt((String)choice_2.getSelectedItem());
						Parameters.network_target_id = Integer.parseInt((String)choice_3.getSelectedItem());
						Parameters.network_edge_id = Integer.parseInt((String)choice.getSelectedItem());
						Parameters.network_oneway_id = Integer.parseInt((String)choice_4.getSelectedItem());

					}catch(NumberFormatException ex){

						Tools.printError("Error: network column id are not consistent");
						return;

					}

				}

				if (Parameters.track_header){

					Parameters.track_columns_x_name = (String) choice_5.getSelectedItem();
					Parameters.track_columns_y_name = (String) choice_6.getSelectedItem(); 

					if (!choice_7.getSelectedItem().equals("")){
						Parameters.track_columns_t_name = (String) choice_7.getSelectedItem(); 
					}

				}
				else{

					try{

						Parameters.track_columns_x_id = Integer.parseInt((String)choice_5.getSelectedItem());
						Parameters.track_columns_y_id = Integer.parseInt((String)choice_6.getSelectedItem());

					}
					catch(NumberFormatException ex){

						Tools.printError("Error: track column id are not consistent");
						return;

					}

					// Check no time stamp column
					if (choice_7.getSelectedItem() == null){

						choice_7.setSelectedItem("");

					}

					if (!choice_7.getSelectedItem().equals("")){
						Parameters.track_columns_t_id = Integer.parseInt((String)choice_7.getSelectedItem()); 
					}else{
						Parameters.track_columns_t_id = -1;
					}

				}

				if (!choice_8.getSelectedItem().equals("")){

					Parameters.track_date_fmt = (String) choice_8.getSelectedItem();

				}

				if (!textField_3.getText().equals("")){

					Parameters.track_error_code = (String) textField_3.getText();

				}

				Parameters.computation_sigma = Double.parseDouble(textField_7.getText());
				Parameters.computation_radius = Double.parseDouble(textField_8.getText());
				Parameters.computation_beta = Double.parseDouble(textField_11.getText());
				Parameters.computation_transition = Double.parseDouble(textField_9.getText());

				Parameters.network_rmse = Double.parseDouble(textField_17.getText());
				Parameters.network_inaccuracies = chckbxAllowForNetwork.isSelected();

				Parameters.confidence_min_ratio = chckbxComputeEpochbyepochConfidence.isSelected();
				Parameters.confidence_ratio = Double.parseDouble(textField_16.getText());

				if ((Parameters.confidence_ratio > 1.0) && (Parameters.confidence_min_ratio )){

					JOptionPane.showMessageDialog(null, "Minimal confidence ratio must be lesser or equal to 1", "Error", JOptionPane.ERROR_MESSAGE);
					return;

				}


				if ((Parameters.confidence_ratio < 0.0) && (Parameters.confidence_min_ratio )){

					JOptionPane.showMessageDialog(null, "Minimal confidence ratio must be positive", "Error", JOptionPane.ERROR_MESSAGE);
					return;

				}

				if ((Parameters.network_rmse < 0.0) && (Parameters.network_inaccuracies )){

					JOptionPane.showMessageDialog(null, "Network root mean square error must be positive", "Error", JOptionPane.ERROR_MESSAGE);
					return;

				}

				Parameters.gui_report_path = textField_12.getText();
				Parameters.gui_parameters_path = textField_6.getText();

				Parameters.index_format_csv = rdbtnCsv.isSelected();

				Parameters.ref_to_network = chckbxRecordMapmatchedPoint.isSelected();

				Parameters.output_index_all_edge = chckbxPrintIndexFor.isSelected();

				Parameters.remove_deg_2_nodes = chckbxRemoveDegree.isSelected();

				if (comboBox_1.getSelectedItem().equals(CHOICE_DISTRIBUTIONS[1]))     {Parameters.computation_distribution = Parameters.DISTRIBUTION_UNIFORM;}
				if (comboBox_1.getSelectedItem().equals(CHOICE_DISTRIBUTIONS[2]))     {Parameters.computation_distribution = Parameters.DISTRIBUTION_EXPONENTIAL;}
				if (comboBox_1.getSelectedItem().equals(CHOICE_DISTRIBUTIONS[0]))     {Parameters.computation_distribution = Parameters.DISTRIBUTION_NORMAL;}
				if (comboBox_1.getSelectedItem().equals(CHOICE_DISTRIBUTIONS[3]))     {Parameters.computation_distribution = Parameters.DISTRIBUTION_RAYLEIGH;}

				if (chckbxLimitNumberOf.isSelected()){
					Parameters.max_number_candidates = (Integer) spinner.getValue();
				}
				else{
					Parameters.max_number_candidates  = -1;
				}

				if (chckbxLimitSpeedBetween.isSelected()){
					Parameters.computation_speed_limit = Double.parseDouble(textField_10.getText());
				}
				else{
					Parameters.computation_speed_limit  = Double.MAX_VALUE;
				}


				Parameters.failure_skip = chckbxSkipUnsolvedPoints.isSelected();
				Parameters.precompute_distances = chckbxPrecomputeDistancesOn.isSelected();
				Parameters.sort_nodes = chckbxReorganizeLabelsOn.isSelected();

				if (comboBox.getSelectedItem().equals(" source node (m)")){Parameters.abs_curv_type = "from_source_m";}
				if (comboBox.getSelectedItem().equals(" target node (m)")){Parameters.abs_curv_type = "from_target_m";}
				if (comboBox.getSelectedItem().equals(" source node (%)")){Parameters.abs_curv_type = "from_source_%";}
				if (comboBox.getSelectedItem().equals(" target node (%)")){Parameters.abs_curv_type = "from_source_%";}


				Parameters.rmse_type_before = rdbtnNewRadioButton.isSelected();

				if (choice_5.getSelectedItem().equals("")){

					JOptionPane.showMessageDialog(null, "Track X column must be provided", "Error", JOptionPane.ERROR_MESSAGE);
					return;

				}

				if (choice_6.getSelectedItem().equals("")){

					JOptionPane.showMessageDialog(null, "Track Y column must be provided", "Error", JOptionPane.ERROR_MESSAGE);
					return;

				}


				// Test limit speed
				if (chckbxLimitSpeedBetween.isSelected()){

					if (choice_7.getSelectedItem().equals("")){

						JOptionPane.showMessageDialog(null, "Time stamp columnn must be provided to limit speed between points", "Error", JOptionPane.ERROR_MESSAGE);
						return;

					}

				}

				Parameters.computation_angle = Double.parseDouble(textField_13.getText());

				if (chckbxSpatiotemporalAutocorrelationBetween.isSelected()){

					Parameters.computation_autocorrelation = slider.getValue();
					Parameters.computation_scope = Double.parseDouble(textField_5.getText());

				}

				Parameters.remove_deg_2_nodes = chckbxRemoveDegree.isSelected();
				Parameters.make_topology = chckbxBuildTopology.isSelected();

				try{

					Parameters.topo_tolerance = Double.parseDouble(textField_2.getText());

				}catch(NumberFormatException ex){

					JOptionPane.showMessageDialog(null, "Topology tolerance parameter is not valid", "Error", JOptionPane.ERROR_MESSAGE);
					return;


				}

				if (Parameters.topo_tolerance <= 0){

					JOptionPane.showMessageDialog(null, "Topology tolerance parameter must be strictly positive", "Error", JOptionPane.ERROR_MESSAGE);
					return;

				}


				if (chckbxSensorObservationsAre.isSelected()){

					try{

						Parameters.bias_x = Double.parseDouble(textField_14.getText());
						Parameters.bias_y = Double.parseDouble(textField_15.getText());

					}
					catch(NumberFormatException ex){

						JOptionPane.showMessageDialog(null, "Bias parameters are not valid numeric values", "Error", JOptionPane.ERROR_MESSAGE);
						return;

					}

				}

				// -----------------------------------------------------------
				// Test compatibility between sort_nodes and interpolation
				// -----------------------------------------------------------
				if ((!Parameters.sort_nodes) && (Parameters.output_path_interpolation)){

					String message = "Interpolating path between GPS points requires to reorganize node labels after processing.\r\n";
					message = message + "Do you want to activate this option?";		

					int rep = JOptionPane.showConfirmDialog(null, message, "Warning", JOptionPane.YES_NO_CANCEL_OPTION);

					if (rep == JOptionPane.CANCEL_OPTION){
						return;
					}
					else if(rep == JOptionPane.YES_OPTION){
						chckbxReorganizeLabelsOn.setSelected(true);
					}

				}
				
				// -----------------------------------------------------------
				// Test compatibility between interpolation and buffer
				// -----------------------------------------------------------
				if ((!Parameters.distance_buffer.equals("full_network")) && (Parameters.output_path_interpolation)){

					String message = "Cannot return interpolated path when buffer optimization is activated.\r\n";

					JOptionPane.showMessageDialog(null, message, "Warning", JOptionPane.WARNING_MESSAGE);
		
					chckbxInterpolation.setSelected(false);
					Parameters.output_path_interpolation = false;

				}

				// -----------------------------------------------------------
				// Test coordinates system
				// -----------------------------------------------------------
				Loaders.parameterize();
				Track track = Loaders.loadTrack(Parameters.input_track_path_list.get(0));

				double x = track.getX().get(0);
				double y = track.getY().get(0);

				boolean wgs84_probable = Parameters.input_track_path.endsWith(".gpx");
				wgs84_probable = wgs84_probable || ((x >= -180) && (x <= 180) && (y >= -90) && (x <= 90));

				if (wgs84_probable && (!checkBox_2.isSelected())){

					String message = "Point coordinates may be in geographic system (decimal degrees).\r\n";
					message = message + "Do you want to convert them to planimetric system before map matching?";		

					int rep = JOptionPane.showConfirmDialog(null, message, "Warning", JOptionPane.YES_NO_CANCEL_OPTION);

					if (rep == JOptionPane.CANCEL_OPTION){
						return;
					}
					else if(rep == JOptionPane.YES_OPTION){
						checkBox_2.setSelected(true);
					}

				}

				Parameters.project_coordinates = checkBox_2.isSelected();

				// -----------------------------------------------------------

				Parameters.output_confidence = chckbxComputeEpochbyepochConfidence.isSelected();
				Parameters.output_rmse = chckbxComputeEpochbyepochRmse.isSelected();
				Parameters.add_spatial_index = chckbxStoreMapmatchedPoints.isSelected();
				Parameters.output_index_coords = chckbxPrintPointsCoordinates.isSelected();

				// Corrections in input
				Parameters.input_network_path = Parameters.input_network_path.replaceAll("(^ )|( $)", "");
				Parameters.input_track_path = Parameters.input_track_path.replaceAll("(^ )|( $)", "");
				Parameters.output_path = Parameters.output_path.replaceAll("(^ )|( $)", "");

				// Block button
				btnCompute.setEnabled(false);


				Thread worker = new Thread() {
					public void run() {


						final int code = MapMatching.executeAllProcessFromGUI();

						SwingUtilities.invokeLater(new Runnable() {
							public void run() {


								// Process terminated
								if ((!MapMatching.open_report || !Parameters.output_report) && (!Parameters.graphical_output) && (code == 0)){

									JOptionPane.showMessageDialog(null, "Map matching has been performed with success", "Status", JOptionPane.INFORMATION_MESSAGE);

								}

								btnCompute.setEnabled(true);

								return;

							}
						});

					}
				};

				worker.start();


			}
		});

		JPanel panel_4 = new JPanel();
		tabbedPane.addTab("Advanced", null, panel_4, null);
		panel_4.setLayout(null);

		JLabel lblInputTracks = new JLabel("Input coordinates");
		lblInputTracks.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblInputTracks.setBounds(22, 11, 148, 14);
		panel_4.add(lblInputTracks);

		JLabel lblAlgorithm = new JLabel("Computation parameters");
		lblAlgorithm.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblAlgorithm.setBounds(22, 152, 148, 14);
		panel_4.add(lblAlgorithm);

		Label label_22 = new Label("Sensor distribution:");
		label_22.setBounds(22, 39, 103, 14);
		panel_4.add(label_22);

		comboBox_1 = new JComboBox<String>();
		comboBox_1.setModel(new DefaultComboBoxModel(new String[] {"Normal   ", "Uniform", "Exponential", "Rayleigh"}));
		comboBox_1.setBounds(131, 36, 89, 20);
		panel_4.add(comboBox_1);

		textField_14 = new JTextField();
		textField_14.setEnabled(false);
		textField_14.setText("0.0");
		textField_14.setHorizontalAlignment(SwingConstants.CENTER);
		textField_14.setColumns(10);
		textField_14.setBounds(286, 64, 45, 20);
		panel_4.add(textField_14);

		label_18 = new Label("Sensor bias:");
		label_18.setEnabled(false);
		label_18.setBounds(267, 39, 75, 14);
		panel_4.add(label_18);

		label_23 = new Label("Bx = ");
		label_23.setEnabled(false);
		label_23.setBounds(253, 68, 29, 14);
		panel_4.add(label_23);

		label_24 = new Label("m");
		label_24.setEnabled(false);
		label_24.setBounds(335, 68, 29, 14);
		panel_4.add(label_24);

		label_25 = new Label("By = ");
		label_25.setEnabled(false);
		label_25.setBounds(253, 94, 29, 14);
		panel_4.add(label_25);

		textField_15 = new JTextField();
		textField_15.setEnabled(false);
		textField_15.setText("0.0");
		textField_15.setHorizontalAlignment(SwingConstants.CENTER);
		textField_15.setColumns(10);
		textField_15.setBounds(286, 90, 45, 20);
		panel_4.add(textField_15);

		label_26 = new Label("m");
		label_26.setEnabled(false);
		label_26.setBounds(335, 94, 29, 14);
		panel_4.add(label_26);

		Label label_15 = new Label("Edge transition cost:");
		label_15.setBounds(225, 177, 103, 22);
		panel_4.add(label_15);

		textField_9 = new JTextField();
		textField_9.setText("0.0");
		textField_9.setHorizontalAlignment(SwingConstants.CENTER);
		textField_9.setColumns(10);
		textField_9.setBounds(334, 177, 45, 20);
		panel_4.add(textField_9);

		chckbxSensorObservationsAre = new JCheckBox(" Sensor observations are biased");
		chckbxSensorObservationsAre.setBounds(24, 65, 191, 23);
		panel_4.add(chckbxSensorObservationsAre);

		button_9 = new JButton("Previous");
		button_9.setBounds(25, 384, 89, 25);
		panel_4.add(button_9);

		button_10 = new JButton("Quit");
		button_10.setBounds(290, 384, 89, 25);
		panel_4.add(button_10);

		chckbxComputeEpochbyepochConfidence = new JCheckBox(" Compute epoch-by-epoch confidence index");
		chckbxComputeEpochbyepochConfidence.setBounds(22, 242, 245, 23);
		panel_4.add(chckbxComputeEpochbyepochConfidence);

		chckbxComputeEpochbyepochRmse = new JCheckBox(" Compute epoch-by-epoch rmse");
		chckbxComputeEpochbyepochRmse.setBounds(22, 177, 199, 23);
		panel_4.add(chckbxComputeEpochbyepochRmse);

		JLabel lblOutputNetwork = new JLabel("Output");
		lblOutputNetwork.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblOutputNetwork.setBounds(22, 282, 148, 14);
		panel_4.add(lblOutputNetwork);

		chckbxStoreMapmatchedPoints = new JCheckBox(" Store map-matched points in network edges");
		chckbxStoreMapmatchedPoints.setBounds(22, 327, 245, 23);
		panel_4.add(chckbxStoreMapmatchedPoints);

		checkBox_2 = new JCheckBox(" Project coordinates in metric system");
		checkBox_2.setBounds(24, 90, 221, 23);
		panel_4.add(checkBox_2);

		rdbtnNewRadioButton = new JRadioButton("Before map-matching (predicted)");
		rdbtnNewRadioButton.setEnabled(false);
		rdbtnNewRadioButton.setBounds(32, 197, 188, 23);
		panel_4.add(rdbtnNewRadioButton);

		rdbtnPredicted = new JRadioButton("After map-matching (observed)");
		rdbtnPredicted.setSelected(true);
		rdbtnPredicted.setEnabled(false);
		rdbtnPredicted.setBounds(32, 217, 177, 23);
		panel_4.add(rdbtnPredicted);

		chckbxRecordMapmatchedPoint = new JCheckBox(" Record points curvilinear abscissa from :");
		chckbxRecordMapmatchedPoint.setBounds(22, 302, 227, 23);
		panel_4.add(chckbxRecordMapmatchedPoint);

		rdbtnCsv = new JRadioButton("csv");
		rdbtnCsv.setSelected(true);
		rdbtnCsv.setEnabled(false);
		rdbtnCsv.setBounds(270, 327, 45, 23);
		panel_4.add(rdbtnCsv);

		rdbtnXml = new JRadioButton("xml");
		rdbtnXml.setEnabled(false);
		rdbtnXml.setBounds(319, 327, 45, 23);
		panel_4.add(rdbtnXml);

		chckbxPrintIndexFor = new JCheckBox("Print index for empty edges");
		chckbxPrintIndexFor.setEnabled(false);
		chckbxPrintIndexFor.setBounds(30, 347, 164, 23);
		panel_4.add(chckbxPrintIndexFor);

		comboBox = new JComboBox<String>();
		comboBox.setEnabled(false);
		comboBox.setModel(new DefaultComboBoxModel(new String[] {" source node (m)", " target node (m)", " source node (%)", " target node (%)"}));
		comboBox.setBounds(253, 302, 111, 20);
		panel_4.add(comboBox);

		chckbxPrintPointsCoordinates = new JCheckBox("Print coordinates in index file");
		chckbxPrintPointsCoordinates.setEnabled(false);
		chckbxPrintPointsCoordinates.setBounds(200, 347, 197, 23);
		panel_4.add(chckbxPrintPointsCoordinates);

		final Label label_27 = new Label("Min confidence :");
		label_27.setEnabled(false);
		label_27.setBounds(280, 218, 89, 22);
		panel_4.add(label_27);

		textField_16 = new JTextField();
		textField_16.setEnabled(false);
		textField_16.setText("0.0");
		textField_16.setHorizontalAlignment(SwingConstants.CENTER);
		textField_16.setColumns(10);
		textField_16.setBounds(300, 243, 45, 20);
		panel_4.add(textField_16);

		chckbxAllowForNetwork = new JCheckBox(" Allow for network inaccuracies");
		chckbxAllowForNetwork.setBounds(24, 115, 209, 23);
		panel_4.add(chckbxAllowForNetwork);

		textField_17 = new JTextField();
		textField_17.setText("0.0");
		textField_17.setHorizontalAlignment(SwingConstants.CENTER);
		textField_17.setEnabled(false);
		textField_17.setColumns(10);
		textField_17.setBounds(286, 116, 45, 20);
		panel_4.add(textField_17);

		final Label label_28 = new Label("RMSE = ");
		label_28.setEnabled(false);
		label_28.setBounds(237, 120, 45, 14);
		panel_4.add(label_28);

		final Label label_29 = new Label("m");
		label_29.setEnabled(false);
		label_29.setBounds(335, 118, 29, 14);
		panel_4.add(label_29);



		tabbedPane.addTab("Graphical plot", null, graphics, null);



		JPanel panel_3 = new JPanel();
		tabbedPane.addTab("About", null, panel_3, null);
		panel_3.setBackground(Color.WHITE);
		panel_3.setLayout(null);

		JLabel lblAboutMapmatcherSoftware = new JLabel("Mapmatcher v1.0");
		lblAboutMapmatcherSoftware.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblAboutMapmatcherSoftware.setBounds(22, 11, 240, 14);
		panel_3.add(lblAboutMapmatcherSoftware);

		JLabel label_21 = new JLabel("Mapmatcher v1.0");
		label_21.setFont(new Font("Tahoma", Font.PLAIN, 11));
		label_21.setBounds(85, 80, 240, 240);
		label_21.setIcon(Main.img);
		panel_3.add(label_21);

		btnHelp = new JButton("Help");
		btnHelp.setBounds(290, 384, 89, 25);
		panel_3.add(btnHelp);

		chckbxActivateHelp = new JCheckBox("Activate tool tips");
		chckbxActivateHelp.setBackground(Color.WHITE);
		chckbxActivateHelp.setBounds(23, 385, 112, 23);
		chckbxActivateHelp.setSelected(true);
		panel_3.add(chckbxActivateHelp);


		// ---------------------------------------------------------------------------------------
		// Actions
		// ---------------------------------------------------------------------------------------

		// Input network rmse
		chckbxAllowForNetwork.addActionListener(new ActionListener() {


			public void actionPerformed(ActionEvent e) {

				label_28.setEnabled(chckbxAllowForNetwork.isSelected());
				label_29.setEnabled(chckbxAllowForNetwork.isSelected());
				textField_17.setEnabled(chckbxAllowForNetwork.isSelected());

			}
		});

		// Compute rmse at each time step
		chckbxComputeEpochbyepochRmse.addActionListener(new ActionListener() {


			public void actionPerformed(ActionEvent e) {

				rdbtnPredicted.setEnabled(chckbxComputeEpochbyepochRmse.isSelected());
				rdbtnNewRadioButton.setEnabled(chckbxComputeEpochbyepochRmse.isSelected());

			}
		});

		// Compute rmse after map-matching
		rdbtnNewRadioButton.addActionListener(new ActionListener() {


			public void actionPerformed(ActionEvent e) {

				if (!rdbtnNewRadioButton.isSelected()){

					rdbtnNewRadioButton.setSelected(true);
					return;

				}

				rdbtnPredicted.setSelected(!rdbtnNewRadioButton.isSelected());

			}
		});

		// Compute rmse before map-matching
		rdbtnPredicted.addActionListener(new ActionListener() {


			public void actionPerformed(ActionEvent e) {

				if (!rdbtnPredicted.isSelected()){

					rdbtnPredicted.setSelected(true);
					return;

				}

				rdbtnNewRadioButton.setSelected(!rdbtnPredicted.isSelected());

			}
		});

		// Input network file chooser
		btnNewButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				String folder = "";

				if (new File(textField.getText()).exists()){

					folder = new File(textField.getText()).getAbsolutePath();

				}

				JFileChooser filechooser = new JFileChooser(new File(folder)){

					protected JDialog createDialog( Component parent ) throws HeadlessException {
						JDialog dialog = super.createDialog( parent );
						dialog.setIconImage( Main.img.getImage());
						return dialog;
					}

				};

				filechooser.setDialogTitle("Input network");
				filechooser.showOpenDialog(null);

				if (filechooser.getSelectedFile() == null){

					return;

				}

				textField.setText(filechooser.getSelectedFile().getPath());

				fillNetworkComboBoxes();

				setListOfOutputs();

			}
		});

		// Change path of input network in text field
		textField.getDocument().addDocumentListener(new DocumentListener() {

			public void changedUpdate(DocumentEvent e) {
				test_load();
			}
			public void removeUpdate(DocumentEvent e) {
				test_load();
			}
			public void insertUpdate(DocumentEvent e) {
				test_load();
			}

			public void test_load() {

				fillNetworkComboBoxes();

			}
		});

		// Input network file delimiter button
		btnS.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				String output = JOptionPane.showInputDialog("Delimiter for network file: ", Parameters.network_delimiter);

				if ((output != null) && (!output.equals(""))){

					Parameters.network_delimiter = output;

				}

				fillNetworkComboBoxes();

			}
		});


		// First line contains header check box
		chckbxNewCheckBox.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				fillNetworkComboBoxes();

			}
		});


		// Build topology check box
		chckbxBuildTopology.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				setListOfOutputs();

				label.setEnabled(chckbxBuildTopology.isSelected());
				textField_2.setEnabled(chckbxBuildTopology.isSelected());
				label_1.setEnabled(chckbxBuildTopology.isSelected());

				choice_2.setEnabled(!chckbxBuildTopology.isSelected());
				choice_3.setEnabled(!chckbxBuildTopology.isSelected());
				choice_4.setEnabled(!chckbxBuildTopology.isSelected());

				label_5.setEnabled(!chckbxBuildTopology.isSelected());
				label_6.setEnabled(!chckbxBuildTopology.isSelected());
				label_7.setEnabled(!chckbxBuildTopology.isSelected());

			}
		});



		// Button to change page section
		btnNext.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				tabbedPane.setSelectedIndex(1);

			}
		});

		// Button to change page section
		button_7.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				tabbedPane.setSelectedIndex(2);

			}
		});

		// Button to change page section
		btnPrevious.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				tabbedPane.setSelectedIndex(0);

			}
		});

		// Input network file chooser
		button.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				String folder = "";

				if (new File(textField_1.getText()).exists()){

					folder = new File(textField_1.getText()).getAbsolutePath();

				}

				JFileChooser filechooser = new JFileChooser(new File(folder)){

					protected JDialog createDialog( Component parent ) throws HeadlessException {
						JDialog dialog = super.createDialog( parent );
						dialog.setIconImage( Main.img.getImage() );
						return dialog;
					}

				};
				filechooser.setDialogTitle("Input track(s)");
				filechooser.showOpenDialog(null);

				if (filechooser.getSelectedFile() == null){

					return;

				}

				textField_1.setText(filechooser.getSelectedFile().getPath());

				fillTrackComboBoxes();

			}
		});

		// Input tracks file delimiter button
		button_2.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				String output = JOptionPane.showInputDialog("Delimiter for track file: ", Parameters.track_delimiter);

				if ((output != null) && (!output.equals(""))){

					Parameters.track_delimiter = output;

				}

				fillTrackComboBoxes();

			}
		});

		// First line contains header check box
		checkBox.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				fillTrackComboBoxes();

			}
		});

		// Change path of input track in text field
		textField_1.addFocusListener(new FocusListener() {


			public void focusLost(FocusEvent e) {

				fillTrackComboBoxes();

			}


			public void focusGained(FocusEvent e) {

				removeHelpText();

			}
		});

		textField_1.addKeyListener(new KeyAdapter() 
		{
			public void keyPressed(KeyEvent evt)
			{
				if(evt.getKeyCode() == KeyEvent.VK_ENTER){

					fillTrackComboBoxes();

				}
			}
		});

		// Output file chooser
		button_3.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {


				String folder = "";


				if (new File(textField_4.getText()).exists()){

					folder = new File(textField_4.getText()).getAbsolutePath();

				}

				JFileChooser filechooser = new JFileChooser(new File(folder)){

					protected JDialog createDialog( Component parent ) throws HeadlessException {
						JDialog dialog = super.createDialog( parent );
						dialog.setIconImage( Main.img.getImage() );
						return dialog;
					}

				};
				filechooser.setDialogTitle("Output folder");
				filechooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				filechooser.showOpenDialog(null);


				if (filechooser.getSelectedFile() == null){

					return;

				}

				textField_4.setText(filechooser.getSelectedFile().getPath());

				textField_12.setText((textField_4.getText()+"/map_matching_report.txt").replace("\\", "/").replace("//", "/"));

				setListOfOutputs();

			}
		});

		// Change path of output folder in text field
		textField_4.getDocument().addDocumentListener(new DocumentListener() {

			public void changedUpdate(DocumentEvent e) {
				changeReport();
			}
			public void removeUpdate(DocumentEvent e) {
				changeReport();
			}
			public void insertUpdate(DocumentEvent e) {
				changeReport();
			}

			public void changeReport() {

				if (!textField_4.getText().equals("")){

					textField_12.setText((textField_4.getText()+"/map_matching_report.txt").replace("\\", "/").replace("//", "/"));
					textField_6.setText((textField_4.getText()+"/parameters.txt").replace("\\", "/").replace("//", "/"));

				}else{

					textField_12.setText("");
					textField_6.setText("");

				}

			}
		});


		// Output file delimiter button
		button_4.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				String output = JOptionPane.showInputDialog("Delimiter for output file(s): ", Parameters.output_delimiter);

				if ((output != null) && (!output.equals(""))){

					Parameters.output_delimiter = output;

				}

				setListOfOutputs();

			}
		});

		// Output report file check box
		chckbxPrintReportFile.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				chckbxOpenReportFile.setEnabled(chckbxPrintReportFile.isSelected());

				lblNumberOfFiles.setText("Number of files to print :  "+computeNumberOfFilesToPrint());

				textField_12.setEnabled(chckbxPrintReportFile.isSelected());
				button_8.setEnabled(chckbxPrintReportFile.isSelected());

			}
		});

		// Output path interpolation check box
		chckbxInterpolation.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				lblNumberOfFiles.setText("Number of files to print :  "+computeNumberOfFilesToPrint());

			}
		});

		// Remove degree 2 nodes
		chckbxRemoveDegree.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				setListOfOutputs();
				choice.setEnabled(!chckbxRemoveDegree.isSelected());

			}
		});


		// Save parameters check box
		chckbxSaveParameters.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				textField_6.setEnabled(chckbxSaveParameters.isSelected());
				button_6.setEnabled(chckbxSaveParameters.isSelected());

				lblNumberOfFiles.setText("Number of files to print :  "+computeNumberOfFilesToPrint());

			}
		});

		// Debug files check box
		chckbxPrintDebugFiles.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				lblNumberOfFiles.setText("Number of files to print :  "+computeNumberOfFilesToPrint());

			}
		});



		// Report file chooser
		button_8.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {


				String folder = "";


				if (new File(textField_12.getText()).exists()){

					folder = new File(textField_12.getText()).getAbsolutePath();

				}

				JFileChooser filechooser = new JFileChooser(new File(folder)){

					protected JDialog createDialog( Component parent ) throws HeadlessException {
						JDialog dialog = super.createDialog( parent );
						dialog.setIconImage( Main.img.getImage() );
						return dialog;
					}

				};
				filechooser.setDialogTitle("Report file");
				filechooser.showOpenDialog(null);


				if (filechooser.getSelectedFile() == null){

					return;

				}

				textField_12.setText(filechooser.getSelectedFile().getAbsolutePath());

			}
		});

		// Save parameters file chooser
		button_6.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {


				String folder = "";


				if (new File(textField_6.getText()).exists()){

					folder = new File(textField_6.getText()).getAbsolutePath();

				}

				JFileChooser filechooser = new JFileChooser(new File(folder)){

					protected JDialog createDialog( Component parent ) throws HeadlessException {
						JDialog dialog = super.createDialog( parent );
						dialog.setIconImage( Main.img.getImage() );
						return dialog;
					}

				};
				filechooser.setDialogTitle("Parameter file");
				filechooser.showOpenDialog(null);


				if (filechooser.getSelectedFile() == null){

					return;

				}

				textField_6.setText(filechooser.getSelectedFile().getAbsolutePath());

			}
		});

		// Edit list of files to print
		FocusListener fl = new FocusListener() {


			public void focusLost(FocusEvent e) {

				setListOfOutputs();

			}


			public void focusGained(FocusEvent e) {

			}
		};

		textField.addFocusListener(fl);
		textField_4.addFocusListener(fl);
		txtmmdat.addFocusListener(fl);

		txtmmdat.getDocument().addDocumentListener(new DocumentListener() {



			public void removeUpdate(DocumentEvent e) {

				if (Parameters.input_track_path_list == null){

					return;

				}

				if(Parameters.input_track_path_list.size() < 10){

					setListOfOutputs();

				}

			}


			public void insertUpdate(DocumentEvent e) {

				if (Parameters.input_track_path_list == null){

					return;

				}

				if(Parameters.input_track_path_list.size() < 10){
					setListOfOutputs();	
				}
			}


			public void changedUpdate(DocumentEvent e) {

				if (Parameters.input_track_path_list == null){

					return;

				}

				if(Parameters.input_track_path_list.size() < 10){
					setListOfOutputs();
				}
			}
		});

		button_5.addActionListener(new ActionListener() {


			public void actionPerformed(ActionEvent e) {

				visualize(textField.getText(), "Network input file");

			}
		});


		button_1.addActionListener(new ActionListener() {


			public void actionPerformed(ActionEvent e) {

				if (Parameters.input_track_path_list == null){

					JOptionPane.showMessageDialog(null, "File path must be specified before visualization", "Input warning", JOptionPane.WARNING_MESSAGE);
					return;

				}else{

					visualize(Parameters.input_track_path_list.get(0), "Track input file");

				}

			}
		});

		btnHelp.addActionListener(new ActionListener() {


			public void actionPerformed(ActionEvent e) {

				if (Desktop.isDesktopSupported()) {
					try {
						Desktop.getDesktop().browse(new URI(web_link));
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}else {

					File readme = new File("readme.txt");

					if (!readme.exists()) {

						JOptionPane.showMessageDialog(null, "Warning: cannot find readme file", "Warning", JOptionPane.WARNING_MESSAGE);

						return;

					}



					try {

						Desktop.getDesktop().edit(readme);

					} catch (IOException ex) {

						JOptionPane.showMessageDialog(null, "Warning: cannot open readme file", "Warning", JOptionPane.WARNING_MESSAGE);

					}

				}

			}

		});

		chckbxRecordMapmatchedPoint.addActionListener(new ActionListener() {


			public void actionPerformed(ActionEvent e) {

				comboBox.setEnabled(chckbxRecordMapmatchedPoint.isSelected());

			}
		});


		// Help
		chckbxActivateHelp.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				setToolTips(chckbxActivateHelp.isSelected());

			}

		});


		// Sensor is biased
		chckbxSensorObservationsAre.addActionListener(new ActionListener() {


			public void actionPerformed(ActionEvent e) {

				label_18.setEnabled(chckbxSensorObservationsAre.isSelected());
				label_23.setEnabled(chckbxSensorObservationsAre.isSelected());
				label_24.setEnabled(chckbxSensorObservationsAre.isSelected());
				label_25.setEnabled(chckbxSensorObservationsAre.isSelected());
				label_26.setEnabled(chckbxSensorObservationsAre.isSelected());

				textField_14.setEnabled(chckbxSensorObservationsAre.isSelected());
				textField_15.setEnabled(chckbxSensorObservationsAre.isSelected());

			}
		});

		button_9.addActionListener(new ActionListener() {


			public void actionPerformed(ActionEvent e) {

				tabbedPane.setSelectedIndex(2);

			}
		});

		button_10.addActionListener(new ActionListener() {


			public void actionPerformed(ActionEvent e) {

				int dialogResult = JOptionPane.showConfirmDialog(null, "Are you sure you want to quit Map Matcher ?", "Quit", JOptionPane.YES_NO_OPTION);

				if (dialogResult == 0){

					System.exit(0);

				}

			}
		});


		chckbxComputeEpochbyepochConfidence.addActionListener(new ActionListener() {


			public void actionPerformed(ActionEvent e) {

				if (chckbxComputeEpochbyepochConfidence.isSelected()){

					int answer = JOptionPane.showConfirmDialog(
							null,
							"Computing confidence indices for each epoch is computationnaly expensive.\n"
									+ "This may render the process slower. Do you want to continue?",
									"Confirmation",
									JOptionPane.YES_NO_OPTION);

					if (answer == 1){

						chckbxComputeEpochbyepochConfidence.setSelected(false);

					}

				}

				label_27.setEnabled(chckbxComputeEpochbyepochConfidence.isSelected());
				textField_16.setEnabled(chckbxComputeEpochbyepochConfidence.isSelected());


			}

		});

		// Save index
		chckbxStoreMapmatchedPoints.addActionListener(new ActionListener() {


			public void actionPerformed(ActionEvent e) {

				setListOfOutputs();

				rdbtnCsv.setEnabled(chckbxStoreMapmatchedPoints.isSelected());
				rdbtnXml.setEnabled(chckbxStoreMapmatchedPoints.isSelected());

				chckbxPrintIndexFor.setEnabled(chckbxStoreMapmatchedPoints.isSelected());
				chckbxPrintPointsCoordinates.setEnabled(chckbxStoreMapmatchedPoints.isSelected());

			}

		});


		// Save index in CSV
		rdbtnCsv.addActionListener(new ActionListener() {


			public void actionPerformed(ActionEvent e) {

				if (!rdbtnCsv.isSelected()){

					rdbtnCsv.setSelected(true);
					return;

				}

				rdbtnXml.setSelected(!rdbtnCsv.isSelected());

			}

		});

		// Save index in XML
		rdbtnXml.addActionListener(new ActionListener() {


			public void actionPerformed(ActionEvent e) {

				if (!rdbtnXml.isSelected()){

					rdbtnXml.setSelected(true);
					return;

				}

				rdbtnCsv.setSelected(!rdbtnXml.isSelected());

			}

		});

		button_11.addActionListener(new ActionListener() {


			public void actionPerformed(ActionEvent e) {

				String folder = "";


				if (new File(textField_12.getText()).exists()){

					folder = new File(textField_12.getText()).getAbsolutePath();

				}

				JFileChooser filechooser = new JFileChooser(new File(folder)){

					protected JDialog createDialog( Component parent ) throws HeadlessException {
						JDialog dialog = super.createDialog( parent );
						dialog.setIconImage( Main.img.getImage() );
						return dialog;
					}

				};

				filechooser.setDialogTitle("Parameters file");
				filechooser.showOpenDialog(null);


				if (filechooser.getSelectedFile() == null){

					return;

				}
				else{

					removeHelpText();
					Parameters.load(filechooser.getSelectedFile().getAbsolutePath());


					// ------------------------------------------------------------------
					// Filling interface fields
					// ------------------------------------------------------------------


					textField.setText(Parameters.input_network_path);
					textField_1.setText(Parameters.input_track_path);

					chckbxNewCheckBox.setSelected(Parameters.network_header);
					checkBox.setSelected(Parameters.track_header);

					fillNetworkComboBoxes();
					fillTrackComboBoxes();

					textField_2.setText(Parameters.topo_tolerance+"");
					textField_3.setText(Parameters.track_error_code);
					textField_4.setText(Parameters.output_path);
					txtmmdat.setText(Parameters.output_suffix);
					textField_5.setText(Parameters.computation_scope+"");
					textField_9.setText(Parameters.computation_transition+"");
					textField_12.setText((Parameters.output_path+"/map_matching_report.txt").replace("\\", "/").replace("//", "/"));
					textField_6.setText((textField_4.getText()+"/parameters.txt").replace("\\", "/").replace("//", "/"));

					choice_8.setSelectedItem(Parameters.track_date_fmt);

					chckbxRemoveDegree.setSelected(Parameters.remove_deg_2_nodes);
					chckbxBuildTopology.setSelected(Parameters.make_topology);
					chckbxPrintReportFile.setSelected(Parameters.output_report);
					chckbxGraphicalOutput.setSelected(Parameters.graphical_output);
					chckbxSaveParameters.setSelected(Parameters.output_parameters);
					chckbxPrintDebugFiles.setSelected(Parameters.output_debug);
					checkBox_2.setSelected(Parameters.project_coordinates);

					textField_6.setEnabled(chckbxSaveParameters.isSelected());
					button_6.setEnabled(chckbxSaveParameters.isSelected());

					textField_12.setEnabled(chckbxPrintReportFile.isSelected());
					button_8.setEnabled(chckbxPrintReportFile.isSelected());
					chckbxOpenReportFile.setEnabled(chckbxPrintReportFile.isSelected());

					label.setEnabled(chckbxBuildTopology.isSelected());
					label.setEnabled(chckbxBuildTopology.isSelected());
					textField_2.setEnabled(chckbxBuildTopology.isSelected());

					chckbxCleanDirectory.setSelected(Parameters.output_clear);
					chckbxKeepSensorError.setSelected(Parameters.output_errors);
					chckbxOpenReportFile.setSelected(Parameters.output_errors);
					chckbxReorganizeLabelsOn.setSelected(Parameters.sort_nodes);
					chckbxSkipUnsolvedPoints.setSelected(Parameters.failure_skip);
					chckbxPrecomputeDistancesOn.setSelected(Parameters.precompute_distances);
					chckbxLimitNumberOf.setSelected(Parameters.max_number_candidates != -1);
					chckbxLimitSpeedBetween.setSelected(Parameters.computation_speed_limit != Double.MAX_VALUE);
					chckbxSpatiotemporalAutocorrelationBetween.setSelected(Parameters.computation_autocorrelation != 0.0);
					chckbxInterpolation.setSelected(Parameters.output_path_interpolation);

					chckbxComputeEpochbyepochRmse.setSelected(Parameters.output_rmse);
					chckbxComputeEpochbyepochConfidence.setSelected(Parameters.output_confidence);
					chckbxStoreMapmatchedPoints.setSelected(Parameters.add_spatial_index); 
					chckbxPrintPointsCoordinates.setEnabled(Parameters.add_spatial_index);
					chckbxPrintIndexFor.setEnabled(Parameters.add_spatial_index);
					chckbxRecordMapmatchedPoint.setSelected(Parameters.ref_to_network);

					textField_17.setText(Parameters.network_rmse+"");
					chckbxAllowForNetwork.setSelected(Parameters.network_inaccuracies);

					textField_16.setText(Parameters.confidence_ratio+"");
					chckbxComputeEpochbyepochConfidence.setSelected(Parameters.confidence_min_ratio);

					textField_17.setEnabled(chckbxAllowForNetwork.isSelected());
					textField_16.setEnabled(chckbxComputeEpochbyepochConfidence.isSelected());

					label_28.setEnabled(chckbxAllowForNetwork.isSelected());
					label_29.setEnabled(chckbxAllowForNetwork.isSelected());

					label_27.setEnabled(chckbxComputeEpochbyepochConfidence.isSelected());

					textField_7.setText(Parameters.computation_sigma+"");
					textField_8.setText(Parameters.computation_radius+"");
					textField_11.setText(Parameters.computation_beta+"");
					textField_13.setText(Parameters.computation_angle+"");

					rdbtnCsv.setSelected(Parameters.index_format_csv);
					rdbtnXml.setSelected(!Parameters.index_format_csv);

					rdbtnCsv.setEnabled(chckbxStoreMapmatchedPoints.isSelected());
					rdbtnXml.setEnabled(chckbxStoreMapmatchedPoints.isSelected());

					chckbxPrintIndexFor.setEnabled(chckbxStoreMapmatchedPoints.isSelected());
					chckbxPrintIndexFor.setSelected(Parameters.output_index_all_edge);
					chckbxPrintPointsCoordinates.setSelected(Parameters.output_index_coords);

					slider.setValue((int)Parameters.computation_autocorrelation);

					chckbxSensorObservationsAre.setSelected((Parameters.bias_x != 0)||(Parameters.bias_y != 0));

					label_18.setEnabled(chckbxSensorObservationsAre.isSelected());
					label_23.setEnabled(chckbxSensorObservationsAre.isSelected());
					label_24.setEnabled(chckbxSensorObservationsAre.isSelected());
					label_25.setEnabled(chckbxSensorObservationsAre.isSelected());
					label_26.setEnabled(chckbxSensorObservationsAre.isSelected());

					textField_14.setEnabled(chckbxSensorObservationsAre.isSelected());
					textField_15.setEnabled(chckbxSensorObservationsAre.isSelected());

					textField_14.setText(Parameters.bias_x+"");
					textField_15.setText(Parameters.bias_y+"");

					comboBox_1.setSelectedIndex(Parameters.computation_distribution-1);

					textField_10.setEnabled(chckbxLimitSpeedBetween.isSelected());
					spinner.setEnabled(chckbxLimitNumberOf.isSelected());

					comboBox.setEnabled(chckbxRecordMapmatchedPoint.isSelected());


					if (Parameters.abs_curv_type.equals("from_source_m")){comboBox.setSelectedItem(" source node (m)");}
					if (Parameters.abs_curv_type.equals("from_target_m")){comboBox.setSelectedItem(" target node (m)");}
					if (Parameters.abs_curv_type.equals("from_source_%")){comboBox.setSelectedItem(" source node (%)");}
					if (Parameters.abs_curv_type.equals("from_source_%")){comboBox.setSelectedItem(" target node (%)");}

					slider.setEnabled(chckbxSpatiotemporalAutocorrelationBetween.isSelected());
					label_19.setEnabled(chckbxSpatiotemporalAutocorrelationBetween.isSelected());
					textField_5.setEnabled(chckbxSpatiotemporalAutocorrelationBetween.isSelected());

					if (Parameters.distance_buffer.equals("full_network")){comboBox_2.setSelectedItem("full newtork");}
					if (Parameters.distance_buffer.equals("buffered_tracks")){comboBox_2.setSelectedItem("buffered tracks");}
					if (Parameters.distance_buffer.equals("1st_track")){comboBox_2.setSelectedItem("1st buffered track");}

					textField_18.setText(""+Parameters.buffer_radius);

					if (Parameters.max_number_candidates != -1){

						spinner.setValue(Parameters.max_number_candidates);

					}

					if (Parameters.computation_speed_limit != Double.MAX_VALUE){

						textField_10.setText(Parameters.computation_speed_limit+"");

					}

					if (Parameters.network_header){

						choice.setSelectedItem(Parameters.network_edge_name);
						choice_1.setSelectedItem(Parameters.network_geom_name);
						choice_2.setSelectedItem(Parameters.network_source_name);
						choice_3.setSelectedItem(Parameters.network_target_name);
						choice_4.setSelectedItem(Parameters.network_oneway_name);

					}
					else{

						choice.setSelectedItem(Parameters.network_edge_id);
						choice_1.setSelectedIndex(Parameters.network_geom_id);
						choice_2.setSelectedIndex(Parameters.network_source_id);
						choice_3.setSelectedItem(Parameters.network_target_id);
						choice_4.setSelectedItem(Parameters.network_oneway_id);

					}

					if (Parameters.track_header){

						choice_5.setSelectedItem(Parameters.track_columns_x_name);
						choice_6.setSelectedItem(Parameters.track_columns_y_name);
						choice_7.setSelectedItem(Parameters.track_columns_t_name);

					}
					else{


						choice_5.setSelectedIndex(Parameters.track_columns_x_id);
						choice_6.setSelectedIndex(Parameters.track_columns_y_id);
						choice_7.setSelectedIndex(Parameters.track_columns_t_id);

					}


				}

			}
		});

		addHelpText();
		setToolTips(true);

	}


	private void addHelpText(){

		textField_1.setText("Use * to refer to multiple track data files");
		textField_1.setForeground(Color.GRAY);
		Font myFont = new Font("Segoe UI", Font.ITALIC, 12);
		textField_1.setFont(myFont);


	}

	private void removeHelpText(){

		if (helpMultipleTrackPaths){

			textField_1.setText("");
			textField_1.setForeground(Color.BLACK);

			Font myFont = new Font("Tahoma", Font.PLAIN, 11);
			textField_1.setFont(myFont);

			helpMultipleTrackPaths = false;

		}

	}


	private void setToolTips(boolean activate){

		if (activate){

			textField.setToolTipText("Input network data file path here");
			btnNewButton.setToolTipText("Browse folders for network data file");
			btnS.setToolTipText("Set input network file delimiter");
			button_5.setToolTipText("Display selected network file contents");
			chckbxNewCheckBox.setToolTipText("Check this box if first line of network file contains column names");
			chckbxBuildTopology.setToolTipText("Build network graph topology from a set of edges");
			textField_2.setToolTipText("Set tolerance distance (in m) between nodes to merge");
			chckbxRemoveDegree.setToolTipText("Remove all nodes of degree 2");
			choice.setToolTipText("Edge index (string or integer) column name or number");
			choice_1.setToolTipText("Edge geometry (wkt) column name or number");
			choice_2.setToolTipText("Edge source (string or integer) column name or number");
			choice_3.setToolTipText("Edge target (string or integer) column name or number");
			choice_4.setToolTipText("Column name or number to specify if edge is \"one way\" type (boolean)");
			textField_1.setToolTipText("Input track data file path(s) here.");
			button.setToolTipText("Browse folders for track data file");
			button_2.setToolTipText("Set input track file delimiter");
			button_1.setToolTipText("Display selected track file contents");
			checkBox.setToolTipText("Check this box if first line of track file contains column names");
			textField_3.setToolTipText("Error code to mark sensor or logging failure in track data file (string or integer)");
			choice_5.setToolTipText("Track record X coordinate (floating point value) column name or number");
			choice_6.setToolTipText("Track record Y coordinate (floating point value) column name or number");
			choice_7.setToolTipText("Track record timestamp column name or number (optional)");
			choice_8.setToolTipText("Timestamp format");
			textField_4.setToolTipText("Choose output directory for map-matched files");
			button_3.setToolTipText("Browse to select output directory");
			button_4.setToolTipText("Set input network file delimiter");
			chckbxCleanDirectory.setToolTipText("Check for deleting all files in output directory before processing");
			chckbxKeepSensorError.setToolTipText("Check to keep rows with error code in output file");
			txtmmdat.setToolTipText("All output map-matched files created will end with this suffix");
			lblOutputSuffix.setToolTipText("All output map-matched files created will end with this suffix");
			chckbxPrintReportFile.setToolTipText("Print file summarizing main results of map-matching");
			chckbxOpenReportFile.setToolTipText("Automatically open report file in notepad after program termination");
			textField_12.setToolTipText("Input file path for report");
			button_8.setToolTipText("Browse folders for report file path");
			chckbxSaveParameters.setToolTipText("Save parameters in the path specified below");
			textField_6.setToolTipText("input path for saving current parameters");
			button_6.setToolTipText("Browse folders for parameters file");
			chckbxPrintDebugFiles.setToolTipText("Print additional output files (including QGIS project visualization)");
			btnReset.setToolTipText("Reset parameters to default value");
			button_11.setToolTipText("Load a parameter file");
			btnNext.setToolTipText("Go to output settings tab");
			btnPrevious.setToolTipText("Go back to input settings tab");
			button_7.setToolTipText("Go to computation settings tab");
			btnPrevious_1.setToolTipText("Go back to output settings tab");
			button_9.setToolTipText("Go back to computation settings tab");
			btnQuit.setToolTipText("Quit Map Matcher program");
			button_10.setToolTipText("Quit Map Matcher program");
			btnCompute.setToolTipText("Launch map matching process");
			textField_7.setToolTipText("Positional standard deviation (in input coordinates unit)");
			textField_8.setToolTipText("Set max. positional error (in input coordinates unit) to reduce computation time");
			textField_11.setToolTipText("Beta transition cost factor");
			textField_13.setToolTipText("Cost on track-edge angle similarity criteria");
			chckbxSpatiotemporalAutocorrelationBetween.setToolTipText("Check this box to account for position covariance between successive points");
			slider.setToolTipText("Correlation between successive points");
			textField_5.setToolTipText("Autocorrelation variogram scope (in input coordinate units)");
			chckbxPrecomputeDistancesOn.setToolTipText("Precompute network shortest paths to optimize runnnig time (adviced if the number of tracks to process is large)");
			chckbxLimitNumberOf.setToolTipText("Set a maximal number of map-matched candidate points to account for at each epoch");
			spinner.setToolTipText("Set a maximal number of map-matched candidate points to account for at each point");
			chckbxLimitSpeedBetween.setToolTipText("Set a maximal speed of vehicle between any couple of successive points");
			chckbxReorganizeLabelsOn.setToolTipText("Apply post processing to minimize the number of link labels");
			chckbxSkipUnsolvedPoints.setToolTipText("Continue process even when no solution is found for a point. Map-matched output coordinates are set equal to raw input coordinates.");
			comboBox_1.setToolTipText("Statistical distribution of position errors. ");
			chckbxSensorObservationsAre.setToolTipText("Check this box to report bias in track coordinates");
			textField_14.setToolTipText("Track coordinates bias in X direction (in input coordinate units)");
			textField_15.setToolTipText("Track coordinates bias in Y direction (in input coordinate units)");
			checkBox_2.setToolTipText("If data are in geographic coordinates (decimal degrees) project data in local metric system");
			chckbxAllowForNetwork.setToolTipText("Check this box to report geometric inaccuracies in input network");
			textField_17.setToolTipText("Input network (estimated) root mean square error");
			chckbxComputeEpochbyepochRmse.setToolTipText("Provide root mean square error of for each point in track sequence");
			rdbtnNewRadioButton.setToolTipText("Predicted rmse of sensor error inferred from observed map-matching displacment");
			rdbtnPredicted.setToolTipText("Observed rmse map-matching displacment");
			textField_9.setToolTipText("Set additional cost to minimize number of edge transition");
			chckbxComputeEpochbyepochConfidence.setToolTipText("Compute confidence of map-matching solution for each point in track sequence");
			textField_16.setToolTipText("Map-match only points whose confidence value is above this threshold (0.0 means no filtering)");
			chckbxRecordMapmatchedPoint.setToolTipText("Output position on edge (curvilinear abscissa) of map-matched points");
			comboBox.setToolTipText("Select curvilinear abscissa origine point and whether it should be normalized with respect to edge lengths");
			chckbxStoreMapmatchedPoints.setToolTipText("Create reverse index to store points on each network edge");
			rdbtnCsv.setToolTipText("Store index on network in Comma Separated Value format");
			rdbtnXml.setToolTipText("Store index on network in Extensible Markup Language format");
			chckbxPrintIndexFor.setToolTipText("Keep all edges (even edges without associated track points) in index");
			chckbxPrintPointsCoordinates.setToolTipText("Output point coordinates in index");
			chckbxActivateHelp.setToolTipText("Uncheck to hide help tooltip texts");
			btnHelp.setToolTipText("Open read me file in notepad");
			chckbxGraphicalOutput.setToolTipText("Plot complete network graph even when buffer has been precomputed");
			chckbxInterpolation.setToolTipText("Output interpolation path between map-matched points");


		}else{

			textField.setToolTipText("");
			btnNewButton.setToolTipText("");
			btnS.setToolTipText("");
			button_5.setToolTipText("");
			chckbxNewCheckBox.setToolTipText("");
			chckbxBuildTopology.setToolTipText("");
			textField_2.setToolTipText("");
			chckbxRemoveDegree.setToolTipText("");
			choice.setToolTipText("");
			choice_1.setToolTipText("");
			choice_2.setToolTipText("");
			choice_3.setToolTipText("");
			choice_4.setToolTipText("");
			textField_1.setToolTipText("");
			button.setToolTipText("");
			button_2.setToolTipText("");
			button_1.setToolTipText("");;
			checkBox.setToolTipText("");
			textField_3.setToolTipText("");
			choice_5.setToolTipText("");
			choice_6.setToolTipText("");
			choice_7.setToolTipText("");
			choice_8.setToolTipText("");
			textField_4.setToolTipText("");
			button_3.setToolTipText("");
			button_4.setToolTipText("");
			chckbxCleanDirectory.setToolTipText("");
			chckbxKeepSensorError.setToolTipText("");
			txtmmdat.setToolTipText("");
			lblOutputSuffix.setToolTipText("");
			chckbxPrintReportFile.setToolTipText("");
			chckbxOpenReportFile.setToolTipText("");
			textField_12.setToolTipText("");
			button_8.setToolTipText("");
			chckbxSaveParameters.setToolTipText("");
			textField_6.setToolTipText("");
			button_6.setToolTipText("");
			chckbxPrintDebugFiles.setToolTipText("");
			btnReset.setToolTipText("");
			button_11.setToolTipText("");
			btnNext.setToolTipText("");
			btnPrevious.setToolTipText("");
			button_7.setToolTipText("");
			btnPrevious_1.setToolTipText("");
			button_9.setToolTipText("");
			btnQuit.setToolTipText("");
			button_10.setToolTipText("");
			btnCompute.setToolTipText("");
			textField_7.setToolTipText("");
			textField_8.setToolTipText("");
			textField_11.setToolTipText("");
			textField_13.setToolTipText("");
			chckbxSpatiotemporalAutocorrelationBetween.setToolTipText("");
			slider.setToolTipText("");
			textField_5.setToolTipText("");
			chckbxPrecomputeDistancesOn.setToolTipText("");
			chckbxLimitNumberOf.setToolTipText("");
			spinner.setToolTipText("");
			chckbxLimitSpeedBetween.setToolTipText("");
			chckbxReorganizeLabelsOn.setToolTipText("");
			chckbxSkipUnsolvedPoints.setToolTipText("");
			comboBox_1.setToolTipText("");
			chckbxSensorObservationsAre.setToolTipText("");
			textField_14.setToolTipText("");
			textField_15.setToolTipText("");
			checkBox_2.setToolTipText("");
			chckbxAllowForNetwork.setToolTipText("");
			textField_17.setToolTipText("");
			chckbxComputeEpochbyepochRmse.setToolTipText("");
			rdbtnNewRadioButton.setToolTipText("");
			rdbtnPredicted.setToolTipText("");
			textField_9.setToolTipText("");
			chckbxComputeEpochbyepochConfidence.setToolTipText("");
			textField_16.setToolTipText("");
			chckbxRecordMapmatchedPoint.setToolTipText("");
			comboBox.setToolTipText("");
			chckbxStoreMapmatchedPoints.setToolTipText("");
			rdbtnCsv.setToolTipText("");
			rdbtnXml.setToolTipText("");
			chckbxPrintIndexFor.setToolTipText("");
			chckbxPrintPointsCoordinates.setToolTipText("");
			chckbxActivateHelp.setToolTipText("");
			btnHelp.setToolTipText("");
			chckbxGraphicalOutput.setText("");
			chckbxInterpolation.setToolTipText("");

		}

	}

}



