package com.shop.shopmasterclone.repository;


import com.shop.shopmasterclone.dto.ItemSearchDto;
import com.shop.shopmasterclone.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ItemRepositoryCustom {

    Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable);
}
