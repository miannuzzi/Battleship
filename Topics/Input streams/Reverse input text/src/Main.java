import java.io.BufferedReader;
import java.io.InputStreamReader;

class Main {
    public static void main(String[] args) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        // start coding here



        String output = reader.readLine();


        for (int i = output.length() - 1; i >= 0 ; i--) {
            System.out.print(output.charAt(i));
        }

        reader.close();
    }
}