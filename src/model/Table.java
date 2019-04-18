/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import Exceptions.EmptyStatementException;
import java.util.ArrayList;
import manager.Manager;

/**
 *
 * @author miquel.mirat
 */
public class Table {
    private final String BLUE = "\u001B[34m";
    private final String NONE = "\u001B[0m";
    private String name;
    private String esquema;
    private String alias;
    private ArrayList<Column> columnas;
    private Manager manager = Manager.getInstance();

    public Table() {
        this.name = "";
        this.esquema = "";
        this.alias = "";
        this.columnas = new ArrayList<>();
    }

    public Table(String name, String esquema, String alias) {
        this.name = name;
        this.esquema = esquema;
        this.alias = alias;
    }
   

    public Table(String fullName) {
        this.name = fullName;
        this.esquema = "";
        this.alias = "undefined";
        this.columnas = new ArrayList<>();
    }

    //CALC OUTPUT TABLE SCHEMA
    public Table withSchema() {
        if (this.name.contains(".")) {
            this.setEsquema(this.name.split("\\.")[0]);
            this.setName(this.name.split("\\.")[1]);
        } else {
            this.setEsquema("WORK");
        }
        return this;
    }

    /**
     * Método que calcula las columnas de las tablas de un step,
     *
     * @param s Step parametro que contiene la lista de tablas a rellenar y la
     * sentencia que las contiene.
     */
    public void calcColumns(Step s) {
        Column temp = null;
        ArrayList<Table> tablas_entrada = s.getIn_tables();
        String content = manager.chooseContentForStep(s);
        String tempContent = "";
        int count = 0;
        //System.out.println("CONTENT: "+content);
        for (char c : content.toCharArray()) {
            if (c == '(') {
                count++;
            } else if (c == ')') {
                count--;
            }
            if (count == 0 && c == ',') {
                c = '\n';
            }
            tempContent += c;
        }
        //System.out.println("TEMPCONTENT:  " + tempContent);
        content = tempContent;
        try {
            if (tempContent.equalsIgnoreCase("")) {
                throw new EmptyStatementException("NO COLUMNS EXCEPTION");
            }
            String[] lines = content.split("\n");
            String[] words;
            String caseWhen = "";
            String w1, w2, w3;
            for (String l : lines) {
                //System.out.println("FULL LINE: " + l);
                temp = new Column();
                switch (l.trim().split(" ").length) {
                    case 1:
                        if (l.equalsIgnoreCase("")) {
                            break;
                        }
                        //System.out.println("ONE WORD--->"+l);
                        temp = new Column(l).full();

                        break;
                    case 2:
                        //System.out.println("TWO WORD--->"+l);
                        
                        w1 = l.split(" ")[0];
                        w2 = l.split(" ")[1];
                        if (w1.contains("@")) {
                            //ELIMINAMOS LOS @                      LOS PARENTESIS                             EL PREFIJO KEEP        
                            w1 = w1.replaceAll("@", " ").replaceAll("\\(", "").replaceAll("\\)", "").substring(5);
                        }
                        temp = new Column(w2).full(w1);
                        
                        break;
                    case 3:
                        //System.out.println("THREE WORD--->"+l);
                        l = l.trim();
                        w1 = l.split(" ")[0];
                        w3 = l.split(" ")[2];
                        temp = new Column(w3).full(w1);
                        if (temp.getName().contains("@")) {
                            //ELIMINAMOS LOS @                      LOS PARENTESIS                             EL PREFIJO KEEP        
                            temp.setName(temp.getName().replaceAll("@", " ").replaceAll("\\(", "").replaceAll("\\)", "").substring(5));
                        }
                        break;
                    case 4:
                        //System.out.println("TABLE.JAVA CALC COLUMNS, WORDS: 4" );
                        words = l.split(" ");
                        w3 = words[words.length - 1];
                        for (String w : words) {
                            if (w.equalsIgnoreCase("as")) {
                                break;
                            }
                            caseWhen += w + " ";
                        }
                        if (caseWhen.contains("@")) {
                            //ELIMINAMOS LOS @                      LOS PARENTESIS                             EL PREFIJO KEEP        
                            caseWhen = caseWhen.replaceAll("@", " ").replaceAll("\\(", "").replaceAll("\\)", "").substring(5);
                        }
                        temp = new Column(caseWhen, w3, "undefined");
                        
                        
                        
                        break;
                    default:
                        //System.out.println("TABLE.JAVA CALC COLUMNS, WORDS: MORE THAN 4" );
                        //System.out.println("WHATT??"+l.split(" ").length+"--->"+l);
                        words = l.split(" ");
                        caseWhen = "";
                        w3 = words[words.length - 1];
                        for (String w : words) {
                            if (w.equalsIgnoreCase("as")) {
                                break;
                            }
                            if(!w.equals(" ")){
                                caseWhen += w.trim() + " ";
                            }
                        }
                        if (caseWhen.contains("@")) {
                            //ELIMINAMOS LOS @                      LOS PARENTESIS                             EL PREFIJO KEEP        
                            caseWhen = caseWhen.replaceAll("@", " ").replaceAll("\\(", "").replaceAll("\\)", "").substring(5).trim();
                        }
                        temp = new Column(caseWhen, w3, "undefined");
                        
                        
                }
                //System.out.println("BEFORE ADDING: "+ temp.toCSV());
                this.getColumnas().add(temp);
            }
            //System.out.println("SIZEEEE  " + tablas_entrada.size());
            for (Column c : this.getColumnas()) {

                for (Table t : tablas_entrada) {
                //System.out.println(t.getAlias());

                //EL ALIASORIGEN ES NULL...
                    //System.out.println("name "+ c.getName());
                    //System.out.println("aliasorigen "+ c.getAliasOrigen());
                    if (c.getAliasOrigen().equalsIgnoreCase(t.alias)) {
                        c.setTablaOrigen(t);
                    }
                }
                if(c.getTablaOrigen() == null){
                    c.setTablaOrigen(new Table("undefined","undefined","undefined"));
                }
                //si no se ha seteado ningun origen y solo hay una tabla de entrada en el step, ese sera el origen
                if(c.getTablaOrigen().getAlias().equals("undefined") || c.getTablaOrigen().getAlias().equals("default")
                   && s.getIn_tables().size() == 1){
                    c.setTablaOrigen(s.getIn_tables().get(0));
                }
                //System.out.println("CALC COLUMNS FINAL: " + c.toCSV());
            }
            
        } catch (EmptyStatementException e) {
            
            //System.out.println(e.getMessage()+" IN STEP WITH OUTPUT TABLE:" + s.getOut_tables().get(0).getName() );
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
        //return "name=" + name + ", esquema=" + esquema + ", alias=" + alias + ", columnas=" + columnas.size();
        return BLUE +"NAME: "+ NONE +Manager.withRightPadding(name)
              +BLUE +"ESQUEMA: "+ NONE +Manager.withRightPadding(esquema)
              +BLUE +"ALIAS: "+ NONE +Manager.withRightPadding(alias)
              +BLUE +"N. COLUMNAS: "+ NONE +Manager.withRightPadding(String.valueOf(columnas.size()));
    }
    public String toCSV(){
        return esquema +";"+name+";"+alias+";";
    }

}
