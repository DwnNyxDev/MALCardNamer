package src;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionListener;

import java.io.*;

import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.DefaultStyledDocument.ElementSpec;

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

import javax.swing.SwingUtilities;
import java.awt.Image;

import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.ListSelectionEvent;

public class CardEdition extends JPanel {

    private static JFrame frame;
    private ArrayList<PsdButton> psds;
    //private static File saveData;
    private JPanel psdPanel;
    private JPanel buttonPanel;
    private JPanel psdSettingsPanel;
    private JPanel userPanel;
    //private static JScrollPane scroller;
    private JPanel listedUsersPanel;
    private ArrayList<String> savedUsers;
    private ArrayList<CardUser> addedUsers;
    private static boolean ctrlHeld;
    private static boolean shiftHeld;
    private int lastSelectedIndex = 0;
    private String name;
    

    public CardEdition(JFrame frameIn, String nameIn){
        super();
        this.setLayout(new BorderLayout());
        frame=frameIn;
        name=nameIn;
        psds = new ArrayList<PsdButton>();
        savedUsers = new ArrayList<String>();
        addedUsers = new ArrayList<CardUser>();
        createPanel();
    }

    private void createPanel(){
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double screenHeight = screenSize.getHeight();
        double screenWidth = screenSize.getWidth();
        buttonPanel = new JPanel(new GridLayout(0,10));
        psdSettingsPanel = new JPanel(new FlowLayout());
        userPanel = new JPanel(new FlowLayout());
        listedUsersPanel = new JPanel(new GridLayout(4,0));
        

        ctrlHeld = false;
        shiftHeld=false;
    
        //frame.setResizable(false);

        JLabel psdOpenLabel = new JLabel(new ImageIcon(new ImageIcon("OpenPsds.png").getImage().getScaledInstance((int)(frame.getWidth()*.25), -1, Image.SCALE_SMOOTH)));

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
        if(Maincopy.start){
            searchList.setEnabled(false);
            if(Maincopy.manualTut){
                search.setEnabled(true);
            }
            else{
                search.setEnabled(false);
            }
        }
        JScrollPane nameScroller = new JScrollPane(searchList);
        nameScroller.setVisible(false);
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
                    nameScroller.setVisible(true);
                }

