package poo.heranca.dao;

import java.util.List;

import poo.heranca.logica.IConta;

public interface ContaDAO {
	  void criarConta(String cpf, IConta conta);
	    void depositar(String numeroConta, double valor);
	    void sacar(String numeroConta, double valor);
	    String transferir(String contaOrigem, String contaDestino, double valor);
	    String getExtrato(String cpf, String mes, String ano);
	    List<IConta> listarContasPorCliente(String cpf);
	    void removerConta(String cpf, String numeroConta);
	    void consultarSaldo(String numeroConta);
	    double consultarBalanco(String cpf);
}
