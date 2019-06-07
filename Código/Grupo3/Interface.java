package Grupo3;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import java.util.*;

public class Interface extends Agent {
	private static final long serialVersionUID = 1L;
	private String agentName;
	private HashMap<Integer, String> Historico;
	private HashMap<Integer,Produto> Produtos;
	private HashMap<String, String> coordenadasCidadao = new HashMap<String, String>();
	private HashMap<String, String> coordenadasGestor = new HashMap<String, String>();
	private HashMap<String, Integer> historicoCidadao = new HashMap<String, Integer>();
	private HashMap<Integer, Integer> historicoProduto = new HashMap<Integer, Integer>();
	int numero_pedido=0;
	
	protected void setup() {
		super.setup();	
		for(int i=1;i<101;i++) {
			historicoProduto.put(i,0);
			}
		for(int j=1;j<11;j++) {
			historicoCidadao.put("Cidadão"+j,0);
			}
		this.addBehaviour(new CalcularDistancia());
		addBehaviour(new CalcularMetricas(this,10000));
		//this.addBehaviour(new Historico());
		criarProd(this.getAID());	
		}
	
	//o hashmap tem um indice que facilita ao cliente para "escolher" o produto
	public HashMap<Integer,Produto> criarProd (AID agents){
		Produtos = new HashMap<>();
		
		Random rand = new Random();
		for (int i=1; i<10; i++) {
			String nome = "Produto" + i;
			float preco = rand.nextInt(4000);
			preco = preco/100;
			int dosagem = rand.nextInt(5)*100+500;
			Produto Prod = new Produto(i,"Produto:" + nome, preco, dosagem);
			Produtos.put(i,Prod);
			//System.out.println(Prod.getnome()+" "+Prod.getpreco()+" "+Prod.getdosagem());
		}
		return Produtos;
	}

	@Override
	protected void takeDown() {
		super.takeDown();
	}	

	public class CalcularDistancia extends CyclicBehaviour {

		private static final long serialVersionUID = 1L;
		private int xOrigem, yOrigem, numGestores, xDestino, yDestino;
		private int gestoresProcessed = 0;
		private AID closestGestor;
		private int minDistancia = 1000;
		String[] coordenadas_cidadao = null;
		String[] coordenadas_gestor = null;
		String[] parts = null;
		String[] parts2 = null;
		String[] parts3 = null;
		ArrayList<String> gestores_disp = new ArrayList<String>();
		
