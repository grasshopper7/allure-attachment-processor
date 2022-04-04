package tech.grasshopper.processor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import lombok.Builder;
import tech.grasshopper.pojo.Attachment;
import tech.grasshopper.pojo.AttachmentData;
import tech.grasshopper.pojo.HttpData;
import tech.grasshopper.pojo.HttpLogData;
import tech.grasshopper.pojo.HttpRequestData;
import tech.grasshopper.pojo.HttpResponseData;

public class AttachmentProcessor {

	private HttpData httpData;
	private HttpLogData log;
	private List<HttpLogData> httpLogData;
	private String fileNamePrefix = "";
	private AttachmentData data;

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

			if (!attachment.getSource().endsWith(Attachment.FILENAME_SUFFIX)) {
				logger.info("Skipping attachment as file name not correct - " + path.toString());
				continue;
			}

			try {
				Gson gson = new Gson();
				data = gson.fromJson(Files.newBufferedReader(path), AttachmentData.class);
			} catch (JsonSyntaxException | JsonIOException | IOException e) {
				logger.info(String.format(
						"Skipping attachment at '%s', as unable to parse result to AttachmentData pojo.", path));
				continue;
			}

			try {
				httpData = HttpData.createHttpData(data);
			} catch (IllegalArgumentException e) {
				// Should never happen
				logger.info(String.format("Skipping attachment at '%s', due to invalid name.", path));
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
			processHeadersAndCookies();
		} catch (IOException e) {
			logger.info(e.getMessage() + " Skipping headers and cookies for - " + path.toString());
		}
		try {
			processAllParameters();
		} catch (IOException e) {
			logger.info(e.getMessage() + " Skipping parameters for - " + path.toString());
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
		String content = data.getBody();
		if (content.length() > 0) {
			AttachmentContentProcessor.builder().fileNamePrefix(fileNamePrefix).reportDirectory(reportDirectory).build()
					.processBodyContent(content);
			httpData.setBodyContentFile(fileNamePrefix);
		}
	}

	private void processHeadersAndCookies() throws IOException {
		Map<String, String> headers = data.getHeaders();
		Map<String, String> cookies = data.getCookies();

		if (headers.isEmpty() && cookies.isEmpty())
			return;

		Map<String, Map<String, String>> headersAndCookies = new HashMap<>();
		if (!headers.isEmpty())
			headersAndCookies.put(Attachment.HEADERS, headers);
		if (!cookies.isEmpty())
			headersAndCookies.put(Attachment.COOKIES, cookies);

		AttachmentContentProcessor.builder().fileNamePrefix(fileNamePrefix).reportDirectory(reportDirectory).build()
				.processHeadersAndCookiesContent(headersAndCookies);
		httpData.setHeadersAndCookiesContentFile(fileNamePrefix);
	}

	private void processAllParameters() throws IOException {
		Map<String, Map<String, String>> parameters = data.getAllParameters();
		List<Map<String, String>> parts = data.getMultiParts();

		if (parameters.isEmpty() && parts.isEmpty())
			return;

		AttachmentContentProcessor.builder().fileNamePrefix(fileNamePrefix).reportDirectory(reportDirectory).build()
				.processAllParametersContent(parameters, parts);
		httpData.setAllParametersContentFile(fileNamePrefix);
	}
}
