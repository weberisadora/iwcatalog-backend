package iwcatalog.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserInsertDTO extends UserDTO {
    private String password;

    UserInsertDTO() {
        super();
    }

}