		@Override
		public void action() {
			ACLMessage msg3 = receive();
			if (msg3 != null && msg3.getPerformative() == ACLMessage.INFORM && msg3.getContent().contains("Coordenadas:")) {
				agentName = msg3.getSender().getLocalName();
				String type = agentName.replaceAll("[0-9]","");
				
				if (type.equals("Cidadão")) {
					String posicoes = msg3.getContent();
					coordenadasCidadao.put(agentName, posicoes);
					coordenadas_cidadao = posicoes.split(",");
					//System.out.println("Coordenadas CIDADAO: "+" "+coordenadasCidadao);
					}
				else if(type.equals("Gestor")) {
					String posicoes2 = msg3.getContent();
					coordenadasGestor.put(agentName, posicoes2);
					//System.out.println("Coordenadas GESTOR: "+" "+coordenadasGestor);
					coordenadas_gestor = posicoes2.split(",");
				 }	
			}
			else if (msg3 != null && msg3.getPerformative() == ACLMessage.REQUEST && Produtos.containsKey(Integer.parseInt(msg3.getContent()))) { // receber pedidos de requisição
				numero_pedido++;
				System.out.println("Encomenda do produto " + msg3.getContent()+ " pelo " + msg3.getSender().getLocalName() + " correspondente ao pedido nº " + numero_pedido);
				String cidadao = msg3.getSender().getLocalName();
				String conteudo = msg3.getContent();
				DFAgentDescription dfd = new DFAgentDescription();
	    		ServiceDescription sd =new ServiceDescription();
	    		sd.setType("vendaProdutosFarmaceuticos");
	    		dfd.addServices(sd);
				try {
		    		DFAgentDescription[] results = DFService.search(this.myAgent, dfd);
						if(results.length>0) {
							for (int i = 0; i < results.length; ++i) {
								DFAgentDescription dfd1 = results[i];
								AID receiver = dfd1.getName();
								ACLMessage msg2 = new ACLMessage(ACLMessage.CFP);
								msg2.setContent(conteudo+","+cidadao+","+numero_pedido);
								msg2.addReceiver(receiver);
								//System.out.println("Produto solicitado " +  " " + msg2.getContent());
								myAgent.send(msg2);
							}							
						}									
				} catch (FIPAException fe) {
					fe.printStackTrace();
				}
			}
			else if(msg3 != null && msg3.getPerformative() == ACLMessage.INFORM && msg3.getContent().contains("disponivel") ){
				parts=msg3.getContent().split(",");
				String gestor = msg3.getSender().getLocalName();
				String cidadao = parts[1];
				String coord_cidadao = coordenadasCidadao.get(cidadao).replaceFirst("Coordenadas:","");
				
				parts3=coord_cidadao.split(",");
				xDestino = Integer.parseInt(parts3[0]);
				yDestino = Integer.parseInt(parts3[1]);
				String produto = parts[2];
				gestoresProcessed++;
				
				if (parts[0].equals("disponivel")){
					String coord_gestor = coordenadasGestor.get(gestor).replaceFirst("Coordenadas:","");
					parts2=coord_gestor.split(",");
					xOrigem = Integer.parseInt(parts2[0]);
					yOrigem = Integer.parseInt(parts2[1]);					
					int distance = (int) Math
							.sqrt(((Math.pow((xDestino - xOrigem), 2)) + (Math.pow((yDestino - yOrigem), 2))));
					if (distance < minDistancia) {
						minDistancia = distance;
						closestGestor = msg3.getSender();
						}
					}
				if (gestoresProcessed == 5 && closestGestor!=null) {
					System.out.println("Farmácia Escolhida :" + closestGestor.getLocalName());
					ACLMessage mensagem = new ACLMessage(ACLMessage.REQUEST);
					mensagem.addReceiver(closestGestor);
					mensagem.setContent(cidadao + "," + xDestino + "," + yDestino + "," + produto);
					System.out.println("Enviado o pedido do " + cidadao + " ao " + closestGestor.getLocalName() + " - produto " + produto);
					myAgent.send(mensagem);
					int total = historicoProduto.get(Integer.parseInt(produto));
					int a = Integer.parseInt(produto);
					historicoProduto.replace(a,total+1);
					int total2 = historicoCidadao.get(cidadao);
					historicoCidadao.replace(cidadao,total2+1);
					gestoresProcessed = 0;
					minDistancia = 1000;
					closestGestor = null;
					cidadao = null;
					parts = null;
					}
				}
			else {
				block();
			}
		}
	}
	
	private class CalcularMetricas extends TickerBehaviour {
		private static final long serialVersionUID = 1L;
		public CalcularMetricas(Agent a, long period) {
			super(a, period);
		}
		public void onTick() {
			System.out.println("-------------------------------------------------------------");
			int max = 0;
			int max2 = 0;
			int prod = 0;
			String prod2 = "";
			for(int i=1;i<101;i++) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				int stock = historicoProduto.get(i);
				System.out.println("| Historico por produto - Produto" + i + " - " + stock + " |");
				if(stock>max) {
					max=stock;
					prod=i;
					ACLMessage msgs = new ACLMessage(ACLMessage.INFORM);
					
					AID receiver1 = new AID();
					receiver1.setLocalName("gestor_dados");
					prod2= Integer.toString(i);
					msgs.addReceiver(receiver1);
					msgs.setContent(prod2);
					myAgent.send(msgs);
					}
				}
			System.out.println("Produto mais vendido " + prod + " com um total de " + max);
			for(int j=1;j<11;j++) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				int stock2 = historicoCidadao.get("Cidadão"+j);
				System.out.println("| Historico por Cidadão - Cidadão" + j + " - " + stock2 + " |");
				if(stock2>max2) {
					max2=stock2;
					prod2="Cidadão"+j;	
					}
				}
			System.out.println("Cidadão com mais compras efetuadas " + prod2 + " com um total de " + max2);
			}
		}
	//Histórico que é enviado para o gestor_dados apenas funciona quando cidadão tem apenas um comportamento de pedido de produto 

}

		

