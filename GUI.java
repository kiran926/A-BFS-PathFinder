import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.InsetsUIResource;
import javax.swing.JColorChooser;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
/**
 * GUI Class for managing the Graphical User Interface of the application.
 */
public class GUI {

    // Main frame of the application (main window)
    private JFrame frame;

    // Component references that are necessary for communication between components
    private GraphicsCanvas graphicsCanvas;

    private JCheckBox showVizualizationCheckbox;
    private JCheckBox enableDiagonalsCB;
    private JSlider vizualizationSpeedSlider;
    private JComboBox<String> algorithmComboBox;

    private JLabel startPointLabel;
    private JLabel endPointLabel;
    private JLabel shortestPathLabel;
    private JLabel computationalTimeLabel;
    //private JLabel label1;

    private JTextArea outputLog;

	private JLabel label1;

    /**
     * Constructor for GUI class
     */
    public GUI() {
        makeFrame();
    }

    /**
     * Initializes the frame for this class instance
     */
    private void makeFrame() {
        // Create frame
        frame = new JFrame("Pathfinding Visualizer");
       
     
        
       
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
       // frame.setBounds(100, 200, 350, 300);
       //frame.setSize(400,400);
        // Allow application to terminate peacefully when being exited
        
        // Create content pane
        Container contentPane = frame.getContentPane();
        contentPane.setLayout(new BorderLayout(0, 0));

        // "Fill out" the Borderlayout with widgets and stuff
        Component westLayout = makeWestLayout();
        frame.add(westLayout, BorderLayout.WEST);

        Component southLayout = makeSouthLayout();
        frame.add(southLayout, BorderLayout.SOUTH);

        Component centerLayout = makeCenterLayout();
        frame.add(centerLayout, BorderLayout.CENTER);

        Component eastLayout = makeEastLayout();
        frame.add(eastLayout, BorderLayout.EAST);
        
        // Make a menubar for the frame
        JMenuBar menuBar = makeMenuBar();
        frame.setJMenuBar(menuBar);

        // Pack frame and set visible
        frame.pack();
        frame.setVisible(true);
        frame.setMinimumSize(frame.getPreferredSize());
    }

    /**
     * Initializes a menu bar for the specified frame
     * @return - the created menubar
     */
    private JMenuBar makeMenuBar() {
        // Create a menubar and assign it to frame
        JMenuBar menubar = new JMenuBar();
        //frame.setJMenuBar(menubar);

        JMenu file = new JMenu("Path Finding Visualizer");
        file.setFont(new Font("Cambria", Font.BOLD, 18));
        menubar.add(file);
        
        
        JMenuItem info=new JMenuItem("Welcome");
        info.setFont(new Font("Cambria", Font.PLAIN, 18));
        info.addActionListener(e -> JOptionPane.showMessageDialog(frame, "At its core, a pathfinding algorithm seeks to find  the shortest path \n between two points. This application visualizes various \n pathfinding algorithms in action, and more!", "Welcome", JOptionPane.INFORMATION_MESSAGE));

        file.add(info);
        
        // Create File Menu
        JMenu fileMenu = new JMenu("File");
       
        fileMenu.setFont(new Font("Cambria", Font.BOLD, 18));
        menubar.add(fileMenu);

        // Create menu items for File Menu
        //JMenuItem openItem = new JMenuItem("Open");
        //fileMenu.add(openItem);
        JMenuItem quitItem = new JMenuItem("Quit");
       
        quitItem.setFont(new Font("Cambria", Font.PLAIN, 18));
    
        quitItem.addActionListener(e -> quit());
        fileMenu.add(quitItem);

        // Create Help Menu
        JMenu helpMenu = new JMenu("Help");
       
        helpMenu.setFont(new Font("Cambria", Font.BOLD, 18));
        menubar.add(helpMenu);

        // Create menu items for Help Menu
        JMenuItem showHelpItem = new JMenuItem("About");
        
        showHelpItem.setFont(new Font("Cambria", Font.PLAIN, 18));
        
        showHelpItem.addActionListener(e -> JOptionPane.showMessageDialog(frame, "Software written by Kiran", "Help", JOptionPane.INFORMATION_MESSAGE));
        helpMenu.add(showHelpItem);

        return menubar;
    }

