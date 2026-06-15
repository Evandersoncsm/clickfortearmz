package com.app.and.integracao;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/** Fonte unica das telas sugeridas e dos contratos Omie usados pelos prototipos. */
@Component
public class OmieTelaCatalogo {

    private static final List<OmieTela> TELAS = List.of(
            tela(
                    "produtos", "Cadastros", "Produtos",
                    "Pesquisa e manutenção do catálogo de produtos usado no estoque, compras, vendas e produção.",
                    "https://app.omie.com.br/api/v1/geral/produtos/", "ListarProdutos", "Alta",
                    List.of("Código ou SKU", "Descrição", "Família", "Inativo", "Página"),
                    List.of("Código", "Descrição", "Unidade", "NCM", "EAN", "Valor unitário"),
                    List.of("Consultar", "Incluir", "Alterar", "Atualizar ou incluir")),
            tela(
                    "estoque", "Estoque", "Posição de estoque",
                    "Consulta da posição dos produtos por data e local de estoque.",
                    "https://app.omie.com.br/api/v1/estoque/consulta/", "ListarPosEstoque", "Alta",
                    List.of("Data da posição", "Local de estoque", "Código do produto", "Tipo do item", "Página"),
                    List.of("Código", "Produto", "Local", "Quantidade", "Data da posição"),
                    List.of("Consultar posição", "Exportar resultado")),
            tela(
                    "movimentos-estoque", "Estoque", "Movimentos de estoque",
                    "Entradas e saídas de produtos em um período, com filtro por local.",
                    "https://app.omie.com.br/api/v1/estoque/movestoque/", "ListarMovimentos", "Alta",
                    List.of("Data inicial", "Data final", "Local de estoque", "Código do produto", "Página"),
                    List.of("Data", "Produto", "Local", "Entrada", "Saída", "Saldo"),
                    List.of("Listar movimentos", "Consultar previsão", "Exportar resultado")),
            tela(
                    "locais-estoque", "Estoque", "Locais de estoque",
                    "Cadastro dos depósitos e locais usados nas consultas e movimentações.",
                    "https://app.omie.com.br/api/v1/estoque/local/", "ListarLocaisEstoque", "Média",
                    List.of("Código", "Descrição", "Tipo", "Ativo", "Página"),
                    List.of("ID Omie", "Código", "Descrição", "Tipo", "Padrão", "Ativo"),
                    List.of("Consultar", "Incluir", "Alterar")),
            tela(
                    "pedidos-compra", "Compras", "Pedidos de compra",
                    "Acompanhamento dos pedidos enviados aos fornecedores e suas previsões.",
                    "https://app.omie.com.br/api/v1/produtos/pedidocompra/", "PesquisarPedCompra", "Alta",
                    List.of("Número", "Fornecedor", "Data inicial", "Data final", "Etapa", "Página"),
                    List.of("Número", "Fornecedor", "Previsão", "Etapa", "Situação", "Total"),
                    List.of("Pesquisar", "Consultar", "Incluir", "Alterar")),
            tela(
                    "ordens-producao", "Produção", "Ordens de produção",
                    "Planejamento e acompanhamento das ordens de produção cadastradas na Omie.",
                    "https://app.omie.com.br/api/v1/produtos/op/", "ListarOrdemProducao", "Alta",
                    List.of("Número da OP", "Produto", "Data inicial", "Data final", "Situação", "Página"),
                    List.of("Ordem", "Produto", "Quantidade", "Previsão", "Conclusão", "Situação"),
                    List.of("Listar", "Consultar", "Incluir", "Alterar", "Concluir", "Reverter")),
            tela(
                    "pedidos-venda", "Vendas", "Pedidos de venda",
                    "Consulta dos pedidos de produtos, etapas de faturamento e previsão de entrega.",
                    "https://app.omie.com.br/api/v1/produtos/pedido/", "ListarPedidos", "Média",
                    List.of("Número", "Cliente", "Previsão inicial", "Previsão final", "Etapa", "Página"),
                    List.of("Número", "Cliente", "Previsão", "Etapa", "Itens", "Valor total"),
                    List.of("Listar", "Consultar", "Incluir", "Alterar", "Ver status")),
            tela(
                    "clientes", "Cadastros", "Clientes e fornecedores",
                    "Pesquisa dos cadastros usados pelos pedidos de compra e de venda.",
                    "https://app.omie.com.br/api/v1/geral/clientes/", "ListarClientes", "Média",
                    List.of("Razão social", "Nome fantasia", "CNPJ ou CPF", "Código de integração", "Página"),
                    List.of("ID Omie", "Razão social", "Nome fantasia", "CNPJ/CPF", "Cidade", "E-mail"),
                    List.of("Consultar", "Incluir", "Alterar", "Atualizar ou incluir"))
    );

    public List<OmieTela> listar() {
        return TELAS;
    }

    public Optional<OmieTela> buscar(String slug) {
        return TELAS.stream().filter(tela -> tela.slug().equals(slug)).findFirst();
    }

    private static OmieTela tela(String slug, String grupo, String titulo, String descricao,
                                 String endpoint, String chamadaPrincipal, String prioridade,
                                 List<String> filtros, List<String> colunas, List<String> acoes) {
        return new OmieTela(
                slug, grupo, titulo, descricao, endpoint, chamadaPrincipal, endpoint,
                prioridade, filtros, colunas, acoes);
    }
}
