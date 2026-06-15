package com.app.and.application.service;

import com.app.and.application.port.in.GerarRelatorioUseCase;
import com.app.and.application.port.in.ListarDiasContagemUseCase;
import com.app.and.application.port.out.ContagemRepositoryPort;
import com.app.and.domain.model.DiaContagem;
import com.app.and.domain.model.Relatorio;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GerarRelatorioService implements GerarRelatorioUseCase, ListarDiasContagemUseCase {

    private final ContagemRepositoryPort repositorio;

    public GerarRelatorioService(ContagemRepositoryPort repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public Relatorio gerar() {
        return new Relatorio(repositorio.ultima().orElse(null));
    }

    @Override
    public Relatorio gerar(String chave) {
        if (chave == null || chave.isBlank()) {
            return gerar();
        }
        return new Relatorio(repositorio.porChave(chave).orElse(null));
    }

    @Override
    public List<DiaContagem> listar() {
        return repositorio.listarDias();
    }
}
