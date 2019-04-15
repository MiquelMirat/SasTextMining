/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package manager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Column;
import model.Step;
import model.Table;

/**
 *
 * @author miquel.mirat
 */
public class FileManager {
    StringBuilder csv;
    public FileManager(){
        
    }
    
    public String readFile(String filename){
        // The name of the file to open.
        String fileName = filename;
        // This will reference one line at a time
        String line = null;
        String content = "";
        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while((line = bufferedReader.readLine()) != null) {
                content += line;
            }   
            bufferedReader.close(); 
        }catch(FileNotFoundException ex) {
            System.out.println("Unable to open file '" + fileName + "'");                
        }
        catch(IOException ex) {
            System.out.println("Error reading file '" + fileName + "'");                  
        }finally{
            return content;
        }
    }
    public void writeCSV(ArrayList<Step> steps) {
        csv = new StringBuilder();
        for (Step s : steps) {
            csv.append(";;;\nSTEP;"+ s.getType() +";\nOUTPUT TABLES;;;\nNAME;ESQUEMA;ALIAS;N_COLUMNAS\n");
            for (Table t : s.getOut_tables()) {
                csv.append(t.getName() + ";" + t.getEsquema() + ";" + t.getAlias() + ";" + t.getColumnas().size() + "\n");
                if (t.getColumnas().size() > 0) {
                    csv.append(";;;\nCOLUMNAS;;;\nNAME;ALIAS;ALIAS ORIGEN;TABLA ORIGEN\n");
                    for (Column c : t.getColumnas()) {
                       csv.append(c.getName() + ";" + c.getAlias() + ";" + c.getAliasOrigen() + ";" + c.getTablaOrigen().getName() + "\n");
                    }
                }
            }
            csv.append(";;;\nORIGENES;;;\nNAME;ESQUEMA;ALIAS;\n");
            for (Table t : s.getIn_tables()) {
                csv.append(t.getName() + ";" + t.getEsquema() + ";" + t.getAlias() + ";\n");
            }
            csv.append(";;;\n;FILTROS;GROUPS;ORDERS\n");
            for(int i = 0; i<s.getFilters().size(); i++){
                csv.append(";"+s.getFilters().get(i) + ";" + s.getGroupings().get(i) + ";" + s.getSorted_by().get(i) + "\n");
            }
            //System.out.println(";;;\n");
        }
        try(PrintWriter writer = new PrintWriter(new File("test.csv"))){
            writer.write(csv.toString());
        
        
        }   catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
    
    }
    
}
