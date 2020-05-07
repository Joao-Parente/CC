import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;

public class threadAnonGW implements Runnable {

    private Socket s;
    private String targetServer;
    private int porta;


    private int maxBytes;

    private ArrayList<String> peers;
    private int indice_ultimo;

    public threadAnonGW (Socket aa,String t,int p,ArrayList<String> pee){
        this.s=aa;
        this.targetServer=t;
        this.porta=p;
        this.maxBytes=10000;
        peers=pee;
        indice_ultimo=0;
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






            Runnable leCliente_escreveServidor = () -> {
                try {
                    byte[] bufferINcs = new byte[maxBytes];
                    int numBytes1;
                    while ((numBytes1 = in.read(bufferINcs, 0, maxBytes)) > 0) {
                        System.out.println(new String(bufferINcs));
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
            //so.shutdownOutput();
            //so.shutdownInput();
            //so.close();
            s.close();

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
