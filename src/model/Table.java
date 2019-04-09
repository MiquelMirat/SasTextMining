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
public class Table {
    private String name;
    private String esquema;
    private String alias;
    private ArrayList<Column> columnas;
    
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
    
    public Table withAliasAndSchema(){
        
        return this;
    }

    public String getName() {return name;}
    public void setName(String name) {this.name = name;}
    public String getEsquema() {return esquema;}
    public void setEsquema(String esquema) {this.esquema = esquema;}
    public String getAlias() {return alias;}
    public void setAlias(String alias) {this.alias = alias;}
    public ArrayList<Column> getColumnas() {return columnas;}
    public void setColumnas(ArrayList<Column> columnas) {this.columnas = columnas;}
    
    
    
    
    
}