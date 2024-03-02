package vg.skye.snippetist;

import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;

import java.util.ArrayList;
import java.util.List;

public class SnippetSuggestion extends Suggestion {
    private final String value;
    private SnippetSuggestion(StringRange range, String text) {
        super(range, ":" + text + ":");
        this.value = Snippetist.snippets.get(text);
    }

    @Override
    public String apply(String input) {
        StringRange range = getRange();
        if (range.getStart() == 0 && range.getEnd() == input.length()) {
            return value;
        }
        final StringBuilder result = new StringBuilder();
        if (range.getStart() > 0) {
            result.append(input, 0, range.getStart());
        }
        result.append(value);
        if (range.getEnd() < input.length()) {
            result.append(input.substring(range.getEnd()));
        }
        return result.toString();
    }

    public static Suggestions suggest(String text, int start) {
        List<Suggestion> result = new ArrayList<>();
        String remaining = text.substring(start + 1);
        for (String key: Snippetist.snippets.keySet()) {
            if (key.startsWith(remaining)) {
                result.add(new SnippetSuggestion(StringRange.between(start, text.length()), key));
            }
        }
        return Suggestions.create(text, result);
    }
}
