package com.shop.shopmasterclone.controller;

import com.shop.shopmasterclone.dto.ItemSearchDto;
import com.shop.shopmasterclone.dto.MainItemDto;
import com.shop.shopmasterclone.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final ItemService itemService;

    /**
     * 메인 페이지를 위한 컨트롤러 메서드입니다. 이 메서드는 메인 페이지에 표시될 상품 목록을 조회하여 반환합니다.
     * 사용자는 상품 검색 조건을 기반으로 상품 목록을 필터링할 수 있습니다. 검색 조건에는 상품명, 카테고리,
     * 판매 상태 등이 포함될 수 있습니다.
     *
     * <p>페이징 처리가 적용되어 있으며, 한 페이지 당 상품 목록의 수는 6개로 제한됩니다. 사용자는 페이지 번호를
     * 통해 추가 상품 목록을 조회할 수 있습니다.</p>
     *
     * @param itemSearchDto 상품 검색 조건을 담은 DTO. 검색 조건이 없는 경우 모든 상품이 조회됩니다.
     * @param page 요청된 페이지 번호. 페이지 번호는 0부터 시작합니다. Optional 객체를 사용하여, 페이지 번호가 제공되지 않은 경우 기본값으로 0을 사용합니다.
     * @param model 뷰에 데이터를 전달하기 위한 Model 객체. 조회된 상품 목록, 상품 검색 조건, 최대 페이지 수 등의 정보를 포함합니다.
     * @return 메인 페이지의 뷰 이름을 나타내는 문자열. 조회된 상품 목록과 함께 "main" 뷰로 사용자를 리다이렉트합니다.
     */
    @GetMapping(value="/")
    public String main(
            ItemSearchDto itemSearchDto,
            Optional<Integer> page,
            Model model
    ){
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 6);
        Page<MainItemDto> items = itemService.getMainItemPage(itemSearchDto, pageable);
        model.addAttribute("items", items);
        model.addAttribute("itemsSearchDto", itemSearchDto);
        model.addAttribute("maxPage", 5);
        return "main";
    }

}
