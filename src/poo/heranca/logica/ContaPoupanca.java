package poo.heranca.logica;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import poo.heranca.banco.util.TaxaUtils;

public class ContaPoupanca implements IConta {

    private LocalDateTime dataAniversarioPoupanca;
    private static final long serialVersionUID = 1L;
    private String numero;
    private BigDecimal saldo;
    private LocalDateTime dataAbertura;
    private boolean status;
    private List<Transacao> transacoes = new ArrayList<>();
    
    public ContaPoupanca(String numero) {
        this.numero = numero;
        this.saldo = BigDecimal.ZERO;
        this.dataAbertura = LocalDateTime.now();
        this.status = true;
    }

    @Override
    public void sacar(BigDecimal quantia) {
        if (isStatus() && quantia.compareTo(saldo) <= 0) {
            saldo = saldo.subtract(quantia);
            transacoes.add(new Transacao(quantia, TipoTransacao.DEBITO, null));
        } else {
            System.out.println("Operação inválida");
        }
    }

    @Override
    public void depositar(BigDecimal quantia) {
        if (isStatus() && quantia.compareTo(BigDecimal.ZERO) > 0) {
            saldo = saldo.add(quantia);
            transacoes.add(new Transacao(quantia, TipoTransacao.CREDITO, null));
        } else {
            System.out.println("Operação Inválida");
        }
    }

    @Override
    public void transferir(BigDecimal quantia, IConta destino) {
        if (isStatus() && destino.isStatus()) {
            if (destino instanceof ContaPoupanca) {
                if (saldo.compareTo(quantia) >= 0) {
                    saldo = saldo.subtract(quantia);
                    transacoes.add(new Transacao(quantia, TipoTransacao.TRANSFERENCIA_DEBITO, destino));
                    destino.getTransacoes().add(new Transacao(quantia, TipoTransacao.TRANSFERENCIA_CREDITO, destino));
                    destino.setSaldo(destino.getSaldo().add(quantia));
                } else {
                    System.out.println("Saldo Insuficiente");
                }
            } else if (destino instanceof ContaCorrente) {
                BigDecimal taxaAdministrativa = new BigDecimal(TaxaUtils.TAXA_ADMINISTRATIVA);
                if (saldo.compareTo(quantia.add(taxaAdministrativa)) >= 0) {
                    saldo = saldo.subtract(quantia.add(taxaAdministrativa));
                    transacoes.add(new Transacao(quantia, TipoTransacao.TRANSFERENCIA_DEBITO, destino));
                    destino.getTransacoes().add(new Transacao(quantia, TipoTransacao.TRANSFERENCIA_CREDITO, destino));
                    destino.setSaldo(destino.getSaldo().add(quantia));
                } else {
                    System.out.println("Saldo Insuficiente");
                }
            } else {
                System.out.println("Operação inválida");
            }
        }
    }

    @Override
    public void emitirExtrato(Month mes, Integer ano) {
        // Implementação do extrato
    }

    // Getters and Setters

    public LocalDateTime getDataAniversarioPoupanca() {
        return dataAniversarioPoupanca;
    }

    public void setDataAniversarioPoupanca(LocalDateTime dataAniversarioPoupanca) {
        this.dataAniversarioPoupanca = dataAniversarioPoupanca;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
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

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public List<Transacao> getTransacoes() {
        return transacoes;
    }

    public void setTransacoes(List<Transacao> transacoes) {
        this.transacoes = transacoes;
    }

	@Override
	public String toString() {
		return "ContaPoupanca [numero=" + numero + ", saldo=" + saldo + ", dataAbertura=" + dataAbertura + ", status="
				+ status +  "]";
	}
}
