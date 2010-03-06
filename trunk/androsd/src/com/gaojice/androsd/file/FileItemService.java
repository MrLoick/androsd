package com.gaojice.androsd.file;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.gaojice.androsd.constants.Constants;

public class FileItemService {
	/**
	 * 
	 * @param pathOnServer
	 * @return
	 */
	public List<FileItem> getFileItems(String pathOnServer) {
		List<FileItem> fileItems = new ArrayList<FileItem>();
		File root = new File(pathOnServer);
		for (File file : root.listFiles()) {
			FileItem fileItem = new FileItem();
			fileItem.setIsFile(file.isFile());
			fileItem.setName(file.getName());
			if (pathOnServer.equals("/")) {
				pathOnServer = "";
			}
			fileItem.setLink(file.getAbsolutePath().replaceAll(
					Constants.ROOT_DIR, ""));
			fileItems.add(fileItem);
		}
		return fileItems;
	}
}
