package Grupo3;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;



public class gestor_dados extends Agent {
	private static final long serialVersionUID = 1L;
	int occurences[] = new int[100];
	int w = 0;
	int top3[] = new int[3];
	int max = 0;
	
	protected void setup() {
		super.setup();
		for(int i = 0; i<3; i++) top3[i] = 0;
		this.addBehaviour(new PiechartDemo());
	}

	private class PiechartDemo extends CyclicBehaviour{
		private static final long serialVersionUID = 1L;
		String parts = null;
	
		private int maxI(List<Integer> arr) {
			int mi = 0;
			int maxNum = (int) arr.get(0); // get the first number in array
			for (int i = 1; i < arr.size(); i++) {
			    if ( (int) arr.get(i) > maxNum) {
			      maxNum = (int) arr.get(i);
			      mi = i;
			    }
			}
			arr.set(mi, 0);
			return mi;
		}
		
		public void action() {
			ACLMessage msg = receive();
			if (msg != null && msg.getPerformative() == ACLMessage.INFORM) {
				parts = msg.getContent();
				int value = Integer.parseInt(parts);
				occurences[value]++;
				
			DefaultPieDataset pie = new DefaultPieDataset();
			
			
			for (int i=1;i<10;i++) {
				pie.setValue("Produto"+i, occurences[i]);
				JFreeChart chart= ChartFactory.createPieChart(
						"Produtos Mais Vendidos", pie, true, true, false);
				try { 
					ChartUtilities.saveChartAsJPEG(new File("D:/Users/Desktop/Sistemas Inteligentes/grafico.jpeg"),
					chart,	500, 500	
					);
			
				}catch(Exception e) {
					System.err.println("error"+e);
					}
				List<Integer> max3 = new ArrayList<Integer>();
				for(int j = 0; j < occurences.length; j++)
					max3.add(occurences[j]);

				for(int j = 0; j < 3; j++)
					top3[j] = maxI(max3);
				
				System.err.println("3 Produtos Mais Vendidos: " + Arrays.toString(top3));
			   
				
			}
				}

			}
		 }
}

	

