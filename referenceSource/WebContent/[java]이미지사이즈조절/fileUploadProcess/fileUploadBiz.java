TFileUpload tFileUpload = param.getTFileUpLoad();
if (tFileUpload != null) {

    int fileCnt = tFileUpload.getItemCount();
    for (int i = 0; i < fileCnt; i++) {
        TFileItem tFileItem = tFileUpload.getItem(i);
        if (tFileItem != null) {
            orgFileName = tFileItem.getFileName();
            orgFileSize = tFileItem.getSize();
        }
    }
    logger.debug(" fileCnt=" + fileCnt);
} else {
    logger.debug(" tFileUpload null");
}


  //파일업로드
FileUtil fileUtil = new FileUtil();
result = fileUtil.uploadFile(param, UploadJobEnum.HP_MOBILEGCCT, UploadFileTypeEnum.IMG, "chn", true);
