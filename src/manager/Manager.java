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
    
    ArrayList<Step> steps = new ArrayList<>();
    FileManager fm;
    String content;
    
    public Manager(){
        this.fm = new FileManager();
        this.content = "";
    }
    
    public void read(String filename){
        this.content = fm.readFile(filename);
    }
    
    
    
    
    public String chooseContentForStep(Step s){
        String content = "";
        if(s instanceof ProcStep){
            if(((ProcStep) s).getProcType().equalsIgnoreCase("sql")){
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
    
    
    public void /* ArrayList<Step> */calcSteps(){
        boolean inStep = false;
        ArrayList<Step> steps = new ArrayList<>();
        String[] words = this.content.split(" ");
        
        Step temp = null;
        
        for (String w: words){
            if(w.equalsIgnoreCase("proc")){
                temp = new ProcStep("PROC");
                inStep = true;
            }
            if(w.equalsIgnoreCase("data")){
                temp = new DataStep("DATA");
                inStep = true;
            }
            if(w.equalsIgnoreCase("quit") || w.equalsIgnoreCase("run")){
                steps.add(temp);
                inStep = false;
            }
            if(inStep){
                temp.setRawContent(temp.getRawContent() + " " + w);
            }
        }
        this.steps = steps;
        //return steps;
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
