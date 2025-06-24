package com.gogatherly.gogatherly.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GeneratedColumn;
import org.hibernate.annotations.SoftDelete;
import org.hibernate.annotations.SoftDeleteType;
import org.hibernate.type.NumericBooleanConverter;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "scanners")
@SoftDelete(columnName = "deleted", strategy = SoftDeleteType.DELETED, converter = NumericBooleanConverter.class)
public class Scanner implements UserDetails , CredentialsContainer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String username;

    private String password;

    @Enumerated(value = EnumType.STRING)
    private ROLE role;

    @Column(updatable = false, insertable = false)
    private boolean deleted;

    @ManyToOne
    @JoinColumn(
            name = "event_id",
            referencedColumnName = "id"
    )
    private Event event;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(this.role.name()));
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
        return true;
    }

    @Override
    public String getPassword() {
        return this.password ;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public void eraseCredentials() {
        this.password = null;
    }
}
