package com.desafiototvs.backend;

import com.desafiototvs.backend.controllers.ContaController;
import com.desafiototvs.backend.model.Conta;
import com.desafiototvs.backend.repositories.ContaRepository;
import com.desafiototvs.backend.services.ContaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.hamcrest.Matchers.is;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(ContaController.class)
public class ContaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ContaRepository contaRepository;

    @MockBean
    private ContaService contaService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateConta() throws Exception {
        Conta conta = new Conta();
        conta.setDataVencimento(LocalDate.now());
        conta.setDataPagamento(LocalDate.now().plusDays(5));
        conta.setValor(BigDecimal.valueOf(100));
        conta.setDescricao("Test");
        conta.setSituacao("pending");

        when(contaService.createConta(any(Conta.class))).thenReturn(conta);

        mockMvc.perform(MockMvcRequestBuilders.post("/contas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(conta)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.descricao").value("Test"));
    }

    @Test
    void testUpdateConta() throws Exception {
        Conta conta = new Conta();
        conta.setId(1L);
        conta.setDataVencimento(LocalDate.now());
        conta.setDataPagamento(LocalDate.now().plusDays(5));
        conta.setValor(BigDecimal.valueOf(100));
        conta.setDescricao("Test");
        conta.setSituacao("pending");

        when(contaService.updateConta(any(Long.class), any(Conta.class))).thenReturn(conta);

        mockMvc.perform(MockMvcRequestBuilders.put("/contas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(conta)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descricao").value("Test"));
    }

    @Test
    void testAlterarSituacao() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.patch("/contas/1/situacao")
                        .param("situacao", "paid"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetContas() throws Exception {
        Conta conta1 = new Conta();
        conta1.setId(1L);
        Conta conta2 = new Conta();
        conta2.setId(2L);

        List<Conta> contasList = Arrays.asList(conta1, conta2);
        Page<Conta> contasPage = new PageImpl<>(contasList);

        when(contaService.getContas(any(Pageable.class))).thenReturn(contasPage);

        mockMvc.perform(MockMvcRequestBuilders.get("/contas")
                        .param("page", "0")
                        .param("size", "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[1].id").value(2L))
                .andExpect(jsonPath("$.totalElements", is(2)))
                .andExpect(jsonPath("$.totalPages", is(1)))
                .andExpect(jsonPath("$.number", is(0)));
    }

    @Test
    void testGetContaById() throws Exception {
        Conta conta = new Conta();
        conta.setId(1L);

        when(contaService.getContaById(1L)).thenReturn(conta);

        mockMvc.perform(MockMvcRequestBuilders.get("/contas/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void testImportContasFromCSV() throws Exception {
        String csvContent = "data_vencimento,data_pagamento,valor,descricao,situacao\n"
                + "2024-06-01,2024-06-05,100.00,Utility Bill,paid\n"
                + "2024-06-15,2024-06-20,250.00,Rent,paid\n";

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "contas.csv",
                MediaType.TEXT_PLAIN_VALUE,
                csvContent.getBytes()
        );

        mockMvc.perform(MockMvcRequestBuilders.multipart("/contas/import")
                        .file(file))
                .andExpect(status().isOk());
    }

    @Test
    void testImportContasFromCSV_EmptyFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "empty.csv",
                MediaType.TEXT_PLAIN_VALUE,
                "".getBytes()
        );

        mockMvc.perform(MockMvcRequestBuilders.multipart("/contas/import")
                        .file(file))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testImportContasFromCSV_Error() throws Exception {
        String csvContent = "data_vencimento,data_pagamento,valor,descricao,situacao\n"
                + "2024-06-01,2024-06-05,100.00,Utility Bill,paid\n"
                + "2024-06-15,2024-06-20,250.00,Rent,paid\n";

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "contas.csv",
                MediaType.TEXT_PLAIN_VALUE,
                csvContent.getBytes()
        );

        doThrow(new RuntimeException("Erro ao importar o arquivo")).when(contaService).importContasFromCSV(file);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/contas/import")
                        .file(file))
                .andExpect(status().isInternalServerError());
    }
}
