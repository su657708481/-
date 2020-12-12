
granule gTree10X1(Tree10X1) {
    external int v10X1;
    {
        return v10X1 == 0;
    }
}
class Tree10X1 within gTree10X1 {
    public int get1() { return 200; }
}
