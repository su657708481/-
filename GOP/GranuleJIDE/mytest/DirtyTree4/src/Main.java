
    external int v4X0=0;
external int v4X1=0;
external int v4X2=0;
external int v4X3=0;

class Main {
    public static void main(String[] args) {
        Tree4X3 t = new Tree4X3();
        int counter = 0;
        for (int i = 0; i < 100000; ++i)
             counter += t.get3(); //warm up

        long startTime = System.nanoTime();
        for (int i = 0; i < 100000; ++i)
             counter += t.get3(); //run
        long endTime = System.nanoTime();
        System.out.println(endTime - startTime);
        System.exit(0);
    }
}

