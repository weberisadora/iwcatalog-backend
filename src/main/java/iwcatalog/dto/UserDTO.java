package iwcatalog.dto;

import iwcatalog.entities.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO implements Serializable {
    @Setter
    private Long id;

    @NotBlank(message = "Campo requerido")
    @Setter
    private String firstName;

    @Setter
    private String lastName;

    @Email(message = "Por favor, digite um e-mail válido")
    @Setter
    private String email;

    private Set<RoleDTO> roles = new HashSet<>();

    public UserDTO(User entity) {
        id = entity.getId();
        firstName = entity.getFirstName();
        lastName = entity.getLastName();
        email = entity.getEmail();
        entity.getRoles().forEach(role -> roles.add(new RoleDTO(role)));
    }
}
