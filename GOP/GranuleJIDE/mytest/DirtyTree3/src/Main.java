
    external int v3X0=0;
external int v3X1=0;
external int v3X2=0;

class Main {
    public static void main(String[] args) {
        Tree3X2 t = new Tree3X2();
        int counter = 0;
        for (int i = 0; i < 100000; ++i)
             counter += t.get2(); //warm up

        long startTime = System.nanoTime();
        for (int i = 0; i < 100000; ++i)
             counter += t.get2(); //run
        long endTime = System.nanoTime();
        System.out.println(endTime - startTime);
        System.exit(0);
    }
}

