/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Column;
import model.ProcStep;
import model.SASFile;
import model.Step;
import model.Table;

/**
 *
 * @author miquel.mirat
 */
public class FileManager {

    StringBuilder csv;
    Manager mng = Manager.getInstance();
    ArrayList<SASFile> sasFiles = new ArrayList<>();
    String inputFolder = "SASSources";
    String outputFolder = "outputs";
    final String SEPARATOR = File.separator;

    public FileManager() {

    }

    public String readFile(String filename) {
        // The name of the file to open.
        String fileName = filename;
        // This will reference one line at a time
        String line = null;
        String content = "";
        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while ((line = bufferedReader.readLine()) != null) {
                content += line;
            }
            bufferedReader.close();
        } catch (FileNotFoundException ex) {
            System.out.println("Unable to open file '" + fileName + "'");
        } catch (IOException ex) {
            System.out.println("Error reading file '" + fileName + "'");
        } finally {
            return content;
        }
    }

    public void readFolderContent(String file) {
        File folder = new File(file);
        File[] files = folder.listFiles();
        for (File f : files) {
            if (f.isFile()) {
                if (f.getName().split("\\.")[1].equals("sas")) {
                    String content = mng.prepareInput(readFile(file + SEPARATOR + f.getName()));
                    SASFile sas = new SASFile(content, f.getName());
                    sas.calcSteps();
                    sas.extractData();
                    this.sasFiles.add(sas);
                }
            } else {
                System.out.println("won't process SAS files in subfolders");
            }

        }

    }

    public void output(String outputFolder) {
        for (SASFile f : sasFiles) {
            writeCSV(f.getSteps(), f.getFilename(), outputFolder);
            writeCSV2(f.getSteps(), f.getFilename(), outputFolder);
        }
        writeTODOv1(outputFolder);
        writeTODOv2(outputFolder);
    }

    public void writeTODOv1(String outputFolder) {
        for (SASFile f : sasFiles) {
            for (Step s : f.getSteps()) {
                csv.append(";;;\nSTEP;").append(s.getType()).append(";\nOUTPUT TABLES;;;\nNAME;ESQUEMA;ALIAS;N_COLUMNAS\n");
                for (Table t : s.getOut_tables()) {
                    csv.append(t.getName()).append(";").append(t.getEsquema()).append(";").append(t.getAlias()).append(";").append(t.getColumnas().size()).append("\n");
                    if (t.getColumnas().size() > 0) {
                        csv.append(";;;\nCOLUMNAS;;;\nNAME;ALIAS;ALIAS ORIGEN;TABLA ORIGEN\n");
                        for (Column c : t.getColumnas()) {
                            csv.append(c.getName()).append(";").append(c.getAlias()).append(";").append(c.getAliasOrigen()).append(";").append(c.getTablaOrigen().getName()).append("\n");
                        }
                    }
                }
                csv.append(";;;\nORIGENES;;;\nNAME;ESQUEMA;ALIAS;\n");
                for (Table t : s.getIn_tables()) {
                    csv.append(t.getName()).append(";").append(t.getEsquema()).append(";").append(t.getAlias()).append(";\n");
                }
                csv.append(";;;\n;FILTROS;GROUPS;ORDERS\n");
                for (int i = 0; i < s.getFilters().size(); i++) {
                    csv.append(";").append(s.getFilters().get(i)).append(";").append(s.getGroupings().get(i)).append(";").append(s.getSorted_by().get(i)).append("\n");
                }
                //System.out.println(";;;\n");
            }
        }
        try (PrintWriter writer = new PrintWriter(new File(outputFolder + SEPARATOR + "TODO_V1.csv"))) {
            writer.write(csv.toString());

        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void writeTODOv2(String outputFolder) {
        csv = new StringBuilder();
        csv.append("FILE;STEP;ORIGEN;;;OUTPUT;;;COLUMNS;;;;TRANSFORMATIONS;;?\n"
                + ";;ESQUEMA;NAME;ALIAS;ESQUEMA;NAME;ALIAS;ALIAS;ALIAS ORIGEN;T.ORIGEN;DATO/CALCULO;FILTRO;GROUP;ORDER\n");
        for (SASFile sas : sasFiles) {
            int stepId = 0;
            for (Step s : sas.getSteps()) {
                stepId++;
                //System.out.println(stepId);
                for (Table t_in : s.getIn_tables()) {
                    for (Table t_out : s.getOut_tables()) {
                        if (!t_out.getColumnas().isEmpty()) {
                            //System.out.println(t_out.getColumnas().size());
                            for (Column c : t_out.getColumnas()) {
                                //System.out.println(c.toCSV());
                                csv.append(newFullLine(s, stepId, t_out, t_in, c));
                                //System.out.println();
                            }
                        } else {
                            csv.append(newFullLineNoColumns(s, stepId, t_out, t_in));
                            //FULLLINE WITHOUT COLUMN
                        }
                    }
                }
            }
        }
        try (PrintWriter writer = new PrintWriter(new File(outputFolder + SEPARATOR + "TODO_V2.csv"))) {
            writer.write(csv.toString());
        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void writeCSV2(ArrayList<Step> steps, String source, String outputFolder) {
        int stepId = 0;
        csv = new StringBuilder();
        csv.append("FILE;STEP;ORIGEN;;;OUTPUT;;;COLUMNS;;;;TRANSFORMATIONS;;?\n"
                + source + ";;ESQUEMA;NAME;ALIAS;ESQUEMA;NAME;ALIAS;ALIAS;ALIAS ORIGEN;T.ORIGEN;DATO/CALCULO;FILTRO;GROUP;ORDER\n");

        for (Step s : steps) {
            stepId++;
            //System.out.println(stepId);
            for (Table t_in : s.getIn_tables()) {
                for (Table t_out : s.getOut_tables()) {
                    if (!t_out.getColumnas().isEmpty()) {
                        //System.out.println(t_out.getColumnas().size());
                        for (Column c : t_out.getColumnas()) {
                            //System.out.println(c.toCSV());
                            csv.append(newFullLine(s, stepId, t_out, t_in, c));
                            //System.out.println();
                        }
                    } else {
                        csv.append(newFullLineNoColumns(s, stepId, t_out, t_in));
                        //FULLLINE WITHOUT COLUMN
                    }
                }
            }
        }

        try (PrintWriter writer = new PrintWriter(new File(outputFolder + SEPARATOR + source.split("\\.")[0] + "V2.csv"))) {
            writer.write(csv.toString());
        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage());
        }

    }

    //CSV2 LINE FORMAT
    public String newFullLine(Step s, int stepId, Table out, Table in, Column c) {
        String line = s.getSourceFile() + ";" + stepId + ";" + in.toCSV() + out.toCSV() + c.toCSV() + getTails(s) + "\n";
        return line;
    }

    public String newFullLineNoColumns(Step s, int stepId, Table out, Table in) {
        String line = s.getSourceFile() + ";" + stepId + ";" + in.toCSV() + out.toCSV() + "NO;COLUMNS;DEFINED;;" + getTails(s) + "\n";
        return line;
    }

    public String getTails(Step s) {
        String filtros = "";
        String groups = "";
        String order = "";
        if (s instanceof ProcStep) {
            ProcStep ss = (ProcStep) s;
            if (ss.getProcType().equalsIgnoreCase("sql")) {
                filtros = ss.getWhere();
                groups = ss.getGroup();
                order = ss.getOrder();
            } else {
                order = ss.getBy();
            }

        }

        return filtros + ";" + groups + ";" + order;
    }

    public void writeCSV(ArrayList<Step> steps, String source, String outputFolder) {
        csv = new StringBuilder();
        for (Step s : steps) {
            csv.append(";;;\nSTEP;").append(s.getType()).append(";\nOUTPUT TABLES;;;\nNAME;ESQUEMA;ALIAS;N_COLUMNAS\n");
            for (Table t : s.getOut_tables()) {
                csv.append(t.getName()).append(";").append(t.getEsquema()).append(";").append(t.getAlias()).append(";").append(t.getColumnas().size()).append("\n");
                if (t.getColumnas().size() > 0) {
                    csv.append(";;;\nCOLUMNAS;;;\nNAME;ALIAS;ALIAS ORIGEN;TABLA ORIGEN\n");
                    for (Column c : t.getColumnas()) {
                        csv.append(c.getName()).append(";").append(c.getAlias()).append(";").append(c.getAliasOrigen()).append(";").append(c.getTablaOrigen().getName()).append("\n");
                    }
                }
            }
            csv.append(";;;\nORIGENES;;;\nNAME;ESQUEMA;ALIAS;\n");
            for (Table t : s.getIn_tables()) {
                csv.append(t.getName()).append(";").append(t.getEsquema()).append(";").append(t.getAlias()).append(";\n");
            }
            csv.append(";;;\n;FILTROS;GROUPS;ORDERS\n");
            for (int i = 0; i < s.getFilters().size(); i++) {
                csv.append(";").append(s.getFilters().get(i)).append(";").append(s.getGroupings().get(i)).append(";").append(s.getSorted_by().get(i)).append("\n");
            }
            //System.out.println(";;;\n");
        }
        try (PrintWriter writer = new PrintWriter(new File(outputFolder + SEPARATOR + source.split("\\.")[0] + "V1.csv"))) {
            writer.write(csv.toString());

        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage());
        }

    }

    public StringBuilder getCsv() {
        return csv;
    }

    public void setCsv(StringBuilder csv) {
        this.csv = csv;
    }

    public Manager getMng() {
        return mng;
    }

    public void setMng(Manager mng) {
        this.mng = mng;
    }

    public ArrayList<SASFile> getSasFiles() {
        return sasFiles;
    }

    public void setSasFiles(ArrayList<SASFile> sasFiles) {
        this.sasFiles = sasFiles;
    }

}
