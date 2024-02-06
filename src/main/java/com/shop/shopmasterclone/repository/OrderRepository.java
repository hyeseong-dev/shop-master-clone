package com.shop.shopmasterclone.repository;

import com.shop.shopmasterclone.entity.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {


    /**
     * 사용자 이메일에 해당하는 주문 목록을 조회합니다.
     * 주문은 주문 날짜의 내림차순으로 정렬됩니다.
     *
     * @param email 조회할 사용자의 이메일 주소입니다.
     * @param pageable 페이징 처리 정보를 담고 있는 Pageable 객체입니다.
     * @return 조회된 주문 목록을 반환합니다. 페이징 처리가 적용됩니다.
     */
    @Query("select o from Order o  where o.member.email = :email order by o.orderDate desc")
    List<Order> findOrders(@Param("email") String email, Pageable pageable);

    /**
     * 사용자 이메일에 해당하는 주문의 총 개수를 조회합니다.
     *
     * @param email 조회할 사용자의 이메일 주소입니다.
     * @return 해당 사용자의 주문 총 개수를 반환합니다.
     */
    @Query("select count(o) from Order o where o.member.email = :email")
    Long countOrder(@Param("email") String email);

    /**
     * 사용자 이메일을 기반으로 주문 목록을 조회하고, 주문 날짜의 내림차순으로 정렬하여 반환합니다.
     *
     * @param email 조회할 사용자의 이메일 주소입니다.
     * @param pageable 페이징 처리 정보를 담고 있는 Pageable 객체입니다.
     * @return 조회된 주문 목록을 반환합니다. 페이징 처리가 적용됩니다.
     */
    List<Order> findByMemberEmailOrderByOrderDateDesc(String email, Pageable pageable);

    /**
     * 사용자 이메일에 해당하는 주문의 총 개수를 조회합니다.
     *
     * @param email 조회할 사용자의 이메일 주소입니다.
     * @return 해당 사용자의 주문 총 개수를 반환합니다.
     */
    Long countByMemberEmail(String email);

}
