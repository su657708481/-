
    external int v2X0=0;
external int v2X1=0;

class Main {
    public static void main(String[] args) {
        for (int i = 1; i <= 100; ++i)
            v2X0 = i;
        v2X0 = 0;
        System.exit(0);
    }
}

