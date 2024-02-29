package be.technobel.chesstournament.dal.models.entities;

import be.technobel.chesstournament.dal.models.enums.Gender;
import be.technobel.chesstournament.dal.models.enums.Role;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;
import java.util.stream.Collectors;

@Entity @Data
public class UserEntity implements UserDetails {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(unique = true)
    private String username;
    @Column(unique = true)
    private String email;
    private String password;
    private Date dateOfBirth;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    private int elo;
    @Enumerated(EnumType.STRING)
    private Role role;
    private boolean enabled;

    @ManyToMany(mappedBy = "participants",fetch = FetchType.EAGER)
    private List<TournamentEntity> tournaments = new ArrayList<>();

    public void setElo(int elo) {
        if (elo<0){
            throw new IllegalArgumentException("Elo cannot be negative");
        } else if (elo>3000){
            throw new IllegalArgumentException("Elo must be under 3000");
        } else if (elo == 0) {
            this.elo = 1200;
        } else{
            this.elo = elo;
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_"+role.name()));
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
}
