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
public class Comment {
    //carac
    private String text;
    private int pos;
    
    //contructor
    public Comment(){
        this.text = " ";
    }

    
    //getter i setter
    public String getText() {return text;}
    public void setText(String text) {this.text = text;}
    public int getPos() {return pos;}
    public void setPos(int pos) {this.pos = pos;}
    
    
}
