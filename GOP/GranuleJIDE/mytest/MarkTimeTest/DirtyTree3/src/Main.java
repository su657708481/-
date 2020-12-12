
    external int v3X0=0;
external int v3X1=0;
external int v3X2=0;

class Main {
    public static void main(String[] args) {
        for (int i = 1; i <= 100; ++i)
            v3X0 = i;
        v3X0 = 0;
        System.exit(0);
    }
}

