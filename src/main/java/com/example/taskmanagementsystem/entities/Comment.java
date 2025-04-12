package com.example.taskmanagementsystem.entities;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@NoArgsConstructor
@Table(name = "comment",
        uniqueConstraints = {@UniqueConstraint(name = "uc_comment", columnNames = { "create_date", "talker_id" })})
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne(fetch = FetchType.EAGER)
    @PrimaryKeyJoinColumn(name = "task_id", referencedColumnName = "id")
    private Task task;
    @CreationTimestamp
    @Column(name = "create_date")
    private Instant createDate;
    @Version
    @UpdateTimestamp
    @Column(name = "update_date")
    private Instant updateDate;
    @Column(name = "text")
    private String text;
    @ManyToOne(fetch = FetchType.EAGER)
    @PrimaryKeyJoinColumn(name = "talker_id", referencedColumnName = "id")
    private User talker;

    public Comment(Task task, String text, User talker) {
        setTask(task);
        setText(text);
        setTalker(talker);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public Instant getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Instant createDate) {
        this.createDate = createDate;
    }

    public Instant getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Instant updateDate) {
        this.updateDate = updateDate;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public User getTalker() {
        return talker;
    }

    public void setTalker(User talker) {
        this.talker = talker;
    }
}
