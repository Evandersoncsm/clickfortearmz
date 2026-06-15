package com.app.and.integracao;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Credenciais da API do Omie. Mantemos um único registro (id = 1).
 * O App Secret fica cifrado no banco; o App Key não é segredo.
 */
@Entity
@Table(name = "integracao")
public class IntegracaoEntity {

    @Id
    private Long id = 1L;

    @Column(length = 255)
    private String appKey;

    @Column(length = 1024)
    private String appSecret;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }
}
