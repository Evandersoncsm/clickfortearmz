package com.app.and.integracao;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
public class IntegracaoService {

    private final IntegracaoRepository repositorio;
    private final CryptoService crypto;

    public IntegracaoService(IntegracaoRepository repositorio, CryptoService crypto) {
        this.repositorio = repositorio;
        this.crypto = crypto;
    }

    @Transactional(readOnly = true)
    public List<ContaOmie> listar() {
        return repositorio.findAll().stream()
                .map(this::paraConta)
                .sorted(Comparator.comparing(ContaOmie::nome, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    @Transactional(readOnly = true)
    public ContaOmie buscar(Long id) {
        if (id == null) {
            return ContaOmie.nova();
        }
        return repositorio.findById(id)
                .map(this::paraConta)
                .orElseThrow(() -> new IllegalArgumentException("Conta Omie não encontrada."));
    }

    /** Credenciais de uma conta específica, para chamadas feitas somente no servidor. */
    @Transactional(readOnly = true)
    public CredenciaisOmie credenciais(Long id) {
        IntegracaoEntity entidade = repositorio.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Conta Omie não encontrada."));
        return new CredenciaisOmie(
                entidade.getId(),
                nomeDa(entidade),
                texto(entidade.getAppKey()),
                crypto.decrypt(entidade.getAppSecret()));
    }

    /**
     * Cria ou atualiza uma conta. App Secret em branco mantém o valor atual.
     */
    @Transactional
    public synchronized ContaOmie salvar(Long id, String nome, String appKey, String appSecret) {
        String nomeNormalizado = obrigatorio(nome, "Informe um nome para identificar a conta.");
        String appKeyNormalizada = obrigatorio(appKey, "Informe o App Key da conta Omie.");
        boolean novaConta = id == null;

        IntegracaoEntity entidade = novaConta ? novaEntidade() : repositorio.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Conta Omie não encontrada."));

        if (novaConta && (appSecret == null || appSecret.isBlank())) {
            throw new IllegalArgumentException("Informe o App Secret da nova conta Omie.");
        }

        entidade.setNome(nomeNormalizado);
        entidade.setAppKey(appKeyNormalizada);

        if (appSecret != null && !appSecret.isBlank()) {
            entidade.setAppSecret(crypto.encrypt(appSecret.trim()));
        }

        return paraConta(repositorio.save(entidade));
    }

    @Transactional
    public void excluir(Long id) {
        if (id == null || !repositorio.existsById(id)) {
            throw new IllegalArgumentException("Conta Omie não encontrada.");
        }
        repositorio.deleteById(id);
    }

    private IntegracaoEntity novaEntidade() {
        long proximoId = repositorio.findAll().stream()
                .map(IntegracaoEntity::getId)
                .filter(java.util.Objects::nonNull)
                .mapToLong(Long::longValue)
                .max()
                .orElse(0L) + 1;
        IntegracaoEntity entidade = new IntegracaoEntity();
        entidade.setId(proximoId);
        return entidade;
    }

    private ContaOmie paraConta(IntegracaoEntity entidade) {
        return new ContaOmie(
                entidade.getId(),
                nomeDa(entidade),
                texto(entidade.getAppKey()),
                entidade.getAppSecret() != null && !entidade.getAppSecret().isBlank());
    }

    private String nomeDa(IntegracaoEntity entidade) {
        String nome = texto(entidade.getNome());
        return nome.isBlank() ? "Conta Omie " + entidade.getId() : nome;
    }

    private String obrigatorio(String valor, String mensagem) {
        String normalizado = texto(valor);
        if (normalizado.isBlank()) {
            throw new IllegalArgumentException(mensagem);
        }
        return normalizado;
    }

    private String texto(String valor) {
        return valor == null ? "" : valor.trim();
    }
}
