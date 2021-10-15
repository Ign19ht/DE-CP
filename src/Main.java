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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

class Solution {
    private double rightBorder;
    private double xInitial;
    private double yInitial;
    private int amountOfSteps;
    private double h;
    private double parameter;

    public Solution() {
        rightBorder = 7.;
        xInitial = 0.;
        yInitial = 0.;
        amountOfSteps = 15;
        h = (rightBorder - xInitial) / amountOfSteps;
        calculateParameter();
    }

    private double f(double x, double y) {
        return 2 * Math.exp(x) - y;
    }
    
    private void calculateParameter() {
        parameter = (yInitial - Math.exp(xInitial)) / Math.exp(-xInitial);
    }

    public void setRightBorder(double rightBorder) {
        this.rightBorder = rightBorder;
        h = (rightBorder - xInitial) / amountOfSteps;
    }

    public void setxInitial(double xInitial) {
        this.xInitial = xInitial;
        h = (rightBorder - xInitial) / amountOfSteps;
        calculateParameter();
    }

    public void setyInitial(double yInitial) {
        this.yInitial = yInitial;
        h = (rightBorder - xInitial) / amountOfSteps;
        calculateParameter();
    }

    public void setAmountOfSteps(int amountOfSteps) {
        this.amountOfSteps = amountOfSteps;
        h = (rightBorder - xInitial) / amountOfSteps;
    }

    public XYSeries getExactSolution() {
        var series = new XYSeries("Exact");
        double x = xInitial;
        for (int i = 0; i <= amountOfSteps; i++) {
            series.add(x, Math.exp(x) + parameter * Math.exp(-x));
            x += h;
        }
        return series;
    }

    public XYSeries getEulerSolution() {
        var series = new XYSeries("Euler");
        double x = xInitial;
        double y = yInitial;
        series.add(x, y);
        for (int i = 1; i <= amountOfSteps; i++) {
            y += h * f(x, y);
            x += h;
            series.add(x, y);
        }
        return series;
    }

    public XYSeries getImprovedEulerSolution() {
        var series = new XYSeries("Improved Euler");
        double x = xInitial;
        double y = yInitial;
        series.add(x, y);
        for (int i = 1; i <= amountOfSteps; i++) {
            y += h * f(x + h / 2, y + h * f(x, y) / 2);
            x += h;
            series.add(x, y);
        }
        return series;
    }

    public XYSeries getRungeKuttaSolution() {
        var series = new XYSeries("Runge-Kutta");
        double x = xInitial;
        double y = yInitial;
        series.add(x, y);
        for (int i = 1; i <= amountOfSteps; i++) {
            double k1 = f(x, y);
            double k2 = f(x + h / 2, y + h * k1 / 2);
            double k3 = f(x + h / 2, y + h * k2 / 2);
            double k4 = f(x + h, y + h * k3);
            y += h * (k1 + 2 * k2 + 2 * k3 + k4) / 6;
            x += h;
            series.add(x, y);
        }
        return series;
    }
}

enum PageName {
    Functions,
    ErrorsX,
    ErrorsN
}

class GUI extends JFrame{

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
    private final Solution solution;

    private XYDataset dataset;
    private ChartPanel chartPanel;
    private JPanel initialConditionsPanel;
    private JPanel checkBoxesPanel;
    private JPanel pagesPanel;
    private PageName currentPage;

    ArrayList<String> functionsOrder = new ArrayList<>();

    public GUI(Solution solution) {
        this.solution = solution;
        functionsOrder.add(EXACT_KEY);
        functionsOrder.add(EULER_KEY);
        functionsOrder.add(IMPROVED_EULER_KEY);
        functionsOrder.add(RUNGE_KUTTA_KEY);
        currentPage = PageName.Functions;
        dataset = createDataset();
    }

    private void setPageSettings() {
        ((JCheckBox)checkBoxesPanel.getClientProperty(EXACT_CB_KEY)).setVisible(currentPage == PageName.Functions);
        ((JButton)pagesPanel.getClientProperty(PAGE1_BUTTON_KEY)).setEnabled(currentPage != PageName.Functions);
        ((JButton)pagesPanel.getClientProperty(PAGE2_BUTTON_KEY)).setEnabled(currentPage != PageName.ErrorsX);
        ((JButton)pagesPanel.getClientProperty(PAGE3_BUTTON_KEY)).setEnabled(currentPage != PageName.ErrorsN);
        ((JTextField)initialConditionsPanel.getClientProperty(X_INITIAL_TF_KEY)).setEnabled(currentPage != PageName.ErrorsN);
        ((JTextField)initialConditionsPanel.getClientProperty(Y_INITIAL_TF_KEY)).setEnabled(currentPage != PageName.ErrorsN);
        ((JTextField)initialConditionsPanel.getClientProperty(X_RIGHT_BOUND_TF_KEY)).setEnabled(currentPage != PageName.ErrorsN);
        ((JTextField)initialConditionsPanel.getClientProperty(STEPS_TF_KEY)).setVisible(currentPage != PageName.ErrorsN);
        ((JTextField)initialConditionsPanel.getClientProperty(N0_TF_KEY)).setVisible(currentPage == PageName.ErrorsN);
        ((JTextField)initialConditionsPanel.getClientProperty(N_MAX_TF_KEY)).setVisible(currentPage == PageName.ErrorsN);
        ((JLabel)initialConditionsPanel.getClientProperty(STEPS_LABEL_KEY)).setVisible(currentPage != PageName.ErrorsN);
        ((JLabel)initialConditionsPanel.getClientProperty(N0_LABEL_KEY)).setVisible(currentPage == PageName.ErrorsN);
        ((JLabel)initialConditionsPanel.getClientProperty(N_MAX_LABEL_KEY)).setVisible(currentPage == PageName.ErrorsN);
    }

