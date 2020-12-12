
    external int v2X0=0;
external int v2X1=0;

class Main {
    public static void main(String[] args) {
        Tree2X1 t = new Tree2X1();
        int counter = 0;
        for (int i = 0; i < 100000; ++i)
             counter += t.get1(); //warm up

        long startTime = System.nanoTime();
        for (int i = 0; i < 100000; ++i)
             counter += t.get1(); //run
        long endTime = System.nanoTime();
        System.out.println(endTime - startTime);
        System.exit(0);
    }
}

