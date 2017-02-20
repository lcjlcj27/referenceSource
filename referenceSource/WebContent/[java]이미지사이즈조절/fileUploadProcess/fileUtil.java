class FileUtil {


	public TValueObject uploadFile(TValueObject input
	                             , UploadJobEnum jobEnum
	                             , UploadFileTypeEnum fileTypeEnum
	                             , String system
	                             , boolean sftpYN
	                               ) throws TFrameException {

		TValueObject result = new TValueObject();

		TFileUpload tFileUpload = input.getTFileUpLoad();

		List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();

		if (fileTypeEnum == null) {
			throw new TFrameException(MessageUtil.getMessage("E011"));
		}
		if (tFileUpload != null) {
			int fileCnt = tFileUpload.getItemCount();
			
			for (int i = 0; i < fileCnt; i++) {
				TFileItem tFileItem = tFileUpload.getItem(i);
				if (tFileItem == null) {
					throw new TFrameException(MessageUtil.getMessage("E011"));
				}
				String orgFileName = tFileItem.getFileName();
				if (StringUtils.isBlank(orgFileName)) {
					continue;
				}
				if (logger.isDebugEnabled()) {
					logger.debug("File name : " + orgFileName);
					logger.debug("File parameter name : " + tFileItem.getParamterName());
					logger.debug("File size : " + tFileItem.getSize());
				}

				// ���� ���� üũ
				String[] fileArr = StringUtils.split(orgFileName, '.');
				if (fileArr.length < 2 || fileTypeEnum.getTypes().indexOf(fileArr[fileArr.length - 1].toLowerCase()) < 0) {
					throw new TFrameException(MessageUtil.getMessage("E012", new String[] { fileTypeEnum.getTypes() }));
				}

				if (jobEnum == null) {
					jobEnum = UploadJobEnum.COMMON;
				}
				String newFileName = String.valueOf(System.nanoTime()) + "." + fileArr[fileArr.length - 1];
				String rootPath = uploadRootPath;
				if (fileTypeEnum.isImage()) {
					//newFileName = orgFileName;
					rootPath = imageFileRootPath;
				}
				String uploadDir = jobEnum.name() + File.separator + DateTime.now().toString("yyyyMMdd");
				try {
					// dir ����
					makeDir(rootPath + File.separator + uploadDir);
					File file = new File(rootPath + File.separator + uploadDir + File.separator + newFileName);

					tFileItem.writeFileTo(file);
					// ���� ������ ���������� ���� ��� ������ �����Ѵ�.
					fileTypeCheckAndDelete(file, fileTypeEnum);

					// ���� �̽��� �̹��� ������ ��� ������Ͽ� ��ȯ���� 2015-07-15
					if (fileTypeEnum.isImage()) {
						if(input.getString("MO_ONLY") == null) {		// ������� �ƴѰ��
							file = transImageFile(file, fileTypeEnum);
						} else {
							file = transImageFileForMO(file, fileTypeEnum);
						}
					}
					
					/*Local�� �ƴ� ��츸 ó����: ���߼��������� �׽�Ʈ ����*/
					//if (PropertyUtil.getProperty("serverType").equalsIgnoreCase("local") == false && (sftpYN == true)) {
					/*=SFTP=====================================================================================*/
					logger.debug("SFTP : Start_0_______________________________________________________________");

					SFtpClientUtil sftpClientUtil = new SFtpClientUtil();
					logger.debug("SFTP : Start_0-1_______________________________________________________________");
					String sourceFilePath = rootPath + File.separator + uploadDir + File.separator + newFileName;
					logger.debug("SFTP : Start_0-2_______________________________________________________________");
					
					/*Ÿ�ٰ�� ���� add */
					//String targetDirPath = sftpClientUtil.getChnFileRootPath() + File.separator + uploadDir;
					String targetDirPath = File.separator + uploadDir;
					
					
					logger.debug("SFTP : 1 sourceFilePath=" + sourceFilePath);
					logger.debug("SFTP : 2 targetDirPath=" + targetDirPath);

					// ���� ���н� TFrameException�� throw �ϱ� ������ ���� ó�� �ʿ��ϸ� try catch �� ó���ϼ���.
					//boolean fileSendResult = sftpClientUtil.sendFileToChnWeb(sourceFilePath, targetDirPath);

					boolean fileSendResult = false;
					if ("chn".equalsIgnoreCase(system)) {
						logger.debug("### sendFileToChnWeb !! ###");
						logger.debug("### sourceFilePath : "+sourceFilePath);
						logger.debug("### targetDirPath : "+targetDirPath);
						fileSendResult = sftpClientUtil.sendFileToChnWeb(sourceFilePath, targetDirPath);
					} else if("cma".equalsIgnoreCase(system)){
						logger.debug("### sendFileToCmaWeb !! ###");
						logger.debug("### sourceFilePath : "+sourceFilePath);
						logger.debug("### targetDirPath : "+targetDirPath);
						fileSendResult = sftpClientUtil.sendFileToCmaWeb(sourceFilePath, targetDirPath);
						
					} else {
						fileSendResult = sftpClientUtil.sendFileToAccWeb(sourceFilePath);
					}
					logger.debug("SFTP : 3 fileSendResult=" + fileSendResult);

					if (fileSendResult == false) {
						logger.debug("SFTP : 4 fileSendResult=" + fileSendResult);
						throw new TFrameException(MessageUtil.getMessage("E014", new String[] { "SFTP ���� " }));
					}

					logger.debug("SFTP : 4 fileSendResult=" + fileSendResult);
					logger.debug("SFTP : END_99_______________________________________________________________");
					/*=SFTP END=================================================================================*/
					//}

				} catch (TFrameException e) {
					logger.error("_______________________________________________________________");
					logger.error("File Write Error : org : " + tFileItem.getFileName() + "  //  target : " + uploadDir);
					logger.error(e.toString());
					logger.error("_______________________________________________________________");
					result.setString("MO_ERR_YN", "Y");
					result.setString("MO_ERR_MSG", e.getMessage());
				} catch (Exception e) {
					logger.error("_______________________________________________________________");
					logger.error("File Write Error : org : " + tFileItem.getFileName() + "  //  target : " + uploadDir);
					logger.error(e.toString());
					logger.error("_______________________________________________________________");
					result.setString("MO_ERR_YN", "Y");
					result.setString("MO_ERR_MSG", e.getMessage());
				}

				String fullPath = rootPath + File.separator + uploadDir + File.separator + newFileName;

				Map<String, String> resultMap = new HashMap<String, String>();
				resultMap.put("orgFileName", orgFileName);
				resultMap.put("newFileName", newFileName);
				resultMap.put("filePath", uploadDir);
				resultMap.put("fieldName", tFileItem.getParamterName());
				resultMap.put("fullPath", fullPath);
				resultList.add(resultMap);

				/* For Mobile */
				result.setString("filePathForMO", File.separator + uploadDir + File.separator + newFileName);
			}
		}

		result.setObject("resultList", resultList);
		return result;
	}







}