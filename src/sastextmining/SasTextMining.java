/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sastextmining;

import Exceptions.CustomException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import manager.FileManager;
import manager.Manager;
import model.Column;
import model.DataStep;
import model.ProcStep;
import model.SASFile;
import model.Step;
import model.Table;

/**
 *
 * @author miquel.mirat
 */
public class SasTextMining {

    static FileManager fm = new FileManager();
    static Manager mng = Manager.getInstance();
    static ArrayList<Step> steps = null;

    public static void main(String[] args) {
        String inputFolder = "";
        String outputFolder = "";

        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");

            fm.readFolderContent(getInputFolder());
            fm.output(getOutputFolder());
            
//            for (SASFile sas : fm.getSasFiles()) {
//                for (Step s : sas.getSteps()) {
//                    for (Table t : s.getOut_tables()) {
//                        for (Column c : t.getColumnas()) {
//                            System.out.println(c.toString());
//                        }
//                    }
//                }
//            }
        } catch (UnsupportedLookAndFeelException | IllegalAccessException | ClassNotFoundException | InstantiationException ex) {
            Logger.getLogger(SasTextMining.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CustomException e){
            System.out.println(e.getMessage());
            promptError("Error selecting files");
        }

    }
    
    public static void promptError(String msg ){
        JFrame frame = new JFrame();
        JOptionPane.showMessageDialog(new JFrame(),
            msg);
    }

    public static String getInputFolder() throws CustomException {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setDialogTitle("Selecciona el directorio con los .SAS dentro");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            return String.valueOf(chooser.getSelectedFile());
        } else {
            throw new CustomException("Select input folder!");
        }
    }

    public static String getOutputFolder() throws CustomException {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setDialogTitle("Selecciona el directorio para los ficheros .csv");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            return String.valueOf(chooser.getSelectedFile());
        } else {
            throw new CustomException("Select output folder!");
        }

    }

}
