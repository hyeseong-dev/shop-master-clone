package com.shop.shopmasterclone.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link FileService}의 파일 업로드 및 삭제 기능을 검증하는 테스트 클래스입니다.
 * <p>
 * 이 클래스는 Spring Boot의 테스트 환경을 활용하여 {@link FileService}가 제공하는
 * 파일 업로드 및 삭제 기능의 정확성을 검증합니다. 테스트는 임시 디렉토리를 사용하여
 * 실제 파일 시스템에 영향을 주지 않으며, 각 테스트 수행 후에는 생성된 파일과 디렉토리를
 * 안전하게 정리합니다.
 */
@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
class FileServiceTest {

    @Autowired
    private FileService fileService;

    private Path tempDir;

    /**
     * 각 테스트 실행 전에 임시 디렉토리를 생성하는 설정 메소드입니다.
     * 이 디렉토리는 파일 업로드 및 삭제 테스트에 사용됩니다.
     *
     * @throws Exception 임시 디렉토리 생성 중 발생할 수 있는 예외
     */
    @BeforeEach
    void setUp() throws Exception {
        // 임시 디렉토리 생성
        tempDir = Files.createTempDirectory("upload_test");
    }

    /**
     * 각 테스트 실행 후에 임시 디렉토리와 그 내용을 삭제하는 정리 메소드입니다.
     * 이 과정은 테스트 간의 격리를 보장하며, 테스트 후 시스템을 깨끗한 상태로 유지합니다.
     *
     * @throws Exception 디렉토리 삭제 중 발생할 수 있는 예외
     */
    @AfterEach
    void tearDown() throws Exception {
        // 테스트 후 임시 디렉토리 삭제
        Files.walk(tempDir)
                .map(Path::toFile)
                .forEach(File::delete);
    }

    /**
     * {@link FileService#uploadFile(String, String, byte[])} 메소드의 정확성을 검증하는 테스트입니다.
     * 이 테스트는 {@link MockMultipartFile}을 사용하여 파일 업로드 기능을 시뮬레이션하고,
     * 업로드된 파일이 지정된 위치에 성공적으로 생성되었는지 확인합니다.
     *
     * @throws Exception 파일 업로드 중 발생할 수 있는 예외
     */
    @Test
    @DisplayName("파일 업로드가 성공적으로 수행되어야 한다")
    void testUploadFile() throws Exception {
        // Given
        String originalFileName = "test.jpg";
        String contentType = "image/jpeg";
        byte[] content = "test content".getBytes();
        MultipartFile multipartFile = new MockMultipartFile("user-file", originalFileName, contentType, content);

        // When
        String savedFileName = fileService.uploadFile(tempDir.toString(), multipartFile.getOriginalFilename(), multipartFile.getBytes());

        // Then
        File savedFile = new File(tempDir.toString(), savedFileName);
        assertTrue(savedFile.exists(), "파일이 성공적으로 업로드되어야 합니다.");
    }

    /**
     * {@link FileService#deleteFile(String)} 메소드의 정확성을 검증하는 테스트입니다.
     * 이 테스트는 임시 파일을 생성한 후, 해당 파일을 삭제하는 기능을 시험하고,
     * 파일이 성공적으로 삭제되었는지 확인합니다.
     *
     * @throws Exception 파일 삭제 중 발생할 수 있는 예외
     */
    @Test
    @DisplayName("파일 삭제가 성공적으로 수행되어야 한다")
    void testDeleteFile() throws Exception {
        // Given
        String content = "delete test content";
        Path file = Files.createFile(tempDir.resolve("delete-test.jpg"));
        Files.write(file, content.getBytes());

        // When
        fileService.deleteFile(file.toString());
        // Then
        File deletedFile = file.toFile();
        assertFalse(deletedFile.exists(), "파일이 성공적으로 삭제되어야 합니다.");
    }
}