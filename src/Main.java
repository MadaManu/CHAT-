/*
@authors: Vladut Madalin Druta
		Antonio Nikolova
		Mark Whelan
*/
import java.net.InetAddress;

public class Main {
	public static void main(String[] args) {
		try{
		InetAddress group = InetAddress.getByName("231.66.66.66");
		 MulticastSocket s = new MulticastSocket(6665);
		 s.joinGroup(group);
		 
		 Gui ui= new Gui(900,400,true);
		 ui.setConnectionInfoLabel(group.getHostAddress() + ":"  + s.getLocalPort());
		 Mediator mediator= new Mediator(ui);
		 ui.addUserName(mediator.getName());
		//mediator.setName("Ton");
		 
		 Sender sender= new Sender(s,group,mediator);
		 Receiver receiver= new Receiver(s,mediator);
		 ui.addObserver(mediator);
		 receiver.setNoise(20);
		 
		 mediator.addSender(sender);
		 Thread ts= new Thread(sender);
		 Thread tr= new Thread(receiver);
		 ts.start();
		 tr.start();
		 
	}catch(Exception e){
		
	}}

}
