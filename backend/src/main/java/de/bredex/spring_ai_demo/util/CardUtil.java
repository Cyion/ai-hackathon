package de.bredex.spring_ai_demo.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CardUtil {
    private CardUtil() {}

    public static String extractCardInfo(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(json);
            JsonNode card = root.get("card");

            String name = card.path("name").asText();
            String manaCost = card.path("manaCost").asText();
            double cmc = card.path("cmc").asDouble();
            String type = card.path("type").asText();
            String types = card.path("types").toString();
            String text = card.path("text").asText();
            String rulings = card.path("rulings").toString();

            return String.format(
                "name: %s, manaCost: %s, cmc: %.1f, type: %s, types: %s, text: %s, rulings: %s",
                name, manaCost, cmc, type, types, text, rulings
            );
        } catch (Exception e) {
            return "Fehler beim Parsen der Kartendaten: " + e.getMessage();
        }
    }
}
