package com.felipeboos.gestao_produtos.handler;

import com.felipeboos.gestao_produtos.exception.RecursoDuplicadoException;
import com.felipeboos.gestao_produtos.exception.RecursoNaoEncontradoException;
import com.felipeboos.gestao_produtos.exception.RegraDeNegocioException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = GlobalExceptionHandlerTest.TestController.class)
@Import({GlobalExceptionHandler.class, GlobalExceptionHandlerTest.TestController.class})
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @RestController
    @RequestMapping("/teste")
    public static class TestController {

        @GetMapping("/not-found")
        public void notFound() {
            throw new RecursoNaoEncontradoException("Recurso nao encontrado");
        }

        @GetMapping("/negocio")
        public void regraNegocio() {
            throw new RegraDeNegocioException("Erro de regra de negocio");
        }

        @GetMapping("/duplicado")
        public void duplicado() {
            throw new RecursoDuplicadoException("Recurso duplicado");
        }

        @GetMapping("/generico")
        public void erroGenerico() {
            throw new RuntimeException("Erro inesperado");
        }

        @PostMapping("/validacao")
        public void validacao(@RequestBody @Valid TestDTO dto) {
        }
    }

    @Getter
    @Setter
    public static class TestDTO {
        @NotBlank(message = "campo obrigatorio")
        private String nome;
    }

    @Test
    @DisplayName("T1 - GlobalExceptionHandlerTest - Deve retornar 404 para RecursoNaoEncontradoException")
    void t1_deveRetornarNotFound() throws Exception {
        mockMvc.perform(get("/teste/not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensagem").value("Recurso nao encontrado"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("T2 - GlobalExceptionHandlerTest - Deve retornar 400 para RegraDeNegocioException")
    void t2_deveRetornarBadRequest() throws Exception {
        mockMvc.perform(get("/teste/negocio"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.mensagem").value("Erro de regra de negocio"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("T3 - GlobalExceptionHandlerTest - Deve retornar 409 para RecursoDuplicadoException")
    void t3_deveRetornarConflict() throws Exception {
        mockMvc.perform(get("/teste/duplicado"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.mensagem").value("Recurso duplicado"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("T4 - GlobalExceptionHandlerTest - Deve retornar 400 com lista de erros de validacao")
    void t4_deveRetornarErrosDeValidacao() throws Exception {
        mockMvc.perform(post("/teste/validacao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0]").value("nome: campo obrigatorio"));
    }

    @Test
    @DisplayName("T5 - GlobalExceptionHandlerTest - Deve retornar 500 para erro generico")
    void t5_deveRetornarErroGenerico() throws Exception {
        mockMvc.perform(get("/teste/generico"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Erro interno no servidor"));
    }
}