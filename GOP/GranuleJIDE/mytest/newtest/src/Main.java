
    external int nv1X0=0;

class Main {
    public static void main(String[] args) {
if (true) {
        for (int i = 0; i < 100000; ++i) {
            NTree1X0 t = new NTree1X0(); //warm up
            t.get0();
        }

        long startTime = System.nanoTime();
        for (int i = 0; i < 10000000; ++i)
            new NTree1X0();
        long endTime = System.nanoTime();
        System.out.println(endTime - startTime);
        System.exit(0);
} else {
nv1X0 = 1;
        for (int i = 0; i < 100000; ++i) {
            NTree1X0 t = new NTree1X0(); //warm up
            t.get0();
        }
        NTree1X0[] arr = new NTree1X0[100000];
        for (int i = 0; i < 100000; ++i)
            arr[i] = new NTree1X0();
        long startTime = System.nanoTime();
        for (int i = 0; i < 100000; ++i) {
             arr[i].get0();
        }
        long endTime = System.nanoTime();
        System.out.println(endTime - startTime);
        System.exit(0);
}
    }
}

