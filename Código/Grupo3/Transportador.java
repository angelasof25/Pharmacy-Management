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

public class Transportador extends Agent {	
	private static final long serialVersionUID = 1L;
	@Override
	protected void setup() {
		super.setup();

		this.addBehaviour(new EntregarProduto());
		}
		
	private class EntregarProduto extends CyclicBehaviour {
		String [] parts = null;
		public void action() {
			ACLMessage msg = receive();
			if (msg != null && msg.getPerformative() == ACLMessage.REQUEST) {
				parts = msg.getContent().split(",");
				String cidadao = parts[0];
				String xOrigem = parts[1];
				String yOrigem = parts[2];
				String produto = parts[3];

				AID receiver = new AID();
				receiver.setLocalName(cidadao);
				ACLMessage msg2 = new ACLMessage(ACLMessage.INFORM);
				msg2.setContent(cidadao+","+produto);
				msg2.addReceiver(receiver);
				System.out.println("O produto " +  produto + " está a ser entregue ao " + cidadao);
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				myAgent.send(msg2);
				}		
			else {
				block();
				}
			}
		}
}

		
		