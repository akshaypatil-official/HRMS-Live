package com.org.Service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.org.Entity.Challans;

public interface ChallanService {

	List<Challans> getAllChallans();

	void saveChallan(Challans challan, MultipartFile photoFile1, MultipartFile photoFile2, MultipartFile photoFile3,String username);

	void deleteChallanById(Long id);

}
