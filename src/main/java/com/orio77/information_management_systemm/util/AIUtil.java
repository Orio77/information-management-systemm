package com.orio77.information_management_systemm.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;

import org.springframework.ai.chat.model.Generation;

public class AIUtil {

  public static String extractFromResponse(
      String response,
      String startDelimiter,
      String endDelimiter) {
    int startIndex = response.indexOf(startDelimiter);
    int endIndex = response.indexOf(
        endDelimiter,
        startIndex + startDelimiter.length());

    if (startIndex != -1 && endIndex != -1) {
      return response
          .substring(startIndex + startDelimiter.length(), endIndex)
          .trim();
    } else {
      return "";
    }
  }

  public static List<String> extractJSONFromResponse(String response) {
    List<String> jsonStrings = new ArrayList<>();
    String startDelimiter = "```json";
    String endDelimiter = "```";

    // Extract JSON blocks
    String json = extractFromResponse(response, startDelimiter, endDelimiter);

    // If JSON was found, parse it
    if (!json.isEmpty()) {
      ObjectMapper mapper = new ObjectMapper();
      try {
        JsonNode jsonNode = mapper.readTree(json);

        // Check if it's an array and iterate through each element
        if (jsonNode.isArray()) {
          for (JsonNode element : jsonNode) {
            jsonStrings.add(element.toString());
          }
        } else {
          // If it's a single object, add it directly
          jsonStrings.add(jsonNode.toString());
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    return jsonStrings;
  }

  public static List<String> extractJSONFromResponse(List<Generation> response) {
    List<String> jsonStrings = new ArrayList<>();
    String startDelimiter = "```json";
    String endDelimiter = "```";

    for (Generation gen : response) {
      String genText = gen.getOutput().getText();

      // Ensure it's not the thinking block
      if (!genText.contains(startDelimiter) || !genText.contains(endDelimiter)) {
        continue;
      }

      // Extract JSON blocks
      String json = extractFromResponse(genText, startDelimiter, endDelimiter);

      // If JSON was found, parse it
      if (!json.isEmpty()) {
        ObjectMapper mapper = new ObjectMapper();
        try {
          JsonNode jsonNode = mapper.readTree(json);

          // Check if it's an array and iterate through each element
          if (jsonNode.isArray()) {
            for (JsonNode element : jsonNode) {
              jsonStrings.add(element.toString());
            }
          } else {
            // If it's a single object, add it directly
            jsonStrings.add(jsonNode.toString());
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
    return jsonStrings;
  }
}
