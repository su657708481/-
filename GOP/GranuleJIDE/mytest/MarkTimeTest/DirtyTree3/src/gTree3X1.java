
granule gTree3X1(Tree3X1) {
    external int v3X1;
    {
        return v3X1 == 0;
    }
}
class Tree3X1 within gTree3X1 {
    public int get1() { return 200; }
}
