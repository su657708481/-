
granule gTree10X6(Tree10X6) {
    external int v10X6;
    {
        return v10X6 == 0;
    }
}
class Tree10X6 within gTree10X6 {
    public int get6() { return 200; }
}
