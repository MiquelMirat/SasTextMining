/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sastextmining;

import java.util.ArrayList;
import manager.FileManager;
import manager.Manager;
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
            //HICE LAS TABLAS DE SALIDA DE LOS PROC, FALTA DE LAS DE ENTRADA
            
            
        }
        for(Step s: steps){
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
