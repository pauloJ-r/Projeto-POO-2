package poo.heranca.dao;

import java.util.List;
import poo.heranca.logica.Cliente;

public interface ClienteDAO {
    void salvarCliente(Cliente cliente);
    Cliente localizarClientePorCPF(String cpf);
    void atualizarCliente(Cliente cliente); 
    void removerCliente(String cpf);
    List<Cliente> listarClientes();
}
