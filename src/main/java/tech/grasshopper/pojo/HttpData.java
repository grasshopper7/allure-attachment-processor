package tech.grasshopper.pojo;

import java.util.Map;

import lombok.Builder.Default;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import static tech.grasshopper.display.HtmlSnippets.*;

@Getter
@SuperBuilder
public abstract class HttpData {

	@Default
	private String bodyContentFile = "";

	@Default
	private String headersAndCookiesContentFile = "";

	@Default
	private String allParametersContentFile = "";

	public void setBodyContentFile(String fileNamePrefix) {
		this.bodyContentFile = contentFileName(fileNamePrefix, Attachment.BODY);
	}

	public void setHeadersAndCookiesContentFile(String fileNamePrefix) {
		this.headersAndCookiesContentFile = contentFileName(fileNamePrefix, Attachment.HEADERSANDCOOKIES);
	}

	public void setAllParametersContentFile(String fileNamePrefix) {
		this.allParametersContentFile = contentFileName(fileNamePrefix, Attachment.ALLPARAMETERS);
	}

	private String contentFileName(String fileNamePrefix, String type) {
		// Figure out why fileseparator and paths.get does not work
		StringBuffer sbr = new StringBuffer(Attachment.REPORT_DATA_DIRECTORY);
		return sbr.append("/").append(fileNamePrefix).append(Attachment.FILENAME_SEPARATOR).append(type).append(".html")
				.toString();
	}

	public static HttpData createHttpData(AttachmentData data) {
		if (data.getName().equalsIgnoreCase(AttachmentData.ATTACHMENT_RESPONSE_NAME))
			return HttpResponseData.builder().statusCode(data.getResponseCode()).build();

		else if (data.getName().equalsIgnoreCase(AttachmentData.ATTACHMENT_REQUEST_NAME))
			return HttpRequestData.builder().httpMethod(data.getMethod()).endpoint(data.getUrl()).build();

		throw new IllegalArgumentException("Attachment data name is invalid.");
	}

	protected int rowCount() {
		if (containsHttpContentFiles())
			return 1;
		return 0;
	}

	public boolean containsHttpContentFiles() {
		if (bodyContentFile.isEmpty() && headersAndCookiesContentFile.isEmpty() && allParametersContentFile.isEmpty())
			return false;
		return true;
	}

	public abstract void addPropertiesDisplay(Map<String, String> details);

	public abstract void addHttpContentFilesDisplay(Map<String, String> details);

	protected void createFileLinks(String type, Map<String, String> details) {
		StringBuffer sbr = new StringBuffer();

		if (containsHttpContentFiles()) {
			if (!bodyContentFile.isEmpty()) {
				sbr.append(dataFileLink(bodyContentFile, "Body"));
				details.put(type + " Body", bodyContentFile);
			}
			if (!headersAndCookiesContentFile.isEmpty()) {
				sbr.append(dataFileLink(headersAndCookiesContentFile, "Headers & Cookies"));
				details.put(type + " Headers & Cookies", headersAndCookiesContentFile);
			}
			if (!allParametersContentFile.isEmpty()) {
				sbr.append(dataFileLink(allParametersContentFile, "Parameters"));
				details.put(type + " Parameters", allParametersContentFile);
			}
		}
		details.put(type, sbr.toString());
	}
}
