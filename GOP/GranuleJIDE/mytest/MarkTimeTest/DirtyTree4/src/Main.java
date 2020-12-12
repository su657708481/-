
    external int v4X0=0;
external int v4X1=0;
external int v4X2=0;
external int v4X3=0;

class Main {
    public static void main(String[] args) {
        for (int i = 1; i <= 100; ++i)
            v4X0 = i;
        v4X0 = 0;
        System.exit(0);
    }
}

