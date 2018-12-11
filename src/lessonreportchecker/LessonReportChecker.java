/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lessonreportchecker;

import javax.swing.JOptionPane;

/**
 *
 * @author Kevin Strileckis
 */
public class LessonReportChecker {
    
    public static void main(String[] args) {
        String[] options = {"CSA", "CSP"};
        
        //Get the selection from the user (probably only me)
        int sel = JOptionPane.showOptionDialog(null, "Choose class to check", "Hey! Listen!", 
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        
        switch(sel)
        {
            case 0:
                UICSA uicsa = new UICSA();
                break;
            case 1:
                UICSP uicsp = new UICSP();
                break;
        }
    }
}