                frame.validate();
                frame.repaint();
            }
            public void focusLost(FocusEvent f){
                if(search.getText().length()<1){
                    search.setText("Search for a saved user or add a new one here");
                }
                nameScroller.setVisible(false);
                frame.validate();
                frame.repaint();
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
                                if(SwingUtilities.isRightMouseButton(m)&&!Maincopy.start){
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
                                if(SwingUtilities.isRightMouseButton(m)&&!Maincopy.start){
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
                if(!Maincopy.start&&(model.getSize()>0&&search.getText().equals(model.getElementAt(0))||savedUsers.contains(search.getText()))){
                    nameScroller.setVisible(false);
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
                                if(SwingUtilities.isRightMouseButton(m)&&!Maincopy.start){
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
                                if(SwingUtilities.isRightMouseButton(m)&&!Maincopy.start){
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
                                    if(!Maincopy.start){
                                        savedUsers.add(longName.getText()+","+shortName.getText());
                                    }
                                    CardUser newUser = createUser(longName.getText(),shortName.getText());
                                    newUser.addMouseListener(new MouseAdapter(){
                                        public void mousePressed(MouseEvent m){
                                            if(SwingUtilities.isRightMouseButton(m)&&!Maincopy.start){
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
                                            if(SwingUtilities.isRightMouseButton(m)&&!Maincopy.start){
                                                addedUsers.remove(newUser);
                                                listedUsersPanel.remove(newUser);
                                                model.addElement(newUser.fullName);
                                                frame.validate();
                                                frame.repaint();
                                            }
                                        }
                                    });
                                }
                                if(addedUsers.size()>0&&Maincopy.start&&Maincopy.startStep==3){
                                    Maincopy.startStep=4;
                                    Maincopy.stepLabel.setText("Step 4: Add PSDs to User's List");
                                    Maincopy.detailPane.setText("Now, give yourself some cards.\n Simply drag each psd one at a time into your list.\nThis can be done faster by selecting all the psds at once as if you were selecting files.\nI.e. Select the leftmost psd. Hold shift and select the rightmost psd. Then drag them into the list.");
                                    search.setText("");
                                    search.setEnabled(false);
                                    for(PsdButton psd: psds){
                                        psd.setEnabled(true);
                                    }
                                    Maincopy.tutFrame.pack();
                                }
                            }
                            saveUsers();
                            search.setText("");
                            newUserDialog.dispose();
                            frame.requestFocusInWindow();
                            nameScroller.setVisible(false);
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

        nameScroller.setPreferredSize(new Dimension(frame.getWidth(),75));
        searchPanel.add(nameScroller);
        
        
        userPanel.setLayout(new BorderLayout());
        userPanel.add(BorderLayout.NORTH,searchPanel);
        userPanel.add(BorderLayout.CENTER,listedUsersPanel);
        
        
        contentPanel.add(BorderLayout.NORTH,psdPanel);
        contentPanel.add(BorderLayout.CENTER,userPanel);
        this.add(BorderLayout.CENTER,contentPanel);
        frame.addMouseListener(new MouseAdapter(){
            public void mousePressed(MouseEvent m){
                frame.requestFocusInWindow();
            }
        });
        
        frame.addWindowFocusListener(new WindowAdapter(){
            public void windowGainedFocus(WindowEvent w){
                frame.validate();
                frame.repaint();
            }
            public void windowLostFocus(WindowEvent w){
                ctrlHeld=false;
                shiftHeld=false;
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
    }

    public void addPsds(PsdButton[] newPsds){
        for(int i=0; i<newPsds.length; i++){
            PsdButton tempBtn = newPsds[i];
            boolean alreadyExists=false;
            for(PsdButton psd: psds){
                if(psd.name.equals(tempBtn.name)){
                    alreadyExists=true;
                }
            }
            if(!alreadyExists){
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
                            

                            textBorder = BorderFactory.createTitledBorder("Replace Text");
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

                            textBorder = BorderFactory.createTitledBorder("Replace Layer");
                            textBorder.setTitleJustification(TitledBorder.CENTER);
                            JTextField replaceLayer = new JTextField(9);
                            replaceLayer.setBackground(psdSettingsPanel.getBackground());
                            replaceLayer.setHorizontalAlignment(JTextField.CENTER);
                            replaceLayer.setBorder(textBorder);
                            boolean sameReplaceLayer = true;
                            for(PsdButton psd : psds){
                                if(psd.selected&&!psd.replaceLayer.equals(tempBtn.replaceLayer)){
                                    sameReplaceLayer=false;
                                }
                            }
                            if(sameReplaceLayer){
                                replaceLayer.setText(tempBtn.replaceLayer);
                            }
                            replaceLayer.addFocusListener(new FocusListener(){
                                public void focusGained(FocusEvent f){
                                    replaceLayer.setText("");
                                }
                                public void focusLost(FocusEvent f){
                                    if(replaceLayer.getText().length()==0){
                                        boolean sameReplaceLayer = true;
                                        for(PsdButton psd : psds){
                                            if(psd.selected&&!psd.replaceLayer.equals(tempBtn.replaceLayer)){
                                                sameReplaceLayer=false;
                                            }
                                        }
                                        if(sameReplaceLayer){
                                            replaceLayer.setText(tempBtn.replaceLayer);
                                        }
                                        else{
                                            replaceLayer.setText("");
                                        }
                                    }
                                    else{
                                        for(PsdButton psd : psds){
                                            if(psd.selected){
                                                psd.replaceLayer=replaceLayer.getText();
                                            }
                                        }
                                    }
                                }
                            });

                            JRadioButton repStringBtn = new JRadioButton("Text",true);
                            repStringBtn.addActionListener(new ActionListener(){
                                public void actionPerformed(ActionEvent a) {
                                    if(repStringBtn.isSelected()){
                                        replaceText.setEnabled(true);
                                        for(PsdButton psd: psds){
                                            if(psd.selected){
                                                psd.repString=true;
                                            }
                                        }
                                    }
                                    else{
                                        replaceText.setEnabled(false);
                                        for(PsdButton psd: psds){
                                            if(psd.selected){
                                                psd.repString=false;
                                            }
                                        }
                                    }
                                }
                            });
                            JRadioButton repLayerBtn = new JRadioButton("Layer",false);
                            repLayerBtn.addActionListener(new ActionListener(){
                                public void actionPerformed(ActionEvent a) {
                                    if(repLayerBtn.isSelected()){
                                        replaceLayer.setEnabled(true);
                                        for(PsdButton psd: psds){
                                            if(psd.selected){
                                                psd.repLayer=true;
                                            }
                                        }
                                    }
                                    else{
                                        replaceLayer.setEnabled(false);
                                        for(PsdButton psd: psds){
                                            if(psd.selected){
                                                psd.repLayer=false;
                                            }
                                        }
                                    }
                                }
                            });
                            boolean sameLayerSetting = true;
                            for(PsdButton psd : psds){
                                if(psd.selected&&psd.repLayer!=tempBtn.repLayer){
                                    sameLayerSetting=false;
                                }
                            }
                            if(!sameLayerSetting){
                                replaceLayer.setEnabled(false);
                                repLayerBtn.setSelected(false);
                            }
                            else{
                                repLayerBtn.setSelected(tempBtn.repLayer);
                                replaceLayer.setEnabled(tempBtn.repLayer);
                            }

                            boolean sameTextSetting = true;
                            for(PsdButton psd : psds){
                                if(psd.selected&&psd.repString!=tempBtn.repString){
                                    sameTextSetting=false;
                                }
                            }
                            if(!sameTextSetting){
                                replaceText.setEnabled(false);
                                repStringBtn.setSelected(false);
                            }
                            else{
                                repStringBtn.setSelected(tempBtn.repString);
                                replaceText.setEnabled(tempBtn.repString);
                            }

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
                            JPanel replacePanel = new JPanel(new GridLayout(2,1));
                            replacePanel.setBackground(psdSettingsPanel.getBackground());
                            TitledBorder replaceBorder = BorderFactory.createTitledBorder("Replace:");
                            replaceBorder.setTitleJustification(TitledBorder.CENTER);
                            replacePanel.setBorder(replaceBorder);
                            repStringBtn.setBackground(psdSettingsPanel.getBackground());
                            repLayerBtn.setBackground(psdSettingsPanel.getBackground());
                            replacePanel.add(repStringBtn);
                            replacePanel.add(repLayerBtn);
                            psdSettingsPanel.add(replacePanel);
                            psdSettingsPanel.add(replaceText);
                            psdSettingsPanel.add(replaceLayer);
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
        for(PsdButton psd: psds){
            psd.selected=false;
            psd.setBackground(null);
        }
        psdPanel.removeAll();
        psdSettingsPanel.setVisible(false);
        psdPanel.add(BorderLayout.CENTER,buttonPanel);
        psdPanel.add(BorderLayout.SOUTH,psdSettingsPanel);
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

    private void saveUsers(){
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

    public CardUser createUser(String longName, String shortName){
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
                if(Maincopy.start&&Maincopy.startStep==4){
                    Maincopy.startStep=5;
                    Maincopy.stepLabel.setText("Step 5: PSD Settings");
                    Maincopy.detailPane.setText("If you click on any of the psds at the top, you'll see a settings panel.\nHere is where you can set: \n\"charLimit\", the max length of a name.\n\"altName\",the name of your card on the request site. (Only if you're reading webpage)\n\"replace\",the word or layer to replace in your psds text layers with the requester's name.\nAll of these are set correctly for the moment.\nPress Next when you're ready to move on.");
                    JButton nxt = new JButton("Next");
                    nxt.addActionListener(new ActionListener(){
                        public void actionPerformed(ActionEvent a){
                            Maincopy.startStep=6;
                            Maincopy.stepLabel.setText("Step 6: Save Script");
                            Maincopy.detailPane.setText("The last step in this program is to save your script.\nGo to File->Save Script and click on it.\nYou will be prompted to select a save location, the place where your cards will be saved\nI reccomend saving them in a specified edition folder, i.e. OMCEdition.\nIf you receieve a message saying your script was saved successfully, you're set to move on.\nIf you didn't... contact me.");
                            Maincopy.tutFrame.remove(nxt);
                            Maincopy.tutFrame.pack();
                            Maincopy.tutFrame.validate();
                            Maincopy.tutFrame.repaint();
                            
                        }
                    });
                    Maincopy.tutFrame.add(BorderLayout.SOUTH,nxt);
                    Maincopy.tutFrame.pack();
                }
            }
            
        });

        final CardUser finalPanel = userPanel;

        finalPanel.addMouseListener(new MouseAdapter(){
            public void mousePressed(MouseEvent m){
                if(SwingUtilities.isRightMouseButton(m)&&!Maincopy.start){
                    addedUsers.remove(finalPanel);
                    listedUsersPanel.remove(finalPanel);
                    frame.validate();
                    frame.repaint();
                }
            }
        });
        frame.validate();
        frame.repaint();
        return userPanel;
    }

    public ArrayList<PsdButton> getPsds(){
        return psds;
    }

    public String getName(){
        return name;
    }

    public void setName(String newVal){
        name=newVal;
    }

    public ArrayList<CardUser> getAddedUsers(){
        return addedUsers;
    }
}
