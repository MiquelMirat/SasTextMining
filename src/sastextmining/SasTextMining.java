/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sastextmining;

import java.util.ArrayList;
import manager.FileManager;
import manager.Manager;
import model.ProcStep;
import model.Step;

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
            
            
            
        }
//        for(Step s: steps){
//            System.out.println(s.toString());
//        }
        

        //archivamos los comnetarios de los steps
        
        //calculamos el contenido de los steps
//        String test = "hello   extra    spaces";
//        System.out.println(test);
//        test = test.replaceAll("\\s+"," ");
//        System.out.println(test);
        
        
    }
    
}
