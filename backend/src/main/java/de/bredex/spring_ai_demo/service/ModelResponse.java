package de.bredex.spring_ai_demo.service;

import java.util.List;

public record ModelResponse(String explanation, List<MagicCard> cards) {
    
}
