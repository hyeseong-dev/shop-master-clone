package com.shop.shopmasterclone.controller;

import com.shop.shopmasterclone.dto.MemberFormDto;
import com.shop.shopmasterclone.entity.Member;
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


@Controller
public class ItemController {

    @GetMapping(value="/admin/item/new")
    public String itemForm(){
        return "/item/itemForm";
    }
}
