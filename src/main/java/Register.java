import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Register {

    private String email;
    private String password;
    private String name;
}
