package poo.heranca.app;

import java.util.List;
import java.util.Scanner;

import poo.heranca.logica.Cliente;
import poo.heranca.logica.ContaCorrente;
import poo.heranca.logica.ContaPoupanca;
import poo.heranca.logica.IConta;
import poo.heranca.dao.ClienteDAO;
import poo.heranca.dao.ClienteLogDAO;
import poo.heranca.dao.ContaDAO;
import poo.heranca.dao.ContaLogDAO;
import poo.heranca.bd.IConnection;
import poo.heranca.bd.ConnectionMySQL;

public class Aplicacao {

    public static void main(String[] args) {
        // Conexão com o banco de dados
        IConnection conexao = new ConnectionMySQL();
        ClienteDAO clienteDAO = new ClienteLogDAO(conexao);
        ContaDAO contaDAO = new ContaLogDAO(conexao);

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n===== Menu de Opções =====");
            System.out.println("1. Adicionar Cliente");
            System.out.println("2. Listar Clientes");
            System.out.println("3. Consultar Cliente por CPF");
            System.out.println("4. Atualizar Cliente");
            System.out.println("5. Remover Cliente");
            System.out.println("6. Criar Conta Poupança");
            System.out.println("7. Criar Conta Corrente");
            System.out.println("8. Depositar");
            System.out.println("9. Sacar");
            System.out.println("10. Transferir");
            System.out.println("11. Consultar Saldo");
            System.out.println("12. Consultar Balanço");
            System.out.println("13. Consultar Extrato");
            System.out.println("14. Listar Contas por Cliente");
            System.out.println("15. Remover Conta");
            System.out.println("16. Sair");
            System.out.print("Escolha uma opção: ");

            int opcao = scanner.nextInt();
            scanner.nextLine(); // Limpa o buffer

            switch (opcao) {
            case 1:
                // Adicionar Cliente
                System.out.print("Digite o CPF do cliente: ");
                String cpfCliente = scanner.nextLine();
                System.out.print("Digite o nome do cliente: ");
                String nomeCliente = scanner.nextLine();
                Cliente cliente = new Cliente(cpfCliente, nomeCliente);

                // Verificar se o cliente já existe
                if (clienteDAO.localizarClientePorCPF(cpfCliente) != null) {
                    System.out.println("Erro: Já existe um cliente com o CPF " + cpfCliente);
                } else {
                    clienteDAO.salvarCliente(cliente);
                }
                break;

                case 2:
                    // Listar Clientes
                    List<Cliente> clientes = clienteDAO.listarClientes();
                    for (Cliente c : clientes) {
                        System.out.println("CPF: " + c.getCpf() + ", Nome: " + c.getNome());
                    }
                    break;

                case 3:
                    // Consultar Cliente por CPF
                    System.out.print("Digite o CPF do cliente: ");
                    cpfCliente = scanner.nextLine();
                    Cliente clienteEncontrado = clienteDAO.localizarClientePorCPF(cpfCliente);
                    if (clienteEncontrado != null) {
                        System.out.println("Cliente encontrado - CPF: " + clienteEncontrado.getCpf() + ", Nome: " + clienteEncontrado.getNome());
                    } else {
                        System.out.println("Cliente não encontrado.");
                    }
                    break;

                case 4:
                    // Atualizar Cliente
                    System.out.print("Digite o CPF do cliente a ser atualizado: ");
                    cpfCliente = scanner.nextLine();
                    Cliente clienteParaAtualizar = clienteDAO.localizarClientePorCPF(cpfCliente);
                    if (clienteParaAtualizar != null) {
                        System.out.print("Digite o novo nome do cliente: ");
                        String novoNome = scanner.nextLine();
                        clienteParaAtualizar.setNome(novoNome);
                        clienteDAO.atualizarCliente(clienteParaAtualizar);
                        System.out.println("Cliente atualizado com sucesso!");
                    } else {
                        System.out.println("Cliente não encontrado.");
                    }
                    break;

                case 5:
                	 // Remover Cliente
                    System.out.print("Digite o CPF do cliente a ser removido: ");
                    String cpfCliente1 = scanner.nextLine();

                    // Verificar se o cliente existe
                    Cliente clienteExistente = clienteDAO.localizarClientePorCPF(cpfCliente1);
                    if (clienteExistente == null) {
                        System.out.println("Erro: Cliente com o CPF " + cpfCliente1 + " não encontrado.");
                        break; 
                    }

                    // Se o cliente existir, tentar removê-lo
                    clienteDAO.removerCliente(cpfCliente1);
                    System.out.println("Cliente removido com sucesso!");
                    break;

                case 6:
                    // Criar Conta Poupança
                    System.out.print("Digite o CPF do cliente: ");
                    cpfCliente = scanner.nextLine();
                    System.out.print("Digite o número da conta poupança: ");
                    String numContaPoupanca = scanner.nextLine();
                    
                    IConta contaPoupanca = new ContaPoupanca(numContaPoupanca);
                    contaDAO.criarConta(cpfCliente, contaPoupanca);
                    
                    break;

                case 7:
                    // Criar Conta Corrente
                    System.out.print("Digite o CPF do cliente: ");
                    cpfCliente = scanner.nextLine();
                    System.out.print("Digite o número da conta corrente: ");
                    String numContaCorrente = scanner.nextLine();
                    
                    IConta contaCorrente = new ContaCorrente(numContaCorrente);
                    contaDAO.criarConta(cpfCliente, contaCorrente);
                    
                 
                    break;

                case 8:
                    // Depositar
                    System.out.print("Digite o número da conta para depósito: ");
                    String numeroContaDeposito = scanner.nextLine();
                    System.out.print("Digite o valor para depósito: ");
                    double valorDeposito = scanner.nextDouble();

                    // Realizar o depósito diretamente, deixando o tratamento para o DAO
                    contaDAO.depositar(numeroContaDeposito, valorDeposito);

                    break;


                case 9:
                    // Sacar
                    System.out.print("Digite o número da conta para saque: ");
                    String numeroContaSaque = scanner.nextLine();
                    System.out.print("Digite o valor para saque: ");
                    double valorSaque = scanner.nextDouble();
                    contaDAO.sacar(numeroContaSaque, valorSaque);  
                    break;


                case 10:
                    // Transferir
                    System.out.print("Digite o número da conta de origem: ");
                    String contaOrigem = scanner.nextLine();
                    System.out.print("Digite o número da conta de destino: ");
                    String contaDestino = scanner.nextLine();
                    System.out.print("Digite o valor para transferência: ");
                    double valorTransferencia = scanner.nextDouble();
                    scanner.nextLine(); // Consumir a nova linha após o double

                    // Chama o método transferir e captura a mensagem
                    String resultadoTransferencia = contaDAO.transferir(contaOrigem, contaDestino, valorTransferencia);
                    System.out.println(resultadoTransferencia); // Exibe a mensagem de sucesso ou erro
                    break;


                case 11:
                    // Consultar Saldo
                    System.out.print("Digite o número da conta para consultar saldo: ");
                    String numeroContaSaldo = scanner.nextLine();
                    // Chama o método de consulta de saldo, que deve lidar com mensagens
                    contaDAO.consultarSaldo(numeroContaSaldo);
                    break;



                case 12:
                    // Consultar Balanço
                    System.out.print("Digite o CPF para consultar balanço: ");
                    String cpfCliente2 = scanner.nextLine();
                    // Chama o método de consulta de balanço, que deve lidar com mensagens
                    contaDAO.consultarBalanco(cpfCliente2);
                    break;


                case 13:
                    // Consultar Extrato
                    System.out.print("Digite o número da conta: ");
                    String numeroConta = scanner.nextLine();
                    System.out.print("Digite o mês (MM): ");
                    String mes = scanner.nextLine();
                    System.out.print("Digite o ano (YYYY): ");
                    String ano = scanner.nextLine();
                    
                    // Obtendo o extrato
                    String extrato = contaDAO.getExtrato(numeroConta, mes, ano);
                    
                    // Imprimindo o extrato
                    if (extrato.isEmpty()) {
                        System.out.println("Nenhum lançamento encontrado para o período especificado.");
                    } else {
                        System.out.println("Extrato da conta " + numeroConta + ": \n" + extrato);
                    }
                    break;


                case 14:
                    // Listar Contas por Cliente
                	System.out.print("Digite o CPF do cliente: ");
                    String cpfCliente11 = scanner.nextLine();
                    List<IConta> contas = contaDAO.listarContasPorCliente(cpfCliente11);
                    if (contas.isEmpty()) {
                        System.out.println("O cliente com CPF " + cpfCliente11 + " não possui contas.");
                    } else {
                        System.out.println("Contas do cliente com CPF " + cpfCliente11 + ":");
                        for (IConta conta : contas) {
                            String tipo = (conta instanceof ContaPoupanca) ? "Poupança" : "Corrente";
                            System.out.println("Número da conta: " + conta.getNumero() + ", Tipo: " + tipo + ", Saldo: R$ " + conta.getSaldo());
                        }
                    }
                    break;
                case 15:
                    // Remover Conta
                    System.out.print("Digite o CPF do cliente: ");
                    cpfCliente11 = scanner.nextLine();
                    System.out.print("Digite o número da conta para remover: ");
                    String numeroContaRemover = scanner.nextLine();
                    contaDAO.removerConta(cpfCliente11, numeroContaRemover);
                    break;

                case 16:
                    // Sair
                    System.out.println("Saindo...");
                    scanner.close();
                    return;

                default:
                    System.out.println("Opção inválida! Tente novamente.");
                    break;
            }
        }
    }
}
