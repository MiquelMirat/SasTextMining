/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package model;

import manager.Manager;

/**
 *
 * @author miquel.mirat
 */
public class Column {
    private final String BLUE = "\u001B[34m";
    private final String NONE = "\u001B[0m";
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
            if(!w1.equals(".")){//si hay algo mas que un .
                if(w1.contains("(")){
                    this.setName(w1);
                    this.setAliasOrigen("undefined");
                }else{
                this.setName(w1.split("\\.")[1]);
                this.setAliasOrigen(w1.split("\\.")[0]);
                }
            }else{//si tansolo es un .
                this.setName("undefined");
                this.setAliasOrigen("undefined");
                
            }
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
    public Table getTablaOrigen() {return tablaOrigen;}
    public void setTablaOrigen(Table tablaOrigen) {this.tablaOrigen = tablaOrigen;}

    @Override
    public String toString() {
        /*return  "name=" + name
                + "\t\t alias=" + alias 
                + "\t\t  aliasOrigen="+ aliasOrigen +
                "\t\tTabla Origen="+tablaOrigen.getName();*/
        return BLUE + "NAME: "+NONE+Manager.withRightPadding(name)
              +BLUE +"ALIAS: "+NONE+Manager.withRightPadding(alias)
              +BLUE +"ALIAS ORIGEN: "+NONE+Manager.withRightPadding(aliasOrigen)
              +BLUE +"TABLA ORIGEN: "+NONE+Manager.withRightPadding(tablaOrigen.getName());

    }
    public String toCSV(){
//        System.out.println(alias);
//        System.out.println(aliasOrigen);
//        //System.out.println(tablaOrigen.getName());
//        System.out.println(name);
        return alias+";"+aliasOrigen+";"+tablaOrigen.getName()+";"+name+";";
    }
    public String tabbed(String text){
        text = text.trim();
        if(text.length() > 23){
            return text;
        }else if(text.length() > 15){
            return text  +  "\t";
        }else if(text.length() > 9){
            return text + "\t\t";
        }else if(text.length() > 1){
            return text + "\t\t\t";
        }else{
            return text + "\t\t\t\t";
        }
    }
    
    
    
}
