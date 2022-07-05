import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

public class GUI {

    JSplitPane mainMenuHorizontalSplitPane;
    private final JTextField widthTextField;
    private final JTextField heightTextField;


    private JFrame simulationWindow; //GUI window
    private SimulationPanel leftPanel; //Left GUI panel displaying main simulation board
    private JPanel rightPanel; //Right panel containing GUI buttons
    private JPanel bottomPanel; //Bottom panel meant for displaying information about people
    private JSplitPane horizontalSplitPane; //Splits GUI into two horizontal sides
    private JSplitPane verticalSplitPane; //Splits GUI into two vertical sides
    private final SimulationEngine simulationEngine;

    Dimension simulationBoardScale;
    BufferedImage img;
    Graphics imgGx;
    Dimension simulationDimensions;

    JLabel startingInfectionAmountLabel;
    JSlider startingInfectionsSlider;

    JLabel startingComplyingAmountLabel;
    JSlider startingComplyingSlider;

    JLabel infectiousnessLabel;
    JSlider infectiousnessSlider;

    JLabel mortalityLabel;
    JSlider mortalitySlider;

    JLabel healthyColourLabel;
    JLabel infectedColourLabel;
    JLabel deceasedColourLabel;
    JLabel recoveredColourLabel;
    JLabel infectedLabel;
    JLabel recoveredLabel;
    JLabel deceasedLabel;
    JLabel infectionIncreaseLabel;
    JLabel complyingLabel;

    Font textFieldFont;
    Font mainLabelFont;
    Font colourPickFont;

    File imgFile;

