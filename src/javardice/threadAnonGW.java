import java.io.*;
import java.net.*;
import java.util.*;

public class threadAnonGW implements Runnable {

    private Socket s;

    private String targetServer;
    private int porta;


    private int maxBytes;

    private ArrayList<String> peers;
    private int indice_ultimo;

    //cada thread ao ser iniciada vai ter o seu id
    private int identificador_thread;

    private PipedInputStream pipe_input;

    public threadAnonGW (Socket aa,String t,int p,ArrayList<String> parceiros,int id_th,PipedInputStream pis){
        this.s=aa;
        this.targetServer=t;
        this.porta=p;
        this.maxBytes=65535;
        this.peers=parceiros;
        this.indice_ultimo=0;
        this.identificador_thread = id_th;
        this.pipe_input=pis;
    }

    public synchronized String getProximoPeer(){
        String ret = peers.get(indice_ultimo);
        if(indice_ultimo+1 >= peers.size()){
            indice_ultimo=0;
        }
        else{
            indice_ultimo++;
        }
        return ret;
    }


    public void run() {
        try {

            DataInputStream in = new DataInputStream(s.getInputStream());
            DataOutputStream out = new DataOutputStream(s.getOutputStream());

            DatagramSocket socket_udp=new DatagramSocket(new InetSocketAddress(getProximoPeer(),6666));

            SocketAddress sa = new InetSocketAddress(getProximoPeer(),6666);

            Runnable leCliente_escreveServidor = () -> {
                try {
                    byte[] bufferINcs = new byte[maxBytes];

                    int numBytes1;
                    while ((numBytes1 = in.read(bufferINcs, 0, maxBytes)) > 0) {
                        //System.out.println(new String(bufferINcs));
                        byte[] pacote = new byte[bufferINcs.length+50];

                        int index = 0;

                        byte[] tipo_pacote = ByteConversion.leIntToByteArray(3);
                        pacote[index++]=tipo_pacote[0];pacote[index++]=tipo_pacote[1];pacote[index++]=tipo_pacote[2];pacote[index++]=tipo_pacote[3];

                        byte[] idthread = ByteConversion.leIntToByteArray(identificador_thread);
                        pacote[index++]=idthread[0];pacote[index++]=idthread[1];pacote[index++]=idthread[2];pacote[index++]=idthread[3];

                        String[] partes = targetServer.split(".");
                        for(int i=0;i<partes.length;i++){
                            byte[] num = ByteConversion.leIntToByteArray(Integer.parseInt(partes[i]));
                            //pacote.add(num[0]);pacote.add(num[1]);pacote.add(num[2]);pacote.add(num[3]);
                            pacote[index++]=num[0];pacote[index++]=num[1];pacote[index++]=num[2];pacote[index++]=num[3];
                        }

                        byte[] porta = ByteConversion.leIntToByteArray(this.porta);
                        pacote[index++]=porta[0];pacote[index++]=porta[1];pacote[index++]=porta[2];pacote[index++]=porta[3];

                        for(int i=0;i<bufferINcs.length;i++){
                            pacote[index++]=bufferINcs[i];
                        }

                        DatagramPacket pacote_a_enviar = new DatagramPacket(pacote,index,sa);
                        socket_udp.send(pacote_a_enviar);

                        //encriptar e
                        //out_s.write(bufferINcs, 0, numBytes1);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            };
            Thread t1 = new Thread(leCliente_escreveServidor);
            t1.start();

            t1.join();




            s.shutdownOutput();
            s.shutdownInput();

            s.close();

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
