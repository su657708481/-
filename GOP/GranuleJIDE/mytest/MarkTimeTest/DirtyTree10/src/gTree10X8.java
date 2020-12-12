
granule gTree10X8(Tree10X8) {
    external int v10X8;
    {
        return v10X8 == 0;
    }
}
class Tree10X8 within gTree10X8 {
    public int get8() { return 200; }
}
