package br.com.dias.apiRest.controller;

import br.com.dias.apiRest.data.dto.v1.UploadFileResponseDTO;
import br.com.dias.apiRest.service.FileStorageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "File Endpoint")
@RestController
@RequestMapping("/api/file/v1")
public class FileController {

    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping("/uploadFile")
    public UploadFileResponseDTO uploadFile(@RequestParam("file") MultipartFile file) {
       var fileName =  fileStorageService.storeFile(file);
       String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
               .path("/api/file/v1/downloadFile/")
               .path(fileName)
               .toUriString();

        return new UploadFileResponseDTO(fileName, fileDownloadUri,file.getContentType(), file.getSize());
    }

    @PostMapping("/uploadMultipleFile")
    public List<UploadFileResponseDTO> uploadMultipleFile(@RequestParam("files") MultipartFile[] files) {
        return Arrays.stream(files)
                .map(this::uploadFile)
                .collect(Collectors.toList());
    }

    @GetMapping("/downloadFile/{filename:.+}")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable String filename,
            HttpServletRequest request
    ) {
        var resource =  fileStorageService.loadFileAsResource(filename);
       String contentType = "";

       try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
       } catch (Exception e) {
           System.out.println("Could not determine file type!");
       }

       if (contentType.isBlank()) {
           contentType = "application/octet-stream";
       }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
