package com.gaojice.androsd.file;

public class FileItem {
	private String name;
	private String pathOnServer;

	private Boolean isFile;
	private String link;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPathOnServer() {
		return pathOnServer;
	}

	public void setPathOnServer(String pathOnServer) {
		this.pathOnServer = pathOnServer;
	}

	public Boolean getIsFile() {
		return isFile;
	}

	public void setIsFile(Boolean isFile) {
		this.isFile = isFile;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	@Override
	public String toString() {
		return "<tr><td><a href=\"" + this.getLink() + "\">" + this.getName()
				+ "</a></td></tr>";
	}
}
