package granulej.lang;
import javassist.*;

import java.io.IOException;

public class ShadowSetClassGenerator {
    public void addShadow(String name)
    {

    }

    public void build()
    {

    }

    public static void main(String[] args)
    {
        ClassPool cp = ClassPool.getDefault();
        try {
            cp.insertClassPath("D:\\GranuleJ\\GOP\\GranuleJIDE\\example\\Log\\bin");
            CtClass c1 = cp.get("gLowOverload_AbstractGenerator");
            CtClass c2 = cp.get("gPlainText_SysLogGenerator");
            c1.setName("g_6B41DFD954DC2D8014CD3244F1C966D5");
            c1.setSuperclass(c2.getSuperclass());
            c2.setName("g_2418E6D575AFE46C46D7380BC1F90A61");
            c2.setSuperclass(c1);
            c1.writeFile();
            c2.writeFile();
        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (CannotCompileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
