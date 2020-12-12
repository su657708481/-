
granule gTree10X5(Tree10X5) {
    external int v10X5;
    {
        return v10X5 == 0;
    }
}
class Tree10X5 within gTree10X5 {
    public int get5() { return 200; }
}
