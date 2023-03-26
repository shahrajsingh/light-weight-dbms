package org.example;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class DbFunctions extends Database{

    public DbFunctions(){
        super();
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
}
