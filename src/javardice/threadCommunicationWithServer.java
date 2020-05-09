import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.PipedInputStream;
import java.net.Socket;

public class threadCommunicationWithServer implements Runnable {

    private String ipServidor;
    private int portaServidor;

    private String ip_next_anongw;

    private PipedInputStream input;

    private int maxBytes;

    public threadCommunicationWithServer(String ip_servidor,int porta,String ip_next_anongw,PipedInputStream in){
        this.ipServidor=ip_servidor;
        this.portaServidor=porta;
        this.ip_next_anongw=ip_next_anongw;
        this.input=in;
        this.maxBytes=1024;
    }

    public void run() {
        try {
            Socket socket = new Socket(ipServidor,portaServidor);

            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            Runnable lepipe_escreve_servidor = () -> {
                try {
                    byte[] bufferINcs = new byte[maxBytes];
                    int numBytes1;
                    while ((numBytes1 = input.read(bufferINcs, 0, maxBytes)) > 0) {
                        out.write(bufferINcs,0,numBytes1);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            };
            Thread t1 = new Thread(lepipe_escreve_servidor);
            t1.start();

            Runnable leservidor_escreve_anongw = () -> {
                try {
                    byte[] bufferINcs = new byte[maxBytes];
                    int numBytes1;
                    while ((numBytes1 = in.read(bufferINcs, 0, maxBytes)) > 0) {

                        //fazer um pacote para mandar os bytes

                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            };
            Thread t2 = new Thread(leservidor_escreve_anongw);
            t2.start();


        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
