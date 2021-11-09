package com.dwnnyxdev;
import java.util.ArrayList;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.BorderLayout;

public class CardUser extends JPanel {
    String[] names;
    String fullName;
    ArrayList<PsdButton> cards;
    DefaultListModel<String> model;
    JList<String> cardList;

    public CardUser(String longName){
        super();
        names=new String[]{longName};
        fullName=longName;
        cards = new ArrayList<PsdButton>();
        model = new DefaultListModel<>();
        cardList = new CardList(model,this);
        DefaultListCellRenderer renderer = (DefaultListCellRenderer) cardList.getCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.CENTER);
        cardList.setDragEnabled(true);
        cardList.setTransferHandler(new PsdImportTransferHandler());
        cardList.addMouseListener(new MouseAdapter(){
            public void mousePressed(MouseEvent m){
                if(SwingUtilities.isRightMouseButton(m)){
                    JList list = (JList)m.getSource();
                    int row=list.locationToIndex(m.getPoint());
                    model.removeElementAt(row);
                }
            }
        });
        JScrollPane cardScroller = new JScrollPane(cardList,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        this.setLayout(new BorderLayout());
        this.add(BorderLayout.CENTER,cardScroller);
    }

    public CardUser(String longName,String shortName){
        super();
        names=new String[]{longName,shortName};
        fullName=longName+","+shortName;
        cards = new ArrayList<PsdButton>();
        model = new DefaultListModel<>();
        cardList = new CardList(model,this);
        DefaultListCellRenderer renderer = (DefaultListCellRenderer) cardList.getCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.CENTER);
        cardList.setDragEnabled(true);
        cardList.setTransferHandler(new PsdImportTransferHandler());
        cardList.addMouseListener(new MouseAdapter(){
            public void mousePressed(MouseEvent m){
                if(SwingUtilities.isRightMouseButton(m)){
                    JList list = (JList)m.getSource();
                    int row=list.locationToIndex(m.getPoint());
                    model.removeElementAt(row);
                }
            }
        });
        JScrollPane cardScroller = new JScrollPane(cardList,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        this.setLayout(new BorderLayout());
        this.add(BorderLayout.CENTER,cardScroller);
    }


    
}
