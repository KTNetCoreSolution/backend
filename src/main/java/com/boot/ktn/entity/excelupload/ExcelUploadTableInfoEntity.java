package com.boot.ktn.entity.excelupload;

import lombok.Data;

@Data
public class ExcelUploadTableInfoEntity {
    String uploadName;
    String targetTable;
    int startRow;
    int colCnt;
    String useYn;
    String delYn;
}
