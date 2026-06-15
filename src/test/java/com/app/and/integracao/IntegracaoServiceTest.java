package com.app.and.integracao;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IntegracaoServiceTest {

    @Mock
    private IntegracaoRepository repositorio;

    @Mock
    private CryptoService crypto;

    @InjectMocks
    private IntegracaoService servico;

    @Test
    void criaNovaContaComProximoIdESegredoCifrado() {
        IntegracaoEntity existente = entidade(3L, "Matriz", "key-1", "cifrado-1");
        when(repositorio.findAll()).thenReturn(List.of(existente));
        when(crypto.encrypt("segredo-novo")).thenReturn("cifrado-novo");
        when(repositorio.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ContaOmie conta = servico.salvar(null, "Filial", "key-2", "segredo-novo");

        assertEquals(4L, conta.id());
        assertEquals("Filial", conta.nome());
        assertTrue(conta.temAppSecret());

        ArgumentCaptor<IntegracaoEntity> captor = ArgumentCaptor.forClass(IntegracaoEntity.class);
        verify(repositorio).save(captor.capture());
        assertEquals("cifrado-novo", captor.getValue().getAppSecret());
    }

    @Test
    void editarComSegredoEmBrancoPreservaValorAtual() {
        IntegracaoEntity existente = entidade(1L, "Matriz", "key-antiga", "cifrado-atual");
        when(repositorio.findById(1L)).thenReturn(Optional.of(existente));
        when(repositorio.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ContaOmie conta = servico.salvar(1L, "Matriz atualizada", "key-nova", " ");

        assertEquals("Matriz atualizada", conta.nome());
        assertEquals("key-nova", conta.appKey());
        assertEquals("cifrado-atual", existente.getAppSecret());
        verify(crypto, never()).encrypt(any());
    }

    @Test
    void devolveCredenciaisDaContaEscolhida() {
        IntegracaoEntity filial = entidade(2L, "Filial", "key-filial", "cifrado-filial");
        when(repositorio.findById(2L)).thenReturn(Optional.of(filial));
        when(crypto.decrypt("cifrado-filial")).thenReturn("segredo-filial");

        CredenciaisOmie credenciais = servico.credenciais(2L);

        assertEquals(2L, credenciais.contaId());
        assertEquals("key-filial", credenciais.appKey());
        assertEquals("segredo-filial", credenciais.appSecret());
    }

    private IntegracaoEntity entidade(Long id, String nome, String appKey, String appSecret) {
        IntegracaoEntity entidade = new IntegracaoEntity();
        entidade.setId(id);
        entidade.setNome(nome);
        entidade.setAppKey(appKey);
        entidade.setAppSecret(appSecret);
        return entidade;
    }
}
