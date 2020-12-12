
    external int v8X0=0;
external int v8X1=0;
external int v8X2=0;
external int v8X3=0;
external int v8X4=0;
external int v8X5=0;
external int v8X6=0;
external int v8X7=0;

class Main {
    public static void main(String[] args) {
        Tree8X7 t = new Tree8X7();
        int counter = 0;
        for (int i = 0; i < 100000; ++i)
             counter += t.get7(); //warm up

        long startTime = System.nanoTime();
        for (int i = 0; i < 100000; ++i)
             counter += t.get7(); //run
        long endTime = System.nanoTime();
        System.out.println(endTime - startTime);
        System.exit(0);
    }
}

