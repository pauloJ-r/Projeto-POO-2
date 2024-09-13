package poo.heranca.logica;

import java.math.BigDecimal;
import java.time.Month;
import java.util.List;

public interface IConta {
	
	void sacar(BigDecimal quantia); 
	
	void depositar(BigDecimal quantia); 
		
	void transferir(BigDecimal quantia, IConta destino); 
	
	void emitirExtrato(Month mes, Integer year); 
	
	boolean isStatus();
	
	public List<Transacao> getTransacoes();
	
	public BigDecimal getSaldo();
	
	public void setSaldo(BigDecimal saldo);
	
	public String getNumero();
	
}