    private JPanel createPagesPanel() {
        JButton page1Button = new JButton("Page 1");
        JButton page2Button = new JButton("Page 2");
        JButton page3Button = new JButton("Page 3");

        page1Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentPage = PageName.Functions;
                setPageSettings();
            }
        });
        page2Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentPage = PageName.ErrorsX;
                setPageSettings();
            }
        });
        page3Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentPage = PageName.ErrorsN;
                setPageSettings();
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

    private void updateChartDataset() {
        dataset = createDataset();
        chartPanel.getChart().getXYPlot().setDataset(dataset);
        chartPanel.repaint();
    }

    private void updateGraphVisibility(String key, boolean isVisible) {
        chartPanel.getChart().getXYPlot().getRenderer().setSeriesVisible(functionsOrder.indexOf(key), isVisible);
        chartPanel.repaint();
    }

    private JPanel createInitialConditionsPanel() {
        JLabel xInitialLabel = new JLabel("x0");
        JLabel xRightBorderLabel = new JLabel("X");
        JLabel stepsLabel = new JLabel("n");
        JLabel yInitialLabel = new JLabel("y0");
        JLabel n0Label = new JLabel("n0");
        JLabel nMaxLabel = new JLabel("N");

        JTextField xInitialTF = new JTextField("0");
        JTextField xRightBorderTF = new JTextField("7");
        JTextField stepsTF = new JTextField("10");
        JTextField yInitialTF = new JTextField("0");
        JTextField n0TF = new JTextField("10");
        JTextField nMaxTF = new JTextField("20");

        xInitialTF.setMinimumSize(new Dimension(30,20));
        xRightBorderTF.setMinimumSize(new Dimension(30,20));
        stepsTF.setMinimumSize(new Dimension(30,20));
        yInitialTF.setMinimumSize(new Dimension(30,20));
        n0TF.setMinimumSize(new Dimension(30,20));
        nMaxTF.setMinimumSize(new Dimension(30,20));

        xInitialTF.getDocument().addDocumentListener(new DocumentListener() {

            void warn(DocumentEvent e) {
                try {
                    String text = e.getDocument().getText(0, e.getDocument().getLength());
                    if (!text.isEmpty()) {
                        solution.setxInitial(Double.parseDouble(text));
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
        yInitialTF.getDocument().addDocumentListener(new DocumentListener() {

            void warn(DocumentEvent e) {
                try {
                    String text = e.getDocument().getText(0, e.getDocument().getLength());
                    if (!text.isEmpty()) {
                        solution.setyInitial(Double.parseDouble(text));
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
        xRightBorderTF.getDocument().addDocumentListener(new DocumentListener() {

            void warn(DocumentEvent e) {
                try {
                    String text = e.getDocument().getText(0, e.getDocument().getLength());
                    if (!text.isEmpty()) {
                        solution.setRightBorder(Double.parseDouble(text));
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
        stepsTF.getDocument().addDocumentListener(new DocumentListener() {

            void warn(DocumentEvent e) {
                try {
                    String text = e.getDocument().getText(0, e.getDocument().getLength());
                    if (!text.isEmpty()) {
                        solution.setAmountOfSteps(Integer.parseInt(text));
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
//        n0TF.getDocument().addDocumentListener(new DocumentListener() {
//
//            void warn(DocumentEvent e) {
//                try {
//                    String text = e.getDocument().getText(0, e.getDocument().getLength());
//                    if (!text.isEmpty()) {
//                        solution.setAmountOfSteps(Integer.parseInt(text));
//                        updateChartDataset();
//                    }
//                } catch (BadLocationException ex) {
//                    ex.printStackTrace();
//                }
//            }
//
//            @Override
//            public void insertUpdate(DocumentEvent e) {
//                warn(e);
//            }
//
//            @Override
//            public void removeUpdate(DocumentEvent e) {
//                warn(e);
//            }
//
//            @Override
//            public void changedUpdate(DocumentEvent e) {
//                warn(e);
//            }
//        });
//        nMaxTF.getDocument().addDocumentListener(new DocumentListener() {
//
//            void warn(DocumentEvent e) {
//                try {
//                    String text = e.getDocument().getText(0, e.getDocument().getLength());
//                    if (!text.isEmpty()) {
//                        solution.setAmountOfSteps(Integer.parseInt(text));
//                        updateChartDataset();
//                    }
//                } catch (BadLocationException ex) {
//                    ex.printStackTrace();
//                }
//            }
//
//            @Override
//            public void insertUpdate(DocumentEvent e) {
//                warn(e);
//            }
//
//            @Override
//            public void removeUpdate(DocumentEvent e) {
//                warn(e);
//            }
//
//            @Override
//            public void changedUpdate(DocumentEvent e) {
//                warn(e);
//            }
//        });


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
        for (String function : functionsOrder) {
            switch (function) {
                case EXACT_KEY -> dataset.addSeries(solution.getExactSolution());
                case EULER_KEY -> dataset.addSeries(solution.getEulerSolution());
                case IMPROVED_EULER_KEY -> dataset.addSeries(solution.getImprovedEulerSolution());
                case RUNGE_KUTTA_KEY -> dataset.addSeries(solution.getRungeKuttaSolution());
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
        setVisible(true);
    }

}

public class Main {
    public static void main(String[] args) {
        Solution solution = new Solution();
        GUI gui = new GUI(solution);
        gui.createWindow();
    }
}
