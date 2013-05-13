/*
@authors: Vladut Madalin Druta
		Antonio Nikolova
		Mark Whelan
*/
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Observable;
import java.util.Observer;

public class Sender implements Runnable {
	private MulticastSocket socket;
	private InetAddress group;
	Mediator mediator;

	public Sender(MulticastSocket socket, InetAddress group,Mediator med){
		this.socket=socket;
		this.group=group;
		mediator=med;
	}
	
	public void run(){
		byte [] message; 
		while(true){
			rest();
			message= mediator.getPreparedPacket();
			System.out.println("THIS IS THE MESSAGE LENGTH: "+message.length);
			DatagramPacket packet = new DatagramPacket(message,message.length,
                    group, 6665);

			try {
				socket.send(packet);
				System.out.println("Packet sent");
				if(packet.getData()[0]==Mediator.DISCONNECT){
					rest();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}	
			
		}
	}
	public MulticastSocket getSocket(){
		return socket;
	}
	public InetAddress getGroup(){
		return group;
	}
	public synchronized void wakeUp(){
		notify();
	}
	public synchronized void rest(){
		try {
			wait();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
