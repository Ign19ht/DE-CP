import org.jfree.data.xy.XYSeries;

class DataProvider {
    private double xInitial;
    private double yInitial;
    private double rightBorder;
    private int amountOfSteps;
    private int nMin;
    private int nMax;

    private XYSeries exactSolution;
    private XYSeries eulerSolution;
    private XYSeries improvedEulerSolution;
    private XYSeries rungeKuttaSolution;

    public DataProvider() {
        xInitial = 0;
        yInitial = 0;
        rightBorder = 7;
        amountOfSteps = 10;
        nMin = 10;
        nMax = 20;
        calculateNewSolutions();
    }

    private void calculateNewSolutions() {
        Solution solution = new Solution(xInitial, yInitial, rightBorder, amountOfSteps);
        exactSolution = solution.getExactSolution();
        eulerSolution = solution.getEulerSolution();
        improvedEulerSolution = solution.getImprovedEulerSolution();
        rungeKuttaSolution = solution.getRungeKuttaSolution();
    }

    public void setRightBorder(double rightBorder) {
        this.rightBorder = rightBorder;
        calculateNewSolutions();
    }

    public void setxInitial(double xInitial) {
        this.xInitial = xInitial;
        calculateNewSolutions();
    }

    public void setyInitial(double yInitial) {
        this.yInitial = yInitial;
        calculateNewSolutions();
    }

    public void setAmountOfSteps(int amountOfSteps) {
        this.amountOfSteps = amountOfSteps;
        calculateNewSolutions();
    }

    public void setnMin(int nMin) {
        this.nMin = nMin;
    }

    public void setnMax(int nMax) {
        this.nMax = nMax;
    }

    public double getxInitial() {
        return xInitial;
    }

    public double getyInitial() {
        return yInitial;
    }

    public double getRightBorder() {
        return rightBorder;
    }

    public int getAmountOfSteps() {
        return amountOfSteps;
    }

    public int getnMin() {
        return nMin;
    }

    public int getnMax() {
        return nMax;
    }

    public XYSeries getExactSolution() {
        return exactSolution;
    }

    public XYSeries getEulerSolution() {
        return eulerSolution;
    }

    public XYSeries getImprovedEulerSolution() {
        return improvedEulerSolution;
    }

    public XYSeries getRungeKuttaSolution() {
        return rungeKuttaSolution;
    }

    public XYSeries getEulerErrorDependsX() {
        var series = new XYSeries("Euler");
        for (int i = 0; i <= amountOfSteps; i++) {
            series.add(exactSolution.getX(i), Math.abs((double) exactSolution.getY(i) - (double) eulerSolution.getY(i)));
        }
        return series;
    }

    public XYSeries getImprovedEulerErrorDependsX() {
        var series = new XYSeries("Improved Euler");
        for (int i = 0; i <= amountOfSteps; i++) {
            series.add(exactSolution.getX(i), Math.abs((double) exactSolution.getY(i) - (double) improvedEulerSolution.getY(i)));
        }
        return series;
    }

    public XYSeries getRungeKuttaErrorDependsX() {
        var series = new XYSeries("Runge-Kutta");
        for (int i = 0; i <= amountOfSteps; i++) {
            series.add(exactSolution.getX(i), Math.abs((double) exactSolution.getY(i) - (double) rungeKuttaSolution.getY(i)));
        }
        return series;
    }

    public XYSeries[] getErrorsDependsN() {
        var eulerSeries = new XYSeries("Euler");
        var improvedEulerSeries = new XYSeries("Improved Euler");
        var rungeKuttaSeries = new XYSeries("Runge-Kutta");
        for (int n = nMin; n <= nMax; n++) {
            Solution solution = new Solution(xInitial, yInitial, rightBorder, n);
            XYSeries exact = solution.getExactSolution();
            XYSeries euler = solution.getEulerSolution();
            XYSeries improvedEuler = solution.getImprovedEulerSolution();
            XYSeries rungeKutta = solution.getRungeKuttaSolution();
            double eulerErrorMax = 0;
            double improvedRulerErrorMax = 0;
            double rungeKuttaErrorMax = 0;
            for (int i = 0; i <= n; i++) {
                eulerErrorMax = Math.max(eulerErrorMax, Math.abs((double) exact.getY(i) - (double) euler.getY(i)));
                improvedRulerErrorMax = Math.max(improvedRulerErrorMax, Math.abs((double) exact.getY(i) - (double) improvedEuler.getY(i)));
                rungeKuttaErrorMax = Math.max(rungeKuttaErrorMax, Math.abs((double) exact.getY(i) - (double) rungeKutta.getY(i)));
            }
            eulerSeries.add(n, eulerErrorMax);
            improvedEulerSeries.add(n, improvedRulerErrorMax);
            rungeKuttaSeries.add(n, rungeKuttaErrorMax);
        }
        XYSeries[] result = new XYSeries[3];
        result[0] = eulerSeries;
        result[1] = improvedEulerSeries;
        result[2] = rungeKuttaSeries;
        return result;
    }
}
