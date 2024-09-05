package com.example.taskmanagementsystem.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Data
@Table(name = "comment")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.EAGER)
    @PrimaryKeyJoinColumn(name = "talker_id", referencedColumnName = "id")
    private User talker;
    @ManyToOne(fetch = FetchType.EAGER)
    @PrimaryKeyJoinColumn(name = "task_id", referencedColumnName = "id")
    private Task task;
    @Version
    @UpdateTimestamp
    @Column(name = "date")
    private Instant date;
    @Column(name = "text")
    private String text;
}
