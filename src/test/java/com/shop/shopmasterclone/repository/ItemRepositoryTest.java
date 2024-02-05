package com.shop.shopmasterclone.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.shopmasterclone.constant.ItemSellStatus;
import com.shop.shopmasterclone.dto.ItemSearchDto;
import com.shop.shopmasterclone.dto.MainItemDto;
import com.shop.shopmasterclone.entity.Item;
import com.shop.shopmasterclone.entity.ItemImg;
import com.shop.shopmasterclone.entity.QItem;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;
import org.thymeleaf.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@TestPropertySource(locations="classpath:application-test.properties")
class ItemRepositoryTest {

    @Autowired
    ItemImgRepository itemImgRepository;

    @Autowired
    ItemRepository itemRepository;

    @PersistenceContext
    EntityManager em;

    // 이 메서드는 ItemRepositoryTest 클래스 내부 또는 적절한 테스트 설정 클래스 내부에 위치해야 합니다.
    public void createTestItemsWithImages() {
        LocalDateTime now = LocalDateTime.now();
        for (int i = 1; i <= 5; i++) {
            // Item 엔티티 생성
            Item item = new Item();
            item.setItemNm("테스트 상품 " + i);
            item.setPrice(10000 + i * 100);
            item.setItemDetail("테스트 상세 설명 " + i);
            item.setItemSellStatus(ItemSellStatus.SELL);
            item.setStockNumber(100 - i);
            item.setRegTime(now.minusDays(i));
            item.setUpdateTime(now);
            Item savedItem = itemRepository.save(item);

            // ItemImg 엔티티 생성
            ItemImg itemImg = new ItemImg();
            itemImg.setItem(savedItem); // Item 엔티티와 연결
            itemImg.setImgUrl("/images/item" + i + ".jpg"); // 가상의 이미지 URL 설정
            itemImg.setRepimgYn("Y"); // 대표 이미지로 설정
            itemImgRepository.save(itemImg); // ItemImg 엔티티 저장
        }
    }

