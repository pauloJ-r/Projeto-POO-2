package poo.heranca.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import poo.heranca.bd.IConnection;
import poo.heranca.logica.Cliente;

public class ClienteLogDAO implements ClienteDAO {
    private IConnection conn;

    public ClienteLogDAO(IConnection conn) {    
        this.conn = conn;
    }

    @Override
    public void salvarCliente(Cliente cliente) {
        // Verificar se o cliente já existe
        if (localizarClientePorCPF(cliente.getCpf()) != null) {
            System.out.println("Erro: Já existe um cliente com o CPF " + cliente.getCpf());
            return;
        }

        String sql = "INSERT INTO Cliente (cpf, nome) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.getConnection().prepareStatement(sql)) {
            stmt.setString(1, cliente.getCpf());
            stmt.setString(2, cliente.getNome());
            stmt.executeUpdate();
            System.out.println("Cliente adicionado com sucesso!");
        } catch (SQLException e) {
            System.out.println("Erro ao adicionar cliente: " + e.getMessage());
            e.printStackTrace();
        }
    }


    @Override
    public Cliente localizarClientePorCPF(String cpf) {
        String sql = "SELECT * FROM Cliente WHERE cpf = ?";
        Cliente cliente = null;
        try (PreparedStatement stmt = conn.getConnection().prepareStatement(sql)) {
            stmt.setString(1, cpf);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String nome = rs.getString("nome");
                cliente = new Cliente(cpf, nome);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cliente;
    }

    @Override
    public void atualizarCliente(Cliente cliente) {
        String sql = "UPDATE Cliente SET nome = ? WHERE cpf = ?";
        try (PreparedStatement stmt = conn.getConnection().prepareStatement(sql)) {
            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getCpf());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removerCliente(String cpf) {
        String sql = "DELETE FROM Cliente WHERE cpf = ?";
        try (PreparedStatement stmt = conn.getConnection().prepareStatement(sql)) {
            stmt.setString(1, cpf);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Cliente> listarClientes() {
        String sql = "SELECT * FROM Cliente";
        List<Cliente> clientes = new ArrayList<>();
        try (Statement stmt = conn.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String cpf = rs.getString("cpf");
                String nome = rs.getString("nome");
                Cliente cliente = new Cliente(cpf, nome);
                clientes.add(cliente);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clientes;
    }
}
