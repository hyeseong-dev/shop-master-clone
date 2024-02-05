package com.shop.shopmasterclone.entity;

import com.shop.shopmasterclone.constant.ItemSellStatus;
import com.shop.shopmasterclone.dto.ItemFormDto;
import com.shop.shopmasterclone.exception.OutofStockException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 상품 업데이트와 재고 감소 기능의 동작을 명확하게 설명합니다.
 */
class ItemTest {

    @Test
    @DisplayName("상품 정보가 업데이트되면, 새로운 정보로 정확히 반영되어야 한다")
    void givenItemDetails_whenUpdatingItem_thenItemDetailsAreUpdatedCorrectly() {
        // Given: 상품 정보를 준비한다
        Item item = new Item();
        ItemFormDto itemFormDto = new ItemFormDto();
        itemFormDto.setItemNm("새로운 상품");
        itemFormDto.setPrice(20000);
        itemFormDto.setStockNumber(50);
        itemFormDto.setItemDetail("새로운 상품 상세 설명");
        itemFormDto.setItemSellStatus(ItemSellStatus.SELL);

        // When: 상품 정보를 업데이트한다
        item.updateItem(itemFormDto);

        // Then: 업데이트된 상품 정보가 정확히 반영되어야 한다
        assertEquals(itemFormDto.getItemNm(), item.getItemNm());
        assertEquals(itemFormDto.getPrice(), item.getPrice());
        assertEquals(itemFormDto.getStockNumber(), item.getStockNumber());
        assertEquals(itemFormDto.getItemDetail(), item.getItemDetail());
        assertEquals(itemFormDto.getItemSellStatus(), item.getItemSellStatus());
    }

    @Test
    @DisplayName("재고가 부족할 경우, OutofStockException 예외가 발생해야 한다")
    void givenInsufficientStock_whenRemovingStock_thenThrowsOutofStockException() {
        // Given: 초기 재고를 설정한다
        Item item = new Item();
        item.setStockNumber(100); // 초기 재고 설정

        // When
        item.removeStock(10); // 재고 10 감소

        // Then
        assertEquals(90, item.getStockNumber());
        // 재고 부족 시 예외 발생 검증
        Exception exception = assertThrows(OutofStockException.class, () -> item.removeStock(100),
                "재고가 부족할 때는 OutofStockException이 발생해야 합니다.");
        assertTrue(exception.getMessage().contains("재고가 부족합니다."));
    }
}