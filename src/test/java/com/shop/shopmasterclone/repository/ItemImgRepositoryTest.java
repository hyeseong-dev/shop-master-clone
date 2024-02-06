package com.shop.shopmasterclone.repository;

import com.shop.shopmasterclone.entity.Item;
import com.shop.shopmasterclone.entity.ItemImg;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
public class ItemImgRepositoryTest {

    @Autowired
    private ItemImgRepository itemImgRepository;

    @Autowired
    private ItemRepository itemRepository;

    private void createTestItemsWithImages() {
        LocalDateTime now = LocalDateTime.now();
        for (int i = 1; i <= 5; i++) {
            Item item = new Item();
            item.setItemNm("테스트 상품 " + i);
            item.setPrice(10000 + i);
            item.setItemDetail("테스트 상세 설명 " + i);
            item.setStockNumber(100 - i);
            item.setRegTime(now.minusDays(i));
            item.setUpdateTime(now);
            item = itemRepository.save(item);

            ItemImg itemImg = new ItemImg();
            itemImg.setItem(item); // Item 엔티티와 연결
            itemImg.setImgName("test" + i + ".jpg");
            itemImg.setOriImgName("original" + i + ".jpg");
            itemImg.setImgUrl("/images/item" + i + ".jpg"); // 가상의 이미지 URL 설정
            itemImg.setRepimgYn(i == 1 ? "Y" : "N"); // 첫 번째 이미지만 대표 이미지로 설정
            itemImgRepository.save(itemImg);
        }
    }

    @Test
    @DisplayName("Given item images, when query by item ID in ascending order, then return images sorted by ID")
    public void givenItemImages_whenQueryByItemIdInAscendingOrder_thenReturnImagesSortedById() {
        // Assuming that createTestItemsWithImages has been called and items with IDs exist
        List<ItemImg> allItemImgs = itemImgRepository.findAll();
        Long itemId = allItemImgs.get(0).getItem().getId(); // Get the ID of the first item

        List<ItemImg> foundItemImgs = itemImgRepository.findByItemIdOrderByIdAsc(itemId);

        assertThat(foundItemImgs).isNotEmpty();
        assertThat(foundItemImgs.size()).isGreaterThanOrEqualTo(1);
        assertThat(foundItemImgs.get(0).getItem().getId()).isEqualTo(itemId);
    }

    @Test
    @DisplayName("Given item images, when query by item ID and representative image flag, then return the representative image")
    public void givenItemImages_whenQueryByItemIdAndRepimgYn_thenReturnRepresentativeImage() {
        // Assuming that createTestItemsWithImages has been called and items with IDs exist
        List<ItemImg> allItemImgs = itemImgRepository.findAll();
        Long itemId = allItemImgs.get(0).getItem().getId(); // Get the ID of the first item

        ItemImg foundRepImg = itemImgRepository.findByItemIdAndRepimgYn(itemId, "Y");

        assertThat(foundRepImg).isNotNull();
        assertThat(foundRepImg.getRepimgYn()).isEqualTo("Y");
        assertThat(foundRepImg.getItem().getId()).isEqualTo(itemId);
    }
}
