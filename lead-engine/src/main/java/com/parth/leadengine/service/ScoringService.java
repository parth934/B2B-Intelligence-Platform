package com.parth.leadengine.service;

import com.parth.leadengine.model.Company;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class ScoringService {

    public int calculateScore(Company company) {
        int score = 0;
        String techStack = company.getTechStack() != null ? company.getTechStack().toLowerCase() : "";

        // 1. Core Base Hiring Signal
        if (company.isHiring()) {
            score += 30;
        }

        // 2. Multi-Domain Technology Evaluation Layer
        boolean matchedTech = false;

        // Enterprise Java Domain
        if (techStack.contains("java") || techStack.contains("spring")) {
            score += 20;
            matchedTech = true;
        }

        // Python & AI/ML Domain
        if (techStack.contains("python") || techStack.contains("machine learning") || techStack.contains("ai") || techStack.contains("data science")) {
            score += 25; // Higher premium weight due to high market demand for AI/ML
            matchedTech = true;
        }

        // Cloud & DevOps Domain
        if (techStack.contains("aws") || techStack.contains("azure") || techStack.contains("cloud") || techStack.contains("docker") || techStack.contains("kubernetes")) {
            score += 20;
            matchedTech = true;
        }

        // Database Domain
        if (techStack.contains("postgres") || techStack.contains("sql") || techStack.contains("database") || techStack.contains("oracle")) {
            score += 15;
            matchedTech = true;
        }

        // Testing & QA Domain
        if (techStack.contains("testing") || techStack.contains("selenium") || techStack.contains("qa") || techStack.contains("automation testing")) {
            score += 10;
            matchedTech = true;
        }

        // Frontend Web Frameworks
        if (techStack.contains("react") || techStack.contains("javascript") || techStack.contains("angular")) {
            score += 10;
        }

        // Cap the maximum score at 100
        return Math.min(score, 100);
    }

    public String determineStatus(int score) {
        if (score >= 70) return "HOT";
        if (score >= 45) return "WARM";
        return "COLD";
    }
}