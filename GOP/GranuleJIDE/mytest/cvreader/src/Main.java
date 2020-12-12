
external CVTestType cv = new CVTestType(100);

class Main
{
    public static void main(String[] args)
    {
        CVClass c = new CVClass();
        c.get();

        System.out.println("Wait for context change");
        cv = new CVTestType(200);
        c.get();
    }
}
