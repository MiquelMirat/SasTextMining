/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package manager;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author miquel.mirat
 */
public class FileManager {
    
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
    
    
    
}
