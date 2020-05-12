import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.*;
import java.util.*;

public class main {

    public static void main(String[] args) throws Exception{

        ArrayList<String> peers = new ArrayList<>();
        if(args.length >= 7){
            String target_server=args[2];
            int porta_servidor=Integer.parseInt(args[4]);
            for(int i=6;i<args.length;i++){
                peers.add(args[i]);
            }

            int identificador = 0;

            HashMap<Integer,PipedOutputStream> pipes_threads = new HashMap<>();
            Thread receiver_handler = new Thread(new threadRecieveFromPeers(pipes_threads));
            receiver_handler.start();

            ServerSocket serverSocket = new ServerSocket(porta_servidor);

            while(true){
                Socket s = serverSocket.accept();

                //Acrescentar o novo pipe de escrita
                PipedOutputStream output = new PipedOutputStream();
                pipes_threads.put(identificador,output);

                //Pipe de leitura
                PipedInputStream in = new PipedInputStream(output);
                System.out.println("Aceitei conexao");
                Thread t = new Thread(new threadAnonGW(s,target_server,porta_servidor,peers,identificador,in));
                t.start();
                identificador++;
            }

        }

    }
}
