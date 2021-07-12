package net.timme7893.tokens.utils.text;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PValues {

    @Getter
    private List<Object> values;

    private PValues(Object... values) {
        this.values = Arrays.asList(values);
    }

    public static PValues set(Object... values) {
        return new PValues(values);
    }

    public static PValues ignore() {
        return new PValues();
    }

    public void toLowerCase() {
        this.values = this.values.stream().map(object -> object.toString().toLowerCase()).collect(Collectors.toList());
    }
}
