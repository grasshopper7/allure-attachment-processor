package tech.grasshopper.pojo;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import tech.grasshopper.pojo.HttpNoData.HttpNoRequestData;
import tech.grasshopper.pojo.HttpNoData.HttpNoResponseData;

@Data
@Builder
public class HttpLogData {

	@Default
	private HttpRequestData httpRequestData = HttpNoRequestData.builder().build();

	@Default
	private HttpResponseData httpResponseData = HttpNoResponseData.builder().build();
}
