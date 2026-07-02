package com.example.Biblioteca;

import com.example.Biblioteca.Entity.AutorEntity;
import com.example.Biblioteca.Entity.EditoraEntity;
import com.example.Biblioteca.Entity.LivroEntity;
import com.example.Biblioteca.Entity.UsuarioEntity;
import com.example.Biblioteca.dto.LivroRequestDTO;
import com.example.Biblioteca.repository.AutorRepository;
import com.example.Biblioteca.repository.EditoraRepository;
import com.example.Biblioteca.repository.LivroRepository;
import com.example.Biblioteca.repository.UsuarioRepository;
import com.example.Biblioteca.service.TokenService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@AutoConfigureTestRestTemplate
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
        "api.security.token.secret=chave-super-secreta-de-teste",
         "spring.flyway.enabled=false",
         "spring.jpa.hibernate.ddl-auto=create-drop"
		})


public class LivroIntegrationTest {
    private final TestRestTemplate restTemplate;
    private final TokenService tokenService;
    private final UsuarioRepository usuarioRepository;
    private final LivroRepository livroRepository;
    private final AutorRepository autorRepository;
    private final EditoraRepository editoraRepository;

    @MockitoBean
    private JavaMailSender javaMailSender;

    @Autowired
    public LivroIntegrationTest(
            TestRestTemplate restTemplate,
            LivroRepository livroRepository,
            AutorRepository autorRepository,
            EditoraRepository editoraRepository,
            TokenService tokenService,
            UsuarioRepository usuarioRepository
    ) {
        this.restTemplate = restTemplate;
        this.livroRepository = livroRepository;
        this.autorRepository = autorRepository;
        this.editoraRepository = editoraRepository;
        this.tokenService = tokenService;
        this.usuarioRepository = usuarioRepository;
    }

    private HttpHeaders headers;
    private AutorEntity autorBase;
	private EditoraEntity editoraBase;

    private UsuarioEntity usuarioBase;

    @BeforeEach
    void setUp() {

        usuarioBase = new UsuarioEntity();
        usuarioBase.setNome("Testador");
        usuarioBase.setLogin("teste@teste.com");
        usuarioBase.setSenha("senha123");
        usuarioBase.setRoles(new java.util.HashSet<>());

        usuarioBase = usuarioRepository.save(usuarioBase);

        String token = tokenService.generateToken(usuarioBase);

        headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        AutorEntity autor = new AutorEntity();
        autor.setNome("Tolkien");
        autor.setIdade(80);
        autor.setBiografia("Criador de mundos");
        autorBase = autorRepository.save(autor);

        EditoraEntity editora = new EditoraEntity();
        editora.setNome("HarperCollins");
        editora.setCnpj("123456789");
        editoraBase = editoraRepository.save(editora);

    }

    @AfterEach
    void limparBanco(){
        livroRepository.deleteAll();
        autorRepository.deleteAll();
        editoraRepository.deleteAll();
        usuarioRepository.deleteAll();
    }

    @Test
    @DisplayName("Cenário 1: POST /livros - Deve retornar 201 e persistir no banco")
    void deveCriarLivroNoBancoERetornar201(){
        LivroRequestDTO novoLivro = new LivroRequestDTO(
                "O Senhor dos Anéis",
                "Livro de fantasia épica",
                autorBase.getId(),
                editoraBase.getId()
        );
        HttpEntity<LivroRequestDTO> request = new HttpEntity<>(novoLivro, headers);

        ResponseEntity<Void> response = restTemplate.exchange(
                "/livros",
                HttpMethod.POST,
                request,
                Void.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode(), "A API deveria retornar 201 Created");

        assertEquals(1, livroRepository.count(), "Deveria existir 1 livro com o nome 'O Senhor dos Anéis' no banco");
        LivroEntity livroSalvo = livroRepository.findAll().get(0);
        assertEquals("O Senhor dos Anéis", livroSalvo.getNome(), "O nome do livro salvo deveria ser 'O Senhor dos Anéis'");
    }

    @Test
    @DisplayName("Cenário 2: GET /livros/{id} - Deve retornar 404 ao buscar ID inexistente")
    void deveRetornar404AoBuscarIdInexistente(){
        Long idInexistente = 999L;
        HttpEntity<Void> request = new HttpEntity<>(null, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "/livros/" + idInexistente,
                HttpMethod.GET,
                request,
                String.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "A API deveria retornar 404 Not Found para ID inexistente");
    }

    @Test
	@DisplayName("Cenário 3: PATCH /livros/{id}/lido - Deve retornar 200 e alterar status no banco")
    void deveAlterarStatusDoLivroParaLido () {
        LivroEntity livroExistente = new LivroEntity(null, "O Hobbit", "Aventura", false, null, null, editoraBase, autorBase);
        livroExistente = livroRepository.save(livroExistente);

        HttpEntity<Void> request = new HttpEntity<>(null, headers);

        ResponseEntity<Void> response = restTemplate.exchange(
                "/livros/" + livroExistente.getId() + "/lido",
                HttpMethod.PATCH,
                request,
                Void.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode(), "A API deveria retornar 200 OK ao alterar status do livro");

       	LivroEntity livroAtualizado = livroRepository.findById(livroExistente.getId()).orElseThrow();
        assertTrue(livroAtualizado.getLido(), "O status do livro deveria ser alterado para 'lido' no banco");
    }
}
