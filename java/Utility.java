public class Utility {
    public static int[] copy(int[] arr) {
        if (arr == null) return null;
        int[] copy = new int[arr.length];

        System.arraycopy(arr, 0, copy, 0, arr.length);

        return copy;
    }

    public static int[][] copy(int[][] arr) {
        if (arr == null) return null;
        int[][] copy = new int[arr.length][];

        for (int i = 0; i < arr.length; i++) {
            copy[i] = copy(arr[i]);
        }

        return copy;
    }
}
