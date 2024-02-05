package com.shop.shopmasterclone.controller;

import com.shop.shopmasterclone.dto.ItemFormDto;
import com.shop.shopmasterclone.dto.ItemImgDto;
import com.shop.shopmasterclone.dto.ItemSearchDto;
import com.shop.shopmasterclone.entity.Item;
import com.shop.shopmasterclone.service.ItemService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations="classpath:application-test.properties")
class ItemControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private ItemService itemService; // ItemService 모의 객체 주입

    /**
     * 상품 상세 정보 페이지 조회를 테스트합니다.
     * 관리자 권한으로 상품 상세 페이지에 접근할 때 올바른 모델이 뷰에 전달되는지 검증합니다.
     */
    @Test
    @DisplayName("상품 상세 정보 페이지 조회 테스트")
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void itemDetailPageTest() throws Exception {
        Long testItemId = 1L; // 테스트할 상품 ID
        ItemFormDto mockItemFormDto = new ItemFormDto(); // 모의 ItemFormDto 객체 생성

        // ItemService의 getItemDtl 메서드 호출 시 모의 객체를 반환하도록 설정
        Mockito.when(itemService.getItemDtl(testItemId)).thenReturn(mockItemFormDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/admin/item/{itemId}", testItemId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attributeExists("itemFormDto"))
                .andExpect(MockMvcResultMatchers.view().name("item/itemForm"));
    }

    @Test
    @DisplayName("일반 사용자 상품 상세 정보 페이지 접근 테스트")
    @WithMockUser(username = "user", roles = "USER")
    public void itemDetailForUserAccessTest() throws Exception {
        Long testItemId = 1L; // 테스트할 상품 ID

        // ItemFormDto 객체와 ItemImgDto 객체 설정
        ItemFormDto mockItemFormDto = new ItemFormDto();
        ItemImgDto itemImgDto = new ItemImgDto();
        itemImgDto.setImgUrl("testImageUrl.jpg"); // 테스트 이미지 URL
        mockItemFormDto.setItemImgDtoList(Arrays.asList(itemImgDto));

        // ItemService.getItemDtl 메서드가 mockItemFormDto를 반환하도록 설정
        Mockito.when(itemService.getItemDtl(testItemId)).thenReturn(mockItemFormDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/item/{itemId}", testItemId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attributeExists("item"))
                .andExpect(MockMvcResultMatchers.view().name("item/itemDtl"));
    }

    /**
     * 관리자가 상품 관리 페이지에 접근하여 페이징이 올바르게 작동하는지 테스트합니다.
     */
    @Test
    @DisplayName("관리자 상품 관리 페이지 페이징 조회 테스트")
    @WithMockUser(username="admin", roles="ADMIN")
    public void givenAdminAccessAndPageNumber_whenViewingItemManagePage_thenReturnsCorrectPage() throws Exception {
        int testPage = 0;
        List<Item> itemList = new ArrayList<>();
        Page<Item> itemsPage = new PageImpl<>(itemList, PageRequest.of(testPage, 10), itemList.size());

        // ItemService의 getAdminItemPage 메서드 호출 시 모의 Page<Item> 객체를 반환하도록 설정
        Mockito.when(itemService.getAdminItemPage(Mockito.any(ItemSearchDto.class), Mockito.any(PageRequest.class)))
                .thenReturn(itemsPage);

        mockMvc.perform(MockMvcRequestBuilders.get("/admin/items").param("page", String.valueOf(testPage)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attributeExists("items"))
                .andExpect(MockMvcResultMatchers.view().name("item/itemMng"));
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