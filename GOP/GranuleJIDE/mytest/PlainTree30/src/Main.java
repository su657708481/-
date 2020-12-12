
    external int v30X0=0;
external int v30X1=0;
external int v30X2=0;
external int v30X3=0;
external int v30X4=0;
external int v30X5=0;
external int v30X6=0;
external int v30X7=0;
external int v30X8=0;
external int v30X9=0;
external int v30X10=0;
external int v30X11=0;
external int v30X12=0;
external int v30X13=0;
external int v30X14=0;
external int v30X15=0;
external int v30X16=0;
external int v30X17=0;
external int v30X18=0;
external int v30X19=0;
external int v30X20=0;
external int v30X21=0;
external int v30X22=0;
external int v30X23=0;
external int v30X24=0;
external int v30X25=0;
external int v30X26=0;
external int v30X27=0;
external int v30X28=0;
external int v30X29=0;

class Main {
    public static void main(String[] args) {
        Tree30X29 t = new Tree30X29();
        int counter = 0;
        for (int i = 0; i < 100000; ++i)
             counter += t.get29(); //warm up

        long startTime = System.nanoTime();
        for (int i = 0; i < 100000; ++i)
             counter += t.get29(); //run
        long endTime = System.nanoTime();
        System.out.println(endTime - startTime);
        System.exit(0);
    }
}

