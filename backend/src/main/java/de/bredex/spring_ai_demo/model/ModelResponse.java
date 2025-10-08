package de.bredex.spring_ai_demo.model;

import java.util.List;

public record ModelResponse(String explanation, List<MagicCard> cards) {
    
}
