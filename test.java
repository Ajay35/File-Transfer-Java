import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

class test{
	public static void main(String[] args) throws FileNotFoundException
	{
		String name="tet.cpp";
		File f=new File("test1/test2/test3/"+name);
		f.getParentFile().mkdirs();
		FileOutputStream fos=new FileOutputStream(f);
		return;
	}
}