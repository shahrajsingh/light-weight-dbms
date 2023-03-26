package org.example;
import java.io.*;
import java.util.*;

/**
 * Class extends CommonFuciton class
 */
public class Database extends CommonFunction{

    protected HashMap<String, String> tables = new HashMap<String, String>();
    private static final String database_data_file = "database_data.txt";
    private File database_data;

    /**
     * Method to check and load databse and tables;
     */
    public Database(){
        database_data = new File(System.getProperty("user.home"), database_data_file);
        database_data = checkFile(database_data, database_data_file,"A new Database has been created you can now create tables and access them");
        System.out.println("Database Loaded");
        loadTables();
    }

    /**
     * Method to create table
     * @param tableName string
     * @param structure string //the columns to be created
     */
    protected void createTable(String tableName, String structure){
        String tableFileName = tableName + ".txt";
        if(tables.containsKey(tableName)){
            System.out.println("Table already created, Select new table name!!");
        } else{
            String[] cols = structure.split(",");
            File file = createFile(tableFileName, "table { "+ tableName +" } created");
            tables.put(tableName,tableFileName);
            String th = createLine(cols);
            writeFile(file, th,true);
            storeTableDetails(tableName,tableFileName);
        }

    }

    /**
     * Method to print table
     * @param tableFile //the table to print
     * @param cols // the cols to show
     * @param print_all if all the cols are to be printed
     * @param where // to solve where clause
     */
    protected void print_table(File tableFile, String[] cols, boolean print_all, String[] where){
        try{
            BufferedReader br = new BufferedReader(new FileReader(tableFile));
            String line = "";
            line = br.readLine();
            if(line == null || line.equals(""))
            {
                print_error("The table is empty <-->");
                return;
            }
            String[] headings = line.split("\\|");
            ArrayList<Integer> colsNumbers = new ArrayList<Integer>();
            int matchIndex = -1;
            if(where != null){
                for(int a = 0;a < headings.length;a++){
                    if(headings[a].replaceAll("\\s+","").equals(where[0])){
                        matchIndex = a;
                        break;
                    }
                }
                if(matchIndex < 0){
                    print_error("query not correct");
                }
            } else{
                matchIndex = 0;
            }
            if(print_all){
                System.out.println(line);
                while((line = br.readLine()) != null && matchIndex >= 0){
                        if(where != null){
                            line = solveWhere(where,line, matchIndex);
                        }
                        if(!line.equals(""))
                            System.out.println(line);
                    }
                    System.out.println("<-- table end -->");
                    br.close();
            } else {
                System.out.println(createLine(cols));
                int colsInd = 0;
                for(int a = 0;a < headings.length;a++){
                    if( colsInd < cols.length && headings[a].replaceAll("\\s+","").equals(cols[colsInd])){
                        colsNumbers.add(a);
                        colsInd++;
                    }
                }

                while((line = br.readLine()) != null){

                    line = solveWhere(where,line,matchIndex);
                    if(line.equals("")){
                        continue;
                    } else{
                        String[] new_line = line.split("\\|");
                        String line_out = "";
                        int colInd = 0;
                        for(int a = 0;a< new_line.length;a++){
                            if (colInd >= cols.length){
                                break;
                            }
                            if(colsNumbers.contains(a) && colInd < cols.length){
                                line_out += new_line[a];
                                line_out += " | ";
                                colInd++;
                            }
                        }
                        System.out.println(line_out);
                    }
                }
            }
        } catch (IOException e){
            print_error(e.getMessage());
        }

    }

    /**
     * method to get the col index of the reqeusted column based on where clause
     * @param headings
     * @param where
     * @return
     */
    protected int getColIndex(String[] headings, String where){
        int colIndex = -1;
        for(int a = 0;a < headings.length;a++){
            if(headings[a].replaceAll("\\s+","").equals(where)){
                colIndex = a;
                break;
            }
        }
        return colIndex;
    }

    /**
     * commone method to create a  table row line
     * @param array // array of string i.e values of different columns;
     * @return
     */
    public String createLine(String[] array){
        StringBuilder line = new StringBuilder("");
        int arrLen = array.length;
        for( int a = 0;a < arrLen;a++){
            line.append(array[a]);
            if(a != array.length - 1){
                line.append(" | ");
            }
        }
        return line.toString();
    }


    /**
     * Method to match columns with query input columns and create a row line
     * @param main String[]//all columns
     * @param matcher String[] // the columns to match with
     * @param matcherValue String[] //value of all the matching columns
     * @param appendDelimiterForMatcher // append columns sepereator
     * @return String // the new formed table row line
     */
    protected String matcherString(String[] main, String[] matcher, String[] matcherValue, boolean appendDelimiterForMatcher){
        int len = main.length;
        int matchInd = 0;
        StringBuilder bu = new StringBuilder("");
        for(int a = 0;a < len;a++){
            if( matchInd < matcher.length && main[a].replaceAll("\\s+","").equals(matcher[matchInd].replaceAll("\\s+",""))){
                bu.append(matcherValue[matchInd]);
                matchInd++;
                if(appendDelimiterForMatcher && a != len-1){
                    bu.append(" | ");
                }
            }
            if(a != len - 1 && !appendDelimiterForMatcher){
                bu.append(" | ");
            }

        }

        if(matchInd < matcherValue.length ){
            return "";
        }

        return bu.toString();
    }

    /**
     * store table details in database file
     * @param tableName string
     * @param tableFileName string
     */
    private void storeTableDetails(String tableName, String tableFileName){
        String tableDetailsObj = tableName + ":" + tableFileName;
        writeFile(database_data,tableDetailsObj,true);
    }

    /**
     * method to solve the where condition
     * @param where string array extracted by breaking elemnts after where clause based on =
     * @param line string the current line the is to be checked fot where condition
     * @param index integer to check for the column number directly
     * @return
     */

    public String solveWhere(String[] where,String line, int index){
        if(where == null){
            return "";
        }
        String value = where[1];
        String[] data = line.split("\\|");
        if(data[index].replaceAll("\\s+","").equals(value))
            return line;
        else
            return "";
    }

    /**
     * method to load tables in memmory
     */
    private void loadTables(){
        try (BufferedReader br = new BufferedReader(new FileReader(database_data))){
            String line;
            while ((line = br.readLine()) != null){
                String[] tableNames = line.split(":");
                tables.put(tableNames[0], tableNames[1]);
            }
        } catch (IOException e){
            print_error(e.getMessage() + " in database");
        }
    }

}
