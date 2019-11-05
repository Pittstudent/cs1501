// radix sort class
public class RadixSort
{
	// sorting
	public static void rsort(int[] arr){
		int N = arr.length;	// number of items to be sorted
		int R = 10;		// radix
		int W = 3;		// largest number of digits
		int[] aux = new int[N];	// copy array
        
		// starting at LSD (ones) increment until MSD (hundreds)
		for (int place = 1; place <= Math.pow(R,(W-1)); place *= R)
		{
		    // array for frequency counts (buckets)
		    int[] count = new int[R];

				// for each element of the array update freq count
		    for (int i = 0; i < N; i++)
			count[(arr[i] / place) % R]++;

		    // transform counts to indicies
		    // each element of the array stores the sum of the previous counts
		    for (int i = 1; i < R; i++)
			count[i] += count[i - 1];

		    // distribute
		    // starting at the end of input array, go to count array bucket for digit,
		    // place it in that index of aux, decrease count array
		    for (int i = N - 1; i >= 0; i--){
			aux[--count[(arr[i] / place) % R]] = arr[i];
		    }

		    // copy back to arr
		    for (int i = 0; i < N; i++)
			arr[i] = aux[i];

		    System.out.println(place + "'s place digits:");        
			for (int i = 0; i < N; i++)
				System.out.print(arr[i]+" ");            
			System.out.println();        
		}
	}
	
	// main method
	public static void main(String[] args){
		int[] a = {3, 9, 300, 152, 261, 16, 187, 49};
		int n = a.length;
		
		System.out.println("Array before sort: ");        
		for (int i = 0; i < n; i++)
		    System.out.print(a[i]+" ");            
		System.out.println();         

		rsort(a);

		System.out.println("Array after sort: ");        
		for (int i = 0; i < n; i++)
		    System.out.print(a[i]+" ");            
		System.out.println();
	}
}