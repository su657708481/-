granule gTestB1(TestB) {
	external int varB;
	{
System.out.println(varB);
		return varB == 1;
	}

}
	class TestB within gTestB1 {
		public void actionB()
		{
			setBX(22);
		}
	}
