import java.io.FileFilter;
import java.io.File;

public class myFilter implements FileFilter{


	public boolean accept(File memberFile){


		return !memberFile.isHidden();



	}



}