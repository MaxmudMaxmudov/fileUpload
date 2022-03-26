package com.company;

import com.company.dto.AttachFilterDTO;
import com.company.service.AttachService;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class FileUploadApplicationTests {

	@Autowired
	private AttachService attachService;
	@Test
	void contextLoads() {

		AttachFilterDTO filterDTO=new AttachFilterDTO();
		filterDTO.setFromSize(12L);
		filterDTO.setToSize(25L);

		attachService.filter(1,1,filterDTO);
	}

}
