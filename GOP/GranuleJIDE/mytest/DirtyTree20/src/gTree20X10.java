
granule gTree20X10(Tree20X10) {
    external int v20X10;
    {
        return v20X10 == 0;
    }
}
class Tree20X10 within gTree20X10 {
    public int get10() { return 200; }
}
