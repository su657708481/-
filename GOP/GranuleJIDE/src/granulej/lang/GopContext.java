package granulej.lang;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class GopContext {
	private static HashMap<String, Context> contexts=new HashMap<String, Context>();
	private static HashMap<String, Object> object_cache = new HashMap<String, Object>();

	private static ArrayList<String> con_list=new ArrayList<String>();
	private static String p_str="";
	
	public static synchronized HashMap<String, Context> getContexts() {
		return contexts;
	}

	public static void setContexts(HashMap<String, Context> contexts) {
		GopContext.contexts = contexts;
	}

	public static String getContextSequence() {
		addContexts();
		System.out.println(p_str);
		return p_str;
	}
	public static void setContextSequence(String p_str) {
		GopContext.p_str = p_str;
	}
	public static synchronized String getContext(String name,String modifiers) {      	
		   if(contexts.containsKey(name)){
		     Context cn=contexts.get(name);
		     if(cn!=null && cn.getValue()!=null){
		    	 return cn.getValue().trim();
		     }
		     return "";			   
		   }
		   else 
			 return "";
	}

	public static byte[] base64Decode(String data)
	{
		int[] tbl = {
				-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
				-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
				-1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, 63, 52, 53, 54,
				55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2,
				3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19,
				20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, -1, 26, 27, 28, 29, 30,
				31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47,
				48, 49, 50, 51, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
				-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
				-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
				-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
				-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
				-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
				-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
				-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 };
		byte[] bytes = data.getBytes();
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		for (int i = 0; i < bytes.length; ) {
			int b = 0;
			if (tbl[bytes[i]] != -1) {
				b = (tbl[bytes[i]] & 0xFF) << 18;
			}
			// skip unknown characters
			else {
				i++;
				continue;
			}

			int num = 0;
			if (i + 1 < bytes.length && tbl[bytes[i+1]] != -1) {
				b = b | ((tbl[bytes[i+1]] & 0xFF) << 12);
				num++;
			}
			if (i + 2 < bytes.length && tbl[bytes[i+2]] != -1) {
				b = b | ((tbl[bytes[i+2]] & 0xFF) << 6);
				num++;
			}
			if (i + 3 < bytes.length && tbl[bytes[i+3]] != -1) {
				b = b | (tbl[bytes[i+3]] & 0xFF);
				num++;
			}

			while (num > 0) {
				int c = (b & 0xFF0000) >> 16;
				buffer.write((char)c);
				b <<= 8;
				num--;
			}
			i += 4;
		}
		return buffer.toByteArray();
	}

	public static synchronized Object getContextObject(String name, String modifiers) {
		if (object_cache.containsKey(name))
			return object_cache.get(name);

		try {
			java.io.ObjectInputStream objectin = new java.io.ObjectInputStream(new java.io.ByteArrayInputStream(
					base64Decode(getContext(name, modifiers))
			));
			Object obj = objectin.readObject();
			object_cache.put(name, obj);
			return obj;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static synchronized void addContexts(){
		 StringBuffer pstr=new StringBuffer();
         int len=con_list.size();
		 Iterator<String> iter = con_list.iterator();
		 int i=0;
		 while(iter.hasNext()){
		  i++;
		  String str = (String) iter.next();
		  pstr.append(str);
		  if(i!=len)
		  pstr.append(";");
		 }
		 p_str=pstr.toString();
		 System.out.println("Context variable is published !");
	}
	public static synchronized void addContext(String name,String value)
	{
	   if(!contexts.containsKey(name))
	   {  
		  Context cn=new Context(name,value);
		  contexts.put(name,cn);		  
	   }		
	}
	
	public static synchronized void addContext(String name, String value, String modifiers) {
		    String p_str=name+":"+value+":"+modifiers;
		    if(!con_list.contains(p_str))
		    con_list.add(p_str);
		    Context cn=new Context(name,value,modifiers);
		    if(!contexts.containsKey(name))
		    contexts.put(name,cn);
		    //System.out.println("context variable is added!");		
	}
	
	public static synchronized void modifyContext(String name,String newValue)
	{ 
	    if(contexts.containsKey(name)){
		 Context ct=(Context)contexts.get(name);
		 if("".equals(newValue))
         newValue=null;
		 ct.setValue(newValue);
		}		
	}
	
	public static synchronized void modifyContext(String name, String newValue,String modifiers) {
          if(contexts.containsKey(name)) {
        	 Context ct=(Context)contexts.get(name);        	 
        	 if(modifiers!=null && !modifiers.equals("") && !modifiers.equals(ct.getModifiers()))
             ct.setModifiers(modifiers);
        	 //修改值
        	 if(newValue==null && newValue==ct.getValue())
        	 return ;
        	 if(newValue!=null && ct.getValue()!=null && newValue.equals(ct.getValue()))
        	 return ;
        	 if(newValue==null && ct.getValue()!=null || newValue!=null && ct.getValue()==null || newValue!=null && ct.getValue()!=null && !newValue.equals(ct.getValue())){
                if("".equals(newValue))
                  newValue=null;        		 
                ct.setValue(newValue);
                System.out.println("Context Changed: name = " + name + ", value = " + newValue);
				ContextChangedEvent.notifyChanged(name);
               //new Thread(new ContextModify(name+":"+newValue)).start();
                new ContextModify(name+":"+newValue).run();
        	 }
          }
	}

	private static String base64Encode(byte[] data) {
		char[] tbl = {
				'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P',
				'Q','R','S','T','U','V','W','X','Y','Z','a','b','c','d','e','f',
				'g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v',
				'w','x','y','z','0','1','2','3','4','5','6','7','8','9','+','/' };

		StringBuilder buffer = new StringBuilder();
		int pad = 0;
		for (int i = 0; i < data.length; i += 3) {

			int b = ((data[i] & 0xFF) << 16) & 0xFFFFFF;
			if (i + 1 < data.length) {
				b |= (data[i+1] & 0xFF) << 8;
			} else {
				pad++;
			}
			if (i + 2 < data.length) {
				b |= (data[i+2] & 0xFF);
			} else {
				pad++;
			}

			for (int j = 0; j < 4 - pad; j++) {
				int c = (b & 0xFC0000) >> 18;
				buffer.append(tbl[c]);
				b <<= 6;
			}
		}
		for (int j = 0; j < pad; j++) {
			buffer.append("=");
		}

		return buffer.toString();
	}

	public static String seraializeObject(java.io.Serializable object) {
		try
		{
			java.io.ByteArrayOutputStream bytesout = new java.io.ByteArrayOutputStream();
			java.io.ObjectOutputStream objectout = new java.io.ObjectOutputStream(bytesout);
			objectout.writeObject(object);
			objectout.close();
			return base64Encode(bytesout.toByteArray());
		} catch(IOException e)
		{
			return null;
		}
	}

	public static synchronized void modifyContextByObject(String name, java.io.Serializable object, String modifiers) {
		modifyContext(name, seraializeObject(object), modifiers);
		object_cache.put(name, object);
	}

	public static synchronized void addContextByObject(String name, java.io.Serializable object, String modifiers) {
		addContext(name, seraializeObject(object), modifiers);
		object_cache.put(name, object);
	}
}