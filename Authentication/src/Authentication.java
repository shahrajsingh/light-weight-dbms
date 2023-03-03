import org.example.CommonFunction;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * Class extends CommonFunction class i.e that is the class that contains functions commonly used by different classes
 */
public class Authentication extends CommonFunction {
    private static final String user_data_file = "user_data.txt";
    private final String username;
    private final String password;
    private String secret_answer;
    private int choice = 0;
    private int secret_question_no;
    private String[] userData;

    private final File user_data;
    private static final String[] questions = { "1. what was the name of your first pet?",
            "2. What is your mother's maiden name?", "3. Where were you born" };

    /**
     * Method to add initialise the username,password,choice variables. it also loads userDataFile and checks it.
     * @param username
     * @param password
     * @param choice
     */
    Authentication(String username, String password, int choice) {
        super();
        this.username = username;
        this.password = password;
        this.choice = choice;
        user_data = new File(System.getProperty("user.home"), user_data_file);
        checkFile(user_data, user_data_file, "");
    }

    /**
     * Method to call respective function based on choice and this also verifies if username is already taken;
     * @param sc
     * @return
     */
    public boolean checkUser(Scanner sc) {
        boolean authenticationSuccess = false;

        if (this.choice == 1) {
            authenticationSuccess = this.login(sc);
        } else if (this.choice == 2) {
            if (checkUsername()) {
                System.out.println("Username is taken");
            } else {
                authenticationSuccess = this.signup(sc);
                authenticationSuccess = false;
            }
        } else {
            System.out.println("Invalid option");
        }
        return authenticationSuccess;
    }

    /**
     *
     * @return boolean   //returns true or false by checking usernames taken;
     */
    public boolean checkUsername() {
        boolean userNameExist = false;
        try (BufferedReader br = new BufferedReader(new FileReader(user_data))) {
            String line;
            while ((line = br.readLine()) != null) {
                this.userData = line.split("\\|");
                String userName = userData[0];
                if (userName.equals(this.username)) {
                    userNameExist = true;
                    break;
                }
            }
        } catch (IOException error) {
            print_error(error.getMessage() + " line 59 ");
        }
        return userNameExist;
    };

    /**
     *
     * @return String array , returns 2factor auth quesitons;
     */
    public String[] getQuestions() {
        return questions;
    }

    /**
     * method to set 2 factor authentication
     * @param question_number integer
     * @param secret_answer string
     */
    public void setQuestions(int question_number, String secret_answer) {
        this.secret_question_no = question_number;
        this.secret_answer = secret_answer;
    }

    /**
     * method to signup user;
     * @param sc
     * @return boolean if signup success or failed
     */
    private boolean signup(Scanner sc) {
        boolean signupSuccess = false;
        System.out.println("Please enter the question number which you want to answer");
        for (String question : questions) {
            System.out.println(question);
        }
        System.out.print("Selected Question Number: ");
        this.secret_question_no = sc.nextInt();
        System.out.println("Please provide the answer of selected question");
        this.secret_answer = sc.next();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(user_data, true))) {
            bw.write(this.username + "|" + this.password + "|" + this.secret_question_no + "|" + this.secret_answer);
            bw.newLine();
            bw.close();
            System.out.println("Signup Successful. Please login");
            signupSuccess = true;
        } catch (IOException error) {
            print_error(error.getMessage() + " line 107 ");
            System.exit(1);
        }

        return true;
    }

    /**
     * Method to login user
     * @param sc
     * @return boolean true/false based on the userinfo
     */
    private boolean login(Scanner sc) {
        boolean loginSuccess = false;
        if (!checkUsername()) {
            System.out.println("The Entered username does not exit. Please check username or signup first.");
        } else {
            if (this.password.equals(userData[1])) {
                System.out.println("Please answer the following question");
                System.out.println(questions[Integer.parseInt(this.userData[2]) - 1]);
                String answer = sc.next();
                if (answer.equals(userData[3])) {
                    System.out.println("Login Success. :)");
                    loginSuccess = true;
                } else {
                    System.out.println("User authentication failed");
                }
            } else {
                System.out.println("User authentication failed");
            }
        }
        return loginSuccess;
    }
}
