package net.timme7893.tokens.utils.text;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PKeys {

    @Getter
    private List<String> keys;

    private PKeys(String... keys) {
        this.keys = Arrays.asList(keys);
    }

    public static PKeys set(String... keys) {
        return new PKeys(keys);
    }

    public static PKeys ignore() {
        return new PKeys();
    }

    public void toLowerCase() {
        this.keys = this.keys.stream().map(String::toLowerCase).collect(Collectors.toList());
    }
}
