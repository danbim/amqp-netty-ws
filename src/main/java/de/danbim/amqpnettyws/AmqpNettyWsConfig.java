package de.danbim.amqpnettyws;

public class AmqpNettyWsConfig {

	private int websocketPort;

	private String brokerHost;

	private int brokerPort;

	private String brokerVirtualHost;

	private String brokerUsername;

	private String brokerPassword;

	private String brokerExchangeName;

	private String brokerExchangeType;

	private boolean brokerExchangeDurable;

	private String brokerRoutingKey;

	public AmqpNettyWsConfig(final int websocketPort, final String brokerHost, final int brokerPort,
							 final String brokerVirtualHost, final String brokerUsername, final String brokerPassword,
							 final String brokerExchangeName,
							 final String brokerExchangeType, final boolean brokerExchangeDurable,
							 final String brokerRoutingKey) {

		this.websocketPort = websocketPort;
		this.brokerHost = brokerHost;
		this.brokerPassword = brokerPassword;
		this.brokerPort = brokerPort;
		this.brokerUsername = brokerUsername;
		this.brokerVirtualHost = brokerVirtualHost;
		this.brokerExchangeName = brokerExchangeName;
		this.brokerExchangeName = brokerExchangeName;
		this.brokerExchangeType = brokerExchangeType;
		this.brokerExchangeDurable = brokerExchangeDurable;
		this.brokerRoutingKey = brokerRoutingKey;
	}

	public boolean isBrokerExchangeDurable() {
		return brokerExchangeDurable;
	}

	public String getBrokerExchangeName() {
		return brokerExchangeName;
	}

	public String getBrokerExchangeType() {
		return brokerExchangeType;
	}

	public String getBrokerHost() {
		return brokerHost;
	}

	public String getBrokerPassword() {
		return brokerPassword;
	}

	public int getBrokerPort() {
		return brokerPort;
	}

	public String getBrokerUsername() {
		return brokerUsername;
	}

	public String getBrokerVirtualHost() {
		return brokerVirtualHost;
	}

	public int getWebsocketPort() {
		return websocketPort;
	}

	public String getBrokerRoutingKey() {
		return brokerRoutingKey;
	}
}
