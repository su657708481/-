granule gTestC(TestC) {
	external S s1;
	{
 System.out.println("s1 value: " + s1.value);
              return s1.value == 100;
	}

}
	class TestC within gTestC {
		public void print() {
  System.out.println("GTESTC");
 }

	}
