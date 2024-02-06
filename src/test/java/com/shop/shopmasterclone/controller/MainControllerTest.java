package com.shop.shopmasterclone.controller;

import com.shop.shopmasterclone.dto.ItemSearchDto;
import com.shop.shopmasterclone.dto.MainItemDto;
import com.shop.shopmasterclone.service.ItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

import static org.mockito.ArgumentMatchers.any;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;

@WebMvcTest(MainController.class)
@TestPropertySource(locations="classpath:application-test.properties")
class MainControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        given(itemService.getMainItemPage(null, PageRequest.of(0, 6)))
                .willReturn(new PageImpl<>(Collections.emptyList())); // Ensure this mock returns as expected
    }

    @Test
    @DisplayName("메인 페이지 로딩 테스트")
    void mainPageLoadingTest() throws Exception {
        // Given
        MainItemDto mainItemDto = new MainItemDto(
                1L,
                "테스트 상품",
                "테스트 상품 상세 설명",
                "test.jpg",
                10000
        );

        Page<MainItemDto> itemsPage = new PageImpl<>(Collections.singletonList(mainItemDto),
                PageRequest.of(0, 6), 1);

        given(itemService.getMainItemPage(any(ItemSearchDto.class), any(Pageable.class)))
                .willReturn(itemsPage);

        // when
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("main"))
                .andExpect(model().attributeExists("items"))
                .andExpect(model().attributeExists("itemSearchDto"))
                .andExpect(model().attributeExists("maxPage"))
                // Directly asserting the size of 'items' content is not straightforward in MockMvc
                .andExpect(model().attribute("items", hasItem(
                        allOf(
                                hasProperty("id", is(mainItemDto.getId())),
                                hasProperty("itemNm", is(mainItemDto.getItemNm())),
                                hasProperty("itemDetail", is(mainItemDto.getItemDetail())),
                                hasProperty("price", is(mainItemDto.getPrice())),
                                hasProperty("imgUrl", is(mainItemDto.getImgUrl()))
                        )
                )));
        // then (additional assertions if necessary)
    }
}