    /**
     * Creates the west side of the content in the Borderlayout of the specified content pane
     * @return - The created layout packed inside a Component
     */
    private Component makeWestLayout() {
        // Create a panel to hold the components
        JPanel westPanel = new JPanel();
        westPanel.setLayout(new GridBagLayout());
        
        Border border = new LineBorder(Color.WHITE, 6, true);
        westPanel.setBorder(border);
        westPanel.setBackground(new java.awt.Color(77, 77, 77));
        
        
       /* JLabel label1=new JLabel("MENU");
        //label1.setAlignmentY(java.awt.Label.CENTER);
         label1.setBounds(20, 20, 125, 20);
         label1.setFont(new Font("Serif", Font.BOLD, 24));
         this.label1 = label1;
         frame.add(label1);*/

        // Create GridBagContraints
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new InsetsUIResource(10, 10, 10, 10);

        // Make settings panel
        Component settingsPanel = makeSettingsSubPanel();
        c.gridy = 0;
        //c.anchor = GridBagConstraints.NORTH;
        c.fill = GridBagConstraints.HORIZONTAL;
        westPanel.add(settingsPanel, c);

        // Make configuration panel 
        Component configurationPanel = makeConfigurationSubPanel();
        c.gridy = 1;
        //c.anchor = GridBagConstraints.NORTH;
        c.fill = GridBagConstraints.HORIZONTAL;
        westPanel.add(configurationPanel, c);

        return westPanel;
    }