    public GUI(SimulationEngine engine){
        simulationEngine = engine;
        JFrame startWindow = new JFrame();
        JPanel mainMenuTopPanel = new JPanel();
        JButton startButton = new JButton();

        JPanel mainMenuBottomPanel = new JPanel();
        mainMenuHorizontalSplitPane = new JSplitPane();

        widthTextField = new JTextField();
        heightTextField = new JTextField();

        AbstractDocument widthDoc = (AbstractDocument) widthTextField.getDocument();
        AbstractDocument heightDoc = (AbstractDocument) heightTextField.getDocument();

        textFieldFont = new Font("SansSerif", Font.PLAIN, 16);
        mainLabelFont = new Font("SansSerif", Font.BOLD, 40);
        colourPickFont = new Font("SansSerif", Font.BOLD, 20);

        widthTextField.setPreferredSize(new Dimension(80, 25));
        heightTextField.setPreferredSize(new Dimension(80, 25));
        widthTextField.setFont(textFieldFont);
        heightTextField.setFont(textFieldFont);
        widthDoc.setDocumentFilter(new DocumentSizeFilter(4));
        heightDoc.setDocumentFilter(new DocumentSizeFilter(3));
        widthTextField.setDocument(widthDoc);
        heightTextField.setDocument(heightDoc);

        System.out.println(((AbstractDocument) widthTextField.getDocument()).getDocumentFilter().toString());

        JLabel startLabel = configureLabel("Covid Simulator", mainLabelFont, Color.black);
        JLabel widthLabel = configureLabel("Width", textFieldFont, Color.black);
        JLabel heightLabel = configureLabel("Height", textFieldFont, Color.black);
        JLabel maxWidthLabel = configureLabel("Range 10 - 1200", textFieldFont, Color.black);
        JLabel maxHeightLabel = configureLabel("Range 10 - 800", textFieldFont, Color.black);

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    boolean flag = false;
                    int width = Integer.parseInt(widthTextField.getText());
                    if(width > 1200 || width < 10){
                        maxWidthLabel.setForeground(Color.red);
                        flag = true;
                    }
                    else{
                        maxWidthLabel.setForeground(Color.BLACK);
                    }
                    int height = Integer.parseInt(heightTextField.getText());
                    if(height > 800 || height < 10){
                        maxHeightLabel.setForeground(Color.red);
                        flag = true;
                    }
                    else{
                        maxHeightLabel.setForeground(Color.black);
                    }
                    if (!flag) {
                        Color colours[] = {
                                healthyColourLabel.getForeground(),
                                infectedColourLabel.getForeground(),
                                recoveredColourLabel.getForeground(),
                                deceasedColourLabel.getForeground()
                        };
                        engine.StartSimulation(Integer.parseInt(heightTextField.getText()), Integer.parseInt(widthTextField.getText()),
                                startingInfectionsSlider.getValue(), startingComplyingSlider.getValue(), infectiousnessSlider.getValue(), mortalitySlider.getValue(), colours);
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        startingInfectionAmountLabel = configureLabel("Starting infection amount: 5%", textFieldFont, Color.black);
        startingComplyingAmountLabel = configureLabel("Starting complying amount: 80%", textFieldFont, Color.black);
        infectiousnessLabel = configureLabel("Infectiousness: 75%", textFieldFont, Color.black);
        mortalityLabel = configureLabel("Mortality: 5%", textFieldFont, Color.black);
        healthyColourLabel = configureLabel("Healthy", textFieldFont, Color.green);
        infectedColourLabel = configureLabel("Infected", textFieldFont, Color.red);
        deceasedColourLabel = configureLabel("Deceased", textFieldFont, Color.black);
        recoveredColourLabel = configureLabel("Recovered", textFieldFont, Color.blue);
        JLabel chooseColourLabel = configureLabel("Choose Colours", colourPickFont, Color.black);

        startButton.setText("Start Simulation");

        Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
        labelTable.put(0, new JLabel("0%"));
        labelTable.put(50, new JLabel("50%"));
        labelTable.put(100, new JLabel("100%"));

        startingInfectionsSlider = configureSlider(startingInfectionAmountLabel, labelTable, "Starting infection amount", 5);
        startingComplyingSlider = configureSlider(startingComplyingAmountLabel, labelTable, "Starting conplying amount", 80);
        infectiousnessSlider = configureSlider(infectiousnessLabel, labelTable, "Infectiousness", 75);
        mortalitySlider = configureSlider(mortalityLabel, labelTable, "Mortality", 5);

        JButton changeHealthyColourButton = configureColourChangingButton(healthyColourLabel);
        JButton changeInfectedColourButton = configureColourChangingButton(infectedColourLabel);
        JButton changeRecoveredColourButton = configureColourChangingButton(recoveredColourLabel);
        JButton changeDeceasedColourButton = configureColourChangingButton(deceasedColourLabel);

        mainMenuTopPanel.setLayout(new GridBagLayout());
        mainMenuTopPanel.add(startLabel, setConstrains(
                0,
                0,
                1,
                0,
                GridBagConstraints.NORTH,
                new Insets(0, 0, 50, 0),
                5
        ));
        mainMenuTopPanel.add(widthTextField, setConstrains(
                1,
                1,
                0,
                0,
                GridBagConstraints.WEST,
                new Insets(0,10,10,0),
                1
        ));
        mainMenuTopPanel.add(heightTextField, setConstrains(
                1,
                2,
                0,
                0,
                GridBagConstraints.WEST,
                new Insets(0, 10, 10, 0),
                1
        ));
        mainMenuTopPanel.add(maxWidthLabel, setConstrains(
                3,
                1,
                0.5,
                0,
                GridBagConstraints.WEST,
                new Insets(0,0,10,0),
                1
        ));
        mainMenuTopPanel.add(maxHeightLabel, setConstrains(
                3,
                2,
                0.5,
                0,
                GridBagConstraints.WEST,
                new Insets(0, 0, 10, 0),
                1
        ));
        mainMenuTopPanel.add(widthLabel, setConstrains(
                0,
                1,
                0.01,
                0,
                GridBagConstraints.EAST,
                new Insets(0, 10, 10, 0),
                1
        ));
        mainMenuTopPanel.add(heightLabel, setConstrains(
                0,
                2,
                0.01,
                0,
                GridBagConstraints.EAST,
                new Insets(0, 10, 10, 0),
                1
        ));
        mainMenuTopPanel.add(startingInfectionsSlider, setConstrains(
                0,
                4,
                0.1,
                0,
                GridBagConstraints.WEST,
                new Insets(10,10,0,0),
                4
        ));
        mainMenuTopPanel.add(startingInfectionAmountLabel, setConstrains(
                0,
                3,
                0.1,
                0,
                GridBagConstraints.WEST,
                new Insets(0,10,0,0),
                4
        ));
        mainMenuTopPanel.add(infectiousnessSlider, setConstrains(
                4,
                2,
                1,
                0,
                GridBagConstraints.EAST,
                new Insets(10,0,0,40),
                1
        ));
        mainMenuTopPanel.add(infectiousnessLabel, setConstrains(
                4,
                1,
                1,
                0,
                GridBagConstraints.EAST,
                new Insets(0,0,0,40),
                1
        ));
        mainMenuTopPanel.add(mortalitySlider, setConstrains(
                4,
                4,
                1,
                0,
                GridBagConstraints.EAST,
                new Insets(10,0,0,40),
                1
        ));
        mainMenuTopPanel.add(mortalityLabel, setConstrains(
                4,
                3,
                1,
                0,
                GridBagConstraints.EAST,
                new Insets(10,0,0,40),
                1
        ));
        mainMenuTopPanel.add(startingComplyingSlider, setConstrains(
                0,
                6,
                0.5,
                0,
                GridBagConstraints.WEST,
                new Insets(10,10,0,0),
                4
        ));
        mainMenuTopPanel.add(startingComplyingAmountLabel, setConstrains(
                0,
                5,
                0.5,
                0,
                GridBagConstraints.WEST,
                new Insets(10,10,0,0),
                4
        ));
        mainMenuTopPanel.add(startButton, setConstrains(
                0,
                7,
                0.5,
                0,
                GridBagConstraints.CENTER,
                new Insets(50, 0, 0, 0),
                5
        ));
        mainMenuTopPanel.add(new JLabel(" "), setConstrains(
                1,
                10,
                1,
                1,
                GridBagConstraints.BASELINE,
                new Insets(0,0,0,0),
                5
        ));

        mainMenuBottomPanel.setLayout(new GridBagLayout());
        mainMenuBottomPanel.add(healthyColourLabel, setConstrains(
                0,
                1,
                0.5,
                0,
                GridBagConstraints.EAST,
                new Insets(0,10,0,0),
                2
        ));
        mainMenuBottomPanel.add(infectedColourLabel, setConstrains(
                0,
                2,
                0.5,
                0,
                GridBagConstraints.EAST,
                new Insets(0,10,0,0),
                2
        ));
        mainMenuBottomPanel.add(recoveredColourLabel, setConstrains(
                0,
                3,
                0.5,
                0,
                GridBagConstraints.EAST,
                new Insets(0,10,0,0),
                2
        ));
        mainMenuBottomPanel.add(deceasedColourLabel, setConstrains(
                0,
                4,
                0.5,
                0,
                GridBagConstraints.EAST,
                new Insets(0,10,0,0),
                2
        ));
        mainMenuBottomPanel.add(changeHealthyColourButton, setConstrains(
                4,
                1,
                0.5,
                0,
                GridBagConstraints.WEST,
                new Insets(0,10,0,0),
                2
        ));
        mainMenuBottomPanel.add(changeInfectedColourButton, setConstrains(
                4,
                2,
                0.5,
                0,
                GridBagConstraints.WEST,
                new Insets(0,10,0,0),
                2
        ));
        mainMenuBottomPanel.add(changeRecoveredColourButton, setConstrains(
                4,
                3,
                0.5,
                0,
                GridBagConstraints.WEST,
                new Insets(0,10,0,0),
                2
        ));
        mainMenuBottomPanel.add(changeDeceasedColourButton, setConstrains(
                4,
                4,
                0.5,
                0,
                GridBagConstraints.WEST,
                new Insets(0,10,0,0),
                2
        ));
        GridBagConstraints c = setConstrains(
                1,
                0,
                0.5,
                0,
                GridBagConstraints.EAST,
                new Insets(10,0,0,100),
                1
        );
        c.gridheight = 4;
        mainMenuBottomPanel.add(chooseColourLabel, c);
        mainMenuTopPanel.add(new JLabel(" "), setConstrains(
                1,
                5,
                1,
                1,
                GridBagConstraints.BASELINE,
                new Insets(0,0,0,0),
                5
        ));

        startWindow.setPreferredSize(new Dimension(600, 650));
        mainMenuTopPanel.setPreferredSize(new Dimension(600, 450));
        mainMenuBottomPanel.setPreferredSize(new Dimension(600, 200));

        mainMenuHorizontalSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        mainMenuHorizontalSplitPane.setDividerLocation(450);
        mainMenuHorizontalSplitPane.setTopComponent(mainMenuTopPanel);
        mainMenuHorizontalSplitPane.setBottomComponent(mainMenuBottomPanel);
        mainMenuHorizontalSplitPane.setEnabled(false);

        startWindow.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        startWindow.getContentPane().add(mainMenuHorizontalSplitPane);
        startWindow.getContentPane().setLayout(new GridLayout());
        startWindow.pack();
        startWindow.setLocationRelativeTo(null);
        startWindow.setVisible(true);
    }

    private JButton configureColourChangingButton(JLabel colourLabel){
        JButton changeColourButton = new JButton();
        changeColourButton.setText("Change Colour");
        changeColourButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color clr = JColorChooser.showDialog(bottomPanel, String.format("Choose Colour For %s units", colourLabel.getText()), colourLabel.getForeground());
                colourLabel.setForeground(clr);
            }
        });

        return changeColourButton;
    }

    private JLabel configureLabel(String text, Font font, Color color){
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(color);
        return label;
    }

    private JSlider configureSlider(JLabel sliderLabel, Hashtable<Integer, JLabel> labelsForSlider, String labelText, int startingValue){
        JSlider sliderToConfigure = new JSlider(0, 100, startingValue);
        sliderToConfigure.setLabelTable(labelsForSlider);
        sliderToConfigure.setPaintLabels(true);
        sliderToConfigure.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                sliderLabel.setText(String.format("%s: %d%s", labelText, sliderToConfigure.getValue(), "%"));
            }
        });

        return sliderToConfigure;
    }

    private GridBagConstraints setConstrains(int gridx, int gridy, double weightx, double weighty, int anchor, Insets insets, int gridWidth){
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = gridx;
        c.gridy = gridy;
        c.weightx = weightx;
        c.weighty = weighty;
        c.anchor = anchor;
        c.insets = insets;
        c.gridwidth = gridWidth;
        return c;
    }

    public void createBoardGUI(Dimension simulationBoardDimensions, Dimension boardScale){
        JButton btn = new JButton();

        simulationBoardScale = boardScale;

        simulationWindow = new JFrame();

        leftPanel = new SimulationPanel();
        rightPanel = new JPanel();
        bottomPanel = new JPanel();
        horizontalSplitPane = new JSplitPane();
        verticalSplitPane = new JSplitPane();

        //Configure horizontal split
        horizontalSplitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        horizontalSplitPane.setDividerLocation(simulationBoardDimensions.width * boardScale.width);
        horizontalSplitPane.setLeftComponent(leftPanel);
        horizontalSplitPane.setRightComponent(rightPanel);
        horizontalSplitPane.setEnabled(false);

        //Configure vertical split
        verticalSplitPane.setTopComponent(horizontalSplitPane);
        verticalSplitPane.setBottomComponent(bottomPanel);
        verticalSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        verticalSplitPane.setDividerLocation(simulationBoardDimensions.height * boardScale.height);
        verticalSplitPane.setEnabled(false);

        JButton button = new JButton();
        ActionListener event = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(button.getText().equals("Run")){
                    button.setText("Stop");
                    btn.setVisible(false);
                }
                else{
                    button.setText("Run");
                    btn.setVisible(true);
                }
                simulationEngine.runButtonPressed();
            }
        };

        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    simulationEngine.UpdateBoard();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        button.addActionListener(event);
        button.setText("Run");
        button.setFont(colourPickFont);
        btn.setText("Iterate");
        btn.setFont(colourPickFont);

        simulationWindow.setPreferredSize(new Dimension(simulationBoardDimensions.width * boardScale.width + 250,
                simulationBoardDimensions.height * boardScale.height + 150));
        simulationWindow.getContentPane().setLayout(new GridLayout());
        simulationWindow.getContentPane().add(verticalSplitPane);

        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setDoubleBuffered(true);
        leftPanel.setPreferredSize(new Dimension(simulationBoardDimensions.width * boardScale.width, simulationBoardDimensions.height * boardScale.height));
        rightPanel.setPreferredSize(new Dimension(250, simulationBoardDimensions.height * boardScale.height));
        rightPanel.setLayout(new GridBagLayout());

        Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
        labelTable.put(0, new JLabel("0%"));
        labelTable.put(50, new JLabel("50%"));
        labelTable.put(100, new JLabel("100%"));

        complyingLabel = configureLabel(String.format("Complying: %d%s", startingComplyingSlider.getValue(), "%"), textFieldFont, Color.black);

        JSlider complyingSlider = new JSlider(0, 100, startingComplyingSlider.getValue());
        complyingSlider.setLabelTable(labelTable);
        complyingSlider.setPaintLabels(true);
        complyingSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                complyingLabel.setText(String.format("Complying: %d%s", complyingSlider.getValue(), "%"));

                if(!complyingSlider.getValueIsAdjusting()){
                    try {
                        simulationEngine.changedComplyingPercent(complyingSlider.getValue());
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        GridBagConstraints c = setConstrains(
                0,
                0,
                1,
                0,
                GridBagConstraints.CENTER,
                new Insets(20,0,0,0),
                4
        );
        c.fill = GridBagConstraints.HORIZONTAL;
        rightPanel.add(complyingLabel, c);

        c = setConstrains(
                1,
                1,
                1,
                0,
                GridBagConstraints.CENTER,
                new Insets(10,0,0,0),
                4
        );
        c.fill = GridBagConstraints.HORIZONTAL;
        rightPanel.add(complyingSlider, c);
        c = setConstrains(
                0,
                3,
                1,
                0.001,
                GridBagConstraints.PAGE_END,
                new Insets(0,0,0,0),
                4
        );
        c.ipady = 100;
        c.fill = GridBagConstraints.HORIZONTAL;
        rightPanel.add(button, c);
        c = setConstrains(
                0,
                2,
                1,
                0.1,
                GridBagConstraints.SOUTH,
                new Insets(0,0,0,0),
                3
        );
        c.ipady = 30;
        c.fill = GridBagConstraints.HORIZONTAL;
        rightPanel.add(btn, c);

        infectedLabel = configureLabel("Infected: ", colourPickFont, Color.black);
        recoveredLabel = configureLabel("Recovered: ", colourPickFont, Color.black);
        deceasedLabel = configureLabel("Deceased: ", colourPickFont, Color.black);
        infectionIncreaseLabel = configureLabel("New Infections: ", colourPickFont, Color.black);

        bottomPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 40));
        bottomPanel.add(infectedLabel);
        bottomPanel.add(recoveredLabel);
        bottomPanel.add(deceasedLabel);
        bottomPanel.add(infectionIncreaseLabel);

        simulationDimensions = simulationBoardDimensions;
        img = new BufferedImage(simulationBoardDimensions.width,
                simulationBoardDimensions.height, BufferedImage.TYPE_INT_RGB);
        imgGx = img.createGraphics();

        simulationWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        simulationWindow.setTitle("Covid Simulation");
        simulationWindow.pack();
        simulationWindow.setLocationRelativeTo(null);
        simulationWindow.setResizable(false);

        simulationWindow.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                simulationEngine.stopSimulation();
            }
        });

        simulationWindow.setVisible(true);
    }

    public void drawPoint(int x, int y, Color color){
        imgGx.setColor(color);
        imgGx.drawLine(x,y,x,y);
    }

    public void drawImg(){
        if(imgFile == null){
            imgFile = new File("img.bmp");
        }
        try{
            ImageIO.write(img, "bmp" ,imgFile);
            Image scaledImage = img.getScaledInstance(simulationDimensions.width * simulationBoardScale.width,
                    simulationDimensions.height * simulationBoardScale.height, Image.SCALE_DEFAULT);
            leftPanel.updateParameters(0, 0, scaledImage);
            leftPanel.repaint();
        }
        catch (Exception ignored){}
    }

    public void updateStatsLabels(int currentInfected, int recovered, int deceased, int infectedIncrease){
        infectedLabel.setText(String.format("Infected: %d", currentInfected));
        recoveredLabel.setText(String.format("Recovered: %d", recovered));
        deceasedLabel.setText(String.format("Deceased: %d", deceased));
        infectionIncreaseLabel.setText(String.format("New Infections: %d", infectedIncrease));
    }
}

class DocumentSizeFilter extends DocumentFilter{
    int maxIntegers;

    public DocumentSizeFilter(int maxIntegers){
        this.maxIntegers = maxIntegers;
    }

    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
        try{
            Integer.parseInt(string);
        }
        catch (Exception e){
            Toolkit.getDefaultToolkit().beep();
            return;
        }
        if((fb.getDocument().getLength() + string.length()) <= maxIntegers){
            System.out.println(fb.getDocument().getLength());
            super.insertString(fb, offset, string, attr);
        }
        else{
            Toolkit.getDefaultToolkit().beep();
        }
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {

        try {
            Integer.parseInt(text);

            if ((fb.getDocument().getLength() + text.length() - length) <= maxIntegers){
                super.replace(fb, offset, length, text, attrs);
                return;
            }
        }
        catch (Exception ignored){

        }
        Toolkit.getDefaultToolkit().beep();
    }
}