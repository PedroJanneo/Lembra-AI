package com.lembrai.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Classe de configuração para o CORS (Cross-Origin Resource Sharing).
 * O CORS é um mecanismo de segurança que permite que recursos (como sua API)
 * sejam acessados por um domínio diferente (como o seu frontend HTML).
 * Sem essa configuração, o navegador bloquearia a comunicação entre eles.
 */
@Configuration
public class CorsConfig {
    /**
     * Define um "bean" que configura o CORS para a sua aplicação Spring.
     * Um bean é um objeto que o Spring gerencia e injeta onde for necessário.
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            /**
             * Método sobrescrito para adicionar o mapeamento de CORS.
             */
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                // Adiciona o mapeamento para todos os endpoints da sua API (/**)
                registry.addMapping("/**")
                        // Define quais origens (domínios) têm permissão para acessar a sua API.
                        // "http://127.0.0.1:5500" e "http://localhost:5500" são os domínios
                        // padrão do Live Server do VS Code, onde o seu frontend estará rodando.
                        .allowedOrigins("http://127.0.0.1:5500", "http://localhost:5500")
                        // Permite todos os métodos HTTP (GET, POST, PUT, DELETE, etc.).
                        .allowedMethods("*");
            }
        };
    }
}
