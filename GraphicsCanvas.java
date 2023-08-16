import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.image.*;
import javax.swing.plaf.DimensionUIResource;

import java.util.LinkedList;
import java.util.ArrayList;

enum Mode {
    FREEPLACE(0),
    STARTPLACE(1),
    WALLPLACE(2),
    ENDPLACE(3);

    private final int value;
    private Mode(int value) {
        this.value = value;
    }
    
    public int getValue() {
        return value;
    }
}

enum ComputationalMethod {
    ASTAR("A*"),
    BFS("Breadth First Search");

    private final String value;
    private ComputationalMethod(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ComputationalMethod getEnum(String value) {
        for (ComputationalMethod c : ComputationalMethod.values()) {
            if (value.equals(c.getValue())) {
                return c;
            }
        }
        return ComputationalMethod.ASTAR;
    }

    
}

/**
 * A Graphics Canvas, used as the main viewport in the application.
 */
public class GraphicsCanvas extends Canvas {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Decleare buffer and buffergraphics
    private BufferedImage buffer;
    private Graphics bufferGraphics; 

    // Define initial cell size
    private int cellDimension = 8; //px

    // Define size of board
    int cellCountX = 80;
    int cellCountY = 80;

    // Width and Height of board in world size
    int width = cellCountX * cellDimension;
    int height = cellCountY * cellDimension;

    // Define zoom and pan used for viewport/canvas
    private double zoom = 1;
    private double panX = 0;
    private double panY = 0;
    private double startPanX = 0;
    private double startPanY = 0;

    // Declare private field containing board information
    private Board board;

    // Define initial mode
    private Mode mode = Mode.FREEPLACE;
    private ComputationalMethod computationalMethod = ComputationalMethod.ASTAR;

    // Stream for collecting calculation results
    private LinkedList<ArrayList<ArrayList<int[]>>> computationList = new LinkedList<ArrayList<ArrayList<int[]>>>();
    //                 ^^ The arraylists holds arraylists for each of the type of cells

    // Current computationResult and shortest path
    private ArrayList<ArrayList<int[]>> currentComputation = computationList.pollFirst();
    private ArrayList<int[]> currentPath;

    private boolean showVizualization = true;
    private boolean enableDiagonals = false;
    private boolean finishedVisualizing = true;

    // Timer for drawing steps
    private Timer vizualizationTimer = new Timer(100, null);
    

    // Declare references to other UI components
    private JCheckBox showVizualizationCheckbox;
    private JCheckBox enableDiagonalsCB;
    private JSlider vizualizationSpeedSlider;
    private JComboBox<?> algorithmComboBox;

    private JLabel startPointLabel;
    private JLabel endPointLabel;
    private JLabel shortestPathLabel;
    private JLabel computationalTimeLabel;


    private JTextArea outputLog;

    /**
     * Constructor for GraphicsCanvas class. UI references to certain components are required.
     * @param showVizualizationCheckbox
     * @param enableDiagonalsCB
     * @param vizualizationSpeedSlider
     * @param algorithmComboBox
     * @param startPointLabel
     * @param endPointLabel
     * @param shortestPathLabel
     * @param computationalTimeLabel
     * @param outputLog
     */
    public GraphicsCanvas(JCheckBox showVizualizationCheckbox, JCheckBox enableDiagonalsCB, JSlider vizualizationSpeedSlider, JComboBox<?> algorithmComboBox, JLabel startPointLabel, JLabel endPointLabel, JLabel shortestPathLabel, JLabel computationalTimeLabel, JTextArea outputLog) {
        super();
        //this.createBufferStrategy(2);

        // Set background and preferred dimension
        Dimension preferredDimension = new DimensionUIResource(width, height);
        setBackground(Color.LIGHT_GRAY);
        setPreferredSize(preferredDimension);

        // Create a board to contain all user input cells
        board = new Board(cellCountX, cellCountY);

        // Call reset() to set initial zoom and pan depending on viewport size
        resetViewport(width, height);

        // Set a start and end tile
        board.setTile(Cell.START, 10, 30);
        board.setTile(Cell.END, 50, 30);

        // Update start and end labels
        startPointLabel.setText("(" + board.getStart()[0] + ", " + board.getStart()[1] + ")");
        endPointLabel.setText("(" + board.getEnd()[0] + ", " + board.getEnd()[1] + ")");

        // Add a mouselistener to listen for different mouse inputs.
        this.addMouseListener(makeMouseInputAdapter());
        this.addMouseMotionListener(makeMouseMotionListener(this));
        this.addMouseWheelListener(makeMouseWheelListener());

        // Set references to ui objects
        this.showVizualizationCheckbox = showVizualizationCheckbox;
        this.enableDiagonalsCB = enableDiagonalsCB;
        this.vizualizationSpeedSlider = vizualizationSpeedSlider;
        this.algorithmComboBox = algorithmComboBox;
        this.startPointLabel = startPointLabel;
        this.endPointLabel = endPointLabel;
        this.shortestPathLabel = shortestPathLabel;
        this.computationalTimeLabel = computationalTimeLabel;
        this.outputLog = outputLog;
    }

