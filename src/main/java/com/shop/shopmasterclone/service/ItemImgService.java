package com.shop.shopmasterclone.service;

import com.shop.shopmasterclone.entity.ItemImg;
import com.shop.shopmasterclone.repository.ItemImgRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;


/**
 * 상품 이미지 관련 서비스를 제공하는 클래스입니다. 상품 이미지의 업데이트와 저장을 담당합니다.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ItemImgService {
    @Value("${itemImgLocation}")
    private String itemImgLocation;

    private final ItemImgRepository itemImgRepository;
    private final FileService fileService;

    /**
     * 주어진 상품 이미지 ID를 사용하여 이미지를 업데이트합니다.
     *
     * @param itemImgId     업데이트할 상품 이미지의 ID
     * @param itemImgFile   업데이트할 상품 이미지 파일
     * @throws Exception    이미지 업데이트 또는 파일 처리 중 발생할 수 있는 예외를 처리합니다.
     */
    public void updateItemImg(Long itemImgId, MultipartFile itemImgFile) throws Exception{
        if(!itemImgFile.isEmpty()){
            ItemImg savedItemImg = itemImgRepository.findById(itemImgId).orElseThrow(EntityNotFoundException::new);

            // 기존 이미지 파일 삭제
            if(!StringUtils.isEmpty(savedItemImg.getImgName()))
                fileService.deleteFile(itemImgLocation + "/" + savedItemImg.getImgName());

            String oriImgName = itemImgFile.getOriginalFilename();
            String imgName = fileService.uploadFile(itemImgLocation, oriImgName, itemImgFile.getBytes());
            String imgUrl = "/images/item/" + imgName;
            savedItemImg.updateItemImg(oriImgName, imgName, imgUrl);
        }
    }

    /**
     * 새로운 상품 이미지를 저장합니다.
     *
     * @param itemImg       저장할 상품 이미지 엔티티
     * @param itemImgFile   저장할 상품 이미지 파일
     * @throws Exception    이미지 업로드 및 저장 중 발생할 수 있는 예외를 처리합니다.
     */
    public void saveItemImg(ItemImg itemImg, MultipartFile itemImgFile) throws Exception{
        String oriImgName = itemImgFile.getOriginalFilename();
        String imgName = "";
        String imgUrl = "";

        //파일 업로드
        if(!StringUtils.isEmpty(oriImgName)){
            imgName = fileService.uploadFile(itemImgLocation, oriImgName, itemImgFile.getBytes());
            imgUrl = "/images/item/" + imgName;
        }

        //상품 이미지 정보 저장
        itemImg.updateItemImg(oriImgName, imgName, imgUrl);
        itemImgRepository.save(itemImg);
    }


}