    /**
     * Creates a Settings panel containing controls for all settings, such as vizualization speed, and vizualization type
     * @return - Created settingspanel
     */
    private Component makeSettingsSubPanel() {
        // Create a "Settings" panel to hold some components
        JPanel settingsPanel = new JPanel();
        settingsPanel.setLayout(new GridBagLayout());
        settingsPanel.setFont(new  Font("Arial", 0, 50));
       // settingsPanel.setBorder(BorderFactory.createTitledBorder("SETTINGS"));
        settingsPanel.setBackground(new java.awt.Color(206, 206, 192));
        Border border = new LineBorder(Color.ORANGE, 4, true);
        settingsPanel.setBorder(border);
        //Color lightblue = new Color(255,204,204);
        //settingsPanel.setBackground(lightblue);
        

        // Create GridBagConstraits
       // label1.setAlignmentX(java.awt.Label.CENTER);
       // label1.setFont(new java.awt.Font("Microsoft YaHei UI", 1, 18)); // NOI18N
        
        
        
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new InsetsUIResource(15, 10, 5, 5);
        
        // Create "Show vizualization:" label
        
        JLabel label1 = new JLabel("SETTINGS ", SwingConstants.CENTER);
        label1.setFont(new Font("Cambria", Font.BOLD, 22));
        label1.setForeground(new java.awt.Color(204, 0, 102));
       // label1.setOpaque(true);
       // label1.setBorder(BorderFactory.createLineBorder(Color.red, 1, true));
       /* c.gridx = 0;
        c.gridy = 0;*/
        c.anchor = GridBagConstraints.SOUTH;
        settingsPanel.add(label1, c);
        
        JLabel showVizualizationLabel = new JLabel("Show Vizualization:");
        showVizualizationLabel.setFont(new Font("Cambria", Font.PLAIN, 16));
        c.gridx = 0;
        c.gridy = 1;
        c.anchor = GridBagConstraints.EAST;
        settingsPanel.add(showVizualizationLabel, c);
        
        // Create checkbox
        JCheckBox showVizualizationCB = new JCheckBox();
        showVizualizationCB.setOpaque(true);
        showVizualizationCB.setBorder(BorderFactory.createLineBorder(Color.red, 1, true));
        showVizualizationCB.setSelected(true);
        showVizualizationCB.addActionListener(e -> graphicsCanvas.setShowVizualization());
        c.gridx = 1;
        c.gridy = 1;
        c.anchor = GridBagConstraints.WEST;
        settingsPanel.add(showVizualizationCB, c);
        this.showVizualizationCheckbox = showVizualizationCB;

        // Create "Enable diagonals: " label
        JLabel enableDiagonalsLabel = new JLabel("Enable Diagonals:");
        enableDiagonalsLabel.setFont(new Font("Cambria", Font.PLAIN, 16)); 
        c.gridx = 0;
        c.gridy = 2;
        c.anchor =  GridBagConstraints.EAST;
        settingsPanel.add(enableDiagonalsLabel, c);

        // Create checkbox
        JCheckBox enableDiagonalsCB = new JCheckBox();
        enableDiagonalsCB.setOpaque(true);
        enableDiagonalsCB.setBorder(BorderFactory.createLineBorder(Color.red, 1, true));
        enableDiagonalsCB.setSelected(false);
        enableDiagonalsCB.addActionListener(e -> graphicsCanvas.setEnableDiagonals());
        c.gridx = 1;
        c.gridy = 2;
        c.anchor = GridBagConstraints.WEST;
        settingsPanel.add(enableDiagonalsCB, c);
        this.enableDiagonalsCB = enableDiagonalsCB;

        // Create "Vizualization speed:" label
        JLabel vizualizationSpeedLabel = new JLabel("Vizualization Speed:");
        vizualizationSpeedLabel.setFont(new Font("Cambria", Font.PLAIN, 16)); 
        c.gridx = 0;
        c.gridy = 3;
        c.anchor = GridBagConstraints.EAST;
        settingsPanel.add(vizualizationSpeedLabel, c);

        // Create slider
        JSlider vizualizationSpeedSlider = new JSlider(10, 100, 50);
        vizualizationSpeedSlider.setOpaque(true);
        vizualizationSpeedSlider.setBorder(BorderFactory.createLineBorder(Color.white, 1, true));
        vizualizationSpeedSlider.addChangeListener(e -> graphicsCanvas.updateTimer());
        c.gridx = 1;
        c.gridy = 3;
        c.anchor = GridBagConstraints.WEST;
        settingsPanel.add(vizualizationSpeedSlider, c);
        this.vizualizationSpeedSlider = vizualizationSpeedSlider;

        // Create "Pathfinding algorithm" label
        JLabel pathfindingAlgorithmLabel = new JLabel("Algorithm:");
        pathfindingAlgorithmLabel.setFont(new Font("Cambria", Font.PLAIN, 16)); 
        c.gridx = 0;
        c.gridy = 4;
        c.anchor = GridBagConstraints.EAST;
        settingsPanel.add(pathfindingAlgorithmLabel, c);
        
        // Add combobox for selection of algorithm
        JComboBox<String> algorithmComboBox = new JComboBox<>(new String[] {"A*", "Breadth First Search"});
        algorithmComboBox.setFont(new Font("Cambria", Font.PLAIN, 16));
        algorithmComboBox.addActionListener(e -> graphicsCanvas.updateComputationalMethod());
        c.gridx = 1;
        c.gridy = 4;
        c.anchor = GridBagConstraints.WEST;
        settingsPanel.add(algorithmComboBox, c);
        this.algorithmComboBox = algorithmComboBox;

        return settingsPanel;
    }

