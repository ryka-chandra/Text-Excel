import java.awt.*;
import java.awt.event.*;
import java.io.File;

import javax.swing.*;

/**
 * The UI for the Grid. This will leverage processCommand() to interact with the grid.
 * <p>TODO: <ul>
 * <li>Add the FileOpen dialog to load/save files</li>
 * <li>make inline cell editing better. Still doesn't work great when modifying text cells</li>
 * <li>enable better sizing of the cell width.</li>
 * <li>enable the number of rows/cols to grow without clearing the grid</li>
 * <li>enable the number of rows/cols to shink if those cells are empty or unreferenced</li>
 * </ul>
 */
public class MainUI extends JFrame {

	// This is for some warning regarding a serializable class
	private static final long serialVersionUID = 1L;

	// number of TextFields in GUI = grid size + 1
	private int rows = 23;
	private int cols = 13;
	private JButton goButton = new JButton("go");
	private JPanel contentPane;
	private JPanel gridPanel;
	private ExcelBase engine = null;
	// 50 is the width of the text field
	private JTextField outputWindow = new JTextField(50);
	private JTextField[][] textGrid = null;
	private JTextField input;

	public MainUI() {
		super("Text Excel UI");

		engine = ExcelBase.engine;
		
		// add 1 to make room for the labels
		rows = 1 + Integer.parseInt(engine.processCommand("rows"));
		cols = 1 + Integer.parseInt(engine.processCommand("cols"));

		textGrid = new JTextField[rows][cols];
		
		// this is important so that the process will close when the user
		// clicks on the red X.
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		createPanel();
		
		attachEvents();
		// add go button event
		goButton.addActionListener((e) -> onGoButtonClicked(e));
		getRootPane().setDefaultButton(goButton);
	}

	/**
	 * When the display needs to be updated, we go through
	 * each and every cell in the grid, get the new display,
	 * and update the JTextCell to the proper display text.
	 */
	private void updateUI() {
		// the textGrid includes labels. So, when we update
		// the UI, we should only need to update the rows & cols > 1.
		// The textGrid[x][0] are number labels for the rows.
		// The textGrid[0][x] are letter labels for the columns
		for (int row = 1; row < rows; row++) {
			for (int col = 1; col < cols; col++) {
				String s = engine.processCommand("display " + getCellName(row, col));
				textGrid[row][col].setText(s);
			}
		}
	}

	private String getCellName(int row, int col) {
		char letter = (char) ('a' + (col-1));
		return "" + letter + row;
	}
	
	private void updateCellDisplay(int row, int col) {
		String expr = textGrid[row][col].getText().trim();
		String cmd = getCellName(row, col) + " ";
		if (!expr.startsWith("=")) {
			cmd += "= ";
		}
		cmd += expr;
		String result = engine.processCommand(cmd);

		// TODO: truncate the display better
		if (result != null && result.length() > 0) {
			outputWindow.setText(result);
		}

		updateUI();
	}

