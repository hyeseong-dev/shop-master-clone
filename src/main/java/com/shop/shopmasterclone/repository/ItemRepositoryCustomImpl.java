package com.shop.shopmasterclone.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.shopmasterclone.constant.ItemSellStatus;
import com.shop.shopmasterclone.dto.ItemSearchDto;
import com.shop.shopmasterclone.dto.MainItemDto;
import com.shop.shopmasterclone.dto.QMainItemDto;
import com.shop.shopmasterclone.entity.Item;
import com.shop.shopmasterclone.entity.QItem;
import com.shop.shopmasterclone.entity.QItemImg;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 상품 정보에 대한 커스텀 쿼리 메서드를 제공하는 구현 클래스입니다.
 * {@link com.querydsl.jpa.impl.JPAQueryFactory}를 사용하여 구현되었으며,
 * {@link jakarta.persistence.EntityManager}를 통해 JPA 쿼리를 생성합니다.
 */
public class ItemRepositoryCustomImpl implements ItemRepositoryCustom{

    private JPAQueryFactory queryFactory;

    /**
     * 생성자를 통해 {@link EntityManager}를 받아 {@link JPAQueryFactory} 인스턴스를 초기화합니다.
     *
     * @param entityManager JPA 엔티티 관리자
     */
    public ItemRepositoryCustomImpl(EntityManager entityManager){
        this.queryFactory=new JPAQueryFactory(entityManager);
    }

    /**
     * 판매 상태에 따른 검색 조건을 반환합니다.
     *
     * @param searchSellStatus 검색하려는 상품의 판매 상태
     * @return 판매 상태에 해당하는 BooleanExpression 조건, 검색 조건이 null일 경우 null 반환
     */
    private BooleanExpression searchSellStatusEq(ItemSellStatus searchSellStatus){
        return searchSellStatus == null ? null : QItem.item.itemSellStatus.eq(searchSellStatus);
    }

    /**
     * 등록 날짜 기준으로 검색 조건을 반환합니다.
     *
     * @param searchDateType 검색하려는 기간 ('1d', '1w', '1m', '6m')
     * @return 등록 날짜 기준 검색 조건, 검색 기간이 'all'이거나 null일 경우 null 반환
     * @throws IllegalArgumentException 유효하지 않은 검색 기간이 입력될 경우 예외 발생
     */
    private BooleanExpression regDtsAfter(String searchDateType){

        if (StringUtils.equals("all", searchDateType) || searchDateType == null) {
            return null;
        }

        LocalDateTime dateTime = LocalDateTime.now();

        dateTime = switch (searchDateType) {
            case "1d" -> dateTime.minusDays(1);
            case "1w" -> dateTime.minusWeeks(1);
            case "1m" -> dateTime.minusMonths(1);
            case "6m" -> dateTime.minusMonths(6);
            default -> throw new IllegalArgumentException("Invalid searchDateType: " + searchDateType);
        };

        return QItem.item.regTime.after(dateTime);
    }

    /**
     * 검색어에 따른 검색 조건을 반환합니다.
     *
     * @param searchBy 검색 필드 ('itemNm', 'createdBy')
     * @param searchQuery 사용자가 입력한 검색어
     * @return 검색 필드와 검색어에 해당하는 BooleanExpression 조건, 검색 필드 또는 검색어가 null일 경우 null 반환
     */
    private BooleanExpression searchByLike(String searchBy, String searchQuery){
        if ("itemNm".equals(searchBy)) {
            return QItem.item.itemNm.like("%" + searchQuery + "%");
        } else if ("createdBy".equals(searchBy)) {
            return QItem.item.createdBy.like("%" + searchQuery + "%");
        }
        return null;
    }

    /**
     * 관리자 페이지용 상품 페이지 조회 기능을 제공합니다.
     *
     * @param itemSearchDto 상품 검색 조건을 담은 DTO
     * @param pageable 페이징 정보
     * @return 검색 조건과 일치하는 상품의 페이지 객체
     */
    @Override
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        List<Item> content = queryFactory
                .selectFrom(QItem.item)
                .where(regDtsAfter(itemSearchDto.getSearchDateType()),
                        searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
                        searchByLike(itemSearchDto.getSearchBy(), itemSearchDto.getSearchQuery()))
                .orderBy(QItem.item.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(content, pageable, content.size());
    }

