import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

enum PageName {
    Functions,
    ErrorsX,
    ErrorsN
}

class GUI extends JFrame {

    private final String EXACT_KEY = "exactKey";
    private final String EULER_KEY = "eulerKey";
    private final String IMPROVED_EULER_KEY = "improvedEulerKey";
    private final String RUNGE_KUTTA_KEY = "rungeKuttaKey";
    private final String EXACT_CB_KEY = "exactCheckBoxKey";
    private final String PAGE1_BUTTON_KEY = "page1ButtonKey";
    private final String PAGE2_BUTTON_KEY = "page2ButtonKey";
    private final String PAGE3_BUTTON_KEY = "page3ButtonKey";
    private final String X_INITIAL_TF_KEY = "xInitialTFKey";
    private final String Y_INITIAL_TF_KEY = "yInitialTFKey";
    private final String X_RIGHT_BOUND_TF_KEY = "xRightBoundTFKey";
    private final String STEPS_TF_KEY = "stepsTFKey";
    private final String STEPS_LABEL_KEY = "stepsLabelKey";
    private final String N0_TF_KEY = "n0TFKey";
    private final String N0_LABEL_KEY = "n0LabelKey";
    private final String N_MAX_TF_KEY = "nMaxTFKey";
    private final String N_MAX_LABEL_KEY = "nMaxLabelKey";
    private final DataProvider dataProvider;

    private XYDataset dataset;
    private ChartPanel chartPanel;
    private JPanel initialConditionsPanel;
    private JPanel checkBoxesPanel;
    private JPanel pagesPanel;
    private PageName currentPage;

    ArrayList<String> functionsOrder = new ArrayList<>();

    public GUI(DataProvider dataProvider) {
        this.dataProvider = dataProvider;
        functionsOrder.add(EXACT_KEY);
        functionsOrder.add(EULER_KEY);
        functionsOrder.add(IMPROVED_EULER_KEY);
        functionsOrder.add(RUNGE_KUTTA_KEY);
        currentPage = PageName.Functions;
        dataset = createDataset();
    }

    private void setPageSettings() {
        ((JCheckBox) checkBoxesPanel.getClientProperty(EXACT_CB_KEY)).setVisible(currentPage == PageName.Functions);
        ((JButton) pagesPanel.getClientProperty(PAGE1_BUTTON_KEY)).setEnabled(currentPage != PageName.Functions);
        ((JButton) pagesPanel.getClientProperty(PAGE2_BUTTON_KEY)).setEnabled(currentPage != PageName.ErrorsX);
        ((JButton) pagesPanel.getClientProperty(PAGE3_BUTTON_KEY)).setEnabled(currentPage != PageName.ErrorsN);
        ((JTextField) initialConditionsPanel.getClientProperty(X_INITIAL_TF_KEY)).setEnabled(currentPage != PageName.ErrorsN);
        ((JTextField) initialConditionsPanel.getClientProperty(Y_INITIAL_TF_KEY)).setEnabled(currentPage != PageName.ErrorsN);
        ((JTextField) initialConditionsPanel.getClientProperty(X_RIGHT_BOUND_TF_KEY)).setEnabled(currentPage != PageName.ErrorsN);
        ((JTextField) initialConditionsPanel.getClientProperty(STEPS_TF_KEY)).setVisible(currentPage != PageName.ErrorsN);
        ((JTextField) initialConditionsPanel.getClientProperty(N0_TF_KEY)).setVisible(currentPage == PageName.ErrorsN);
        ((JTextField) initialConditionsPanel.getClientProperty(N_MAX_TF_KEY)).setVisible(currentPage == PageName.ErrorsN);
        ((JLabel) initialConditionsPanel.getClientProperty(STEPS_LABEL_KEY)).setVisible(currentPage != PageName.ErrorsN);
        ((JLabel) initialConditionsPanel.getClientProperty(N0_LABEL_KEY)).setVisible(currentPage == PageName.ErrorsN);
        ((JLabel) initialConditionsPanel.getClientProperty(N_MAX_LABEL_KEY)).setVisible(currentPage == PageName.ErrorsN);
        String xLabel = "";
        String yLabel = "";
        boolean isVisible = true;
        switch (currentPage) {
            case Functions -> {
                xLabel = "X";
                ;
                yLabel = "Y";
                isVisible = ((JCheckBox) checkBoxesPanel.getClientProperty(EXACT_CB_KEY)).isSelected();
            }
            case ErrorsX -> {
                xLabel = "X";
                yLabel = "E(x)";
                isVisible = false;
            }
            case ErrorsN -> {
                xLabel = "N";
                yLabel = "E(n)";
                isVisible = false;
            }
        }
        chartPanel.getChart().getXYPlot().getDomainAxis().setLabel(xLabel);
        chartPanel.getChart().getXYPlot().getRangeAxis().setLabel(yLabel);
        chartPanel.getChart().getXYPlot().getRenderer().setSeriesVisible(functionsOrder.indexOf(EXACT_KEY), isVisible);
    }

