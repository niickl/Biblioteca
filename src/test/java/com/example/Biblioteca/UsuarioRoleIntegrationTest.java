package com.example.Biblioteca;

import com.example.Biblioteca.Entity.RoleEntity;
import com.example.Biblioteca.Entity.RoleEnum;
import com.example.Biblioteca.Entity.UsuarioEntity;
import com.example.Biblioteca.repository.RoleRepository;
import com.example.Biblioteca.repository.UsuarioRepository;
import com.example.Biblioteca.service.TokenService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.http.HttpHeaders;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;

@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
			properties = {
                "api.security.token.secret=chave-super-secreta-de-teste",
                "spring.flyway.enabled=false",
                "spring.jpa.hibernate.ddl-auto=create-drop"
        })
public class UsuarioRoleIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private RoleRepository roleRepository;

    @MockitoBean
    private JavaMailSender javaMailSender;

    private HttpHeaders headers;
    private String tokenAutenticacao;
    private UsuarioEntity usuarioAlvo;
    private RoleEntity roleUser;
    private RoleEntity roleAdmin;

    @Autowired
    public UsuarioRoleIntegrationTest(WebTestClient webTestClient, UsuarioRepository usuarioRepository, RoleRepository roleRepository, TokenService tokenService) {
        this.webTestClient = webTestClient;
        this.usuarioRepository = usuarioRepository;
        this.roleRepository = roleRepository;
        this.tokenService = tokenService;
    }

    @BeforeEach
    void setup(){
        RoleEntity novaRoleUser = new RoleEntity();
        novaRoleUser.setStatus(RoleEnum.user);
        roleUser = roleRepository.save(novaRoleUser);

        RoleEntity novaRoleAdmin = new RoleEntity();
        novaRoleAdmin.setStatus(RoleEnum.admin);
        roleAdmin = roleRepository.save(novaRoleAdmin);

        UsuarioEntity adminLogado = new UsuarioEntity();
        adminLogado.setNome("Admin Logado");
        adminLogado.setLogin("admin@logado.com");
        adminLogado.setSenha("senha123");
        adminLogado.setRoles(new HashSet<>());
        adminLogado = usuarioRepository.save(adminLogado);

        tokenAutenticacao = "Bearer " + tokenService.generateToken(adminLogado);

        usuarioAlvo = new UsuarioEntity();
        usuarioAlvo.setNome("Usuario Alvo");
        usuarioAlvo.setLogin("usuario@alvo.com");
        usuarioAlvo.setSenha("senha123");
        usuarioAlvo.setRoles(new HashSet<>());
        usuarioAlvo = usuarioRepository.save(usuarioAlvo);
    }

    @AfterEach
    void limparBanco(){
        usuarioRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    @DisplayName("Cenário 1: PATCH - Deve associar Role com sucesso e atualizar a tabela")
    void deveAssociarRoleComSucesso() {
        webTestClient.patch()
                .uri("/usuarios/{usuarioId}/roles/{roleId}", usuarioAlvo.getId(), roleUser.getId())
                .header(HttpHeaders.AUTHORIZATION, tokenAutenticacao)
                .exchange()
                .expectStatus().isOk();

        UsuarioEntity usuarioAtualizado = usuarioRepository.findById(usuarioAlvo.getId()).orElseThrow();
        assertEquals(1, usuarioAtualizado.getRoles().size());
        assertTrue(usuarioAtualizado.getRoles().stream().anyMatch(role -> role.getId().equals(roleUser.getId())));
	}

    @Test
    @DisplayName("Cenário 2: PATCH - Deve retornar 400 Bad Request ao tentar duplicar associação")
    void deveRetornarBadRequestAoDuplicarAssociacao() {
        usuarioAlvo.getRoles().add(roleUser);
        usuarioRepository.save(usuarioAlvo);

        webTestClient.patch()
                .uri("/usuarios/{usuarioId}/roles/{roleId}", usuarioAlvo.getId(), roleUser.getId())
                .header(HttpHeaders.AUTHORIZATION, tokenAutenticacao)
                .exchange()
                .expectStatus().isBadRequest();

        UsuarioEntity usuarioVerificado = usuarioRepository.findById(usuarioAlvo.getId()).orElseThrow();
        assertEquals(1, usuarioVerificado.getRoles().size());
    }

    @Test
    @DisplayName("Cenário 3: DELETE - Deve remover associação e retornar 204 No Content")
    void deveRemoverAssociacaoERetornar204NoContent() {
        usuarioAlvo.getRoles().add(roleUser);
        usuarioRepository.save(usuarioAlvo);

        webTestClient.delete()
                .uri("/usuarios/{usuarioId}/roles/{roleId}", usuarioAlvo.getId(), roleUser.getId())
                .header(HttpHeaders.AUTHORIZATION, tokenAutenticacao)
                .exchange()
                .expectStatus().isNoContent();

        UsuarioEntity usuarioVerificado = usuarioRepository.findById(usuarioAlvo.getId()).orElseThrow();
        assertTrue(usuarioVerificado.getRoles().isEmpty());
    }

    @Test
    @DisplayName("Cenário 4: PATCH/DELETE - Deve retornar 404 se UUID do usuário não existir")
    void deveRetornar404SeUsuarioNaoExistir() {
        webTestClient.patch()
                .uri("/usuarios/{usuarioId}/roles/{roleId}", "00000000-0000-0000-0000-000000000000", roleUser.getId())
                .header(HttpHeaders.AUTHORIZATION, tokenAutenticacao)
                .exchange()
                .expectStatus().isNotFound();

        webTestClient.delete()
                .uri("/usuarios/{usuarioId}/roles/{roleId}", "00000000-0000-0000-0000-000000000000", roleUser.getId())
                .header(HttpHeaders.AUTHORIZATION, tokenAutenticacao)
                .exchange()
                .expectStatus().isNotFound();
    }
}
