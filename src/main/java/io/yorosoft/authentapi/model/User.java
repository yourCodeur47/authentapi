package io.yorosoft.authentapi.model;

import io.yorosoft.authentapi.model.util.Auditable;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.GenerationType;

@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "idx_users_email", columnNames = {"email"}),
                @UniqueConstraint(name = "idx_users_username", columnNames = {"username"})
        }
)
public class User extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String username;

    @Column(nullable = false, length = 255)
    private String email;

    @Column(name = "first_name", nullable = false, length = 255)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 255)
    private String lastName;

    @Column(nullable = false, length = 255)
    private String authorities;

    @Column(nullable = false)
    private Boolean enabled;

    @Column(nullable = false, length = 255)
    private String password;
}
