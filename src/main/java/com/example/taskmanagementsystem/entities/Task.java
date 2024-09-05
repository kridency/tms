package com.example.taskmanagementsystem.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.List;

@Entity
@Data
@Table(name = "task")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    @ManyToOne(fetch = FetchType.EAGER)
    @PrimaryKeyJoinColumn(name = "author_id", referencedColumnName = "id")
    private User author;
    @ManyToOne(fetch = FetchType.EAGER)
    @PrimaryKeyJoinColumn(name = "worker_id", referencedColumnName = "id")
    private User worker;
    @Version
    @UpdateTimestamp
    @Column(name = "date")
    private Instant date;
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private TaskStatus status;
    @OneToMany(mappedBy = "task", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;
}
