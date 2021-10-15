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
import java.awt.*;
import java.util.Arrays;

class Solution {

    private final static int LEFT_BORDER = 0;
    private final static int RIGHT_BORDER = 7;
    private final static double X_INITIAL = 0.;
    private final static double Y_INITIAL = 0.;
    private static int amountOfSteps = 15;
    private static double h = (double) (RIGHT_BORDER - LEFT_BORDER) / amountOfSteps;

    private static double f(double x, double y) {
        return 2 * Math.exp(x) - y;
    }

    public void changeAmountOfSteps(int n) {
        amountOfSteps = n;
        h = (double) (RIGHT_BORDER - LEFT_BORDER) / amountOfSteps;
    }

    public XYSeries getExactSolution() {
        var series = new XYSeries("Exact");
        double x = LEFT_BORDER;
        for (int i = 0; i <= amountOfSteps; i++) {
            series.add(x, Math.exp(x) - Math.exp(-x));
            x += h;
        }
        return series;
    }

    public XYSeries getEulerSolution() {
        var series = new XYSeries("Euler");
        double x = X_INITIAL;
        double y = Y_INITIAL;
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
        double x = X_INITIAL;
        double y = Y_INITIAL;
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
        double x = X_INITIAL;
        double y = Y_INITIAL;
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

    private final Solution solution;

    private boolean[] isVisible;

    public GUI(Solution solution) {
        this.solution = solution;
        isVisible = new boolean[4];
        Arrays.fill(isVisible, true);
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

    private JPanel createInitialConditionsPanel() {
        JLabel xInitialLabel = new JLabel("x0");
        JLabel xRightBorderLabel = new JLabel("X");
        JLabel stepsLabel = new JLabel("n");
        JLabel yInitialLabel = new JLabel("y0");

        JTextField xInitialTF = new JTextField("0");
        xInitialTF.setMinimumSize(new Dimension(30,20));
        JTextField xRightBorderTF = new JTextField("7");
        xRightBorderTF.setMinimumSize(new Dimension(30,20));
        JTextField stepsTF = new JTextField("10");
        stepsTF.setMinimumSize(new Dimension(30,20));
        JTextField yInitialTF = new JTextField("0");
        yInitialTF.setMinimumSize(new Dimension(30,20));

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
        JCheckBox ExactCB = new JCheckBox("Exact", true);
        JCheckBox EulerCB = new JCheckBox("Euler", true);
        JCheckBox ImprovedEulerCB = new JCheckBox("IE", true);
        JCheckBox RungeKuttaCB = new JCheckBox("RK", true);

        JPanel checkBoxes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JPanel checkBoxesBlock = new JPanel(new GridLayout(4, 1));
        checkBoxesBlock.add(ExactCB);
        checkBoxesBlock.add(EulerCB);
        checkBoxesBlock.add(ImprovedEulerCB);
        checkBoxesBlock.add(RungeKuttaCB);
        checkBoxes.add(checkBoxesBlock);

        return checkBoxes;
    }

    private XYDataset createDataset() {
        var dataset = new XYSeriesCollection();
        dataset.addSeries(solution.getExactSolution());
        dataset.addSeries(solution.getEulerSolution());
        dataset.addSeries(solution.getImprovedEulerSolution());
        dataset.addSeries(solution.getRungeKuttaSolution());
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

        for (int i = 0; i < 4; i++) renderer.setSeriesLinesVisible(i, isVisible[i]);

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

        XYDataset dataset = createDataset();
        JFreeChart chart = createChart(dataset);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(500, 500));

        Container container = getContentPane();

        container.add(createPagesPanel(), BorderLayout.NORTH);
        container.add(createInitialConditionsPanel(), BorderLayout.WEST);
        container.add(chartPanel, BorderLayout.CENTER);
        container.add(createCheckBoxesPanel(), BorderLayout.EAST);

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
