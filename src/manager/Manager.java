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
    
    public Manager(){
        
    }
    
    public ArrayList<Step> calcSteps(String rawContent){
        boolean inStep = false;
        ArrayList<Step> steps = new ArrayList<>();
        String[] words = rawContent.split(" ");
        
        Step temp = null;
        
        for (String w: words){
            if(w.equalsIgnoreCase("proc")){
                temp = new ProcStep();
                inStep = true;
            }
            if(w.equalsIgnoreCase("data")){
                temp = new DataStep();
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
        return steps;
    }
    
}
