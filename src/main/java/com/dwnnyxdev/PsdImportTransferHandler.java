package com.dwnnyxdev;
import java.awt.Component;

import javax.swing.TransferHandler;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.DataFlavor;
import javax.swing.DefaultListModel;

public class PsdImportTransferHandler extends TransferHandler {

    public static final DataFlavor SUPPORTED_DATE_FLAVOR = new DataFlavor(PsdButton[].class,"PsdButton Array");

    public PsdImportTransferHandler() {
    }

    @Override
    public boolean canImport(TransferHandler.TransferSupport support) {
        return true;
    }

    @Override
    public boolean importData(TransferHandler.TransferSupport support) {
        boolean accept = false;
        if (canImport(support)) {
            try {
                Transferable t = support.getTransferable();
                Object value = t.getTransferData(SUPPORTED_DATE_FLAVOR);
                if (value instanceof PsdButton[]) {
                    Component component = support.getComponent();
                    if (component instanceof CardList) {
                        CardList tempList = (CardList)component;
                        if(tempList.owner!=null){
                            PsdButton[] cardArray = (PsdButton[])value;
                            DefaultListModel tempModel = (DefaultListModel)tempList.getModel();
                            for(PsdButton psd : cardArray){
                                if(!tempList.owner.cards.contains(psd)){
                                    tempList.owner.cards.add(psd);
                                    tempModel.addElement(psd.name);
                                }
                            }
                        }
                        accept = true;
                    }
                }
            } catch (Exception exp) {
                exp.printStackTrace();
            }
        }
        return accept;
    }
}
