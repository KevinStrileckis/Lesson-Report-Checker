/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lessonreportchecker;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JTextArea;

/**
 *
 * @author Kevin Strileckis
 */
public class UICSP implements ActionListener {
    
    private JFrame top, topSide, topCloseWriter;
    private JButton load;
    private JTextArea reportArea;
    private PrintWriter writer;
    private JButton closeWriter;
    
    public UICSP(){
        setUp();
    }
    
    private void setUp(){
        top = new JFrame("Principles Report Checker");
        top.setPreferredSize(new Dimension(150, 250));
        top.setLocation(400, 250);
        topSide = new JFrame("Principles Report Checker");
        topSide.setPreferredSize(new Dimension(150, 250));
        topSide.setLocation(650, 250);
        topCloseWriter = new JFrame("Principles Report Checker");
        topCloseWriter.setPreferredSize(new Dimension(150, 250));
        topCloseWriter.setLocation(950, 250);
        
        load = new JButton("Load new report");
        load.addActionListener(this);
        top.add(load);
        
        closeWriter = new JButton("Close Writer");
        closeWriter.addActionListener(this);
        topCloseWriter.add(closeWriter);
        
        reportArea = new JTextArea();
        topSide.add(reportArea);
        
        try {
            writer = new PrintWriter("Output.preport", "UTF-8");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(UICSP.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(UICSP.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        top.pack();
        top.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        top.setVisible(true);
        topSide.pack();
        topSide.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        topSide.setVisible(true);
        topCloseWriter.pack();
        topCloseWriter.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        topCloseWriter.setVisible(true);
    }
    
    private void doneWriting(){
        //close writer
        writer.close();
    }
    
    @SuppressWarnings("empty-statement")
    private void readLessonReportFile() throws IOException{
        
        JFileChooser browse = new JFileChooser();
        browse.setMultiSelectionEnabled(true);
        try {
            //Set the default location to the parent directory. If this fails somehow, it will be Documents.
            browse.setCurrentDirectory(new File(UICSP.class.getProtectionDomain().getCodeSource().getLocation().toURI()));
            //Try to open the lessons folder. If this fails, then the selector will fall back on the parent directory
            browse.setCurrentDirectory(new File(UICSP.class.getProtectionDomain().getCodeSource().getLocation().toURI() + "\\Lessons"));
        } catch (URISyntaxException ex) {
            Logger.getLogger(UICSP.class.getName()).log(Level.SEVERE, null, ex);
        }
        int temp = browse.showOpenDialog(top);
        File[] reports;
            
            //handle if user selects a file
            if(temp == JFileChooser.APPROVE_OPTION){
                reports = browse.getSelectedFiles();
            }
            else
            {
                return;
            }
        
        //Read all reports
        for(File r : reports)
            readSingleLesson(r);
    }
    
    private void readSingleLesson(File report) throws IOException{
        String name;
        ArrayList<String> question = new ArrayList<>();
        ArrayList<Integer> score = new ArrayList<>();
        Date date = new Date();
        
        try{
            FileReader fr = new FileReader(report);
            BufferedReader br = new BufferedReader(fr);
            String buffer;
            
            //Skip 12 garbage lines
            for(int i=0; i<12; ++i)
                buffer = br.readLine();
            //Get name and unscramble
            buffer = br.readLine();
            buffer = unscramble(buffer.substring(0, buffer.length()-3));
            name = buffer;
            reportArea.append(buffer+ "\n");
            //Skip 20 garbage lines
            for(int i=0; i<20; ++i)
                buffer = br.readLine();
            //Get name without unscrambling
            buffer = br.readLine();
            reportArea.append(buffer+ "\n");
            //Skip garbage line
            buffer = br.readLine();
            //Get name and unscramble
            buffer = br.readLine();
            buffer = unscramble(buffer.substring(0, buffer.length()-2));
            reportArea.append(buffer+ "\n"+ "\n");
            //Get all scores
            buffer = br.readLine();
                //The score was scrambled by adding by 'a'. Get the value of that difference for the user
            while(buffer.length() > 4){
                question.add(question.size(), buffer.substring(1));
                score.add(score.size(), buffer.charAt(0) - 'a');
                reportArea.append(Integer.toString(score.get(score.size()-1)) + question.get(question.size()-1) + "\n");
                //
                buffer = br.readLine();
            }
            //Skip garbage line
            buffer = br.readLine();
            //Get totalTotal -- do not unscramble but times by 2
            buffer = br.readLine();
            reportArea.append(buffer + " * 2\n");
            //Get totalAnswers -- do not unscramble
            buffer = br.readLine();
            reportArea.append(buffer + "\n");
            //Get time
            buffer = br.readLine();
            reportArea.append(buffer + "\n");
            
            //The remaining 66 lines are garbage
            
            //Output full report to file
            writer.print(name);
                //Output all questions on a single line
            for(int i=0; i<question.size(); ++i)
                writer.print("\t" + question.get(i));
                //Output all scores on the next line
            writer.println("");
            for(int i=0; i<score.size(); ++i)
                writer.print("\t" + score.get(i));
            writer.println("");
        }
        catch (FileNotFoundException ex){
            System.out.println("Error in reading lesson file.");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals(closeWriter.getActionCommand()))
            doneWriting();
        else
        {
            try {
                readLessonReportFile();
            } catch (IOException ex) {
                Logger.getLogger(UICSP.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    /*  writer.println(makeTrash(4, 8));//garbage line;
        writer.println(scramble(UserInfo.getName()));
        writer.println(scramble(UserInfo.getScore()));
        writer.println(makeTrash(3, 5));//garbage line;
        writer.println(totalTotal / 2);
        writer.println(totalAnswers);
        writer.println(UserInfo.getTimeStart());
        writer.println(UserInfo.getTimeEnd());
    */
    
    
    
    
    private  String unscramble(String s){
        String s2 = "";
        for(int i=s.length()-1; i>0; i--){
            s2 += (char)(s.charAt(i) - 3);
        }
        return s2;
    }
}
