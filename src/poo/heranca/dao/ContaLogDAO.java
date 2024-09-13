package poo.heranca.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import poo.heranca.bd.IConnection;
import poo.heranca.logica.ContaCorrente;
import poo.heranca.logica.ContaPoupanca;
import poo.heranca.logica.IConta;

public class ContaLogDAO implements ContaDAO {

    private IConnection conn;

    public ContaLogDAO(IConnection conn) {
        this.conn = conn;
    }

    @Override
    public void criarConta(String cpf, IConta conta) {
        String sql = "INSERT INTO Conta (numero_conta, cpf, saldo, tipo_conta) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.getConnection().prepareStatement(sql)) {
            stmt.setString(1, conta.getNumero());
            stmt.setString(2, cpf);
            stmt.setBigDecimal(3, conta.getSaldo());
            // Definindo o tipo da conta com base na instância
            String tipoConta = (conta instanceof ContaPoupanca) ? "POUPANCA" : "CORRENTE";
            stmt.setString(4, tipoConta);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void depositar(String numeroConta, double valor) {
        String sql = "UPDATE Conta SET saldo = saldo + ? WHERE numero_conta = ?";
        try (PreparedStatement stmt = conn.getConnection().prepareStatement(sql)) {
            stmt.setDouble(1, valor);
            stmt.setString(2, numeroConta);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sacar(String numeroConta, double valor) {
        String sql = "UPDATE Conta SET saldo = saldo - ? WHERE numero_conta = ?";
        try (PreparedStatement stmt = conn.getConnection().prepareStatement(sql)) {
            stmt.setDouble(1, valor);
            stmt.setString(2, numeroConta);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void transferir(String contaOrigem, String contaDestino, double valor) {
        try {
            conn.getConnection().setAutoCommit(false);

            // Sacar da conta origem
            sacar(contaOrigem, valor);

            // Depositar na conta destino
            depositar(contaDestino, valor);

            conn.getConnection().commit();
        } catch (SQLException e) {
            try {
                conn.getConnection().rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    @Override
    public String getExtrato(String cpf, String mes, String ano) {
        String sql = "SELECT * FROM Lancamentos WHERE cpf = ? AND mes = ? AND ano = ?";
        StringBuilder extrato = new StringBuilder();
        try (PreparedStatement stmt = conn.getConnection().prepareStatement(sql)) {
            stmt.setString(1, cpf);
            stmt.setString(2, mes);
            stmt.setString(3, ano);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                extrato.append("Lançamento: ")
                        .append(rs.getString("descricao"))
                        .append(" - Valor: ")
                        .append(rs.getBigDecimal("valor"))
                        .append("\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return extrato.toString();
    }

    @Override
    public List<IConta> listarContasPorCliente(String cpf) {
        String sql = "SELECT * FROM Conta WHERE cpf = ?";
        List<IConta> contas = new ArrayList<>();
        try (PreparedStatement stmt = conn.getConnection().prepareStatement(sql)) {
            stmt.setString(1, cpf);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String tipoConta = rs.getString("tipo_conta");
                String numeroConta = rs.getString("numero_conta");
                IConta conta;
                if ("POUPANCA".equalsIgnoreCase(tipoConta)) {
                    conta = new ContaPoupanca(numeroConta);
                } else if ("CORRENTE".equalsIgnoreCase(tipoConta)) {
                    conta = new ContaCorrente(numeroConta);
                } else {
                    throw new SQLException("Tipo de conta desconhecido: " + tipoConta);
                }
                conta.setSaldo(rs.getBigDecimal("saldo"));
                contas.add(conta);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return contas;
    }




    @Override
    public void removerConta(String cpf, String numeroConta) {
        String sql = "DELETE FROM Conta WHERE cpf = ? AND numero_conta = ?";
        try (PreparedStatement stmt = conn.getConnection().prepareStatement(sql)) {
            stmt.setString(1, cpf);
            stmt.setString(2, numeroConta);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public double consultarSaldo(String numeroConta) {
        String sql = "SELECT saldo FROM Conta WHERE numero_conta = ?";
        double saldo = 0;
        try (PreparedStatement stmt = conn.getConnection().prepareStatement(sql)) {
            stmt.setString(1, numeroConta);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                saldo = rs.getDouble("saldo");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return saldo;
    }

    @Override
    public double consultarBalanco(String cpf) {
        String sql = "SELECT SUM(saldo) AS total_saldo FROM Conta WHERE cpf = ?";
        double balanco = 0;
        try (PreparedStatement stmt = conn.getConnection().prepareStatement(sql)) {
            stmt.setString(1, cpf);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                balanco = rs.getDouble("total_saldo");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return balanco;
    }
}
