package com.parth.leadengine.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import com.parth.leadengine.model.Company;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ScoringService {

    @Value("${GEMINI_API_KEY}")
    private String apiKey;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public Company enrichLeadWithAI(String companyName, String rawJobDescription) {

        Company company = new Company();
        company.setName(companyName);
        company.setHiring(true);

        String prompt = "Analyze this raw tech job description for a business development lead engine:\n\n"
                + "\"" + rawJobDescription + "\"\n\n"
                + "Extract the core details and respond ONLY with a valid, flat JSON object. Do not include markdown codeblocks or extra text. "
                + "Follow this schema exactly:\n"
                + "{\n"
                + "  \"score\": <integer between 1 and 100 based on scale, market demand, and budget urgency>,\n"
                + "  \"status\": <string, strictly one of: 'HOT', 'WARM', 'COLD'>,\n"
                + "  \"techStack\": <string, comma-separated list of core technical frameworks detected like Java, Python, AWS, SQL, Selenium, etc.>,\n"
                + "  \"industry\": <string, specialized industry vertical like FinTech, HealthTech, E-commerce, SaaS, Logistics>\n"
                + "}";

        try {
            // Instantiate the modern Google Gen AI client explicitly with your key
            Client client = Client.builder()
                    .apiKey(apiKey)
                    .build();

            // Fire the text generation mapping call
            GenerateContentResponse response = client.models.generateContent(
                    "gemini-2.5-flash",
                    prompt,
                    null
            );

            // Clean any potential code block wrappers
            String cleanJsonString = response.text()
                    .replace("```json", "")
                    .replace("```", "")
                    .trim();

            // Map keys directly into the JPA database entity fields
            JsonNode rootNode = objectMapper.readTree(cleanJsonString);

            company.setScore(rootNode.path("score").asInt(50));
            company.setStatus(rootNode.path("status").asText("WARM"));
            company.setTechStack(rootNode.path("techStack").asText("Unknown"));
            company.setIndustry(rootNode.path("industry").asText("Technology"));

        } catch (Exception e) {
            System.err.println("Gemini SDK Pipeline Processing Failed, using fallback layout parameters: " + e.getMessage());
            // Safe system fallbacks
            company.setScore(40);
            company.setStatus("COLD");
            company.setTechStack("Unknown");
            company.setIndustry("Technology");
        }

        return company;
    }

    // Bridge method to prevent compilation errors in your background scheduler pipeline
    public int calculateScore(Company company) {
        // This keeps your scheduler happy. It runs the AI enrichment pipeline
        // using the company's tech stack data as the context description.
        Company aiEnrichedCompany = enrichLeadWithAI(company.getName(), company.getTechStack());

        // Update the original object with the new AI-generated values
        company.setScore(aiEnrichedCompany.getScore());
        company.setStatus(aiEnrichedCompany.getStatus());
        company.setTechStack(aiEnrichedCompany.getTechStack());
        company.setIndustry(aiEnrichedCompany.getIndustry());

        return company.getScore();
    }

    // Second bridge method to keep the background scheduler/controller compilation intact
    public String determineStatus(int score) {
        // Fallback conditional tracking logic to satisfy legacy dual-method controller invocations
        if (score >= 70) return "HOT";
        if (score >= 45) return "WARM";
        return "COLD";
    }
}