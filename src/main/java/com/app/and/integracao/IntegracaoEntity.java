package com.app.and.integracao;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Credenciais de uma conta Omie. Cada registro representa uma empresa/conta.
 * O App Secret fica cifrado no banco; o App Key não é segredo.
 */
@Entity
@Table(name = "integracao")
public class IntegracaoEntity {

    @Id
    private Long id;

    @Column(length = 120)
    private String nome;

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

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
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
