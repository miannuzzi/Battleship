import java.io.BufferedReader;
import java.io.InputStreamReader;

class Main {
    public static void main(String[] args) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        // start coding here
        boolean isFirst = true;
        int value = reader.read();
        int wordCount = 0;
        int previous = 0;

        while (value != -1) {
            if ((char)value == ' ' && !isFirst && previous != ' ') {
                wordCount++;
            }
            isFirst = false;
            previous = value;

            value = reader.read();
        }

        if (previous != ' ') {
            wordCount++;
        }
        System.out.println(wordCount);
        reader.close();
    }
}