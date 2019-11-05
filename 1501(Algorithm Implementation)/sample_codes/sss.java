public class sss{
	static boolean subset(int set[], int sum, int n) {
		boolean[][] subset = new boolean[sum+1][n+1];
		for (int i = 0; i <= n; i++) subset[0][i] = true;
		for (int i = 1; i <= sum; i++) subset[i][0] = false;
		for (int i = 1; i <= sum; i++) {
			for (int j = 1; j <= n; j++) {
				subset[i][j] = subset[i][j-1];
				if (i >= set[j-1])subset[i][j] = subset[i][j] || subset[i - set[j-1]][j-1];
				System.out.print(subset[i][j]+"\t");
			}
			System.out.println();
		}
		return subset[sum][n];
	}

	public static void main(String[] args) {
		int[] set={1,2,3};
		int k=6;
		boolean result=subset(set,k,3);
		System.out.println("result: "+result);
	}

}