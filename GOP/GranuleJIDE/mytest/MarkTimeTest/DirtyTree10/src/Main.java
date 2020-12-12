
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
        for (int i = 1; i <= 100; ++i)
            v10X0 = i;
        v10X0 = 0;
        System.exit(0);
    }
}

