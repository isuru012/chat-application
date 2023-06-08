/*

   ` Coded By Isuru Dulakshana
   ` Date     6/5/2023 8:49 PM

*/


import java.io.DataInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerSide {
    private static List<HandleClients> clients=new ArrayList<>();

    public static void main(String[] args) {
        final int port=3000;
        try{
            ServerSocket serverSocket=new ServerSocket(port);
            while(true){
                Socket socket=serverSocket.accept();

                DataInputStream dataInputStream=new DataInputStream(socket.getInputStream());
                String name=dataInputStream.readUTF();

                HandleClients handleClients=new HandleClients(socket,name);
                clients.add(handleClients);
                handleClients.start();

            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }


    public static void sendMessageToAll(String name, String messageWithName) {
        System.out.println(messageWithName);
        for (HandleClients handleClients:clients) {
            if (!handleClients.name.equals(name)){
                handleClients.sendMessage(messageWithName);
            }
        }
    }
}
