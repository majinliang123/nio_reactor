import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client {

    public static void main(String[] args) {
        ExecutorService pool = Executors.newFixedThreadPool(18);

        long now = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            pool.submit(() -> {
                try {
                    System.out.println(new Date());
                    Socket socket = new Socket("localhost", 8080);
                    InputStream in = socket.getInputStream();
                    ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
                    ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
                    byte[] buf = new byte[512];
                    int l = 0;
                    while ((l = in.read(buf)) != -1) {
                        byteArrayOut.write(buf, 0, l);
                    }
                    byte[] bytes = byteArrayOut.toByteArray();
                    String str = bytes != null ? new String(bytes) : null;
                    System.out.println(str);
                } catch (Exception e) {

                }

            });

        }
        pool.shutdown();
        Thread t = new Thread() {
            @Override
            public void run() {
                System.out.println(System.currentTimeMillis() - now);
            }
        };
        Runtime.getRuntime().addShutdownHook(t);
    }
}
