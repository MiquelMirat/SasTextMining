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
public class ProcStep extends Step{
    
    //caracteristicas
    
    //COMMON
    private String type;
    //SQL
    private String create;
    private String select;
    private String from;
    private String where;
    private String group;
    private String order;
    //SORT TRANSPOSE
    private String data;
    private String out;
    private String rename;
    private String keep;
    private String drop;
    private String by;
    private String var;
    
    
    //contructor
    public ProcStep() {
        super();
        this.create = "";
        this.select = "";
        this.from = "";
        this.where = "";
        this.group = "";
        this.order = "";
        
        this.data = "";
        this.out = "";
        this.rename = "";
        this.keep = "";
        this.drop = "";
        this.by = "";
        
        this.var = "";
    }

    @Override
    public void divideStatements(){
        //System.out.println("DIVIDE PRoC");
        String[] words = this.getRawContent().split(" ");
        //System.out.println(words[2]);
        String type = words[2].toUpperCase();
        this.setType(type);
        switch(type){
            case "SQL":
                this.divideSqlStatements(words);break;
            case "SORT":
            case "TRANSPOSE":
                this.divideSortTransposeStatements(words);break;
            default:
                System.out.println("PROC divide statements switch default, type: "+type);
        }
    }
    
    public void divideSqlStatements(String[] words){
        boolean create = false, select = false, from = false, where = false, group = false, order = false;
        for(String w: words){
            switch(w.toUpperCase()){
                case "CREATE":
                    create = true; break;
                case "SELECT":
                    select = true;
                    create = false; break;
                case "FROM":
                    from = true;
                    select = false; break;
                case "WHERE":
                    where = true;
                    from = false; break;
                case "GROUP":
                    group = true;
                    where = false; from = false; break;
                case "ORDER":
                    order = true;
                    from = false; where = false; group = false; break;
                case ";":
                    from = false; order = false; group = false; break;
                    
            }//end switch
            if(create)  {this.create += w + " ";}
            if(select)  {this.select += w + " ";}
            if(from)    {this.from  += w + " ";}
            if(where)   {this.where += w + " ";}
            if(group)   {this.group += w + " ";}
            if(order)   {this.order += w + " ";} 
        }//end foreach
        
        //TRIMEAMOS TODOS LOS BLOQUES
        this.create = this.create.trim();   this.select = this.select.trim();
        this.from = this.from.trim();       this.where = this.from.trim();
        this.group = this.from.trim();      this.order = this.from.trim();
        //System.out.println(this.from);
    }
    
    public void divideSortTransposeStatements(String[] words){
        boolean data = false, out = false, rename = false, by = false, keep = false, drop = false, var = false;
        for (String w: words){
            //System.out.println(w);
            if(w.equalsIgnoreCase("BY")){ by = true;}
            if(w.equalsIgnoreCase("VAR")){ var = true;}
            if(w.equalsIgnoreCase(";")){ 
                data = false; out = false; by = false; keep = false; drop = false; var = false;
            }
            if(w.toUpperCase().contains("DATA=")){ data = true;}
            if(w.toUpperCase().contains("OUT=")){ out = true; data = false;}
            if(w.toUpperCase().contains("KEEP=")){ keep = true; out = false;}
            if(w.toUpperCase().contains("DROP=")){ drop = true; keep = false;}
            if(w.toUpperCase().contains("RENAME=")){ rename = true; out = false; keep = false; data = false;}
           
           
            if (data)   {this.data += w + " ";}
            if (out)    {this.out += w + " ";}
            if (rename) {this.rename += w + " ";}
            if (by)     {this.by  += w + " ";}
            if (keep)   {this.keep += w + " ";}
            if (drop)   {this.drop += w + " ";}
            if (var)    {this.var += w + " ";}
        }
        
        this.data = this.data.trim();    this.out = this.out.trim();
        this.rename = this.rename.trim();this.by = this.by.trim();
        this.drop = this.drop.trim();    this.var = this.var.trim();
        
    }

