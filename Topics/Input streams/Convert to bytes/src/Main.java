import java.io.InputStream;

class Main {
    public static void main(String[] args) throws Exception {
        InputStream inputStream = System.in;

        byte value = (byte) inputStream.read();

        while (value != -1) {
            System.out.print(value);

            value = (byte) inputStream.read();
        }

        inputStream.close();
    }
}