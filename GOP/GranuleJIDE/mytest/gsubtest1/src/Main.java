external int Subtestcv0 = 1;
class Main {
    public static void main(String[] args) {
        Subtest s = new Subtest();

        try {
    	  Subtestcv0 = 0;
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace(); 
        }

        System.out.println(s.get());
    }
}

