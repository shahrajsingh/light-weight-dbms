import java.util.NoSuchElementException;
import java.util.Scanner;
import org.example.CommonFunction;
import org.example.Database;

public class Main {
    private static boolean isAuthenticated = false;

    /**
     * driver function to check authentication and read query;
     * @param args
     */
    public static void main(String[] args) {


        while (!isAuthenticated) {
            Scanner sc = new Scanner(System.in);
            System.out.println("1. Login || 2. Signup || 3. Exit");
            int choice;
            try {
                choice = sc.nextInt();
                sc.nextLine();
                if (choice < 1 || choice > 3)
                    continue;
                System.out.print("Username: ");
                String username = sc.next();
                System.out.println();
                System.out.print("Password: ");
                String pass = sc.next();
                Authentication auth = new Authentication(username,pass,choice);
                isAuthenticated= auth.checkUser(sc);
                continue;
            } catch (NoSuchElementException e) {
                System.out.print(e.getMessage());
            }

            if (isAuthenticated || sc.hasNextInt() && sc.nextInt() == 3) {
                break;
            }
            sc.close();
        }

        Database db = new Database();
        System.out.println("The console will now be always ready to take query inputs, type EXIT (case Sensitive) to exit program");
        String query = "";
        while(!query.equals("EXIT")){
            query = "";
            Scanner sc = new Scanner(System.in);
            query = sc.nextLine();
            db.readQuery(query);
            System.out.println("<-->");
        }
    }

}