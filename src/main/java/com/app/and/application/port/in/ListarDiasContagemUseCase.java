package com.app.and.application.port.in;

import com.app.and.domain.model.DiaContagem;

import java.util.List;

/**
 * Porta de entrada: lista os dias de contagem persistidos.
 */
public interface ListarDiasContagemUseCase {

    List<DiaContagem> listar();
}
