package com.shop.shopmasterclone.controller;

import com.shop.shopmasterclone.dto.ItemFormDto;
import com.shop.shopmasterclone.dto.MemberFormDto;
import com.shop.shopmasterclone.entity.Member;
import com.shop.shopmasterclone.service.ItemService;
import com.shop.shopmasterclone.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping(value="/admin/item/new")
    public String itemForm(Model model){
        model.addAttribute("itemFormDto", new ItemFormDto());
        return "/item/itemForm";
    }

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