    /**
     * 상품명을 포함하는 검색 조건을 생성합니다.
     * 이 메서드는 사용자가 입력한 검색어를 포함하는 상품명에 대해 검색할 때 사용되며,
     * Querydsl의 like 연산자를 활용하여 구현됩니다.
     *
     * @param searchQuery 사용자가 입력한 검색어. 상품명에 이 문자열이 포함되어 있는 상품을 검색합니다.
     *                    검색어는 부분 일치를 사용하여, 해당 문자열을 포함하는 모든 상품명이 대상이 됩니다.
     * @return 상품명에 입력된 검색어를 포함하는 조건을 나타내는 BooleanExpression.
     *         검색어가 비어 있거나 null일 경우, null을 반환하여 검색 조건에서 제외됩니다.
     */
    private BooleanExpression itemNmLike(String searchQuery){
        return StringUtils.isEmpty(searchQuery) ? null : QItem.item.itemNm.like("%"+searchQuery+"%");
    }

    /**
     * 메인 페이지용 상품 DTO 페이지 조회 기능을 제공합니다.
     *
     * @param itemSearchDto 상품 검색 조건을 담은 DTO
     * @param pageable 페이징 정보
     * @return 검색 조건과 일치하는 MainItemDto의 페이지 객체
     */
    @Override
    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        // QItem과 QItemImg의 인스턴스를 생성합니다. Querydsl을 사용하여 쿼리를 타입 안전하게 구성합니다
        QItem item = QItem.item;
        QItemImg itemImg = QItemImg.itemImg;

        // 메인 페이지에 표시될 상품 목록을 조회합니다.
        List<MainItemDto> mainItemDtoList = queryFactory
                .select(
                        // DTO 생성자를 사용하여 조회 결과를 MainItemDto 타입으로 프로젝션합니다.
                        // 이때, 상품의 기본 정보 및 대표 이미지 URL, 가격 정보를 선택합니다.
                        new QMainItemDto(
                                item.id,
                                item.itemNm,
                                item.itemDetail,
                                itemImg.imgUrl,
                                item.price)
                                )
                .from(itemImg)                                      // 대표 이미지를 가진 상품 이미지에서 시작하는 쿼리입니다.
                .join(itemImg.item, item)                           // 상품 이미지와 상품 엔티티를 조인합니다. 이때, itemImg의 item 속성과 item 엔티티를 연결합니다.
                .where(itemImg.repimgYn.eq("Y"))               //대표 이미지 여부가 'Y'인 항목만 필터링합니다.
                .where(itemNmLike(itemSearchDto.getSearchQuery())) // 사용자가 입력한 검색어로 상품명을 필터링합니다. itemNmLike 메서드는 해당 검색어를 포함하는 상품명에 대한 조건을 생성합니다.
                .orderBy(item.id.desc())                            // 상품 ID를 기준으로 내림차순으로 정렬합니다. 새로 등록된 상품이 먼저 나타나도록 합니다.
                .offset(pageable.getOffset())                       // 페이징 처리를 위해, 건너뛸 레코드 수를 지정합니다.
                .limit(pageable.getPageSize())                      // 한 페이지에 표시할 레코드 수를 제한합니다.
                .fetch();                                           // 쿼리를 실행하고 결과를 리스트로 가져옵니다.

        // 조회된 상품 목록, 페이징 정보, 전체 항목 수를 이용하여 Page 객체를 생성하고 반환합니다.
        // 이때, 전체 항목 수는 mainItemDtoList의 크기를 사용합니다. 실제 애플리케이션에서는 전체 항목 수를 정확히 계산하기 위해 별도의 쿼리를 실행할 수 있습니다.
        return new PageImpl<>(mainItemDtoList, pageable, mainItemDtoList.size());
    }
}
