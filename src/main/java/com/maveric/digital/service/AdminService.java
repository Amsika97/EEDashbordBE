package com.maveric.digital.service;

import java.util.List;

import com.maveric.digital.responsedto.CsvMetaDataDto;

public interface AdminService {

	void importCsvMetaData(List<CsvMetaDataDto> metaDataList);
	

}
