external int varA = 0;
external int varB = 1;

class Main
{
    static int passNum = 0;
    static int failedNum = 0;

    static void check(String item, int expected, int actual)
    {
        if (actual == expected)
        {
            System.out.println(item + " PASS");
            ++passNum;
        }
        else
        {
            System.out.println(item + " FAILED expected: " + expected + " but got " + actual);
            ++failedNum;
        }
    }

    public static void main(String[] args)
    {
/*
        TestA a = new TestA();
System.out.println("createA:" + a.hashCode());
        a.actionA();
        check("test", 11, a.getAX());
        TestB b = new TestB();
        System.out.println(b.getClass().getName());
System.out.println("createB:" + b.hashCode());
        b.actionA();
        b.actionB();
        check("test", 11, b.getAX());
        check("test", 21, b.getBX());
        TestB c = new TestB();
        System.out.println(c.getClass().getName());
        c.actionB();
        check("test", 21, c.getBX());
        System.out.println("PASS : FAILED = " + passNum + " : " +failedNum);
*/
/* test para
      try {

            TestB b = new TestB();
            varB = 0;Thread.sleep(1000);
long t0 = System.nanoTime();
            b.actionB();
System.out.println(System.nanoTime() - t0);
            System.out.println(b.getBX());
            Thread.sleep(1000);


        } catch (InterruptedException e) {
            e.printStackTrace(); 
        }
*/
            for (int i = 0; i < 1000; ++i) { TestB b = new TestB(); b.actionB(); } //warm up
final int casenum = 1000;
            TestB[] b = new TestB[casenum];
for (int i = 0; i < 1000; ++i) { b[i] = new TestB();}
            long t0 = System.nanoTime();
            for (int i = 0; i < casenum; ++i) {b[i].actionB(); }
            System.out.println(System.nanoTime() - t0);
       System.exit(0);
    }
}
