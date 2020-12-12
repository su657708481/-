external int v0;
external int v1;
external int v2;
external int v3;

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
        Tree3 tree = new Tree3();
        check("0", 1000, tree.get0());
        check("1", 1000, tree.get1());
        check("2", 1000, tree.get2());
        check("3", 1000, tree.get3());
        check("0", 1000, tree.get0());
        check("1", 1000, tree.get1());
        check("2", 1000, tree.get2());
        check("3", 1000, tree.get3());
        System.out.println("PASS : FAILED = " + passNum + " : " +failedNum);

        System.exit(0);
    }
}
