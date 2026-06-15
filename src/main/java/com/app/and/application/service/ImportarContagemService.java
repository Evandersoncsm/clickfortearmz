package com.app.and.application.service;

import com.app.and.application.port.in.ImportarContagemUseCase;
import com.app.and.application.port.out.ContagemPlanilhaReaderPort;
import com.app.and.application.port.out.ContagemRepositoryPort;
import com.app.and.domain.model.ContagemDiaria;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class ImportarContagemService implements ImportarContagemUseCase {

    private final ContagemPlanilhaReaderPort leitor;
    private final ContagemRepositoryPort repositorio;

    public ImportarContagemService(ContagemPlanilhaReaderPort leitor, ContagemRepositoryPort repositorio) {
        this.leitor = leitor;
        this.repositorio = repositorio;
    }

    @Override
    public ContagemDiaria importar(InputStream workbook) {
        ContagemDiaria contagem = leitor.lerDiaMaisRecente(workbook);
        repositorio.salvar(contagem);
        return contagem;
    }
}
