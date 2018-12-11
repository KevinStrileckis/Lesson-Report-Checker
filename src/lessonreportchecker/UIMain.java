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
import java.net.URISyntaxException;
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
public class UIMain implements ActionListener {
    
    private JFrame top, topSide;
    private JButton load;
    private JTextArea reportArea;
    
    public UIMain(){
        setUp();
    }
    
    private void setUp(){
        top = new JFrame("Lesson Report Checker");
        top.setPreferredSize(new Dimension(150, 250));
        top.setLocation(400, 250);
        topSide = new JFrame("Lesson Report Checker");
        topSide.setPreferredSize(new Dimension(150, 250));
        topSide.setLocation(650, 250);
        
        
        load = new JButton("Load new lesson");
        load.addActionListener(this);
        top.add(load);
        
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
        try {
            //Set the default location to the parent directory. If this fails somehow, it will be Documents.
            browse.setCurrentDirectory(new File(UIMain.class.getProtectionDomain().getCodeSource().getLocation().toURI()));
            //Try to open the lessons folder. If this fails, then the selector will fall back on the parent directory
            browse.setCurrentDirectory(new File(UIMain.class.getProtectionDomain().getCodeSource().getLocation().toURI() + "\\Lessons"));
        } catch (URISyntaxException ex) {
            Logger.getLogger(UIMain.class.getName()).log(Level.SEVERE, null, ex);
        }
        
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
                buffer = br.readLine();
            
            //Unscramble name + some garbage
            buffer = br.readLine();
            buffer = unscramble(buffer);
            reportArea.append(buffer + "\n");
            
            //Skip 20 garbage lines
            for(int i=0; i<20;++i)
                buffer = br.readLine();
            
            //Get name -- do not unscramble
            buffer = br.readLine();
            reportArea.append(buffer + "\n");
            //Skip garbage line
            buffer = br.readLine();
            //Get name
            buffer = br.readLine();
            buffer = unscramble(buffer);
            reportArea.append(buffer + "\n");
            //Get score
            buffer = br.readLine();
            int j = (buffer.charAt(0) - 'a');
            reportArea.append(String.valueOf(j) + "\n");
            //Skip garbage
            buffer = br.readLine();
            //Get totalTotal -- do not unscramble
            buffer = br.readLine();
            reportArea.append(buffer + "\n");
            //Get totalAnswers -- do not unscramble
            buffer = br.readLine();
            reportArea.append(buffer + "\n");
            //Get time start
            buffer = br.readLine();
            reportArea.append(buffer + "\n");
            //Get time end
            buffer = br.readLine();
            reportArea.append(buffer + "\n");
            
            //Skip 66 garbage lines
            for(int i=0; i<66;++i)
                buffer = br.readLine();
        }
        catch (FileNotFoundException ex){
            System.out.println("Error in reading lesson file.");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            readLessonReportFile();
        } catch (IOException ex) {
            Logger.getLogger(UIMain.class.getName()).log(Level.SEVERE, null, ex);
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
        for(int i=s.length()-1; i>=0; i--){
            s2 += (char)(s.charAt(i) - 3);
        }
        return s2;
    }
}
