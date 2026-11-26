package cl.lowcost.ufs.Models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

// "user" es palabra reservada en Postgres, por eso la tabla se llama users.
// El id es el mismo UUID que Supabase Auth pone en el token (claim "sub").
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private boolean admin;
}