	private void attachEvents() {
		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < cols; col++) {
				JTextField cell = textGrid[row][col];
				if (!cell.isEditable()) {
					continue;
				}

				// Make these final to work with the event listeners
				final int r = row;
				final int c = col;

				cell.addFocusListener(new FocusListener() {
					public void focusGained(FocusEvent e) {
						// stop the Go button from stealing enter presses
						getRootPane().setDefaultButton(null); 
					}

					public void focusLost(FocusEvent e) {
						// give back to the Go button its Default Buttons status
						getRootPane().setDefaultButton(goButton);
						
						// if we lost focus then we need to update display
						updateCellDisplay(r, c);
					}
				});

				// TODO: for this "enter key" action, put focus
				// to the next cell
				cell.addActionListener((e) -> updateCellDisplay(r ,c));
			}
		}
	}

	private void updateGridSize() {
		// don't know if this creates a memory leak due to the cells 
		// that are children to gridPanel not being removed
		contentPane.remove(gridPanel); 

		// add 1 to the rows and cols to make room for
		// the row and column labels we need
		rows = 1 + Integer.parseInt(engine.processCommand("rows"));
		cols = 1 + Integer.parseInt(engine.processCommand("cols"));

		textGrid = null;
		gridPanel = null;

		textGrid = new JTextField[rows][cols];
		gridPanel = new JPanel(new GridLayout(rows, cols));

		createAllTextFields();
		contentPane.add(gridPanel);
		attachEvents();

		contentPane.revalidate();
	}

	private void onGoButtonClicked(ActionEvent e) {

		if ("quit".equalsIgnoreCase(input.getText())) {
			System.exit(0);
		}

		String result = null;
		boolean updateGrid = false;
		
		// TODO: handle save file and refactor into a method
		if (input.getText().equalsIgnoreCase("load")) {
         // Creates a new JFileChooser object
         JFileChooser chooser = new JFileChooser();
         // Opens the dialog to select a file, stores the id as a int
         int idResult = chooser.showOpenDialog(this);

         // Verify's the id, if approved than goes into if statement
         if (idResult == JFileChooser.APPROVE_OPTION) {
            // Selects the file chosen
            File f = chooser.getSelectedFile();
            String name = chooser.getName(f);
            result = engine.processCommand("load " + name);
            updateGrid = true;
         }
		} else {
			result = engine.processCommand(input.getText());
			String txt = input.getText();
			updateGrid = ((txt.startsWith("rows") || txt.startsWith("cols")) && txt.matches("^(\\w+) *= *(\\d+)"));
		}
		
		if (result != null && result.length() > 0) {
			outputWindow.setText(result);
		}
		
		if (updateGrid) {
			updateGridSize();
		}

		// clear the input window to prepare for the next command
		input.setText("");
		updateUI();
	}		

	private void createGridLabels() {
		for (int i = 1; i < rows; i++) {
			JTextField cell = textGrid[i][0];
			cell.setText(Integer.toString(i));
			cell.setEditable(false);
		}

		for (int i = 1; i < cols; i++) {
			JTextField cell = textGrid[0][i];
			// cell.setMaximumSize(new Dimension(cell.getMaximumSize().width,
			// cell.getMinimumSize().height * 2));

			cell.setText((char) ('A' + i - 1) + "");
			cell.setEditable(false);
		}
	}

	private void createPanel() {
		contentPane = new JPanel();
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
		contentPane.setOpaque(true); // content panes must be opaque
		setContentPane(contentPane);

		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.X_AXIS));
		// Review: Is this 50 necessary and correct?
		input = new JTextField(50);
		// Review: is this dimension the best way?
		inputPanel.setMaximumSize(new Dimension(1920, inputPanel.getMinimumSize().height));

		inputPanel.add(input);
		inputPanel.add(goButton);
		outputWindow.setEditable(false);
		outputWindow.setMaximumSize(new Dimension(1920, outputWindow.getMinimumSize().height));

		gridPanel = new JPanel(new GridLayout(rows, cols));

		createAllTextFields();

		contentPane.add(inputPanel);
		contentPane.add(outputWindow);
		contentPane.add(gridPanel);
	}

	private void createAllTextFields() {
		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < cols; col++) {
				JTextField cell = new JTextField();
				// the default size of the text fields is 20,20
				// so that the display will have an acceptable size
				// TODO: Review cell.setSize(). appears to have no effect.
				cell.setSize(new Dimension(20, 20));
				this.textGrid[row][col] = cell;
				
				// would be cool to set this to true so that the
				// user can edit the cell directly
				cell.setEditable(true);
				gridPanel.add(cell);
			}
		}
		// we cannot edit the first cell 
		// we setEditable(false) for the labels in createGridLabels()
		this.textGrid[0][0].setEditable(false);
		createGridLabels();
	}

	/**
	 * Create the GUI and show it. For thread safety, this method should be invoked
	 * from the event-dispatching thread.
	 */
	private static void createAndShowGUI() {

		// Create and set up the content pane.
		MainUI newContentPane = new MainUI();

		// without setting this, our program won't exit when the frame is closed.
		// This appears to only be important when we invokeLater() on this
		// EventDispatchThread.
		newContentPane.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		newContentPane.addWindowListener(new WindowAdapter(){
         public void windowClosing(WindowEvent e){
             System.out.println("Goodbye from the UI");
         }
      });
		
		// Display the window
		newContentPane.pack();
		newContentPane.setVisible(true);

		newContentPane.updateUI();
	}

	public static void showUI() {

		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}
}
