
    external int v5X0=0;
external int v5X1=0;
external int v5X2=0;
external int v5X3=0;
external int v5X4=0;

class Main {
    public static void main(String[] args) {
        Tree5X4 t = new Tree5X4();
        int counter = 0;
        for (int i = 0; i < 100000; ++i)
             counter += t.get4(); //warm up

        long startTime = System.nanoTime();
        for (int i = 0; i < 100000; ++i)
             counter += t.get4(); //run
        long endTime = System.nanoTime();
        System.out.println(endTime - startTime);
        System.exit(0);
    }
}

