package de.danbim.amqpnettyws;

import com.google.common.eventbus.EventBus;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;

public class AmqpNettyWsChannelPipelineFactory implements ChannelPipelineFactory {

	private final EventBus eventBus;

	public AmqpNettyWsChannelPipelineFactory(final EventBus eventBus) {
		this.eventBus = eventBus;
	}

	public ChannelPipeline getPipeline() throws Exception {
		ChannelPipeline pipeline = Channels.pipeline();
		pipeline.addFirst("handler", new AmqpConsumerHandler(eventBus));
		pipeline.addFirst("webSocketServerHandler", new WebSocketServerHandler());
		pipeline.addFirst("encoder", new HttpResponseEncoder());
		pipeline.addFirst("aggregator", new HttpChunkAggregator(65536));
		pipeline.addFirst("decoder", new HttpRequestDecoder());
		return pipeline;
	}
}
