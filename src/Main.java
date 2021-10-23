public class Main {
    public static void main(String[] args) {
        GUI gui = new GUI(new DataProvider());
        gui.createWindow();
        gui.setVisible(true);
    }
}
