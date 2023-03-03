package org.example;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;

public class CommonFunction {

    /**
     *
     * @param message string to print
     */
    public void print_error(String message) {
        System.out.println(message + " Please try again. :(");
    }

    /**
     * Method to check if file is presen
     * @param file // target file
     * @param fileName //file name
     * @param successMessage string // message to print on screen after success
     * @return File //new created file or existing file
     */
    public File checkFile(File file, String fileName, String successMessage) {
        if (!file.exists()) {
            try {
                file = new File(System.getProperty("user.home"), fileName);
                boolean file_created = file.createNewFile();
                if(Objects.equals(successMessage, ""))
                    System.out.println("A new Database has been created you can now create tables and access them");
                else
                    System.out.println(successMessage);
                if (!file_created) {
                    throw new IOException("Error creating new File");
                }
            } catch (IOException error) {
                print_error(error.getMessage() + "line 78 ");
                System.exit(1);

            }
        }
        return file;
    }

    /**
     * Method to create new file
     * @param filename
     * @param successMessage
     * @return
     */
    public File createFile(String filename, String successMessage){
        File file = new File(System.getProperty("user.home"), filename);
        try {
            if(file.createNewFile()){
                System.out.println(successMessage);
            } else {
                System.out.println("error -> entered name already exists" );
            }

        } catch (IOException e){
            print_error(e.getMessage());
        }
        return file;
    }

    /**
     * method to write into file
     * @param file
     * @param line
     * @param append
     * @return
     */
    public boolean writeFile(File file, String line, boolean append){
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file,append));
            bw.write(line);
            bw.newLine();
            bw.close();
        } catch (IOException e){
            print_error(e.getMessage());
            return false;
        }
        return true;
    }


}
