granule gTestA(TestA) {
	external int varA;
	{
		return varA == 0;
	}

}
	class TestA within gTestA{
		public void actionA()
		{
			setAX(11);
		}
	}
