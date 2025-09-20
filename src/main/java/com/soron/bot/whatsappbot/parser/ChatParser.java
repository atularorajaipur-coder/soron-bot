package com.soron.bot.whatsappbot.parser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

class QA {
    String question;
    String answer;
    QA(String q, String a) { question = q; answer = a; }
}

@Component
public class ChatParser {

    private List<QA> qaPairs = new ArrayList<>();

    public List<QA> getQaPairs() {
        return qaPairs;
    }

    @PostConstruct
    public void parse() throws Exception {
       // String chatText = Files.readString(Path.of("chat.txt"));
        InputStream is = ChatParser.class.getClassLoader().getResourceAsStream("chat.txt");
        if (is == null) {
            System.out.println("File not found!");
            return;
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        List<String> customerBuffer = new ArrayList<>();
        List<String> businessmanBuffer = new ArrayList<>();

        String line;
        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) continue;

            String customer = "Solar Client R.S. Poonia E72:";
            String businessMan = "Sauron Energy:";
            // Detect sender
            String sender = null;
            String message = line;

            if (line.contains(customer)) {
                sender = "Customer";
                message = line.substring(line.indexOf(customer) + customer.length()).trim();
            } else if (line.contains(businessMan)) {
                sender = "Businessman";
                message = line.substring(line.indexOf("Sauron Energy:") + businessMan.length()).trim();
            }

            if (sender == null) {
                // Continuation of previous message
                if (!customerBuffer.isEmpty() && businessmanBuffer.isEmpty()) {
                    // Append to last customer message
                    int last = customerBuffer.size() - 1;
                    customerBuffer.set(last, customerBuffer.get(last) + "\n" + line);
                } else if (!businessmanBuffer.isEmpty()) {
                    int last = businessmanBuffer.size() - 1;
                    businessmanBuffer.set(last, businessmanBuffer.get(last) + "\n" + line);
                }
                continue;
            }

            if (sender.equals("Customer")) {
                if (!businessmanBuffer.isEmpty()) {
                    // Pair previous question(s) with answer(s)
                    String question = String.join("\n", customerBuffer);
                    String answer = String.join("\n", businessmanBuffer);
                    qaPairs.add(new QA(question, answer));
                    customerBuffer.clear();
                    businessmanBuffer.clear();
                }
                customerBuffer.add(message);
            } else if (sender.equals("Businessman")) {
                businessmanBuffer.add(message);
            }
        }

        // Pair last messages
        if (!customerBuffer.isEmpty() && !businessmanBuffer.isEmpty()) {
            String question = String.join("\n", customerBuffer);
            String answer = String.join("\n", businessmanBuffer);
            qaPairs.add(new QA(question, answer));
        }


        // Print parsed Q&A
        for (QA qa : qaPairs) {
            System.out.println("Q: " + qa.question);
            System.out.println("A: " + qa.answer);
            System.out.println("----");
        }
    }
}