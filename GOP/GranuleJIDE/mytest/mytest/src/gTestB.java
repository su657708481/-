granule gTestB(TestB) {
	external int varB;
	{
System.out.println(varB);
		return varB == 0;
	}

}
	class TestB within gTestB {
		public void actionB()
		{
			setBX(21);
		}
	}