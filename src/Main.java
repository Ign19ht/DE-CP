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

    public void setRightBorder(int rightBorder) {
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

class GUI extends JFrame{

    private final String EXACT_KEY = "exactKey";
    private final String EULER_KEY = "eulerKey";
    private final String IMPROVED_EULER_KEY = "improvedEulerKey";
    private final String RUNGE_KUTTA_KEY = "rungeKuttaKey";
    private final Solution solution;

    private XYDataset dataset;
    private ChartPanel chartPanel;
    private JPanel initialConditionsPanel;
    private JPanel checkBoxesPanel;
    private JPanel pagesPanel;

    ArrayList<String> functionsOrder = new ArrayList<>();

    public GUI(Solution solution) {
        this.solution = solution;
        functionsOrder.add(EXACT_KEY);
        functionsOrder.add(EULER_KEY);
        functionsOrder.add(IMPROVED_EULER_KEY);
        functionsOrder.add(RUNGE_KUTTA_KEY);
        dataset = createDataset();
    }

    private JPanel createPagesPanel() {
        JButton Page1Button = new JButton("Page 1");
        JButton Page2Button = new JButton("Page 2");
        JButton Page3Button = new JButton("Page 3");

        JPanel pages = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel pagesBlock = new JPanel(new GridLayout(1, 3));
        pagesBlock.add(Page1Button);
        pagesBlock.add(Page2Button);
        pagesBlock.add(Page3Button);
        pages.add(pagesBlock);

        return pages;
    }

    private void updateChartDataset() {
        dataset = createDataset();
        chartPanel.getChart().getXYPlot().setDataset(dataset);
        chartPanel.repaint();
    }

    private void updateVisibility(String key, boolean isVisible) {
        chartPanel.getChart().getXYPlot().getRenderer().setSeriesVisible(functionsOrder.indexOf(key), isVisible);
        chartPanel.repaint();
//        setSeriesLinesVisible(i, isVisible[i]);
    }

    private JPanel createInitialConditionsPanel() {
        JLabel xInitialLabel = new JLabel("x0");
        JLabel xRightBorderLabel = new JLabel("X");
        JLabel stepsLabel = new JLabel("n");
        JLabel yInitialLabel = new JLabel("y0");

        JTextField xInitialTF = new JTextField("0");
        JTextField xRightBorderTF = new JTextField("7");
        JTextField stepsTF = new JTextField("10");
        JTextField yInitialTF = new JTextField("0");

        xInitialTF.setMinimumSize(new Dimension(30,20));
        xRightBorderTF.setMinimumSize(new Dimension(30,20));
        stepsTF.setMinimumSize(new Dimension(30,20));
        yInitialTF.setMinimumSize(new Dimension(30,20));

        xInitialTF.getDocument().addDocumentListener(new DocumentListener() {

            void warn(DocumentEvent e) {
                try {
                    String text = e.getDocument().getText(0, e.getDocument().getLength());
                    if (!text.isEmpty()) {
                        solution.setxInitial(Integer.parseInt(text));
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
                        solution.setyInitial(Integer.parseInt(text));
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
                        solution.setRightBorder(Integer.parseInt(text));
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
                        .addComponent(stepsTF)));
        initialConditionBlock.setHorizontalGroup(initialConditionBlock.createSequentialGroup()
                .addGroup(initialConditionBlock.createParallelGroup()
                        .addComponent(xInitialLabel)
                        .addComponent(yInitialLabel)
                        .addComponent(xRightBorderLabel)
                        .addComponent(stepsLabel))
                .addGroup(initialConditionBlock.createParallelGroup()
                        .addComponent(xInitialTF)
                        .addComponent(yInitialTF)
                        .addComponent(xRightBorderTF)
                        .addComponent(stepsTF)));
        initialConditionBlock.linkSize(SwingConstants.VERTICAL, xInitialTF, yInitialTF, xRightBorderTF, stepsTF);
        initialCondition.setLayout(initialConditionBlock);

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
                updateVisibility(EXACT_KEY, e.getStateChange() == ItemEvent.SELECTED);
            }
        });
        eulerCB.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                updateVisibility(EULER_KEY, e.getStateChange() == ItemEvent.SELECTED);
            }
        });
        improvedEulerCB.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                updateVisibility(IMPROVED_EULER_KEY, e.getStateChange() == ItemEvent.SELECTED);
            }
        });
        rungeKuttaCB.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                updateVisibility(RUNGE_KUTTA_KEY, e.getStateChange() == ItemEvent.SELECTED);
            }
        });

        JPanel checkBoxes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JPanel checkBoxesBlock = new JPanel(new GridLayout(4, 1));
        checkBoxesBlock.add(exactCB);
        checkBoxesBlock.add(eulerCB);
        checkBoxesBlock.add(improvedEulerCB);
        checkBoxesBlock.add(rungeKuttaCB);
        checkBoxes.add(checkBoxesBlock);

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
