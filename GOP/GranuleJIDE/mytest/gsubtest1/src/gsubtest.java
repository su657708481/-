
granule gSubTest1(Subtest) {
    external int Subtestcv0;
    {
        return Subtestcv0 == 1;
    }
}
class Subtest within gSubTest1 {
    public int get() { return 1; }
}
