granule gTree2X(Tree2) {
	external int v2;
	{
		return v2 == 1;
	}
}
	class Tree2 within gTree2X {
		public int get2() { return 1000; }
	}