package iwcatalog.dto;

import iwcatalog.services.validation.UserInsertValid;
import lombok.Getter;
import lombok.Setter;

@UserInsertValid
@Setter
@Getter
public class UserInsertDTO extends UserDTO {
    private String password;

    UserInsertDTO() {
        super();
    }
}
