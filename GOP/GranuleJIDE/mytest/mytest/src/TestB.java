public class TestB extends TestA
{
    public int bx = 20;
    public TestB()
    {
    }
    public void actionB() {}
    public void setBX(int newX) { bx = newX; }
    public int getBX() {System.out.println("TestB:"+hashCode());return bx;}
}
