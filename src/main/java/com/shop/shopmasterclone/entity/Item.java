package com.shop.shopmasterclone.entity;

import com.shop.shopmasterclone.constant.ItemSellStatus;
import com.shop.shopmasterclone.dto.ItemFormDto;
import com.shop.shopmasterclone.exception.OutofStockException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 상품 정보를 나타내는 엔티티 클래스입니다.
 * 이 클래스는 상품의 기본적인 정보와 상태를 관리합니다.
 *
 * @see BaseEntity 상품 엔티티는 기본 엔티티 상속을 통해 공통 속성을 가집니다.
 */
@Entity
@Table(name="item")
@Getter
@Setter
@ToString
public class Item extends BaseEntity {

    /**
     * 상품의 고유 식별자입니다. 데이터베이스에서 자동으로 생성됩니다.
     */
    @Id
    @Column(name = "item_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;       //상품 코드

    /**
     * 상품명을 나타냅니다. null 값을 허용하지 않으며, 최대 길이는 50자입니다.
     */
    @Column(nullable = false, length = 50)
    private String itemNm; //상품명

    /**
     * 상품 가격을 나타냅니다. null 값을 허용하지 않습니다.
     */
    @Column(name = "price", nullable = false)
    private int price; //가격

    /**
     * 상품의 재고 수량을 나타냅니다. null 값을 허용하지 않습니다.
     */
    @Column(nullable = false)
    private int stockNumber; //재고수량

    /**
     * 상품의 상세 설명을 나타냅니다. 긴 텍스트를 저장할 수 있도록 Lob 어노테이션이 사용됩니다.
     */
    @Lob
    @Column(nullable = false)
    private String itemDetail; //상품 상세 설명

    /**
     * 상품의 판매 상태를 나타냅니다. 판매 중, 품절 등의 상태를 관리합니다.
     */
    @Enumerated(EnumType.STRING)
    private ItemSellStatus itemSellStatus; //상품 판매 상태

    /**
     * 상품 정보를 업데이트합니다. 상품 정보 입력 폼으로부터 받은 데이터로 상품 정보를 갱신합니다.
     *
     * @param itemFormDto 상품 정보를 담고 있는 DTO 객체입니다.
     */
    public void updateItem(ItemFormDto itemFormDto){
        this.itemNm = itemFormDto.getItemNm();
        this.price = itemFormDto.getPrice();
        this.stockNumber = itemFormDto.getStockNumber();
        this.itemDetail = itemFormDto.getItemDetail();
        this.itemSellStatus = itemFormDto.getItemSellStatus();
    }

    /**
     * 상품 재고를 감소시킵니다. 주문 수량만큼 재고를 감소시키며, 재고가 부족한 경우 예외를 발생시킵니다.
     *
     * @param stockNumber 감소시킬 재고의 수량입니다.
     * @throws OutofStockException 재고가 주문 수량보다 적을 때 발생하는 예외입니다.
     */
    public void removeStock(int stockNumber){
        int restStock = this.stockNumber - stockNumber;
        if(restStock < 0){
            throw new OutofStockException("상품의 재고가 부족합니다. (현재 재고 수량: " + this.stockNumber + ")");
        }
        this.stockNumber = restStock;
    }
}