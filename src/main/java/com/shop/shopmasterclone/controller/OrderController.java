package com.shop.shopmasterclone.controller;

import com.shop.shopmasterclone.dto.OrderHistDto;
import com.shop.shopmasterclone.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;

/**
 * 주문과 관련된 웹 요청을 처리하는 컨트롤러입니다.
 * 사용자의 주문 내역 조회와 주문 취소 기능을 제공합니다.
 */
@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * 주문 취소 요청을 처리합니다.
     * 사용자가 자신의 주문을 취소할 권한이 있는지 검증한 후, 주문 취소를 진행합니다.
     *
     * @param orderId 취소할 주문의 식별자입니다.
     * @param principal 현재 인증된 사용자의 정보를 담고 있는 Principal 객체입니다.
     * @return 주문 취소가 성공적으로 완료되면 주문 ID와 함께 OK 상태를,
     *         권한이 없는 경우 "주문 취소 권한이 없습니다." 메시지와 함께 FORBIDDEN 상태를 반환합니다.
     */
    @PostMapping("/order/{orderId}/cancel")
    public @ResponseBody ResponseEntity cancelOrder(
            @PathVariable("orderId") Long orderId,
            Principal principal
    ){
        if (!orderService.validateOrder(orderId, principal.getName())){
            return new ResponseEntity<String>("주문 취소 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }

        orderService.cancelOrder(orderId);
        return new ResponseEntity<Long>(orderId, HttpStatus.OK);
    }

    /**
     * 사용자의 주문 내역을 조회하여 페이지 단위로 반환합니다.
     *
     * @param page 요청받은 페이지 번호입니다. 페이지 번호가 주어지지 않은 경우 기본값으로 0을 사용합니다.
     * @param principal 현재 인증된 사용자의 정보를 담고 있는 Principal 객체입니다.
     * @param model 뷰에 전달할 데이터를 담고 있는 Model 객체입니다.
     * @return "order/orderHist" 뷰 이름과 함께, 주문 내역과 페이징 정보를 Model에 추가하여 반환합니다.
     */
    @GetMapping(value = "/orders")
    public String orderHist(
            @RequestParam("page") Optional<Integer> page,
            Principal principal,
            Model model
    ){
        Pageable pageable = PageRequest.of(page.orElse(0), 4);
        Page<OrderHistDto> orderHistDtoList = orderService.getOrderList(principal.getName(), pageable);

        model.addAttribute("orders", orderHistDtoList);
        model.addAttribute("page", pageable.getPageNumber());
        model.addAttribute("maxPage", 5);
        return "order/orderHist";
    }
}
