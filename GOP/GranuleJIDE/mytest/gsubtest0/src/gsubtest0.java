
granule gSubTest0(Subtest) {
    external int Subtestcv0;
    {
        return Subtestcv0 == 0;
    }
}
class Subtest within gSubTest0 {
    public int get() { return 0; }
}
