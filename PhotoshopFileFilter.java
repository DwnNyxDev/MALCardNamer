import java.io.File;
import javax.swing.filechooser.FileFilter;
/**
 * Write a description of class PhotoshopFileFilter here.
 *
 * @author Vandell Vatel
 * @version 8/31/21
 */
public class PhotoshopFileFilter extends FileFilter
{
    public String getDescription(){
        return "Photoshop Exectuables";
    }
    
    public boolean accept(File f){
        if(f.getName().equals("Photoshop.exe")||(f.listFiles()!=null&&f.listFiles().length>0)){
            return true;
        }
        return false;
    }
}
