package de.danbim.amqpnettyws;

import de.danbim.amqpnettyws.util.Log4JLevelOptionHandler;
import org.apache.log4j.Level;
import org.kohsuke.args4j.Option;

public class AmqpNettyWsConfig {

	@Option(name = "--websocketPort", usage = "port to listen on for websocket connections")
	public int websocketPort = 8080;

	@Option(name = "--brokerHost", usage = "broker hostname", required = true)
	public String brokerHost;

	@Option(name = "--brokerPort", usage = "broker port")
	public int brokerPort = 5672;

	@Option(name = "--brokerVirtualHost", usage = "broker virtual host")
	public String brokerVirtualHost = "";

	@Option(name = "--brokerUsername", usage = "broker username")
	public String brokerUsername = null;

	@Option(name = "--brokerPassword", usage = "broker password")
	public String brokerPassword = null;

	@Option(name = "--brokerExchangeName", usage = "broker exchange name", required = true)
	public String brokerExchangeName;

	@Option(name = "--brokerExchangeType", usage = "broker exchange type", required = true)
	public String brokerExchangeType;

	@Option(name = "--brokerExchangeDurable", usage = "broker exchange durable", required = true)
	public boolean brokerExchangeDurable;

	@Option(name = "--brokerRoutingKey", usage = "broker routing key")
	public String brokerRoutingKey;

	@Option(name = "--logLevel",
			usage = "Set logging level (valid values: TRACE, DEBUG, INFO, WARN, ERROR).",
			handler = Log4JLevelOptionHandler.class)
	public Level logLevel = null;

	@Option(name = "--verbose", usage = "Verbose (DEBUG) logging output (default: INFO).")
	public boolean verbose = false;

	@Option(name = "--help", usage = "This help message.")
	public boolean help = false;

}
