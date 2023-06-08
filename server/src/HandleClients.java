/*

   ` Coded By Isuru Dulakshana
   ` Date     6/5/2023 8:52 PM

*/


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class HandleClients extends Thread{
    private Socket socket;
    DataOutputStream dataOutputStream;
    String name;

    public HandleClients(Socket socket, String name) {
        this.socket=socket;
        this.name=name;

    }

    public void run(){
        try {
            dataOutputStream=new DataOutputStream(socket.getOutputStream());
            DataInputStream dataInputStream=new DataInputStream(socket.getInputStream());

            while (true){
                String message=dataInputStream.readUTF();

                String messageWithName=this.name+" :- "+message;

                ServerSide.sendMessageToAll(this.name,messageWithName);


            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
    public void sendMessage(String reply){
        try {
            dataOutputStream.writeUTF(reply);
            dataOutputStream.flush();
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
