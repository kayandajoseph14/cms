package com.inn.cafe.POJO;

import javax.persistence.*;

import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serializable;

// Named query for finding a user by their email. This query is associated with the User entity.
@NamedQuery(name = "User.findByEmailId", query = "select u from User u where u.email=:email")
@NamedQuery(name = "User.getAllUser", query = "select new com.inn.cafe.wrapper.UserWrapper(u.id,u.name,u.email,u.contactNumber,u.status) from User u where u.role='user'")
@NamedQuery(name = "User.updateStatus", query = "update User u set u.status=:status where u.id =:id")
@NamedQuery(name = "User.getAllAdmin", query = "select u.email from User u where u.role='admin'")

// Lombok's @Data annotation generates getters, setters, and other utility methods like equals(), hashCode(), and toString().
@Data
@Entity  // Specifies that this class is an entity mapped to a database table.
@DynamicUpdate  // Ensures that only changed columns are updated in the database.
@DynamicInsert  // Ensures that only non-null fields are included in the insert statement.
@Table(name = "user", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email"),  // Enforces uniqueness on the 'email' column.
        @UniqueConstraint(columnNames = "contactNumber")  // Enforces uniqueness on the 'contactNumber' column.
})
public class User implements Serializable {  // Implements Serializable to allow instances to be serialized.

    private static final long serialVersionUID = 1L;  // Defines a version for this class during serialization.

    @Id  // Marks this field as the primary key of the entity.
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Auto-generates the ID using the database's identity column.
    @Column(name = "id")  // Maps this field to the "id" column in the "user" table.
    private Integer id;

    // Uncomment the validation annotations below as needed for field validation, e.g., NotNull, Size.

    // Name of the user. This field is required (nullable = false).
    @Column(name = "name", nullable = false)
    private String name;

    // Contact number of the user. It's unique and required.
    @Column(name = "contactNumber", unique = true, nullable = false)
    private String contactNumber;

    // Email of the user. It's unique and required.
    @Column(name = "email", unique = true, nullable = false)
    private String email;

    // User's password. This field is required and must meet length constraints (if applied).
    @Column(name = "password", nullable = false)
    private String password;

    // Status of the user account (e.g., active or inactive).
    @Column(name = "status")
    private String status;

    // Role of the user (e.g., admin, user).
    @Column(name = "role")
    private String role;
}
