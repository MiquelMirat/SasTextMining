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
public class Column {
    private String name;
    private String alias;
    private String aliasOrigen;
    private Table tablaOrigen;
    

    public Column() {
    }

    public Column(String name, String alias, String aliasOrigen) {
        this.name = name;
        this.alias = alias;
        this.aliasOrigen = aliasOrigen;
    }
    public Column(String alias){
        this.alias = alias.trim();
    }
    
    /**
     * Method that complements the constructor
     * @return Column this
     */
    public Column full(){
        if(this.getAlias().equalsIgnoreCase("*")){
            this.setName("*");
            this.setAliasOrigen("undefined");
        }
        if(this.getAlias().contains(".")){
            this.setName(this.getAlias().split("\\.")[1]);
            this.setAliasOrigen(this.getAlias().split("\\.")[0]);
        }else{
            this.setName(this.getAlias());
            this.setAliasOrigen("undefined");
        }
        return this;
    }
    public Column full(String w1){
        if(w1.equalsIgnoreCase("*")){
            this.setName("*");
            this.setAliasOrigen("undefined");
        }
        if(w1.contains(".")){
            this.setName(w1.split("\\.")[1]);
            this.setAliasOrigen(w1.split("\\.")[0]);
        }else{
            this.setName(w1);
            this.setAliasOrigen("undefined");
        }
        return this;
    }

    public String getName() {return name;}
    public void setName(String name) {this.name = name;}
    public String getAlias() {return alias;}
    public void setAlias(String alias) {this.alias = alias;}
    public String getAliasOrigen() {return aliasOrigen;}
    public void setAliasOrigen(String aliasOrigen) {this.aliasOrigen = aliasOrigen;}

    @Override
    public String toString() {
        return  "name=" + name + "\t\t alias=" + alias + "\t\t  aliasOrigen=" + aliasOrigen;
        
        
       
    }
    
    
    
}
