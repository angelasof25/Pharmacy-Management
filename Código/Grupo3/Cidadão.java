
package Grupo3;

import java.util.Random;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class Cidadão extends Agent {
	
	private static final long serialVersionUID = 1L;		
	private String nome;
	private int cc;
	private String data_nasc;
	private String morada;
	int xDestination;
	int yDestination;
	int cliente;
	
	
	protected void setup() {
		super.setup();
		//start = (Calendar.getInstance()).getTimeInMillis();
		//System.out.println("Starting Cidadão");

		Random rand = new Random();
		nome = this.getLocalName();
		cc = rand.nextInt(1000000000)+100000000;
		data_nasc =(rand.nextInt(28)+1)+"-"+(rand.nextInt(12)+1)+"-"+rand.nextInt(100);
		morada = "morada_"+this.getLocalName();
		cliente = rand.nextInt(5);
		xDestination = rand.nextInt(100);
		yDestination = rand.nextInt(100);
		
		this.addBehaviour(new EnviarPosicao());		
		if (cliente == 0) {
			addBehaviour(new EncomendarProduto1vez());
		}
		if (cliente == 1 || cliente == 2) {
			this.addBehaviour(new EncomendarProdutoigual());
		}
		if (cliente > 2) {
			this.addBehaviour(new EncomendarProdutonvez());
		}
		this.addBehaviour(new ReceberProduto());
	}
	
	private class EnviarPosicao extends SimpleBehaviour {
		//private static final long serialVersionUID = 1L;
		@Override
		public void action() {
			System.out.println("Coordenadas do " + getLocalName() + ":" + xDestination + "," + yDestination);

			AID receiver = new AID();
			receiver.setLocalName("Interface");
			ACLMessage mensagem = new ACLMessage(ACLMessage.INFORM);
			mensagem.setContent( "Coordenadas:" + xDestination + "," + yDestination );
			mensagem.addReceiver(receiver);
			myAgent.send(mensagem);			
		}
		@Override
		public boolean done() {
			return true;
		}
	}
		
	// ***************************** ENCOMENDAR UM PRODUTO APENAS UMA VEZ ****************
	private class EncomendarProduto1vez extends OneShotBehaviour {
		//private static final long serialVersionUID = 1L;

		//@Override
		public void action() {
			Random rand = new Random();
			int id = rand.nextInt(10)+1;
			
			ACLMessage msg1 = new ACLMessage(ACLMessage.REQUEST);
			AID receiver1 = new AID();
			receiver1.setLocalName("Interface");
			msg1.addReceiver(receiver1);
			msg1.setContent(Integer.toString(id));
			//System.out.println("Pedido efetuado " + msg1.getContent());
			myAgent.send(msg1);		
		}
	}

	// ***************************** ENCOMENDAR SEMPRE O MESMO PRODUTO DE X em X SEGUNDOS ****************
	private class EncomendarProdutoigual extends CyclicBehaviour {
		int i = 0;
		int id;
		public void action() {
			if(i==0) {
				Random rand = new Random();
				id = rand.nextInt(10)+1;
			}
			i++;
			
			AID receiver2 = new AID();
			receiver2.setLocalName("Interface");
			ACLMessage msg2 = new ACLMessage(ACLMessage.REQUEST);
			msg2.addReceiver(receiver2);
			msg2.setContent(Integer.toString(id));
			myAgent.send(msg2);
			try {
				Thread.sleep(15000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	// ******************************************* REPETIR ENCOMENDA DE X em X SEGUNDOS ****************
	private class EncomendarProdutonvez extends CyclicBehaviour {
		public void action() {
			Random rand = new Random();
			int id = rand.nextInt(10)+1;

			ACLMessage msg3 = new ACLMessage(ACLMessage.REQUEST);
			AID receiver3 = new AID();
			receiver3.setLocalName("Interface");
			msg3.addReceiver(receiver3);
			msg3.setContent(Integer.toString(id));
			myAgent.send(msg3);
			try {
				Thread.sleep(25000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	
	private class ReceberProduto extends CyclicBehaviour {

		public void action() {
			String [] parts = null;
			ACLMessage msg = receive();
			if (msg != null && msg.getPerformative() == ACLMessage.INFORM) {
				parts = msg.getContent().split(",");
				String cidadao = parts[0];
				String produto = parts[1];
				if(cidadao.equals(myAgent.getLocalName())) {
					System.out.println("O " +  cidadao + " recebeu o produto " + produto);
					}
				else {
					System.out.println("O Produto" +  produto + " não era para o " + cidadao);	
					}
				}		
			else {
				block();
				}
		}
	}
	
}

