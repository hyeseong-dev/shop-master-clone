package com.shop.shopmasterclone.service;

import com.shop.shopmasterclone.constant.ItemSellStatus;
import com.shop.shopmasterclone.dto.ItemFormDto;
import com.shop.shopmasterclone.dto.ItemSearchDto;
import com.shop.shopmasterclone.dto.MainItemDto;
import com.shop.shopmasterclone.entity.Item;
import com.shop.shopmasterclone.entity.ItemImg;
import com.shop.shopmasterclone.repository.ItemImgRepository;
import com.shop.shopmasterclone.repository.ItemRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
class ItemServiceTest {

    @Autowired
    ItemService itemService;
    @Autowired
    ItemRepository itemRepository;

    @Autowired
    ItemImgRepository itemImgRepository;

    /**
     * 메인 페이지 상품 조회 기능에 대한 테스트 메서드입니다.
     * 상품 검색 조건을 주어 관리자 상품 페이지에서 조회할 때 조건에 맞는 상품 페이지가 반환되는지 검증합니다.
     *
     * <p>이 테스트는 상품명으로 검색할 때, 검색 조건에 맞는 상품이 포함된 페이지가 정확하게 반환되는지 확인합니다.
     * 상품 정보와 상품 이미지 파일은 미리 생성하여 데이터베이스에 저장된 상태로 시작합니다.</p>
     *
     * <p>Spring Security를 사용하여 테스트 실행 시 모의 사용자를 인증 정보에 추가합니다.</p>
     *
     * @throws Exception 상품 정보 및 이미지 파일의 저장 과정에서 발생할 수 있는 예외를 처리합니다.
     */
    @Test
    @DisplayName("메인 페이지 상품 조회 테스트")
    @WithMockUser(username = "user", roles = "USER")
    void givenMainPageItemSearchConditions_whenSearchingItems_thenShouldReturnCorrectItemPage() throws Exception {
        // Given: 상품 정보와 이미지 파일 준비 및 저장
        createTestItemsWithImages("테스트 상품 1", ItemSellStatus.SELL, 10);
        createTestItemsWithImages("테스트 상품 2", ItemSellStatus.SELL, 10);

        // 상품 검색 조건 설정
        ItemSearchDto itemSearchDto = new ItemSearchDto();
        itemSearchDto.setSearchBy("itemNm");
        itemSearchDto.setSearchQuery("테스트 상품");

        Pageable pageable = PageRequest.of(0, 5); // 첫 번째 페이지, 페이지 당 5개 항목

        // When: 메인 페이지에서 상품 조회
        Page<MainItemDto> resultPage = itemService.getMainItemPage(itemSearchDto, pageable);

        // Then: 조회된 상품 페이지 검증
        assertNotNull(resultPage, "조회된 상품 페이지는 null이 아니어야 합니다.");
        assertTrue(resultPage.getTotalElements() > 0, "조회된 상품의 총 수가 0보다 커야 합니다.");
        resultPage.getContent().forEach(item -> {
            assertTrue(item.getItemNm().contains(itemSearchDto.getSearchQuery()), "상품 이름에 검색 쿼리가 포함되어야 합니다.");
        });
    }

    /**
     * 테스트를 위한 상품 및 상품 이미지 데이터를 생성하고 저장하는 보조 메서드입니다.
     * 이 메서드는 상품명, 판매 상태, 수량 등의 정보를 받아 상품과 해당 상품의 이미지를 데이터베이스에 저장합니다.
     * 첫 번째 이미지는 대표 이미지로 설정됩니다.
     *
     * <p>이 메서드는 테스트 데이터 준비를 위해 사용되며, 각 테스트 케이스 실행 전에 호출됩니다.</p>
     *
     * @param itemName 상품명입니다. 생성할 상품의 이름을 지정합니다.
     * @param sellStatus 상품의 판매 상태입니다. {@link ItemSellStatus}의 값 중 하나를 사용합니다.
     * @param quantity 상품의 수량입니다. 생성할 상품의 재고 수량을 지정합니다.
     * @throws Exception 상품 정보 및 이미지 파일의 저장 과정에서 발생할 수 있는 예외를 처리합니다.
     */
    private void createTestItemsWithImages(String itemName, ItemSellStatus sellStatus, int quantity) throws Exception {
        ItemFormDto itemFormDto = new ItemFormDto();
        itemFormDto.setItemNm(itemName);
        itemFormDto.setItemDetail(itemName + " 상세 설명");
        itemFormDto.setPrice(10000);
        itemFormDto.setStockNumber(quantity);
        itemFormDto.setItemSellStatus(sellStatus);

        List<MultipartFile> itemImgFileList = new ArrayList<>();
        itemImgFileList.add(new MockMultipartFile("file", "test.jpg", "image/jpeg", "test image content".getBytes()));

        itemService.saveItem(itemFormDto, itemImgFileList);
    }

