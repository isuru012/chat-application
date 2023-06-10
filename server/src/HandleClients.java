/*

   ` Coded By Isuru Dulakshana
   ` Date     6/5/2023 8:52 PM

*/


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;

public class HandleClients extends Thread {
    private Socket socket;
    DataOutputStream dataOutputStream;
    String name;

    public HandleClients(Socket socket, String name) {
        this.socket = socket;
        this.name = name;

    }

    public void run() {
       /* try {
            dataOutputStream=new DataOutputStream(socket.getOutputStream());
            DataInputStream dataInputStream=new DataInputStream(socket.getInputStream());

            while (true){
                String message=dataInputStream.readUTF();

                String messageWithName=this.name+" :- "+message;

                ServerSide.sendMessageToAll(this.name,messageWithName);


            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }*/

        try {
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());


            while (true) {
                /*int messageType = dataInputStream.readInt();
                System.out.println("messagetype"+messageType);*/

                String dataType = dataInputStream.readUTF();

                if (dataType.equals("TEXT")) {
                    // Text message
                    String message = dataInputStream.readUTF();
                    String messageWithName = this.name + " :- " + message;
                    ServerSide.sendMessageToAll(this.name, messageWithName);

                } else if (dataType.equals("IMAGE")) {
                    // Image message
                    /*byte[] sizeAr = new byte[4];
                    dataInputStream.read(sizeAr);
                    int size = ByteBuffer.wrap(sizeAr).asIntBuffer().get();

                    byte[] imageAr = new byte[size];
                    dataInputStream.read(imageAr);

                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imageAr);
                    BufferedImage image = ImageIO.read(byteArrayInputStream);*/

                    // Handle the image data as needed
                    // For example, you can save the image to a file or process it in some way

                    // You can also broadcast the image to all clients

                    String imagePath = dataInputStream.readUTF();

                    ServerSide.broadcastImage(imagePath, this.name);
                }
            }

        } catch (IOException e) {
            // Handle any exceptions or errors that occur during communication
            // For example, you can remove the client from the list of active clients
            // clients.remove(this);
        }
    }


    public void sendMessage(String reply) {
        try {
            //Setting data type to text
            dataOutputStream.writeUTF("TEXT");
            dataOutputStream.flush();


            dataOutputStream.writeUTF(reply);
            dataOutputStream.flush();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void sendImage(String imagePath,String name) {
        System.out.println(imagePath);
        try {

            //Setting data type to image

            dataOutputStream.writeUTF("IMAGE");
            dataOutputStream.flush();

            dataOutputStream.writeUTF(name);
            dataOutputStream.flush();

            //dataOutputStream.writeInt(imageData.length);
            dataOutputStream.writeUTF(imagePath);
            dataOutputStream.flush();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
