package src;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionListener;

import java.io.*;

import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.JTableHeader;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import java.util.Scanner;
import java.awt.Toolkit;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.awt.GridLayout;
import java.util.Collections;
import java.util.List;
import java.awt.Image;

import javax.swing.event.DocumentListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.ListSelectionEvent;


/**
 * Write a description of class main here.
 *
 * @author Vandell Vatel
 * @version v1.3.0
 */
public class Main
{
    private static JFrame frame;
    private static ArrayList<PsdButton> psds = new ArrayList<PsdButton>();
    private static File photoFile;
    private static boolean found_photoshop;
    private static File scriptsFolder;
    private static File saveData;
    private static File lastOpenLocation;
    protected static boolean start;
    protected static int startStep;
    protected static JFrame tutFrame;
    protected static JLabel stepLabel;
    protected static JTextPane detailPane;
    protected static boolean manualTut;
    private static JTabbedPane tPane;
    private static CardEdition selectedGroup;

    private static ArrayList<CardEdition> editions = new ArrayList<CardEdition>();
    private static int groupNum=1;

    public static void main(String[] args){
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double screenHeight = screenSize.getHeight();
        double screenWidth = screenSize.getWidth();
        found_photoshop=false;

        start = false;
        startStep=1;
        
        frame = new JFrame("MALCardNamer");
        frame.setIconImage(new ImageIcon("MAL_Logo.png").getImage());
        frame.setSize((int)(screenWidth*.75),(int)(screenHeight*.75));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //frame.setResizable(false);

        tPane = new JTabbedPane();
        tPane.addChangeListener(new ChangeListener(){
            @Override
            public void stateChanged(ChangeEvent e) {
                if(tPane.getSelectedIndex()!=-1){
                    selectedGroup=editions.get(tPane.getSelectedIndex());
                }
            }
        });
        frame.add(tPane);

        JLabel psdOpenLabel = new JLabel(new ImageIcon(new ImageIcon("OpenPsds.png").getImage().getScaledInstance((int)(frame.getWidth()*.25), -1, Image.SCALE_SMOOTH)));
        JLayeredPane framePane = new JLayeredPane();
        JPanel dragDropPanel = new JPanel();
        dragDropPanel.setBounds(0,0,frame.getWidth(),frame.getHeight());

        JMenuBar mb = new JMenuBar();
        JMenu m1 = new JMenu("File");
        JMenuItem m11= new JMenuItem("Open PSDs");
        JMenuItem m12= new JMenuItem("Read Webpage");
        
        m12.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent a){
                if(selectedGroup.getPsds().size()>0){
                    final JDialog dialog = new JDialog(frame,"Topic Contents",true);
                    JTextField cln = new JTextField("Cards",15);
                    cln.setHorizontalAlignment(JTextField.CENTER);
                    TitledBorder clnBorder = BorderFactory.createTitledBorder("Card Line Name");
                    clnBorder.setTitleJustification(TitledBorder.CENTER);
                    cln.setBorder(clnBorder);
                    cln.setBackground(dialog.getBackground());
                    JTextArea contents = new JTextArea(40,80);
                    TitledBorder contentsBorder = BorderFactory.createTitledBorder("Topic Page Contents");
                    contentsBorder.setTitleJustification(TitledBorder.CENTER);
                    JScrollPane scrollPane = new JScrollPane(contents);
                    scrollPane.setBorder(contentsBorder);
                    JButton done = new JButton("Done");
                    JButton cancel = new JButton("Cancel");

                    if(start&&startStep==3&&!manualTut){
                        startStep=4;
                        stepLabel.setText("Step 4: Card Line Name");
                        detailPane.setText("The first thing you should do is fill out the name of the card line.\nThis is the line that a card requester would write what cards they want on.\nI.e. DawnofNyx:1,2,3 -> DawnofNyx | Cards:3,5,7 -> Cards\nPlease type DawnofNyx into the text field.");
                        done.setEnabled(false);
                        contents.setEnabled(false);
                    }

                    cln.getDocument().addDocumentListener(new DocumentListener(){
                        @Override
                        public void insertUpdate(DocumentEvent e) {
                            if(start){
                                if(cln.getText().equals("DawnofNyx")){
                                    startStep=5;
                                    stepLabel.setText("Step 5: Topic Page Contents");
                                    detailPane.setText("Finally, copy your request form from the topic page\nMake sure you copy your form entirely.\nPaste your form into the Topic Page Contents Text Area\nWhen you're ready, just press the done button.");
                                    done.setEnabled(true);
                                    contents.setEnabled(true);
                                }
                                else{
                                    startStep=4;
                                    stepLabel.setText("Step 4: Card Line Name");
                                    detailPane.setText("The first thing you should do is fill out the name of the card line.\nThis is the line that a card requester would write what cards they want on.\nI.e. DawnofNyx:1,2,3 -> DawnofNyx | Cards:3,5,7 -> Cards\nPlease type DawnofNyx into the text field.");
                                    done.setEnabled(false);
                                    contents.setEnabled(false);
                                }
                            }
                        }

                        @Override
                        public void removeUpdate(DocumentEvent e) {
                            if(start){
                                if(cln.getText().equals("DawnofNyx")){
                                    startStep=5;
                                    stepLabel.setText("Step 5: Topic Page Contents");
                                    detailPane.setText("Finally, copy your request form from the topic page\nMake sure you copy your form entirely.\nPaste your form into the Topic Page Contents Text Area\nWhen you're ready, just press the done button.");
                                    done.setEnabled(true);
                                    contents.setEnabled(true);
                                }
                                else{
                                    startStep=4;
                                    stepLabel.setText("Step 4: Card Line Name");
                                    detailPane.setText("The first thing you should do is fill out the name of the card line.\nThis is the line that a card requester would write what cards they want on.\nI.e. DawnofNyx:1,2,3 -> DawnofNyx | Cards:3,5,7 -> Cards\nPlease type DawnofNyx into the text field.");
                                    done.setEnabled(false);
                                    contents.setEnabled(false);
                                }
                            }
                        }

                        @Override
                        public void changedUpdate(DocumentEvent e) { 
                        }
                    });

                    done.addActionListener(new ActionListener(){
                        public void actionPerformed(ActionEvent a){
                            if(!start||(cln.getText().equals("DawnofNyx"))){
                                boolean searchForNames=false;
                                boolean searchForCards=false;
                                String firstName=null;
                                String secondName=null;
                                CardUser newUser=null;
                                String cardMaker = cln.getText();
                                if(cardMaker!=null&&cardMaker.length()>0){
                                    for(var i=0; i<contents.getText().split("\\n").length;i++){
                                        String nextLine = contents.getText().split("\\n")[i];
                                        if(nextLine.contains("Posts:")){
                                            searchForNames=true;
                                            searchForCards=false;
                                            firstName=null;   
                                            secondName=null; 
                                            newUser=null;
                                        }
                                        if(searchForNames){
                                            if((nextLine.contains("name")||nextLine.contains("Name"))){
                                                int nameIndex = nextLine.toLowerCase().indexOf("name");
                                                String firstNameLine = nextLine.substring(nameIndex+4);
                                                if(firstNameLine.indexOf(":")!=-1){
                                                    firstNameLine = firstNameLine.substring(firstNameLine.indexOf(":"));
                                                }
                                                firstName=firstNameLine.replace(":","").trim();
                                                if(firstName.contains(",")){
                                                    secondName=firstName.substring(firstName.indexOf(",")+1);
                                                    firstName=firstName.substring(0,firstName.indexOf(","));
                                                }
                                                else if(firstName.contains("/")){
                                                    secondName=firstName.substring(firstName.indexOf("/")+1);
                                                    firstName=firstName.substring(0,firstName.indexOf("/"));
                                                }
                                                else if(firstName.contains("|")){
                                                    secondName=firstName.substring(firstName.indexOf("|")+1);
                                                    firstName=firstName.substring(0,firstName.indexOf("/"));
                                                }
                                                else{
                                                    String altLine = contents.getText().split("\\n")[i+1];
                                                    if((altLine.contains("name")||altLine.contains("Name"))){
                                                        int altIndex = altLine.toLowerCase().indexOf("name");
                                                        String secondNameLine = altLine.substring(altIndex+4);
                                                        if(secondNameLine.indexOf(":")!=-1){
                                                            secondNameLine = secondNameLine.substring(secondNameLine.indexOf(":"));
                                                        }
                                                        secondName=secondNameLine.replace(":","").trim();
                                                        if(secondName.contains(",")){
                                                            secondName=secondName.substring(0,secondName.indexOf(","));
                                                        }
                                                        else if(secondName.contains("/")){
                                                            secondName=secondName.substring(0,secondName.indexOf("/"));
                                                        }
                                                        else if(secondName.contains("|")){
                                                            secondName=secondName.substring(0,secondName.indexOf("|"));
                                                        }
                                                    }
                                                }
                                                if(firstName.length()<1&&secondName!=null&secondName.length()>0){
                                                    firstName=secondName;
                                                    secondName=null;
                                                }
                                                if(!firstName.equals("[")){
                                                    if(secondName!=null){
                                                        secondName=secondName.trim();
                                                    }
                                                    newUser=selectedGroup.createUser(firstName, secondName);
                                                    searchForNames=false;
                                                    searchForCards=true;
                                                }
                                            }
                                        }
                                        else if(searchForCards){
                                            if(newUser!=null){
                                                if(nextLine.toLowerCase().contains(cardMaker.toLowerCase())&&nextLine.toLowerCase().contains("all")){
                                                    for(PsdButton psd: selectedGroup.getPsds()){
                                                        if(!newUser.cards.contains(psd)){
                                                            newUser.cards.add(psd);
                                                            newUser.model.addElement(psd.name);
                                                        }
                                                    }
                                                }
                                                else{
                                                    for(PsdButton psd : selectedGroup.getPsds()){
                                                        if(nextLine.toLowerCase().contains(cardMaker.toLowerCase())){
                                                            if(isNumber(psd.name.replace(".psd",""))){
                                                                ArrayList<String> numsInLine = new ArrayList<String>();
                                                                for(int start=0; start<nextLine.length(); start++){
                                                                    if(Character.isDigit(nextLine.charAt(start))&&(start<1||!Character.isLetter(nextLine.charAt(start-1)))){
                                                                        String newNum="";
                                                                        int end=start+1;
                                                                        int endIndex=0;
                                                                        while(end+endIndex<nextLine.length()&&Character.isDigit(nextLine.charAt(end+endIndex))){
                                                                            endIndex++;
                                                                        }
                                                                        newNum=nextLine.substring(start, end+endIndex);
                                                                        numsInLine.add(newNum);
                                                                        start=end;
                                                                    }
                                                                }
                                                                if(numsInLine.contains(psd.name.replace(".psd",""))){
                                                                    if(!newUser.cards.contains(psd)){
                                                                        newUser.cards.add(psd);
                                                                        newUser.model.addElement(psd.name);
                                                                    }
                                                                }
                                                            }
                                                            else if(nextLine.toLowerCase().contains(psd.name.toLowerCase().replace(".psd",""))){
                                                                if(!newUser.cards.contains(psd)){
                                                                    newUser.cards.add(psd);
                                                                    newUser.model.addElement(psd.name);
                                                                }
                                                            }
                                                            else{
                                                                String cardLine = nextLine.substring(nextLine.toLowerCase().indexOf(cardMaker.toLowerCase())+cardMaker.length());
                                                                if(psd.altName.length()>0){
                                                                    if(isNumber(psd.altName)){
                                                                        ArrayList<String> numsInLine = new ArrayList<String>();
                                                                        for(int start=0; start<nextLine.length(); start++){
                                                                            if(Character.isDigit(nextLine.charAt(start))&&(start<1||!Character.isLetter(nextLine.charAt(start-1)))){
                                                                                String newNum="";
                                                                                int end=start+1;
                                                                                int endIndex=0;
                                                                                while(end+endIndex<nextLine.length()&&Character.isDigit(nextLine.charAt(end+endIndex))){
                                                                                    endIndex++;
                                                                                }
                                                                                newNum=nextLine.substring(start, end+endIndex);
                                                                                numsInLine.add(newNum);
                                                                                start=end;
                                                                            }
                                                                        }
                                                                        if(numsInLine.contains(psd.altName)){
                                                                            if(!newUser.cards.contains(psd)){
                                                                                newUser.cards.add(psd);
                                                                                newUser.model.addElement(psd.name);
                                                                            }
                                                                        }
                                                                    }
                                                                    else if(cardLine.toLowerCase().contains(psd.altName.toLowerCase().replace(".psd",""))){
                                                                        if(!newUser.cards.contains(psd)){
                                                                            newUser.cards.add(psd);
                                                                            newUser.model.addElement(psd.name);
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                    ArrayList<String> basePSDs = new ArrayList<String>();
                                                    for(PsdButton psd : selectedGroup.getPsds()){
                                                        int index=1;
                                                        String endNumber="";
                                                        String psdName = psd.name.replace(".psd","");
                                                        if(!isNumber(psdName)){
                                                            while(psdName.length()-index>=0&&Character.isDigit(psdName.charAt(psdName.length()-index))){
                                                                endNumber+=psdName.charAt(psdName.length()-index);
                                                                index++;
                                                            }
                                                            if(endNumber.length()>0){
                                                                String basePSD = psdName.substring(0, psdName.length()-endNumber.length());
                                                                if(!basePSDs.contains(basePSD)){
                                                                    basePSDs.add(basePSD);
                                                                }
                                                            }
                                                        }
                                                    }
                                                    if(nextLine.toLowerCase().contains(cardMaker.toLowerCase())){
                                                        for(String base : basePSDs){
                                                            int fromIndex=0;
                                                            while(nextLine.indexOf(base,fromIndex)!=-1){
                                                                ArrayList<String> nums = new ArrayList<String>();
                                                                for(int start=nextLine.indexOf(base,fromIndex)+base.length(); start<nextLine.length(); start++){
                                                                    if(Character.isDigit(nextLine.charAt(start))){
                                                                        String newNum="";
                                                                        int end=start+1;
                                                                        int endIndex=0;
                                                                        while(end+endIndex<nextLine.length()&&Character.isDigit(nextLine.charAt(end+endIndex))){
                                                                            endIndex++;
                                                                        }
                                                                        newNum=nextLine.substring(start, end+endIndex);
                                                                        nums.add(newNum);
                                                                        start=end;
                                                                    }
                                                                    else if(Character.isLetter(nextLine.charAt(start))){
                                                                        break;
                                                                    }
                                                                }
                                                                for(String num : nums){
                                                                    String newName = base+num;
                                                                    for(PsdButton psd: selectedGroup.getPsds()){
                                                                        String psdName = psd.name.replace(".psd","");
                                                                        if(psdName.equals(newName)){
                                                                            if(!newUser.cards.contains(psd)){
                                                                                newUser.cards.add(psd);
                                                                                newUser.model.addElement(psd.name);
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                                fromIndex=nextLine.indexOf(base,fromIndex)+1;
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    dialog.dispose();
                                    if(selectedGroup.getAddedUsers().size()>0&&selectedGroup.getAddedUsers().get(0).cards.size()>0){
                                        if(start&&startStep==5){
                                            m12.setEnabled(false);
                                            startStep=6;
                                            stepLabel.setText("Step 6: Save Script");
                                            detailPane.setText("The last step in this program is to save your script.\nGo to File->Save Script and click on it.\nYou will be prompted to select a save location, the place where your cards will be saved\nI reccomend saving them on your desktop.\nIf you receieve a message saying your script was saved successfully, you're set to move on.\nIf you didn't... contact me.");
                                        }
                                    }
                                    else{
                                        if(start){
                                            tutFrame.dispose();
                                        }
                                        JOptionPane.showMessageDialog(frame, "Something went wrong with reading your requests.", "No Requests Found", JOptionPane.ERROR_MESSAGE);
                                    }
                                    
                                }
                                else{
                                    JOptionPane.showMessageDialog(frame,"Please input a valid card line name","Card Line Name Error",JOptionPane.ERROR_MESSAGE);
                                }
                            }
                            else{
                                JOptionPane.showMessageDialog(frame,"Please input DawnofNyx as the card line name","DawnofNyx Missing Error",JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    });
                    cancel.addActionListener(new ActionListener(){
                        public void actionPerformed(ActionEvent a){
                            dialog.dispose();
                        }
                    });
                    dialog.add(BorderLayout.NORTH,cln);
                    dialog.add(BorderLayout.CENTER,scrollPane);
                    JPanel btnPanel = new JPanel(new FlowLayout());
                    btnPanel.add(done);
                    if(!start){
                        btnPanel.add(cancel);
                    }
                    dialog.add(BorderLayout.SOUTH,btnPanel);
                    dialog.pack();
                    dialog.setLocationRelativeTo(frame);
                    dialog.setVisible(true);
                }
                else{
                    JOptionPane.showMessageDialog(frame, "Please open some psds first!", "No PSDs Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        JMenuItem m13 = new JMenuItem("Save Script");
        
        m13.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent a){
                if((start&&startStep==6)||!start){
                    boolean atLeastOne=false;
                    for(CardEdition group: editions){
                        if(group.getAddedUsers().size()>0){
                            for(CardUser u: group.getAddedUsers()){
                                if(u.model.size()>0){
                                    atLeastOne=true;
                                    break;
                                }
                            }
                        }
                    }
                    if(atLeastOne){
                        boolean error=false;
                        JFileChooser tempSaver = new JFileChooser(new File(System.getProperty("user.dir")).getParentFile());
                        tempSaver.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                        if(tempSaver.showDialog(frame,"Save Location")==JFileChooser.APPROVE_OPTION){
                            File saveLocation = tempSaver.getSelectedFile();
                            InputStream in = getClass().getResourceAsStream("rename.jsx");
                            File renameFile = new File(scriptsFolder.getAbsolutePath()+"\\RenameMalCards.jsx");
                            ArrayList<String> renameContents = new ArrayList<String>();
                            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                            boolean addToArray=true;
                            String newLine = null;
                            try{
                                newLine = reader.readLine();
                            }
                            catch(IOException e){
                                error=true;
                                JOptionPane.showMessageDialog(frame, e, "ReadRenameFileError", JOptionPane.ERROR_MESSAGE);
                            }
                            while(newLine!=null&&!error){
                                if(addToArray || newLine.equals("//start of code")){
                                    renameContents.add(newLine+"\n");
                                    if(newLine.equals("//start of data")){
                                        addToArray=false;
                                    }
                                    else if(newLine.equals("//start of code")){
                                        addToArray=true;
                                    }
                                }
                                try{
                                    newLine = reader.readLine();
                                }
                                catch(IOException e){
                                    error=true;
                                    JOptionPane.showMessageDialog(frame, e, "ReadRenameFileError", JOptionPane.ERROR_MESSAGE);
                                }
                            }
                            try {
                                reader.close();
                            } catch (IOException e) {
                                error=true;
                                JOptionPane.showMessageDialog(frame, e, "FileReaderCloseError", JOptionPane.ERROR_MESSAGE);
                            }
                            if(!error){
                                ArrayList<String> data = new ArrayList<String>();
                                data.add("var groups = {\n");
                                for(int g=0; g<editions.size(); g++){
                                    CardEdition tempED = editions.get(g);
                                    data.add("'"+g+"' : {'users':\n         {\n");
                                    for(int i=0; i<tempED.getAddedUsers().size(); i++){
                                        CardUser tempUser = tempED.getAddedUsers().get(i);
                                        String cardInput = "            '"+i+"' : {'names':['"+tempUser.names[0]+"'";
                                        if(tempUser.names.length>1){
                                            cardInput+=",'"+tempUser.names[1]+"'";
                                        }
                                        cardInput+="],cards:[";
                                        for(int j=0; j<tempUser.model.size(); j++){
                                            if(j<tempUser.model.size()-1){
                                                cardInput+="'"+String.valueOf(tempUser.model.get(j))+"',";
                                            }
                                            else{
                                                cardInput+="'"+String.valueOf(tempUser.model.get(j))+"'";
                                            }
                                        }
                                        cardInput+="]},\n";
                                        data.add(cardInput);
                                    }
                                    data.add("      }\n      ,'psds':\n       {\n");
                                    for(int p=0; p<tempED.getPsds().size(); p++){
                                        PsdButton psd = tempED.getPsds().get(p);
                                        String psdInput;
                                        psdInput = "            '"+p+"' : {'name':'"+psd.name+"','path':'"+psd.file.getAbsolutePath().replace("\\","\\\\")+"','limit':"+psd.charLimit+",'repString':"+psd.repString+",'replaceWord':'"+psd.replaceString+"','repLayer':"+psd.repLayer+",'replaceLayer':'"+psd.replaceLayer+"'";
                                        psdInput+="},\n";
                                        data.add(psdInput);
                                    }
                                    data.add("      }\n     ,'name' : '"+tempED.getName()+"'\n      },\n");
                                }
                                data.add("};\n\n");
                                String savePath = saveLocation.getAbsolutePath();
                                data.add("var save_location = '"+savePath.replace("\\","\\\\")+"';\n");
                                renameContents.addAll(1,data);
                                FileWriter cmdWriter = null;
                                try {
                                    cmdWriter = new FileWriter(renameFile);
                                } catch (IOException e) {
                                    error=true;
                                    JOptionPane.showMessageDialog(frame, e, "FileWriterCreationError", JOptionPane.ERROR_MESSAGE);
                                }
                                if(!error){
                                    for(String line: renameContents){
                                        try {
                                            cmdWriter.write(line);
                                        } catch (IOException e) {
                                            error=true;
                                            JOptionPane.showMessageDialog(frame, e, "FileWriterWritingError", JOptionPane.ERROR_MESSAGE);
                                            break;
                                        }
                                    }
                                    try {
                                        cmdWriter.close();
                                    } catch (IOException e) {
                                        error=true;
                                        JOptionPane.showMessageDialog(frame, e, "FileWriterCloseError", JOptionPane.ERROR_MESSAGE);
                                    }
                                    if(!error){
                                        if(start&&startStep==6){
                                            startStep=7;
                                            stepLabel.setText("Step 7: Run Script in Photoshop");
                                            if(manualTut){
                                                detailPane.setText("Open Photoshop (if you have multiple photoshops, open the one whose scripts folder you selected).\nGo to File->Scripts->RenameMalCards and click it.\nWatch your cards get named.\nThis concludes the manual tutorial. Please consider posting these cards on the OMC Creators Thread.\nClick Done to close the tutorial.");
                                            }
                                            else{
                                                detailPane.setText("Open Photoshop (if you have multiple photoshops, open the one whose scripts folder you selected).\nGo to File->Scripts->RenameMalCards and click it.\nWatch your cards get named.\nThis concludes the automatic tutorial. Please consider posting these cards on the OMC Creators Thread.\nClick Done to close the tutorial.");
                                            }
                                            JButton done = new JButton("Done");
                                            done.addActionListener(new ActionListener(){
                                                public void actionPerformed(ActionEvent a){
                                                    tutFrame.dispose();
                                                }
                                            });
                                            tutFrame.add(BorderLayout.SOUTH,done);
                                            tutFrame.pack();
                                        }
                                        JOptionPane.showMessageDialog(frame, "Script saved successfully.", "Script Notification",JOptionPane.INFORMATION_MESSAGE);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        });
        
        m1.add(m11);
        m1.add(m12);
        m1.add(m13);

        JMenu m2 = new JMenu("Editions");
        JMenuItem m21 = new JMenuItem("Add Edition");
        JPanel edPanel = new JPanel();
        edPanel.setLayout(new BoxLayout(edPanel, BoxLayout.PAGE_AXIS));
        m21.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                String edName = JOptionPane.showInputDialog(frame, "Please input the name of the edition:", "Group"+groupNum);
                if(edName!=null){
                    boolean nameExists = false;
                    for(CardEdition ed : editions){
                        if (ed.getName().equals(edName)){
                            nameExists=true;
                            break;
                        }
                    }
                    if(!nameExists){
                        CardEdition newEdition = new CardEdition(frame,edName);
                        editions.add(newEdition);
                        tPane.add(newEdition,edName);
                        tPane.setSelectedComponent(newEdition);
                        groupNum++;
                        JTextField edField = new JTextField(edName);
                        edField.addFocusListener(new FocusListener(){
                            @Override
                            public void focusGained(FocusEvent e) {
                                edField.setText("");
                            }

                            @Override
                            public void focusLost(FocusEvent e) {
                                String newName = edField.getText();
                                boolean nameExists = false;
                                for(CardEdition ed : editions){
                                    if (ed.getName().equals(newName)){
                                        nameExists=true;
                                        break;
                                    }
                                }
                                if(!nameExists&&newName.length()>0){
                                    newEdition.setName(newName);
                                    tPane.setTitleAt(tPane.getComponentZOrder(newEdition),newName);
                                }
                                else{
                                    edField.setText(newEdition.getName());
                                }
                            }
                        });
                        edField.addMouseListener(new MouseAdapter(){
                            public void mousePressed(MouseEvent m){
                                if(SwingUtilities.isRightMouseButton(m)){
                                    tPane.remove(newEdition);
                                    edPanel.remove(edField);
                                    m2.getPopupMenu().setVisible(false);
                                    m2.getPopupMenu().updateUI();
                                    m2.getPopupMenu().setVisible(true);
                                }
                            }
                        });
                        edPanel.add(edField);
                        if(start&&startStep==1){
                            startStep=2;
                            m2.setEnabled(false);
                            m1.setEnabled(true);
                            stepLabel.setText("Step 2: Open Psds");
                            detailPane.setText("Now, open all the sample psds that I included with the program.\nFile->Open PSDs->1,2,3.psd");
                        }
                    }
                    else{
                        JOptionPane.showMessageDialog(frame, "This edition name is already in use.", "Duplicate Edition Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } 
        });

        m2.add(edPanel);
        m2.add(m21);
        
        JMenu m3 = new JMenu("Help");
        JMenuItem m31 = new JMenuItem("Tutorial");

        m3.add(m31);
        mb.add(m1);
        mb.add(m2);
        mb.add(m3);

    

        frame.add(BorderLayout.NORTH,mb);

        frame.addMouseListener(new MouseAdapter(){
            public void mousePressed(MouseEvent m){
                frame.requestFocusInWindow();
            }
        });

        frame.setVisible(true);
        m11.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent a){
                if(editions.size()>0){
                    final JFileChooser psdSelecter = new JFileChooser(new File(System.getProperty("user.dir")).getParentFile());
                    if(start==false){
                        if(lastOpenLocation!=null){
                            psdSelecter.setCurrentDirectory(lastOpenLocation);
                        }
                        psdSelecter.setMultiSelectionEnabled(true);
                        psdSelecter.setFileFilter(new FileNameExtensionFilter("Photoshop Projects","psd"));
                    }
                    else{
                        File samplesFolder = new File("Samples");
                        if(samplesFolder.exists()){
                            psdSelecter.setCurrentDirectory(samplesFolder);
                            psdSelecter.setMultiSelectionEnabled(true);
                        }
                        else{
                            if(tutFrame!=null){
                                tutFrame.dispose();
                            }
                        }
                    }
                    ArrayList<File> chosenFiles = new ArrayList<File>();
                    psdSelecter.addPropertyChangeListener(JFileChooser.SELECTED_FILES_CHANGED_PROPERTY,new PropertyChangeListener(){
                        public void propertyChange(PropertyChangeEvent pc){
                            List<File> selected = Arrays.asList(psdSelecter.getSelectedFiles());
                            for(File chosen : chosenFiles){
                                if(!selected.contains(chosen)){
                                    chosenFiles.remove(chosen);
                                }
                            }
                            for(File select: selected){
                                if(!chosenFiles.contains(select)){
                                    chosenFiles.add(select);
                                }
                            }
                        }
                    });
                    if(psdSelecter.showOpenDialog(frame)==JFileChooser.APPROVE_OPTION){
                        ArrayList<String> chosenNames = new ArrayList<String>();
                        for(File f: chosenFiles){
                            chosenNames.add(f.getName());
                        }
                        if(!start||(start&&chosenNames.contains("1.psd")&&chosenNames.contains("2.psd")&&chosenNames.contains("3.psd"))){
                            
                            PsdButton[] psdBtns  = new PsdButton[chosenFiles.size()];
                            for(int f=0; f<chosenFiles.size(); f++){
                                psdBtns[f]=new PsdButton(chosenFiles.get(f));
                                psds.add(psdBtns[f]);
                            }
                            lastOpenLocation = chosenFiles.get(0).getParentFile();
                            selectedGroup.addPsds(psdBtns);
                            frame.validate();
                            frame.repaint();
                            
                            if(start&&startStep==2){
                                startStep=3;
                                if(manualTut){
                                    stepLabel.setText("Step 3: Add User");
                                    detailPane.setText("Now, add a user, a.k.a the person that requested the cards in the search bar.\nThe format for adding a new user is:\n \"defaultName,alternateName\"\nLet's start by adding yourself as a user.\n Type in your default and alternate names in the above format.\n I.e. this is how mine would look. DawnofNyx,Dawn\nOnce you're done, just hit enter.");
                                }
                                else{
                                    stepLabel.setText("Step 3: Read Webpage");
                                    detailPane.setText("Now, if you haven't already, open the sample request page I made in the MALCardNamer club.\nCreate your own request and keep that webpage open.\nNow navigate to File->Read Webpage in the program and click it.");
                                    m12.setEnabled(true);
                                }
                                tutFrame.pack();
                                m11.setEnabled(false);
                                for(PsdButton psd : selectedGroup.getPsds()){
                                    psd.setEnabled(false);
                                }
                                tutFrame.validate();
                                tutFrame.repaint();
                            }
                            
                        }
                        else{
                            JOptionPane.showMessageDialog(frame, "Please select all the sample psds\n1.psd,2.psd, and 3.psd", "Missing Samples Error", JOptionPane.ERROR_MESSAGE);
                        }
                        
                    }
                }
                else{
                    JOptionPane.showMessageDialog(frame, "Please create an Edition first.", "No Editions Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        m31.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent a){
                Object[] options = {"Manual","Automatic","Cancel"};
                int opt = JOptionPane.showOptionDialog(frame,"Would you like to take the manual or automatic tutorial?","Tutorial",JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE,null,options,options[0]);
                if(opt==JOptionPane.YES_OPTION||opt==JOptionPane.NO_OPTION){
                    if(opt==JOptionPane.YES_OPTION){
                        manualTut=true;
                    }
                    else{
                        manualTut=false;
                    }
                    start=true;
                    startStep=1;
                    tPane.removeAll();
                    editions.clear();
                    edPanel.removeAll();
                    m1.setEnabled(false);
                    m12.setEnabled(false);
                    m3.setEnabled(false);
                    psds.clear();
                    frame.requestFocusInWindow();
                    tutFrame = new JFrame("Tutorial Window");
                    tutFrame.setResizable(false);
                    tutFrame.setAlwaysOnTop(true);
                    stepLabel = new JLabel();
                    stepLabel.setHorizontalAlignment(JLabel.CENTER);
                    stepLabel.setForeground(Color.CYAN);
                    stepLabel.setText("Step 1: Create Edition");
                    tutFrame.add(BorderLayout.NORTH,stepLabel);
                    detailPane = new JTextPane();
                    StyledDocument doc = detailPane.getStyledDocument();
                    SimpleAttributeSet center = new SimpleAttributeSet();
                    StyleConstants.setAlignment(center,StyleConstants.ALIGN_CENTER);
                    doc.setParagraphAttributes(0,doc.getLength(),center,false);
                    detailPane.setEditable(false);
                    detailPane.setText("First, go to Editions->Add Edition and create a group for your psds.\nSince we're using my club's OMCs, you could name this edition:\n'MALCardNamerOMC'");
                    tutFrame.add(BorderLayout.CENTER,detailPane);
                    tutFrame.pack();
                    tutFrame.setLocationRelativeTo(frame);
                    tutFrame.setLocation(frame.getWidth()-tutFrame.getContentPane().getWidth(),0);
                    tutFrame.setVisible(true);
                    tutFrame.addWindowListener(new WindowAdapter(){
                        public void windowClosing(WindowEvent w){
                            start=false;
                            startStep=1;
                            m11.setEnabled(true);
                            m12.setEnabled(true);
                            m13.setEnabled(true);
                            m2.setEnabled(true);
                            m3.setEnabled(true);
                            m1.setEnabled(true);
                            tPane.removeAll();
                            edPanel.removeAll();
                            editions.clear();
                        }
                        public void windowClosed(WindowEvent w){
                            start=false;
                            startStep=1;
                            m11.setEnabled(true);
                            m12.setEnabled(true);
                            m13.setEnabled(true);
                            m2.setEnabled(true);
                            m3.setEnabled(true);
                            m1.setEnabled(true);
                            tPane.removeAll();
                            edPanel.removeAll();
                            editions.clear();
                        }
                    });

                }
            }
        });  
        
       
       saveData = new File("saveData.txt");
       try{
           saveData.createNewFile();
           Scanner pathReader = new Scanner(saveData);
           if(pathReader.hasNextLine()){
               String path = pathReader.nextLine();
               pathReader.close();
               if(!path.endsWith("Scripts")){
                   found_photoshop=false;
                   photoFile=null;
                   getPhotoFile();
                    for(ActionListener a : m31.getActionListeners()){
                        a.actionPerformed(new ActionEvent(Main.class,ActionEvent.ACTION_PERFORMED,null));  
                    }
                }
                else{
                    scriptsFolder=new File(path);
                }
            }
            else{
                pathReader.close();
                found_photoshop=false;
                photoFile=null;
                getPhotoFile();
                for(ActionListener a : m31.getActionListeners()){
                    a.actionPerformed(new ActionEvent(Main.class,ActionEvent.ACTION_PERFORMED,null));  
                }
            }

        } catch(IOException e){
            System.out.println("Something went wrong. >-<");
        }     
    }

    private static boolean isNumber(String s){
        try{
            Integer.parseInt(s);
            return true;
        }
        catch (NumberFormatException e){
            return false;
        }
    }
    
    private static void getPhotoFile(){
        int returnVal = JOptionPane.showConfirmDialog(frame, "Are you using Adobe Photoshop?", "Initial Setup", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if(returnVal==JOptionPane.YES_OPTION){
            final JDialog dialog = new JDialog(frame,"Path Selector",true);
            
            JTextField path = new JTextField(40);
            TitledBorder pathBorder = BorderFactory.createTitledBorder("Please select the path to your Photoshop scripts folder.");
            pathBorder.setTitleJustification(TitledBorder.CENTER);
            path.setBorder(pathBorder);
            path.setBackground(dialog.getBackground());
            path.setEditable(true);
            path.setHorizontalAlignment(JTextField.CENTER);

            JButton done = new JButton("Done");
            done.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent a){
                    dialog.dispose();
                    try{
                        saveData.createNewFile();
                        FileWriter pathWriter = new FileWriter(saveData);
                        pathWriter.write(scriptsFolder.getAbsolutePath());
                        pathWriter.close();
                    } catch(IOException e){
                        System.out.println("Something went wrong >-<");
                    }
                }
            });
            done.setEnabled(false);
            path.getDocument().addDocumentListener(new DocumentListener(){
                public void changedUpdate(DocumentEvent d){

                }
                public void insertUpdate(DocumentEvent d){
                    if(path.getText().endsWith("Scripts")){
                        done.setEnabled(true);
                        scriptsFolder=new File(path.getText());
                    }
                    else if(scriptsFolder==null){
                        done.setEnabled(false);
                    }
                }
                public void removeUpdate(DocumentEvent d){
                    if(path.getText().endsWith("Scripts")){
                        done.setEnabled(true);
                        scriptsFolder=new File(path.getText());
                    }
                    else if(scriptsFolder==null){
                        done.setEnabled(false);
                    }
                }
            });
            
            JButton test = new JButton("Search");
            test.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent a){
                    String currDir = System.getProperty("user.dir");
                    String startDir = currDir.substring(0, currDir.indexOf("\\")+1);
                    File directory = new File(startDir);
                    searchPhotoshop(directory,path,done);
                }
            });

            JButton select = new JButton("Select");
            select.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent a){
                    JFileChooser pathChooser = new JFileChooser();
                    if(scriptsFolder!=null){
                        if(scriptsFolder.getParentFile()!=null){
                            pathChooser.setCurrentDirectory(scriptsFolder.getParentFile().getParentFile());
                        }
                    }
                    pathChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    if(pathChooser.showOpenDialog(dialog)==JFileChooser.APPROVE_OPTION){
                        String getPath = pathChooser.getSelectedFile().getAbsolutePath();
                        File f = new File(getPath);
                        if(f.getName().equals("Scripts")){
                            path.setText(getPath);
                            scriptsFolder=f;
                        }
                        done.setEnabled(true);
                    };
                }
            });
            
            
            
            JPanel textPanel = new JPanel();
            textPanel.add(path);

            JPanel buttonPanel = new JPanel();
            buttonPanel.add(test);
            buttonPanel.add(select);
            buttonPanel.add(done);
            
            dialog.add(BorderLayout.NORTH,textPanel);
            dialog.add(BorderLayout.SOUTH,buttonPanel);

            dialog.addWindowListener(new WindowAdapter(){
                public void windowClosing(WindowEvent w){
                    System.exit(0);
                }
            });
            
            dialog.pack();
            dialog.setIconImage(new ImageIcon("Photoshop_Logo.png").getImage());
            dialog.setLocationRelativeTo(frame);
            dialog.setVisible(true);
        }
        else{
            JOptionPane.showMessageDialog(frame,"Then get it.","No Photoshop Error",JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }
    
    private static void searchPhotoshop(File directory, JTextField tf,JButton doneBtn){
        File[] flist = directory.listFiles(new FileFilter(){
            public boolean accept(File pathname){
                if(pathname.getName().endsWith(".exe")){
                    return true;
                }
                return false;
            }
        });
        for(File f : flist){
            if(f.getName().toLowerCase().equals("photoshop.exe")){
                found_photoshop=true;
                File photoshopFolder = f.getParentFile();
                for(File folder: photoshopFolder.listFiles()){
                    if(folder.getName().equals("Presets")){
                        for(File f2: folder.listFiles()){
                            if(f2.getName().equals("Scripts")){
                                scriptsFolder=f2;
                                tf.setText(scriptsFolder.getAbsolutePath());
                                doneBtn.setEnabled(true);
                            }
                        }
                    }
                }
            }
        }
        if(!found_photoshop){
            ArrayList<Thread> threadList = new ArrayList<Thread>();
            for(File f : directory.listFiles()){
                if(f.listFiles()!=null&&f.listFiles().length>0){
                    threadList.add(new Thread(new Runnable(){
                        public void run(){
                            if(!found_photoshop){
                                searchPhotoshop(f,tf,doneBtn);
                            }
                        }

                    }));
                }
            }
            for(Thread t: threadList){
                t.start();
            }
        }
    }
}
