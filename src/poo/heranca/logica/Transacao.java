package poo.heranca.logica;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Random;

public class Transacao implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private BigDecimal valor;
	private TipoTransacao tipo;
	private IConta contaDestino;
	private LocalDateTime dataEfetivacao;
	
	public Transacao(BigDecimal valor, TipoTransacao debito, IConta destino) {
		this.id = new Random().nextLong();
		this.valor = valor;
		this.tipo = debito;
		this.contaDestino = (IConta) destino;
		this.dataEfetivacao = LocalDateTime.now();
		
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	public TipoTransacao getTipo() {
		return tipo;
	}

	public void setTipo(TipoTransacao tipo) {
		this.tipo = tipo;
	}

	public IConta getContaDestino() {
		return (IConta) contaDestino;
	}

	public void setContaDestino(IConta contaDestino) {
		this.contaDestino = contaDestino;
	}

	public LocalDateTime getDataEfetivacao() {
		return dataEfetivacao;
	}

	public void setDataEfetivacao(LocalDateTime dataEfetivacao) {
		this.dataEfetivacao = dataEfetivacao;
	}

	@Override
	public String toString() {
		return "Transacao [id=" + id + ", valor=" + valor + ", tipo=" + tipo + ", contaDestino=" + contaDestino
				+ ", dataEfetivacao=" + dataEfetivacao + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Transacao other = (Transacao) obj;
		return Objects.equals(id, other.id);
	}
	
	
	
}
