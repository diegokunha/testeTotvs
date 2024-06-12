package com.desafiototvs.backend;

import com.desafiototvs.backend.domain.model.Conta;
import com.desafiototvs.backend.domain.repository.ContaRepository;
import com.desafiototvs.backend.application.service.ContaService;
import com.desafiototvs.backend.ui.dto.ContaRequestDTO;
import com.desafiototvs.backend.ui.dto.ContaResponseDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ContaApplicationServiceTest {

    @Mock
    private ContaRepository contaRepository;

    @InjectMocks
    private ContaService contaService;

    @Test
    void testCreateConta() {
        ContaRequestDTO conta = new ContaRequestDTO();
        conta.setDataVencimento(LocalDate.now());
        conta.setDataPagamento(LocalDate.now().plusDays(5));
        conta.setValor(BigDecimal.valueOf(100));
        conta.setDescricao("Test");
        conta.setSituacao("pending");

        when(contaRepository.save(any(Conta.class))).thenReturn(convertToEntity(conta));

        ContaResponseDTO createdConta = contaService.createConta(conta);
        assertEquals(conta, createdConta);
    }

    @Test
    void testUpdateConta() {
        Conta conta = new Conta();
        conta.setDataVencimento(LocalDate.now());
        conta.setDataPagamento(LocalDate.now().plusDays(5));
        conta.setValor(BigDecimal.valueOf(100));
        conta.setDescricao("Test");
        conta.setSituacao("pending");

        when(contaRepository.findById(1L)).thenReturn(Optional.of(conta));
        when(contaRepository.save(any(Conta.class))).thenReturn(conta);

        ContaResponseDTO updatedConta = contaService.updateConta(1L, convertToRequest(conta));
        assertEquals(conta, updatedConta);
    }

    @Test
    void testUpdateConta_NotFound() {
        ContaRequestDTO conta = new ContaRequestDTO();

        when(contaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> contaService.updateConta(1L, conta));
    }

    @Test
    void testAlterarSituacao() {
        Conta conta = new Conta();
        conta.setId(1L);
        conta.setSituacao("pending");

        when(contaRepository.findById(1L)).thenReturn(Optional.of(conta));

        contaService.alterarSituacao(1L, "paid");
        verify(contaRepository, times(1)).save(conta);
        assertEquals("paid", conta.getSituacao());
    }

    @Test
    void testGetContaById() {
        Conta conta = new Conta();
        conta.setId(1L);

        when(contaRepository.findById(1L)).thenReturn(Optional.of(conta));

        ContaResponseDTO foundConta = contaService.getContaById(1L);
        assertEquals(conta, foundConta);
    }

    @Test
    void testGetContaById_NotFound() {
        when(contaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> contaService.getContaById(1L));
    }

    private Conta convertToEntity(ContaRequestDTO contaRequestDTO) {
        Conta conta = new Conta();
        conta.setDataVencimento(contaRequestDTO.getDataVencimento());
        conta.setDataPagamento(contaRequestDTO.getDataPagamento());
        conta.setValor(contaRequestDTO.getValor());
        conta.setDescricao(contaRequestDTO.getDescricao());
        conta.setSituacao(contaRequestDTO.getSituacao());
        return conta;
    }

    private ContaRequestDTO convertToRequest(Conta conta) {
        ContaRequestDTO contaRequestDTO = new ContaRequestDTO();
        contaRequestDTO.setDataVencimento(conta.getDataVencimento());
        contaRequestDTO.setDataPagamento(conta.getDataPagamento());
        contaRequestDTO.setValor(conta.getValor());
        contaRequestDTO.setDescricao(conta.getDescricao());
        contaRequestDTO.setSituacao(conta.getSituacao());
        return contaRequestDTO;
    }
}
