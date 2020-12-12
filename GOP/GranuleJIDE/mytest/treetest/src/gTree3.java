granule gTree3(Tree3) {
	external int v3;
	{
		return v3 == 0;
	}
}
	class Tree3 within gTree3 {
		public int get3() { return 1000; }
	}