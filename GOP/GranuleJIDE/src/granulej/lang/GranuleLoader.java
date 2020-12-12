package granulej.lang;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class GranuleLoader {
	
	private String repositories[] = new String[0];
    
	public String[] findRepositories() {
          return repositories.clone();
    }
	
	public String[] getRepositories(){
	   return repositories.clone();
	}
	
	public String getRepositoriesString() {
        StringBuilder sb=new StringBuilder();
        for( int i=0; i<repositories.length ; i++ ) {
            sb.append( repositories[i]).append(":");
        }
        return sb.toString();
    }
	
	public void addRepository(String repository) {
		
	    for(int i=0; i< repositories.length; i++)
	    {
	    	if(repository.equals(repositories[i]))
	         return ;	
	    }
	    
	    String result[] =new String[repositories.length+1];
	    for(int i=0; i< repositories.length; i++)
	      result[i]=repositories[i];
        result[repositories.length]=repository;
        repositories=result;        
    }

    private static ArrayList<String> loadPaths = new ArrayList<String>();

    public static synchronized void addLoadPath(String path)  {
		for (int i = 0; i < loadPaths.size(); ++i)
			if (loadPaths.get(i).equals(path))
				return;
		loadPaths.add(path);
	}

	private static boolean exists(String gname, String path) {
    	String fullpath = path + File.separator + gname + ".class";
		File f = new File(fullpath);
		return f.exists();
	}
	private static Class loadGranule(String gname, String path) {
    	if (!exists(gname, path)) return null;
		String curPath=IndividualInfo.getInstance().getWorkDirectory();
		try {
			FileSystemClassLoader newloader = new FileSystemClassLoader(path);
			Class clazz = newloader.loadClass(gname);
			newloader.SearchAndCache(gname, clazz);
			return clazz;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	//粒的装载，解决不同路径的相同类的类的重载
	public static Class loadGranule(String gname) {
		Class clazz = loadGranule(gname, IndividualInfo.getInstance().getWorkDirectory());
		if (clazz != null)
			return clazz;

		for (int i = 0; i < loadPaths.size(); ++i) {
			clazz = loadGranule(gname, loadPaths.get(i));
			if (clazz != null)
				return clazz;
		}
		System.out.println("Not found granule! " + gname);
		return null;
	}
}