    @Test
    @DisplayName("상품 검색 조건을 사용하여 관리자 상품 페이지에서 조회 시, 조건에 맞는 상품 페이지가 반환되어야 함")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void GivenSearchConditions_whenAdminViewsItemPage_thenShouldReturnCorrectItemPage() throws Exception {
        // Given: 상품 정보와 이미지 파일 준비 및 저장
        ItemFormDto itemFormDto1 = new ItemFormDto();
        itemFormDto1.setItemNm("테스트상품1");
        itemFormDto1.setItemSellStatus(ItemSellStatus.SELL);
        itemFormDto1.setItemDetail("테스트 상품입니다.");
        itemFormDto1.setPrice(10000);
        itemFormDto1.setStockNumber(100);
        itemService.saveItem(itemFormDto1, createMultipartFiles());

        ItemFormDto itemFormDto2 = new ItemFormDto();
        itemFormDto2.setItemNm("테스트상품2");
        itemFormDto2.setItemSellStatus(ItemSellStatus.SOLD_OUT);
        itemFormDto2.setItemDetail("또 다른 테스트 상품입니다.");
        itemFormDto2.setPrice(20000);
        itemFormDto2.setStockNumber(50);
        itemService.saveItem(itemFormDto2, createMultipartFiles());

        ItemSearchDto itemSearchDto = new ItemSearchDto();
        itemSearchDto.setSearchBy("itemNm");
        itemSearchDto.setSearchQuery("테스트상품");

        // When: 관리자 상품 페이지에서 조회
        Page<Item> itemPage = itemService.getAdminItemPage(itemSearchDto, PageRequest.of(0, 10));

        // Then: 조건에 맞는 상품 페이지가 반환되어야 함
        assertEquals(2, itemPage.getTotalElements(), "조회된 상품의 총 수가 예상과 다릅니다.");

        // 검증: 반환된 페이지의 상품 이름이 검색 조건에 정확히 일치하는지 확인
        itemPage.getContent().forEach(item -> {
            assertTrue(item.getItemNm().contains("테스트상품"), "상품 이름이 검색 조건에 일치해야 합니다.");
        });

        // 검증: 상품의 가격과 재고 수량이 올바르게 설정되었는지 확인
        itemPage.getContent().forEach(item -> {
            assertTrue(item.getPrice() >= 1000, "상품 가격이 설정 조건을 만족해야 합니다.");
            assertTrue(item.getStockNumber() > 0, "상품 재고 수량이 0보다 커야 합니다.");
        });

        // 검증: 상품 상태가 올바르게 설정되었는지 확인 (예: SELL 또는 SOLD_OUT 상태이어야 합니다.)
        itemPage.getContent().forEach(item -> {
            assertTrue(item.getItemSellStatus() == ItemSellStatus.SELL || item.getItemSellStatus() == ItemSellStatus.SOLD_OUT,
                    "상품의 판매 상태가 '판매 중(SELL)' 또는 '판매 완료(SOLD_OUT)'이어야 합니다.");
        });

        // 검증: 페이징 처리가 정상적으로 이루어졌는지 확인 (예: 페이지 사이즈, 페이지 번호)
        assertEquals(0, itemPage.getNumber(), "조회된 페이지 번호가 예상과 다릅니다.");
        assertEquals(10, itemPage.getSize(), "페이지의 크기가 설정한 값과 다릅니다.");

        // 선택적 검증: 첫 번째 상품 이미지가 대표 이미지로 설정되었는지 확인
        itemImgRepository.findByItemIdOrderByIdAsc(itemPage.getContent().get(0).getId()).forEach(itemImg -> {
            if(itemImg.equals(itemImgRepository.findByItemIdOrderByIdAsc(itemPage.getContent().get(0).getId()).get(0))) {
                assertEquals("Y", itemImg.getRepimgYn(), "첫 번째 상품 이미지가 대표 이미지로 설정되어야 합니다.");
            }
        });
    }

    List<MultipartFile> createMultipartFiles() throws Exception {

        List<MultipartFile> multipartFileList = new ArrayList<>();

         for(int i = 0; i<5; i++){
             String path = "C:/shop/item/";
             String imageName = "image" + i + ".jpg";
             MockMultipartFile multipartFile = new MockMultipartFile(path, imageName, "image/jpg", new byte[]{1,2,3,4});
             multipartFileList.add(multipartFile);
         }
         return multipartFileList;
    }

    @Test
    @DisplayName("상품 등록 시 상품 정보와 이미지가 정상적으로 저장되어야 한다")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void GivenItemInfoAndImg_whenRegisteringItem_thenItShouldBeSavedCorrectly() throws Exception {
        // Given: 상품 정보와 이미지 파일 준비
        ItemFormDto itemFormDto = new ItemFormDto();
        itemFormDto.setItemNm("테스트상품");
        itemFormDto.setItemSellStatus(ItemSellStatus.SELL);
        itemFormDto.setItemDetail("테스트 상품입니다.");
        itemFormDto.setPrice(1000);
        itemFormDto.setStockNumber(100);
        List<MultipartFile> multipartFileList = createMultipartFiles();

        // When: 상품 등록 로직 실행
        Long itemId = itemService.saveItem(itemFormDto, multipartFileList);

        // Then: 상품 정보와 이미지가 정상적으로 저장되었는지 검증
        List<ItemImg> itemImgList = itemImgRepository.findByItemIdOrderByIdAsc(itemId);
        Item item = itemRepository.findById(itemId).orElseThrow(EntityNotFoundException::new);

        assertEquals(itemFormDto.getItemNm(), item.getItemNm());
        assertEquals(itemFormDto.getItemSellStatus(), item.getItemSellStatus());
        assertEquals(itemFormDto.getItemDetail(), item.getItemDetail());
        assertEquals(itemFormDto.getPrice(), item.getPrice());
        assertEquals(itemFormDto.getStockNumber(), item.getStockNumber());
        assertEquals(multipartFileList.get(0).getOriginalFilename(), itemImgList.get(0).getOriImgName());
    }
}