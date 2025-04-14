package com.example.taskmanagementsystem.entities;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.UUID;

@Entity
@Table(name = "task", uniqueConstraints = {@UniqueConstraint(name="uc_task", columnNames = { "title" })})
@NoArgsConstructor
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String title;
    private String description;
    @ManyToOne(fetch = FetchType.EAGER)
    @PrimaryKeyJoinColumn(name = "author_id", referencedColumnName = "id")
    private User author;
    @ManyToOne(fetch = FetchType.EAGER)
    @PrimaryKeyJoinColumn(name = "executor_id", referencedColumnName = "id")
    private User executor;
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private StatusType status;
    @Enumerated(EnumType.STRING)
    @Column(name = "priority")
    private PriorityType priority;
    @OneToMany(mappedBy = "task", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Collection<Comment> comments;

    public Task(String title, String description, StatusType status, PriorityType priority) {
        setTitle(title);
        setDescription(description);
        setStatus(status);
        setPriority(priority);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public User getExecutor() {
        return executor;
    }

    public void setExecutor(User executor) {
        this.executor = executor;
    }

    public StatusType getStatus() {
        return status;
    }

    public void setStatus(StatusType status) {
        this.status = status;
    }

    public PriorityType getPriority() {
        return priority;
    }

    public void setPriority(PriorityType priority) {
        this.priority = priority;
    }

    public Collection<Comment> getComments() {
        return comments;
    }

    public void setComments(Collection<Comment> comments) {
        this.comments = comments;
    }
}
