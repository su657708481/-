
granule gTree3X2(Tree3X2) {
    external int v3X2;
    {
        return v3X2 == 0;
    }
}
class Tree3X2 within gTree3X2 {
    public int get2() { return 200; }
}
