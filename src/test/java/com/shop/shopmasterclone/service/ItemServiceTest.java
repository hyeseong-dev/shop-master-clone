package com.shop.shopmasterclone.service;

import com.shop.shopmasterclone.constant.ItemSellStatus;
import com.shop.shopmasterclone.dto.ItemFormDto;
import com.shop.shopmasterclone.entity.Item;
import com.shop.shopmasterclone.entity.ItemImg;
import com.shop.shopmasterclone.repository.ItemImgRepository;
import com.shop.shopmasterclone.repository.ItemRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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