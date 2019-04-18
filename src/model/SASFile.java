/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package model;

import java.util.ArrayList;

/**
 *
 * @author miquel.mirat
 */
public class SASFile {
    private String content;
    private String filename;
    private ArrayList<Step> steps = new ArrayList<>();

    public SASFile(String content,String filename) {
        this.content = content;
        this.filename = filename;
    }
    
    
    public void calcSteps(){
        boolean inStep = false;
        ArrayList<Step> steps = new ArrayList<>();
        String[] words = this.content.split(" ");
        //System.out.println(words.length);
        Step temp = null;
        
        for (String w: words){
            if(w.equalsIgnoreCase("proc")){
                //System.out.println("proooc");
                temp = new ProcStep("PROC",this.filename);
                inStep = true;
            }
            if(w.equalsIgnoreCase("data")){
                temp = new DataStep("DATA", this.filename);
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
        System.out.println(this.steps.size() +" STEPS IN FILE:  "+this.filename);
        //return steps;
    }
    public void extractData(){
        for (Step s : this.getSteps()) {
            //archivamos los comentarios
            s.archiveComments();
            //dividimos en sus partes
            s.divideStatements();
            //calculamos sus tablas de salida
            s.calcOutputTables();
            //calculamos sus tablas de entrada
            s.calcInputTables();
            //para cada tabla de salida de cada step
            for (Table t : s.getOut_tables()) {
                //calculamos la columnas
                t.calcColumns(s);
                //NOTA-- en los bloques transpose, las columnas son nulas siempre...
                //FALTA ADECUARDLO A LOS CASE WHEN

            }
            s.calcFilters();
            s.calcGroupings();
            s.calcSortings();
            s.fillArrays();
            
        }
    }
    
    

    public String getContent() {return content;}
    public void setContent(String content) {this.content = content;}
    public ArrayList<Step> getSteps() {return steps;}
    public void setSteps(ArrayList<Step> steps) {this.steps = steps;}

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
    

    
    
    
    
}