    /**
     * Creates a Configuration sub panel containing information about current/last simulation, as well as RUN and CLEAR buttons
     * @return - created configurationpanel
     */
    private Component makeConfigurationSubPanel() {
        // Create configuration panel
        JPanel configurationPanel = new JPanel();
        configurationPanel.setLayout(new GridBagLayout());
       // configurationPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "CONFIGURATION"));
        configurationPanel.setBackground(new java.awt.Color(206, 206, 192));
        Border border = new LineBorder(Color.ORANGE, 4, true);
        configurationPanel.setBorder(border);
        configurationPanel.setFont(new Font("Cambria", Font.PLAIN, 20));

        // Create GridBagConstraints for layout
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new InsetsUIResource(15, 5, 5, 5);
        
        JLabel label2 = new JLabel("CONFIGURATION");
        label2.setFont(new Font("Cambria", Font.BOLD, 22));
        label2.setForeground(new java.awt.Color(204, 0, 102));
       // label1.setOpaque(true);
       // label1.setBorder(BorderFactory.createLineBorder(Color.red, 1, true));
       /* c.gridx = 0;
        c.gridy = 0;*/
       // c.anchor = GridBagConstraints.SOUTH;
        configurationPanel.add(label2, c);


        // Create Labels for showing info on startpoint, endpoint, shortest path length and computation time
        JLabel startLabel = new JLabel("Start Point Node:");
        startLabel.setFont(new Font("Cambria", Font.PLAIN, 16));

        JLabel startValLabel = new JLabel("NOT SET");
        //JLanel pane = new JPanel();
        startValLabel.setForeground(new java.awt.Color(255,0,0));
        startValLabel.setOpaque(true);
        startValLabel.setBorder(BorderFactory.createLineBorder(Color.red, 1, true));
        startValLabel.setFont(new Font("Cambria", Font.PLAIN, 16));
        this.startPointLabel = startValLabel;
        
        JLabel endLabel = new JLabel("End Point Node:");
        endLabel.setFont(new Font("Cambria", Font.PLAIN, 16));
        
        JLabel endValLabel = new JLabel("NOT SET");
        endValLabel.setForeground(new java.awt.Color(0,0,255));
        endValLabel.setOpaque(true);
        endValLabel.setBorder(BorderFactory.createLineBorder(Color.blue, 1, true));
        endValLabel.setFont(new Font("Cambria", Font.PLAIN, 16));
        this.endPointLabel = endValLabel;

        JLabel shortestPathLabel = new JLabel("Shortest Path:");
        shortestPathLabel.setFont(new Font("Cambria", Font.PLAIN, 16));

        JLabel shortestPathValLabel = new JLabel("N/A");
        shortestPathValLabel.setForeground(new java.awt.Color(0, 153, 153));
        shortestPathValLabel.setOpaque(true);
        shortestPathValLabel.setBorder(BorderFactory.createLineBorder(Color.blue, 1, true));
        shortestPathValLabel.setFont(new Font("Cambria", Font.PLAIN, 16));
        this.shortestPathLabel = shortestPathValLabel;

        JLabel computationTimeLabel = new JLabel("Time:");
        computationTimeLabel.setFont(new Font("Cambria", Font.PLAIN, 17));

        JLabel computationTimeValLabel = new JLabel("N/A");
        computationTimeValLabel.setForeground(new java.awt.Color(0, 153, 0));
        computationTimeValLabel.setOpaque(true);
        computationTimeValLabel.setBorder(BorderFactory.createLineBorder(Color.green, 1, true));
  
        computationTimeValLabel.setFont(new Font("Cambria", Font.PLAIN, 16));
        this.computationalTimeLabel = computationTimeValLabel;

        // Add left-side labels to layout
        c.gridx = 0;
        c.anchor = GridBagConstraints.EAST;

        c.gridy = 1;
        configurationPanel.add(startLabel, c);

        c.gridy = 2;
        configurationPanel.add(endLabel, c);

        c.gridy = 3;
        configurationPanel.add(shortestPathLabel, c);

        c.gridy = 4;
        configurationPanel.add(computationTimeLabel, c);

        // Add right-side labels to layout
        c.gridx = 1;
        c.anchor = GridBagConstraints.WEST;

        c.gridy = 1;
        configurationPanel.add(startValLabel, c);

        c.gridy = 2;
        configurationPanel.add(endValLabel, c);

        c.gridy = 3;
        configurationPanel.add(shortestPathValLabel, c);

        c.gridy = 4;
        configurationPanel.add(computationTimeValLabel, c);

        // Now create the RUN and CLEAR buttons
        c.gridy = 5;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 0.5;

        JButton runButton = new JButton("RUN");
        runButton.setBackground(new java.awt.Color(153, 255, 102));
        runButton.setOpaque(true);
        runButton.setBorder(BorderFactory.createLineBorder(Color.black, 2, true));
        runButton.setFont(new Font("Cambria", Font.PLAIN, 16));
        
        runButton.addActionListener(e -> graphicsCanvas.run());
        c.gridx = 0;
        c.anchor = GridBagConstraints.EAST;
        configurationPanel.add(runButton, c);
        
        JButton clearButton = new JButton("CLEAR");
        clearButton.setBackground(new java.awt.Color(153, 255, 102));
        clearButton.setOpaque(true);
        clearButton.setBorder(BorderFactory.createLineBorder(Color.black, 2, true));
        clearButton.setFont(new Font("Cambria", Font.PLAIN, 16));
        clearButton.addActionListener(e -> graphicsCanvas.reset());
        c.gridx = 1;
        c.anchor = GridBagConstraints.WEST;
        configurationPanel.add(clearButton, c);

        return configurationPanel;
    }

