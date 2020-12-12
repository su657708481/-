
    external int v10X0=0;
external int v10X1=0;
external int v10X2=0;
external int v10X3=0;
external int v10X4=0;
external int v10X5=0;
external int v10X6=0;
external int v10X7=0;
external int v10X8=0;
external int v10X9=0;

class Main {
    public static void main(String[] args) {
        Tree10X9 t = new Tree10X9();
        int counter = 0;
        for (int i = 0; i < 100000; ++i)
             counter += t.get9(); //warm up

        long startTime = System.nanoTime();
        for (int i = 0; i < 100000; ++i)
             counter += t.get9(); //run
        long endTime = System.nanoTime();
        System.out.println(endTime - startTime);
        System.exit(0);
    }
}

