package com.app.and.application.service;

import com.app.and.application.port.in.ConsultarProducaoUseCase;
import com.app.and.application.port.in.ImportarProducaoUseCase;
import com.app.and.application.port.out.ProducaoPlanilhaReaderPort;
import com.app.and.application.port.out.ProducaoRepositoryPort;
import com.app.and.domain.model.ProgramacaoProducao;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class ImportarProducaoService implements ImportarProducaoUseCase, ConsultarProducaoUseCase {

    private final ProducaoPlanilhaReaderPort leitor;
    private final ProducaoRepositoryPort repositorio;

    public ImportarProducaoService(ProducaoPlanilhaReaderPort leitor, ProducaoRepositoryPort repositorio) {
        this.leitor = leitor;
        this.repositorio = repositorio;
    }

    @Override
    public ProgramacaoProducao importar(InputStream workbook) {
        ProgramacaoProducao programacao = leitor.ler(workbook);
        repositorio.salvar(programacao);
        return programacao;
    }

    @Override
    public ProgramacaoProducao ultima() {
        return repositorio.ultima().orElseGet(() -> new ProgramacaoProducao(java.util.List.of()));
    }
}
