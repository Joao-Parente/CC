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
        this.maxBytes=16384;
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

            DatagramSocket socket_udp=new DatagramSocket();

            SocketAddress sa = new InetSocketAddress(getProximoPeer(),6666);

            Runnable leCliente_escreveServidor = () -> {
                try {
                    byte[] bufferINcs = new byte[maxBytes];

                    int numBytes1;
                    while ((numBytes1 = in.read(bufferINcs, 0, maxBytes)) > 0) {
                        //System.out.println(new String(bufferINcs));
                        int tamanho_pacote = numBytes1+48;
                        byte[] pacote = new byte[tamanho_pacote];

                        int index = 0;

                        byte[] tipo_pacote = ByteConversion.leIntToByteArray(3);
                        pacote[index++]=tipo_pacote[0];pacote[index++]=tipo_pacote[1];pacote[index++]=tipo_pacote[2];pacote[index++]=tipo_pacote[3];

                        byte[] idthread = ByteConversion.leIntToByteArray(identificador_thread);
                        pacote[index++]=idthread[0];pacote[index++]=idthread[1];pacote[index++]=idthread[2];pacote[index++]=idthread[3];

                        String[] partes = "192.168.1.98".split("[.]");//Endereco ip da maquina a mandar
                        for(int i=0;i<partes.length;i++){
                            byte[] num = ByteConversion.leIntToByteArray(Integer.parseInt(partes[i]));
                            pacote[index++]=num[0];pacote[index++]=num[1];pacote[index++]=num[2];pacote[index++]=num[3];
                        }

                        partes = targetServer.split("[.]");
                        for(int i=0;i<partes.length;i++){
                            byte[] num = ByteConversion.leIntToByteArray(Integer.parseInt(partes[i]));
                            pacote[index++]=num[0];pacote[index++]=num[1];pacote[index++]=num[2];pacote[index++]=num[3];
                        }

                        byte[] porta = ByteConversion.leIntToByteArray(this.porta);
                        pacote[index++]=porta[0];pacote[index++]=porta[1];pacote[index++]=porta[2];pacote[index++]=porta[3];

                        byte[] size_packet = ByteConversion.leIntToByteArray(tamanho_pacote);
                        pacote[index++]=size_packet[0];pacote[index++]=size_packet[1];pacote[index++]=size_packet[2];pacote[index++]=size_packet[3];

                        for(int i=0;i<numBytes1;i++){
                            pacote[index++]=bufferINcs[i];
                            char be = (char) bufferINcs[i];
                            System.out.print(be);
                        }

                        DatagramPacket pacote_a_enviar = new DatagramPacket(pacote,index,sa);
                        socket_udp.send(pacote_a_enviar);

                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            };
            Thread t1 = new Thread(leCliente_escreveServidor);
            t1.start();



            Runnable lePipe_escreveCliente = () -> {
                try {
                    byte[] bufferINcs = new byte[maxBytes];

                    int numBytes1;
                    while ((numBytes1 = pipe_input.read(bufferINcs, 0, maxBytes)) > 0) {

                        out.write(bufferINcs,0,numBytes1);
                        out.flush();

                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            };
            Thread t2 = new Thread(lePipe_escreveCliente);
            t2.start();

            t1.join();
            t2.join();


            s.shutdownOutput();
            s.shutdownInput();

            s.close();

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