    /**
     * Creates the center part of the content in the Borderlayout of the specified content pane
     * @return - The created layout packed indside a Component
     */
    private Component makeCenterLayout() {
        // Create a sample canvas
        GraphicsCanvas canvas = new GraphicsCanvas(showVizualizationCheckbox, enableDiagonalsCB, vizualizationSpeedSlider, algorithmComboBox, startPointLabel, endPointLabel, shortestPathLabel, computationalTimeLabel, outputLog);
        canvas.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                canvas.resized();
            }
        });
        this.graphicsCanvas = canvas;

        return canvas;
    }

    /**
     * Creates the south part of the content in the Borderlayout of the specified content pane
     * @return - the created layout packed inside a Component
     */
    private Component makeSouthLayout() {
        
        // Create text area for output log
        JTextArea outputLog = new JTextArea("Welcome to Pathfinding Vizualizer!\n");
        outputLog.setFont(new Font("Cambria", Font.PLAIN, 16));
        outputLog.setEditable(false);
        outputLog.setLineWrap(true);
        /*
        DefaultCaret caret = (DefaultCaret)outputLog.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        */
        this.outputLog = outputLog;

        // Create a JScrollPane to house outputLog
        JScrollPane logPanel = new JScrollPane(outputLog);

        return logPanel;
    }

    private Component makeEastLayout() {

        // Create a panel for containt components
        JPanel eastPanel = new JPanel();
        eastPanel.setLayout(new BoxLayout(eastPanel, BoxLayout.X_AXIS));

        // Create a vertical separator
        JSeparator separator = new JSeparator(JSeparator.VERTICAL);
        eastPanel.add(separator);

        // Create a toolbar
        JToolBar toolbar = new JToolBar(JToolBar.VERTICAL);
        eastPanel.add(toolbar);

        // Create four buttons for toolbar
        
       
        
        

        JButton redButton = new JButton("Start Node  ");
        redButton.setBackground(new java.awt.Color(255, 0, 0));
        redButton.setFont(new Font("Cambria", Font.PLAIN, 20));
        redButton.addActionListener(e -> graphicsCanvas.setMode(Mode.STARTPLACE));
        redButton.setForeground(new java.awt.Color(255,255,255));
        redButton.setToolTipText("Start Node");
        
        toolbar.add(redButton);

        JButton blueButton = new JButton("End Node   ");
       blueButton.setBackground(new java.awt.Color(0, 0, 255));
        blueButton.setFont(new Font("Cambria", Font.PLAIN, 20));
        blueButton.addActionListener(e -> graphicsCanvas.setMode(Mode.ENDPLACE));
        blueButton.setForeground(new java.awt.Color(255,255,255));
        blueButton.setToolTipText("End Node");
        toolbar.add(blueButton);

        JButton blackButton = new JButton("Wall Node  ");
        blackButton.setBackground(new java.awt.Color(0, 0, 0));
        blackButton.setFont(new Font("Cambria", Font.PLAIN, 20));
        blackButton.addActionListener(e -> graphicsCanvas.setMode(Mode.WALLPLACE));
        blackButton.setForeground(new java.awt.Color(255,255,255));
        blackButton.setToolTipText("Wall Node");
        toolbar.add(blackButton);
        
       JButton whiteButton = new JButton("Erase Node");
       whiteButton.setBackground(new java.awt.Color(128, 128, 128));
       whiteButton.setFont(new Font("Cambria", Font.PLAIN, 20));
        whiteButton.setForeground(new java.awt.Color(255,255,255));
        whiteButton.addActionListener(e -> graphicsCanvas.setMode(Mode.FREEPLACE));
        whiteButton.setToolTipText("Erase Node");
        toolbar.add(whiteButton);

        // Return created eastPanel
        return eastPanel;
    }

    private ImageIcon createImageIcon(Color color, int width, int height) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = img.createGraphics();
        g.setColor(color);
        g.fillRect(0, 0, width, height);
        return new ImageIcon(img);
    }

    /**
     * Calmly terminates the application 
     */
    private void quit() {
        System.exit(0);
    }
    
}