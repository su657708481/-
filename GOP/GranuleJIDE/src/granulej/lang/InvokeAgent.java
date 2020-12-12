package granulej.lang;

import granulej.gui.datastructure.ExecuteUnit;
import granulej.gui.datastructure.GranuleUnit;
import gui.constant.GranuleConstant;
import javassist.ClassPool;
import javassist.CtClass;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class InvokeAgent {
	
	private static ArrayList<String> lastGranules = null;
	private static HashMap<String,Class> loadedGranules=new HashMap<String,Class>();
	private static HashMap<String,Method> fitnessMethods = new HashMap<String,Method>();
	
	//做适合性检查
	public static <IN> boolean doFitness(String gName)
	{
		//System.out.println("in doFitness");
		//long t0 = System.nanoTime();
		GranuleNode gn = GranuleTree.getInstance().getGranuleNode(gName);
		if (gn == null || gn.isRemoved()) {
			lastGranules = null;
			return false;
		}

		if (GranuleOptions.enableDirtyFlag && !gn.isDirty() && gn.getStatus())
			return true;

        //记录下当前可能要处理的粒
		lastGranules = null;
		boolean result = fitnessChecking(gName);
		//long t1 = System.nanoTime();
		//if (GranuleOptions.enableAnalyseGranuleSub)
			//System.out.println("fitness check:" + (t1 - t0));


		return result;
/*
		 * 这里代码有用
		 * 这是遍历演化记录，来查找之前到现在是否有合理的粒，也就是本地先查找是否有合适的粒可使用
		for(String gName:gNameList)
		{
			printLog("granuleName: "+gName);
			if(fitnessChecking(className,gName,methodName,methodSignature))
				return true;
		}
*/

	}
	
	//替换返回类型不是void的方法
	public static <IN,OUT> OUT replaceMethod(IN obj,String gName,String methodName,Class[] argsTypes,Object [] args)
	{
//		printLog("in replaceMethod");

		ExecuteUnit exeUnit;
		long t0 = 0, t1 = 0;
		if (lastGranules == null) {
			exeUnit = compositeAndTransfer(obj);
		} else {
			t0 = System.nanoTime();
			GranuleUnit granuleUnit = searchForSimilarGranule(lastGranules);

			if(granuleUnit==null)
			{
				System.out.println("can't find similar granule!!!");
				return null;
			}
			t1 = System.nanoTime();
			exeUnit = analyseExecuteUnit(obj,gName,granuleUnit);
			if(exeUnit==null)
			{
				System.out.println("can't find new execute method!!!");
				return null;
			}
		}
		long t2 = System.nanoTime();
		//执行新对象的函数,并将新的对象更新到对象列表中
		Object result = executeShadowClassMethod(exeUnit.newInstance,exeUnit.newClass,methodName,argsTypes,args);
		long t3 = System.nanoTime();
		ObjectAgent.updateObject(obj.hashCode(), exeUnit.newInstance);
		long t4 = System.nanoTime();
		if (lastGranules != null)
			new Thread(new xmlFileUpdate(IndividualInfo.getInstance().getConfigfile(),"cTree")).start();
		long t5 = System.nanoTime();
		if (GranuleOptions.enableAnalyseGranuleSub) {
			System.out.println("search: " + (t1 - t0));
			System.out.println("analyse: " + (t2 - t1));
			System.out.println("exec: " + (t3 - t2));
			System.out.println("update: " + (t4 - t3));
			System.out.println("xml: " + (t5 - t4));
			System.out.println("sum: " + (t5 - t0));
		}

		ObjectAgent.invalidCache();
		return (OUT)result;
	}
	
	//替换返回类型为void的方法
	public static <IN> void replaceVoidMethod(IN obj,String gName,String methodName,Class[] argsTypes,Object [] args)
	{
//		printLog("in replaceVoidMethod");

		ExecuteUnit exeUnit;
        long t0 = 0, t1 = 0;
		if (lastGranules == null) {
			exeUnit = compositeAndTransfer(obj);
		} else {
            t0 = System.nanoTime();
			GranuleUnit granuleUnit = searchForSimilarGranule(lastGranules);
			if(granuleUnit==null)
			{
				System.out.println("can't find similar granule!!!");
				return;
			}
            t1 = System.nanoTime();
			exeUnit = analyseExecuteUnit(obj,gName,granuleUnit);

			if(exeUnit==null)
			{
				System.out.println("can't find new execute method!!!");
				return;
			}
		}

//		test();		
		
//		//执行新对象的函数，并将新的对象更新到对象列表中
        long t2 = System.nanoTime();
		executeShadowClassMethod(exeUnit.newInstance,exeUnit.newClass,methodName,argsTypes,args);
        long t3 = System.nanoTime();
		ObjectAgent.updateObject(obj.hashCode(), exeUnit.newInstance);
        long t4 = System.nanoTime();
		if (lastGranules != null)
			new Thread(new xmlFileUpdate(IndividualInfo.getInstance().getConfigfile(),"cTree")).start();
        long t5 = System.nanoTime();
		if (GranuleOptions.enableAnalyseGranuleSub) {
			System.out.println("search: " + (t1 - t0));
			System.out.println("analyse: " + (t2 - t1));
			System.out.println("exec: " + (t3 - t2));
			System.out.println("update: " + (t4 - t3));
			System.out.println("xml: " + (t5 - t4));
			System.out.println("sum: " + (t5 - t0));
		}
		ObjectAgent.invalidCache();
	}	
	
	private static GranuleUnit searchForSimilarGranule(ArrayList<String> gNames)
	{
		if (GranuleOptions.enablePreloadThread)
			return PreloadThread.getInstance().searchForSimilarGranule(gNames);

		long t0 = System.nanoTime();
		//如果当前粒为空，那就没法替换
		if(gNames == null || gNames.size()==0)
			return null;

		Object[] similarGra = GranulePolling.getInstance().getPollingResult(gNames.get(0), 0);
		if(similarGra[1].equals(GranuleConstant.GRANULE_NOT_EXIST))
		{
			System.out.println("no similar granule on granule server!!!");
			return null;
		}

		Class simiGranule=(Class)similarGra[0];
		String simiFileName=(String)similarGra[1];

		boolean isFit = executeGranule(simiGranule);
		//System.out.println("find similar granule: " + simiGranule + " " + isFit);
		GranuleUnit unit = new GranuleUnit(simiGranule,null,simiFileName);

		long t1 = System.nanoTime();
		if (GranuleOptions.enableSearchTimeStat)
			System.out.println("SearchTime: " + (t1 - t0));
		return unit;

		//查找并执行相似粒，直到找到合适的粒为止

		//Class currentGranule = null;
/*
		while(simiGranule==null||simiFileName==null||currentGranule==null)
		{

			
			boolean isFit = true;
			for(int j=gNames.size()-1;j>=0&&isFit;--j)
			{
				 String gName = gNames.get(j);
				 //TODO 这里如果在服务器上实在找不到粒，应该跳出去，但现在的服务器没有处理这种情况，找不到就一直找
//				 System.out.println("gName: "+gName);
	             Object [] similarGra=GranulePolling.getInstance().getPollingResult(gName,0);
//	             System.out.println(similarGra[1]);
	             //此处用于测试排除网络部分的执行效率
//				 Object [] similarGra = new Object[2];
//				 similarGra[0] = loadedGranules.get(gName+"2");
//				 if(similarGra[0]==null)
//					 System.out.println("granule is null");
//				 similarGra[1] = "C:/Users/Administrator/Downloads/GranuleJIDE/example/Test_1/bin/TestGranuleTree.xml";
	             if(similarGra[1].equals(GranuleConstant.GRANULE_NOT_EXIST))
	             {
	            	 System.out.println("no similar granule on granule server!!!");
	            	 return null;
	             }
	
	             //TODO 这里还需要对相似粒的子粒做适合性检测，只有当对应的子粒也符合适合性条件的时候才能算是相似粒
	             if(executeGranule((Class)similarGra[0]))
	             {
	            	 if(j==0)
	            	 {
	            		 simiGranule = (Class)similarGra[0];
	            	 }
	            	 if(j==gNames.size()-1)
	            	 {
	            		 currentGranule = (Class)similarGra[0];
		                 simiFileName = (String)similarGra[1]; 
	            	 }
	             }
	             else
	             {
	            	 isFit = false;
	             }
			}
			

		}

//		printLog("find similar granule: "+simiGranule);
		
		//如果服务器上找不到合适的粒
		if(simiGranule==null||simiFileName==null)
		{
			System.out.println("Similar granule not found!");
			return null;
		}
		
		GranuleUnit unit = new GranuleUnit(simiGranule,currentGranule,simiFileName);

		return unit;
		*/
	}

	private static ExecuteUnit compositeAndTransfer(Object obj) {
	    long t0 = System.nanoTime();
		Class newClass = ObjectAgent.compositeShadowForClass(obj.getClass());
		if(newClass==null)
		{
			System.out.println("load new class failed");
			return null;
		}
        long t1 = System.nanoTime();
		Object newInstance = transferClassAndObjectState(newClass,obj);
        long t2 = System.nanoTime();
		if(newInstance==null)
		{
			printLog("create new object failed");
			return null;
		}
		if (GranuleOptions.enableAnalyseGranuleSub)
		{
			System.out.println("composite:" + (t1 - t0));
			System.out.println("transfer:" + (t2 - t1));
		}
		return new ExecuteUnit(newInstance, newClass);
	}

	private static ExecuteUnit analyseExecuteUnit(Object obj,String gName,GranuleUnit unit)
	{
//		printLog("in ExecuteUnit");
		//是否在安全点
		String className = obj.getClass().getName();
		if(isSafePoint(className))
		{
			String shadowClassName = "";
			try{
//				System.out.println("lastGranules.get(0): "+lastGranules.get(0));
//				System.out.println("unit.simiFileName: "+unit.simiFileName);
//				System.out.println("simiGranule.getName(): "+unit.simiGranule.getName());
				long t0 = System.nanoTime();
				XmlParser.replaceGranuleTree(lastGranules.get(0), unit.simiFileName, unit.simiGranule.getName());
				long t1 = System.nanoTime();
				System.out.println("replacetree:" + (t1 - t0));
			}
			
			catch(Exception e)
			{
				e.printStackTrace();
				return null;
			}
            //shadowClassName = unit.currentGranule.getName()+"_"+className.split("_")[1];
			
			//当粒替换发生的位置在父粒上的时候，需要用匹配的新结果来组装影子类的名字
//			shadowClassName = gName+"_"+className.split("_")[1];

//			System.out.println(shadowClassName);

//			Object curInstance = instances.get(obj.hashCode());

			/*
			ClassLoader classLoader = obj.getClass().getClassLoader();

			Class newClass = null;
			try{
				newClass = classLoader.loadClass(shadowClassName);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			*/

			return compositeAndTransfer(obj);

		}
		
//		System.out.println("class is not on the safePoint!");
		return null;
	}

	private static Method getMethod(Class cls, String methodName, Class[] argsTypes) {
		while (cls != null) {
			try{
				Method method=cls.getDeclaredMethod(methodName,argsTypes);
				return method;
			}
			catch(Exception e) {
				cls = cls.getSuperclass();
			}
		}
		return null;
	}

	//执行影子类的方法
	private static Object executeShadowClassMethod(Object obj,Class cls,String methodName,Class [] argsTypes,Object[] args)
	{
		//获取方法
//		printLog("class:"+cls.getName());
		Method method = getMethod(cls, methodName, argsTypes);
		if (method == null) {
			printLog(methodName+"get method failed"+"  ! in method of executeShadowClassMethod");
			return null;
		}

		Object result=null;
		//执行方法
		try{
			method.setAccessible(true);
			result=method.invoke(obj, args);
		}
		catch(Exception e)
		{
			printLog(methodName+"execute method failed"+"  ! in method of executeShadowClassMethod");
			printLog(e.toString());
		}
		
		return result;
	}

	private static List<Field> getAllFields(Class<?> type) {
		List<Field> fields = new ArrayList<Field>();
		for (Class<?> c = type; c != null; c = c.getSuperclass()) {
			fields.addAll(Arrays.asList(c.getDeclaredFields()));
		}
		return fields;
	}
	private static HashMap<String, Field> getAllFieldsMap(Class<?> type) {
		HashMap<String, Field> h = new HashMap<String, Field>();
		for (Field field: getAllFields(type)) {
			h.put(field.getName(), field);
		}
		return h;
	}
	//装换类的状态和对象的状态
	private static Object transferClassAndObjectState(Class newClass,Object oldObject)
	{
		//接下来该转换类的状态和对象，对应vs中的translateClassState()
		Class oldClass=oldObject.getClass();
		
//		System.out.println("start transfer class and object!");
		
		//生成新的对象
		Object resObj=null;
		try{
			Constructor constructor = newClass.getDeclaredConstructor();
			constructor.setAccessible(true);

			resObj=constructor.newInstance();
		}
		catch(Exception e)
		{
			printLog(e.toString());
			printLog(newClass.getName()+"create new instance failed"+"  ! in method of transferClassAndObjectState");
			return resObj;
		}
		
		int fieldCount=0;
		//获取新类的属性，如果刚好旧类中的静态属性存在，那么就转移过来，如果不存在，则初始化为null
		List<Field> newFields = getAllFields(newClass);
		HashMap<String, Field> oldFields = getAllFieldsMap(oldClass);

		for(Field field:newFields)
		{
			//如果属性是静态的，则转换到类的属性上
			if(Modifier.isStatic(field.getModifiers()))
			{
				//获取该属性的值
				Object fieldValue = null;
				try{
					Field oldField = oldFields.get(field.getName());

					if(oldField!=null&&Modifier.isStatic(oldField.getModifiers()))
					{
						oldField.setAccessible(true);
						fieldValue=oldField.get(null);
						System.out.println("static value:" + fieldValue.toString());
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
					printLog(field.getName()+"get static field failed"+"  ! in method of transferClassAndObjectState");
				}
				
				
				//将属性设置到新类中
				try{
					field.setAccessible(true);
					field.set(null, fieldValue);
				}
				catch(Exception e)
				{
					e.printStackTrace();
					printLog(field.getName()+"set static filed failed"+"  ! in method of transferClassAndObjectState");				
				}
			}
			else  //不是静态属性，则将属性的值转换到新的对象上
			{
				//获取该属性的值
				Object fieldValue = null;
				try{
					Field oldField = oldFields.get(field.getName());
					if(oldField!=null&&!Modifier.isStatic(oldField.getModifiers()))
					{
						oldField.setAccessible(true);
						fieldValue=oldField.get(oldObject);
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
					printLog(field.getName()+"get object filed failed"+"  ! in method of transferClassAndObjectState");
				}
				
				//将属性设置到新类中
				try{
					field.setAccessible(true);
					field.set(resObj, fieldValue);
				}
				catch(Exception e)
				{
					e.printStackTrace();
					printLog(field.getName()+"set object field failed"+"  ! in method of transferClassAndObjectState");				
				}
			}
		}
		
		return resObj;
		
	}

	private static void getDirtyParents(GranuleNode g, ArrayList<String> result) {
		while (g != null && !g.getGranuleName().equals("g0")) {
			if (GranuleOptions.enableDirtyFlag && !g.isDirty() && g.getStatus())
				break;
			result.add(g.getGranuleName());
			g = g.getGranuleParent();
		}
	}

	private static ArrayList<String> getDirtyParents(String gName) {
		ArrayList<String> result = new ArrayList<String>();
		getDirtyParents(GranuleTree.getInstance().getGranuleNode(gName), result);
		return result;
	}

	//TODO 这里遇到一个问题，适合性检查如果是检查当前粒的所有父粒是不是都是true的话，那么如果遇到中间粒为false，将导致粒树需要替换这个粒所有的子粒，
	//     使得替换对象时，需要更新所有粒对应的对象，这里存在不她合理的问题,所以当前情况下，我只检查了当前粒的状态
	//检查一个粒的适合性
	public static boolean fitnessChecking(String gName)
	{
//		printLog("in fitnessChecking");
		//得到粒的父粒，挨个判断是否满足适合性条件
		ArrayList<String> gParents = getDirtyParents(gName);//GranuleTree.getGranuleParents(gName, "root");
//		ArrayList<String> gParents = new ArrayList<String>();
		Collections.reverse(gParents);
		//依次遍历粒树，挨个执行fitness函数
		for(int i=0;i<gParents.size();++i)
		{
			//将粒load进来

			String graName = gParents.get(i);
			GranuleNode gn = GranuleTree.getInstance().getGranuleNode(graName);

			if (!gn.isDirty() && gn.getStatus())
				continue;
			
/*
			Class granule=loadedGranules.get(graName);
			if(granule==null){
				granule = GranuleLoader.loadGranule(graName);
				loadedGranules.put(graName, granule);
			}
//			printLog("granule name:"+granule.getName());
			
			if(!executeGranule(granule))
			{
				lastGranules = new ArrayList<String>(gParents.subList(i, gParents.size()));
				
//				System.out.println("final result of "+gName+": false");
				return false;
			}
*/
			Method fitMethod = fitnessMethods.get(graName);
			if(fitMethod==null)
			{
				Class granule = GranuleLoader.loadGranule(graName);
				loadedGranules.put(graName, granule);
				
				try{
					fitMethod=granule.getDeclaredMethod("fitness");
				}
				catch(Exception e)
				{
					printLog("get fitness method failed！！");
					e.printStackTrace();
				}
				try{
					//先将fitness方法的访问权限去掉，然后再执行
					fitMethod.setAccessible(true);
					fitnessMethods.put(graName, fitMethod);
//					printLog("fitness result of "+granule.getName()+": "+result);
				}
				catch(Exception e)
				{
					printLog("execute fitness method failed!");
					e.printStackTrace();
				}
			}

			if(!executeFitness(fitMethod))
			{
				lastGranules = new ArrayList<String>(gParents.subList(i, gParents.size()));
				gn.setStatus(false);
				gn.clearDirty();
				return false;
			}
			else
			{
				gn.clearDirty();
				gn.setStatus(true);
			}
		}		

		//System.out.println("final result of "+gName+": true");
		return true;
	}
	
	public static boolean executeGranule(Class granule)
	{
		
////		printLog("granule name is: "+granule.getName());
		if(granule==null)
		{
			return false;
		}
		
		Boolean result=false;
		//获取fitness方法
		Method fitMethod = null;
		try{
			fitMethod=granule.getDeclaredMethod("fitness");
		}
		catch(Exception e)
		{
			printLog("get fitness method failed！！");
			e.printStackTrace();
			return false;
		}
		try{
			//先将fitness方法的访问权限去掉，然后再执行
			fitMethod.setAccessible(true);
			result=(Boolean)fitMethod.invoke(null, null);
//			printLog("fitness result of "+granule.getName()+": "+result);
		}
		catch(Exception e)
		{
			printLog("execute fitness method failed!");
			e.printStackTrace();
			return false;
		}
		
//		System.out.println(granule.getName()+": "+result);
		
		return result;
	}
	
	public static void loadAllGranules(ArrayList<String> granules)
	{

		for(String graName:granules)
		{
			if(graName.equals("g0"))
				continue;
			Class granule=loadedGranules.get(graName);
			if(granule==null){
				System.out.println("已经加载："+graName);
				granule = GranuleLoader.loadGranule(graName);
				loadedGranules.put(graName, granule);
				
				//获取fitness方法
				Method fitMethod = null;
				try{
					fitMethod=granule.getDeclaredMethod("fitness");
				}
				catch(Exception e)
				{
					printLog("get fitness method failed！！");
					e.printStackTrace();
				}
				try{
					//先将fitness方法的访问权限去掉，然后再执行
					fitMethod.setAccessible(true);
					fitnessMethods.put(graName, fitMethod);
//					printLog("fitness result of "+granule.getName()+": "+result);
				}
				catch(Exception e)
				{
					printLog("execute fitness method failed!");
					e.printStackTrace();
				}
			}
		}
		
	}
	
	public static boolean executeFitness(Method fitMethod)
	{
		try{
			return (Boolean)fitMethod.invoke(null, null);
//			printLog("fitness result of "+granule.getName()+": "+result);
		}
		catch(Exception e)
		{
			printLog("execute fitness method failed!");
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean isSafePoint(String className)
	{
		return true;
	}
	
	//根据类名获取粒的名字
	public static String getGranuleName(String className)
	{
		String gName = "";
//		printLog("className: "+className);
		
		//TODO 这里以的标记可能会有问题，因为下划线在函数中是常用的符号，可能会混淆
		int start = className.lastIndexOf("/\\");
		int end = className.lastIndexOf("_");
		if(start>=end)
		{
//			printLog("className： "+className+" get granules failed!!");
			gName=null;
		}
		else
			gName=className.substring(start+1, end-1);
		 
		return gName;
	} 
	
	public static void test()
	{
		Math.exp(10);
	}
	
	//打印log
	public static void printLog(String info)
	{
		System.out.println(info);
	}
	
	public static void main(String args[])
	{
		ArrayList array = new ArrayList();
		array.add("3");
		//Array.get(index);
	}

}
