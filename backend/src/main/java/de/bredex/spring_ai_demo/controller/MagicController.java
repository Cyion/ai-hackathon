package de.bredex.spring_ai_demo.controller;

import java.util.ArrayList;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import de.bredex.spring_ai_demo.service.MagicService;

@RestController
public class MagicController {
    private final MagicService magicService;

    public MagicController(MagicService magicService) {
        this.magicService = magicService;
    }

    @GetMapping("/cut")
    public String cutDeck(@RequestParam String deck) {
        final List<String> cards = new ArrayList<>();

        for (String cardId : deck.split(",")) {
            final String card = this.magicService.getCard(cardId);
            cards.add(card);
        }
        return this.magicService.cutDeck(cards);
    }
}
