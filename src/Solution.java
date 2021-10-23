import org.jfree.data.xy.XYSeries;

class Solution {
    private final double xInitial;
    private final double yInitial;
    private final int amountOfSteps;
    private final double h;
    private final double parameter;

    public Solution(double xInitial, double yInitial, double rightBorder, int amountOfSteps) {
        this.xInitial = xInitial;
        this.yInitial = yInitial;
        this.amountOfSteps = amountOfSteps;
        h = (rightBorder - xInitial) / amountOfSteps;
        parameter = (yInitial - Math.exp(xInitial)) / Math.exp(-xInitial);
    }

    private double f(double x, double y) {
        return 2 * Math.exp(x) - y;
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