package de.danbim.amqpnettyws;

import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.AbstractService;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.util.internal.ExecutorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AmqpNettyWs extends AbstractService {

	private static final Logger log = LoggerFactory.getLogger(AmqpNettyWs.class);

	private final EventBus eventBus = new EventBus();

	private AmqpNettyWsConfig config;

	private ExecutorService bossExecutor;

	private ExecutorService workerExecutor;

	private Channel serverChannel;

	private AmqpConsumerService amqpConsumerService;

	public AmqpNettyWs(final AmqpNettyWsConfig config) {
		this.config = config;
	}

	@Override
	protected void doStart() {

		bossExecutor = Executors.newCachedThreadPool();
		workerExecutor = Executors.newCachedThreadPool();
		ServerBootstrap bootstrap = new ServerBootstrap(
				new NioServerSocketChannelFactory(bossExecutor, workerExecutor)
		);
		bootstrap.setPipelineFactory(new AmqpNettyWsChannelPipelineFactory(eventBus));
		serverChannel = bootstrap.bind(new InetSocketAddress(config.websocketPort));

		amqpConsumerService = new AmqpConsumerService(eventBus, config);

		try {
			amqpConsumerService.start().get();
		} catch (Exception e) {
			notifyFailed(e);
			return;
		}

		notifyStarted();
	}

	@Override
	protected void doStop() {

		try {
			serverChannel.close().await();
		} catch (Exception e) {
			log.warn("{}", e);
		}

		ExecutorUtil.terminate(bossExecutor, workerExecutor);

		try {
			amqpConsumerService.stop().get();
		} catch (Exception e) {
			log.warn("{}", e);
		}

		notifyStopped();
	}
}
