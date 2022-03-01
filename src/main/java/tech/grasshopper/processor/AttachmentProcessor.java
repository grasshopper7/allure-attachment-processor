package tech.grasshopper.processor;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import lombok.Builder;
import tech.grasshopper.pojo.Attachment;
import tech.grasshopper.pojo.HttpData;
import tech.grasshopper.pojo.HttpLogData;
import tech.grasshopper.pojo.HttpRequestData;
import tech.grasshopper.pojo.HttpResponseData;

public class AttachmentProcessor {

	private HttpData httpData;
	private HttpLogData log;
	private List<HttpLogData> httpLogData;
	private String fileNamePrefix = "";
	private HtmlParser htmlParser;

	private String allureResultsDirectory;

	private String reportDirectory;

	@Builder
	public AttachmentProcessor(String allureResultsDirectory, String reportDirectory) {
		this.allureResultsDirectory = allureResultsDirectory;
		this.reportDirectory = reportDirectory;
	}

	private final static Logger logger = Logger.getLogger(AttachmentProcessor.class.getName());

	public List<HttpLogData> process(List<Attachment> attachments) {
		httpLogData = new ArrayList<>();

		for (Attachment attachment : attachments) {
			Path path = Paths.get(allureResultsDirectory, attachment.getSource());

			try {
				htmlParser = HtmlParser.builder().filePath(path).build();
				htmlParser.initialize();
			} catch (IOException e) {
				logger.info("Skipping attachment as unable to access file - " + path.toString());
				continue;
			}

			String methodUrlOrStatusTxt = "";
			try {
				methodUrlOrStatusTxt = htmlParser.retrieveMethodUrlOrStatusText();
			} catch (IllegalArgumentException e) {
				logger.info(e.getMessage() + " Skipping attachment - " + path.toString());
				continue;
			}

			httpData = HttpData.createHttpData(methodUrlOrStatusTxt);

			if (attachment.getSource().indexOf(Attachment.FILENAME_SEPARATOR) == -1) {
				logger.info("Skipping attachment as file name not correct - " + path.toString());
				continue;
			}
			fileNamePrefix = retrieveFileNamePrefix(attachment.getSource());

			createOrUpdateHttpLogData();
			processContent(path);
		}
		return httpLogData;
	}

	private void processContent(Path path) {
		// Refactor this code
		try {
			processBodyContent();
		} catch (IOException e) {
			logger.info(e.getMessage() + " Skipping body for - " + path.toString());
		}
		try {
			processHeaders();
		} catch (IOException e) {
			logger.info(e.getMessage() + " Skipping headers for - " + path.toString());
		}
		try {
			processCookies();
		} catch (IOException e) {
			logger.info(e.getMessage() + " Skipping cookies for - " + path.toString());
		}
	}

	private String retrieveFileNamePrefix(String source) {
		return source.substring(0, source.lastIndexOf('-'));
	}

	private void createOrUpdateHttpLogData() {
		if (httpData instanceof HttpRequestData) {
			log = HttpLogData.builder().build();
			log.setHttpRequestData((HttpRequestData) httpData);
			httpLogData.add(log);
		} else
			log.setHttpResponseData((HttpResponseData) httpData);
	}

	private void processBodyContent() throws IOException {
		String content = htmlParser.retrieveBodyContent();
		if (content.length() > 0) {
			AttachmentContentProcessor.builder().fileNamePrefix(fileNamePrefix).reportDirectory(reportDirectory).build()
					.processBodyContent(content);
			httpData.setBodyContentFile(fileNamePrefix);
		}
	}

	private void processHeaders() throws IOException {
		Map<String, String> headers = htmlParser.retrieveHeadersContent();
		if (!headers.isEmpty()) {
			AttachmentContentProcessor.builder().fileNamePrefix(fileNamePrefix).reportDirectory(reportDirectory).build()
					.processHeadersContent(headers);
			httpData.setHeadersContentFile(fileNamePrefix);
		}
	}

	private void processCookies() throws IOException {
		Map<String, String> cookies = htmlParser.retrieveCookiesContent();
		if (!cookies.isEmpty()) {
			AttachmentContentProcessor.builder().fileNamePrefix(fileNamePrefix).reportDirectory(reportDirectory).build()
					.processCookiesContent(cookies);
			httpData.setCookiesContentFile(fileNamePrefix);
		}
	}
}
