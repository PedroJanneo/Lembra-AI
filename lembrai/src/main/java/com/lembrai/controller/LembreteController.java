package com.lembrai.controller;

import com.lembrai.dto.CreateCheckoutRequest;
import com.lembrai.model.Lembrete;
import com.lembrai.repository.LembreteRepository;
import com.lembrai.services.TwilioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/lembretes")
public class LembreteController {

    @Autowired
    private LembreteRepository lembreteRepository;

    @Autowired
    private TwilioService twilioService;

    // Injeta o PagamentoController para criar o link de pagamento da Stripe.
    @Autowired
    private PagamentoController pagamentoController;

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

    // Criar um novo lembrete, gerar o link de pagamento e enviar a mensagem
    @PostMapping
    public ResponseEntity<Lembrete> criar(@RequestBody Lembrete lembrete) {
        // Salva o lembrete no banco de dados primeiro
        Lembrete salvo = lembreteRepository.save(lembrete);

        // 1. Cria a requisição para o controller de pagamentos
        CreateCheckoutRequest checkoutReq = new CreateCheckoutRequest();
        checkoutReq.setAmountInCents(lembrete.getValorDivida().longValue());
        checkoutReq.setDescription("Pagamento do Título: " + lembrete.getTitulo());

        // 2. Chama o metodo de checkout para gerar o link de pagamento
        String linkPagamento = pagamentoController.criarCheckout(checkoutReq)
                .getBody()
                .getCheckoutUrl();

        // 3. Converte o valor da dívida de BigDecimal para double,
        // para que a formatação da string funcione corretamente.
        BigDecimal valorDivida = salvo.getValorDivida();
        double valorReais = valorDivida.doubleValue();

        // 4. Formata a mensagem
        String mensagem = String.format(
                "Olá %s, você possui uma dívida de R$%.2f referente ao título: %s.\n\n\n" +
                        "Para pagar, use o link: %s",
                salvo.getNomeDevedor(),
                valorReais, // Usa o double para a formatação
                salvo.getTitulo(),
                linkPagamento
        );

        // 5. Envia a mensagem via Twilio
        twilioService.enviarMensagemWhatsApp(mensagem);

        // Retorna a resposta
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
