package guru.qa.niffler.condition;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Colour {
    yellow("rgba(255, 183, 3, 1)"),
    orange("rgba(251, 133, 0, 1)"),
    blue("rgba(41, 65, 204, 1)"),
    green("rgba(53, 173, 123, 1)");

    public final String rgb;

}