package et.gov.ethgov.platform.web;

import et.gov.ethgov.platform.web.filter.RequestContextFilter;
import et.gov.ethgov.platform.web.problem.EthGovExceptionHandler;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@Import({EthGovExceptionHandler.class, RequestContextFilter.class})
public class EthGovWebAutoConfiguration {}
