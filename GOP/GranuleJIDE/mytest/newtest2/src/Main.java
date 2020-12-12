
    external int nv1X0=1;

class Main {
    public static void main(String[] args) {
        for (int i = 0; i < 100000; ++i) {
            NTree1X0 t = new NTree1X0(); //warm up
        }

        long startTime = System.nanoTime();
        for (int i = 0; i < 10000000; ++i) {
            new NTree1X0();
        }
        long endTime = System.nanoTime();
        System.out.println(endTime - startTime);
        System.exit(0);
    }
}

