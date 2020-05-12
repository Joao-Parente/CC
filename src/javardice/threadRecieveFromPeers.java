import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
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

        int tipoPacote = ByteConversion.byteArrayToLeInt(Arrays.copyOfRange(a, 0, 4));
        int idThread = ByteConversion.byteArrayToLeInt(Arrays.copyOfRange(a, 4, 8));


        switch (tipoPacote) {
            case 0:
            case 1:
                //pr = new pacote_recebido(tipoPacote, idThread, ip_origem, null, -1, null);
                break;

            case 2:
                //pr = new pacote_recebido(tipoPacote, idThread, ip_origem, null, -1, Arrays.copyOfRange(a, 27, a.length - 1));
                int tamanho_pacote2 = ByteConversion.byteArrayToLeInt(Arrays.copyOfRange(a,8,12));
                if(pipes_escrita_cliente.containsKey(idThread)){
                    try {
                        pipes_escrita_cliente.get(idThread).write(a,12,tamanho_pacote2-12);
                        pipes_escrita_cliente.get(idThread).flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case 3:

                //System.out.println("Entrei no caso 3 e vou enviar para a thread servidor o pacote!");

                String ip_origem = ByteConversion.byteArrayToLeInt(Arrays.copyOfRange(a, 8, 12)) + "." +
                        ByteConversion.byteArrayToLeInt(Arrays.copyOfRange(a, 12, 16)) + "." +
                        ByteConversion.byteArrayToLeInt(Arrays.copyOfRange(a, 16, 20)) + "." +
                        ByteConversion.byteArrayToLeInt(Arrays.copyOfRange(a, 20, 24));

                String ipServidor = ByteConversion.byteArrayToLeInt(Arrays.copyOfRange(a, 24, 28)) + "." +
                        ByteConversion.byteArrayToLeInt(Arrays.copyOfRange(a, 28, 32)) + "." +
                        ByteConversion.byteArrayToLeInt(Arrays.copyOfRange(a, 32, 36)) + "." +
                        ByteConversion.byteArrayToLeInt(Arrays.copyOfRange(a, 36, 40));

                int portaServidor = ByteConversion.byteArrayToLeInt(Arrays.copyOfRange(a, 40, 44));

                int tamanho_pacote = ByteConversion.byteArrayToLeInt(Arrays.copyOfRange(a, 44, 48));

                try{
                    if(!pipes_escrita_comms_servidor.containsKey(idThread+ipServidor+portaServidor)){
                        PipedOutputStream out = new PipedOutputStream();
                        PipedInputStream in = new PipedInputStream(out);
                        pipes_escrita_comms_servidor.put(idThread+ipServidor+portaServidor,out);

                        Thread serverComms = new Thread(new threadCommunicationWithServer(ipServidor,portaServidor,ip_origem,idThread,in));
                        serverComms.start();

                        out.write(a,48,tamanho_pacote-48);
                        out.flush();
                    }
                    else{
                        pipes_escrita_comms_servidor.get(idThread+ipServidor+portaServidor).write(a,48,tamanho_pacote-48);
                        pipes_escrita_comms_servidor.get(idThread+ipServidor+portaServidor).flush();
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
