/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package model;

/**
 *
 * @author miquel.mirat
 */
public class DataStep extends Step{
    private String data;
    private String set;
    private String keep;
    private String drop;
    
    
    
    public DataStep(){
        super();
        this.data = "";
        this.set = "";
        this.keep = "";
        this.drop = "";
    }
       
    
    @Override
    public void divideStatements(){
        String[] words = this.getRawContent().split(" ");
        //System.out.println(words.length);
        boolean data = false, set = false, keep = false, drop = false;
        for(String w: words){
            if(w.equalsIgnoreCase("DATA")){ data = true; }
            if(w.equalsIgnoreCase("SET")){ set = true; }
            if(w.toUpperCase().contains("KEEP=")){ keep = true; data = false; drop = false;}
            if(w.toUpperCase().contains("DROP=")){ drop = true; data = false; keep = false;}
            if(w.equalsIgnoreCase(";")){ data = false; set = false; keep = false; drop = false; }
            
            if(data) {this.setData(this.getData() + w + " "); System.out.println(this.data);}
            if(set)  {this.setSet(this.getSet() + w + " "); System.out.println(this.set);}
            if(keep) {this.setKeep(this.getKeep() + w + " "); System.out.println(this.keep);}
            if(drop) {this.setDrop(this.getDrop() + w + " "); System.out.println(this.drop);}
        }
        
    }

    @Override
    public void calcOutputTables() {
        //System.out.println(this.getRawContent());
        if(this.getSet().equals("")){
            System.out.println("no hay na");
        }else{
            System.out.println("hay algo");
        }
        
    }
    

    public String getData() {return data;}
    public void setData(String data) {this.data = data;}
    public String getSet() {return set;}
    public void setSet(String set) {this.set = set;}
    public String getKeep() {return keep;}
    public void setKeep(String keep) {this.keep = keep;}
    public String getDrop() {return drop;}
    public void setDrop(String drop) {this.drop = drop;}

    @Override
    public String toString() {
        return "DataStep{" + "\ndata=" + data + ", \nset=" + set + ", \nkeep=" + keep + ", \ndrop=" + drop + '}';
    }
    
    
    
    
}