    @Override
    public void calcOutputTables() {
        String content = "";
        Table temp = null;
        switch(type.toUpperCase()){
            case "SQL":
                //System.out.println("SQL OUTPUT");
                if(this.getCreate() == ""){ break; }
                content = this.getCreate().split(" ")[2];
                temp = new Table(content).withSchema();
                this.getOut_tables().add(temp);
                break;
            case "SORT":
            case "TRANSPOSE":
                //System.out.println("SORT TRANSPOSE OUTPUT");
                if(this.getOut() == ""){ break; }
                content = this.getOut().substring(4);
                for(String table: content.split(" ")){
                    temp = new Table(table).withSchema();
                    //System.out.println(temp.getEsquema());
                    this.getOut_tables().add(temp); 
                }
                break;
        }
        //System.out.println(this.getOut_tables().size()+this.getOut_tables().get(0).getName());
    }
    
    @Override
    public void calcInputTables(){
        String content = "";
        Table temp = null;
        switch(type.toUpperCase()){
            case "SQL":
                //System.out.println("SQL OUTPUT");
                if(this.getFrom() == ""){ System.out.println("FROM VACIO"); break; }
                String[] words = this.getFrom().split(" ");
                //System.out.println("LENGHT FROM:" + words.length+ "TEXT FROM: "+ this.getFrom());
                for(int i = 0; i<words.length; i++){
                    if(words[i].equalsIgnoreCase("from") || words[i].equalsIgnoreCase("join")){
                        temp = new Table(words[i+1]).withSchema();
                        if(i+2 < words.length-1){
                            if(words[i+2].equalsIgnoreCase("as")){
                                temp.setAlias(words[i+3]);
                            }else{
                                if(!words[i+2].equalsIgnoreCase("on")){
                                    temp.setAlias(words[i+2]);
                                }
                                if(words[i+3].equalsIgnoreCase("on")){
                                    temp.setAlias(words[i+2]);
                                }
                            }
                        }else{
                            if(i+1 < words.length - 1){
                                if(words[i+2].equalsIgnoreCase(";")){
                                    temp.setAlias(words[i+1]);
                                }else{
                                    temp.setAlias(words[i+2]);
                                }
                            }else{
                                temp.setAlias(words[i+1]);
                            }
                        }
                    }
                }
                this.getIn_tables().add(temp);
                break;
            case "SORT":
            case "TRANSPOSE":
                //System.out.println("SORT TRANSPOSE OUTPUT");
                if(this.getData() == ""){ System.out.println("DATA VACIO"); break; }
                content = this.getData().substring(5);
                for(String table: content.split(" ")){
                    temp = new Table(table).withSchema();
                    temp.setAlias("");
                    //System.out.println(temp.getEsquema());
                    this.getIn_tables().add(temp); 
                }
                //System.out.println(this.getOut_tables().size());
                break;
        }
    }
    
    
    
    
    
    
    
    
    //getters y setters
    public String getType() {return type;}
    public void setType(String type) {this.type = type;}
    public String getCreate() {return create;}
    public void setCreate(String create) {this.create = create;}
    public String getSelect() {return select;}
    public void setSelect(String select) {this.select = select;}
    public String getFrom() {return from;}
    public void setFrom(String from) {this.from = from;}
    public String getWhere() {return where;}
    public void setWhere(String where) {this.where = where;}
    public String getGroup() {return group;}
    public void setGroup(String group) {this.group = group;}
    public String getOrder() {return order;}
    public void setOrder(String order) {this.order = order;}
    public String getData() {return data;}
    public void setData(String data) {this.data = data;}
    public String getOut() {return out;}
    public void setOut(String out) {this.out = out;}
    public String getKeep() {return keep;}
    public void setKeep(String keep) {this.keep = keep;}
    public String getDrop() {return drop;}
    public void setDrop(String drop) {this.drop = drop;}
    public String getBy() {return by;}
    public void setBy(String by) {this.by = by;}
    public String getVar() {return var;}
    public void setVar(String var) {this.var = var;}

    @Override
    public String toString() {
        if(type.equalsIgnoreCase("sql")){
            return "ProcSqlStep{"
                    +  "\ncreate=" + create + ", \nselect=" + select 
                    + ", \nfrom=" + from + ", \nwhere=" + where 
                    + ", \ngroup=" + group + ", \norder=" + order;
        }else{
           return "Proc"+type+"Step{" 
                   + "\ndata=" + data + ", \nout=" + out 
                   + ", \nrename=" + rename + ", \nkeep=" + keep 
                   + ", \ndrop=" + drop + ", \nby=" + by 
                   + ", \nvar=" + var + '}';
        }
        
    }
    
    
    
    
    
    
}
