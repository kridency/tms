package com.example.taskmanagementsystem.web.controllers;

import com.example.taskmanagementsystem.AbstractTest;
import com.example.taskmanagementsystem.dto.RefreshTokenDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

public class TaskControllerTest extends AbstractTest {
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "admin@usa.net", password = "12345")
    public void whenAdminRequestCreateTaskAndLeaveComment_thenReturnCreated() throws Exception {
        UserDetails userDetails = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

         MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/auth/login")
                        .content("{ \"email\":\"" + userDetails.getUsername()
                                + "\",\"password\":\"" + userDetails.getPassword() + "\" }")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        var token = objectMapper
                .readValue(result.getResponse().getContentAsByteArray(), RefreshTokenDto.class).getAccessToken();

        String taskTitle = objectMapper.readTree(mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/tasks")
                        .header("Authorization", "Bearer " + token)
                        .content("{\"title\":\"title_1\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn().getResponse().getContentAsString()).get("title").asText();

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api//comments")
                        .content("{\"task_title\":\"" + taskTitle + "\", \"text\":\"Comment successfully added!\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    @WithMockUser(username = "user@usa.net", password = "54321")
    public void whenUserRequestCreateTask_thenReturnForbidden() throws Exception {
        UserDetails userDetails = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        MvcResult result =mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/auth/login")
                        .content("{ \"email\":\"" + userDetails.getUsername()
                                + "\",\"password\":\"" + userDetails.getPassword() + "\" }")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        var token = objectMapper
                .readValue(result.getResponse().getContentAsByteArray(), RefreshTokenDto.class).getAccessToken();

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/tasks")
                        .header("Authorization", "Bearer " + token)
                        .content("{\"title\":\"title_1\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }
}
