package pl.pastebin.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;

import java.sql.Timestamp;

@Entity
@Table(name = "metadata")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Metadata {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    @NotNull
    private String name;

    @Column(name = "hash")
    @NotNull
    private String hash;

    @Column(name = "created_at")
    @NotNull
    private Timestamp createdAt;

    @Column(name = "expression_date")
    @NotNull
    private Timestamp expressionDate;
}
