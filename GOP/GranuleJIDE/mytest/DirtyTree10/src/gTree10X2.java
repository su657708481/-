
granule gTree10X2(Tree10X2) {
    external int v10X2;
    {
        return v10X2 == 0;
    }
}
class Tree10X2 within gTree10X2 {
    public int get2() { return 200; }
}
