package de.danbim.amqpnettyws;

import de.uniluebeck.itm.tr.util.Logging;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AmqpNettyWsCli {

	static {
		Logging.setDebugLoggingDefaults();
	}

	private static final Logger log = LoggerFactory.getLogger(AmqpNettyWsCli.class);

	public static void main(String[] args) {

		final AmqpNettyWsConfig config = parseCmdLineOptions(args);
		/*final AmqpNettyWsConfig config = new AmqpNettyWsConfig(
				1234,
				"localhost", 5672, "/",
				"guest", "guest",
				"amq.fanout", "fanout", true, ""
		);*/
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

	private static AmqpNettyWsConfig parseCmdLineOptions(final String[] args) {

		AmqpNettyWsConfig options = new AmqpNettyWsConfig();
		CmdLineParser parser = new CmdLineParser(options);

		try {
			parser.parseArgument(args);
			if (options.help) {
				printHelpAndExit(parser);
			}
		} catch (CmdLineException e) {
			System.err.println(e.getMessage());
			printHelpAndExit(parser);
		}

		return options;
	}

	private static void printHelpAndExit(CmdLineParser parser) {
		System.err.print("Usage: java " + AmqpNettyWsConfig.class.getCanonicalName());
		parser.printSingleLineUsage(System.err);
		System.err.println();
		parser.printUsage(System.err);
		System.exit(1);
	}

}
