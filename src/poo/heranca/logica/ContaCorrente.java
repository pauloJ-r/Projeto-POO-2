package poo.heranca.logica;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import poo.heranca.banco.util.TaxaUtils;

public class ContaCorrente implements IConta {

	private static final long serialVersionUID = 1L;
	private String numero;
	private BigDecimal saldo;
	private LocalDateTime dataAbertura;
	private boolean status;
	
	//private List<Transacao> transacoes = new ArrayList();
	private List<Transacao> transacoes = new ArrayList<>();
	
	public ContaCorrente (String numero) {
		this.numero = numero;
		this.saldo = new BigDecimal("0");
		this.dataAbertura = LocalDateTime.now();
		this.status = true;
	}

	@Override
	public void sacar(BigDecimal quantia) {
		if(isStatus() && quantia.compareTo(this.getSaldo()) <= 0 ) {
			setSaldo(getSaldo().subtract(quantia));
		    getTransacoes().add(new Transacao(quantia, TipoTransacao.DEBITO, null));
		}else
			System.out.println("Operação inválida");
	
		
	}

	@Override
	public void depositar(BigDecimal quantia) {
		{	if(isStatus() && quantia.compareTo(getSaldo()) >= 0) {
			setSaldo(getSaldo().add(quantia));
			getTransacoes().add(new Transacao(quantia, TipoTransacao.CREDITO, null));
		}else
			System.out.println("Operação Inválida");
		
	}
	}

	@Override
	public void transferir(BigDecimal quantia, IConta destino) {
		{	
			if(this.getSaldo().compareTo(quantia) > 0 && this.isStatus() 
				&& destino.isStatus()) 
		if(destino instanceof ContaCorrente ){
			setSaldo(getSaldo().subtract(quantia));
			getTransacoes().add(new Transacao(quantia, TipoTransacao.TRANSFERENCIA_DEBITO, destino));
			destino.getTransacoes().add(new Transacao(quantia, TipoTransacao.TRANSFERENCIA_CREDITO, destino));
			destino.setSaldo(destino.getSaldo().add(quantia));
		}else if(destino instanceof ContaPoupanca){
			if (this.getSaldo().compareTo(quantia.add(quantia.multiply(BigDecimal.valueOf(0.02)))) > 0) {
				BigDecimal taxaAdministrativa = new BigDecimal(TaxaUtils.TAXA_ADMINISTRATIVA);
				
                setSaldo(getSaldo().subtract(taxaAdministrativa));
                getTransacoes().add(new Transacao(quantia, TipoTransacao.TRANSFERENCIA_DEBITO, destino));
                destino.getTransacoes().add(new Transacao(quantia, TipoTransacao.TRANSFERENCIA_CREDITO, destino));
                destino.setSaldo(destino.getSaldo().add(quantia));
			}
			 	else {
			 		System.out.println("Saldo Insuficiente");
			 	}
		}
		else
			System.out.println("Operação inválida");
	}
		
	}

	@Override
	public void emitirExtrato(Month mes, Integer year) {
		// TODO Auto-generated method stub
		
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public BigDecimal getSaldo() {
		return saldo;
	}

	public void setSaldo(BigDecimal saldo) {
		this.saldo = saldo;
	}

	public LocalDateTime getDataAbertura() {
		return dataAbertura;
	}

	public void setDataAbertura(LocalDateTime dataAbertura) {
		this.dataAbertura = dataAbertura;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public List<Transacao> getTransacoes() {
		return transacoes;
	}

	public void setTransacoes(List<Transacao> transacoes) {
		this.transacoes = transacoes;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public String toString() {
		return "ContaCorrente [numero=" + numero + ", saldo=" + saldo + ", dataAbertura=" + dataAbertura + ", status="
				+ status + "]";
	}

	


}


