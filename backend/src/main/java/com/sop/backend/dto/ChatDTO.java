package com.sop.backend.dto;

import java.time.LocalDateTime;

public class ChatDTO {
    private Long id;
    private String content;
    private int upvotes;
    private int downvotes;
    private LocalDateTime created_at;
    private String discussion_title;
    private Long discussion_id;
    private String userName;

    public ChatDTO() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getUpvotes() {
        return upvotes;
    }

    public void setUpvotes(int upvotes) {
        this.upvotes = upvotes;
    }

    public int getDownvotes() {
        return downvotes;
    }

    public void setDownvotes(int downvotes) {
        this.downvotes = downvotes;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public String getDiscussion_title() {
        return discussion_title;
    }

    public void setDiscussion_title(String discussion_title) {
        this.discussion_title = discussion_title;
    }

    public Long getDiscussion_id() {
        return discussion_id;
    }

    public void setDiscussion_id(Long discussion_id) {
        this.discussion_id = discussion_id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}