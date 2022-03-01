package tech.grasshopper.pojo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

import lombok.Builder.Default;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class HttpRequestData extends HttpData {

	@Default
	private String httpMethod = "";

	@Default
	private String endpoint = "";

	@Override
	public int rowCount() {
		return 2 + super.rowCount();
	}

	@Override
	public void addPropertiesDisplay(Map<String, String> details) {
		details.put("Method", httpMethod);
		details.put("Endpoint", endpoint);
	}

	@Override
	public void addHttpContentFilesDisplay(Map<String, String> details) {
		details.put("Request", createFileLinks());
	}
}
