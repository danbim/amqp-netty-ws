package de.danbim.amqpnettyws;

import de.uniluebeck.itm.tr.util.Logging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AmqpNettyWsCli {

	static {
		Logging.setDebugLoggingDefaults();
	}

	private static final Logger log = LoggerFactory.getLogger(AmqpNettyWsCli.class);

	public static void main(String[] args) {

		final AmqpNettyWsConfig config = new AmqpNettyWsConfig(
				1234,
				"localhost", 5672, "/",
				"guest", "guest",
				"amq.fanout", "fanout", true, ""
		);
		final AmqpNettyWs amqpNettyWs = new AmqpNettyWs(config);

		Runtime.getRuntime().addShutdownHook(new Thread("ShutdownThread") {
			@Override
			public void run() {
				try {
					amqpNettyWs.stop().get();
				} catch (Exception e) {
					log.error("{}", e);
				}
			}
		}
		);

		try {
			amqpNettyWs.start().get();
		} catch (Exception e) {
			log.error("{}", e);
		}

	}

}
