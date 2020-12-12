package granulej.lang;

import javassist.ClassPool;
import javassist.CtClass;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

public class ObjectAgent {
	private static int serialNum=0;
	private static HashMap<Integer,Object> objectList = new HashMap<Integer,Object>(); 
	private static ArrayList<Integer> hashCodeList = new ArrayList<Integer>();
	
	/**
	 * 
	 * @param type     //要new的Class对象  
	 * @param argTypes //new对象时使用的参数列表的class对象
	 * @param args     //new对象时使用的具体参数
	 * @return         //返回一个Class对象指定类型的对象引用
	 */
	//TODO 可以考虑将constructor直接传递进来，这样加快运行时new对象的速度
	//TODO 另一种方法是，用编号区别对象，这样可以在初始化时提前生成对象，在使用时直接用编号查询即可
	public static <Out> Out getObjectReference(int index,Class type,Class[] argTypes,Object[] args)
	{ 
		//如果该对象已经存在了，则直接返回对象
		Object obj = null;
		if(index<hashCodeList.size())
			return (Out)objectList.get(hashCodeList.get(index));
		
		//如果不存在对象，则调用构造函数新建一个对象
		Constructor constructor = null;
		try{
			constructor=type.getDeclaredConstructor(argTypes);
		}
		catch(Exception e)
		{
			System.out.println("can't find the Construtor,in method of getObjectReference!!");
			e.printStackTrace();
			return null;
		}
		
		try{
			constructor.setAccessible(true);
			obj = constructor.newInstance(args);
		}
		catch(Exception e)
		{
			System.out.println("can't new an object of "+type.getName()+" ,in method of getObjectReference!!");
			e.printStackTrace();
			return null;
		}
		
		//将对象添加到列表中，用hashCode做标识
		objectList.put(obj.hashCode(), obj);
		hashCodeList.add(obj.hashCode());
		
		return (Out)obj;
	}
	
	//
	public static boolean replaceObjectByHashCode(int hashCode,Object obj)
	{
		if(!objectList.containsKey(hashCode))
		{
			System.out.println("invalid hashCode,in method of replaceObjectByHashCode");
			return false;
		}
		
		objectList.put(hashCode, obj);
		return true;
	}
	
	public static int getObjectSerialNum()
	{
		return serialNum++;
	}
	
	//这里用并查集的算法确保查找链路上的每一个节点都指向最新的对象
	public static <T> void checkObject(T obj)
	{
		obj = (T)getLatestObject(obj);
	}
	
	public static void updateObject(int hashCode,Object obj)
	{
		objectList.put(hashCode, obj);
	}
	
	public static Object getLatestObject(Object obj)
	{
		Object resObj = objectList.get(obj.hashCode());
		if(resObj==null)
			return obj;
		Object lastObj = getLatestObject(resObj);
		objectList.put(obj.hashCode(),lastObj);
		
		return lastObj;
	}

	private static String toHex(byte[] bytes) {

		final char[] HEX_DIGITS = "0123456789ABCDEF".toCharArray();
		StringBuilder ret = new StringBuilder(bytes.length * 2);
		for (int i=0; i<bytes.length; i++) {
			ret.append(HEX_DIGITS[(bytes[i] >> 4) & 0x0f]);
			ret.append(HEX_DIGITS[bytes[i] & 0x0f]);
		}
		return ret.toString();
	}

	private static HashSet<String> generatedSet = new HashSet<String>();

	private static boolean isGenerated(String cname) {
		synchronized (generatedSet) {
			return generatedSet.contains(cname);
		}
	}

	private static void setGenerated(String cname) {
		synchronized (generatedSet) {
			generatedSet.add(cname);
		}
	}

	public static Class compositeShadowForClass(Class clazz) {
		try{
			XmlParser doc = XmlParser.getInstance();

			String lastClassName = clazz.getSimpleName();
			Class lastClass = clazz;
			if (lastClassName.contains("_")) {
				lastClassName = lastClassName.split("_")[1];
				lastClass = Class.forName(lastClassName);
			}

			ArrayList<String> shadows = doc.lookupShadowsOfClass(lastClassName);
			String shadowClassName = "";
			if (shadows.size() == 0)
				return lastClass;
			else if (shadows.size() == 1) {
				if (shadows.get(0).equals(clazz.getSimpleName()))
					return clazz;
				else
					return Class.forName(shadows.get(0));
			} else {
				String parentId = lastClassName;
				String gNamePreMd5 = lastClassName;

				MessageDigest md = null;
				try {
					md = MessageDigest.getInstance("MD5");
					ClassPool cp = new ClassPool(ClassPool.getDefault());
					for (String shadow : shadows) {
						gNamePreMd5 = gNamePreMd5 + ";" + shadow;

						String newName = "g" + toHex(md.digest(gNamePreMd5.getBytes())) + "_" + lastClassName;

						if (!isGenerated(newName)) {
							try {
								Class.forName(newName);
							} catch (ClassNotFoundException e) {
								CtClass c = cp.get(shadow);
								c.setName(newName);
								c.setSuperclass(cp.get(parentId));
								c.writeFile();
							}
							setGenerated(newName);
						}
						parentId = newName;
					}
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
					return null;
				}
				shadowClassName = parentId;
			}
			return Class.forName(shadowClassName);
		}
		catch(Exception e)
		{
			System.out.println("composite failed:" + clazz.getSimpleName());
			e.printStackTrace();
			return clazz;
		}
	}

	static ConcurrentHashMap<Class, Constructor> cache_ = new ConcurrentHashMap<Class, Constructor>();
	public static void invalidCache() {
		cache_.clear();
	}
	public static Object newInstance(Class clazz, Class[] argTypes, Object[] args) {
		Constructor constructor = cache_.get(clazz);
		Object obj;
		if (constructor == null) {
			try {

				Class gclass = compositeShadowForClass(clazz);
				System.out.println(gclass.getName());
				constructor = gclass.getDeclaredConstructor(argTypes);
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
			constructor.setAccessible(true);
			cache_.put(clazz, constructor);
		}
		try {
			obj = constructor.newInstance(args);
		} catch(Exception e) {
			System.out.println("can't new an object of "+clazz.getName());
			e.printStackTrace();
			return null;
		}

		return obj;
	}
}
