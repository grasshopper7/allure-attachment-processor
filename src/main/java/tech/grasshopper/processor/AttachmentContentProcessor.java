package tech.grasshopper.processor;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
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

		sbr.append("<div><b>").append(Attachment.BODY).append("</b></div>");
		sbr.append("<div><pre>").append(StringEscapeUtils.escapeHtml4(content)).append("</pre></div>");
		createDisplayFiles(sbr, Attachment.BODY);
	}

	public void processHeadersAndCookiesContent(Map<String, Map<String, String>> data) throws IOException {
		StringBuffer sbr = new StringBuffer();

		data.forEach((k, v) -> {
			sbr.append(processHeadersCookiesParametersContent(v, k));
		});
		createDisplayFiles(sbr, Attachment.HEADERSANDCOOKIES);
	}

	public void processAllParametersContent(Map<String, Map<String, String>> parameters,
			List<Map<String, String>> parts) throws IOException {
		StringBuffer sbr = new StringBuffer();

		parameters.forEach((k, v) -> {
			sbr.append(processHeadersCookiesParametersContent(v, k));
		});

		if (!parts.isEmpty())
			sbr.append(processMultiParts(parts));

		if (sbr.length() == 0)
			return;

		createDisplayFiles(sbr, Attachment.ALLPARAMETERS);
	}

	private StringBuffer processMultiParts(List<Map<String, String>> data) {
		StringBuffer sbr = new StringBuffer();
		sbr.append("<div><b>").append(Attachment.MULTIPARTS).append("</b></div>");
		sbr.append("<table style=\"border: 1px solid black;\">");

		sbr.append("<tr>").append("<td style=\"border: 1px solid black;\">").append("Input Name").append("</td>")
				.append("<td style=\"border: 1px solid black;\">").append("Content").append("</td>")
				.append("<td style=\"border: 1px solid black;\">").append("Mime-Type").append("</td>")
				.append("<td style=\"border: 1px solid black;\">").append("File Name").append("</td>").append("</tr>");

		data.forEach(d -> {
			sbr.append("<tr>").append("<td style=\"border: 1px solid black;\">")
					.append(StringEscapeUtils.escapeHtml4(d.get("name"))).append("</td>")
					.append("<td style=\"border: 1px solid black;\">")
					.append(StringEscapeUtils.escapeHtml4(d.get("content"))).append("</td>")
					.append("<td style=\"border: 1px solid black;\">")
					.append(StringEscapeUtils.escapeHtml4(d.get("mime"))).append("</td>")
					.append("<td style=\"border: 1px solid black;\">")
					.append(StringEscapeUtils.escapeHtml4(d.get("file"))).append("</td>").append("</tr>");
		});
		sbr.append("</table><br>");
		return sbr;
	}

	private StringBuffer processHeadersCookiesParametersContent(Map<String, String> data, String type) {
		StringBuffer sbr = new StringBuffer();
		sbr.append("<div><b>").append(type).append("</b></div>");
		sbr.append("<table style=\"border: 1px solid black;\">");
		data.forEach((k, v) -> {
			sbr.append("<tr>").append("<td style=\"border: 1px solid black;\">")
					.append(StringEscapeUtils.escapeHtml4(k)).append("</td>")
					.append("<td style=\"border: 1px solid black;\">").append(StringEscapeUtils.escapeHtml4(v))
					.append("</td>").append("</tr>");
		});
		sbr.append("</table><br>");
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
