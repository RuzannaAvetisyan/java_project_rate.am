package am.rate.project.bestexchangerate.dom;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.util.Collection;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
public class Client implements UserDetails {
    @Id
    @Email(regexp = "^[\\w-\\+]+(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[a-z]{2,})$")
    private String username;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String password;

    private boolean active;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "client", fetch = FetchType.LAZY)
    private List<Request> requests;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isActive();
    }
}
