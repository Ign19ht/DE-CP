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
    private XYSeries eulerLTE;
    private XYSeries improvedEulerLTE;
    private XYSeries rungeKuttaLTE;
    private XYSeries eulerGTE;
    private XYSeries improvedEulerGTE;
    private XYSeries rungeKuttaGTE;

    public DataProvider() {
        xInitial = 0;
        yInitial = 0;
        rightBorder = 7;
        amountOfSteps = 10;
        nMin = 10;
        nMax = 20;
        getAllSolutions();
        getAllErrors();
    }

    private void getAllSolutions() {
        Solution solution = new Solution(xInitial, yInitial, rightBorder, amountOfSteps);
        exactSolution = solution.getExactSolution();
        eulerSolution = solution.getEulerSolution();
        improvedEulerSolution = solution.getImprovedEulerSolution();
        rungeKuttaSolution = solution.getRungeKuttaSolution();
    }

    private void getAllErrors() {
        Error error = new Error();
        eulerLTE = error.getEulerLTE(exactSolution, eulerSolution, amountOfSteps);
        improvedEulerLTE = error.getImprovedEulerLTE(exactSolution, improvedEulerSolution, amountOfSteps);
        rungeKuttaLTE = error.getRungeKuttaErrorLTE(exactSolution, rungeKuttaSolution, amountOfSteps);
        XYSeries[] GTEs = error.getGTEs(xInitial, yInitial, rightBorder, nMin, nMax);
        eulerGTE = GTEs[0];
        improvedEulerGTE = GTEs[1];
        rungeKuttaGTE = GTEs[2];
    }

    public void setRightBorder(double rightBorder) {
        this.rightBorder = rightBorder;
        getAllSolutions();
        getAllErrors();
    }

    public void setxInitial(double xInitial) {
        this.xInitial = xInitial;
        getAllSolutions();
        getAllErrors();
    }

    public void setyInitial(double yInitial) {
        this.yInitial = yInitial;
        getAllSolutions();
        getAllErrors();
    }

    public void setAmountOfSteps(int amountOfSteps) {
        this.amountOfSteps = amountOfSteps;
        getAllSolutions();
        getAllErrors();
    }

    public void setnMin(int nMin) {
        this.nMin = nMin;
        getAllErrors();
    }

    public void setnMax(int nMax) {
        this.nMax = nMax;
        getAllErrors();
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

    public XYSeries getEulerLTE() {
        return eulerLTE;
    }

    public XYSeries getImprovedEulerLTE() {
        return improvedEulerLTE;
    }

    public XYSeries getRungeKuttaLTE() {
        return rungeKuttaLTE;
    }

    public XYSeries getEulerGTE() {
        return eulerGTE;
    }

    public XYSeries getImprovedEulerGTE() {
        return improvedEulerGTE;
    }

    public XYSeries getRungeKuttaGTE() {
        return rungeKuttaGTE;
    }
}
