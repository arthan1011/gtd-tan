import java.util.Scanner;

/**
 * Created by arthan on 15.04.2017. | Project gtd-tan
 */
public class App {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.print("Type here: ");
        String s = scanner.nextLine();
        System.out.println("You typed " + "'" + s + "'");

        if (System.console() == null) {
            System.out.println("No console");
        } else {
            System.out.println("Console exists");
        }
    }
}
