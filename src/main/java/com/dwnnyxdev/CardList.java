package com.dwnnyxdev;
import javax.swing.JList;
import javax.swing.ListModel;

public class CardList extends JList<String>{

    public CardUser owner;
    public CardList(ListModel lm, CardUser ownerIn){
        super(lm);
        owner=ownerIn;
    }
}
