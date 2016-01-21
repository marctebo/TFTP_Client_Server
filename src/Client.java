/**
 * Assignment 1
 * SYSC 3303 Client Class
 * Marc teBoekhorst
 * 100925246
 */
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Scanner;

public class Client {
	
	private DatagramSocket sendReceiveSocket;
	private DatagramPacket sendPacket, receivePacket;
	private String fileName;
	private String mode;
	private byte[] msg;
	
	public Client(){
		try{
			sendReceiveSocket = new DatagramSocket();
		}
		catch (SocketException se){
			se.printStackTrace();
			System.exit(1);
		}
		fileName = "test.txt";
		mode = "neTascII".toLowerCase();
		
		/*Scanner scan = new Scanner(System.in);
		System.out.println("Enter a File Name (with .txt, .jpeg, .xml, etc):");
		fileName = scan.next();
		System.out.println("Enter a Mode (NetASCII or Octet):");
		mode = scan.next().toLowerCase();
		scan.close();*/
		
	}
	/**
	 * Client Algorithm
	 */
	public void clientAct(){
		String request;

		for (int i = 0; i < 11; i++){
			msg = new byte[fileName.length() + mode.length() + 4];
			msg[0] = 0;
			
			if(i == 10){		//11th request is invalid
				msg[1] = 0;
				request = "INVALID";
			}
			else if(i%2 == 0){	//even numbers are read requests
				msg[1] = 1;
				request = "READ";
			}
			else {				//odd numbers are write requests
				msg[1] = 2;
				request = "WRITE";
			}
			
			//Forming the byte array
			byte[] fileNameToBytes = fileName.getBytes();
			int offset1 = fileNameToBytes.length;
			
			System.arraycopy(fileNameToBytes, 0, msg, 2, offset1);	//attaching the file 
			msg[offset1 + 2] = 0;									//name converted to bytes
			
			byte[] modeToBytes = mode.getBytes();		//attaching the file name
			int offset2 = modeToBytes.length;			//converted to bytes
			System.arraycopy(modeToBytes, 0, msg, offset1 + 3, offset2);
			
			int offset3 = offset1 + offset2 + 3;
			msg[offset3] = 0;
	
			//forming packet to be sent to the "server" (intermediate host)
			try{
				sendPacket = new DatagramPacket(msg,msg.length,InetAddress.getLocalHost(),68);
			}
			catch(UnknownHostException e){
				e.printStackTrace();
				System.exit(1);
			}
			
			//information about the packet being sent
			System.out.println("Sending: "+ request + " Request");
			System.out.println("To Host: " + sendPacket.getAddress());
			System.out.println("Destination host port: " + sendPacket.getPort());
			System.out.println("File Name: " + fileName);
			System.out.println("Mode: " + mode);
			
			int len1 = offset3 + 1;
			System.out.println("Length: " + len1);

			String info = new String(msg,0,len1);
			System.out.println("Information as String: " + info);
			System.out.println("Information as Bytes: "+ Arrays.toString(msg));
			
			//sending the packet
			try{
				sendReceiveSocket.send(sendPacket);
			}
			catch(IOException e){
				e.printStackTrace();
				System.exit(1);
			}
			System.out.println("Client sent packet\n");
			
			byte[] data = new byte[4];
			receivePacket = new DatagramPacket(data,data.length);
			
			//receiving packet
			try{
				sendReceiveSocket.receive(receivePacket);
			}
			catch(IOException e){
				e.printStackTrace();
				System.exit(1);
			}
			
			//printing information about the received packet
			System.out.println("Client received a packet");
			System.out.println("From Host" + receivePacket.getAddress()
					+ " with port " + receivePacket.getPort());
			int len2 = receivePacket.getLength();
			System.out.println("Length: " + len2);
			
			System.out.print("Containing: ");
			
			for(int k = 0; k<data.length;k++){
				System.out.print(" " + data[k]);
			}
			
			//String received = new String(data,0,len2);
			System.out.println("\n");
		}
	}
	
	public static void main(String args[]){
		Client c = new Client();
		c.clientAct();
	}
}
