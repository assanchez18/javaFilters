package filters.tinyTypes;

import jakarta.validation.constraints.NotBlank;

public class ColumnName extends StringTinyType {

    public ColumnName(@NotBlank String value) {
        super(value);
    }
}
