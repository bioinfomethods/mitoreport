package au.edu.mcri.mitoreport

import gngs.Utils
import groovy.util.logging.Slf4j
import io.micronaut.configuration.picocli.PicocliRunner
import picocli.CommandLine.Command
import picocli.CommandLine.Model.CommandSpec
import picocli.CommandLine.ParameterException
import picocli.CommandLine.Spec

@Slf4j
@Command(name = 'mito-cli', description = 'Mito CLI', synopsisSubcommandLabel = "COMMAND", mixinStandardHelpOptions = true, subcommands = [MitoReportCommand, MitoMapDownloadCommand])
class MitoCli implements Runnable {

    static void main(String[] args) throws Exception {
        Utils.configureSimpleLogging()
        PicocliRunner.run(MitoCli.class, args)
    }

    @Spec
    CommandSpec spec

    void run() {
        throw new ParameterException(spec.commandLine(), "Missing required command");
    }
}
