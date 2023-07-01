package filters.tinyTypes;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public abstract class StringTinyType implements TinyType<String> {

    @NotBlank
    private final String value;

    @Override
    public String get() {
        return value;
    }

    @Override
    public int compareTo(@NotNull TinyType<String> o) {
        return value.compareTo(o.get());
    }
}
