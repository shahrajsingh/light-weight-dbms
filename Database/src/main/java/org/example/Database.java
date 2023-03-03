package org.example;
import java.io.*;
import java.util.*;

/**
 * Class extends CommonFuciton class
 */
public class Database extends CommonFunction{

    private HashMap<String, String> tables = new HashMap<String, String>();
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
     * Method to read user query and perform operations based on that
     * @param query string query input by user
     */
    public void readQuery(String query){
        String[] operations = query.split(" ");
        switch(operations[0].toLowerCase()){
            case "create":
                if(operations[1].equalsIgnoreCase("table"))
                    createTable(query.split(" ")[2], query.substring(query.indexOf("(") + 1,query.indexOf(")")));
                break;
            case "select":
                if(operations[1].equalsIgnoreCase("*")){
                    if(tables.containsKey(operations[3])){
                        File file= new File(System.getProperty("user.home"),tables.get(operations[3]));
                        if(operations.length > 4 && operations[4].equalsIgnoreCase("where")){
                            String[] where = operations[5].split("=");
                            print_table(file,null,true,where);
                        } else if (operations.length > 4 && !operations[4].equalsIgnoreCase("where")){
                            print_error("error with query");
                        } else if (operations.length == 4){
                            print_table(file,null,true, null);
                        } else {
                            print_error("error with query");
                        }
                    }else{
                        print_error("table does not exist");
                    }
                }else {
                    String[] selected_cols = operations[1].split(",");
                    if(tables.containsKey(operations[3])){
                        File file= new File(System.getProperty("user.home"),tables.get(operations[3]));
                        if(operations.length > 4 && operations[4].equalsIgnoreCase("where")){
                            String[] where = operations[5].split("=");
                            print_table(file,selected_cols,false,where);
                        }else if (operations.length > 4 && !operations[4].equalsIgnoreCase("where")){
                            print_error("error with query");
                        }else if (operations.length == 4){
                            print_table(file,selected_cols,false, null);}
                        else {
                            print_error("error with query");
                        }
                    }else{
                        print_error("table does not exist");
                    }
                }

                break;
            case "insert":
                boolean success = false;
                try{
                    if(operations[1].equalsIgnoreCase("into")){
                        if(tables.containsKey(operations[2])){
                            if(operations[3].equalsIgnoreCase("values")){
                                String values = operations[4].substring(operations[4].indexOf("(") + 1, operations[4].indexOf(")"));
                                success = insetIntoTable(operations[2],"",values);
                            }else{
                                String cols = operations[3].substring(operations[3].indexOf("(") + 1, operations[3].indexOf(")"));
                                if(operations[4].equalsIgnoreCase("values")){
                                    String values = operations[5].substring(operations[5].indexOf("(") + 1, operations[5].indexOf(")"));
                                    System.out.println(values);
                                    success = insetIntoTable(operations[2],cols,values);
                                } else{
                                    System.out.println("looks like syntax error, please try again");
                                }
                            }
                        } else{
                            System.out.println("Looks like table doesn't exist, Please check again");
                        }
                    } else {
                        System.out.println("Looks like the query is incorrect, please check syntax, e.g INSERT INTO table_name () values ()");
                    }
                } catch (ArrayIndexOutOfBoundsException e){
                    System.out.println("looks like a syntax error");
                }

                if(success){
                    System.out.println("1 row inserted into { " + operations[2] + " } table");
                }else{
                    System.out.println("there was some error inserting value");
                }

                break;
            case "update":
                boolean updateSuccess = false;
                if(tables.containsKey(operations[1])){
                    if(operations[2].equalsIgnoreCase("set")){
                        String[] colsValues = operations[3].split(",");
                        HashMap<String,String> colVal = new HashMap<String,String>();
                        for(String colValue: colsValues){
                            String[] keyVal = colValue.split("=");
                            colVal.put(keyVal[0],keyVal[1]);
                        }
                        File table = new File(System.getProperty("user.home"),tables.get(operations[1]));
                        if(operations.length > 4 && operations[4].equalsIgnoreCase("Where")){
                            String[] where = operations[5].split("=");
                            updateSuccess = updateTable(colVal,table,where);
                        }else{
                            updateSuccess = updateTable(colVal,table, null);
                        }
                    } else {
                        print_error("check query");
                    }
                }else{
                    print_error("table not found");
                }
                if(updateSuccess){
                    System.out.println("table updated");
                }else{
                    print_error("table not updated");
                }
                break;
            case "delete":
                boolean deleteSuccess = false;
                if(operations[1].equalsIgnoreCase("from")){
                    if(tables.containsKey(operations[2])){

                        File table = new File(System.getProperty("user.home"),tables.get(operations[2]));
                        if(operations.length == 3){
                            deleteSuccess = deleterow(table, null);
                        } else if(operations.length == 5){
                            if(operations[3].equalsIgnoreCase("where")){
                                String[] where = operations[4].split("=");
                                deleteSuccess = deleterow(table,where);
                            }else{
                                print_error("syntax error");
                            }

                        }


                    }else{
                        print_error("table not found");
                    }
                }else{
                    print_error("syntax error");
                }
                if(deleteSuccess){
                    System.out.println("Delete success");
                } else{
                    print_error("error deleting");
                }
                break;
        }
    }

