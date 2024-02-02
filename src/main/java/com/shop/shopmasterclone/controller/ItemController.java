package com.shop.shopmasterclone.controller;

import com.shop.shopmasterclone.dto.ItemFormDto;
import com.shop.shopmasterclone.dto.MemberFormDto;
import com.shop.shopmasterclone.entity.Member;
import com.shop.shopmasterclone.service.ItemService;
import com.shop.shopmasterclone.service.MemberService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 상품 관련 기능을 처리하는 컨트롤러 클래스입니다. 상품 등록, 수정 관련 HTTP 요청을 처리하며,
 * 관련 서비스인 {@link ItemService}를 통해 상품 데이터를 처리합니다.
 */
@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping(value = "/admin/item/{itemId}")
    public String itemDtl(@PathVariable("itemId") Long itemId, Model model){

        try {
            ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
            model.addAttribute("itemFormDto", itemFormDto);
        } catch(EntityNotFoundException e){
            model.addAttribute("errorMessage", "존재하지 않는 상품 입니다.");
            model.addAttribute("itemFormDto", new ItemFormDto());
            return "item/itemForm";
        }

        return "item/itemForm";
    }

    @GetMapping(value = "/item/{itemId}")
    public String itemDtl(Model model, @PathVariable("itemId") Long itemId){
        ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
        model.addAttribute("item", itemFormDto);
        return "item/itemDtl";
    }

    /**
     * 상품 정보를 수정하는 메서드입니다. HTTP POST 요청을 처리하며, 입력된 상품 정보를 업데이트합니다.
     *
     * @param itemFormDto        수정할 상품 정보를 담은 DTO 객체
     * @param bindingResult      입력 데이터의 유효성 검사 결과
     * @param itemImgFileList    상품 이미지 파일 리스트
     * @param model              뷰 템플릿에 전달할 데이터 모델
     * @return                   수정 완료 후 리다이렉트 페이지 경로
     */
    @PostMapping(value = "/admin/item/{itemId}")
    public String itemUpdate(@Valid ItemFormDto itemFormDto, BindingResult bindingResult,
                             @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList, Model model){
        if(bindingResult.hasErrors()){
            return "item/itemForm";
        }

        if(itemImgFileList.get(0).isEmpty() && itemFormDto.getId() == null){
            model.addAttribute("errorMessage", "첫번째 상품 이미지는 필수 입력 값 입니다.");
            return "item/itemForm";
        }

        try {
            itemService.updateItem(itemFormDto, itemImgFileList);
        } catch (Exception e){
            model.addAttribute("errorMessage", "상품 수정 중 에러가 발생하였습니다.");
            return "item/itemForm";
        }

        return "redirect:/";
    }

    /**
     * 새로운 상품을 등록하는 페이지로 이동하는 메서드입니다.
     *
     * @param model  뷰 템플릿에 전달할 데이터 모델
     * @return       상품 등록 페이지의 경로
     */
    @GetMapping(value="/admin/item/new")
    public String itemForm(Model model){
        model.addAttribute("itemFormDto", new ItemFormDto());
        return "/item/itemForm";
    }

    /**
     * 새로운 상품을 등록하는 메서드입니다. HTTP POST 요청을 처리하며, 입력된 상품 정보를 등록합니다.
     *
     * @param itemFormDto        등록할 상품 정보를 담은 DTO 객체
     * @param bindingResult      입력 데이터의 유효성 검사 결과
     * @param itemImgFileList    상품 이미지 파일 리스트
     * @param model              뷰 템플릿에 전달할 데이터 모델
     * @return                   등록 완료 후 리다이렉트 페이지 경로
     */
    @PostMapping(value="/admin/item/new")
    public String itemNew(
            @Valid ItemFormDto itemFormDto,
            BindingResult bindingResult,
            @RequestParam("itemImgFile")List<MultipartFile> itemImgFileList,
            Model model
    ){
        if(bindingResult.hasErrors())
            return "item/itemForm";

        if(itemImgFileList.get(0).isEmpty() && itemFormDto.getId() == null)
            model.addAttribute("errorMessage", "첫번째 상품 이미지는 필수 입력 값 입니다.");

        try{
            itemService.saveItem(itemFormDto, itemImgFileList);
        }catch(Exception error){
            model.addAttribute("errorMessage", "상품 등록 중 에러가 발생하였습니다.");
            return "item/itemForm";
        }
        return "redirect:/";
    }
}
