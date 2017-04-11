package com.naveen;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import com.naveen.storage.StorageProperties;
import com.naveen.storage.StorageService;
import com.naveen.watcher.FileSystemWatcherService;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class FileuploaderBootApplication {

	//private static Logger log = LoggerFactory.getLogger(FileuploaderBootApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(FileuploaderBootApplication.class, args);
	}

	@Bean
	CommandLineRunner init(StorageService storageService) {
		return (args) -> {
			storageService.deleteAll();
			storageService.init();
		};
	}

	@Bean
	CommandLineRunner initWatcher(FileSystemWatcherService watchService) {
		return (args) -> {
			watchService.startFileWatcher();
		};
	}
}
