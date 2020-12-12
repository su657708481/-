
    external int v1X0=0;

class Main {
    public static void main(String[] args) {
        Tree1X0 t = new Tree1X0();
        int counter = 0;
        for (int i = 0; i < 100000; ++i)
             counter += t.get0(); //warm up

        long startTime = System.nanoTime();
        for (int i = 0; i < 100000; ++i)
             counter += t.get0(); //run
        long endTime = System.nanoTime();
        System.out.println(endTime - startTime);
        System.exit(0);
    }
}

