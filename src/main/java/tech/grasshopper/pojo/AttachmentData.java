package tech.grasshopper.pojo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class AttachmentData {

	public static final String ATTACHMENT_REQUEST_NAME = "Request";
	public static final String ATTACHMENT_RESPONSE_NAME = "Response";

	public static final String REQUEST_PARAMETERS = "Request Parameters";
	public static final String QUERY_PARAMETERS = "Query Parameters";
	public static final String FORM_PARAMETERS = "Form Parameters";
	public static final String PATH_PARAMETERS = "Path Parameters";

	private String name = "";

	private String url = "";

	private String method = "";

	private String body = "";

	private String responseCode = "";

	private Map<String, String> headers = new HashMap<>();

	private Map<String, String> cookies = new HashMap<>();

	private Map<String, String> requestParameters = new HashMap<>();

	private Map<String, String> queryParameters = new HashMap<>();

	private Map<String, String> formParameters = new HashMap<>();

	private Map<String, String> pathParameters = new HashMap<>();

	private List<Map<String, String>> multiParts = new ArrayList<>();

	public Map<String, Map<String, String>> getAllParameters() {
		Map<String, Map<String, String>> params = new LinkedHashMap<>();

		if (!requestParameters.isEmpty())
			params.put(REQUEST_PARAMETERS, requestParameters);
		if (!queryParameters.isEmpty())
			params.put(QUERY_PARAMETERS, queryParameters);
		if (!formParameters.isEmpty())
			params.put(FORM_PARAMETERS, formParameters);
		if (!pathParameters.isEmpty())
			params.put(PATH_PARAMETERS, pathParameters);

		return params;
	}
}
