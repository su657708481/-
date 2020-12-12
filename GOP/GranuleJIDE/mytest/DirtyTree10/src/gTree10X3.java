
granule gTree10X3(Tree10X3) {
    external int v10X3;
    {
        return v10X3 == 0;
    }
}
class Tree10X3 within gTree10X3 {
    public int get3() { return 200; }
}
