package com.lembrai.controller;

import com.lembrai.model.Lembrete;
import com.lembrai.repository.LembreteRepository;
import com.lembrai.services.TwilioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/lembretes")
public class LembreteController {

    @Autowired
    private LembreteRepository lembreteRepository;

    @Autowired
    private TwilioService twilioService;

    // Listar todos os lembretes
    @GetMapping
    public List<Lembrete> listarTodos() {
        return lembreteRepository.findAll();
    }

    // Buscar lembrete por id
    @GetMapping("/{id}")
    public ResponseEntity<Lembrete> buscarPorId(@PathVariable Long id) {
        Optional<Lembrete> lembrete = lembreteRepository.findById(id);
        return lembrete.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Criar um novo lembrete e enviar mensagem (sem Gemini/OpenAI)
    @PostMapping
    public ResponseEntity<Lembrete> criar(@RequestBody Lembrete lembrete) {
        Lembrete salvo = lembreteRepository.save(lembrete);

        // Monta mensagem simples sem usar Gemini/OpenAI
        String mensagem = String.format(
                "Olá %s, você possui uma dívida de R$%.2f referente ao título: %s.",
                salvo.getNomeDevedor(),
                salvo.getValorDivida(),
                salvo.getTitulo()
        );

        twilioService.enviarMensagemWhatsApp(mensagem);

        return ResponseEntity.ok(salvo);
    }

    // Atualizar lembrete existente
    @PutMapping("/{id}")
    public ResponseEntity<Lembrete> atualizar(@PathVariable Long id, @RequestBody Lembrete dados) {
        return lembreteRepository.findById(id)
                .map(lembrete -> {
                    lembrete.setTitulo(dados.getTitulo());
                    lembrete.setDescricao(dados.getDescricao());
                    lembrete.setDataHora(dados.getDataHora());
                    lembrete.setNomeDevedor(dados.getNomeDevedor());
                    lembrete.setValorDivida(dados.getValorDivida());
                    lembrete.setConcluido(dados.isConcluido());
                    Lembrete atualizado = lembreteRepository.save(lembrete);
                    return ResponseEntity.ok(atualizado);
                }).orElse(ResponseEntity.notFound().build());
    }

    // Deletar lembrete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        return lembreteRepository.findById(id)
                .map(lembrete -> {
                    lembreteRepository.delete(lembrete);
                    return ResponseEntity.noContent().<Void>build();
                }).orElse(ResponseEntity.notFound().build());
    }
}
