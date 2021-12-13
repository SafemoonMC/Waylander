package me.eduardwayland.mooncraft.waylander.command.suggest;

import lombok.Getter;
import lombok.NoArgsConstructor;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@NoArgsConstructor
public final class Suggestions {

    /*
    Fields
     */
    private final @NotNull List<Suggestion> suggestionList = new ArrayList<>();

    /*
    Methods
     */
    public void add(@NotNull Suggestion suggestion) {
        if (suggestionList.contains(suggestion)) return;
        suggestionList.add(suggestion);
    }

    public boolean isEmpty() {
        return suggestionList.isEmpty();
    }

    @NotNull
    @UnmodifiableView
    @Contract(pure = true)
    public List<Suggestion> getSuggestionList() {
        return Collections.unmodifiableList(suggestionList);
    }
}