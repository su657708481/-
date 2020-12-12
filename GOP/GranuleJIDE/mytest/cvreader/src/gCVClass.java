
granule gCVClass(CVClass) {
    external CVTestType cv;
    {
        if (cv.get() == 100) {
            System.out.println("Fitness Test Succeed!");
            return true;
        } else {
            System.out.println("Fitness Test Failed!");
            return false;
        }
    }
}

class CVClass within gCVClass {
    public int get() {
 return 1;
 }
}
