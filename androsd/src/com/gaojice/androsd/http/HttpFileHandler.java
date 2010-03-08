package com.gaojice.androsd.http;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.entity.ContentProducer;
import org.apache.http.entity.EntityTemplate;
import org.apache.http.entity.FileEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;

import com.gaojice.androsd.constants.Constants;
import com.gaojice.androsd.file.FileItem;
import com.gaojice.androsd.file.FileItemService;
import com.gaojice.androsd.template.UiTemplate;

public class HttpFileHandler implements HttpRequestHandler {

	private final String docRoot;

	public HttpFileHandler(final String docRoot) {
		super();
		this.docRoot = docRoot;
	}

	public void handle(final HttpRequest request, final HttpResponse response,
			final HttpContext context) throws HttpException, IOException {

		String method = request.getRequestLine().getMethod().toUpperCase(
				Locale.ENGLISH);
		if (!method.equals("GET") && !method.equals("HEAD")
				&& !method.equals("POST")) {
			throw new MethodNotSupportedException(method
					+ " method not supported");
		}
		String target = request.getRequestLine().getUri();

		if (request instanceof HttpEntityEnclosingRequest) {
			storeFile((HttpEntityEnclosingRequest) request);

		}

		final File file = new File(this.docRoot, URLDecoder.decode(target));
		if (!file.exists()) {
			response.setStatusCode(HttpStatus.SC_NOT_FOUND);
			EntityTemplate body = new EntityTemplate(new ContentProducer() {

				public void writeTo(final OutputStream outstream)
						throws IOException {
					OutputStreamWriter writer = new OutputStreamWriter(
							outstream, "UTF-8");
					writer.write("<html><body><h1>");
					writer.write("File ");
					writer.write(file.getPath());
					writer.write(" not found");
					writer.write("</h1></body></html>");
					writer.flush();
				}

			});
			body.setContentType("text/html; charset=UTF-8");
			response.setEntity(body);
			System.out.println("File " + file.getPath() + " not found");

		} else if (file.isDirectory()) {
			response.setStatusCode(HttpStatus.SC_OK);
			EntityTemplate body = new EntityTemplate(new ContentProducer() {
				public void writeTo(final OutputStream outstream)
						throws IOException {
					OutputStreamWriter writer = new OutputStreamWriter(
							outstream, "UTF-8");
					List<FileItem> fileItems = new FileItemService()
							.getFileItems(file.getAbsolutePath());

					StringBuffer tds = new StringBuffer();
					for (FileItem fileItem : fileItems) {
						tds.append(fileItem.toString());
					}
					String html = null;
					try {
						html = UiTemplate.generateHtml(file.getName(), tds
								.toString());
					} catch (URISyntaxException e) {
						e.printStackTrace();
					}
					writer.write(html);
					writer.flush();
				}

			});
			body.setContentType("text/html; charset=UTF-8");
			response.setEntity(body);
			System.out.println("Cannot read file " + file.getPath());

		} else if (!file.canRead()) {
			response.setStatusCode(HttpStatus.SC_FORBIDDEN);
			EntityTemplate body = new EntityTemplate(new ContentProducer() {

				public void writeTo(final OutputStream outstream)
						throws IOException {
					OutputStreamWriter writer = new OutputStreamWriter(
							outstream, "UTF-8");
					writer.write("<html><body><h1>");
					writer.write("Access denied");
					writer.write("</h1></body></html>");
					writer.flush();
				}

			});
			body.setContentType("text/html; charset=UTF-8");
			response.setEntity(body);
			System.out.println("Cannot read file " + file.getPath());

		} else {
			response.setStatusCode(HttpStatus.SC_OK);
			FileEntity body = new FileEntity(file, "APPLICATION/OCTET-STREAM");
			response.setEntity(body);
			System.out.println("Serving file " + file.getPath());

		}
	}

	private void storeFile(HttpEntityEnclosingRequest request)
			throws IllegalStateException, IOException {
		long start =System.currentTimeMillis();
		int data = 0;
		long readed = 0;
		StringBuffer headerBuffer = new StringBuffer();
		HttpEntity entity = request.getEntity();
		InputStream inputStream =  entity
				.getContent();
		while ((data = inputStream.read()) != -1) {
			readed++;
			char ch = (char) data;
			headerBuffer.append((char) data);
			if (ch == 10) {
				if (headerBuffer.substring(headerBuffer.length() - 4).equals(
						Constants.BLANK_LINE)) {
					break;
				}
			}
		}
		long contentLength = entity.getContentLength();
		long boundaryLength = getBoundaryLength(headerBuffer.toString());
		String path = Constants.ROOT_DIR + request.getRequestLine().getUri();
		String filename = headerBuffer.substring(headerBuffer
				.indexOf("filename=\"") + 10, headerBuffer.lastIndexOf("\""));
		if (filename.contains("\\")) {
			filename = filename.substring(filename.lastIndexOf("\\") + 1);
		}
		filename = path + "/"
				+ new String(filename.getBytes("ISO-8859-1"), "UTF-8");
		File file = new File(filename);
		OutputStream outputStream = new FileOutputStream(file);
		int tempSize = 1024 * 8;
		byte[] temp = new byte[tempSize];
		long fileByteCount = contentLength - readed - (boundaryLength + 6);
		long fileByteLoaded = 0;
		int count=0;
		while(fileByteLoaded <= fileByteCount){
			long last = fileByteCount-fileByteLoaded;
			if(last<temp.length){
				temp=new byte[(int) last];
				count=inputStream.read(temp, 0, temp.length);
				outputStream.write(temp, 0, count);
				outputStream.flush();
				break;
			}
			if((count=inputStream.read(temp, 0, temp.length))>0){
				fileByteLoaded+=count;
				outputStream.write(temp, 0, count);
				outputStream.flush();
			}
		}
		outputStream.close();
		System.out.println("speed: "+(fileByteCount/(System.currentTimeMillis()-start)));
	}

	private long getBoundaryLength(String string) {
		String[] lines = string.split("\r\n");
		return Long.valueOf(lines[0].length());
	}
}
