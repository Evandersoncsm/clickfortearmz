package com.app.and.integracao;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IntegracaoService {

    private static final Long ID_FIXO = 1L;

    private final IntegracaoRepository repositorio;
    private final CryptoService crypto;

    public IntegracaoService(IntegracaoRepository repositorio, CryptoService crypto) {
        this.repositorio = repositorio;
        this.crypto = crypto;
    }

    /** App Key do Omie em texto puro (não é segredo) ou vazio se nunca configurado. */
    public String appKey() {
        return repositorio.findById(ID_FIXO).map(IntegracaoEntity::getAppKey).orElse("");
    }

    public boolean temAppSecret() {
        return repositorio.findById(ID_FIXO)
                .map(e -> e.getAppSecret() != null && !e.getAppSecret().isBlank())
                .orElse(false);
    }

    /** App Secret decifrado, para uso nas chamadas à API do Omie. */
    public String appSecretDecifrado() {
        return repositorio.findById(ID_FIXO)
                .map(e -> crypto.decrypt(e.getAppSecret()))
                .orElse(null);
    }

    /**
     * Salva as credenciais. App Secret em branco mantém o valor atual
     * (para não exigir redigitar o segredo a cada alteração).
     */
    @Transactional
    public void salvar(String appKey, String appSecret) {
        IntegracaoEntity entidade = repositorio.findById(ID_FIXO).orElseGet(() -> {
            IntegracaoEntity nova = new IntegracaoEntity();
            nova.setId(ID_FIXO);
            return nova;
        });

        entidade.setAppKey(appKey == null ? "" : appKey.trim());

        if (appSecret != null && !appSecret.isBlank()) {
            entidade.setAppSecret(crypto.encrypt(appSecret.trim()));
        }

        repositorio.save(entidade);
    }
}
