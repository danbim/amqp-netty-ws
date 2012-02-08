/**
 * Copyright (c) 2012, Dennis Pfisterer, All rights reserved.
 */
package de.danbim.amqpnettyws.util;

import org.apache.log4j.Level;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.OptionDef;
import org.kohsuke.args4j.spi.OptionHandler;
import org.kohsuke.args4j.spi.Parameters;
import org.kohsuke.args4j.spi.Setter;

public class Log4JLevelOptionHandler extends OptionHandler<Level> {

	public Log4JLevelOptionHandler(CmdLineParser parser, OptionDef option, Setter<? super Level> setter) {
		super(parser, option, setter);
	}

	@Override
	public int parseArguments(final Parameters parameters) throws CmdLineException {
		setter.addValue(Level.toLevel(parameters.getParameter(0)));
		return 1;
	}

	@Override
	public String getDefaultMetaVariable() {
		return "LEVEL";
	}

}
