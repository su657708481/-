
granule gTree30X10(Tree30X10) {
    external int v30X10;
    {
        return v30X10 == 0;
    }
}
class Tree30X10 within gTree30X10 {
    public int get10() { return 200; }
}
