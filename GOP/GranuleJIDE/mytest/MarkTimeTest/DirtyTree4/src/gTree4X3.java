
granule gTree4X3(Tree4X3) {
    external int v4X3;
    {
        return v4X3 == 0;
    }
}
class Tree4X3 within gTree4X3 {
    public int get3() { return 200; }
}
