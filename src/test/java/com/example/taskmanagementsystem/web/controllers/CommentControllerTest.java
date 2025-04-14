package com.example.taskmanagementsystem.web.controllers;

import com.example.taskmanagementsystem.AbstractTest;
import com.example.taskmanagementsystem.dto.CommentDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.Instant;
import java.util.Collections;

public class CommentControllerTest extends AbstractTest {
    private static final String title = "title_1";

    @Test
    @WithMockUser(username = "user@usa.net", password = "54321")
    @DisplayName("Попытка получить описание комментариев по заданному заголовку задачи.")
    public void givenUserAndTask_whenPostAndGetComment_thenReturnCorrect() throws Exception {
        CommentDto comment = objectMapper.readValue(mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/comments")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("title", title)
                        .content("{ \"text\": \"Comment successfully added!\" }"))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn().getResponse().getContentAsByteArray(), CommentDto.class);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/comments")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("title", title))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(
                        objectMapper.writeValueAsString(Collections.singleton(comment))));
    }

    @Test
    @WithMockUser(username = "user@usa.net", password = "54321")
    @DisplayName("Попытка обновления описание комментария по заданной дате публикации.")
    public void givenUserAndTask_whenPostAndUpdateComment_thenReturnCorrect() throws Exception {
        Instant createDate = objectMapper.readValue(mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/comments")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("title", title)
                        .content("{ \"text\": \"Comment successfully added!\" }"))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn().getResponse().getContentAsByteArray(), CommentDto.class).getCreateDate();

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/comments")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("create_date", createDate.toString())
                        .content("{\"text\":\"Comment successfully updated!\"}"))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    @WithMockUser(username = "user@usa.net", password = "54321")
    @DisplayName("Попытка удаления комментария по заданной дате публикации.")
    public void givenUserAndTask_whenPostAndDeleteComment_thenReturnCorrect() throws Exception {
        Instant createDate = objectMapper.readValue(mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/comments")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("title", title)
                        .content("{ \"text\": \"Comment successfully added!\" }"))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn().getResponse().getContentAsByteArray(), CommentDto.class).getCreateDate();

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/comments")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("create_date", createDate.toString()))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }
}