    /**
     * Sets the mode for this graphicscanvas
     * @param mode - Desired mode.
     */
    public void setMode(Mode mode) {
        this.mode = mode;
    }

    /**
     * Resets the board and repaints canvas with current viewport dimensions.
     */
    public void reset() {
        reset(getWidth(), getHeight());
    }

    /**
     * Resets the board and repaints canvas, using specified parameters for width and height of viewport-
     * @param viewportWidth
     * @param viewportHeight
     */
    public void reset(int viewportWidth, int viewportHeight) {
        // Clear board
        board.clearBoard();

        // Update labels
        startPointLabel.setText("NOT SET");
        endPointLabel.setText("NOT SET");
        shortestPathLabel.setText("N/A");
        computationalTimeLabel.setText("N/A");

        // Clear vizualization list and vizualization and stop timer
        currentComputation = null;
        currentPath = null;
        computationList = new LinkedList<>();
        vizualizationTimer.stop();

        // Reset the viewport
        resetViewport(viewportWidth, viewportHeight);
    }

    /**
     * Resets the pan and zoom only, using specified viewport sizes.
     * @param viewportWidth
     * @param viewportHeight
     */
    public void resetViewport(int viewportWidth, int viewportHeight) {
        // Reset zoom
        zoom = 1;

        // Calculate and set new pans such that grid/board is in center of viewport
        panX = -(viewportWidth/zoom - width) / 2;
        panY = -(viewportHeight/zoom - height) / 2;

        // Repaint canvas
        repaint();
    }

    /**
     * Forces parsing of the board, and a run of the specified algorithm.
     */
    public void run() {
        // Parse graph
        int[][][][] adj = board.getGraph(enableDiagonals);

        // Empty calculation list for collecting results
        computationList = new LinkedList<>();
        currentPath = null;

        // Reset shortest path and computational time labels
        shortestPathLabel.setText("N/A");
        computationalTimeLabel.setText("N/A");

        // If Board has start and end set, run pathfinding algorithm.
        if (board.isStartSet() && board.isEndSet()) {

            // Get current time
            long t = System.currentTimeMillis();

            // Run pathfinding algorithm on separate thread
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {

                        // Get start and end nodes
                        int[] start = board.getStart();
                        int[] end = board.getEnd();

                        // Run different algorithms depending on user selected computational method
                        if (computationalMethod == ComputationalMethod.ASTAR) {
                            // Run A*
                            int[][][] results = Algorithm.A_Star(adj, start, end, computationList, showVizualization);
                            // Compute shortest path using results
                            currentPath = Algorithm.A_Star_path(results, start, end);
                        } else if (computationalMethod == ComputationalMethod.BFS) {
                            // Run BFS
                            int[][][] results = Algorithm.BFS(adj, start, end, computationList, showVizualization);
                            // Compute shortest path using results
                            currentPath = Algorithm.BFS_path(results, start, end);   
                        }
                        // Update shortest path label
                        shortestPathLabel.setText(currentPath.size()+" blocks"  );
                        // Get total computational time
                        long t2 = System.currentTimeMillis() - t;
                        // Update label
                        computationalTimeLabel.setText(t2 + " ms");
                        // Write log
                        writeLog("Computation finished in: " + t2 + "ms. Shortest path: " + currentPath.size() + " blocks.\n");
                        // Repaint for good measure
                        repaint();
                    }
                });

