package com.shop.shopmasterclone.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * 파일 업로드 및 삭제 기능을 제공하는 서비스 클래스입니다.
 * <p>
 * 이 클래스는 파일을 서버에 업로드하고, 필요한 경우 파일을 삭제하는 기능을 제공합니다.
 * 파일 업로드 시 UUID를 사용하여 파일명을 고유하게 생성하며, 원본 파일 확장자를 유지합니다.
 */
@Service
@Log4j2
public class FileService {

    /**
     * 지정된 경로에 파일을 업로드하는 메서드입니다.
     *
     * @param uploadPath 파일을 업로드할 서버의 경로
     * @param originalFileName 원본 파일의 이름, 확장자를 추출하기 위해 사용됩니다.
     * @param fileData 업로드할 파일의 데이터
     * @return 저장된 파일의 이름을 반환합니다. UUID를 사용하여 고유한 파일명을 생성합니다.
     * @throws Exception 파일 업로드 과정에서 발생할 수 있는 예외
     */
    public String uploadFile(
            String uploadPath,
            String originalFileName,
            byte[] fileData
    ) throws Exception {
        UUID uuid = UUID.randomUUID();
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String savedFileName = uuid.toString() + extension;
        String fileUploadFullUrl = uploadPath + "/" + savedFileName;

        try (FileOutputStream fileOutputStream = new FileOutputStream(fileUploadFullUrl)) {
            fileOutputStream.write(fileData);
        } catch (IOException e) {
            log.error("파일 업로드 중 에러 발생: {}", e.getMessage(), e);
            throw new RuntimeException("파일 업로드 중 에러가 발생했습니다.", e);
        }
        return savedFileName;
    }

    /**
     * 지정된 파일 경로의 파일을 삭제하는 메서드입니다.
     *
     * @param filePath 삭제할 파일의 전체 경로
     * @throws Exception 파일 삭제 과정에서 발생할 수 있는 예외
     */
    public void deleteFile(String filePath) {
        File deleteFile = new File(filePath);

        if(deleteFile.exists()){
            if(deleteFile.delete()) {
                log.info("파일을 삭제하였습니다.");
            } else {
                log.error("파일 삭제 실패: 파일 삭제 과정에서 문제가 발생했습니다.");
                throw new RuntimeException("파일을 삭제할 수 없습니다.");
            }
        }else{
            log.info("삭제하려는 파일이 존재하지 않습니다.");
        }
    }

}