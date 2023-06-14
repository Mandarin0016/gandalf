package com.mandarin.discord.json;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mandarin.discord.enums.GuildRole;
import com.mandarin.discord.repository.TriviaRepository;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static com.mandarin.discord.config.GuildStartupConfiguration.*;
import static com.mandarin.discord.util.GuildAccessVerifier.verifyCommandAccess;

public class ReadJsonCommand extends ListenerAdapter {

    public static final String JSON_READ_COMMAND_NAME = "json-read";
    private final TriviaRepository triviaRepository;

    public ReadJsonCommand() {
        this.triviaRepository = new TriviaRepository();
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        if (!event.getName().equalsIgnoreCase(JSON_READ_COMMAND_NAME)) {
            return;
        }

        boolean access = verifyCommandAccess(
                event,
                JSON_READ_COMMAND_NAME,
                java.util.List.of(TRIVIA_TEST_GUILD_ID),
                List.of(GuildRole.TESTER_TRIVIA_SERVER_ROLE));

        if (!access) {
            return;
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            List<TriviaQuestionInsertCommand> questionsAsList = Arrays.asList(mapper.readValue(Paths.get("src/main/java/com/mandarin/discord/json/CSharp01.json").toFile(), TriviaQuestionInsertCommand[].class));

            Set<String> uniqueNames = new HashSet<>(questionsAsList.stream().map(TriviaQuestionInsertCommand::getQuestion).collect(Collectors.toList()));

            List<TriviaQuestionInsertCommand> uniqueQuestions = new ArrayList<>();

            for (TriviaQuestionInsertCommand question : questionsAsList) {

                if (uniqueNames.contains(question.getQuestion()) && uniqueQuestions.stream().map(TriviaQuestionInsertCommand::getQuestion).noneMatch(uq -> uq.equals(question.getQuestion()))) {
                    uniqueQuestions.add(question);
                }
            }

           triviaRepository.insertQuestion(uniqueQuestions);

            System.out.println();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}
