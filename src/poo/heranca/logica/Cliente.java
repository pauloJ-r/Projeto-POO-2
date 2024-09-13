package poo.heranca.logica;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Cliente implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String cpf;
	private String nome;
	
	public Cliente() {
    }
	private ArrayList<IConta> contas = new ArrayList<>();

	public Cliente(String cpf, String nome) {
		this.cpf = cpf;
		this.nome = nome;
	}

	public void addConta(IConta c) {
		if(contas.contains(c))
			System.out.println("Elemento ja adicionado");
		else
			this.contas.add(c);
	}
	
	public void remConta(IConta c) {
		if(contas.contains(c))
			this.contas.remove(c);
		else
			System.out.println("Elemento inexistente");
	}
	
	 public IConta localizarConta(String numeroConta) {
	        for (IConta conta : contas) {
	            if (conta.getNumero().equals(numeroConta)) {
	                return conta;
	            }
	        }
	        return null;
	    }
	
	@Override
	public String toString() {
		return "Cliente [cpf=" + cpf + ", nome=" + nome + ", contas=" + contas + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(cpf);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Cliente other = (Cliente) obj;
		return Objects.equals(cpf, other.cpf);
	}
	
	public String getCpf() {
		return cpf;
	}
	
	public String getNome() {
		return nome;
	}
	
	
	public void setCpf(String cpf) {
		this.cpf = cpf;
	}
	
	public void setNome(String nome) {
		this.nome = nome;
	}
	
	 public List<IConta> getContas() {
	        return contas;
	    }
	
	
	
}