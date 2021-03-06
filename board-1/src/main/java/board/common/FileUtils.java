package board.common;

import java.io.File;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import board.dto.BoardFileDto;

@Component
public class FileUtils {

	public List<BoardFileDto> parseFileInfo(int boardIdx, MultipartHttpServletRequest httpServletRequest)throws Exception{
		if(ObjectUtils.isEmpty(httpServletRequest)) {
			return null;
		}
		
		List<BoardFileDto> fileList = new ArrayList<>();
		DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyyMMdd");
		ZonedDateTime current = ZonedDateTime.now();
		String path = "image/"+current.format(format);
		File file = new File(path);
		if(file.exists() == false) {
			file.mkdirs();
		}
		Iterator<String> iterator  = httpServletRequest.getFileNames();
		
		String newFileName, originalFileExtension, contentType;
		
		while(iterator.hasNext()) {
			List<MultipartFile> list = httpServletRequest.getFiles(iterator.next());
			
			for(MultipartFile mutiMultipartFile : list) {
				if(mutiMultipartFile.isEmpty() == false) {
					contentType = mutiMultipartFile.getContentType();
					if(ObjectUtils.isEmpty(contentType)) {
						break;
					}
					else {
						if(contentType.contains("image/jpeg")) {
							originalFileExtension = ".jpg";
						}else if(contentType.contains("image/png")) {
							originalFileExtension = ".png";
						}else if(contentType.contains("image/gif")) {
							originalFileExtension = ".gif";
						}else {
							break;
						}
					}
					newFileName = Long.toString(System.nanoTime())+
							originalFileExtension;
					BoardFileDto boardFile = new BoardFileDto();
					boardFile.setBoardIdx(boardIdx);
					boardFile.setFileSize(mutiMultipartFile.getSize());
					boardFile.setOriginalFileName(mutiMultipartFile.getOriginalFilename());
					boardFile.setStoredFilePath(path+"/"+newFileName);
					fileList.add(boardFile);
					
					file = new File(path+"/"+newFileName);
					mutiMultipartFile.transferTo(file);
				}
			}
		}
		return fileList;
		
		
		
	}
}
