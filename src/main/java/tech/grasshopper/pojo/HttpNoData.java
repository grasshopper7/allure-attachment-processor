package tech.grasshopper.pojo;

import java.util.Map;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

public interface HttpNoData {

	public default int rowCount() {
		return 0;
	}

	public default boolean containsHttpContentFiles() {
		return false;
	}

	public default void addPropertiesDisplay(Map<String, String> details) {

	}

	public default void addHttpContentFilesDisplay(Map<String, String> details) {

	}

	@Data
	@SuperBuilder
	@EqualsAndHashCode(callSuper = true)
	public class HttpNoRequestData extends HttpRequestData implements HttpNoData {

	}

	@Data
	@SuperBuilder
	@EqualsAndHashCode(callSuper = true)
	public class HttpNoResponseData extends HttpResponseData implements HttpNoData {

	}
}
