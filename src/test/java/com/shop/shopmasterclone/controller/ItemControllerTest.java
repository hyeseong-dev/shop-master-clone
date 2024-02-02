package com.shop.shopmasterclone.controller;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations="classpath:application-test.properties")
class ItemControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("관리자 상품 관리 페이지 페이징 조회 테스트")
    @WithMockUser(username="admin", roles="ADMIN")
    public void givenAdminAccessAndPageNumber_whenViewingItemManagePage_thenReturnsCorrectPage() throws Exception {
        int testPage = 1; // 테스트할 페이지 번호
        mockMvc.perform(MockMvcRequestBuilders.get("/admin/items")
                        .param("page", String.valueOf(testPage)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attributeExists("items"))
                .andExpect(MockMvcResultMatchers.view().name("item/itemMng"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("상품 등록 페이지 권한 테스트")
    @WithMockUser(username="admin", roles="ADMIN")
    public void itemFormTest() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.get("/admin/item/new"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("상품 등록 페이지 일반 회원 접근 권한 테스트")
    @WithMockUser(username="user", roles="USER")
    public void itemFormNotAdmin() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.get("/admin/item/new"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }
}