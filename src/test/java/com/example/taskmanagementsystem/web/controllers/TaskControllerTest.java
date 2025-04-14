package com.example.taskmanagementsystem.web.controllers;

import com.example.taskmanagementsystem.AbstractTest;
import com.example.taskmanagementsystem.dto.TaskDto;
import com.example.taskmanagementsystem.entities.PriorityType;
import com.example.taskmanagementsystem.entities.StatusType;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

public class TaskControllerTest extends AbstractTest {
    protected static final String title = "test_title";

    @Test
    @WithMockUser(username = "admin@usa.net", password = "12345")
    public void givenAdminUser_whenPostAndGetTask_thenReturnCorrect() throws Exception {
        TaskDto task = objectMapper.readValue(mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/tasks")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"title\": \"" + title + "\" }"))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn().getResponse().getContentAsByteArray(), TaskDto.class);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/tasks")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("title", title))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(objectMapper.writeValueAsString(task)));
    }

    @Test
    @WithMockUser(username = "admin@usa.net", password = "12345")
    public void givenAdminUser_whenPostAndUpdateTask_thenReturnCorrect() throws Exception {
        TaskDto task = objectMapper.readValue(mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/tasks")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"title\": \"" + title + "\" }"))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn().getResponse().getContentAsByteArray(), TaskDto.class);

        task.setDescription("new description");
        task.setExecutor("user@usa.net");
        task.setStatus(StatusType.PROCESSING);
        task.setPriority(PriorityType.HIGH);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/tasks")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"" + title + "\", " +
                                "\"description\":\"new description\", " +
                                "\"executor\": \"user@usa.net\", " +
                                "\"status\": \"PROCESSING\", " +
                                "\"priority\": \"HIGH\"}"))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().string(objectMapper.writeValueAsString(task)));
    }

    @Test
    @WithMockUser(username = "admin@usa.net", password = "12345")
    public void givenAdminUserAndTask_whenPostAndDeleteTask_thenReturnCorrect() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/tasks")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"title\": \"" + title + "\" }"))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/tasks")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("title", title))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }
}
