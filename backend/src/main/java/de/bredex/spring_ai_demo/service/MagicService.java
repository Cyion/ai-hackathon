package de.bredex.spring_ai_demo.service;

import java.util.List;
import java.util.Objects;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import de.bredex.spring_ai_demo.util.CardUtil;

@Service
public class MagicService {
    private final static String SYSTEM_PROMPT = "You are an expert in magic the gathering. Magic the gathering is a card game where players use decks of cards to battle each other. Each card has unique abilities and characteristics. Your task is to help players build their decks by identifiying the best deck of {numberOfCards} cards in a given user deck of arbitrary size. The user will provide you with the his/her deck and you will respond with the best deck containing exactly {numberOfCards} cards.";

    private final WebClient webClient;
    private final ChatClient chatClient;
    private final CacheService<String, MagicCard> cardCacheService;
    private final CacheService<String, ModelResponse> deckCacheService;

    public MagicService(WebClient.Builder webClientBuilder, ChatClient.Builder builder,
            CacheService<String, MagicCard> cardCacheService, CacheService<String, ModelResponse> deckCacheService) {
        this.webClient = webClientBuilder.baseUrl("https://api.magicthegathering.io/v1/cards").build();
        this.chatClient = builder.build();
        this.cardCacheService = cardCacheService;
        this.deckCacheService = deckCacheService;
    }

    public MagicCard getCard(String id) {
        if (this.cardCacheService.contains(id)) {
            return this.cardCacheService.get(id).get();
        }

        final MagicCard card = this.webClient.get().uri("/{id}", id).retrieve().bodyToMono(String.class)
                .map(CardUtil::extractCardInfo).block();
        this.cardCacheService.put(id, card);
        return card;
    }

    public ModelResponse cutDeck(List<MagicCard> cards, int count) {
        final String cardsHash = String.valueOf(Objects.hash(cards, count));
        if (this.deckCacheService.contains(cardsHash)) {
            return this.deckCacheService.get(cardsHash).get();
        }

        final ModelResponse response = this.chatClient.prompt()
                .system(systemSpec -> systemSpec.text(SYSTEM_PROMPT).param("numberOfCards", count))
                .user(userSpec -> userSpec.text("The card deck is:\n" + String.join(",", cards.toString()))).call()
                .entity(ModelResponse.class);
        this.deckCacheService.put(cardsHash, response);
        return response;
    }
}
