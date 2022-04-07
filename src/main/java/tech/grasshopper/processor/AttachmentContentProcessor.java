package tech.grasshopper.processor;

import static tech.grasshopper.display.HtmlSnippets.addHtmlBodyTag;
import static tech.grasshopper.display.HtmlSnippets.bodyContent;
import static tech.grasshopper.display.HtmlSnippets.headersCookiesParametersContent;
import static tech.grasshopper.display.HtmlSnippets.multiPartsContent;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import lombok.Builder;
import tech.grasshopper.pojo.Attachment;

@Builder
public class AttachmentContentProcessor {

	private String fileNamePrefix;

	private String reportDirectory;

	public void processBodyContent(String content) throws IOException {
		createDisplayFiles(bodyContent(content), Attachment.BODY);
	}

	public void processHeadersAndCookiesContent(Map<String, Map<String, String>> data) throws IOException {
		StringBuffer sbr = new StringBuffer();

		data.forEach((k, v) -> sbr.append(headersCookiesParametersContent(v, k)));
		createDisplayFiles(sbr, Attachment.HEADERSANDCOOKIES);
	}

	public void processAllParametersContent(Map<String, Map<String, String>> parameters,
			List<Map<String, String>> parts) throws IOException {
		StringBuffer sbr = new StringBuffer();

		parameters.forEach((k, v) -> sbr.append(headersCookiesParametersContent(v, k)));

		if (!parts.isEmpty())
			sbr.append(multiPartsContent(parts));

		if (sbr.length() == 0)
			return;
		createDisplayFiles(sbr, Attachment.ALLPARAMETERS);
	}

	private void createDisplayFiles(StringBuffer content, String fileNameSuffix) throws IOException {
		StringBuffer sbr = new StringBuffer();
		sbr.append(addHtmlBodyTag(content));

		StringBuffer sbrFile = new StringBuffer().append(fileNamePrefix).append(Attachment.FILENAME_SEPARATOR)
				.append(fileNameSuffix).append(".html");

		Path path = Paths.get(reportDirectory, Attachment.REPORT_DATA_DIRECTORY, sbrFile.toString());

		try (FileOutputStream outputStream = new FileOutputStream(path.toString())) {
			outputStream.write(sbr.toString().getBytes());
		} catch (IOException e) {
			throw new IOException(String.format("Unable to process %s%s%s content for display.", fileNamePrefix,
					Attachment.FILENAME_SEPARATOR, fileNameSuffix));
		}
	}
}
