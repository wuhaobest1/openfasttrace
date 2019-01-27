package org.itsallcode.openfasttrace.cli;

import java.util.Optional;

/*-
 * #%L
 * OpenFastTrace
 * %%
 * Copyright (C) 2016 - 2018 itsallcode.org
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import org.itsallcode.openfasttrace.cli.commands.ConvertCommand;
import org.itsallcode.openfasttrace.cli.commands.Performable;
import org.itsallcode.openfasttrace.cli.commands.TraceCommand;

public class CliStarter
{
    private final CliArguments arguments;

    CliStarter(final CliArguments arguments)
    {
        this.arguments = arguments;
    }

    /**
     * Main entry point for the command line application
     * 
     * @param args
     *            command line parameters
     */
    public static void main(final String[] args)
    {
        final DirectoryService directoryService = new StandardDirectoryService();
        main(args, directoryService);
    }

    /**
     * Auxiliary entry point to the command line application that allows
     * injection of a
     * 
     * @param args
     *            command line arguments.
     * @param directoryService
     *            directory service for getting the current directory. This
     *            allows injecting a mock in unit tests.
     */
    static void main(final String[] args, final DirectoryService directoryService)
    {
        final CliArguments arguments = new CliArguments(directoryService);
        new CommandLineInterpreter(args, arguments).parse();
        final ArgumentValidator validator = new ArgumentValidator(arguments);
        if (validator.isValid())
        {
            new CliStarter(arguments).run();
        }
        else
        {
            printToStdError(
                    "oft: " + validator.getError() + "\n" + validator.getSuggestion() + "\n");
            exit(ExitStatus.CLI_ERROR);
        }
    }

    // Writing to standard error by intention
    @SuppressWarnings("squid:S106")
    private static void printToStdError(final String message)
    {
        System.err.println(message);
    }

    // [impl->dsn~cli.command-selection~1]
    void run()
    {
        Performable performable = null;
        final Optional<String> command = this.arguments.getCommand();
        if (!command.isPresent())
        {
            throw new IllegalStateException("Command missing trying to execute OFT mode.");
        }
        switch (command.get())
        {
        case ConvertCommand.COMMAND_NAME:
            performable = new ConvertCommand(this.arguments);
            break;
        case TraceCommand.COMMAND_NAME:
            performable = new TraceCommand(this.arguments);
            break;
        default:
            throw new IllegalStateException(
                    "Unknown command '" + command.get() + "' trying to execute OFT mode.");
        }
        if (performable.run())
        {
            exit(ExitStatus.OK);
        }
        else
        {
            exit(ExitStatus.FAILURE);
        }
    }

    // [impl->dsn~cli.tracing.exit-status~1]
    private static void exit(final ExitStatus exitStatus)
    {
        System.exit(exitStatus.getCode());
    }
}
