package com.example.switchyard.soap.model;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.xml.Namespaces;
import org.apache.camel.model.ModelCamelContext;
import org.apache.camel.processor.RedeliveryPolicy;

public class CamelServiceRoute extends RouteBuilder {

	Namespaces ns = new Namespaces("bank", "http://ws.camelcookbook.org/payment-service/types");
	
	
	/**
	 * The Camel route is configured via this method. The from endpoint is
	 * required to be a SwitchYard service.
	 */
	public void configure() {
		errorHandler(loggingErrorHandler("Camel"));

		// onException(NullPointerException.class).maximumRedeliveries(0).to;

		// @formatter:off
		from("switchyard://Payment").log("Received message: ${body}")
				.to("direct://RequestPipeline")
				.log("Calling response pipeline")
				.to("direct://ResponsePipeline").end();

		from("direct://RequestPipeline")
			.log("In the request pipeline: ${body}")
			.setHeader("bank", xpath("//typ:bank/text()").namespaces(ns))
			.process(new Processor() {
					TransferResponse resp = new TransferResponse();

					@Override
					public void process(Exchange exchange) throws Exception {
						TransferRequest req = (TransferRequest) exchange
								.getIn().getBody();
						resp.setReply("NO OK: " + req.getAmount());
						// }else{
						// resp.setReply("OK");
						// }
						exchange.getIn().setBody(resp, TransferResponse.class);
					}
				})
		.end();

		from("direct://ResponsePipeline")
			.log("In the response pipeline: ${body}")
			.log("Bank in header: ${header.bank}")
		.end();
		// @formatter:on
	}

}
