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
    private String rawContent;
    private ArrayList<Comment> comments;
    private ArrayList<Column> columns;
    private ArrayList<Table> out_tables;
    private ArrayList<Table> in_tables;
    
    
    public Step(){
        this.rawContent = "";
        this.comments = new ArrayList<>();
        this.columns = new ArrayList<>();
        this.out_tables = new ArrayList<>();
        this.in_tables = new ArrayList<>();
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
   public void divideStatements(){}
   public void calcOutputTables(){}
   public void calcInputTables(){}
   public void calcSortings(){}

    public String getRawContent() {
        return rawContent;
    }

    public void setRawContent(String rawContent) {
        this.rawContent = rawContent;
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    public ArrayList<Column> getColumns() {
        return columns;
    }

    public void setColumns(ArrayList<Column> columns) {
        this.columns = columns;
    }

    public ArrayList<Table> getOut_tables() {
        return out_tables;
    }

    public void setOut_tables(ArrayList<Table> out_tables) {
        this.out_tables = out_tables;
    }

    public ArrayList<Table> getIn_tables() {
        return in_tables;
    }

    public void setIn_tables(ArrayList<Table> in_tables) {
        this.in_tables = in_tables;
    }
    
    
}
