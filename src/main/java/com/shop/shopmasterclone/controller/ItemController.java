package com.shop.shopmasterclone.controller;

import com.shop.shopmasterclone.dto.ItemFormDto;
import com.shop.shopmasterclone.dto.ItemSearchDto;

import com.shop.shopmasterclone.entity.Item;

import com.shop.shopmasterclone.service.ItemService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

/**
 * 상품 관련 기능을 처리하는 컨트롤러 클래스입니다. 상품 등록, 수정 관련 HTTP 요청을 처리하며,
 * 관련 서비스인 {@link ItemService}를 통해 상품 데이터를 처리합니다.
 */
@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    /**
     * 상품 관리 페이지를 조회합니다. 이 메소드는 관리자가 상품 목록을 조회할 때 사용됩니다.
     * 선택적으로 페이지 번호를 매개변수로 받아 해당 페이지의 상품 목록과 페이징 정보를 반환합니다.
     *
     * @param itemSearchDto 상품 검색 조건을 담은 데이터 전송 객체
     * @param page 조회할 페이지 번호 (Optional)
     * @param model 뷰에 전달할 모델 객체
     * @return 상품 관리 페이지의 뷰 이름
     */
    @GetMapping(value="/admin/items")
    public String itemMange(
            ItemSearchDto itemSearchDto,
            @RequestParam("page") Optional<Integer> page,
            Model model
    ){
        Pageable pageable = PageRequest.of(page.orElse(0), 3);
        Page<Item> items = itemService.getAdminItemPage(itemSearchDto, pageable);
        model.addAttribute("items", items);
        model.addAttribute("itemSearchDto", itemSearchDto);
        model.addAttribute("maxPage", 5);
        return "item/itemMng";

    }

    /**
     * 특정 상품의 상세 정보 페이지를 조회합니다. 상품 ID를 통해 상품 정보를 조회하고,
     * 해당 정보를 모델에 바인딩하여 뷰에 전달합니다. 상품이 존재하지 않을 경우, 에러 메시지를 포함해 반환합니다.
     *
     * @param itemId 조회할 상품의 ID
     * @param model 뷰에 전달할 데이터를 담은 모델 객체
     * @return 상품 상세 정보 페이지의 뷰 이름
     * @throws EntityNotFoundException 상품 ID에 해당하는 상품이 없을 경우 발생
     */

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

    /**
     * 특정 상품의 상세 정보 페이지를 조회하는 메서드입니다.
     * 상품 ID를 기반으로 상품의 상세 정보를 조회하고, 이 정보를 뷰에 전달합니다.
     * 조회된 상품 정보는 {@link ItemFormDto} 객체에 담겨 모델을 통해 뷰로 전달됩니다.
     * 이 메서드는 일반 사용자가 상품의 상세 정보를 보기 위해 사용됩니다.
     *
     * @param model 뷰에 전달할 데이터를 담는 모델 객체입니다. 조회된 상품의 상세 정보를 포함합니다.
     * @param itemId 조회할 상품의 고유 ID입니다. URL 경로에서 해당 ID를 받아옵니다.
     * @return 상품 상세 정보 페이지의 뷰 이름을 문자열로 반환합니다. 상품 정보가 성공적으로 조회되면, 해당 상품의 상세 정보를 표시하는 뷰로 이동합니다.
     */
    @GetMapping(value = "/item/{itemId}")
    public String itemDtl(
            Model model,
            @PathVariable("itemId") Long itemId
    ){
        ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
        model.addAttribute("item", itemFormDto);
        return "item/itemDtl";
    }

    /**
     * 특정 상품의 정보를 수정합니다. 상품 ID를 통해 기존 상품 정보를 조회하고, 입력 받은 정보로 업데이트합니다.
     * 상품 이미지도 함께 업데이트되며, 새로운 이미지가 제공될 경우 기존 이미지를 대체합니다.
     *
     * @param itemFormDto 수정할 상품 정보가 담긴 DTO 객체
     * @param bindingResult 입력 데이터의 유효성 검사 결과
     * @param itemImgFileList 수정할 상품 이미지 파일 리스트
     * @param model 뷰에 전달할 데이터를 담은 모델 객체
     * @return 상품 수정 후 리다이렉션할 경로
     * @throws EntityNotFoundException 상품 ID에 해당하는 상품이 데이터베이스에 없을 경우
     * @throws IllegalArgumentException 이미지 파일 리스트가 비어 있거나 다른 예외가 발생한 경우
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
     * 상품 등록 폼 페이지로 이동하는 메서드입니다.
     * 이 메서드는 관리자가 새로운 상품을 등록하기 위한 폼을 제공합니다.
     *
     * @param model 뷰로 전달할 데이터를 담는 모델 객체입니다. 상품 등록 폼을 구성하는 데 필요한 정보를 포함합니다.
     * @return 상품 등록 폼이 있는 뷰의 이름을 문자열로 반환합니다.
     */
    @GetMapping(value="/admin/item/new")
    public String itemForm(Model model){
        model.addAttribute("itemFormDto", new ItemFormDto());
        return "/item/itemForm";
    }

    /**
     * 새로운 상품을 등록하는 메서드입니다.
     * 사용자로부터 입력 받은 상품 정보와 이미지 파일들을 검증 후 상품을 등록합니다.
     * 상품 등록에 실패하거나 입력 데이터에 오류가 있는 경우, 오류 메시지와 함께 상품 등록 폼으로 다시 이동합니다.
     *
     * @param itemFormDto 등록할 상품 정보가 담긴 {@link ItemFormDto} 객체입니다. 사용자로부터 입력 받은 데이터를 포함합니다.
     * @param bindingResult 폼 검증 결과를 담는 {@link BindingResult} 객체입니다. 데이터 바인딩 과정에서 발생한 오류 정보를 포함합니다.
     * @param itemImgFileList 사용자가 업로드한 상품 이미지 파일 목록입니다. 첫 번째 이미지는 대표 이미지로 설정됩니다.
     * @param model 뷰로 전달할 데이터를 담는 모델 객체입니다. 오류 메시지 등의 정보를 뷰에 전달하는 데 사용됩니다.
     * @return 상품 등록 성공 시 메인 페이지로 리다이렉트합니다. 오류가 발생한 경우, 상품 등록 폼 뷰 이름을 반환합니다.
     * @throws IllegalArgumentException 첫 번째 이미지 파일이 비어있거나, 다른 유효성 검사에서 예외가 발생한 경우에 대한 처리를 포함합니다.
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
