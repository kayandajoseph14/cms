package com.inn.cafe.POJO;

import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;

@NamedQuery(name = "Category.getAllCategory",query = "select c from Category c")

@Data
@Entity  // Specifies that this class is an entity mapped to a database table.
@DynamicUpdate  // Ensures that only changed columns are updated in the database.
@DynamicInsert  // Ensures that only non-null fields are included in the insert statement.
@Table(name = "category")

public class Category implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id  // Marks this field as the primary key of the entity.
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Auto-generates the ID using the database's identity column.
    @Column(name = "id")  // Maps this field to the "id" column in the "category" table.
    private Integer id;


    // Name of the category. This field is required (nullable = false).
    @Column(name = "name", nullable = false)
    private String name;
}
