/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zemberekstemmer;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Iterator;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.terrier.indexing.Document;
import org.terrier.indexing.FileDocument;
import org.terrier.terms.BaseTermPipelineAccessor;
import org.terrier.terms.TermPipelineAccessor;
import org.terrier.utility.ApplicationSetup;
import zemberek.morphology.TurkishMorphology;

/**
 *
 * @author turgut
 */

public class ZemberekStemmer {

    
    int docId = 0;
    TurkishMorphology morphology = null;

    /**
     * TermPipeline processing
     */
    protected TermPipelineAccessor tpa;

    public static void main(String[] args) {
        try {
            
            System.out.println("This code is generated for applying Stemming based on zemberek-nlp(NLP Tools for Turkish to Turkish Texts \n"
                    + "To use this code refere to https://github.com/ahmetaa/zemberek-nlp and its related article and  \n" +
                    "http://terrier.org Terrier IR Platform and its related article" );
            if(args == null || !(args.length == 2))
            {
                System.out.println("1.Parameter : Input xlsx file. Format of Columns: Fıle Id  FileText");
                System.out.println("2.Parameter : Output csv file path");
                return;
            }
            String sFileName = args[0];
            String outputFileName = args[1];
            
                    

            ZemberekStemmer obj = new ZemberekStemmer();
            obj.ReadExcelFileForStemming(sFileName, outputFileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ZemberekStemmer() {
        load_pipeline();
        morphology = TurkishMorphology.createWithDefaults();
    }

    private void ReadExcelFileForStemming(String sFileName, String outputFileName) {
        try {
            File file = new File(sFileName);   //creating a new file instance  
            FileInputStream fis = new FileInputStream(file);   //obtaining bytes from the file  
            XSSFWorkbook wb = new XSSFWorkbook(fis);
            for (int i = 0; i < 1; i++) {
                ReadExcelSheet(wb, i, outputFileName);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ReadExcelSheet(XSSFWorkbook wb, int sheetIndex, String outputFileName) {

        int iRowCnt = 0;
        int iCellIndex = 0;
        Cell cell = null;
 
            
        try {
            PrintWriter fw = new PrintWriter(new FileWriter(outputFileName));

            
            XSSFSheet sheet = wb.getSheetAt(sheetIndex);     //creating a Sheet object to retrieve object  
            Iterator<Row> itr = sheet.iterator();    //iterating over excel file 
            
            while (itr.hasNext()) {
                Row row = itr.next();
                iRowCnt++;
                if (iRowCnt == 1) {
                    row = itr.next();
                }
                //if(iRowCnt == 21) break; // Sadece ilk 20 dokümana atama yapıldığı için
                Iterator<Cell> cellIterator = row.cellIterator();   //iterating over each column  
                iCellIndex = 0;
                String id = "";
                String metin = "";
                while (cellIterator.hasNext()) {
                    iCellIndex++;
                    cell = cellIterator.next();
                    if (iCellIndex == 1) {
                        id = cell.getStringCellValue();
                    }
                    if (iCellIndex == 2) {
                        metin = cell.getStringCellValue();
                    }
                }
                if (metin.length() > 5) {
                    docId++;
                    String metin_new = ParseDoc(id, metin);

                    fw.append(id + "\t" + metin_new + "\n");
                    fw.flush();
                }
            }

            fw.close();
            System.out.println("RowCnt" + " " + iRowCnt);
        } catch (Exception e) {
            System.out.println("Error" + " " + iRowCnt + " " + iCellIndex);
            System.out.println("-------------------");
            System.out.println(e.getMessage());
            e.printStackTrace();

        }
        
    }
  
    private String ParseDoc(String fileName, String metin) {

        metin = metin.replace('İ', 'i');
        String metin_new = "";

        Document doc = new FileDocument(String.valueOf(docId), new ByteArrayInputStream(
                metin.getBytes()),
                new org.terrier.indexing.tokenisation.UTFTokeniser());


        //System.out.println("Doc Id" + docId);
        String sTerm = null;
        while (!doc.endOfDocument() && (sTerm = doc.getNextTerm()) != null) {
            //String sTerm = doc.getNextTerm();
            //String sTerm2 = tpa.pipelineTerm(sTerm);
            TermMorph termMorph = new TermMorph(sTerm);
            termMorph.processTerm(morphology, sTerm);
            String sTerm2 = termMorph.getLemma();

            // System.out.print(sTerm + " " + sTerm2 + " ");
            if (sTerm2 != "null" && sTerm2 != null) {
                metin_new += sTerm2 + " ";
            }
            else
            {
                metin_new += sTerm + " ";
            }
        }
        
        
        //System.out.println(Winner + "\t"  + Math.round(negVal) + "\t" + Math.round(posVal) + "\t" + Math.round(notrVal) + "\t" );
        return metin_new;
    }


    /**
     * load in the term pipeline
     */
    protected void load_pipeline() {
        final String[] pipes = ApplicationSetup.getProperty(
                "termpipelines", "NoOp").trim()
                .split("\\s*,\\s*");
        synchronized (this) {
            tpa = new BaseTermPipelineAccessor(pipes);
        }
    }

    /*
    
    
    
   
     */
}


