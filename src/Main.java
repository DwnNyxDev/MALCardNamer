package src;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionListener;

import java.io.*;

import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.DefaultStyledDocument.ElementSpec;

import java.util.Scanner;
import java.awt.Toolkit;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.awt.GridLayout;
import java.util.Collections;
import java.util.List;

import javax.swing.SwingUtilities;
import java.awt.Image;

import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.ListSelectionEvent;


/**
 * Write a description of class main here.
 *
 * @author Vandell Vatel
 * @version v1.0.0
 */
public class Main
{
    private static JFrame frame;
    private static ArrayList<PsdButton> psds = new ArrayList<PsdButton>();
    private static File photoFile;
    private static boolean found_photoshop;
    private static File scriptsFolder;
    private static File saveData;
    private static JPanel psdPanel;
    private static JPanel buttonPanel;
    private static JPanel psdSettingsPanel;
    private static JPanel userPanel;
    private static JPanel listedUsersPanel;
    private static ArrayList<String> savedUsers = new ArrayList<String>();
    private static ArrayList<CardUser> addedUsers = new ArrayList<CardUser>();
    private static File lastOpenLocation;
    private static boolean useGlobalSettings;
    private static int globalCLimit;
    private static String globalReplaceString;
    private static boolean ctrlHeld;
    private static boolean shiftHeld;
    private static int lastSelectedIndex = 0;
    private static boolean start;
    private static int startStep;
    private static JFrame tutFrame;
    private static JLabel stepLabel;
    private static JTextPane detailPane;

