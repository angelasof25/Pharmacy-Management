
import java.util.Random;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;

/**
 *
 */

/**
 * @author Grupo 3
 */
public class MainContainerTP3 {

	Runtime rt;
	ContainerController container;

	public ContainerController initContainerInPlatform(String host, String port, String containerName) {
		// Get the JADE runtime interface (singleton)
		this.rt = Runtime.instance();

		// Create a Profile, where the launch arguments are stored
		Profile profile = new ProfileImpl();
		profile.setParameter(Profile.CONTAINER_NAME, containerName);
		profile.setParameter(Profile.MAIN_HOST, host);
		profile.setParameter(Profile.MAIN_PORT, port);
		// create a non-main agent container
		ContainerController container = rt.createAgentContainer(profile);
		return container;
	}

	public void initMainContainerInPlatform(String host, String port, String containerName) {

		// Get the JADE runtime interface (singleton)
		this.rt = Runtime.instance();

		// Create a Profile, where the launch arguments are stored
		Profile prof = new ProfileImpl();
		prof.setParameter(Profile.CONTAINER_NAME, containerName);
		prof.setParameter(Profile.MAIN_HOST, host);
		prof.setParameter(Profile.MAIN_PORT, port);
		prof.setParameter(Profile.MAIN, "true");
		prof.setParameter(Profile.GUI, "true");

		// create a main agent container
		this.container = rt.createMainContainer(prof);
		rt.setCloseVM(true);

	}

	public void startAgentInPlatform(String name, String classpath) {
		try {
			AgentController ac = container.createNewAgent(name, classpath, new Object[0]);
			ac.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		MainContainer a = new MainContainer();

		a.initMainContainerInPlatform("localhost", "9888", "MainContainerTP1");

		a.startAgentInPlatform("Interface", "Grupo3.Interface");
		System.out.println("Foi inicializada a Interface");

		
		a.startAgentInPlatform("Stocker", "Grupo3.Stocker");
		System.out.println("Foi inicializado o Stocker");
		
		a.startAgentInPlatform("gestor_dados", "Grupo3.gestor_dados");
		System.out.println("Foi inicializado o Gestor de dados");
		
		// Cria 5 gestores (1 gestor a cada 0,5 segundos) *************
		int limit_gestor = 5; // Limit number of Gestor
		int n;
		for (n = 1; n < limit_gestor + 1; n++) {
			a.startAgentInPlatform("Gestor" + n, "Grupo3.Gestor");
			System.out.println("Criado o Gestor " + n);			
			}
		try {
			Thread.sleep(500);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			}
				
		// Cria 5 transportadores (1 gestor a cada 0,5 segundos) *************
		int limit_transportador = 5; // Limit number of Gestor
		for (n = 1; n < limit_transportador + 1; n++) {
			a.startAgentInPlatform("Transportador" + n, "Grupo3.Transportador");
			System.out.println("Criado o Transportador " + n);
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				}

		// *******************************************************************************************
		// Cria 10 cidadãos (1 cidadão a cada 0,5 segundos) *************
		int limit_cidadao = 10; // Limit number of Cidadão
		for (n = 1; n < limit_cidadao + 1; n++) {
			a.startAgentInPlatform("Cidadão" + n, "Grupo3.Cidadão");
			System.out.println(" ");
			System.out.println("Criado o Cidadão " + n);	
			
		try {
			Thread.sleep(500);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			}
		}
	}
}