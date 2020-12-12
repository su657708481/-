granule gTree3X(Tree3) {
	external int v3;
	{
		return v3 == 1;
	}
}
	class Tree3 within gTree3X {
		public int get3() { return 1000; }
	}