            // Run thread
            thread.start();

            // Stop vizualizatiotimer
            vizualizationTimer.stop();

            if (showVizualization) {
                // Not finished visualizing
                finishedVisualizing = false;

                // Run timer for forcing update of vizualization
                vizualizationTimer = new Timer((int)(1000000/Math.pow(vizualizationSpeedSlider.getValue(), 3)), new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        
                        // Maybe overkill, idk.
                        Runnable r = new Runnable() {
                            public void run() {
                                ArrayList<ArrayList<int[]>> computationInstance = computationList.pollFirst();

                                if (computationInstance != null) {
                                    finishedVisualizing = false;
                                    currentComputation = computationInstance;
                                    repaint();
                                } else {
                                    finishedVisualizing = true;
                                    repaint();
                                }
                            }
                        };
                        Thread t = new Thread(r);
                        t.start();  
                    }
                });
                vizualizationTimer.start();
            } else {
                finishedVisualizing = true;
                currentComputation = computationList.pollLast();
            }
        } else {
            // Write error message to log
            writeLog("ERROR: START and END nodes required.\n");
        }

    }

    /**
     * Update method used for creating and updating the double buffer, as well as repainting the canvas using double-buffer.
     * This method is called through regular update() calls by the ui, as well as through paint(), to trigger proper repaint when calling repaint().
     * @param g - The graphics object used to draw to the actual canvas
     */
    public void update(Graphics g) {
        // Update grid dimensions based on possible resize
        //cellDimension = Math.min(this.getWidth()/board.getXSize(), this.getHeight()/board.getYSize());

        // Initialize buffer
        if (buffer == null) {
            buffer = new BufferedImage(this.getSize().width, this.getSize().height, BufferedImage.TYPE_INT_RGB);
            bufferGraphics = buffer.getGraphics();
        }
        

        // Clear screen in background
        bufferGraphics.setColor(getBackground());
        bufferGraphics.fillRect(0, 0, this.getSize().width, this.getSize().height);

        // Paint the necessarry content to the buffer
        paintContent(bufferGraphics);

        // Draw buffer to screen
        g.drawImage(buffer, 0, 0, this);
    }

    /**
     * Calls update which utilizes double-buffering.
     */
    public void paint(Graphics g) {
        update(g);
    }

    /**
     * Paints all content using specified Graphics object.
     * @param g - Graphics object used for painting all content.
     */
    private void paintContent(Graphics g) {
        
        // Paint the current computation
        paintComputation(g);

        // Paint the computed shortest path
        paintPath(g);
        
        // Paint the board
        paintBoard(g);

        // Paint the grid
        paintGrid(g);
    
    }


    /**
     * Draws the grid to the screen.
     * @param g - Graphics object to draw grid with.
     */
    private void paintGrid(Graphics g) {

        // Calculate lengths and clean offsets
        double[] sc = new double[]{cellDimension * board.getXSize() * zoom,cellDimension * board.getYSize() * zoom};
        double[] offsets = worldToScreen(0, 0);

        // Set grid color to black
        g.setColor(Color.GRAY);
        
        // Draw grid
        for (int x = 0; x < board.getXSize() + 1; x++) {
            double p = x * cellDimension * zoom;
            g.drawLine((int)(p + offsets[0]), (int)(0 + offsets[1]), (int)(p + offsets[0]), (int)(sc[1] + offsets[1]));
        }

        for (int y = 0; y < board.getYSize() + 1; y++) {
            double p = y * cellDimension * zoom;
            g.drawLine((int)(0 + offsets[0]), (int)(p + offsets[1]), (int)(sc[0] + offsets[0]), (int)(p + offsets[1]));
        }

    }

    /**
     * Draws the saved board contents to the screen.
     * @param g - Graphics object to draw board with.
     */
    private void paintBoard(Graphics g) {

        // Fetch board
        Cell[][] board = this.board.getBoard();

        // Iterate through each cell in board
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                
                // Fetch cell type, and calculate cell position
                Cell c = board[i][j];
                int xPos = j*cellDimension;
                int yPos = i*cellDimension;

                // Draw color depending on cell type
                switch (c) {
                    case FREE:
                        break;
                    case START:
                        drawTile(g, Color.RED, xPos, yPos, cellDimension);
                        break;
                    case WALL:
                        drawTile(g, Color.BLACK, xPos, yPos, cellDimension);
                        break;
                    case END:
                        drawTile(g, Color.BLUE, xPos, yPos, cellDimension);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    /**
     * Draws the computation instance to screen, if any is available
     * @param g - Graphics object to draw computation instance with
     */
    private void paintComputation(Graphics g) {

        if (currentComputation != null) {

            int s = currentComputation.size();
            if (s > 0) {
                ArrayList<int[]> graynodes = currentComputation.get(0);
                for (int[] n : graynodes) {
                    drawTile(g, Color.GREEN, n[0]*cellDimension, n[1]*cellDimension, cellDimension);
                }

                if (s > 1) {
                    ArrayList<int[]> blacknodes = currentComputation.get(1);
                    for (int[] n : blacknodes) {
                        drawTile(g, Color.YELLOW, n[0]*cellDimension, n[1]*cellDimension, cellDimension);
                    }
                }
            }
        }     
    }

    /**
     * Draws the last computed shortest path to the screen
     * @param g - Graphics object to draw path with
     */
    private void paintPath(Graphics g) {
        if (currentPath != null && finishedVisualizing) {
            for (int[] n : currentPath) {
                drawTile(g, Color.CYAN, n[0]*cellDimension, n[1]*cellDimension, cellDimension);
            }
        }
    }

    /**
     * Paints draws a tile with specified coordinates, size and color, using specified graphics object.
     * @param g - Graphics object to draw tile with.
     * @param c - Color to draw tile with.
     * @param xPos - desired x-position of tile
     * @param yPos - desired y-position of tile
     * @param size - desired size (dimension) of tile.
     */
    private void drawTile(Graphics g, Color c, int xPos, int yPos, int size) {
        // Apply zoom before drawing
        double[] pos = worldToScreen(xPos, yPos);
        int scale = (int)Math.ceil(size*zoom);

        // Draw tile
        g.setColor(c);
        g.fillRect((int) pos[0], (int) pos[1], scale, scale);
    }

    /**
     * Converts a set of world coordinates to screen coordinates.
     * @param x
     * @param y
     * @return
     */
    private double[] worldToScreen(int x, int y) {
        double newX = ((x - panX)*zoom);
        double newY = ((y - panY)*zoom);
        return new double[]{newX, newY};
    }

    /**
     * Converts a set of screen coordinates to world coordinates.
     * @param x
     * @param y
     * @return
     */
    private double[] screenToWorld(int x, int y) {
        double newX = (x/zoom + panX);
        double newY = (y/zoom + panY);
        return new double[]{newX, newY};
    }

    /**
     * Writes specified text to outputLog. Automatically moves the caret position down to bottom.
     * @param text - text to write
     */
    private void writeLog(String text) {
        outputLog.append(text);
        outputLog.setCaretPosition(outputLog.getDocument().getLength());
    }

    /**
     * Places tile at specified x and y coordinates to board, using currently active mode.
     * If the placed tile is a start or end tile, updates the labels.
     * @param xPos
     * @param yPos
     */
    private void placeTile(int xPos, int yPos) {
        // If tile that is being placed on top of (effectively erased) is a start or end tile, update the labels
        if (board.getTile(xPos, yPos).equals(Cell.START)) {
            startPointLabel.setText("NOT SET");
        }

        if (board.getTile(xPos, yPos).equals(Cell.END)) {
            endPointLabel.setText("NOT SET");
        }

        // Set the tile on board using transformed coordinates
        board.setTile(Cell.getEnum(mode.getValue()), xPos, yPos);

        // If tile that was just set, is a start tile, update label
        if (board.getTile(xPos, yPos).equals(Cell.START)) {
            startPointLabel.setText("(" + xPos + ", " + yPos + ")");
        }

        // If tile that was just set, is an end tile, update label
        if (board.getTile(xPos, yPos).equals(Cell.END)) {
            endPointLabel.setText("(" + xPos + ", " + yPos + ")");
        }
    }

    private MouseInputAdapter makeMouseInputAdapter() {
        return new MouseInputAdapter() {
            public void mousePressed(MouseEvent e) {

                if (e.getButton() == 1) { // if left-click
                    // Transform pressed coordinates into proper tile coordinates in board
                    double[] worldPos = screenToWorld(e.getX(), e.getY());
                    int xTile = (int) (worldPos[0] / cellDimension);
                    int yTile = (int) (worldPos[1] / cellDimension);

                    if ((xTile >= 0 && xTile < cellCountX) && (yTile >= 0 && yTile < cellCountY)) {
                        // Place tile
                        placeTile(xTile, yTile);

                        // Repaint the canvas
                        repaint();
                    }                    
                } else if (e.getButton() == 3) { // if right-click
                    // Update startPans
                    startPanX = e.getX();
                    startPanY = e.getY();
                } 
            }
        };
    }

    private MouseWheelListener makeMouseWheelListener() {
        return new MouseWheelListener() {
            public void mouseWheelMoved(MouseWheelEvent e) {
                // Capture mouse position in world space before zoom
                double[] beforeZoomMPos = screenToWorld(e.getX(), e.getY());

                // Calculate new zoom
                double scroll = e.getWheelRotation();
                zoom = zoom * (1 - 2*scroll/100.0);

                // Capture mouse position in world space after zoom
                double[] afterZoomMPos = screenToWorld(e.getX(), e.getY());

                // Calculate new pan values
                double nPanX = panX + (beforeZoomMPos[0] - afterZoomMPos[0]);
                double nPanY = panY + (beforeZoomMPos[1] - afterZoomMPos[1]);

                // Update pan
                panX = nPanX;
                panY = nPanY;

                // Repaint canvas
                repaint();
            }
        };
    }

    private MouseMotionListener makeMouseMotionListener(Component parent) {
        return new MouseMotionListener() {
            public void mouseMoved(MouseEvent e) {}

            public void mouseDragged(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) { // If panning
                    // Calculate new pan values
                    double nPanX = panX - (e.getX() - startPanX)/zoom;
                    double nPanY = panY - (e.getY() - startPanY)/zoom;

                    // Check that new pan values allow board visibility
                    int boardSizeX = cellCountX * cellDimension;
                    int boardSizeY = cellCountY * cellDimension;
                    double screenSizeX = -parent.getWidth()/zoom;
                    double screenSizeY = -parent.getHeight()/zoom;

                    if (nPanX > boardSizeX) nPanX = boardSizeX;
                    if (nPanY > boardSizeY) nPanY = boardSizeY;
                    if (nPanX < screenSizeX) nPanX = screenSizeX;
                    if (nPanY < screenSizeY) nPanY = screenSizeY;

                    // Update pan
                    panX = nPanX;
                    panY = nPanY;
                    startPanX = e.getX();
                    startPanY = e.getY();

                    // Repaint canvas
                    repaint();
                } else if (SwingUtilities.isLeftMouseButton(e)) { // If drawing
                    // Transform pressed coordinates into proper tile coordinates in board
                    double[] worldPos = screenToWorld(e.getX(), e.getY());
                    int xTile = (int) (worldPos[0] / cellDimension);
                    int yTile = (int) (worldPos[1] / cellDimension);

                    if ((xTile >= 0 && xTile < cellCountX) && (yTile >= 0 && yTile < cellCountY)) {
                        // Place Tile
                        placeTile(xTile, yTile);

                        // Repaint the canvas
                        repaint();
                    }  
                }
            }
        };
    }

    public void resized() {
        buffer = new BufferedImage(this.getSize().width, this.getSize().height, BufferedImage.TYPE_INT_RGB);
        bufferGraphics = buffer.getGraphics();
    }

    public void updateTimer() {
        vizualizationTimer.setDelay((int)(1000000/Math.pow(vizualizationSpeedSlider.getValue(), 3)));
    }

    public void updateComputationalMethod() {
        computationalMethod = ComputationalMethod.getEnum(algorithmComboBox.getSelectedItem().toString());
    }

    public void setShowVizualization() {
        this.showVizualization = showVizualizationCheckbox.isSelected();
    }

    public void setEnableDiagonals() {
        this.enableDiagonals = enableDiagonalsCB.isSelected();
    }

}