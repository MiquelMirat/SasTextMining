/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package model;

import java.util.ArrayList;
import manager.Manager;

/**
 *
 * @author miquel.mirat
 */
public class Table {
    private String name;
    private String esquema;
    private String alias;
    private ArrayList<Column> columnas;
    private Manager manager = new Manager();
    
    public Table(){
        this.name = "";
        this.esquema = "";
        this.alias = "";
        this.columnas = new ArrayList<>();
    }
    public Table(String fullName){
        this.name = fullName;
        this.esquema = "";
        this.alias = "";
        this.columnas = new ArrayList<>();
    }
    //CALC OUTPUT TABLE SCHEMA
    public Table withSchema(){
        if(this.name.contains(".")){
            this.setEsquema(this.name.split("\\.")[0]);
            this.setName(this.name.split("\\.")[1]);
        }else{
            this.setEsquema("WORK");
        }
        return this;
    }
    /**
     * Método que calcula las columnas de las tablas de un step, 
     * @param s Step parametro que contiene la lista de tablas a rellenar y la sentencia que las contiene.
     */
    public void calcColumns(Step s){
        Column temp = null;
        ArrayList<Table> tablas_entrada = s.getIn_tables();
        String content = manager.chooseContentForStep(s);
        String tempContent = "";
        int count = 0;
        //System.out.println("CONTENT: "+content);
        for(char c : content.toCharArray()){
            if(c == '('){
                count++;
            }else if(c == ')'){
                count--;
            }
            if(count == 0 && c == ','){
                c = '\n';
            }
            tempContent += c;
        }
        //System.out.println("TEMPCONTENT:  "+ tempContent);
        content = tempContent;
        String[] lines = content.split("\n");
        String w1, w2, w3;
        for(String l:lines){
            temp = new Column();
            switch(l.trim().split(" ").length){
                case 1:
                    if(l.equalsIgnoreCase("")){break;}
                    System.out.println("ONE WORD--->"+l);
                    temp = new Column(l).full();
                    break;
                case 2:
                    System.out.println("TWO WORD--->"+l);
                    w1 = l.split(" ")[0]; w2 = l.split(" ")[1];
                    temp = new Column(w2).full(w1);
                    
//                    if(w1.contains(".")){
//                        temp.setName(w1.split("\\.")[1]);
//                        temp.setOrigen(w1.split("\\.")[0]);
//                    }else{
//                        temp.setName(w1.split("\\.")[1]);
//                        temp.setOrigen("undefined");
//                    }
                    break;
                case 3:
                    System.out.println("THREE WORD--->"+l);
                    l = l.trim();
                    w1 = l.split(" ")[0]; w2 = l.split(" ")[1]; w3 = l.split(" ")[2];
                    //System.out.println("W3: " +w1);
                    temp = new Column(w3).full(w1);
                    
                    break;
                case 4:
                    System.out.println("4????--->"+l);
                    break;
                default:
                    System.out.println("WHATT??"+l.split(" ").length+"--->"+l);
                    String[] words = l.split(" ");
                    String caseWhen = "";
                    w3 = words[words.length-1];
                    for(String w: words){
                        if(w.equalsIgnoreCase("as")){break;}
                        caseWhen += w + " ";
                    }
                    temp = new Column(caseWhen, w3, "undefined");
                    //System.out.println("CASE WHEEEN: "+w3);    
                
            }
            this.getColumnas().add(temp);
        }
        
        
        //String content = s instanceof ProcStep ? ((ProcStep) s).getType().equalsIgnoreCase("sql") ? ((ProcStep) s).getSelect() : ((ProcStep) s).getKeep() : ((DataStep) s).getKeep() ;
    }

    public String getName() {return name;}
    public void setName(String name) {this.name = name;}
    public String getEsquema() {return esquema;}
    public void setEsquema(String esquema) {this.esquema = esquema;}
    public String getAlias() {return alias;}
    public void setAlias(String alias) {this.alias = alias;}
    public ArrayList<Column> getColumnas() {return columnas;}
    public void setColumnas(ArrayList<Column> columnas) {this.columnas = columnas;}

    @Override
    public String toString() {
        return "name=" + name + ", esquema=" + esquema + ", alias=" + alias + ", columnas=" + columnas.size();
    }
    
    
    
    
    
}
