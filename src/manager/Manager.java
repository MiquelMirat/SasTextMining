/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package manager;

import java.util.ArrayList;
import model.DataStep;
import model.ProcStep;
import model.Step;

/**
 *
 * @author miquel.mirat
 */
public class Manager {
    private static Manager instance;
    ArrayList<Step> steps = new ArrayList<>();
    FileManager fm;
    String content;
    
    private Manager(){
    }
    public  static Manager getInstance() {
 
    if (instance==null) {
        instance= new Manager();
    }
        return instance;
    }
    
    public String prepareInput(String fileContent){
        String temp = fileContent;
        //System.out.println("FIRST:"+ temp);
        temp = temp.replace("\n"," ")
                   .replace("\r","")
                   .replace("\t","")
                   .replace(" =", "=")
                   .replace("= ", "=")
                   .replace(";"," ;")
                   .replace(";","; ")
                   .replace("*/","*/ ")
                   .replace("SELECT", "SELECT ")
                   .replace("(RENAME=", " (RENAME=") //CAMBIO
                   .replaceAll("\\s+", " ");
        
        //System.out.println("SEC :"+ temp);
        String formatted = "";
        int count = 0;
        for(char c: temp.toCharArray()){
            if(c == '('){count++;}
            if(c == ')'){count--;}
            if(count < 0){count = 0;}
            if(count != 0){//DENTRO DE PARENTESIS
                c = c == ' ' ? '@' : c;//si es un espacio se pone un @
            }
            formatted += c;
        }
        this.content = formatted;
        return formatted;
        //System.out.println("THIRD:"+ this.content);
    }
  
    
    
    //content que contiene las columnas del step
    public String chooseContentForStep(Step s){
        String content = "";
        if(s instanceof ProcStep){
            if(((ProcStep) s).getProcType().equalsIgnoreCase("sql")){
                //System.out.println("SELECT?:"+((ProcStep) s).getSelect().trim());
                content = ((ProcStep) s).getSelect().substring(6).trim();
            }else{
                content = ((ProcStep) s).getKeep().trim();
                if(content.contains("@")){
                    //ELIMINAMOS LOS @                      LOS PARENTESIS                             EL PREFIJO KEEP        
                    content = content.replaceAll("@", "\n").replaceAll("\\(", "").replaceAll("\\)", "").substring(5);
                }
            }
        }else{
            content = ((DataStep) s).getKeep().trim();
        }
        return content;
    }
    
    public static String tabbed(String text){
        text = text.trim();
        if(text.length() > 23){
            return text;
        }else if(text.length() > 15){
            return text  +  "\t";
        }else if(text.length() > 9){
            return text + "\t\t";
        }else if(text.length() > 1){
            return text + "\t\t\t";
        }else{
            return text + "\t\t\t\t";
        }
    }
    public static String withRightPadding(String text){
        int n_spaces = 24 - text.length();
        String spaces = "";
        for(int i = 0; i<n_spaces;i++){
            spaces += " ";
        }
        return text + spaces;
    }
    
    
    
    public ArrayList<Step> getSteps() {return steps;}
    public void setSteps(ArrayList<Step> steps) {this.steps = steps;}
    public String getContent() {return content;}
    public void setContent(String content) {this.content = content;}
    
    
}
