package tech.grasshopper.processor;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import lombok.Builder;
import tech.grasshopper.pojo.Attachment;

public class HtmlParser {

	private Document document;

	private Path filePath;

	@Builder
	public HtmlParser(Path filePath) {
		this.filePath = filePath;
	}

	public void initialize() throws IOException {
		document = Jsoup.parse(filePath.toFile(), null);
	}

	public String retrieveMethodUrlOrStatusText() {
		Element elem = document.selectFirst("body > div");
		if (elem == null)
			throw new IllegalArgumentException("Http method, endpoint or status code not available.");
		return elem.text();
	}

	public String retrieveBodyContent() {
		Elements body = document.getElementsContainingOwnText(Attachment.BODY);
		return (body.isEmpty()) ? "" : body.get(0).nextElementSibling().child(0).text();
	}

	public Map<String, String> retrieveHeadersContent() {
		return retrieveHeadersAndCookies(Attachment.HEADERS);
	}

	public Map<String, String> retrieveCookiesContent() {
		return retrieveHeadersAndCookies(Attachment.COOKIES);
	}

	private Map<String, String> retrieveHeadersAndCookies(String type) {
		Elements elements = document.getElementsContainingOwnText(type);
		Map<String, String> data = new HashMap<>();

		if (!elements.isEmpty()) {
			Elements details = elements.get(0).nextElementSibling().children();
			details.forEach(e -> {
				if (e.text().contains(":")) {
					String[] detail = e.text().split(":");
					data.put(detail[0], detail[1]);
				}
			});
		}
		return data;
	}
}
