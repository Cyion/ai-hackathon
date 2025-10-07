package de.bredex.spring_ai_demo.controller;

import java.util.ArrayList;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.bredex.spring_ai_demo.service.MagicCard;
import de.bredex.spring_ai_demo.service.MagicService;
import de.bredex.spring_ai_demo.service.ModelResponse;

@RestController
public class MagicController {
    private final MagicService magicService;

    public MagicController(MagicService magicService) {
        this.magicService = magicService;
    }

    @GetMapping("/cut")
    public ModelResponse cutDeck(@RequestParam String deck, @RequestParam int count) {
        final List<MagicCard> cards = new ArrayList<>();

        for (String cardId : deck.split(",")) {
            final MagicCard card = this.magicService.getCard(cardId);
            cards.add(card);
        }
        return this.magicService.cutDeck(cards, count);
    }
}
