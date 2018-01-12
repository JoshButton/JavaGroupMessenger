package Client;

public class clientTester {

    public static void main(String[] args) {
        try {
            Communication c = new Communication();

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }
}
