package com.company.service;

import com.company.dto.AttachDTO;
import com.company.dto.AttachFilterDTO;
import com.company.entity.AttachEntity;
import com.company.repository.AttachRepository;
import com.company.spec.AttachSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AttachService {

    @Autowired
    private AttachRepository attachRepository;
    @Value("${attach.upload.folder}")
    private String uploadFolder;
    @Value("${attach.download.url}")
    private String attachOpenUrl;

    public AttachDTO saveFile(MultipartFile file) {

        String filePath = getYmDString(); // 2021/07/13
        String key = UUID.randomUUID().toString();
        String extension = getExtension(file.getOriginalFilename()); // .jpg,.pdf

        File folder = new File(uploadFolder + "/" + filePath); // uploads/2021/07/13
        if (!folder.exists()) {
            folder.mkdirs();
        }

        try {
            byte[] bytes = file.getBytes();
            //  uploads/2021/07/13/dasda_dasda_dasda_dasda.jpg
            Path path = Paths.get(uploadFolder + "/" + filePath + "/" + key + "." + extension);
            Files.write(path, bytes);

            AttachEntity entity = new AttachEntity();
            entity.setKey(key);
            entity.setExtension(extension);
            entity.setOriginName(file.getOriginalFilename());
            entity.setSize(file.getSize());
            entity.setFilePath(filePath);
            entity.setCreatedDate(LocalDateTime.now());
            entity.setVisible(Boolean.TRUE);
            attachRepository.save(entity);

            return toDTO(entity);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ResponseEntity<?> load(String key) {
        try {
            Optional<AttachEntity> optional = attachRepository.findByKey(key);
            if (!optional.isPresent()) {
                return ResponseEntity.badRequest().body("File not found.");
            }
            AttachEntity entity = optional.get();

            Path file = Paths.get(uploadFolder + "/" +
                    entity.getFilePath() + "/" + entity.getKey() + "." + entity.getExtension());
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + entity.getOriginName() + "\"").body(resource);
            } else {
                throw new RuntimeException("Could not read the file!");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    public void delete(String key) {
        Optional<AttachEntity> optional = attachRepository.findByKey(key);
        if (!optional.isPresent()) {
            return;
        }
        String filePath = optional.get().getFilePath() + "/" + key + "." + optional.get().getExtension();
        File file = new File(uploadFolder + "/" + filePath);
        if (file.exists()) {
            file.delete();
        }
        attachRepository.delete(optional.get());
    }

    public Page<AttachDTO> filter(int page, int size, AttachFilterDTO dto) {
        Pageable pageable = PageRequest.of(page, size);

        Specification<AttachEntity> spec = null;
        if (dto.getVisible() != null) {
            spec = Specification.where(AttachSpecification.status(dto.getVisible()));
        } else {
            spec = Specification.where(AttachSpecification.status(Boolean.TRUE));
        }

        if (dto.getOriginName() != null) {
            spec = spec.and(AttachSpecification.like("originName", dto.getOriginName()));
        }
        if (dto.getFromDate() != null) {
            spec = spec.and(AttachSpecification.greaterThanOrEqualTo("createdDate", dto.getFromDate()));
        }
        if (dto.getToDate() != null) {
            spec = spec.and(AttachSpecification.lessThanOrEqualTo("createdDate", dto.getToDate()));
        }
        if (dto.getFromSize() != null && dto.getToSize() != null) {
            spec = spec.and(AttachSpecification.betweenSize(toKb(dto.getFromSize()), toKb(dto.getToSize())));
        }


        Page<AttachEntity> entityPage = attachRepository.findAll(spec, pageable);
        List<AttachDTO> content = entityPage.getContent().stream()
                .map(this::toDTO).collect(Collectors.toList());

        return new PageImpl<AttachDTO>(content, pageable, entityPage.getTotalElements());
    }

    public AttachDTO toDTO(AttachEntity entity) {
        AttachDTO attachDTO = new AttachDTO();

        attachDTO.setId(entity.getId());
        attachDTO.setKey(entity.getKey());
        attachDTO.setFilePath(entity.getFilePath());
        attachDTO.setCreatedDate(entity.getCreatedDate());
        attachDTO.setSize(entity.getSize());
        attachDTO.setOriginName(entity.getOriginName());
        attachDTO.setExtension(entity.getExtension());
        attachDTO.setUrl(attachOpenUrl + entity.getKey());
        return attachDTO;
    }

    private Long toKb(Long megaByte) {
        Long kByte = megaByte * 1024 * 8;
        return kByte;

    }

    public static String getYmDString() {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
        int day = Calendar.getInstance().get(Calendar.DATE);

        return year + "/" + month + "/" + day + "/";
    }

    public String getExtension(String fileName) {
        int lastIndex = fileName.lastIndexOf(".");
        return fileName.substring(lastIndex + 1);
    }

}
