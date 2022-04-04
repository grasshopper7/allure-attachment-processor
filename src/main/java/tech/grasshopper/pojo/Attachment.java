package tech.grasshopper.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Attachment {

	private String name = "";

	private String source = "";

	private String type = "";

	public static final String REPORT_DATA_DIRECTORY = "data";

	public static final String BODY = "Body";
	public static final String HEADERS = "Headers";
	public static final String COOKIES = "Cookies";
	public static final String PARAMETERS = "Parameters";
	public static final String MULTIPARTS = "Multi Parts";
	public static final String HEADERSANDCOOKIES = "Headers And Cookies";
	public static final String ALLPARAMETERS = "All Parameters";

	public static final String FILENAME_SUFFIX = "-attachment.json";
	public static final String FILENAME_SEPARATOR = "-";
}
