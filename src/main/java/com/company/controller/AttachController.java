package com.company.controller;

import com.company.dto.AttachDTO;
import com.company.dto.AttachFilterDTO;
import com.company.service.AttachService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/attach")
@Api(tags = "Attach")
public class AttachController {
    @Autowired
    private AttachService attachService;

    @PostMapping("/upload")
    @ApiOperation(value = "Upload file", notes = "New file upload")
    public ResponseEntity<AttachDTO> fileUpload(@RequestParam("file") MultipartFile file) {
        AttachDTO fileName = attachService.saveFile(file);
        return ResponseEntity.ok().body(fileName);
    }


    @GetMapping("/load/{key}")
    @ApiOperation(value = "Upload File", notes = "uploads file")
    public ResponseEntity<?> loadFile(@PathVariable("key") String key) {
        return attachService.load(key);
//        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
//                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @DeleteMapping("/{key}")
    @ApiOperation(value = "Delete File", notes = "Delete file Date base")
    public ResponseEntity<Resource> deleteFile(@PathVariable("key") String fileToken) {
        attachService.delete(fileToken);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/filter")
    @ApiOperation(value = "Filtering", notes = "By Any Params")
    public ResponseEntity<?> filter(@RequestParam(value = "page", defaultValue = "0") int page,
                                    @RequestParam(value = "size", defaultValue = "10") int size,
                                    @RequestBody AttachFilterDTO dto) {

        return ResponseEntity.ok(attachService.filter(page, size, dto));
    }

}
