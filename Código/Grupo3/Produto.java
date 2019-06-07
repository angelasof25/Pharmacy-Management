package Grupo3;

public class Produto {
	private int id;
	private String nome;
	private float preco;
	private int dosagem;
	
	Produto(int id, String nome, float preco, int dosagem){
		this.id = id;
		this.nome = nome;
		this.preco= preco;
		this.dosagem = dosagem;
	}
	public int getid() {
		return id;
	}
	public String getnome() {
		return nome;
	}
	public float getpreco() {
		return preco;
	}
	public int getdosagem() {
		return dosagem;
	}
}
