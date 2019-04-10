/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sastextmining;

import java.util.ArrayList;
import manager.FileManager;
import manager.Manager;
import model.Column;
import model.DataStep;
import model.ProcStep;
import model.Step;
import model.Table;

/**
 *
 * @author miquel.mirat
 */
public class SasTextMining {
    static FileManager fm = new FileManager();
    static Manager mng = new Manager();
    static ArrayList<Step> steps = null;
    
    public static void main(String[] args) {
        //recojemos el fichero
        String content = fm.readFile(); //System.out.println(content);

        //dividimos el fichero en steps
        steps = mng.calcSteps(content);
        //System.out.println(steps.size());
        for (Step s: steps){
            s.archiveComments();
            s.divideStatements();
            s.calcOutputTables();
            s.calcInputTables();
            for(Table t: s.getOut_tables()){
                t.calcColumns(s);
                //NOTA-- en los bloques transpose, las columnas son nulas siempre...
                //FALTA ADECUARDLO A LOS CASE WHEN
                //FALTA ADECUARLO A LOS KEEP
                
            }
            
            
            
        }
        for(Step s: steps){
            System.out.println("--------------STEEEEEEEEEEEEEEEEEEEEEEP--------");
            System.out.println("\n\nTABLAS DE SALIDA");
            for(Table t : s.getOut_tables()){
                //System.out.println(t.toString()+"\n");
                System.out.println("TABLA SALIDAAA");
                for(Column c : t.getColumnas()){
                    System.out.println(c.toString());
                }
            }
//            System.out.println("\nTABLAS DE ENTRADA");
//            for(Table t : s.getIn_tables()){
//                System.out.println(t.toString()+"\n");
//            }
//            if(s instanceof ProcStep){
//                if(((ProcStep) s).getType().equalsIgnoreCase("sort") || ((ProcStep) s).getType().equalsIgnoreCase("transpose")){
//                    System.out.println(s.toString());
//                }
//            }
//            if(s instanceof DataStep){
//                //System.out.println("STEP " +s.getClass().getName());
//                for(Table t: s.getOut_tables()){
//                    System.out.println(t.toString());
//                }
//            }
        }
        

        //archivamos los comnetarios de los steps
        
        //calculamos el contenido de los steps
//        String test = "hello   extra    spaces";
//        System.out.println(test);
//        test = test.replaceAll("\\s+"," ");
//        System.out.println(test);
        
        
    }
    
}
