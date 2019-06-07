package Grupo3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class Stocker extends Agent {
	private static final long serialVersionUID = 1L;
	@Override
	protected void setup() {
		super.setup();

		this.addBehaviour(new EntregarStock());
		}
		
	public class EntregarStock extends CyclicBehaviour {
		String [] parts = null;
		
		public void action() {
			ACLMessage msg = receive();
			if (msg != null && msg.getPerformative() == ACLMessage.REQUEST) {
				parts = msg.getContent().split(",");
				String gestor = msg.getSender().getLocalName();
				String produto = parts[0];
				String quantidade = parts[1];

				AID receiver = new AID();
				receiver.setLocalName(gestor);
				ACLMessage msg2 = new ACLMessage(ACLMessage.INFORM);
				msg2.addReceiver(receiver);			
				msg2.setContent(gestor + "," + produto + "," + quantidade);
				System.out.println(quantidade + " unidades do produto " +  produto + " são entregues ao " + gestor + " pelo " + myAgent.getLocalName());
				send(msg2);
				
				try {
					Thread.sleep(50000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					}
				}		
			else {
				block();
				}
			}
		}
}

		
		