/**
 * Assignment 1
 * SYSC 3303 Intermediate Host Class
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

public class IntermediateHost {
	
	private DatagramSocket sendSocket, receiveSocket, sendReceiveSocket;
	private DatagramPacket sendPacketServer, sendPacketClient, 
							receivePacketServer, receivePacketClient;
	
	public IntermediateHost(){
		try{
			receiveSocket = new DatagramSocket(68);
			sendReceiveSocket = new DatagramSocket();
		}
		catch(SocketException e){
			e.printStackTrace();
			System.exit(1);
		}
	}
	/**
	 * Intermediate Host Algorithm
	 */
	public void intermediateHostAct(){
		String request = "";
		while(true){
			byte[] msg = new byte[50];
			byte[] data = new byte[4];
			
			//forming receive packet
			receivePacketClient = new DatagramPacket(msg,msg.length);
			System.out.println("Intermediate Host waiting for packet");
			
			//waiting on packet from client
			try{
				System.out.println("Waiting...\n");
				receiveSocket.receive(receivePacketClient);
			}			
			catch(IOException e){
				e.printStackTrace();
				System.exit(1);
			}
			
			System.out.println("Intermediate Host has received a packet");
			
			if(msg[1] == 0){
				request = "INVALID";
			}
			if(msg[1] == 1){
				request = "READ";
			}
			if(msg[1] == 2){
				request = "WRITE";
			}
			
			System.out.println(request + " Request received");
			String fileName = "";
			String mode2 = "";
			String infoBytes = "";
			
			//parsing array
			if(!request.equals("INVALID")){
				byte[] file = new byte[1];
				byte[] mode = new byte[1];
				int count = 0;
				for(int i = 2; i < msg.length; i++){				
					if(msg[i] == 0){
						count++;
						if (count == 1){
							file = Arrays.copyOfRange(msg, 2, i);
						}
						if(count == 2){
							mode = Arrays.copyOfRange(msg, 3 + file.length, i);
							break;
						}
					}	
				}
				//Printing the information of the received packet
				fileName = new String(file);
				System.out.println("File Name: " + fileName);
				
				mode2 = new String(mode);
				System.out.println("Mode: " + mode2);
				
				infoBytes = new String(msg,0,fileName.length() + mode2.length() + 4);
				System.out.println("Information in Bytes: " + infoBytes + "\n");
			}
			
			//forming packet to send to server
			try{
				sendPacketServer = new DatagramPacket(msg,msg.length,
						InetAddress.getLocalHost(),69);
			}
			
			catch(UnknownHostException e){
				e.printStackTrace();
				System.exit(1);
			}
			
			System.out.println("Intermediate Host is sending a packet");
			System.out.println("Sending " + request + " Request");
			
			if(!request.equals("INVALID")){
				System.out.println("File Name: " + fileName);
				System.out.println("Mode: " + mode2);
				System.out.println("Information in Bytes: " + infoBytes + "\n");
			}
			
			//sending the packet to the server
			try{
				sendReceiveSocket.send(sendPacketServer);
			}
			catch(IOException e){
				e.printStackTrace();
				System.exit(1);
			}
			
			//forming packet to receive data from server
			receivePacketServer = new DatagramPacket(data,data.length);
			
			//receiving packet
			try{
				sendReceiveSocket.receive(receivePacketServer);
			}
			catch(IOException e){
				e.printStackTrace();
				System.exit(1);
			}
			
			//printing information received from server
			System.out.println("Intermediate Host received a packet");
			System.out.println("From Server" + receivePacketServer.getAddress()
					+ " with port " + receivePacketServer.getPort());
			
			int len = receivePacketServer.getLength();
			System.out.println("Length: " + len);
			
			System.out.print("Containing: ");
			String received = new String(data,0,len);
			System.out.println(received + "\n");
			
			//forming packet to send to client
			sendPacketClient = new DatagramPacket(data,data.length,
					receivePacketClient.getAddress(),receivePacketClient.getPort());
			
			//forming socket for sending packets to client
			try{
				sendSocket = new DatagramSocket();
			}
			
			catch(SocketException se){
				se.printStackTrace();
				System.exit(1);
			}
			
			//printing information to be sent to client
			System.out.println("Intermediate Host sent packet");
			System.out.println("To Host: " + sendPacketClient.getAddress());
			System.out.println("Destination host port: " + sendPacketClient.getPort());
			String response = new String(data,0,data.length);
			System.out.println("Response Packet: " + response + "\n");
			
			//sending packet to client
		    try {
		        sendSocket.send(sendPacketClient);
		    }
		    catch(IOException e){
		        e.printStackTrace();
		        System.exit(1);
		    }
		}
	}
	
	public static void main(String args[]){
		IntermediateHost i = new IntermediateHost();
		i.intermediateHostAct();
	}
}
