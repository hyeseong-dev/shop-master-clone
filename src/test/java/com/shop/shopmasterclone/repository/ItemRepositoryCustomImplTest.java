package com.shop.shopmasterclone.repository;

import com.shop.shopmasterclone.constant.ItemSellStatus;
import com.shop.shopmasterclone.dto.ItemSearchDto;
import com.shop.shopmasterclone.entity.Item;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(locations="classpath:application-test.properties")
public class ItemRepositoryCustomImplTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private ItemRepository itemRepository;

    @BeforeEach
    void setUp() {
        itemRepository.deleteAll();
    }

    @Test
    @DisplayName("Given 상품 검색 조건 When 관리자 페이지에서 상품을 조회 Then 조건에 맞는 상품만 표시되어야 함")
    public void given_SearchConditions_when_AdminSearchesItems_then_OnlyMatchingItemsShouldBeDisplayed() {
        // Given: 상품 검색 조건을 설정
        Item item = new Item();
        item.setItemNm("Test Item");
        item.setPrice(10000);
        item.setItemDetail("Test Item Detail");
        item.setItemSellStatus(ItemSellStatus.SELL);
        itemRepository.save(item);

        ItemSearchDto itemSearchDto = new ItemSearchDto();
        itemSearchDto.setSearchDateType("all"); // 모든 날짜
        itemSearchDto.setSearchSellStatus(ItemSellStatus.SELL); // 판매 중인 상품
        itemSearchDto.setSearchBy("itemNm"); // 상품명으로 검색
        itemSearchDto.setSearchQuery("Test"); // 'Test'를 포함하는 상품명

        // When: 관리자 페이지에서 상품을 조회
        Page<Item> resultPage = itemRepository.getAdminItemPage(itemSearchDto, PageRequest.of(0, 10));

        // Then: 조건에 맞는 상품만 표시되어야 함
        assertThat(resultPage.getContent()).hasSize(1);
        assertThat(resultPage.getContent().get(0).getItemNm()).isEqualTo("Test Item");
    }
}