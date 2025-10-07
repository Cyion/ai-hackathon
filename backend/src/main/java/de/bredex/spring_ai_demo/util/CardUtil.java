package de.bredex.spring_ai_demo.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.bredex.spring_ai_demo.service.MagicCard;

public class CardUtil {
    private CardUtil() {}

    public static MagicCard extractCardInfo(String json) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            final JsonNode root = mapper.readTree(json);
            final JsonNode card = root.get("card");

            final String id = card.path("multiverseid").asText();
            final String name = card.path("name").asText();
            final String manaCost = card.path("manaCost").asText();
            final String cmc = card.path("cmc").asText();
            final String type = card.path("type").asText();
            final String text = card.path("text").asText();
            final String rulings = card.path("rulings").toString();
            return new MagicCard(id, name, manaCost, cmc, type, text, rulings);
        } catch (Exception e) {
            throw new RuntimeException("Fehler beim Parsen der Kartendaten: " + e.getMessage());
        }
    }
}
