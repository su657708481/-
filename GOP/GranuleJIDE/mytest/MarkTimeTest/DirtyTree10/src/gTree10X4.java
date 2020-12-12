
granule gTree10X4(Tree10X4) {
    external int v10X4;
    {
        return v10X4 == 0;
    }
}
class Tree10X4 within gTree10X4 {
    public int get4() { return 200; }
}
