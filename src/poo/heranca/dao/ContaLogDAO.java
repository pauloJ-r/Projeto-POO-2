package poo.heranca.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import poo.heranca.banco.util.TaxaUtils;
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
            System.out.println("Conta criada com sucesso!");
        } catch (SQLException e) {
            // Verificando se o erro é por conta duplicada
            if (e.getErrorCode() == 1062) {
                System.out.println("Erro: O número da conta '" + conta.getNumero() + "' já existe. Não é possível criar contas duplicadas.");
            } else {
                e.printStackTrace();
            }
        }
    }


    public void depositar(String numeroConta, double valor) {
        if (valor <= 0) {
            System.out.println("O valor do depósito deve ser maior que 0.");
            return;
        }

        String atualizarSaldoSQL = "UPDATE Conta SET saldo = saldo + ? WHERE numero_conta = ?";
        String inserirLancamentoSQL = "INSERT INTO Lancamentos (numero_conta, tipo_transacao, valor, mes, ano) VALUES (?, 'DEPOSITO', ?, ?, ?)";

        try (PreparedStatement atualizarStmt = conn.getConnection().prepareStatement(atualizarSaldoSQL);
             PreparedStatement lancamentoStmt = conn.getConnection().prepareStatement(inserirLancamentoSQL)) {

            // Atualiza o saldo da conta
            atualizarStmt.setDouble(1, valor);
            atualizarStmt.setString(2, numeroConta);
            int rowsAffected = atualizarStmt.executeUpdate();

            // Insere o registro de lançamento
            LocalDateTime agora = LocalDateTime.now();
            String mes = String.format("%02d", agora.getMonthValue()); // Mês com dois dígitos
            String ano = String.valueOf(agora.getYear());

            lancamentoStmt.setString(1, numeroConta);
            lancamentoStmt.setDouble(2, valor);
            lancamentoStmt.setString(3, mes);
            lancamentoStmt.setString(4, ano);
            lancamentoStmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Depósito realizado com sucesso!");
            } else {
                System.out.println("Conta não encontrada. Verifique o número da conta.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void sacar(String numeroConta, double valor) {
        if (valor <= 0) {
            System.out.println("O valor do saque deve ser maior que 0.");
            return;
        }

        String verificarSaldoSQL = "SELECT saldo FROM Conta WHERE numero_conta = ?";
        String atualizarSaldoSQL = "UPDATE Conta SET saldo = saldo - ? WHERE numero_conta = ?";
        String inserirLancamentoSQL = "INSERT INTO Lancamentos (numero_conta, tipo_transacao, valor, mes, ano) VALUES (?, 'SAQUE', ?, ?, ?)";

        try (PreparedStatement verificarStmt = conn.getConnection().prepareStatement(verificarSaldoSQL);
             PreparedStatement atualizarStmt = conn.getConnection().prepareStatement(atualizarSaldoSQL);
             PreparedStatement lancamentoStmt = conn.getConnection().prepareStatement(inserirLancamentoSQL)) {

            // Verifica o saldo da conta
            verificarStmt.setString(1, numeroConta);
            ResultSet rs = verificarStmt.executeQuery();

            if (rs.next()) {
                double saldoAtual = rs.getDouble("saldo");

                if (saldoAtual >= valor) {
                    // Atualiza o saldo da conta
                    atualizarStmt.setDouble(1, valor);
                    atualizarStmt.setString(2, numeroConta);
                    atualizarStmt.executeUpdate();

                    // Insere o registro de lançamento
                    LocalDateTime agora = LocalDateTime.now();
                    String mes = String.format("%02d", agora.getMonthValue());
                    String ano = String.valueOf(agora.getYear());

                    lancamentoStmt.setString(1, numeroConta);
                    lancamentoStmt.setDouble(2, valor);
                    lancamentoStmt.setString(3, mes);
                    lancamentoStmt.setString(4, ano);
                    lancamentoStmt.executeUpdate();

                    System.out.println("Saque realizado com sucesso!");
                } else {
                    System.out.println("Saldo insuficiente para realizar o saque.");
                }
            } else {
                System.out.println("Conta não encontrada.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public String transferir(String contaOrigem, String contaDestino, double valor) {
        if (valor <= 0) {
            return "O valor da transferência deve ser maior que 0.";
        }

        // Definir as SQLs para atualizar saldos e inserir lançamentos
        String atualizarSaldoOrigemSQL = "UPDATE Conta SET saldo = saldo - ? WHERE numero_conta = ?";
        String atualizarSaldoDestinoSQL = "UPDATE Conta SET saldo = saldo + ? WHERE numero_conta = ?";
        String inserirLancamentoOrigemSQL = "INSERT INTO Lancamentos (numero_conta, tipo_transacao, valor, mes, ano) VALUES (?, 'TRANSFERENCIA_DEBITO', ?, ?, ?)";
        String inserirLancamentoDestinoSQL = "INSERT INTO Lancamentos (numero_conta, tipo_transacao, valor, mes, ano) VALUES (?, 'TRANSFERENCIA_CREDITO', ?, ?, ?)";

        Connection connection = null;
        try {
            connection = conn.getConnection();
            connection.setAutoCommit(false);

            // Verifica o tipo das contas de origem e destino
            String tipoContaOrigem = obterTipoConta(connection, contaOrigem);
            String tipoContaDestino = obterTipoConta(connection, contaDestino);

            // Calcula o valor total a ser descontado, incluindo taxa se necessário
            double valorTotal = valor;
            if (!tipoContaOrigem.equals(tipoContaDestino)) {
                BigDecimal taxaAdministrativa = new BigDecimal(TaxaUtils.TAXA_ADMINISTRATIVA);
                valorTotal += taxaAdministrativa.doubleValue();
            }

            // Verifica se a conta origem tem saldo suficiente
            double saldoAtualOrigem = verificarSaldo(connection, contaOrigem);
            if (saldoAtualOrigem < valorTotal) {
                connection.rollback();
                return "Saldo insuficiente para realizar a transferência.";
            }

            // Atualiza o saldo da conta origem
            try (PreparedStatement atualizarSaldoOrigemStmt = connection.prepareStatement(atualizarSaldoOrigemSQL);
                 PreparedStatement lancamentoOrigemStmt = connection.prepareStatement(inserirLancamentoOrigemSQL)) {

                atualizarSaldoOrigemStmt.setDouble(1, valorTotal);
                atualizarSaldoOrigemStmt.setString(2, contaOrigem);
                atualizarSaldoOrigemStmt.executeUpdate();

                // Insere o registro de lançamento na conta origem
                LocalDateTime agora = LocalDateTime.now();
                String mes = String.format("%02d", agora.getMonthValue());
                String ano = String.valueOf(agora.getYear());

                lancamentoOrigemStmt.setString(1, contaOrigem);
                lancamentoOrigemStmt.setDouble(2, valor);
                lancamentoOrigemStmt.setString(3, mes);
                lancamentoOrigemStmt.setString(4, ano);
                lancamentoOrigemStmt.executeUpdate();
            }

            // Atualiza o saldo da conta destino
            try (PreparedStatement atualizarSaldoDestinoStmt = connection.prepareStatement(atualizarSaldoDestinoSQL);
                 PreparedStatement lancamentoDestinoStmt = connection.prepareStatement(inserirLancamentoDestinoSQL)) {

                atualizarSaldoDestinoStmt.setDouble(1, valor);
                atualizarSaldoDestinoStmt.setString(2, contaDestino);
                atualizarSaldoDestinoStmt.executeUpdate();

                // Insere o registro de lançamento na conta destino
                LocalDateTime agora = LocalDateTime.now();
                String mes = String.format("%02d", agora.getMonthValue());
                String ano = String.valueOf(agora.getYear());

                lancamentoDestinoStmt.setString(1, contaDestino);
                lancamentoDestinoStmt.setDouble(2, valor);
                lancamentoDestinoStmt.setString(3, mes);
                lancamentoDestinoStmt.setString(4, ano);
                lancamentoDestinoStmt.executeUpdate();
            }

            connection.commit();
            return "Transferência realizada com sucesso!";

        } catch (SQLException e) {
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
            return "Erro ao realizar a transferência: " + e.getMessage();
        } finally {
            try {
                if (connection != null) {
                    connection.setAutoCommit(true);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private String obterTipoConta(Connection connection, String numeroConta) throws SQLException {
        String sql = "SELECT tipo_conta FROM Conta WHERE numero_conta = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, numeroConta);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("tipo_conta");
            } else {
                throw new SQLException("Conta não encontrada.");
            }
        }
    }

    private double verificarSaldo(Connection connection, String numeroConta) throws SQLException {
        String sql = "SELECT saldo FROM Conta WHERE numero_conta = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, numeroConta);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("saldo");
            } else {
                throw new SQLException("Conta não encontrada.");
            }
        }
    }


    @Override
    public String getExtrato(String numeroConta, String mesStr, String anoStr) {
        // Ajusta o nome da tabela e das colunas conforme o esquema do banco de dados
        String sql = "SELECT * FROM Lancamentos WHERE numero_conta = ? AND mes = ? AND ano = ?";
        StringBuilder extrato = new StringBuilder();

        try {
            // Converte anoStr para inteiro
            int ano = Integer.parseInt(anoStr);

            try (PreparedStatement stmt = conn.getConnection().prepareStatement(sql)) {
                stmt.setString(1, numeroConta);
                stmt.setString(2, mesStr); // Mes como VARCHAR
                stmt.setInt(3, ano); // Ano como inteiro
                ResultSet rs = stmt.executeQuery();

                // Adiciona informações das transações
                while (rs.next()) {
                    LocalDate dataEfetivacao = rs.getTimestamp("data_transacao").toLocalDateTime().toLocalDate();
                    String tipoTransacao = rs.getString("tipo_transacao");
                    BigDecimal valor = rs.getBigDecimal("valor");

                    extrato.append(String.format("Data: %s | Tipo: %s | Valor: %s%n",
                            dataEfetivacao, tipoTransacao, valor.toPlainString()));
                }
            }
        } catch (NumberFormatException e) {
            extrato.append("Formato de ano inválido. Por favor, insira um número válido para o ano.");
        } catch (SQLException e) {
            e.printStackTrace();
            extrato.append("Erro ao consultar o extrato. Verifique os detalhes e tente novamente.");
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




    public void removerConta(String cpf, String numeroConta) {
        String sql = "DELETE FROM Conta WHERE cpf = ? AND numero_conta = ?";
        try (PreparedStatement stmt = conn.getConnection().prepareStatement(sql)) {
            stmt.setString(1, cpf);
            stmt.setString(2, numeroConta);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Conta removida com sucesso.");
            } else {
                System.out.println("Nenhuma conta encontrada para o CPF e número de conta fornecidos.");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao remover a conta: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void consultarSaldo(String numeroConta) {
        String sql = "SELECT saldo FROM Conta WHERE numero_conta = ?";
        try (PreparedStatement stmt = conn.getConnection().prepareStatement(sql)) {
            stmt.setString(1, numeroConta);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                double saldo = rs.getDouble("saldo");
                System.out.println("Saldo: R$ " + saldo);
            } else {
                System.out.println("Conta não encontrada para o número de conta fornecido.");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao consultar o saldo: " + e.getMessage());
            e.printStackTrace();
        }
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
                if (balanco == 0) {
                    System.out.println("Nenhum saldo encontrado para o CPF fornecido.");
                } else {
                    System.out.println("Balanço total: R$ " + balanco);
                }
            } else {
                System.out.println("Nenhum saldo encontrado para o CPF fornecido.");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao consultar o balanço: " + e.getMessage());
            e.printStackTrace();
        }
        return balanco;
    }
}
