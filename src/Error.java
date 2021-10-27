import org.jfree.data.xy.XYSeries;

public class Error {
    public XYSeries getEulerLTE(XYSeries exactSolution, XYSeries eulerSolution, int amountOfSteps) {
        var series = new XYSeries("Euler");
        for (int i = 0; i <= amountOfSteps; i++) {
            series.add(exactSolution.getX(i), Math.abs((double) exactSolution.getY(i) - (double) eulerSolution.getY(i)));
        }
        return series;
    }

    public XYSeries getImprovedEulerLTE(XYSeries exactSolution, XYSeries improvedEulerSolution, int amountOfSteps) {
        var series = new XYSeries("Improved Euler");
        for (int i = 0; i <= amountOfSteps; i++) {
            series.add(exactSolution.getX(i), Math.abs((double) exactSolution.getY(i) - (double) improvedEulerSolution.getY(i)));
        }
        return series;
    }

    public XYSeries getRungeKuttaErrorLTE(XYSeries exactSolution, XYSeries rungeKuttaSolution, int amountOfSteps) {
        var series = new XYSeries("Runge-Kutta");
        for (int i = 0; i <= amountOfSteps; i++) {
            series.add(exactSolution.getX(i), Math.abs((double) exactSolution.getY(i) - (double) rungeKuttaSolution.getY(i)));
        }
        return series;
    }

    public XYSeries[] getGTEs(double xInitial, double yInitial, double rightBorder, int nMin, int nMax) {
        var eulerSeries = new XYSeries("Euler");
        var improvedEulerSeries = new XYSeries("Improved Euler");
        var rungeKuttaSeries = new XYSeries("Runge-Kutta");
        for (int n = nMin; n <= nMax; n++) {
            Solution solution = new Solution(xInitial, yInitial, rightBorder, n);
            XYSeries exact = solution.getExactSolution();
            XYSeries euler = solution.getEulerSolution();
            XYSeries improvedEuler = solution.getImprovedEulerSolution();
            XYSeries rungeKutta = solution.getRungeKuttaSolution();
            double eulerErrorMax = getEulerLTE(exact, euler, n).getMaxY();
            double improvedEulerErrorMax = getImprovedEulerLTE(exact, improvedEuler, n).getMaxY();
            double rungeKuttaErrorMax = getRungeKuttaErrorLTE(exact, rungeKutta, n).getMaxY();
            eulerSeries.add(n, eulerErrorMax);
            improvedEulerSeries.add(n, improvedEulerErrorMax);
            rungeKuttaSeries.add(n, rungeKuttaErrorMax);
        }
        XYSeries[] result = new XYSeries[3];
        result[0] = eulerSeries;
        result[1] = improvedEulerSeries;
        result[2] = rungeKuttaSeries;
        return result;
    }
}
