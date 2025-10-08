package de.bredex.spring_ai_demo.service;

import java.util.List;
import java.util.Objects;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import de.bredex.spring_ai_demo.model.MagicCard;
import de.bredex.spring_ai_demo.model.ModelResponse;
import de.bredex.spring_ai_demo.util.CardUtil;

@Service
public class MagicService {
    private static final String SYSTEM_PROMPT = """
            You are a professional Magic: The Gathering deck-building expert.
            Magic: The Gathering is a strategic card game where players use decks of cards — each with unique abilities and characteristics — to compete against one another.
            
            Your task is to analyze a user-provided deck and identify the best possible deck consisting of exactly {numberOfCards} cards.
            The user will give you a list of cards (the available deck pool), and you must choose the optimal combination of {numberOfCards} cards that work best together.
            
            When selecting the cards, consider factors such as synergy, mana curve, win conditions, consistency, and balance between offense, defense, and utility.
            
            Your response must include two parts:
            1. A list of the selected cards that make up the final deck.
            2. A clear and detailed explanation describing:
               - Why each card was chosen,
               - How the cards synergize with each other,
               - The overall strategy or playstyle of the resulting deck.
            
            Your explanations must be structured, thorough, and easy to understand even for intermediate players.
            """;

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
