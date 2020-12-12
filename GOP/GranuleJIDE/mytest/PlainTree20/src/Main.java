
    external int v20X0=0;
external int v20X1=0;
external int v20X2=0;
external int v20X3=0;
external int v20X4=0;
external int v20X5=0;
external int v20X6=0;
external int v20X7=0;
external int v20X8=0;
external int v20X9=0;
external int v20X10=0;
external int v20X11=0;
external int v20X12=0;
external int v20X13=0;
external int v20X14=0;
external int v20X15=0;
external int v20X16=0;
external int v20X17=0;
external int v20X18=0;
external int v20X19=0;

class Main {
    public static void main(String[] args) {
        Tree20X19 t = new Tree20X19();
        int counter = 0;
        for (int i = 0; i < 100000; ++i)
             counter += t.get19(); //warm up

        long startTime = System.nanoTime();
        for (int i = 0; i < 100000; ++i)
             counter += t.get19(); //run
        long endTime = System.nanoTime();
        System.out.println(endTime - startTime);
        System.exit(0);
    }
}

