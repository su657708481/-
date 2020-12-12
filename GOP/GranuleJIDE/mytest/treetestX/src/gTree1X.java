granule gTree1X(Tree1) {
	external int v1;
	{
		return v1 == 1;
	}
}
	class Tree1 within gTree1X {
		public int get1() { return 1000; }
	}