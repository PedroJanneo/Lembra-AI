package com.lembrai.controller;

import com.lembrai.dto.CheckoutResponse;
import com.lembrai.dto.CreateCheckoutRequest;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pagamentos")
public class PagamentoController {

    @Value("${stripe.keySecret}")
    private String stripeSecretKey;

    @Value("${stripe.buyURL}")
    private String urlCompra;

    @PostMapping("/checkout")
    public ResponseEntity<CheckoutResponse> criarCheckout(@RequestBody CreateCheckoutRequest req) {
        Stripe.apiKey = stripeSecretKey;

        try {
            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD) // cartão
                    .setSuccessUrl(urlCompra + "/pagamento/sucesso?session_id={CHECKOUT_SESSION_ID}")
                    .setCancelUrl(urlCompra + "/pagamento/cancelado")
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setQuantity(1L)
                                    .setPriceData(
                                            SessionCreateParams.LineItem.PriceData.builder()
                                                    .setCurrency("brl") // moeda aqui
                                                    .setUnitAmount(req.getAmountInCents()) // em centavos
                                                    .setProductData(
                                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                    .setName(req.getDescription() != null ? req.getDescription() : "Pagamento")
                                                                    .build()
                                                    )
                                                    .build()
                                    )
                                    .build()
                    )
                    .build();

            Session session = Session.create(params);  // 👈 nada de cast pra Map

            return ResponseEntity.ok(new CheckoutResponse(session.getUrl(), session.getId()));

        } catch (StripeException e) {
            e.printStackTrace();
            return ResponseEntity.status(502).build();
        }
    }
}
