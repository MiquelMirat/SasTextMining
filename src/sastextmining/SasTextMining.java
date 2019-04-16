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
        //leemos fichero
        mng.read("text.sas");
        //formateamos el contenido
        mng.prepareInput();
        //dividimos el contenido en pasos
        mng.calcSteps();
        
        //para cada uno de los steps 
        for (Step s : mng.getSteps()) {
            //archivamos los comentarios
            s.archiveComments();
            //dividimos en sus partes
            s.divideStatements();
            //calculamos sus tablas de salida
            s.calcOutputTables();
            //calculamos sus tablas de entrada
            s.calcInputTables();
            //para cada tabla de salida de cada step
            for (Table t : s.getOut_tables()) {
                //calculamos la columnas
                t.calcColumns(s);
                //NOTA-- en los bloques transpose, las columnas son nulas siempre...
                //FALTA ADECUARDLO A LOS CASE WHEN

            }
            s.calcFilters();
            s.calcGroupings();
            s.calcSortings();
            s.fillArrays();
            
        }
//        for (Step s : mng.getSteps()) {
//            s.printStep();
//        }
        //mng.writeCSV();
        mng.writeCSV2();
        
        //estaba haciendo el metodo fill arrays en Step, para hacer que las arrays de filtros orders i groups sean del mismo 
        //tmaaño para escribirlo en el csv
        
        

        //archivamos los comnetarios de los steps
        //calculamos el contenido de los steps
//        String test = "hello   extra    spaces";
//        System.out.println(test);
//        test = test.replaceAll("\\s+"," ");
//        System.out.println(test);
        /*
         String s1 = "WWWWWWW\tWWWWWWW";
         String s2 = "iiiiiii\tWWWWWWW";
         System.out.println(s1);
         System.out.println(s2);
         */
    }

}
