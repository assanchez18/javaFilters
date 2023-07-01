package filters.tinyTypes;


import jakarta.validation.constraints.NotBlank;

public class TableName extends StringTinyType {

    public TableName(@NotBlank String value) {
        super(value);
    }
}
