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
abstract public class Step {
    private final String RED = "\u001B[31m";
    private final String BOLD = "\u001b[4m";
    private final String NONE = "\u001b[0m";
    private String rawContent;
    private String type;
    private ArrayList<Comment> comments = new ArrayList<>();
    private ArrayList<Column> columns = new ArrayList<>();
    private ArrayList<Table> out_tables = new ArrayList<>();
    private ArrayList<Table> in_tables = new ArrayList<>();
    private ArrayList<String> sorted_by = new ArrayList<>();
    private ArrayList<String> filters = new ArrayList<>();
    private ArrayList<String> groupings = new ArrayList<>();
    
    
    public Step(){
        this.rawContent = "";
        this.type = "";
        
    }
    public Step(String type){
        this.rawContent = "";
        this.type = type;
        
    }
    
    public void archiveComments(){
        boolean inComment = false;
        Comment temp = null;
        char last = 'Z';
        String cleanRawContent = "";
        int count = 0;
        for(char c :this.getRawContent().toCharArray()){    
            if(c == '*' && last == '/'){
                inComment = true;
                temp = new Comment();
                temp.setPos(count);
            }
            if(c == '/' && last == '*'){
                inComment = false;
                this.getComments().add(temp);
            }
            last = c;
            count++;
            if(inComment){
                temp.setText(temp.getText() + c);
            }else{
                cleanRawContent += c;
            }
        }
        cleanRawContent = cleanRawContent.replace("//", " ");
        cleanRawContent = cleanRawContent.replaceAll("\\s+"," ");
        this.setRawContent(cleanRawContent);
        
        /*for(Comment c : this.comments){
            System.out.println(c.getText());
        }*/
        
    }
    public void printStep() {
        System.out.print(NONE + "--------------------------------------------------------");
        System.out.print(NONE + "\n" + BOLD + "--------------STEP--" + this.getType() + "--");
        System.out.print(this instanceof ProcStep ? ((ProcStep) this).getProcType() : "");
        System.out.println("---------------------------");
        System.out.println("--------------------------------------------------------");
        System.out.println("\n" + RED + "TABLAS DE SALIDA");
        for (Table t : this.getOut_tables()) {
            //System.out.print("\t");
            System.out.println("\t" + t.toString());
            System.out.println("\n" + RED + "  COLUMNAS");
            for (Column c : t.getColumnas()) {
                System.out.println("\t" + c.toString());
            }
        }
        System.out.println("\n" + RED + "ORIGENES");
        for (Table t : this.getIn_tables()) {
            System.out.println("\t" + t.toString());
        }
        System.out.println("\n" + RED + "FILTROS");
        for (String f : this.getFilters()) {
            System.out.println("\t" + f);
        }
        System.out.println("\n" + RED + "AGRUPACIONES (en orden de importancia)");
        for (String g : this.getGroupings()) {
            System.out.println("\t" + g);
        }

        System.out.println("\n" + RED + "ORDENACIONES (en orden de importancia)");
        for (String o : this.getSorted_by()) {
            System.out.println("\t" + o);
        }
    }
    
    
    //metodos comunes
    public void divideStatements(){}
    public void calcOutputTables(){}
    public void calcInputTables(){}
    public void calcSortings(){}
    public void calcFilters(){}
    public void calcGroupings(){}
    
    //getter y setters comunes
    public String getRawContent() {return rawContent;}
    public void setRawContent(String rawContent) {this.rawContent = rawContent;}
    public ArrayList<Comment> getComments() {return comments;}
    public void setComments(ArrayList<Comment> comments) {this.comments = comments;}
    public ArrayList<Column> getColumns() {return columns;}
    public void setColumns(ArrayList<Column> columns) {this.columns = columns;}
    public ArrayList<Table> getOut_tables() {return out_tables;}
    public void setOut_tables(ArrayList<Table> out_tables) {this.out_tables = out_tables;}
    public ArrayList<Table> getIn_tables() {return in_tables;}
    public void setIn_tables(ArrayList<Table> in_tables) {this.in_tables = in_tables;}
    public String getType() {return type;}
    public void setType(String type) {this.type = type;}
    public ArrayList<String> getSorted_by() {return sorted_by;}
    public void setSorted_by(ArrayList<String> sorted_by) {this.sorted_by = sorted_by;}
    public ArrayList<String> getFilters() {return filters;}
    public void setFilters(ArrayList<String> filters) {this.filters = filters;}
    public ArrayList<String> getGroupings() {return groupings;}
    public void setGroupings(ArrayList<String> groupings) {this.groupings = groupings;}
    
}
