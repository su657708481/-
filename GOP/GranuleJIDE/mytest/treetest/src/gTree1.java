granule gTree1(Tree1) {
	external int v1;
	{
		return v1 == 0;
	}
}
	class Tree1 within gTree1 {
		public int get1() { return 1000; }
	}