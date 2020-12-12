granule gTree2(Tree2) {
	external int v2;
	{
		return v2 == 0;
	}
}
	class Tree2 within gTree2 {
		public int get2() { return 1000; }
	}