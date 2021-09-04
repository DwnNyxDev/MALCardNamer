import javax.swing.JComponent;
import javax.swing.TransferHandler;
import java.awt.dnd.DnDConstants;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.DataFlavor;

public class PsdExportTransferHandler extends TransferHandler {
    
    public static final DataFlavor SUPPORTED_DATE_FLAVOR = new DataFlavor(PsdButton[].class,"PsdButton Array");
    private String[] psdDetails;
    private PsdButton[] btnCopies;
    
    public PsdExportTransferHandler(String[] psdDetailsIn){
        psdDetails=psdDetailsIn;
    }

    public PsdExportTransferHandler(PsdButton[] btnCopiesIn){
        btnCopies = btnCopiesIn;
    }

    public String[] getPsdName(){
        return psdDetails;
    }

    @Override
    public int getSourceActions(JComponent c) {
        return DnDConstants.ACTION_COPY_OR_MOVE;
    }

    @Override
    protected Transferable createTransferable(JComponent c) {

        return new Transferable(){
            public DataFlavor[] getTransferDataFlavors(){
                return new DataFlavor[]{SUPPORTED_DATE_FLAVOR};
            }
            public Object getTransferData(DataFlavor flavor){
                if(flavor.equals(SUPPORTED_DATE_FLAVOR)){
                    return btnCopies;
                }
                else return "Nothing";
            }
            public boolean isDataFlavorSupported(DataFlavor flavor){
                for (DataFlavor f: getTransferDataFlavors()) {
                    if (flavor.equals(f)) {
                      return true;
                    }
                  }
                  return false;
            }
        };
    }

    @Override
    protected void exportDone(JComponent source, Transferable data, int action) {
        super.exportDone(source, data, action);
        
        // Decide what to do after the drop has been accepted
    }
}
