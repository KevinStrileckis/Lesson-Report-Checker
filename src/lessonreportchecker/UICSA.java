/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lessonreportchecker;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
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
public class UICSA implements ActionListener {
    
    private JFrame top, topSide;
    private JButton load, encrypt;
    private JTextArea reportArea;
    
    public UICSA(){
        setUp();
    }
    
    private void setUp(){
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints cons = new GridBagConstraints();
        top = new JFrame("Lesson Report Checker");
        top.setPreferredSize(new Dimension(150, 250));
        top.setLocation(400, 250);
        top.setLayout(layout);
        topSide = new JFrame("Lesson Report Checker");
        topSide.setPreferredSize(new Dimension(150, 250));
        topSide.setLocation(650, 250);
        
        cons.gridx = 0;
        cons.gridy = 0;
        load = new JButton("Load new lesson submission");
        load.addActionListener(this);
        top.add(load, cons);
        
        cons.gridx = 0;
        cons.gridy = 1;
        encrypt = new JButton("Encrypt lesson");
        encrypt.addActionListener(this);
        top.add(encrypt, cons);
        
        reportArea = new JTextArea();
        topSide.add(reportArea);
        
        
        top.pack();
        top.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        top.setVisible(true);
        topSide.pack();
        topSide.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        topSide.setVisible(true);
    }
    
    private void readLessonReportFile() throws IOException{
        JFileChooser browse = new JFileChooser();
        int temp = browse.showOpenDialog(top);
        File report;
            
            //handle if user selects a file
            if(temp == JFileChooser.APPROVE_OPTION){
                report = browse.getSelectedFile();
            }
            else
            {
                return;
            }
            
        try{
            FileReader fr = new FileReader(report);
            BufferedReader br = new BufferedReader(fr);
            String buffer;
            
            //Skip 12 garbage lines
            for(int i=0; i<12;++i)
                br.readLine();
            //Get scrambled name
            buffer = br.readLine();
            buffer = unscramble(buffer);
            reportArea.append(buffer);
            //Skip 20 garbage lines
            for(int i=0; i<20;++i)
                br.readLine();
            //Get name again
            buffer = br.readLine();
            reportArea.append(buffer);
            //Skip garbage line
            br.readLine();
            //Get scrambled name
            buffer = br.readLine();
            buffer = unscramble(buffer);
            reportArea.append(buffer);
            //Get scrambled score (NOT TRUE SCORE)
            buffer = br.readLine();
                        //int j = (buffer.charAt(0) - 'a');
                        //reportArea.append(String.valueOf(j));
            //Skip garbage line
            br.readLine();
            //Get totalTotal -- do not unscramble
            buffer = br.readLine();
            reportArea.append("totalTotal: " + buffer + "\n");
            //Get totalAnswers -- do not unscramble
            buffer = br.readLine();
            reportArea.append("Answers: " + buffer + "\n");
            //Get time start
            buffer = br.readLine();
            reportArea.append("Start: " + buffer + "\n");
            //Get time end
            buffer = br.readLine();
            reportArea.append("End: " + buffer + "\n");
            
            //3.0 onwards
            //skip 25 garbage lines
            for(int i=0; i<25; ++i)
                br.readLine();
            //Get scores (we are finished when we find the text "")
            buffer = br.readLine();
            while(!buffer.equals("hprfheLhydkwdkz")){
                reportArea.append(buffer+ " ");
                buffer= br.readLine();
            }
            
            //skip remaining variable garbage lines
        }
        catch (FileNotFoundException ex){
            System.out.println("Error in reading lesson file.");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        
        if(e.getActionCommand().equals("Load new lesson submission")){
            try {
                readLessonReportFile();
            } catch (IOException ex) {
                Logger.getLogger(UICSA.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else{
            try {
                encryptLessonFile();
            } catch (IOException ex) {
                Logger.getLogger(UICSA.class.getName()).log(Level.SEVERE, null, ex);
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

    private void encryptLessonFile() throws IOException{
        JFileChooser browse = new JFileChooser();
        int temp = browse.showOpenDialog(top);
        File unencryptedFile;
            
        //handle if user selects a file
        if(temp == JFileChooser.APPROVE_OPTION){
            unencryptedFile = browse.getSelectedFile();
        }
        else{
            return;
        }
            
        try{
            FileReader fr = new FileReader(unencryptedFile);
            BufferedReader br = new BufferedReader(fr);
            ArrayList<String> buffer = new ArrayList<String>();
            
            //Get all lines
            buffer = (ArrayList<String>) Files.readAllLines(unencryptedFile.toPath(), Charset.forName("ISO-8859-1"));
            //Encrypt each line based on index in List
            for(int i=1; i<buffer.size(); ++i){
                switch(i % 3){
                    case 0:
                        buffer.set(i, encryptLine1(buffer.get(i)));
                    case 1:
                        buffer.set(i, encryptLine2(buffer.get(i)));
                    case 2:
                        buffer.set(i, encryptLine3(buffer.get(i)));
                }
            }
            
            
            //Write to file
            String filename = unencryptedFile.getName().substring(0, unencryptedFile.getName().length()-5)+"_E"+".spef";
            String path = new java.io.File( "." ).getCanonicalPath() + "\\";
            System.out.println(path+filename);
            File encryptedFile = new File(path+filename);
            BufferedWriter writer = new BufferedWriter(new FileWriter(encryptedFile));
            //Write all lines to file
            for(String s : buffer){
                writer.write(s);
                writer.newLine();
            }

            writer.close();
        }
        catch (FileNotFoundException ex){
            System.out.println("Error in reading lesson file.");
        }
    }

    private String encryptLine1(String s){
        String s2 = "";
        for(int i=s.length()-1; i>=0; i--){
            s2 += (char)(s.charAt(i) + 5);
        }
        return s2;
    }
    private String encryptLine2(String s){
        String s2 = "";
        for(int i=s.length()-1; i>=0; i--){
            if(i == s.length() / 2)
                s2 += (char)(s.charAt(i) - 5);
            else
                s2 += (char)(s.charAt(i) - 6);
        }
        return s2;
    }
    private String encryptLine3(String s){
        String s2 = "";
        for(int i=s.length()-1; i>=0; i--){
            s2 += (char)(s.charAt(i) + 4);
        }
        return s2;
    }
}
