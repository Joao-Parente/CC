import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.HashMap;

public class threadRecieveFromPeers implements Runnable {

    private DatagramSocket ds;
    private int maxBytes;

    private HashMap<Integer, PipedOutputStream> pipes_escrita_cliente;

    private HashMap<String, PipedOutputStream> pipes_escrita_comms_servidor;

    public threadRecieveFromPeers(HashMap<Integer, PipedOutputStream> pipesEscrita) {
        this.maxBytes = 32768;
        this.pipes_escrita_cliente = pipesEscrita;
        pipes_escrita_comms_servidor = new HashMap<>();
    }

    public void data(byte[] a) {
        if (a == null)
            return;

        int tipoPacote = ByteConversion.byteArrayToLeInt(Arrays.copyOfRange(a, 0, 3));
        int idThread = ByteConversion.byteArrayToLeInt(Arrays.copyOfRange(a, 4, 7));
        String ip_origem = ByteConversion.byteArrayToLeInt(Arrays.copyOfRange(a, 8, 11)) + "." +
                ByteConversion.byteArrayToLeInt(Arrays.copyOfRange(a, 12, 15)) + "." +
                ByteConversion.byteArrayToLeInt(Arrays.copyOfRange(a, 16, 19)) + "." +
                ByteConversion.byteArrayToLeInt(Arrays.copyOfRange(a, 20, 23));

        switch (tipoPacote) {
            case 0:
            case 1:
                //pr = new pacote_recebido(tipoPacote, idThread, ip_origem, null, -1, null);
                break;

            case 2:
                //pr = new pacote_recebido(tipoPacote, idThread, ip_origem, null, -1, Arrays.copyOfRange(a, 27, a.length - 1));

                if(pipes_escrita_cliente.containsKey(idThread)){
                    try {
                        pipes_escrita_cliente.get(idThread).write(a,24,a.length);
                        pipes_escrita_cliente.get(idThread).flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case 3:
                String ipServidor = ByteConversion.byteArrayToLeInt(Arrays.copyOfRange(a, 27, 30)) + "." +
                        ByteConversion.byteArrayToLeInt(Arrays.copyOfRange(a, 31, 34)) + "." +
                        ByteConversion.byteArrayToLeInt(Arrays.copyOfRange(a, 35, 38)) + "." +
                        ByteConversion.byteArrayToLeInt(Arrays.copyOfRange(a, 39, 42));

                int portaServidor = ByteConversion.byteArrayToLeInt(Arrays.copyOfRange(a, 43, 46));
                try{
                    if(!pipes_escrita_comms_servidor.containsKey(ipServidor+portaServidor)){
                        PipedOutputStream out = new PipedOutputStream();
                        PipedInputStream in = new PipedInputStream(out);
                        pipes_escrita_comms_servidor.put(ipServidor+portaServidor,out);

                        Thread serverComms = new Thread(new threadCommunicationWithServer(ipServidor,portaServidor,ip_origem,in));
                        serverComms.start();

                        out.write(a,47,a.length);
                        out.flush();
                    }
                    else{
                        pipes_escrita_comms_servidor.get(ipServidor+portaServidor).write(a,47,a.length);
                        pipes_escrita_comms_servidor.get(ipServidor+portaServidor).flush();
                    }

                }catch (Exception e){}

                break;

            default:
                break;
        }
        //return pr;
    }

    public void run() {
        try {
            ds = new DatagramSocket(6666);
        } catch (SocketException e) {
            e.printStackTrace();
        }

        byte[] bites = new byte[maxBytes];

        DatagramPacket dp_receber = null;

        while(true){

            dp_receber = new DatagramPacket(bites,maxBytes);

            try {
                ds.receive(dp_receber);
            } catch (IOException e) {
                e.printStackTrace();
            }

            data(bites);

            bites = new byte[maxBytes];


        }

    }
}
