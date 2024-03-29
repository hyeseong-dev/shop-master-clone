package com.shop.shopmasterclone.service;

import com.shop.shopmasterclone.dto.ItemFormDto;
import com.shop.shopmasterclone.dto.ItemImgDto;
import com.shop.shopmasterclone.dto.ItemSearchDto;
import com.shop.shopmasterclone.dto.MainItemDto;
import com.shop.shopmasterclone.entity.Item;
import com.shop.shopmasterclone.entity.ItemImg;
import com.shop.shopmasterclone.repository.ItemImgRepository;
import com.shop.shopmasterclone.repository.ItemRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * 상품 관련 서비스를 제공하는 클래스입니다.
 * 이 클래스는 상품의 등록, 수정, 상세 조회 등의 기능을 담당합니다.
 */
@Log4j2
@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final ItemImgService itemImgService;
    private final ItemImgRepository itemImgRepository;

    /**
     * 메인 페이지에 표시할 상품 목록을 조회하는 메서드입니다.
     * 이 메서드는 상품 검색 조건({@link ItemSearchDto})을 기반으로 상품 목록을 페이징 처리하여 반환합니다.
     * 검색 조건에는 상품명, 판매 상태, 등록 날짜 등이 포함될 수 있으며, 이를 통해 사용자가 원하는 상품을 쉽게 찾을 수 있습니다.
     *
     * <p>페이징 처리는 {@link Pageable} 객체를 통해 구현되며, 클라이언트는 페이지 번호와 페이지 당 항목 수를 지정할 수 있습니다.
     * 반환되는 {@link Page} 객체에는 조회된 상품 목록과 페이징 정보(총 페이지 수, 총 항목 수 등)가 포함됩니다.</p>
     *
     * <p>이 메서드는 읽기 전용 트랜잭션에서 실행되므로, 데이터베이스의 데이터를 변경하지 않습니다.</p>
     *
     * @param itemSearchDto 상품 검색 조건을 담고 있는 {@link ItemSearchDto} 객체입니다.
     *                      이 객체에는 검색할 상품명, 판매 상태, 등록 날짜 범위 등의 조건이 포함될 수 있습니다.
     * @param pageable 페이징 처리 정보를 담고 있는 {@link Pageable} 객체입니다.
     *                 이 객체를 통해 요청 페이지 번호와 페이지 당 항목 수를 지정할 수 있습니다.
     * @return 조건에 맞는 상품 목록과 페이징 정보가 포함된 {@link Page<MainItemDto>} 객체를 반환합니다.
     *         조회된 상품이 없는 경우, 비어 있는 페이지 객체가 반환될 수 있습니다.
     */
    @Transactional(readOnly = true)
    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable){
        return itemRepository.getMainItemPage(itemSearchDto, pageable);
    }

    /**
     * 관리자 상품 페이지를 조회하는 기능입니다.
     * 상품 검색 조건과 페이지 정보를 기반으로 상품 목록을 페이징하여 반환합니다.
     *
     * @param itemSearchDto 상품 검색 조건을 담은 DTO
     * @param pageable 페이징 정보
     * @return 조건에 맞는 상품 페이지
     */
    @Transactional(readOnly = true)
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable){
        return itemRepository.getAdminItemPage(itemSearchDto, pageable);
    }

    /**
     * 기존 상품의 정보와 이미지를 업데이트하는 메서드입니다.
     *
     * 이 메서드는 상품 정보를 포함하는 {@link ItemFormDto} 객체와 상품 이미지 파일의 리스트를 받아
     * 해당 상품의 정보와 이미지를 업데이트합니다. 상품 정보는 DTO 객체를 통해 업데이트되며,
     * 각 이미지 파일은 {@link ItemImgService}를 사용하여 업데이트됩니다.
     *
     * @param itemFormDto 업데이트할 상품 정보가 담긴 DTO 객체입니다. 이 객체는 상품의 기본 정보와
     *                    업데이트할 이미지에 대한 ID 목록을 포함해야 합니다.
     * @param itemImgFileList 업데이트할 상품 이미지 파일의 리스트입니다. 이 리스트는 새로운 이미지 파일을 포함할 수 있으며,
     *                        비어 있지 않아야 합니다.
     * @return 업데이트된 상품의 ID를 반환합니다.
     * @throws Exception 상품 정보의 업데이트 또는 이미지 파일의 처리 중 발생할 수 있는 예외를 포괄합니다.
     *                   주로 데이터베이스 접근 실패, 파일 입출력 오류 등이 이에 해당합니다.
     * @throws EntityNotFoundException 주어진 {@code itemFormDto}의 ID로 상품을 찾을 수 없을 때 발생합니다.
     * @throws IllegalArgumentException 이미지 파일 리스트가 비어 있거나, 예상되는 이미지 ID와 일치하지 않을 때 발생합니다.
     */
    public Long updateItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception {
        // 상품 수정
        Item item = itemRepository.findById(itemFormDto.getId()).orElseThrow(() -> new EntityNotFoundException("Item not found with id: " + itemFormDto.getId()));
        item.updateItem(itemFormDto);

        if(itemImgFileList == null || itemImgFileList.isEmpty()) {
            throw new IllegalArgumentException("Image file list cannot be empty when updating item images.");
        }

        List<Long> itemImgIds = itemFormDto.getItemImgIds();

        // 이미지 등록
        for(int i = 0; i < itemImgFileList.size(); i++) {
            if(i < itemImgIds.size()) { // Ensure there's a corresponding image ID for the file
                itemImgService.updateItemImg(itemImgIds.get(i), itemImgFileList.get(i));
            } else {
                // Handle the case where there are more files than expected or log a warning
                log.warn("Extra image file provided without a corresponding item image ID.");
            }
        }

        return item.getId();
    }


    /**
     * 새로운 상품을 등록하는 메서드입니다.
     *
     * @param itemFormDto 상품 등록에 필요한 데이터가 담긴 DTO 객체
     * @param itemImgFileList 상품 이미지 파일 목록
     * @return 등록된 상품의 ID
     * @throws Exception 이미지 저장 중 발생할 수 있는 예외
     */
    public Long saveItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception{
        // 상품 등록
        Item item = itemFormDto.createItem();
        itemRepository.save(item);

        // 이미지 등록
        for(int i=0; i<itemImgFileList.size(); i++){
            ItemImg itemImg = new ItemImg();
            itemImg.setItem(item);

            if(i == 0)
                itemImg.setRepimgYn("Y");
            else
                itemImg.setRepimgYn("N");
            itemImgService.saveItemImg(itemImg, itemImgFileList.get(i));

        }
        return item.getId();
    }

    /**
     * 상품 상세 정보를 조회하는 메서드입니다.
     *
     * @param itemId 조회할 상품의 ID
     * @return 조회된 상품의 상세 정보가 담긴 DTO
     * @throws EntityNotFoundException 주어진 ID에 해당하는 상품이 없을 경우 발생
     */
    @Transactional(readOnly = true)
    public ItemFormDto getItemDtl(Long itemId){
        List<ItemImg> itemImgList = itemImgRepository.findByItemIdOrderByIdAsc(itemId);
        List<ItemImgDto> itemImgDtoList = new ArrayList<>();

        for(ItemImg itemImg: itemImgList){
            ItemImgDto itemImgDto = ItemImgDto.of(itemImg);
            itemImgDtoList.add(itemImgDto);
        }

        Item item = itemRepository.findById(itemId).orElseThrow(EntityNotFoundException::new);
        ItemFormDto itemFormDto = ItemFormDto.of(item);
        itemFormDto.setItemImgDtoList(itemImgDtoList);
        return itemFormDto;
    }
}
