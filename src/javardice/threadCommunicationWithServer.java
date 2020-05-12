import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.PipedInputStream;
import java.net.*;

public class threadCommunicationWithServer implements Runnable {

    private String ipServidor;
    private int portaServidor;

    private String ip_next_anongw;
    private int identificador_thread;

    private PipedInputStream input;

    private int maxBytes;

    public threadCommunicationWithServer(String ip_servidor,int porta,String ip_next_anongw,int identificador_thread,PipedInputStream in){
        this.ipServidor=ip_servidor;
        this.portaServidor=porta;
        this.ip_next_anongw=ip_next_anongw;
        this.input=in;
        this.maxBytes=1024;
        this.identificador_thread=identificador_thread;
    }

    public void run() {
        try {
            Socket socket = new Socket(ipServidor,portaServidor);

            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            Runnable lepipe_escreve_servidor = () -> {
                try {
                    byte[] bufferINps = new byte[maxBytes];
                    int numBytes1;
                    while ((numBytes1 = input.read(bufferINps, 0, maxBytes)) > 0) {

                        out.write(bufferINps,0,numBytes1);
                        out.flush();
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

                    DatagramSocket socket_udp=new DatagramSocket();

                    SocketAddress sa = new InetSocketAddress(ip_next_anongw,6666);

                    while ((numBytes1 = in.read(bufferINcs, 0, maxBytes)) > 0) {
                        int tamanho_pacote = numBytes1+12;
                        byte[] pacote = new byte[tamanho_pacote];

                        int index = 0;

                        byte[] tipo_pacote = ByteConversion.leIntToByteArray(2);
                        pacote[index++]=tipo_pacote[0];pacote[index++]=tipo_pacote[1];pacote[index++]=tipo_pacote[2];pacote[index++]=tipo_pacote[3];

                        byte[] idthread = ByteConversion.leIntToByteArray(identificador_thread);
                        pacote[index++]=idthread[0];pacote[index++]=idthread[1];pacote[index++]=idthread[2];pacote[index++]=idthread[3];

                        byte[] size_packet = ByteConversion.leIntToByteArray(tamanho_pacote);
                        pacote[index++]=size_packet[0];pacote[index++]=size_packet[1];pacote[index++]=size_packet[2];pacote[index++]=size_packet[3];

                        for(int i=0;i<numBytes1;i++){
                            pacote[index++]=bufferINcs[i];
                        }

                        DatagramPacket pacote_a_enviar = new DatagramPacket(pacote,index,sa);
                        socket_udp.send(pacote_a_enviar);

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
