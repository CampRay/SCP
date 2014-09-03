package com.missionsky.scp.dataanalysis.utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.glassfish.gmbal.Description;

import com.missionsky.scp.dataanalysis.entity.ClassInfo;

/**
 * Get sub-class of interface
 * @author ellis.xie 
 * @version 1.0
 */

public class ClassUtil {
	
	/**
	 * Return all sub-class of the interface or abstract class
	 * @param c
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List<Class> getAllClassByFatherClass(Class c) {
		List<Class>  returnClassList = null;
		
		// get current gackage name
		String packageName = c.getPackage().getName();
		try {
			// get all class in current package
			List<Class> allClass = getClasses(packageName);
			if(allClass != null) {
				returnClassList = new ArrayList<Class>();
				for(Class classes : allClass) {
					// determine whether the same interface
					if(c.isAssignableFrom(classes)) {
						// remove interface itself
						if(!c.equals(classes)) {
							returnClassList.add(classes);		
						}
					}
				}
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return returnClassList;
	}
	
	/**
	 * Return infomation of interface's sub-classes 
	 * @param c
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static ArrayList<ClassInfo> subClassInfo(Class c){
		List<Class> classes = ClassUtil.getAllClassByFatherClass(c);
		ArrayList<ClassInfo> classInfos = new ArrayList<>();
		ClassInfo classInfo = null;
		
		for (Class cl : classes) {
			classInfo = new ClassInfo();
			classInfo.setSimpleName(cl.getSimpleName());
			classInfo.setName(cl.getName());
			classInfo.setDescription(((Description)cl.getAnnotation(Description.class)).value());
		}
		
		return classInfos;
	}
	
	
	@SuppressWarnings("rawtypes")
	private static List<Class> getClasses(String packageName) throws ClassNotFoundException, IOException {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();	
		String path = packageName.replace(".", "/");
		Enumeration<URL> resources = classLoader.getResources(path);
		List<File> dirs = null;
		if(resources != null) {
			dirs = new ArrayList<File>();
			while(resources.hasMoreElements()) {
				URL resource = resources.nextElement();
				dirs.add(new File(resource.getFile()));
			}
		}
		List<Class> classes = null;
		if(dirs != null) {
			classes = new ArrayList<Class>();
			for(File directory : dirs) {
				classes.addAll(findClasses(directory,packageName));
			}
		}
		
		return classes;
	}
	
	@SuppressWarnings("rawtypes")
	private static List<Class> findClasses(File directory,String packageName) throws ClassNotFoundException {
		List<Class> classes = null;
		if(!directory.exists()) {
			return classes;
		}
		File[] files = directory.listFiles();
		if(files != null) {
			classes = new ArrayList<Class>();
			for(File file :  files) {
				if(file.isDirectory()) {
					classes.addAll(findClasses(file,packageName+"."+file.getName()));
				}else if(file.getName().endsWith(".class")) {
					classes.add(Class.forName(packageName+"."+file.getName().substring(0, file.getName().length()-6)));
				}
			}
		}
		
		return classes;
	}
}