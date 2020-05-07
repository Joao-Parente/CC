import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;

public class main {

    public static void main(String[] args) throws Exception{

        ArrayList<String> peers = new ArrayList<>();
        if(args.length >= 7){
            String target_server=args[2];
            int porta_servidor=Integer.parseInt(args[4]);
            for(int i=6;i<args.length;i++){
                peers.add(args[i]);
            }

            //ServerSocket ss = new ServerSocket();
            ServerSocket serverSocket = new ServerSocket(porta_servidor);

            while(true){
                Socket s = serverSocket.accept();
                Thread t = new Thread(new threadAnonGW(s,target_server,porta_servidor,peers));
                t.start();
            }



        }

/*
        ServerSocket ss = new ServerSocket(80);

        while(true){
            Socket s = ss.accept();
            Thread t = new Thread(new threadAnonGW(s,"127.0.0.1",12345));
            t.start();
        }
*/
    }
}
