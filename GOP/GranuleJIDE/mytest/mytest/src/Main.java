external int varA = 0;
external int varB = 0;
external S s1 = new S();

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
        TestA a = new TestA();
        varA = 0;
        a.actionA();
        check("test", 11, a.getAX());
        TestB b = new TestB();
        b.actionA();
        b.actionB();
        check("test", 11, b.getAX());
        check("test", 21, b.getBX());
        System.out.println("PASS : FAILED = " + passNum + " : " +failedNum);
S t = new S();
t.value = 100;
s1 = t;
        TestC c = new TestC();
        c.print();
    }
}