    /**
     * Method to create table
     * @param tableName string
     * @param structure string //the columns to be created
     */
    private void createTable(String tableName, String structure){
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
     * Insert data into table
     * @param tableName
     * @param cols
     * @param values
     * @return boolean //if operation success or not
     */
    private boolean insetIntoTable(String tableName,String cols, String values){
        boolean success = false;
        String tableFileName = tables.get(tableName);
        StringBuilder value = new StringBuilder("");
        String tableHeading = "";
        String dataLine = "";
        String[] data = values.split(",");
        File tableFile  = new File(System.getProperty("user.home"),tableFileName);
        try{
            BufferedReader br = new BufferedReader(new FileReader(tableFile));
            tableHeading = br.readLine();
            br.close();
        }catch (IOException e){
            print_error(e.getMessage() + " line 93");
        }

        String[] tableCols = tableHeading.split("\\|");

        if(cols.equals("")){
            if(data.length == tableCols.length){
                dataLine = createLine(data);
            }
            else
                System.out.println("please check statement number of columns mismatch number of values");
        }else{
            String colsArr[] = cols.split(",");
            dataLine = matcherString(tableCols,colsArr,data,false);
        }

        if(!dataLine.equals("")){
           success =  writeFile(tableFile,dataLine,true);
        }
        return success;
    }

    /**
     * Method to print table
     * @param tableFile //the table to print
     * @param cols // the cols to show
     * @param print_all if all the cols are to be printed
     * @param where // to solve where clause
     */
    private void print_table(File tableFile, String[] cols, boolean print_all , String[] where){
        try{
            BufferedReader br = new BufferedReader(new FileReader(tableFile));
            String line = "";
            line = br.readLine();
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
     * method to update table column by column
     * @param columnValue
     * @param table
     * @param where
     * @return
     */

    private boolean updateTable(HashMap<String,String> columnValue, File table,  String[] where){
        boolean updated = false;
        try {
            BufferedReader br = new BufferedReader(new FileReader(table));
            String line = br.readLine();
            HashMap<Integer,String> keyVal = new HashMap<Integer,String>();
            String[] th = line.split("\\|");
            for(int a =0; a<th.length;a++){
                if(columnValue.containsKey(th[a].replaceAll("\\s+",""))){
                    keyVal.put(a, columnValue.get(th[a].replaceAll("\\s+","")));
                }
            }
            ArrayList<String> newLines = new ArrayList<String>();
            newLines.add(line);
            System.out.println(keyVal);
            int colIndex = getColIndex(th,where[0]);
            while((line = br.readLine()) != null){
                String tempLine = "";
                if(where != null && colIndex >= 0){
                    tempLine = solveWhere(where,line,colIndex);
                }
                if(!tempLine.equals(""))
                        line = tempLine;
                String[] cols = line.split("\\|");

                String newLine = "";
                for(int a = 0;a<cols.length;a++){
                    Integer x = a;
                    if(keyVal.containsKey(x)){
                        newLine += keyVal.get(x);
                    }else{
                        newLine += cols[a].replaceAll("\\s+","");
                    }
                    if( a != cols.length - 1){
                        newLine += " | ";
                    }
                }
                newLines.add(newLine);
            }
            br.close();
            BufferedWriter bw = new BufferedWriter(new FileWriter(table,false));
            for(String newline:newLines){
                bw.write(newline);
                bw.newLine();
            }
            bw.close();
            updated = true;
        } catch (IOException e){
            print_error(e.getMessage());
        }
        return updated;
    }

    /**
     * method to delete a row in table
     * @param file
     * @param where
     * @return
     */
    private boolean deleterow(File file,String[] where){
        boolean deleteSuccess = false;
        ArrayList<String> newLines = new ArrayList<String>();
        try{
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = br.readLine();
            newLines.add(br.readLine());
            if(where != null){
                String[] th = line.split("\\|");
                int colIndex = getColIndex(th,where[0]);
                while((line = br.readLine()) != null){
                    String newLine = solveWhere(where,line,colIndex);
                    if(newLine.equals("")){
                        newLines.add(line);
                    }
                }
                br.close();
            }
            BufferedWriter bw = new BufferedWriter(new FileWriter(file,false));
            for(String newline:newLines){
                bw.write(newline);
                bw.newLine();
            }
            bw.close();
            deleteSuccess = true;
        } catch (IOException e){
            print_error(e.getMessage());
        }
        return deleteSuccess;
    }

    /**
     * method to get the col index of the reqeusted column based on where clause
     * @param headings
     * @param where
     * @return
     */
    private int getColIndex (String[] headings, String where){
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
    private String matcherString(String[] main, String[] matcher,String[] matcherValue , boolean appendDelimiterForMatcher){
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