    @Test
    @DisplayName("메인 페이지 상품 조회 테스트")
    public void given_ItemNm_when_SearchesItemNm_then_MachedItemsShouldBeDisplayed(){
        // 테스트 데이터 준비
        // 상품 정보를 생성하고 저장하는 로직을 호출
        this.createTestItemsWithImages(); // createTestItemsWithImages() 메서드는 상품 정보를 여러 개 생성하여 저장하는 로직을 포함해야 합니다.

        // 검색 조건 설정
        ItemSearchDto itemSearchDto = new ItemSearchDto();
        itemSearchDto.setSearchDateType("all"); // 검색 날짜 타입 (예: 'all', '1d', '1w' 등)
        itemSearchDto.setSearchSellStatus(ItemSellStatus.SELL); // 판매 상태 (예: 판매 중)
        itemSearchDto.setSearchBy("itemNm"); // 검색 기준 (예: 상품명)
        itemSearchDto.setSearchQuery("테스트 상품"); // 검색 쿼리 (예: '테스트 상품'을 포함하는 상품명)

        // Pageable 설정
        Pageable pageable = PageRequest.of(0, 5); // 첫 번째 페이지, 페이지 당 5개 항목

        // 테스트 실행
        Page<MainItemDto> resultPage = itemRepository.getMainItemPage(itemSearchDto, pageable);

        // 검증
        // 조회된 상품 목록이 비어있지 않고, 설정한 페이지 크기와 일치하는지 확인
        assertThat(resultPage.getContent()).isNotEmpty();
        assertThat(resultPage.getSize()).isEqualTo(5);
        // 반환된 상품 목록의 첫 번째 상품이 기대하는 상품명을 포함하는지 확인
        assertThat(resultPage.getContent().get(0).getItemNm()).contains("테스트 상품");
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

    @Test
    @DisplayName("상품 저장 테스트")
    public void createItemTest(){
        Item item = new Item();
        item.setItemNm("테스트 상품");
        item.setPrice(10000);
        item.setItemDetail("테스트 상품 상세 설명");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);
        item.setRegTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());
        Item savedItem = itemRepository.save(item);
        System.out.println(savedItem.toString());
    }

    public void createItemList(){
        for(int i=1;i<=10;i++){
            Item item = new Item();
            item.setItemNm("테스트 상품" + i);
            item.setPrice(10000 + i);
            item.setItemDetail("테스트 상품 상세 설명" + i);
            item.setItemSellStatus(ItemSellStatus.SELL);
            item.setStockNumber(100); item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            Item savedItem = itemRepository.save(item);
        }
    }

    @Test
    @DisplayName("상품명 조회 테스트")
    public void findByItemNmTest(){
        this.createItemList();
        List<Item> itemList = itemRepository.findByItemNm("테스트 상품1");
        for(Item item : itemList){
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("상품명, 상품상세설명 or 테스트")
    public void findByItemNmOrItemDetailTest(){
        this.createItemList();
        List<Item> itemList = itemRepository.findByItemNmOrItemDetail("테스트 상품1", "테스트 상품 상세 설명5");
        for(Item item : itemList){
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("가격 LessThan 테스트")
    public void findByPriceLessThanTest(){
        this.createItemList();
        List<Item> itemList = itemRepository.findByPriceLessThan(10005);
        for(Item item : itemList){
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("가격 내림차순 조회 테스트")
    public void findByPriceLessThanOrderByPriceDesc(){
        this.createItemList();
        List<Item> itemList = itemRepository.findByPriceLessThanOrderByPriceDesc(10005);
        for(Item item : itemList){
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("@Query를 이용한 상품 조회 테스트")
    public void findByItemDetailTest(){
        this.createItemList();
        List<Item> itemList = itemRepository.findByItemDetail("테스트 상품 상세 설명");
        for(Item item : itemList){
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("Querydsl 조회 테스트1")
    public void queryDslTest(){
        this.createItemList();
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QItem qItem = QItem.item;
        JPAQuery<Item> query  = queryFactory.selectFrom(qItem)
                .where(qItem.itemSellStatus.eq(ItemSellStatus.SELL))
                .where(qItem.itemDetail.like("%" + "테스트 상품 상세 설명" + "%"))
                .orderBy(qItem.price.desc());

        List<Item> itemList = query.fetch();

        for(Item item : itemList){
            System.out.println(item.toString());
        }
    }

    public void createItemList2(){
        for(int i=1;i<=5;i++){
            Item item = new Item();
            item.setItemNm("테스트 상품" + i);
            item.setPrice(10000 + i);
            item.setItemDetail("테스트 상품 상세 설명" + i);
            item.setItemSellStatus(ItemSellStatus.SELL);
            item.setStockNumber(100);
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            itemRepository.save(item);
        }

        for(int i=6;i<=10;i++){
            Item item = new Item();
            item.setItemNm("테스트 상품" + i);
            item.setPrice(10000 + i);
            item.setItemDetail("테스트 상품 상세 설명" + i);
            item.setItemSellStatus(ItemSellStatus.SOLD_OUT);
            item.setStockNumber(0);
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            itemRepository.save(item);
        }
    }

    @Test
    @DisplayName("상품 Querydsl 조회 테스트 2")
    public void queryDslTest2(){

        this.createItemList2();

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QItem item = QItem.item;
        String itemDetail = "테스트 상품 상세 설명";
        int price = 10003;
        String itemSellStat = "SELL";

        booleanBuilder.and(item.itemDetail.like("%" + itemDetail + "%"));
        booleanBuilder.and(item.price.gt(price));
        System.out.println(ItemSellStatus.SELL);
        if(StringUtils.equals(itemSellStat, ItemSellStatus.SELL)){
            booleanBuilder.and(item.itemSellStatus.eq(ItemSellStatus.SELL));
        }

        Pageable pageable = PageRequest.of(0, 5);
        Page<Item> itemPagingResult = itemRepository.findAll(booleanBuilder, pageable);
        System.out.println("total elements : " + itemPagingResult. getTotalElements ());

        List<Item> resultItemList = itemPagingResult.getContent();
        for(Item resultItem: resultItemList){
            System.out.println(resultItem.toString());
        }


    }

}