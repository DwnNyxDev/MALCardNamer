package src;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionListener;

import java.io.*;

import javax.swing.filechooser.FileNameExtensionFilter;
import java.util.Scanner;
import java.awt.Toolkit;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.*;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.awt.GridLayout;
import java.util.Collections;
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
    private static Process startProcess;
    private static Process photoProcess;
    private static JPanel listedUsersPanel;
    private static ArrayList<String> savedUsers = new ArrayList<String>();
    private static ArrayList<CardUser> addedUsers = new ArrayList<CardUser>();
    private static File lastOpenLocation;

    public static void main(String[] args){
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double screenHeight = screenSize.getHeight();
        double screenWidth = screenSize.getWidth();
        found_photoshop=false;
        buttonPanel = new JPanel(new GridLayout(0,10));
        psdSettingsPanel = new JPanel(new FlowLayout());
        userPanel = new JPanel(new FlowLayout());
        listedUsersPanel = new JPanel(new GridLayout(4,4));
        
        frame = new JFrame("MALCardNamer");
        frame.setIconImage(new ImageIcon("MAL_Logo.png").getImage());
        frame.setSize((int)(screenWidth*.75),(int)(screenHeight*.75));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel psdOpenLabel = new JLabel(new ImageIcon(new ImageIcon("OpenPsds.png").getImage().getScaledInstance((int)(frame.getWidth()*.25), -1, Image.SCALE_SMOOTH)));
        JLayeredPane framePane = new JLayeredPane();
        JPanel dragDropPanel = new JPanel();
        dragDropPanel.setBounds(0,0,frame.getWidth(),frame.getHeight());

        JMenuBar mb = new JMenuBar();
        JMenu m1 = new JMenu("File");
        JMenuItem m11= new JMenuItem("Read Webpage");
        m11.addActionListener(new ActionListener(){
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
                                                if(nextLine.toLowerCase().contains(psd.name.toLowerCase().replace(".psd",""))){
                                                    if(!newUser.cards.contains(psd)){
                                                        newUser.cards.add(psd);
                                                        newUser.model.addElement(psd.name);
                                                    }
                                                }
                                                else{
                                                    String cardLine = nextLine.substring(nextLine.toLowerCase().indexOf(cardMaker.toLowerCase())+cardMaker.length());
                                                    if(psd.altName.length()>0){
                                                        if(cardLine.toLowerCase().contains(psd.altName.toLowerCase().replace(".psd",""))){
                                                            if(!newUser.cards.contains(psd)){
                                                                newUser.cards.add(psd);
                                                                newUser.model.addElement(psd.name);
                                                            }
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
        JMenuItem m12= new JMenuItem("Open PSDs");
        m12.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent a){
                JFileChooser psdSelector = new JFileChooser(System.getProperty("user.dir"));
                if(lastOpenLocation!=null){
                    psdSelector = new JFileChooser(lastOpenLocation);
                }
                psdSelector.setMultiSelectionEnabled(true);
                psdSelector.setFileFilter(new FileNameExtensionFilter("Photoshop Projects","psd"));
                if(psdSelector.showOpenDialog(frame)==JFileChooser.APPROVE_OPTION){
                    File[] psdFiles  = psdSelector.getSelectedFiles();
                    lastOpenLocation = psdFiles[0].getParentFile();
                    for(int i=0; i<psdFiles.length; i++){
                        boolean alreadyExists=false;
                        for(PsdButton psd: psds){
                            if(psd.name.equals(psdFiles[i].getName())){
                                alreadyExists=true;
                            }
                        }
                        if(!alreadyExists){
                            PsdButton tempBtn = new PsdButton(psdFiles[i]);
                            tempBtn.addActionListener(new ActionListener(){
                                public void actionPerformed(ActionEvent a){
                                    JLabel psdLabel = new JLabel(tempBtn.name+": ",JLabel.RIGHT);
                                    if(tempBtn.selected){
                                        tempBtn.setBackground(null);
                                        tempBtn.selected=false;
                                    }
                                    else if(!tempBtn.selected){
                                        tempBtn.setBackground(Color.cyan);
                                        tempBtn.selected=true;
                                    }
                                    psdSettingsPanel.removeAll();
                                    TitledBorder textBorder = BorderFactory.createTitledBorder("charLimit");
                                    textBorder.setTitleJustification(TitledBorder.CENTER);
                                    JTextField cLimitText = new JTextField(9);
                                    cLimitText.setBackground(psdSettingsPanel.getBackground());
                                    cLimitText.setHorizontalAlignment(JTextField.CENTER);
                                    cLimitText.setBorder(textBorder);
                                    cLimitText.setText(String.valueOf(tempBtn.charLimit));
                                    cLimitText.addFocusListener(new FocusListener(){
                                        public void focusGained(FocusEvent f){
                                            cLimitText.setText("");
                                        }
                                        public void focusLost(FocusEvent f){
                                            try{
                                                int cLimit = Integer.valueOf(cLimitText.getText());
                                                tempBtn.charLimit= cLimit;
                                            } catch(NumberFormatException e){
                                                cLimitText.setText(String.valueOf(tempBtn.charLimit));
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
                                    replaceText.setText(tempBtn.replaceString);
                                    replaceText.addFocusListener(new FocusListener(){
                                        public void focusGained(FocusEvent f){
                                            replaceText.setText("");
                                        }
                                        public void focusLost(FocusEvent f){
                                            if(replaceText.getText().length()==0){
                                                replaceText.setText(tempBtn.replaceString);
                                            }
                                            else{
                                                tempBtn.replaceString=replaceText.getText();
                                            }
                                        }
                                    });

                                    psdSettingsPanel.add(psdLabel);
                                    psdSettingsPanel.add(cLimitText);
                                    psdSettingsPanel.add(altNameText);
                                    psdSettingsPanel.add(replaceText);
                                    psdPanel.add(BorderLayout.SOUTH,psdSettingsPanel);
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
                    psdPanel.add(BorderLayout.CENTER,buttonPanel);
                    frame.validate();
                    frame.repaint();
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
                        JFileChooser tempSaver = new JFileChooser();
                        tempSaver.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                        if(tempSaver.showDialog(frame,"Save Location")==JFileChooser.APPROVE_OPTION){
                            File saveLocation = tempSaver.getSelectedFile();
                            File renameTemplate = new File("src\\rename.jsx");
                            File renameFile = new File(scriptsFolder+"\\RenameMalCards.jsx");
                            try{
                                int fileCounter=2;
                                while(!renameFile.createNewFile()){
                                    renameFile = new File(scriptsFolder+"\\RenameMalCards"+fileCounter+".jsx");
                                    fileCounter++;
                                }
                                ArrayList<String> renameContents = new ArrayList<String>();
                                Scanner cmdScanner = new Scanner(renameTemplate,"UTF-8");
                                boolean addToArray=true;
                                while(cmdScanner.hasNextLine()){
                                    String newLine = cmdScanner.nextLine();
                                    if(addToArray || newLine.equals("//start of code")){
                                        renameContents.add(newLine+"\n");
                                        if(newLine.equals("//start of data")){
                                            addToArray=false;
                                        }
                                        else if(newLine.equals("//start of code")){
                                            addToArray=true;
                                        }
                                    }
                                }
                                cmdScanner.close();
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
                                    String psdInput = "    '"+p+"' : {'name':'"+psd.name+"','limit':"+psd.charLimit+",'replace':'"+psd.replaceString+"'";
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
                                FileWriter cmdWriter = new FileWriter(renameFile);
                                for(String line: renameContents){
                                    cmdWriter.write(line);
                                }
                                cmdWriter.write("new File('"+renameFile.getAbsolutePath().replace("\\","\\\\")+"').remove()");
                                cmdWriter.close(); 
                                JOptionPane.showMessageDialog(frame, "Script saved successfully.", "Script Notification",JOptionPane.INFORMATION_MESSAGE);
                            } 
                            catch(IOException e){
                                JOptionPane.showMessageDialog(frame, e, "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }
                }
            }
        });
        m1.add(m12);
        m1.add(m11);
        m1.add(m13);
        mb.add(m1);

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
                if(model.getSize()>0){
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
                        createUser(longName,shortName);
                    }
                    else{
                        String longName = searchText;
                        createUser(longName,null);
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
                if(model.getSize()>0&&search.getText().equals(model.getElementAt(0))){
                    searchList.setVisible(false);
                    frame.validate();
                    frame.repaint();
                    frame.requestFocusInWindow();
                    String searchText = search.getText();
                    if(searchText.indexOf(",")!=-1){
                        String longName = searchText.substring(0,searchText.indexOf(","));
                        String shortName = searchText.substring(searchText.indexOf(",")+1);
                        createUser(longName,shortName);
                    }
                    else{
                        String longName = searchText;
                        createUser(longName,null);
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
                                    savedUsers.add(longName.getText()+","+shortName.getText());
                                    createUser(longName.getText(),shortName.getText());
                                }
                                else{
                                    savedUsers.add(longName.getText());
                                    createUser(longName.getText(),null);
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
        contentPanel.add(userPanel);

        frame.add(BorderLayout.NORTH,mb);
        framePane.add(contentPanel,0,0);
        framePane.add(dragDropPanel,1,0);
        dragDropPanel.setOpaque(false);
        frame.add(BorderLayout.CENTER,contentPanel);
        frame.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent m){
                frame.requestFocusInWindow();
                
            }
        });
        
        frame.addWindowFocusListener(new WindowAdapter(){
            public void windowGainedFocus(WindowEvent w){
                searchList.setVisible(false);
                frame.validate();
                frame.repaint();
            }
        });

        frame.addComponentListener(new ComponentAdapter(){
            public void componentResized(ComponentEvent c){
                lPane.setBounds(0,0,frame.getWidth(),(int)(frame.getHeight()*.8));
                scroller.setBounds(0,18,frame.getWidth(),(int)(frame.getHeight()*.8));
                searchPanel.setBounds(0,0,frame.getWidth(),(int)(searchPanel.getBounds().getHeight()));
            }
        });
        frame.setVisible(true);
       
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
            }

        } catch(IOException e){
            System.out.println("Something went wrong. >-<");
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
        if(shortName!=null){
            CardUser userPanel = new CardUser(longName,shortName);
            addedUsers.add(userPanel);
            TitledBorder nameBorder  = BorderFactory.createTitledBorder(longName);
            nameBorder.setTitleJustification(TitledBorder.CENTER);
            userPanel.setBorder(nameBorder);
            listedUsersPanel.add(userPanel);
            frame.validate();
            frame.repaint();
            return userPanel;
        }
        else{
            CardUser userPanel = new CardUser(longName);
            addedUsers.add(userPanel);
            TitledBorder nameBorder  = BorderFactory.createTitledBorder(longName);
            nameBorder.setTitleJustification(TitledBorder.CENTER);
            userPanel.setBorder(nameBorder);
            listedUsersPanel.add(userPanel);
            frame.validate();
            frame.repaint();
            return userPanel;
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
                    if(photoFile!=null){
                        pathChooser.setCurrentDirectory(photoFile.getParentFile().getParentFile());
                    }
                    pathChooser.setFileFilter(new PhotoshopFileFilter());
                    if(pathChooser.showOpenDialog(dialog)==JFileChooser.APPROVE_OPTION){
                        String getPath = pathChooser.getSelectedFile().getAbsolutePath();
                        path.setText(getPath);
                        photoFile = new File(getPath);
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
