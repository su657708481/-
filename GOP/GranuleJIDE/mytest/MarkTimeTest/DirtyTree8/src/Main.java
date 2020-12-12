
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
        for (int i = 1; i <= 100; ++i)
            v8X0 = i;
        v8X0 = 0;
        System.exit(0);
    }
}

