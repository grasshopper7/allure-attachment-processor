package tech.grasshopper.pojo;

import java.util.Map;

import lombok.Builder.Default;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public abstract class HttpData {

	@Default
	private String bodyContentFile = "";

	@Default
	private String headersContentFile = "";

	@Default
	private String cookiesContentFile = "";

	public void setBodyContentFile(String fileNamePrefix) {
		this.bodyContentFile = contentFileName(fileNamePrefix, Attachment.BODY);
	}

	public void setHeadersContentFile(String fileNamePrefix) {
		this.headersContentFile = contentFileName(fileNamePrefix, Attachment.HEADERS);
	}

	public void setCookiesContentFile(String fileNamePrefix) {
		this.cookiesContentFile = contentFileName(fileNamePrefix, Attachment.COOKIES);
	}

	private String contentFileName(String fileNamePrefix, String type) {
		// Figure out why fileseparator and paths.get does not work
		StringBuffer sbr = new StringBuffer(Attachment.REPORT_DATA_DIRECTORY);
		return sbr.append("/").append(fileNamePrefix).append(Attachment.FILENAME_SEPARATOR).append(type).append(".html")
				.toString();
	}

	public static HttpData createHttpData(String title) {
		String[] details = title.split(" ");

		// Status code 200
		if (title.startsWith("Status code"))
			return HttpResponseData.builder().statusCode(details[2]).build();

		// GET to https://ghchirp.tech/test/blog
		return HttpRequestData.builder().httpMethod(details[0]).endpoint(details[2]).build();
	}

	protected int rowCount() {
		if (containsHttpContentFiles())
			return 1;
		return 0;
	}

	public boolean containsHttpContentFiles() {
		if (bodyContentFile.isEmpty() && headersContentFile.isEmpty() && cookiesContentFile.isEmpty())
			return false;
		return true;
	}

	public abstract void addPropertiesDisplay(Map<String, String> details);

	public abstract void addHttpContentFilesDisplay(Map<String, String> details);

	public void addHeadersContentFileLink(String type, Map<String, String> details) {
		if (!getHeadersContentFile().isEmpty())
			details.put(type + " Headers", getHeadersContentFile());
	}

	public void addCookiesContentFileLink(String type, Map<String, String> details) {
		if (!getCookiesContentFile().isEmpty())
			details.put(type + " Cookies", getCookiesContentFile());
	}

	public void addBodyContentFileLink(String type, Map<String, String> details) {
		if (!getBodyContentFile().isEmpty())
			details.put(type + " Body", getBodyContentFile());
	}

	protected String createFileLinks() {
		StringBuffer sbr = new StringBuffer();

		if (containsHttpContentFiles()) {
			if (!bodyContentFile.isEmpty())
				sbr.append(createFileLink(bodyContentFile, "Body"));
			if (!headersContentFile.isEmpty())
				sbr.append(createFileLink(headersContentFile, "Headers"));
			if (!cookiesContentFile.isEmpty())
				sbr.append(createFileLink(cookiesContentFile, "Cookies"));
		}
		return sbr.toString();
	}

	private String createFileLink(String link, String linkText) {
		StringBuffer sbr = new StringBuffer();
		return sbr.append("<span data=\"").append(link).append("\" type=\"").append(linkText)
				.append("\"><a href='#' onClick=\"window.open('").append(link)
				.append("','','width=700,height=500'); return false;\">").append(linkText)
				.append("</a></span> &nbsp;&nbsp;&nbsp;").toString();
	}
}
