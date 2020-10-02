package io.github.slfotg.config;

import java.io.File;
import java.util.Arrays;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.integration.file.filters.AcceptOnceFileListFilter;
import org.springframework.integration.file.filters.CompositeFileListFilter;
import org.springframework.integration.file.filters.RegexPatternFileListFilter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableIntegration
@Slf4j
public class FileSourceConfig {

    @Bean
    public MessageChannel fileChannel() {
        return new DirectChannel();
    }

    @Bean
    @InboundChannelAdapter(value = "fileChannel", poller = @Poller(fixedDelay = "10000"))
    public MessageSource<File> fileReadingMessageSource(@Value("${input.directory}") File inputDirectory,
            CompositeFileListFilter<File> customFilter) {
        FileReadingMessageSource source = new FileReadingMessageSource();
        source.setDirectory(inputDirectory);
        source.setFilter(customFilter);
        return source;
    }

    @Bean(name = "customFilter")
    public CompositeFileListFilter<File> customFilter() {
        return new CompositeFileListFilter<>(Arrays.asList(new AcceptOnceFileListFilter<File>(100),
                new RegexPatternFileListFilter(Pattern.compile("^.*\\.xml$"))));
    }

    @Bean
    @ServiceActivator(inputChannel = "fileChannel")
    public MessageHandler handleNewFile() {
        return message -> {
            File file = (File) message.getPayload();
            log.info("handling {}", file.getName());
        };
    }
}
