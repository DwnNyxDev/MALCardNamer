import javax.swing.JButton;
import java.io.*;

public class PsdButton extends JButton{
    File file;
    String name;
    String altName;
    int charLimit;
    String nameLayer;
    String[] textLayers;
    boolean selected;
    String replaceString;

    public PsdButton(File fileIn){
        super(fileIn.getName());
        file=fileIn;
        name=file.getName();
        altName="";
        charLimit=20;
        nameLayer="";
        selected=false;
        replaceString="Name";
    }

    
}
