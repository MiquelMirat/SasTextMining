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
        mng.read("output.txt");
        mng.calcSteps();

        for (Step s : mng.getSteps()) {
            s.archiveComments();
            s.divideStatements();
            s.calcOutputTables();
            s.calcInputTables();
            for (Table t : s.getOut_tables()) {
                t.calcColumns(s);
                //NOTA-- en los bloques transpose, las columnas son nulas siempre...
                //FALTA ADECUARDLO A LOS CASE WHEN

            }
            s.calcSortings();
        }
        for (Step s : mng.getSteps()) {
            System.out.println("\n--------------STEEEEEEEEEEEEEEEEEEEEEEP--------");
            System.out.println("\nTABLAS DE SALIDA");
            for (Table t : s.getOut_tables()) {
                //System.out.print("\t");
                System.out.println("\t" + t.toString() + "\nCOLUMNAS");
                for (Column c : t.getColumnas()) {
                    System.out.println("\t" + c.toString());
                }
            }
            System.out.println("\nORIGENES");
            for (Table t : s.getIn_tables()) {
                System.out.println("\t" + t.toString());
            }
            System.out.println("\nORDENACIONES");
            

        }

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
