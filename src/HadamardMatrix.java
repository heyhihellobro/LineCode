/**
 * Created by Vladimir Rodin.
 * Built on: Thinkpad Workstation W540
 * Date: 19.01.2016
 * Twitter: @heyhihellobro
 */
public class HadamardMatrix {

    HadamardMatrix() {

    }

    public String doHadamarMatrix(int level) {

        int N = level;
        boolean[][] H = new boolean[N][N];

        H[0][0] = true;
        for (int n = 1; n < N; n += n) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    H[i + n][j] = H[i][j];
                    H[i][j + n] = H[i][j];
                    H[i + n][j + n] = !H[i][j];
                }
            }
        }

        StringBuilder text = new StringBuilder();

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (H[i][j]) {
                    text.append("* ");
                    System.out.print("* ");
                }
                else {
                    text.append(". ");
                    System.out.print(". ");
                }
            }
            System.out.println();
            text.append("\n");
        }

        return text.toString();
    }

}
