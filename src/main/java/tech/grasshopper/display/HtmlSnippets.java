package tech.grasshopper.display;

import java.util.List;
import java.util.Map;

import org.apache.commons.text.StringEscapeUtils;

import tech.grasshopper.pojo.Attachment;

public class HtmlSnippets {

	private static String borderStyle = "border: 1px solid black; ";
	private static String textStyle = "padding: 3px; vertical-align:top; text-align:left; ";

	private static String tableTagStyle = "<table style='" + borderStyle + " width:50%'>";
	private static String tdTagNameStyle = "<td style='" + borderStyle + textStyle + " width:30%'>";
	private static String tdTagValueStyle = "<td style='" + borderStyle + textStyle + " width:70%'>";

	private static String tableMultiPartsTagStyle = "<table style='" + borderStyle + " width:100%'>";
	private static String tdMultiPartsTagNameStyle = "<td style='" + borderStyle + textStyle + "'>";

	private static String trTagHeadingStyle = "<tr style='font-style: italic;'>";

	public static StringBuffer dataFileLink(String link, String text) {
		StringBuffer sbr = new StringBuffer();

		return sbr.append("<span data=\"").append(link).append("\" type=\"").append(text)
				.append("\"><a href='#' onClick=\"window.open('").append(link)
				.append("','','width=700,height=500'); return false;\">").append(text)
				.append("</a></span> &nbsp;&nbsp;&nbsp;");
	}

	public static StringBuffer bodyContent(String content) {
		StringBuffer sbr = new StringBuffer();

		return sbr.append("<div><b>").append(Attachment.BODY).append("</b></div>").append("<div><pre>")
				.append(StringEscapeUtils.escapeHtml4(content)).append("</pre></div>");
	}

	public static StringBuffer headersCookiesParametersContent(Map<String, String> data, String type) {
		StringBuffer sbr = new StringBuffer();
		sbr.append("<div><b>").append(type).append("</b></div>");
		sbr.append(tableTagStyle);

		sbr.append(trTagHeadingStyle).append(tdTagNameStyle).append("Name").append("</td>").append(tdTagValueStyle)
				.append("Value").append("</td>").append("</tr>");

		data.forEach((k, v) -> {
			sbr.append("<tr>").append(tdTagNameStyle).append(StringEscapeUtils.escapeHtml4(k)).append("</td>")
					.append(tdTagValueStyle).append(StringEscapeUtils.escapeHtml4(v)).append("</td>").append("</tr>");
		});
		sbr.append("</table><br>");
		return sbr;
	}

	public static StringBuffer multiPartsContent(List<Map<String, String>> data) {
		StringBuffer sbr = new StringBuffer();
		sbr.append("<div><b>").append(Attachment.MULTIPARTS).append("</b></div>");

		sbr.append(tableMultiPartsTagStyle);
		sbr.append(trTagHeadingStyle).append(tdMultiPartsTagNameStyle).append("Input Name").append("</td>")
				.append(tdMultiPartsTagNameStyle).append("Content").append("</td>").append(tdMultiPartsTagNameStyle)
				.append("Mime-Type").append("</td>").append(tdMultiPartsTagNameStyle).append("File Name")
				.append("</td>").append("</tr>");

		data.forEach(d -> {
			sbr.append("<tr>").append(tdMultiPartsTagNameStyle).append(StringEscapeUtils.escapeHtml4(d.get("name")))
					.append("</td>").append(tdMultiPartsTagNameStyle)
					.append(StringEscapeUtils.escapeHtml4(d.get("content"))).append("</td>")
					.append(tdMultiPartsTagNameStyle).append(StringEscapeUtils.escapeHtml4(d.get("mime")))
					.append("</td>").append(tdMultiPartsTagNameStyle)
					.append(StringEscapeUtils.escapeHtml4(d.get("file"))).append("</td>").append("</tr>");
		});
		sbr.append("</table><br>");
		return sbr;
	}

	public static StringBuffer addHtmlBodyTag(StringBuffer content) {
		StringBuffer sbr = new StringBuffer();
		return sbr.append("<html><body style=\"margin:15;padding:0\">").append(content).append("</body></html>");
	}
}
