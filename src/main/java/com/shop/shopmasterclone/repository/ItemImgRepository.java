package com.shop.shopmasterclone.repository;

import com.shop.shopmasterclone.entity.ItemImg;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 상품 이미지 데이터에 접근하기 위한 JPA 리포지토리 인터페이스입니다.
 * 이 인터페이스는 상품 이미지 엔티티({@link ItemImg})의 영속성을 관리하며,
 * 상품 ID를 기준으로 상품 이미지를 조회하는 기능을 제공합니다.
 */
public interface ItemImgRepository extends JpaRepository<ItemImg, Long> {

    /**
     * 주어진 상품 ID에 해당하는 모든 상품 이미지를 ID의 오름차순으로 조회합니다.
     *
     * @param itemId 상품 이미지를 조회할 상품의 ID입니다.
     * @return 상품 ID에 해당하는 상품 이미지 목록을 ID 오름차순으로 반환합니다.
     */
    List<ItemImg> findByItemIdOrderByIdAsc(Long itemId);

    /**
     * 주어진 상품 ID와 대표 이미지 여부를 기준으로 상품 이미지를 조회합니다.
     * 대표 이미지 여부는 'Y' 또는 'N'의 문자열로 표현됩니다.
     *
     * @param itemId 상품 이미지를 조회할 상품의 ID입니다.
     * @param repimgYn 대표 이미지 여부를 나타내는 문자열입니다. ('Y' 또는 'N')
     * @return 조건에 맞는 상품 이미지를 반환합니다. 해당하는 이미지가 없는 경우 null을 반환할 수 있습니다.
     */
    ItemImg findByItemIdAndRepimgYn(Long itemId, String repimgYn);
}
