package de.bredex.spring_ai_demo.service;

import java.util.List;

import de.bredex.spring_ai_demo.util.CardUtil;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class MagicService {

    private final WebClient webClient;
    private final ChatClient chatClient;
    private final CardUtil cardUtil;

    public MagicService(WebClient.Builder webClientBuilder, ChatClient.Builder builder) {
        this.webClient = webClientBuilder.baseUrl("https://api.magicthegathering.io/v1/cards").build();
        this.chatClient = builder.build();
        this.cardUtil = new CardUtil();
    }

    public Mono<String> getCard(String id) {
        return this.webClient.get().uri("/{id}", id).retrieve().bodyToMono(String.class)
                .map(cardUtil::extractCardInfo);
    }

    public List<String> cutDeck(List<String> cards) {
        String response = this.chatClient.prompt().system(systemSpec -> systemSpec.text(
                        "You are an expert in magic the gathering. Magic the gathering is a card game where players use decks of cards to battle each other. Each card has unique abilities and characteristics. Your task is to help players build their decks by identifiying the best deck of five cards in a given user deck of arbitrary size. The user will provide you with the his/her deck and you will respond with the best deck containing exactly five cards."))
                .user(userSpec -> userSpec.text("The card deck is:\n" + String.join(",", cards))).call().content();
        System.out.println(response);
        return cards;
    }
}