    private void updateChartDataset() {
        dataset = createDataset();
        chartPanel.getChart().getXYPlot().setDataset(dataset);
        chartPanel.repaint();
    }

    private void updateGraphVisibility(String key, boolean isVisible) {
        chartPanel.getChart().getXYPlot().getRenderer().setSeriesVisible(functionsOrder.indexOf(key), isVisible);
        chartPanel.repaint();
    }

    private JPanel createPagesPanel() {
        JButton page1Button = new JButton("Function");
        JButton page2Button = new JButton("LTE");
        JButton page3Button = new JButton("GTE");

        page1Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentPage = PageName.Functions;
                setPageSettings();
                updateChartDataset();
            }
        });
        page2Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentPage = PageName.ErrorsX;
                setPageSettings();
                updateChartDataset();
            }
        });
        page3Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentPage = PageName.ErrorsN;
                setPageSettings();
                updateChartDataset();
            }
        });

        JPanel pages = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel pagesBlock = new JPanel(new GridLayout(1, 3));
        pagesBlock.add(page1Button);
        pagesBlock.add(page2Button);
        pagesBlock.add(page3Button);
        pages.add(pagesBlock);

        pages.putClientProperty(PAGE1_BUTTON_KEY, page1Button);
        pages.putClientProperty(PAGE2_BUTTON_KEY, page2Button);
        pages.putClientProperty(PAGE3_BUTTON_KEY, page3Button);

        return pages;
    }

    private JPanel createInitialConditionsPanel() {
        JLabel xInitialLabel = new JLabel("x0");
        JLabel xRightBorderLabel = new JLabel("X");
        JLabel stepsLabel = new JLabel("n");
        JLabel yInitialLabel = new JLabel("y0");
        JLabel n0Label = new JLabel("n0");
        JLabel nMaxLabel = new JLabel("N");

        JTextField xInitialTF = new JTextField(String.valueOf(dataProvider.getxInitial()));
        JTextField xRightBorderTF = new JTextField(String.valueOf(dataProvider.getRightBorder()));
        JTextField stepsTF = new JTextField(String.valueOf(dataProvider.getAmountOfSteps()));
        JTextField yInitialTF = new JTextField(String.valueOf(dataProvider.getyInitial()));
        JTextField n0TF = new JTextField(String.valueOf(dataProvider.getnMin()));
        JTextField nMaxTF = new JTextField(String.valueOf(dataProvider.getnMax()));

        xInitialTF.setMinimumSize(new Dimension(30, 20));
        xRightBorderTF.setMinimumSize(new Dimension(30, 20));
        stepsTF.setMinimumSize(new Dimension(30, 20));
        yInitialTF.setMinimumSize(new Dimension(30, 20));
        n0TF.setMinimumSize(new Dimension(30, 20));
        nMaxTF.setMinimumSize(new Dimension(30, 20));

        KeyAdapter keyXYAdapter = new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (!(Character.isDigit(e.getKeyChar()) || e.getKeyChar() == '-' || e.getKeyChar() == '.')) {
                    e.consume();
                }
            }
        };

        KeyAdapter keyStepAdapter = new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (!(Character.isDigit(e.getKeyChar()))) {
                    e.consume();
                }
            }
        };

        xInitialTF.addKeyListener(keyXYAdapter);
        yInitialTF.addKeyListener(keyXYAdapter);
        xRightBorderTF.addKeyListener(keyXYAdapter);
        stepsTF.addKeyListener(keyStepAdapter);
        n0TF.addKeyListener(keyStepAdapter);
        nMaxTF.addKeyListener(keyStepAdapter);

        xInitialTF.getDocument().addDocumentListener(new DocumentListener() {

            void warn(DocumentEvent e) {
                try {
                    String text = e.getDocument().getText(0, e.getDocument().getLength());
                    if (!text.isEmpty()) {
                        dataProvider.setxInitial(Double.parseDouble(text));
                        updateChartDataset();
                    }
                } catch (BadLocationException | NumberFormatException ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                warn(e);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                warn(e);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                warn(e);
            }
        });
        yInitialTF.getDocument().addDocumentListener(new DocumentListener() {

            void warn(DocumentEvent e) {
                try {
                    String text = e.getDocument().getText(0, e.getDocument().getLength());
                    if (!text.isEmpty()) {
                        Double.parseDouble(text);
                        dataProvider.setyInitial(Double.parseDouble(text));
                        updateChartDataset();
                    }
                } catch (BadLocationException | NumberFormatException ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                warn(e);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                warn(e);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                warn(e);
            }
        });
        xRightBorderTF.getDocument().addDocumentListener(new DocumentListener() {

            void warn(DocumentEvent e) {
                try {
                    String text = e.getDocument().getText(0, e.getDocument().getLength());
                    if (!text.isEmpty()) {
                        Double.parseDouble(text);
                        dataProvider.setRightBorder(Double.parseDouble(text));
                        updateChartDataset();
                    }
                } catch (BadLocationException | NumberFormatException ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                warn(e);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                warn(e);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                warn(e);
            }
        });
        stepsTF.getDocument().addDocumentListener(new DocumentListener() {

            void warn(DocumentEvent e) {
                try {
                    String text = e.getDocument().getText(0, e.getDocument().getLength());
                    if (!text.isEmpty()) {
                        dataProvider.setAmountOfSteps(Integer.parseInt(text));
                        updateChartDataset();
                    }
                } catch (BadLocationException ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                warn(e);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                warn(e);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                warn(e);
            }
        });
        n0TF.getDocument().addDocumentListener(new DocumentListener() {

            void warn(DocumentEvent e) {
                try {
                    String text = e.getDocument().getText(0, e.getDocument().getLength());
                    if (!text.isEmpty()) {
                        dataProvider.setnMin(Integer.parseInt(text));
                        updateChartDataset();
                    }
                } catch (BadLocationException ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                warn(e);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                warn(e);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                warn(e);
            }
        });
        nMaxTF.getDocument().addDocumentListener(new DocumentListener() {

            void warn(DocumentEvent e) {
                try {
                    String text = e.getDocument().getText(0, e.getDocument().getLength());
                    if (!text.isEmpty()) {
                        dataProvider.setnMax(Integer.parseInt(text));
                        updateChartDataset();
                    }
                } catch (BadLocationException ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                warn(e);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                warn(e);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                warn(e);
            }
        });


        JPanel initialCondition = new JPanel(new FlowLayout(FlowLayout.CENTER));
        GroupLayout initialConditionBlock = new GroupLayout(initialCondition);
        initialConditionBlock.setAutoCreateGaps(true);
        initialConditionBlock.setAutoCreateContainerGaps(true);
        initialConditionBlock.setVerticalGroup(initialConditionBlock.createSequentialGroup()
                .addGroup(initialConditionBlock.createParallelGroup()
                        .addComponent(xInitialLabel)
                        .addComponent(xInitialTF))
                .addGroup(initialConditionBlock.createParallelGroup()
                        .addComponent(yInitialLabel)
                        .addComponent(yInitialTF))
                .addGroup(initialConditionBlock.createParallelGroup()
                        .addComponent(xRightBorderLabel)
                        .addComponent(xRightBorderTF))
                .addGroup(initialConditionBlock.createParallelGroup()
                        .addComponent(stepsLabel)
                        .addComponent(stepsTF))
                .addGroup(initialConditionBlock.createParallelGroup()
                        .addComponent(n0Label)
                        .addComponent(n0TF))
                .addGroup(initialConditionBlock.createParallelGroup()
                        .addComponent(nMaxLabel)
                        .addComponent(nMaxTF)));
        initialConditionBlock.setHorizontalGroup(initialConditionBlock.createSequentialGroup()
                .addGroup(initialConditionBlock.createParallelGroup()
                        .addComponent(xInitialLabel)
                        .addComponent(yInitialLabel)
                        .addComponent(xRightBorderLabel)
                        .addComponent(stepsLabel)
                        .addComponent(n0Label)
                        .addComponent(nMaxLabel))
                .addGroup(initialConditionBlock.createParallelGroup()
                        .addComponent(xInitialTF)
                        .addComponent(yInitialTF)
                        .addComponent(xRightBorderTF)
                        .addComponent(stepsTF)
                        .addComponent(n0TF)
                        .addComponent(nMaxTF)));
        initialConditionBlock.linkSize(SwingConstants.VERTICAL,
                xInitialTF, yInitialTF, xRightBorderTF, stepsTF, n0TF, nMaxTF);
        initialCondition.setLayout(initialConditionBlock);

        initialCondition.putClientProperty(X_INITIAL_TF_KEY, xInitialTF);
        initialCondition.putClientProperty(Y_INITIAL_TF_KEY, yInitialTF);
        initialCondition.putClientProperty(X_RIGHT_BOUND_TF_KEY, xRightBorderTF);
        initialCondition.putClientProperty(STEPS_TF_KEY, stepsTF);
        initialCondition.putClientProperty(N0_TF_KEY, n0TF);
        initialCondition.putClientProperty(N_MAX_TF_KEY, nMaxTF);
        initialCondition.putClientProperty(STEPS_LABEL_KEY, stepsLabel);
        initialCondition.putClientProperty(N0_LABEL_KEY, n0Label);
        initialCondition.putClientProperty(N_MAX_LABEL_KEY, nMaxLabel);
        return initialCondition;
    }

    private JPanel createCheckBoxesPanel() {
        JCheckBox exactCB = new JCheckBox("Exact", true);
        JCheckBox eulerCB = new JCheckBox("Euler", true);
        JCheckBox improvedEulerCB = new JCheckBox("IE", true);
        JCheckBox rungeKuttaCB = new JCheckBox("RK", true);

        exactCB.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                updateGraphVisibility(EXACT_KEY, e.getStateChange() == ItemEvent.SELECTED);
            }
        });
        eulerCB.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                updateGraphVisibility(EULER_KEY, e.getStateChange() == ItemEvent.SELECTED);
            }
        });
        improvedEulerCB.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                updateGraphVisibility(IMPROVED_EULER_KEY, e.getStateChange() == ItemEvent.SELECTED);
            }
        });
        rungeKuttaCB.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                updateGraphVisibility(RUNGE_KUTTA_KEY, e.getStateChange() == ItemEvent.SELECTED);
            }
        });

        JPanel checkBoxes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JPanel checkBoxesBlock = new JPanel(new GridLayout(4, 1));
        checkBoxesBlock.add(exactCB);
        checkBoxesBlock.add(eulerCB);
        checkBoxesBlock.add(improvedEulerCB);
        checkBoxesBlock.add(rungeKuttaCB);
        checkBoxes.add(checkBoxesBlock);

        checkBoxes.putClientProperty(EXACT_CB_KEY, exactCB);

        return checkBoxes;
    }

    private XYDataset createDataset() {
        var dataset = new XYSeriesCollection();
        switch (currentPage) {
            case Functions -> {
                for (String function : functionsOrder) {
                    switch (function) {
                        case EXACT_KEY -> dataset.addSeries(dataProvider.getExactSolution());
                        case EULER_KEY -> dataset.addSeries(dataProvider.getEulerSolution());
                        case IMPROVED_EULER_KEY -> dataset.addSeries(dataProvider.getImprovedEulerSolution());
                        case RUNGE_KUTTA_KEY -> dataset.addSeries(dataProvider.getRungeKuttaSolution());
                    }
                }
            }
            case ErrorsX -> {
                for (String function : functionsOrder) {
                    switch (function) {
                        case EXACT_KEY -> dataset.addSeries(dataProvider.getExactSolution());
                        case EULER_KEY -> dataset.addSeries(dataProvider.getEulerErrorDependsX());
                        case IMPROVED_EULER_KEY -> dataset.addSeries(dataProvider.getImprovedEulerErrorDependsX());
                        case RUNGE_KUTTA_KEY -> dataset.addSeries(dataProvider.getRungeKuttaErrorDependsX());
                    }
                }
            }
            case ErrorsN -> {
                XYSeries[] data = dataProvider.getErrorsDependsN();
                for (String function : functionsOrder) {
                    switch (function) {
                        case EXACT_KEY -> dataset.addSeries(dataProvider.getExactSolution());
                        case EULER_KEY -> dataset.addSeries(data[0]);
                        case IMPROVED_EULER_KEY -> dataset.addSeries(data[1]);
                        case RUNGE_KUTTA_KEY -> dataset.addSeries(data[2]);
                    }
                }
            }
        }
        return dataset;
    }

    private JFreeChart createChart(XYDataset dataset) {
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Graphs",
                "X",
                "Y",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false);

        XYPlot plot = chart.getXYPlot();

        var renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesPaint(0, Color.RED);
        renderer.setSeriesPaint(1, Color.BLUE);
        renderer.setSeriesPaint(2, Color.YELLOW);
        renderer.setSeriesPaint(3, Color.GREEN);
        renderer.setSeriesStroke(0, new BasicStroke(2.0f));
        renderer.setSeriesStroke(1, new BasicStroke(2.0f));
        renderer.setSeriesStroke(2, new BasicStroke(2.0f));
        renderer.setSeriesStroke(3, new BasicStroke(2.0f));

        plot.setRenderer(renderer);
        plot.setBackgroundPaint(Color.white);

        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.BLACK);

        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.BLACK);

        chart.getLegend().setFrame(BlockBorder.NONE);

        return chart;
    }

    public void createWindow() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JFreeChart chart = createChart(dataset);
        chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(500, 500));

        initialConditionsPanel = createInitialConditionsPanel();
        checkBoxesPanel = createCheckBoxesPanel();
        pagesPanel = createPagesPanel();

        Container container = getContentPane();
        container.add(pagesPanel, BorderLayout.NORTH);
        container.add(initialConditionsPanel, BorderLayout.WEST);
        container.add(chartPanel, BorderLayout.CENTER);
        container.add(checkBoxesPanel, BorderLayout.EAST);

        setPageSettings();

        setLocationRelativeTo(null);
        pack();
    }

}