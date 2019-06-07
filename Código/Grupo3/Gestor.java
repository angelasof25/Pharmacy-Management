package Grupo3;

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

public class Gestor extends Agent {
	int num_identificação=0;
	String farmácia;
	int xOrigin;
	int yOrigin;
	public HashMap<Integer, Integer> qtd_por_produto = new HashMap<Integer, Integer>();
	
	private static final long serialVersionUID = 1L;
	@Override
	protected void setup() {
		super.setup();
		Random rand = new Random();
		xOrigin = rand.nextInt(100);
		yOrigin = rand.nextInt(100);
		num_identificação++;
		farmácia=this.getLocalName();
		
		// Registar agente
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setName(farmácia);
		sd.setType("vendaProdutosFarmaceuticos");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
	
		for(int i=1;i<101;i++) {
			int qtd = rand.nextInt(10);
			qtd_por_produto.put(i,qtd);
			}

		this.addBehaviour(new EnviarPosicao());
		this.addBehaviour(new ReceberPedido());
		addBehaviour(new ReporStock(this, 10000));
		}
	
	private class EnviarPosicao extends OneShotBehaviour {
		//private static final long serialVersionUID = 1L;
		@Override
		public void action() {
			System.out.println("Coordenadas do " + getLocalName() + ":" + xOrigin + "," + yOrigin);

			AID receiver = new AID();
			receiver.setLocalName("Interface");
			ACLMessage mensagem = new ACLMessage(ACLMessage.INFORM);
			mensagem.setContent("Coordenadas:" + xOrigin + "," + yOrigin );
			mensagem.addReceiver(receiver);
			myAgent.send(mensagem);		
		}
	}
	
	private class ReceberPedido extends CyclicBehaviour {
		//private static final long serialVersionUID = 1L;
		String [] parts = null;
		String [] parts2 = null;
		String [] parts3 = null;
	
		//@Override
		public void action() {
			ACLMessage msg = receive();
			if (msg != null && msg.getPerformative() == ACLMessage.CFP) {
				parts = msg.getContent().split(",");
				String conteudo = parts[0];
				String cidadao = parts[1];
				String pedido = parts[2];
				int stock = qtd_por_produto.get(Integer.parseInt(conteudo));
				ACLMessage resp = msg.createReply();
				resp.setPerformative(ACLMessage.INFORM);
				if(stock>0) {
					resp.setContent("disponivel" + "," + cidadao + "," + conteudo + "," + pedido);
					}
				else {
					resp.setContent("indisponivel" + "," + cidadao + "," + conteudo);
					}
				myAgent.send(resp);
				}
			else if(msg != null && msg.getPerformative() == ACLMessage.REQUEST) {
				parts2 = msg.getContent().split(",");
				String cidadao = parts2[0];
				String xOrigem = parts2[1];
				String yOrigem = parts2[2];
				String produto = parts2[3];
				String numero = myAgent.getLocalName().replaceAll("[a-z,A-Z]","");
				numero = "Transportador"+numero;

				AID receiver3 = new AID();
				receiver3.setLocalName(numero);
				ACLMessage msg2 = new ACLMessage(ACLMessage.REQUEST);
				msg2.setContent(cidadao+","+xOrigem+","+yOrigem+","+produto);
				msg2.addReceiver(receiver3);
				myAgent.send(msg2);
				int stock = qtd_por_produto.get(Integer.parseInt(produto))-1;
				qtd_por_produto.replace(Integer.parseInt(produto),stock);
				}
			else if(msg != null) {	
				parts3 = msg.getContent().split(",");
				String gestor = parts3[0];
				if(myAgent.getLocalName().equals(gestor)) {
					String produto = parts3[1];
					String quantidade = parts3[2];
					int stock = qtd_por_produto.get(Integer.parseInt(produto));
					qtd_por_produto.replace(Integer.parseInt(produto),stock+Integer.parseInt(quantidade));
					System.out.println("Atualizado o stock do produto " + produto + " passando de " + stock + " para " + stock+Integer.parseInt(quantidade));		
					}
				}
			else {
				block();
				}
		}
	}
	
	public class ReporStock extends TickerBehaviour {
		private static final long serialVersionUID = 1L;
		public ReporStock(Agent a, long period) {
			super(a, period);
		}
		public void onTick() {
			for(int i=1;i<10;i++) {
				try {
					Thread.sleep(1500);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				int stock = qtd_por_produto.get(i);
				if(stock<3) {
					Random rand = new Random();
					int qtd = rand.nextInt(5)+3;
					AID receiver4 = new AID();
					receiver4.setLocalName("Stocker");
					ACLMessage msg4 = new ACLMessage(ACLMessage.REQUEST);
					msg4.setContent(i+","+qtd);
					msg4.addReceiver(receiver4);
					System.out.println(" ");
					//System.out.println("O " + myAgent.getLocalName() + " está a encomendar ao Stocker o Produto" + i);
					send(msg4);
					qtd_por_produto.replace(i,stock+qtd);
					}

				AID receiver5 = new AID();
				receiver5.setLocalName("Interface");
				ACLMessage msg5 = new ACLMessage(ACLMessage.INFORM);
				msg5.setContent("Dados:" + i + "," + qtd_por_produto.get(i));
				msg5.addReceiver(receiver5);
				send(msg5);
				}
		}
	}
		
	protected void takeDown() {
		super.takeDown();
		try {
			DFService.deregister(this);
		}catch(Exception e) {
		}
	}
}
