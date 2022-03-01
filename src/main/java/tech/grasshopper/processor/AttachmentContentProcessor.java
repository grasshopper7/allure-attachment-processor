package tech.grasshopper.processor;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.apache.commons.text.StringEscapeUtils;

import lombok.Builder;
import tech.grasshopper.pojo.Attachment;

@Builder
public class AttachmentContentProcessor {

	private String fileNamePrefix;

	private String reportDirectory;

	public void processBodyContent(String content) throws IOException {
		StringBuffer sbr = new StringBuffer();
		sbr.append("<div><pre>").append(StringEscapeUtils.escapeHtml4(content)).append("</pre></div>");
		createDisplayFiles(sbr, Attachment.BODY);
	}

	public void processHeadersContent(Map<String, String> data) throws IOException {
		StringBuffer sbr = processHeadersAndCookiesContent(data);
		createDisplayFiles(sbr, Attachment.HEADERS);
	}

	public void processCookiesContent(Map<String, String> data) throws IOException {
		StringBuffer sbr = processHeadersAndCookiesContent(data);
		createDisplayFiles(sbr, Attachment.COOKIES);
	}

	private StringBuffer processHeadersAndCookiesContent(Map<String, String> data) {
		StringBuffer sbr = new StringBuffer();

		sbr.append("<table style=\"border: 1px solid black;\">");
		data.forEach((k, v) -> {
			sbr.append("<tr style=\"border: 1px solid black;\">").append("<td style=\"border: 1px solid black;\">")
					.append(StringEscapeUtils.escapeHtml4(k)).append("</td>")
					.append("<td style=\"border: 1px solid black;\">").append(StringEscapeUtils.escapeHtml4(v))
					.append("</td>").append("</tr>");
		});
		sbr.append("</table>");
		return sbr;
	}

	private void createDisplayFiles(StringBuffer content, String fileNameSuffix) throws IOException {
		StringBuffer sbr = new StringBuffer();
		sbr.append("<html><body>").append(content).append("</body></html>");

		StringBuffer sbrFile = new StringBuffer().append(fileNamePrefix).append(Attachment.FILENAME_SEPARATOR)
				.append(fileNameSuffix).append(".html");

		Path path = Paths.get(reportDirectory, Attachment.REPORT_DATA_DIRECTORY, sbrFile.toString());

		try (FileOutputStream outputStream = new FileOutputStream(path.toString())) {
			outputStream.write(content.toString().getBytes());
		} catch (IOException e) {
			throw new IOException("Unable to process " + fileNameSuffix + " content for display.");
		}
	}
}