    public static void main(String[] args){
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double screenHeight = screenSize.getHeight();
        double screenWidth = screenSize.getWidth();
        found_photoshop=false;
        buttonPanel = new JPanel(new GridLayout(0,10));
        psdSettingsPanel = new JPanel(new FlowLayout());
        userPanel = new JPanel(new FlowLayout());
        listedUsersPanel = new JPanel(new GridLayout(4,0));

        globalCLimit = 20;
        globalReplaceString = "Name";

        ctrlHeld = false;
        shiftHeld=false;
        start = false;
        startStep=1;
        
        frame = new JFrame("MALCardNamer");
        frame.setIconImage(new ImageIcon("MAL_Logo.png").getImage());
        frame.setSize((int)(screenWidth*.75),(int)(screenHeight*.75));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

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
                if(psds.size()>0){
                    final JDialog dialog = new JDialog(frame,"Topic Contents",true);
                    JTextArea contents = new JTextArea(40,80);
                    JButton done = new JButton("Done");
                    done.addActionListener(new ActionListener(){
                        public void actionPerformed(ActionEvent a){
                            boolean searchForNames=false;
                            boolean searchForCards=false;
                            String firstName=null;
                            String secondName=null;
                            CardUser newUser=null;
                            String cardMaker = JOptionPane.showInputDialog(frame, "Your/Card Maker's Username?");
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
                                    if((nextLine.contains("name")||nextLine.contains("Name"))&&!(nextLine.contains("name=")||nextLine.contains("getElementsByTagName"))){
                                        int nameIndex = nextLine.toLowerCase().indexOf("name");
                                        String firstNameLine = nextLine.substring(nameIndex+4);
                                        if(firstNameLine.indexOf(":")!=-1){
                                            firstNameLine = firstNameLine.substring(firstNameLine.indexOf(":"));
                                        }
                                        firstName=firstNameLine.replace("</b>","").replace("<br>","").replace(":","").trim();
                                        if(!firstName.contains("[b]")){
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
                                                if((altLine.contains("name")||altLine.contains("Name"))&&!(altLine.contains("name=")||altLine.contains("getElementsByTagName"))){
                                                    int altIndex = altLine.toLowerCase().indexOf("name");
                                                    String secondNameLine = altLine.substring(altIndex+4);
                                                    if(secondNameLine.indexOf(":")!=-1){
                                                        secondNameLine = secondNameLine.substring(secondNameLine.indexOf(":"));
                                                    }
                                                    secondName=secondNameLine.replace("</b>","").replace("<br>","").replace(":","").trim();
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
                                                newUser=createUser(firstName, secondName);
                                                searchForNames=false;
                                                searchForCards=true;
                                            }
                                        }
                                        else{
                                            firstName=null;
                                        }
                                    }
                                }
                                else if(searchForCards){
                                    if(newUser!=null){
                                        if(cardMaker!=null&&cardMaker.length()>0&&nextLine.toLowerCase().contains(cardMaker.toLowerCase())&&nextLine.toLowerCase().contains("all")){
                                            for(PsdButton psd: psds){
                                                if(!newUser.cards.contains(psd)){
                                                    newUser.cards.add(psd);
                                                    newUser.model.addElement(psd.name);
                                                }
                                            }
                                        }
                                        else{
                                            for(PsdButton psd : psds){
                                                if(cardMaker==null||cardMaker.length()==0){
                                                    if(nextLine.toLowerCase().contains(psd.name.toLowerCase().replace(".psd",""))){
                                                        if(!newUser.cards.contains(psd)){
                                                            newUser.cards.add(psd);
                                                            newUser.model.addElement(psd.name);
                                                        }
                                                    }
                                                }
                                                else if(nextLine.toLowerCase().contains(cardMaker.toLowerCase())){
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
                                            if(cardMaker!=null&&cardMaker.length()>0){
                                                ArrayList<String> basePSDs = new ArrayList<String>();
                                                for(PsdButton psd : psds){
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
                                                                for(PsdButton psd: psds){
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
                            }
                            frame.validate();
                            frame.repaint();
                            dialog.dispose();
                        }
                    });
                    JScrollPane scrollPane = new JScrollPane(contents);
                    //scrollPane.setPreferredSize(new Dimension((int)(frame.getWidth()*.5),(int)(frame.getHeight()*.5)));
                    dialog.add(BorderLayout.CENTER,scrollPane);
                    dialog.add(BorderLayout.SOUTH,done);
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
                if(addedUsers.size()>0){
                    boolean atleastOne=false;
                    for(CardUser u: addedUsers){
                        if(u.model.size()>0){
                            atleastOne=true;
                            break;
                        }
                    }
                    if(atleastOne){
                        boolean error=false;
                        JFileChooser tempSaver = new JFileChooser(new File(System.getProperty("user.dir")).getParentFile());
                        tempSaver.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                        if(tempSaver.showDialog(frame,"Save Location")==JFileChooser.APPROVE_OPTION){
                            File saveLocation = tempSaver.getSelectedFile();
                            InputStream in = getClass().getResourceAsStream("rename.jsx");
                            File renameFile = new File(scriptsFolder.getAbsolutePath()+"\\RenameMalCards.jsx");
                            try{
                                if(!renameFile.createNewFile()){
                                    renameFile.delete();
                                    renameFile.createNewFile();
                                }
                            }
                            catch( IOException e){
                                error=true;
                                JOptionPane.showMessageDialog(frame, e, "RenameFileError", JOptionPane.ERROR_MESSAGE);
                            };
                            if(!error){
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
                                    data.add("var users = {\n");
                                    for(int i=0; i<addedUsers.size(); i++){
                                        CardUser tempUser = addedUsers.get(i);
                                        String cardInput = "    '"+i+"' : {'names':['"+tempUser.names[0]+"'";
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
                                    data.add("};\n\n");
                                    data.add("var psds = {\n");
                                    for(int p=0; p<psds.size(); p++){
                                        PsdButton psd = psds.get(p);
                                        String psdInput;
                                        if(!useGlobalSettings){
                                            psdInput = "    '"+p+"' : {'name':'"+psd.name+"','limit':"+psd.charLimit+",'replace':'"+psd.replaceString+"'";
                                        }
                                        else{
                                            psdInput = "    '"+p+"' : {'name':'"+psd.name+"','limit':"+globalCLimit+",'replace':'"+globalReplaceString+"'";
                                        }
                                        psdInput+="},\n";
                                        data.add(psdInput);
                                    }
                                    data.add("};\n\n");
                                    String savePath = saveLocation.getAbsolutePath();
                                    data.add("var save_location = '"+savePath.replace("\\","\\\\")+"';\n");
                                    for(PsdButton psd :psds){
                                        data.add("app.open(new File('"+psd.file.getAbsolutePath().replace("\\","\\\\")+"'));\n");
                                    }
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
                                            }
                                        }
                                        try {
                                            cmdWriter.close();
                                        } catch (IOException e) {
                                            error=true;
                                            JOptionPane.showMessageDialog(frame, e, "FileWriterCloseError", JOptionPane.ERROR_MESSAGE);
                                        }
                                        if(!error){
                                            if(start&&startStep==5){
                                                startStep=6;
                                                stepLabel.setText("Step 6: Run Script in Photoshop");
                                                detailPane.setText("Open Photoshop (if you have multiple photoshops, open the one whose scripts folder you selected).\nGo to File->Scripts->RenameMalCards and click it.\nWatch your cards get named.\nThis concludes the manual tutorial. Please consider posting these cards on the OMC Creators Thread.\nClick Done to close the tutorial.");
                                                JButton done = new JButton("Done");
                                                done.addActionListener(new ActionListener(){
                                                    public void actionPerformed(ActionEvent a){
                                                        tutFrame.dispose();
                                                    }
                                                });
                                                tutFrame.add(BorderLayout.SOUTH,done);
                                            }
                                            JOptionPane.showMessageDialog(frame, "Script saved successfully.", "Script Notification",JOptionPane.INFORMATION_MESSAGE);
                                        }
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
        JMenu m2 = new JMenu("Global");
        JCheckBox useGlobal = new JCheckBox("Use Global Settings?");
        TitledBorder textBorder = BorderFactory.createTitledBorder("charLimit");
        textBorder.setTitleJustification(TitledBorder.CENTER);
        JTextField cLimitText = new JTextField(9);
        cLimitText.setEditable(false);
        cLimitText.setBackground(psdSettingsPanel.getBackground());
        cLimitText.setHorizontalAlignment(JTextField.CENTER);
        cLimitText.setBorder(textBorder);
        cLimitText.setText(String.valueOf(globalCLimit));
        cLimitText.addFocusListener(new FocusListener(){
            public void focusGained(FocusEvent f){
                if(cLimitText.isEditable()){
                    cLimitText.setText("");
                }
            }
            public void focusLost(FocusEvent f){
                if(cLimitText.isEditable()){
                    try{
                        int cLimit = Integer.valueOf(cLimitText.getText());
                        globalCLimit= cLimit;
                    } catch(NumberFormatException e){
                        cLimitText.setText(String.valueOf(globalCLimit));
                    }
                }
            }
        });

        textBorder = BorderFactory.createTitledBorder("replace");
        textBorder.setTitleJustification(TitledBorder.CENTER);
        JTextField replaceText = new JTextField(9);
        replaceText.setEditable(false);
        replaceText.setBackground(psdSettingsPanel.getBackground());
        replaceText.setHorizontalAlignment(JTextField.CENTER);
        replaceText.setBorder(textBorder);
        replaceText.setText(globalReplaceString);
        replaceText.addFocusListener(new FocusListener(){
            public void focusGained(FocusEvent f){
                if(replaceText.isEditable()){
                    replaceText.setText("");
                }
            }
            public void focusLost(FocusEvent f){
                if(replaceText.isEditable()){
                    if(replaceText.getText().length()==0){
                        replaceText.setText(globalReplaceString);
                    }
                    else{
                        globalReplaceString=replaceText.getText();
                    }
                }
            }
        });
        useGlobal.addItemListener(new ItemListener(){
            public void itemStateChanged(ItemEvent i){
                if(i.getStateChange()==ItemEvent.SELECTED){
                    useGlobalSettings=true;
                    cLimitText.setEditable(true);
                    replaceText.setEditable(true);
                    frame.validate();
                    frame.repaint();
                }
                else{
                    useGlobalSettings=false;
                    cLimitText.setEditable(false);
                    replaceText.setEditable(false);
                    frame.validate();
                    frame.repaint();
                }
            }
        });
        m2.add(useGlobal);
        m2.add(cLimitText);
        m2.add(replaceText);
        JMenu m3 = new JMenu("Help");
        JMenuItem m31 = new JMenuItem("Tutorial");
        m3.add(m31);
        mb.add(m1);
        mb.add(m2);
        mb.add(m3);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBounds(0,0,frame.getWidth(),frame.getHeight());
        psdPanel = new JPanel(new GridLayout(2,1));
        psdPanel.setBackground(frame.getBackground().darker());
        psdPanel.setLayout(new BorderLayout());
        psdPanel.add(BorderLayout.WEST,psdOpenLabel);
        //psdSettingsPanel.setPreferredSize(new Dimension(frame.getWidth(),(int)(frame.getHeight()*.12)));
        psdSettingsPanel.setBackground(psdPanel.getBackground());
        
        JTextField search = new JTextField("Search for a saved user or add a new one here");
        //search.setSize(new Dimension(userPanel.getWidth(),(int)(userPanel.getHeight()*.4)));
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.add(BorderLayout.NORTH,search);
        File userFile = new File("savedUsers.txt");
        try{
            savedUsers.clear();
            userFile.createNewFile();
            Scanner userFileScanner = new Scanner(userFile);
            while(userFileScanner.hasNextLine()){
                savedUsers.add(userFileScanner.nextLine());
            }
            userFileScanner.close();

        } catch(Exception e){
            
        }
        DefaultListModel<String> model = new DefaultListModel<>();
        JList<String> searchList = new JList<String>(model);
        searchList.setVisible(false);
        searchList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        for(String user : savedUsers){
            model.addElement(user);
        }

        search.addFocusListener(new FocusListener(){
            public void focusGained(FocusEvent f){
                if(search.getText().equals("Search for a saved user or add a new one here")){
                    search.setText("");
                }
                if(model.getSize()>0&&searchList.isEnabled()){
                    searchPanel.setBounds(0,0,frame.getWidth(),100);
                    searchList.setVisible(true);
                }
                else{
                    searchPanel.setBounds(0,0,frame.getWidth(),20);
                }
                frame.validate();
                frame.repaint();
            }
            public void focusLost(FocusEvent f){
                if(search.getText().length()<1){
                    search.setText("Search for a saved user or add a new one here");
                }
            }
        });

        searchList.addListSelectionListener(new ListSelectionListener(){
            public void valueChanged(ListSelectionEvent l){
                if(searchList.getSelectedValue()!=null){
                    String searchText = searchList.getSelectedValue();
                    if(searchText.indexOf(",")!=-1){
                        String longName = searchText.substring(0,searchText.indexOf(","));
                        String shortName = searchText.substring(searchText.indexOf(",")+1);
                        CardUser newUser = createUser(longName,shortName);
                        newUser.addMouseListener(new MouseAdapter(){
                            public void mousePressed(MouseEvent m){
                                if(SwingUtilities.isRightMouseButton(m)&&!start){
                                    addedUsers.remove(newUser);
                                    listedUsersPanel.remove(newUser);
                                    model.addElement(newUser.fullName);
                                    frame.validate();
                                    frame.repaint();
                                }
                            }
                        });
                    }
                    else{
                        String longName = searchText;
                        CardUser newUser = createUser(longName,null);
                        newUser.addMouseListener(new MouseAdapter(){
                            public void mousePressed(MouseEvent m){
                                if(SwingUtilities.isRightMouseButton(m)&&!start){
                                    addedUsers.remove(newUser);
                                    listedUsersPanel.remove(newUser);
                                    model.addElement(newUser.fullName);
                                    frame.validate();
                                    frame.repaint();
                                }
                            }
                        });
                    }
                    //model.removeElement(searchText);
                    search.setText("");
                    search.requestFocusInWindow();
                }
            }
        });

        searchList.addMouseListener(new MouseAdapter(){
            public void mousePressed(MouseEvent m){
                if(SwingUtilities.isRightMouseButton(m)){
                    JList list = (JList)m.getSource();
                    int row=list.locationToIndex(m.getPoint());
                    savedUsers.remove(model.getElementAt(row));
                    model.removeElementAt(row);
                    saveUsers();
                    if(model.getSize()==0){
                        searchPanel.setBounds(0,0,frame.getWidth(),20);
                        list.setVisible(false);
                    }
                }
            }
        });
        
        search.getDocument().addDocumentListener(new DocumentListener(){
            public void changedUpdate(DocumentEvent d){
            }
            public void insertUpdate(DocumentEvent d){
                model.clear();
                for(String searchedUser: savedUsers){
                    boolean notAlreadyAdded=true;
                    for(CardUser u : addedUsers){
                        if(u.fullName.equals(searchedUser)){
                            notAlreadyAdded=false;
                        }
                    }
                    if(notAlreadyAdded&&searchedUser.toLowerCase().startsWith(search.getText().toLowerCase())){
                        model.addElement(searchedUser);
                    }
                }

            }
            public void removeUpdate(DocumentEvent d){
                model.clear();
                for(String searchedUser: savedUsers){
                    boolean notAlreadyAdded=true;
                    for(CardUser u : addedUsers){
                        if(u.fullName.equals(searchedUser)){
                            notAlreadyAdded=false;
                        }
                    }
                    if(notAlreadyAdded&&searchedUser.toLowerCase().startsWith(search.getText().toLowerCase())){
                        model.addElement(searchedUser);
                    }
                }
            }
        });

        search.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent a){
                if(!start&&(model.getSize()>0&&search.getText().equals(model.getElementAt(0))||savedUsers.contains(search.getText()))){
                    searchList.setVisible(false);
                    frame.validate();
                    frame.repaint();
                    frame.requestFocusInWindow();
                    String searchText = search.getText();
                    if(searchText.indexOf(",")!=-1){
                        String longName = searchText.substring(0,searchText.indexOf(","));
                        String shortName = searchText.substring(searchText.indexOf(",")+1);
                        CardUser newUser = createUser(longName,shortName);
                        newUser.addMouseListener(new MouseAdapter(){
                            public void mousePressed(MouseEvent m){
                                if(SwingUtilities.isRightMouseButton(m)&&!start){
                                    addedUsers.remove(newUser);
                                    listedUsersPanel.remove(newUser);
                                    model.addElement(newUser.fullName);
                                    frame.validate();
                                    frame.repaint();
                                }
                            }
                        });
                    }
                    else{
                        String longName = searchText;
                        CardUser newUser = createUser(longName,null);
                        newUser.addMouseListener(new MouseAdapter(){
                            public void mousePressed(MouseEvent m){
                                if(SwingUtilities.isRightMouseButton(m)&&!start){
                                    addedUsers.remove(newUser);
                                    listedUsersPanel.remove(newUser);
                                    model.addElement(newUser.fullName);
                                    frame.validate();
                                    frame.repaint();
                                }
                            }
                        });
                    }
                    search.setText("");
                }
                else{
                    final JDialog newUserDialog = new JDialog(frame,"New User",true);
                    //newUserDialog.setPreferredSize(new Dimension((int)(frame.getWidth()*.4),(int)(frame.getHeight()*.4)));
                    JTextField longName = new JTextField();
                    TitledBorder longBorder = BorderFactory.createTitledBorder("Default");
                    longBorder.setTitleJustification(TitledBorder.CENTER);
                    longName.setBorder(longBorder);
                    longName.setHorizontalAlignment(JTextField.CENTER);
                    JTextField shortName = new JTextField();
                    TitledBorder shortBorder = BorderFactory.createTitledBorder("Alternative");
                    shortBorder.setTitleJustification(TitledBorder.CENTER);
                    shortName.setBorder(shortBorder);
                    shortName.setHorizontalAlignment(JTextField.CENTER);
                    String searchText = search.getText();
                    if(searchText.indexOf(",")!=-1){
                        longName.setText(searchText.substring(0,searchText.indexOf(",")));
                        shortName.setText(searchText.substring(searchText.indexOf(",")+1));
                    }
                    else{
                        longName.setText(searchText);
                    }
                    JButton done = new JButton("Done");
                    done.addActionListener(new ActionListener(){
                        public void actionPerformed(ActionEvent a){
                            if(longName.getText().length()>0){
                                if(shortName.getText().length()>0){
                                    if(!start){
                                        savedUsers.add(longName.getText()+","+shortName.getText());
                                    }
                                    CardUser newUser = createUser(longName.getText(),shortName.getText());
                                    newUser.addMouseListener(new MouseAdapter(){
                                        public void mousePressed(MouseEvent m){
                                            if(SwingUtilities.isRightMouseButton(m)&&!start){
                                                addedUsers.remove(newUser);
                                                listedUsersPanel.remove(newUser);
                                                model.addElement(newUser.fullName);
                                                frame.validate();
                                                frame.repaint();
                                            }
                                        }
                                    });
                                }
                                else{
                                    savedUsers.add(longName.getText());
                                    CardUser newUser = createUser(longName.getText(),null);
                                    newUser.addMouseListener(new MouseAdapter(){
                                        public void mousePressed(MouseEvent m){
                                            if(SwingUtilities.isRightMouseButton(m)&&!start){
                                                addedUsers.remove(newUser);
                                                listedUsersPanel.remove(newUser);
                                                model.addElement(newUser.fullName);
                                                frame.validate();
                                                frame.repaint();
                                            }
                                        }
                                    });
                                }
                                if(addedUsers.size()>0&&start&&startStep==2){
                                    startStep=3;
                                    stepLabel.setText("Step 3: Add PSDs to User's List");
                                    detailPane.setText("Now, give yourself some cards.\n Simply drag each psd one at a time into your list.\nThis can be done faster by selecting all the psds at once as if you were selecting files.\nI.e. Select the leftmost psd. Hold shift and select the rightmost psd. Then drag them into the list.");
                                    search.setText("");
                                    search.setEnabled(false);
                                    for(PsdButton psd: psds){
                                        psd.setEnabled(true);
                                    }
                                    tutFrame.pack();
                                }
                            }
                            saveUsers();
                            search.setText("");
                            newUserDialog.dispose();
                            frame.requestFocusInWindow();
                            searchList.setVisible(false);
                            frame.repaint();
                            frame.validate();
                        }
                    });
                    JButton cancel = new JButton("Cancel");
                    cancel.addActionListener(new ActionListener(){
                        public void actionPerformed(ActionEvent a){
                            newUserDialog.dispose();
                        }
                    });
                    JPanel buttonPanel = new JPanel(new FlowLayout());
                    buttonPanel.add(done);
                    buttonPanel.add(cancel);
                    newUserDialog.add(BorderLayout.NORTH,longName);
                    newUserDialog.add(BorderLayout.CENTER,shortName);
                    newUserDialog.add(BorderLayout.SOUTH,buttonPanel);
                    newUserDialog.pack();
                    newUserDialog.setLocationRelativeTo(frame);
                    newUserDialog.setVisible(true);
                }
            }
        });
        JScrollPane nameScroller = new JScrollPane(searchList);
        searchPanel.add(nameScroller);
        userPanel.setLayout(new BorderLayout());
        JLayeredPane lPane = new JLayeredPane();
        //userPanel.add(BorderLayout.NORTH,searchPanel);
        JScrollPane scroller = new JScrollPane(listedUsersPanel);
        //userPanel.add(BorderLayout.CENTER,scroller);
        lPane.setBounds(0,0,frame.getWidth(),(int)(frame.getHeight()*.8));
        scroller.setBounds(0,18,frame.getWidth(),(int)(frame.getHeight()*.8));
        searchPanel.setBounds(0,0,frame.getWidth(),100);
        lPane.add(searchPanel,1,0);
        lPane.add(scroller,0,0);
        userPanel.add(BorderLayout.CENTER,lPane);
        
        search.addFocusListener(new FocusAdapter(){
            public void focusGained(FocusEvent f){
                lPane.setLayer(searchPanel, 1);
                lPane.setLayer(scroller, 0);
            }
            public void focusLost(FocusEvent f){
                lPane.setLayer(searchPanel, 0);
                lPane.setLayer(scroller, 1);
            }
        });
        
        contentPanel.add(BorderLayout.NORTH,psdPanel);
        scroller.addMouseListener(new MouseAdapter(){
            public void mousePressed(MouseEvent m){
                frame.requestFocusInWindow();
            }
        });
        contentPanel.add(userPanel);

        frame.add(BorderLayout.NORTH,mb);
        framePane.add(contentPanel,0,0);
        framePane.add(dragDropPanel,1,0);
        dragDropPanel.setOpaque(false);
        frame.add(BorderLayout.CENTER,contentPanel);
        frame.addMouseListener(new MouseAdapter(){
            public void mousePressed(MouseEvent m){
                frame.requestFocusInWindow();
            }
        });
        
        frame.addWindowFocusListener(new WindowAdapter(){
            public void windowGainedFocus(WindowEvent w){
                searchList.setVisible(false);
                frame.validate();
                frame.repaint();
            }
            public void windowLostFocus(WindowEvent w){
                ctrlHeld=false;
                shiftHeld=false;
            }
        });

        frame.addComponentListener(new ComponentAdapter(){
            public void componentResized(ComponentEvent c){
                lPane.setBounds(0,0,frame.getWidth(),(int)(frame.getHeight()*.8));
                scroller.setBounds(0,18,frame.getWidth(),(int)(frame.getHeight()*.8));
                searchPanel.setBounds(0,0,frame.getWidth(),(int)(searchPanel.getBounds().getHeight()));
            }
        });

        frame.addKeyListener(new KeyAdapter(){
            public void keyPressed(KeyEvent k){
                if(k.getKeyCode()==KeyEvent.VK_CONTROL&&!ctrlHeld){
                    ctrlHeld=true;
                }
                else if(k.getKeyCode()==KeyEvent.VK_SHIFT&&!shiftHeld){
                    shiftHeld=true;
                }
            }
            public void keyReleased(KeyEvent k){
                if(k.getKeyCode()==KeyEvent.VK_CONTROL&&ctrlHeld){
                    ctrlHeld=false;
                }
                else if(k.getKeyCode()==KeyEvent.VK_SHIFT&&shiftHeld){
                    shiftHeld=false;
                }
            }
        });
        frame.setVisible(true);
        m11.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent a){
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
                        File[] psdFiles  = new File[chosenFiles.size()];
                        for(int f=0; f<chosenFiles.size(); f++){
                            psdFiles[f]=chosenFiles.get(f);
                        }
                        if(!start){
                            lastOpenLocation = psdFiles[0].getParentFile();
                        }
                        for(int i=0; i<psdFiles.length; i++){
                            boolean alreadyExists=false;
                            for(PsdButton psd: psds){
                                if(psd.name.equals(psdFiles[i].getName())){
                                    alreadyExists=true;
                                }
                            }
                            if(!alreadyExists){
                                PsdButton tempBtn = new PsdButton(psdFiles[i]);
                                tempBtn.setFocusable(false);
                                tempBtn.addActionListener(new ActionListener(){
                                    public void actionPerformed(ActionEvent a){
                                        frame.requestFocusInWindow();
                                        psdSettingsPanel.removeAll();
                                        psdSettingsPanel.setVisible(false);
                                        if(tempBtn.selected){
                                            tempBtn.setBackground(null);
                                            tempBtn.selected=false;
                                        }
                                        else if(!tempBtn.selected){
                                            if(!ctrlHeld){
                                                for(PsdButton psd : psds){
                                                    if(psd.selected){
                                                        psd.setBackground(null);
                                                        psd.selected=false;
                                                    }
                                                }
                                            }
                                            int currIndex = psds.indexOf(tempBtn);
                                            if(shiftHeld){
                                                if(currIndex<lastSelectedIndex){
                                                    for(int i=currIndex; i<=lastSelectedIndex; i++){
                                                        psds.get(i).selected=true;
                                                        psds.get(i).setBackground(Color.cyan);
                                                    }
                                                }
                                                else{
                                                    for(int i=lastSelectedIndex; i<=currIndex; i++){
                                                        psds.get(i).selected=true;
                                                        psds.get(i).setBackground(Color.cyan);
                                                    }
                                                }
                                            }
                                            tempBtn.setBackground(Color.cyan);
                                            tempBtn.selected=true;
                                            lastSelectedIndex=currIndex;
                                        }
                                        ArrayList<PsdButton> selectedPsds = new ArrayList<PsdButton>();
                                        for(PsdButton psd : psds){
                                            if(psd.selected){
                                                selectedPsds.add(psd);
                                            }
                                        }
                                        if(selectedPsds.size()>0){
                                            JLabel psdLabel = null;
                                            if(selectedPsds.size()<=3){
                                                String psdNames = "";
                                                for(int i=0; i<selectedPsds.size(); i++){
                                                    PsdButton indexPsd = selectedPsds.get(i);
                                                    if(i<selectedPsds.size()-1){
                                                        psdNames+=indexPsd.name+",";
                                                    }
                                                    else{
                                                        psdNames+=indexPsd.name;
                                                    }
                                                }
                                                psdLabel = new JLabel(psdNames+": ",JLabel.RIGHT);
                                            }
                                            else{
                                                String psdNames = "";
                                                for(int i=0; i<3; i++){
                                                    PsdButton indexPsd = selectedPsds.get(i);
                                                    if(i<2){
                                                        psdNames+=indexPsd.name+",";
                                                    }
                                                    else{
                                                        psdNames+=indexPsd.name;
                                                    }
                                                }
                                                String lastPsdName = selectedPsds.get(selectedPsds.size()-1).name;
                                                psdLabel = new JLabel(psdNames+"..."+lastPsdName+": ",JLabel.RIGHT);
                                            }

                                            TitledBorder textBorder = BorderFactory.createTitledBorder("charLimit");
                                            textBorder.setTitleJustification(TitledBorder.CENTER);
                                            JTextField cLimitText = new JTextField(9);
                                            cLimitText.setBackground(psdSettingsPanel.getBackground());
                                            cLimitText.setHorizontalAlignment(JTextField.CENTER);
                                            cLimitText.setBorder(textBorder);
                                            boolean sameLimit=true;
                                            for(PsdButton psd : psds){
                                                if(psd.selected&&psd.charLimit!=tempBtn.charLimit){
                                                    sameLimit=false;
                                                }
                                            }
                                            if(sameLimit){
                                                cLimitText.setText(String.valueOf(tempBtn.charLimit));
                                            }
                                            cLimitText.addFocusListener(new FocusListener(){
                                                public void focusGained(FocusEvent f){
                                                    cLimitText.setText("");
                                                }
                                                public void focusLost(FocusEvent f){
                                                    try{
                                                        int cLimit = Integer.valueOf(cLimitText.getText());
                                                        for(PsdButton psd : psds){
                                                            if(psd.selected){   
                                                                psd.charLimit= cLimit;
                                                            }
                                                        }
                                                    } catch(NumberFormatException e){
                                                        boolean sameLimit=true;
                                                        for(PsdButton psd : psds){
                                                            if(psd.selected&&psd.charLimit!=tempBtn.charLimit){
                                                                sameLimit=false;
                                                            }
                                                        }
                                                        if(sameLimit){
                                                            cLimitText.setText(String.valueOf(tempBtn.charLimit));
                                                        }
                                                        else{
                                                            cLimitText.setText("");
                                                        }
                                                    }
                                                }
                                            });

                                            textBorder = BorderFactory.createTitledBorder("altName");
                                            textBorder.setTitleJustification(TitledBorder.CENTER);
                                            JTextField altNameText = new JTextField(9);
                                            altNameText.setBackground(psdSettingsPanel.getBackground());
                                            altNameText.setHorizontalAlignment(JTextField.CENTER);
                                            altNameText.setBorder(textBorder);
                                            altNameText.setText(tempBtn.altName);
                                            altNameText.addFocusListener(new FocusListener(){
                                                public void focusGained(FocusEvent f){
                                                    altNameText.setText("");
                                                }
                                                public void focusLost(FocusEvent f){
                                                    tempBtn.altName=altNameText.getText();
                                                }
                                            });

                                            textBorder = BorderFactory.createTitledBorder("replace");
                                            textBorder.setTitleJustification(TitledBorder.CENTER);
                                            JTextField replaceText = new JTextField(9);
                                            replaceText.setBackground(psdSettingsPanel.getBackground());
                                            replaceText.setHorizontalAlignment(JTextField.CENTER);
                                            replaceText.setBorder(textBorder);
                                            boolean sameReplace = true;
                                            for(PsdButton psd : psds){
                                                if(psd.selected&&!psd.replaceString.equals(tempBtn.replaceString)){
                                                    sameReplace=false;
                                                }
                                            }
                                            if(sameReplace){
                                                replaceText.setText(tempBtn.replaceString);
                                            }
                                            replaceText.addFocusListener(new FocusListener(){
                                                public void focusGained(FocusEvent f){
                                                    replaceText.setText("");
                                                }
                                                public void focusLost(FocusEvent f){
                                                    if(replaceText.getText().length()==0){
                                                        boolean sameReplace = true;
                                                        for(PsdButton psd : psds){
                                                            if(psd.selected&&!psd.replaceString.equals(tempBtn.replaceString)){
                                                                sameReplace=false;
                                                            }
                                                        }
                                                        if(sameReplace){
                                                            replaceText.setText(tempBtn.replaceString);
                                                        }
                                                        else{
                                                            replaceText.setText("");
                                                        }
                                                    }
                                                    else{
                                                        for(PsdButton psd : psds){
                                                            if(psd.selected){
                                                                psd.replaceString=replaceText.getText();
                                                            }
                                                        }
                                                    }
                                                }
                                            });

                                            psdSettingsPanel.add(psdLabel);
                                            psdSettingsPanel.add(cLimitText);
                                            int numSelected=0;
                                            for(PsdButton psd: psds){
                                                if(psd.selected){
                                                    numSelected++;
                                                }
                                            }
                                            if(numSelected<2){
                                                psdSettingsPanel.add(altNameText);
                                            }
                                            psdSettingsPanel.add(replaceText);
                                            psdSettingsPanel.setVisible(true);
                                        }
                                        frame.validate();
                                        frame.repaint();
                                    }
                                });
                                
                                tempBtn.addMouseMotionListener(new MouseAdapter(){
                                    public void mouseDragged(MouseEvent m){
                                        PsdButton button = (PsdButton) m.getSource();
                                        if(!button.selected){
                                            tempBtn.setTransferHandler(new PsdExportTransferHandler(new PsdButton[]{tempBtn}));
                                            tempBtn.setEnabled(false);
                                            tempBtn.setEnabled(true);
                                        }
                                        else{
                                            ArrayList<PsdButton> selectedPsds = new ArrayList<PsdButton>();
                                            for(PsdButton psd : psds){
                                                if(psd.selected){
                                                    selectedPsds.add(psd);
                                                }
                                            }
                                            tempBtn.setTransferHandler(new PsdExportTransferHandler(selectedPsds.toArray(new PsdButton[0])));
                                            tempBtn.setEnabled(false);
                                            tempBtn.setEnabled(true);
                                        }
                                        TransferHandler handle = button.getTransferHandler();
                                        handle.exportAsDrag(button, m, TransferHandler.COPY);
                                    }
                                });
                                psds.add(tempBtn);
                                buttonPanel.add(tempBtn);
                                if(buttonPanel.getComponentCount()<10){
                                    ((GridLayout)buttonPanel.getLayout()).setColumns(buttonPanel.getComponentCount());
                                }
                                else{
                                    ((GridLayout)buttonPanel.getLayout()).setColumns(10);
                                }
                            }
                        }
                        psdPanel.removeAll();
                        psdSettingsPanel.setVisible(false);
                        psdPanel.add(BorderLayout.CENTER,buttonPanel);
                        psdPanel.add(BorderLayout.SOUTH,psdSettingsPanel);
                        frame.validate();
                        frame.repaint();
                        if(start&&startStep==1){
                            startStep=2;
                            stepLabel.setText("Step 2: Add User");
                            detailPane.setText("Now, add a user, a.k.a the person that requested the cards in the search bar.\nThe format for adding a new user is:\n \"defaultName,alternateName\"\nLet's start by adding yourself as a user.\n Type in your default and alternate names in the above format.\n I.e. this is how mine would look. DawnofNyx,Dawn\nOnce you're done, just hit enter.");
                            tutFrame.pack();
                            m11.setEnabled(false);
                            search.setEnabled(true);
                            searchList.setEnabled(false);
                            for(PsdButton psd : psds){
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
        });

        m31.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent a){
                if(JOptionPane.showConfirmDialog(frame,"Would you like to take a guided tutorial?","Tutorial",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
                    start=true;
                    startStep=1;
                    m12.setEnabled(false);
                    m2.setEnabled(false);
                    m3.setEnabled(false);
                    search.setEnabled(false);
                    psds.clear();
                    addedUsers.clear();
                    buttonPanel.removeAll();
                    psdPanel.removeAll();
                    listedUsersPanel.removeAll();
                    frame.requestFocusInWindow();
                    tutFrame = new JFrame("Tutorial Window");
                    tutFrame.setResizable(false);
                    tutFrame.setAlwaysOnTop(true);
                    stepLabel = new JLabel();
                    stepLabel.setHorizontalAlignment(JLabel.CENTER);
                    stepLabel.setForeground(Color.CYAN);
                    stepLabel.setText("Step 1: Open PSDs");
                    tutFrame.add(BorderLayout.NORTH,stepLabel);
                    detailPane = new JTextPane();
                    StyledDocument doc = detailPane.getStyledDocument();
                    SimpleAttributeSet center = new SimpleAttributeSet();
                    StyleConstants.setAlignment(center,StyleConstants.ALIGN_CENTER);
                    doc.setParagraphAttributes(0,doc.getLength(),center,false);
                    detailPane.setEditable(false);
                    detailPane.setText("First, open all the sample psds that I included with the program.\nFile->Open PSDs->1,2,3.psd");
                    tutFrame.add(BorderLayout.CENTER,detailPane);
                    tutFrame.pack();
                    tutFrame.setLocationRelativeTo(frame);
                    tutFrame.setLocation(frame.getWidth(),0);
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
                            search.setEnabled(true);
                            m1.setEnabled(true);
                            searchList.setEnabled(true);
                            for(PsdButton psd : psds){
                                psd.setEnabled(true);
                            }
                        }
                        public void windowClosed(WindowEvent w){
                            start=false;
                            startStep=1;
                            m11.setEnabled(true);
                            m12.setEnabled(true);
                            m13.setEnabled(true);
                            m2.setEnabled(true);
                            m3.setEnabled(true);
                            search.setEnabled(true);
                            m1.setEnabled(true);
                            searchList.setEnabled(true);
                            for(PsdButton psd : psds){
                                psd.setEnabled(true);
                            }
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

    private  static void saveUsers(){
        File savedUserFile = new File("savedUsers.txt");
        try{
            savedUserFile.createNewFile();
            FileWriter savedWriter = new FileWriter(savedUserFile);
            Collections.sort(savedUsers);
            for(String user: savedUsers){
                savedWriter.write(user+"\n");
            }
            savedWriter.close();
        }catch(IOException e){

        }
    }

    private static CardUser createUser(String longName, String shortName){
        for(CardUser user: addedUsers){
            if(user.names[0].equals(longName)){
                return user;
            }
        }
        CardUser userPanel = null;
        if(shortName!=null){
            userPanel = new CardUser(longName,shortName);
        }
        else{
            userPanel = new CardUser(longName);
        }
        addedUsers.add(userPanel);
        listedUsersPanel.add(userPanel);
        TitledBorder nameBorder  = BorderFactory.createTitledBorder(longName);
        nameBorder.setTitleJustification(TitledBorder.CENTER);
        userPanel.setBorder(nameBorder);
        userPanel.cardList.addPropertyChangeListener(new PropertyChangeListener(){
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if(start&&startStep==3){
                    startStep=4;
                    stepLabel.setText("Step 4: PSD Settings");
                    detailPane.setText("If you click on any of the psds at the top, you'll see a settings panel.\nHere is where you can set: \n\"charLimit\", the max length of a name.\n\"altName\",the name of your card on the request site. (Only if you're reading webpage)\n\"replace\",the word to replace in your psds text layers with the requester's name.\nAll of these are set correctly for the moment.\nPress Next when you're ready to move on.");
                    JButton nxt = new JButton("Next");
                    nxt.addActionListener(new ActionListener(){
                        public void actionPerformed(ActionEvent a){
                            startStep=5;
                            stepLabel.setText("Step 5: Save Script");
                            detailPane.setText("The last step in this program is to save your script.\nGo to File->Save Script and click on it.\nYou will be prompted to select a save location, the place where your cards will be saved\nI reccomend saving them in a specified edition folder, i.e. DemonSlayerEdition.\nIf you receieve a message saying your script was saved successfully, you're set to move on.\nIf you didn't... contact me.");
                            tutFrame.remove(nxt);
                            tutFrame.pack();
                            tutFrame.validate();
                            tutFrame.repaint();
                            
                        }
                    });
                    tutFrame.add(BorderLayout.SOUTH,nxt);
                    tutFrame.pack();
                }
            }
            
        });
        frame.validate();
        frame.repaint();
        return userPanel;
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
            if(f.getName().equals("Photoshop.exe")){
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
